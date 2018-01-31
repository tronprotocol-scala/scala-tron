package org.tron.peer

import akka.actor.Actor
import org.tron.cluster.pubsub.ActorPubSub
import org.tron.protos.Tron._

class PeerActor(peer: Peer) extends Actor with ActorPubSub {

  override def preStart(): Unit = {
    subscribeTo("transaction")
    subscribeTo("block")
  }

  def receive = {
    case transaction: Transaction =>
      peer.addReceiveTransaction(transaction)
    case block: Block =>
      peer.addReceiveBlock(block)
  }

}
