package org.tron.network

import org.tron.core.Sha256Hash
import org.tron.network.message.{Message, MessageTypes}
import org.tron.protos.Tron.{Block, Transaction}

trait NodeDelegate {

  def handleBlock(block: Block): Unit

  def handleTransation(trx: Transaction): Unit

  def getBlockHashes(blockChainSummary: List[Sha256Hash]): List[Sha256Hash]

  def getBlockChainSummary(refPoint: Sha256Hash, numberOfHashes: Int): List[Sha256Hash]

  def getData(hash: Sha256Hash, messageType: MessageTypes): Message

}