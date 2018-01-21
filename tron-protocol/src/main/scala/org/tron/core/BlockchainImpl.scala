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
import org.tron.utils.ByteArray
import org.tron.utils.ByteStringUtils._
import scala.collection.mutable

class BlockchainImpl(
  val blockDB: BlockChainDb) extends Blockchain {

  var lastHash = blockDB.get(Constant.LAST_HASH).getOrElse(null)
  var currentHash = lastHash

  def findTransaction(id: Array[Byte]): Option[Transaction] = {

    val bi = new BlockchainIterator(this)

    bi
      .flatMap { block => block.transactions }
      .find { tx =>
        val txID = ByteArray.toHexString(tx.id.toByteArray)
        val idStr = ByteArray.toHexString(id)
        txID == idStr
      }
  }

  def findUTXO(): Map[String, TXOutputs] = {

    val spenttxos = mutable.Map[String, Array[Long]]()
    val utxo = mutable.Map[String, TXOutputs]()

    val bi = new BlockchainIterator(this)

    def isSpent(txid: String, index: Long): Boolean = spenttxos.get(txid).exists(_.contains(index))

    for {
      block <- bi
      transaction <- block.transactions
    } {
      val txid = ByteArray.toHexString(transaction.id.toByteArray)

      for {
        outIdx <- transaction.vout.indices
        out = transaction.vout(outIdx) if !isSpent(txid, outIdx)
      } {
        var outs = utxo.getOrElse(txid, TXOutputs())
        outs = outs.addOutputs(out)
        utxo.put(txid, outs)
      }

      if (!TransactionUtils.isCoinbaseTransaction(transaction)) {
        for (in <- transaction.vin) {
          val inTxid = ByteArray.toHexString(in.txID.toByteArray)
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

        val lashHash = ByteArray.fromString("lashHash")

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

  def signTransaction(transaction: Transaction, key: ECKey): Transaction = {

    val prevTXs = transaction.vin.map { txInput =>
      val txID: ByteString = txInput.txID
      val prevTX = findTransaction(txID.toByteArray).get
      val key = ByteArray.toHexString(txID.toByteArray)
      (key, prevTX)
    }.toMap

    TransactionUtils.sign(transaction, key, prevTXs)
  }

  def bestBlock: Block = {
    val lastHash = blockDB.get(LAST_HASH).get
    val lastBlock = blockDB.get(lastHash).get
    Block.parseFrom(lastBlock)
  }

  /**
    * Recieve block
    */
  def receiveBlock(block: Block, uTXOSet: UTXOSet): Unit = {
    val lastHash = blockDB.get(LAST_HASH).get
    if (block.getBlockHeader.parentHash.hex != ByteArray.toHexString(lastHash))
      return

    // save the block into the database
    val blockHashKey = block.getBlockHeader.hash.toByteArray
    val blockVal = block.toByteArray
    blockDB.put(blockHashKey, blockVal)

    val ch = block.getBlockHeader.hash.toByteArray

    blockDB.put(LAST_HASH, ch)

    this.lastHash = ch
    currentHash = ch
    // update UTXO cache
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
  }

  def   addGenesisBlock(account: String): Unit = {
    val transactions = TransactionUtils.newCoinbaseTransaction(account, Constant.GENESIS_COINBASE_DATA)

    val genesisBlock = BlockUtils.newGenesisBlock(transactions)

    blockDB.put(genesisBlock.blockHeader.get.hash.toByteArray, genesisBlock.toByteArray)
    val lastHash = genesisBlock.blockHeader.get.hash.toByteArray
    blockDB.put(Constant.LAST_HASH, lastHash)

    this.lastHash = lastHash
    this.currentHash = lastHash
  }
}
