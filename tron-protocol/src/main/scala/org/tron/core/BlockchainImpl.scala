package org.tron.core
import java.util
import java.util.{Arrays, HashMap}

import cats.instances.long
import org.tron.crypto.ECKey
import org.tron.protos.core.TronBlock.Block
import org.tron.protos.core.TronTXInput.TXInput
import org.tron.protos.core.TronTXOutput.TXOutput
import org.tron.protos.core.TronTXOutputs.TXOutputs
import org.tron.protos.core.TronTransaction.Transaction
import org.tron.protos.core.{TronBlock, TronTXOutputs, TronTransaction}
import org.tron.storage.leveldb.LevelDbDataSourceImpl
import org.tron.utils.ByteArray
import sun.nio.ch.Net

import scala.annotation.tailrec
import scala.collection.JavaConverters._
import scala.collection.mutable

class BlockchainImpl(address: String) extends Blockchain {

  def findTransaction(id: Array[Byte]): Option[Transaction] = {

    val bi = new BlockchainIterator(this)

    @tailrec
    def next: Option[Transaction] = {
      if (!bi.hasNext) None
      else {
        val block = bi.next()
        val found = block.getTransactionsList.asScala.find { tx =>
          val txID = ByteArray.toHexString(tx.getId.toByteArray)
          val idStr = ByteArray.toHexString(id)
          txID == idStr
        }

        found match {
          case Some(t) =>
            Some(t)

          // If there is no parent (genesis block) then stop searching
          case _ if block.getBlockHeader.getParentHash.isEmpty =>
            None

          // Nothing found, try next
          case None =>
            next
        }
      }
    }

    next
  }

  def findUTXO(): Map[String, TXOutputs] = {

    val spenttxos = mutable.Map[String, Array[Long]]()
    val utxo = mutable.Map[String, TXOutputs]()

    val bi = new BlockchainIterator(this)

    def isSpent(txid: String, index: Long): Boolean = spenttxos.get(txid).exists(_.contains(index))

    while (bi.hasNext) {
      val block = bi.next()

      for (transaction <- block.getTransactionsList.asScala) {
        val txid = ByteArray.toHexString(transaction.getId.toByteArray)

        for {
          outIdx <- 0 to transaction.getVoutList.asScala.size
          out = transaction.getVoutList.get(outIdx) if !isSpent(txid, outIdx)
        } {
          var outs = utxo.getOrElse(txid, TXOutputs.newBuilder.build)

          outs = outs.toBuilder.addOutputs(out).build
          utxo.put(txid, outs)
        }


        if (!TransactionUtils.isCoinbaseTransaction(transaction)) {
          for (in <- transaction.getVinList.asScala) {
            val inTxid = ByteArray.toHexString(in.getTxID.toByteArray)
            val vindexs = spenttxos.getOrElse(inTxid, Array[Long]())
            spenttxos.put(inTxid, vindexs :+ in.getVout)
          }
        }
      }

    }

    utxo.toMap
  }

  override def addBlock(block: TronBlock.Block): Unit = ???

  override def signTransaction(transaction: TronTransaction.Transaction, key: ECKey): TronTransaction.Transaction = ???

  override def addBlock(transactions: List[TronTransaction.Transaction], net: Net): Unit = ???

  override def currentHash: Array[Byte] = ???

  override def blockDb: LevelDbDataSourceImpl = ???
}
