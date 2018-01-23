/*
 * java-tron is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * java-tron is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *//*
 * java-tron is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * java-tron is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.tron.core

import com.google.protobuf.ByteString
import org.tron.core.Exceptions.TransactionException
import org.tron.crypto.ECKey
import org.tron.crypto.Hash.sha256
import org.tron.protos.core.TronTXInput.TXInput
import org.tron.protos.core.TronTXOutput.TXOutput
import org.tron.protos.core.TronTransaction.Transaction
import org.tron.utils.ByteArrayUtils
import org.tron.utils.ByteStringUtils._
import org.tron.utils.Utils.getRandom
import org.tron.wallet.Wallet

import scala.collection.mutable

object TransactionUtils {

  implicit class TransactionImplicits(transaction: Transaction) {
    def hash = getHash(transaction)
    def hashByteString = ByteString.copyFrom(getHash(transaction))
    def setVin(index: Int, input: TXInput) = {
      transaction.withVin(transaction.vin.updated(index, input))
    }
    def hasId = transaction.id.toByteArray.length > 0
    def isCoinbase = transaction.vin.size == 1 && (transaction.vin.head.txID.size == 0) && (transaction.vin.head.vout == -1)
  }

  private val RESERVE_BALANCE = 10

  def newTransaction(wallet: Wallet, toKey: String, amount: Long, utxoSet: UTXOSet): Either[TransactionException, Transaction] = {

    val spendableOutputs = utxoSet.findSpendableOutputs(wallet.address, amount)
    if (spendableOutputs.amount < amount) {
      Left(TransactionException("Not enough funds"))
    } else {
      val entrySet = spendableOutputs.unspentOutputs

      val txInputs =  for {
        (txID, outs) <- entrySet
        out <- outs
      } yield {
        TXInputUtils.newTXInput(ByteArrayUtils.fromHexString(txID), out, new Array[Byte](0), wallet.key.getPubKey)
      }

      val txOutputs = mutable.ListBuffer[TXOutput]()
      txOutputs.append(TXOutputUtils.newTXOutput(amount, toKey))

      if (spendableOutputs.amount > amount) {
        txOutputs.append(TXOutputUtils.newTXOutput(spendableOutputs.amount - amount, wallet.address.addressHex))
      }

      val newTransaction = Transaction(
        vin = txInputs.toSeq,
        vout = txOutputs)

      utxoSet.blockchain.signTransaction(newTransaction, wallet.key)
    }
  }

  /**
    * new coinbase transaction
    *
    * @param to   String to sender's address
    * @param data String transaction data
    * @return { @link Transaction}
    */
  def newCoinbaseTransaction(to: String, data: String): Transaction = {
    val key = if (data == null || data == "") {
      val randBytes = Array.fill(20)(0.byteValue)
      getRandom.nextBytes(randBytes)
      ByteArrayUtils.toHexString(randBytes)
    } else data

    val txi = TXInputUtils.newTXInput(Array[Byte](), -1, Array[Byte](), ByteArrayUtils.fromHexString(key))
    val txo = TXOutputUtils.newTXOutput(RESERVE_BALANCE, to)
    val coinbaseTransaction = Transaction()
      .addVin(txi)
      .addVout(txo)

    coinbaseTransaction.withId(coinbaseTransaction.hash)
  }

  /**
    * Obtain a data bytes after removing the id and SHA-256(data)
    *
    * @param transaction { @link Transaction} transaction
    * @return byte[] the hash of the transaction's data bytes which have no id
    */
  def getHash(transaction: Transaction): Array[Byte] = {
    val tmp: Transaction = transaction.withId(ByteString.EMPTY)
    sha256(tmp.toByteArray)
  }

  /**
    * Determine whether the transaction is a coinbase transaction
    *
    * @param transaction { @link Transaction} transaction
    * @return boolean true for coinbase, false for not coinbase
    */
  def isCoinbaseTransaction(transaction: Transaction): Boolean = {
    transaction.vin.size == 1 && (transaction.vin.head.txID.size == 0) && (transaction.vin.head.vout == -1)
  }

  def sign(transaction: Transaction, myKey: ECKey, prevTXs: Map[String, Transaction]): Either[TransactionException, Transaction] = {

    var mutableTransaction = transaction

    // No need to sign coinbase transaction
    if (mutableTransaction.isCoinbase) {
      Left(TransactionException("Can't sign coinbase transaction"))
    } else if (mutableTransaction.vin.exists(vin => !prevTXs(vin.txID.hex).hasId)) {
      Left(TransactionException("Previous transaction is incorrect"))
    } else {
      for (i <- mutableTransaction.vin.indices) {
        val vin = mutableTransaction.vin(i)
        val prevTx = prevTXs(vin.txID.hex)
        var transactionCopyBuilder = mutableTransaction
        var vinBuilder = vin
          .withSignature(ByteString.EMPTY)
          .withPubKey(prevTx.vout(vin.vout.toInt).pubKeyHash)

        transactionCopyBuilder = transactionCopyBuilder.setVin(i, vinBuilder)

        transactionCopyBuilder = transactionCopyBuilder
          .withId(transactionCopyBuilder.hash)

        vinBuilder = vinBuilder.withPubKey(ByteString.EMPTY)
        transactionCopyBuilder = transactionCopyBuilder.setVin(i, vinBuilder)

        val signature = myKey.sign(transactionCopyBuilder.id).toByteArray

        var transactionBuilder = mutableTransaction.setVin(i, vinBuilder.withSignature(signature))

        transactionBuilder = transactionBuilder.withId(transactionBuilder.hashByteString)

        mutableTransaction = transactionBuilder
      }

      Right(mutableTransaction)
    }
  }

  def verify(transaction: Transaction, key: ECKey, prevTXs: Map[String, Transaction]): Boolean = {

    // No need to sign coinbase transaction
    if (transaction.isCoinbase)
      return true // scalastyle:ignore

    if (transaction.vin.exists(vin => !prevTXs(vin.txID.hex).hasId)) {
      throw new Exception("Previous transaction is incorrect")
    }

    for (i <- transaction.vin.indices) {
      val vin = transaction.vin(i)
      val prevTx = prevTXs(vin.txID.hex)
      var transactionCopyBuilder = transaction

      var vinBuilder = vin
        .withSignature(ByteString.EMPTY)
        .withPubKey(prevTx.vout(vin.vout.toInt).pubKeyHash)

      transactionCopyBuilder = transactionCopyBuilder.setVin(i, vinBuilder)

      transactionCopyBuilder = transactionCopyBuilder
        .withId(transactionCopyBuilder.hash)

      vinBuilder = vinBuilder.withPubKey(ByteString.EMPTY)
      transactionCopyBuilder = transactionCopyBuilder.setVin(i, vinBuilder)

      if (!key.verify(transactionCopyBuilder.id, vin.signature))
        return false // scalastyle:ignore
    }

    true
  }

  // getData sender
  def getSender(tx: Transaction): Array[Byte] = {
    ECKey.computeAddress(tx.vin.head.pubKey)
  }
}