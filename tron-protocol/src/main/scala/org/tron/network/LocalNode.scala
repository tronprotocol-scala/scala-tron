package org.tron.network

import akka.NotUsed
import akka.stream.scaladsl.Source
import org.tron.network.message.{Message, PeerMessage}
import org.tron.network.peer.PeerConnection

trait LocalNode {

  /**
    * @param message Message which will be broadcasted to all known peers
    */
  def broadcast(message: Message): Unit

  /**
    * Listens to incoming messages
    *
    * @return Stream which contains incoming messages
    */
  def subscribe(): Source[PeerMessage, NotUsed]

  /**
    * @return All known peer connections
    */
  def peers: List[PeerConnection]

  /**
    * @return Peers which are required to sync
    */
  def syncablePeers = peers.filterNot(_.needSyncFrom)

}
