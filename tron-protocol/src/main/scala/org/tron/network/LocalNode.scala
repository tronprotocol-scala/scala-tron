package org.tron.network

import akka.NotUsed
import akka.stream.scaladsl.Source
import org.tron.network.message.{InventoryBaseMessage, Message, PeerMessage}
import org.tron.network.peer.PeerConnection

import scala.concurrent.Future

trait LocalNode {

  /**
    * @param message Message which will be broadcasted to all known peers
    */
  def broadcast(message: Message): Unit

  /**
    * Advertise inventory to peers who need to be synced
    */
  def advertise(message: InventoryBaseMessage): Unit

  /**
    * Listens to incoming messages
    *
    * @return Stream which contains incoming messages
    */
  def subscribe(): Future[Source[PeerMessage, NotUsed]]

  /**
    * @return All known peer connections
    */
  def peers: Future[List[PeerConnection]]

}
