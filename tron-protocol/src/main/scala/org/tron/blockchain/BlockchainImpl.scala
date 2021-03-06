package org.tron
package blockchain

import com.google.protobuf.ByteString
import org.tron.awaitResult
import org.tron.core.Constant.LAST_HASH
import org.tron.utxo.TransactionUtils._
import org.tron.core._
import org.tron.crypto.ECKey
import org.tron.protos.Tron._
import org.tron.utils.ByteArrayUtils
import org.tron.utils.ByteStringUtils._
import org.tron.utxo.{TransactionUtils, UTXOSet}

import scala.async.Async._
import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class BlockchainImpl(val blockDB: DefaultDB) extends Blockchain with Iterable[Block] {

  var lastHash = awaitResult(blockDB.get(Constant.LAST_HASH)).getOrElse(null)
  var currentHash = lastHash

  def iterator = new BlockchainIterator(this)

  def findTransaction(id: TXID): Option[Transaction] = {

    val bi = new BlockchainIterator(this)

    bi
      .flatMap(_.transactions)
      .find { tx =>
        val txID = ByteArrayUtils.toHexString(tx.id.toByteArray)
        val idStr = ByteArrayUtils.toHexString(id)
        txID == idStr
      }
  }

  def findUTXO(): Map[String, TXOutputs] = {

    val spenttxos = mutable.Map[String, Array[Long]]()
    val utxo = mutable.Map[String, TXOutputs]()

    val bi = new BlockchainIterator(this)

    def isSpent(txId: String, index: Long) = {
      spenttxos.get(txId).exists(_.contains(index))
    }

    for {
      block <- bi
      transaction <- block.transactions
    } {
      val txId = transaction.id.hex

      for {
        outIndex <- transaction.vout.indices
        out = transaction.vout(outIndex)
        if !isSpent(txId, outIndex)
      } {
        val outs = utxo
          .getOrElse(txId, TXOutputs())
          .addOutputs(out)

        utxo.put(txId, outs)
      }

      if (!transaction.isCoinbase) {
        for (in <- transaction.vin) {
          val inTxid = in.txID.hex
          val vindexs = spenttxos.getOrElse(inTxid, Array[Long]())
          spenttxos.put(inTxid, vindexs :+ in.vout)
        }
      }
    }

    utxo.toMap
  }

  def addBlock(block: Block) = async {
    await(blockDB.get(block.getBlockHeader.hash.toByteArray)) match {
      case Some(blockInDB) if blockInDB.nonEmpty =>

        await(blockDB.put(block.getBlockHeader.hash.toByteArray, block.toByteArray))

        val lashHash = ByteArrayUtils.fromString("lashHash")

        val lastHash = await(blockDB.get(lashHash)).get
        val lastBlockData = await(blockDB.get(lastHash)).get
        val lastBlock = Block.parseFrom(lastBlockData)

        if (block.getBlockHeader.number > lastBlock.getBlockHeader.number) {
          await(blockDB.put(lashHash, block.getBlockHeader.hash.toByteArray))
          this.lastHash = block.getBlockHeader.hash.toByteArray
          this.currentHash = this.lastHash
        }

      case _ =>
      // ignore
    }
  }

  def addBlock(transactions: List[Transaction]): Block = {
    // get lastHash
    val lastHash = awaitResult(blockDB.get(LAST_HASH)).get
    val parentHash = ByteString.copyFrom(lastHash)
    // get number
    val number = BlockUtils.getIncreaseNumber(this)
    // get difficulty
    val difficulty = ByteString.copyFromUtf8(Constant.DIFFICULTY)
    BlockUtils.newBlock(transactions, parentHash, difficulty, number)
  }

  def signTransaction(transaction: Transaction, key: ECKey): Either[TransactionException, Transaction] = {

    val prevTXs = transaction.vin.map { txInput =>
      val txID: ByteString = txInput.txID
      val prevTX = findTransaction(txID.toByteArray).get
      val key = ByteArrayUtils.toHexString(txID.toByteArray)
      (key, prevTX)
    }.toMap

    TransactionUtils.sign(transaction, key, prevTXs)
  }

  /**
    * Recieve block
    */
  def receiveBlock(block: Block, uTXOSet: UTXOSet): Future[Unit] = async {
    val lastHash = await(blockDB.get(LAST_HASH)).get

    if (block.getBlockHeader.parentHash.hex == ByteArrayUtils.toHexString(lastHash)) {
      // save the block into the database
      val blockHashKey = block.getBlockHeader.hash.toByteArray
      val blockVal = block.toByteArray
      await(blockDB.put(blockHashKey, blockVal))
      val ch = block.getBlockHeader.hash.toByteArray

      await(blockDB.put(LAST_HASH, ch))

      this.lastHash = ch
      this.currentHash = ch

      uTXOSet.reindex()
    }
  }

  def addGenesisBlock(account: Address): Unit = {
    val transactions = TransactionUtils.newCoinbaseTransaction(account, Constant.GENESIS_COINBASE_DATA)

    val genesisBlock = BlockUtils.newGenesisBlock(transactions)
    val hashArray = genesisBlock.blockHeader.get.hash.toByteArray
    awaitResult(blockDB.put(hashArray, genesisBlock.toByteArray))
    val lastHash = hashArray
    awaitResult(blockDB.put(Constant.LAST_HASH, lastHash))

    this.lastHash = lastHash
    this.currentHash = lastHash
  }

}
