package org.tron.core

import org.tron.BlockChainDb
import org.tron.crypto.ECKey
import org.tron.protos.core.TronBlock.Block
import org.tron.protos.core.TronTXOutputs.TXOutputs
import org.tron.protos.core.TronTransaction.Transaction

trait Blockchain {

  def findTransaction(id: Array[Byte]): Option[Transaction]

  def findUTXO(): Map[String, TXOutputs]

  def addBlock(block: Block)

  def signTransaction(transaction: Transaction, key: ECKey): Transaction

//  def addBlock(transactions: List[Transaction], net: Net): Unit

  def currentHash: Array[Byte]

  def blockDB: BlockChainDb

  def addGenesisBlock(account: String): Unit

}
