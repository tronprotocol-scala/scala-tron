package org.tron.blockchain

import org.tron.core.{Address, Exception}
import org.tron.crypto.ECKey
import org.tron.protos.Tron.{Block, TXOutputs, Transaction}
import org.tron.utxo.UTXOSet
import org.tron.{DefaultDB, TXID}

import scala.concurrent.Future

trait Blockchain {

  def findTransaction(id: TXID): Option[Transaction]

  def findUTXO(): Map[String, TXOutputs]

  def addBlock(block: Block): Future[Unit]
  def addBlock(transactions: List[Transaction]): Block
  def receiveBlock(block: Block, uTXOSet: UTXOSet): Future[Unit]

  def signTransaction(transaction: Transaction, key: ECKey): Either[Exception, Transaction]

  def currentHash: Array[Byte]

  def blockDB: DefaultDB

  def addGenesisBlock(account: Address): Unit

}
