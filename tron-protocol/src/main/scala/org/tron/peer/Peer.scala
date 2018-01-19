package org.tron.peer

import org.tron.core.{Blockchain, UTXOSet}
import org.tron.crypto.ECKey
import org.tron.protos.core.TronBlock.Block
import org.tron.protos.core.TronTransaction.Transaction
import org.tron.wallet.Wallet

object Peer {
  val PEER_NORMAL = "normal"
  val PEER_SERVER = "server"
}


case class Peer(
  key: ECKey,
  wallet: Wallet,
  blockchain: Blockchain,
  uTXOSet: UTXOSet,
  peerType: String) {

  def addReceiveTransaction(transaction: Transaction): Unit = {
    blockchain.addBlock(List(transaction))
  }

  def addReceiveBlock(block: Block): Unit = {
    blockchain.receiveBlock(block, uTXOSet)
  }
}
