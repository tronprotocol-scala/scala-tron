package org.tron
package core

import com.google.protobuf.ByteString
import org.tron.core.Constant.LAST_HASH
import org.tron.crypto.ECKey
import org.tron.peer.Peer
import org.tron.protos.core.TronBlock.Block
import org.tron.protos.core.TronTXOutputs.TXOutputs
import org.tron.protos.core.TronTransaction
import org.tron.protos.core.TronTransaction.Transaction
import org.tron.utils.ByteArrayUtils
import org.tron.utils.ByteStringUtils._

import scala.collection.mutable
import TransactionUtils._
import org.tron.core.Exceptions.TransactionException

class BlockchainImpl(
  val blockDB: BlockChainDb) extends Blockchain {

  var lastHash = blockDB.get(Constant.LAST_HASH).getOrElse(null)
  var currentHash = lastHash

  def findTransaction(id: Array[Byte]): Option[Transaction] = {

    val bi = new BlockchainIterator(this)

    bi
      .flatMap { block => block.transactions }
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

  def addBlock(block: Block): Unit = {
    blockDB.get(block.getBlockHeader.hash.toByteArray) match {
      case Some(blockInDB) if blockInDB.nonEmpty =>

        blockDB.put(block.getBlockHeader.hash.toByteArray, block.toByteArray)

        val lashHash = ByteArrayUtils.fromString("lashHash")

        val lastHash = blockDB.get(lashHash).get
        val lastBlockData = blockDB.get(lastHash).get
        val lastBlock = Block.parseFrom(lastBlockData)

        if (block.getBlockHeader.number > lastBlock.getBlockHeader.number) {
          blockDB.put(lashHash, block.getBlockHeader.hash.toByteArray)
          this.lastHash = block.getBlockHeader.hash.toByteArray
          this.currentHash = this.lastHash
        }

      case _ =>
        // ignore
    }

  }

  def addBlock(transactions: List[Transaction]): Block = {
    // get lastHash
    val lastHash = blockDB.get(LAST_HASH).get
    val parentHash = ByteString.copyFrom(lastHash)
    // get number
    val number = BlockUtils.getIncreaseNumber(this)
    // get difficulty
    val difficulty = ByteString.copyFromUtf8(Constant.DIFFICULTY)
    val block = BlockUtils.newBlock(transactions, parentHash, difficulty, number)
    block
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
  def receiveBlock(block: Block, uTXOSet: UTXOSet): Unit = {
    val lastHash = blockDB.get(LAST_HASH).get
    if (block.getBlockHeader.parentHash.hex != ByteArrayUtils.toHexString(lastHash))
      return

    // save the block into the database
    val blockHashKey = block.getBlockHeader.hash.toByteArray
    val blockVal = block.toByteArray
    blockDB.put(blockHashKey, blockVal)
    val ch = block.getBlockHeader.hash.toByteArray

    blockDB.put(LAST_HASH, ch)

    this.lastHash = ch
    this.currentHash = ch

    uTXOSet.reindex()
  }

  def addBlock(transactions: List[TronTransaction.Transaction], net: Net): Unit = {
    // get lastHash
    val lastHash = blockDB.get(LAST_HASH).get
    val parentHash = ByteString.copyFrom(lastHash)
    // get number
    val number = BlockUtils.getIncreaseNumber(this)
    // get difficulty
    val difficulty = ByteString.copyFromUtf8(Constant.DIFFICULTY)

    BlockUtils.newBlock(transactions, parentHash, difficulty, number)
    // TODO send to kafka
  }

  def addGenesisBlock(account: String): Unit = {
    val transactions = TransactionUtils.newCoinbaseTransaction(account, Constant.GENESIS_COINBASE_DATA)

    val genesisBlock = BlockUtils.newGenesisBlock(transactions)
    val hashArray = genesisBlock.blockHeader.get.hash.toByteArray
    blockDB.put(hashArray, genesisBlock.toByteArray)
    val lastHash = hashArray
    blockDB.put(Constant.LAST_HASH, lastHash)

    this.lastHash = lastHash
    this.currentHash = lastHash
  }
}
