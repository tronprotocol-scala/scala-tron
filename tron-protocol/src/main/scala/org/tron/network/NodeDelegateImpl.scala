package org.tron
package network

import org.tron.db.BlockStore
import org.tron.network.message.{Message, MessageTypes}
import org.tron.protos.Tron.{Block, Transaction}

class NodeDelegateImpl(
  blockStore: BlockStore) extends NodeDelegate {


  override def handleTransation(trx: Transaction): Unit = ???


  override def getBlockChainSummary(refPoint: Hash, numberOfHashes: Int): List[Hash] = ???

  override def getData(hash: Hash, messageType: MessageTypes): Message = ???

  def handleBlock(block: Block): Unit = {

  }

  def getBlockHashes(blockChainSummary: List[Hash]): List[Hash] = {
    val lastKnownBlockHash = blockChainSummary
      .reverse
      .find(blockStore.isBlockIncluded)

    lastKnownBlockHash match {
      case Some(lastKnownHash) =>

        (for {
          num <- blockStore.getBlockNumByHash(lastKnownHash) to blockStore.headBlockNum
          if num > 0
        } yield blockStore.getBlockHashByNum(num)).toList

      case None =>
        List.empty
    }
  }
}
