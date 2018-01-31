package org.tron
package core

import org.tron.crypto.ECKey
import org.tron.protos.Tron.{Block, TXOutputs, Transaction}

import scala.concurrent.Future

trait Blockchain {

  def findTransaction(id: TXID): Option[Transaction]

  def findUTXO(): Map[String, TXOutputs]

  def addBlock(block: Block): Future[Unit]
  def addBlock(transactions: List[Transaction]): Block
  def receiveBlock(block: Block, uTXOSet: UTXOSet): Future[Unit]

  def signTransaction(transaction: Transaction, key: ECKey): Either[Exception, Transaction]

  def currentHash: Array[Byte]

  def blockDB: BlockChainDb

  def addGenesisBlock(account: Address): Unit

}
