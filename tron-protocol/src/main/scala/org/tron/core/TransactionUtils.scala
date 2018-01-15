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
import org.tron.crypto.ECKey
import org.tron.crypto.Hash.sha256
import org.tron.protos.core.TronTXInput.TXInput
import org.tron.protos.core.TronTransaction.Transaction
import org.tron.utils.ByteArray
import org.tron.utils.ByteStringUtils._
import org.tron.utils.Utils.getRandom

object TransactionUtils {

  implicit class TransactionImplicits(transaction: Transaction) {
    def hash = getHash(transaction)
    def hashByteString = ByteString.copyFrom(getHash(transaction))
    def setVin(index: Int, input: TXInput) = {
      transaction.withVin(transaction.vin.updated(index, input))
    }
  }


  private val RESERVE_BALANCE = 10

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
      ByteArray.toHexString(randBytes)
    } else data

    val txi = TXInputUtils.newTXInput(Array[Byte](), -1, Array[Byte](), ByteArray.fromHexString(key))
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

  def sign(transaction: Transaction, myKey: ECKey, prevTXs: Map[String, Transaction]): Transaction = {

    var mutableTransaction = transaction

    // No need to sign coinbase transaction
    if (TransactionUtils.isCoinbaseTransaction(mutableTransaction))
      return transaction // scalastyle:ignore

    for (vin <- mutableTransaction.vin) {
      if (prevTXs(vin.txID.hex).id.toByteArray.length == 0) {
        throw new Exception("Previous transaction is incorrect")
      }
    }

    for (i <- 0 to mutableTransaction.vin.size) {
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

    mutableTransaction
  }

  def verify(transaction: Transaction, key: ECKey,  prevTXs: Map[String, Transaction]): Boolean = {

    // No need to sign coinbase transaction
    if (TransactionUtils.isCoinbaseTransaction(transaction))
      return true // scalastyle:ignore

    for (vin <- transaction.vin) {
      if (prevTXs(vin.txID.hex).id.toByteArray.length == 0) {
        throw new Exception("Previous transaction is incorrect")
      }
    }

    for (i <- 0 to transaction.vin.size) {
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