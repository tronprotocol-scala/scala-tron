package org.tron.network

import akka.{NotUsed, util}
import akka.actor.ActorRef
import akka.stream.scaladsl.Source
import org.tron.network.GossipLocalNodeActor._
import org.tron.network.message.{InventoryBaseMessage, Message, PeerMessage}
import org.tron.network.peer.PeerConnection
import akka.pattern.ask
import scala.concurrent.duration._

import scala.concurrent.Future

class LocalNodeActor(actorRef: ActorRef) extends LocalNode {

  import scala.concurrent.ExecutionContext.Implicits.global
  implicit val timeout = util.Timeout(3.seconds)

  /**
    * @param message Message which will be broadcasted to all known peers
    */
  def broadcast(message: Message): Unit = {
    actorRef ! Broadcast(message)
  }


  /**
    * Advertise inventory to peers who need to be synced
    */
  override def advertise(message: InventoryBaseMessage): Unit = {
    actorRef ! BroadcastInventory(message)
  }

  /**
    * Listens to incoming messages
    *
    * @return Stream which contains incoming messages
    */
  def subscribe(): Future[Source[PeerMessage, NotUsed]] = {
    (actorRef ? RequestStream()).mapTo[Source[PeerMessage, NotUsed]]
  }

  /**
    * @return All known peer connections
    */
  def peers: Future[List[PeerConnection]] = {
    for {
      state <- (actorRef ? RequestState()).mapTo[NodeState]
    } yield state.members.values.toList
  }
}
