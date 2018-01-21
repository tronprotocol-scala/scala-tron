package org.tron.peer

import akka.actor.Actor
import org.tron.cluster.pubsub.ActorPubSub
import org.tron.protos.core.TronBlock.Block
import org.tron.protos.core.TronTransaction.Transaction


class PeerActor(peer: Peer) extends Actor with ActorPubSub {

  override def preStart(): Unit = {
    subscribeTo("transaction")
    subscribeTo("block")

    startInitialBlockDownload()
  }

  def startInitialBlockDownload() = {
    val lastBlock = peer.blockchain.bestBlock
  }

  def receive = {
    case transaction: Transaction =>
      peer.addReceiveTransaction(transaction)
    case block: Block =>
      peer.addReceiveBlock(block)
  }

}
