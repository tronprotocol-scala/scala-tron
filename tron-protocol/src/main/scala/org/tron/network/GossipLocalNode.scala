package org.tron.network
import akka.NotUsed
import akka.stream.scaladsl.Source
import org.tron.network.message.{Message, PeerMessage}
import org.tron.network.peer.PeerConnection

object GossipLocalNode {
  def build: LocalNode = ???
}

class GossipLocalNode extends LocalNode {

  private var peersMap = Map[Int, PeerConnection]()

  /**
    * All known peers in the network
    */
  def peers = peersMap.values.toList

  /**
    * Broadcast a message to all peers
    *
    * @param message The message to broadcast
    */
  def broadcast(message: Message) = {
    ???
  }

  def subscribe(): Source[PeerMessage, NotUsed] = {
    Source.empty
  }
}
