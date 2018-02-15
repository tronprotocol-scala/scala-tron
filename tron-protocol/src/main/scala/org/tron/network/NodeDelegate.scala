package org.tron.network

import org.tron.Hash
import org.tron.core.Sha256Hash
import org.tron.network.message.{Message, MessageTypes}
import org.tron.protos.Tron.{Block, Transaction}

trait NodeDelegate {

  def handleBlock(block: Block): Unit

  def handleTransaction(trx: Transaction): Unit

  def getBlockHashes(blockChainSummary: List[Hash]): List[Hash]

  def getBlockChainSummary(refPoint: Hash, numberOfHashes: Int): List[Hash]

  def getData(hash: Hash, messageType: MessageTypes): Message

}