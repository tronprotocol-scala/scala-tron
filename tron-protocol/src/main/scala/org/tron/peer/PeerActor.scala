package org.tron.peer

import akka.actor.Actor
import org.tron.cluster.pubsub.ActorPubSub


class PeerActor(peer: Peer) extends Actor with ActorPubSub {

  override def preStart(): Unit = {

  }

  def receive = {
    case x =>
  }

}
