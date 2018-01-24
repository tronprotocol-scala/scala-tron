package org.tron
package core

import org.tron.crypto.ECKey
import org.tron.protos.core.TronBlock.Block
import org.tron.protos.core.TronTXOutputs.TXOutputs
import org.tron.protos.core.TronTransaction.Transaction

trait Blockchain {

  def findTransaction(id: TXID): Option[Transaction]

  def findUTXO(): Map[String, TXOutputs]

  def addBlock(block: Block): Unit
  def addBlock(transactions: List[Transaction]): Block
  def receiveBlock(block: Block, uTXOSet: UTXOSet): Unit

  def signTransaction(transaction: Transaction, key: ECKey): Either[Exception, Transaction]

  def currentHash: Array[Byte]

  def blockDB: BlockChainDb

  def addGenesisBlock(account: Address): Unit

}
