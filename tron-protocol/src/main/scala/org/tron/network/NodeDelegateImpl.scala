package org.tron
package network

import org.tron.db.{BlockStore, DynamicPropertiesStore}
import org.tron.network.message.{Message, MessageTypes}
import org.tron.protos.Tron.{Block, Transaction}

class NodeDelegateImpl(
  blockStore: BlockStore,
  propertiesStore: DynamicPropertiesStore) extends NodeDelegate {

  def getBlockChainSummary(refPoint: Hash, numberOfHashes: Int): List[Hash] = ???

  def getData(hash: Hash, messageType: MessageTypes): Message = ???

  def handleTransation(trx: Transaction): Unit = {
    blockStore.pushTransactions(trx)
  }

  def handleBlock(block: Block): Unit = {
    // Save latest block to blockstore
    blockStore.saveBlock("".getBytes, block.toByteArray)

    // Save latest block to properties store
    propertiesStore.latestBlockHeaderTimestamp = block.getBlockHeader.timestamp
    propertiesStore.latestBlockHeaderNumber = block.getBlockHeader.number
    propertiesStore.latestBlockHeaderHash = block.getBlockHeader.hash
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
