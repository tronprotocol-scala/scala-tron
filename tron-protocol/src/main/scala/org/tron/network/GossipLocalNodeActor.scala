package org.tron.network

import akka.NotUsed
import akka.actor.{Actor, ActorRef, PoisonPill, Terminated}
import akka.stream.OverflowStrategy
import akka.stream.scaladsl.Source
import com.typesafe.config.Config
import io.scalecube.cluster.membership.MembershipEvent.Type
import io.scalecube.cluster.{Cluster, ClusterConfig, Member}
import io.scalecube.transport.{Address, Message}
import org.tron.network.GossipLocalNodeActor.NodeState
import org.tron.network.message.{MessageDeserializer, MessageTypes, Message => TronMessage}
import org.tron.network.peer.PeerConnection
import rx.Subscription
import rx.subscriptions.CompositeSubscription

import scala.collection.JavaConverters._

object GossipLocalNodeActor {
  case class NodeState(cluster: Cluster, members: Map[String, PeerConnection] = Map.empty) {
    def addPeer(member: Member) = {
      copy(members = members ++ Map(member.id -> PeerConnection(cluster, member)))
    }

    def removePeer(member: Member) = {
      copy(members = members - member.id())
    }

    def addPeers(newMembers: Seq[Member]) = {
      val newPeers = newMembers.map(m => m.id -> PeerConnection(cluster, m)).toMap
      copy(members = members ++ newPeers)
    }
  }
}

/**
  * Gossip Node
  */
class GossipLocalNodeActor(
  port: Int,
  config: Config) extends Actor {

  var state = NodeState(cluster = null)
  var listeners = Map[Int, ActorRef]()
  val subscriptions = new CompositeSubscription()
  val messageSerializer = new MessageDeserializer

  def cluster = state.cluster

  def otherMembers = state.members.values

  def clusterConfig = ClusterConfig.builder
    .seedMembers(seedAddresses.asJava)
    .portAutoIncrement(false)
    .port(port)
    .build

  def seedAddresses = {
    config
      .getStringList("cluster.seedNodes")
      .asScala
      .map { seedAddress =>
        val Array(ip, port) = seedAddress.split(":")
        Address.create(ip, port.toInt)
      }
      .toList
  }

  /**
    * Build a stream which listens to cluster messages
    */
  def listen: Source[TronMessage, NotUsed] = {
    Source.actorRef(500, OverflowStrategy.dropNew)
      .mapMaterializedValue { actorRef =>
        listeners = listeners ++ Map(actorRef.hashCode() -> actorRef)
        context.watch(actorRef)
        NotUsed
      }
  }

  /**
    * Connects to the cluster
    */
  def connect() = {
    val cluster = Cluster.joinAwait(clusterConfig)

    state = NodeState(cluster)
        .addPeers(cluster.otherMembers().asScala.toSeq)

    subscribe {
      cluster.listenMembership()
        .subscribe(event => {
          event.`type`() match {
            case Type.REMOVED =>
              state = state.removePeer(event.oldMember())

            case Type.ADDED | Type.UPDATED =>
              state = state.addPeer(event.newMember())
          }
        })
    }

    subscribe {
      cluster.listen().subscribe(msg => {
        val key = msg.header("type")
        val messageType = MessageTypes.valueOf(key)
        val newValueBytes = msg.data.toString.getBytes("ISO-8859-1")
        val tronMessage = messageSerializer.deserialize(messageType, newValueBytes)
        listeners.values.foreach(_ ! tronMessage)
      })
    }
  }

  /**
    * Disconnects from the cluster
    */
  def disconnect() = {
    cluster.shutdown()
    subscriptions.clear()
    listeners.values.foreach(_ ! PoisonPill)
    listeners = Map.empty
  }


  def subscribe(subscription: Subscription): Unit = {
    subscriptions.add(subscription)
  }


  def broadcast(message: TronMessage) = {
    otherMembers.foreach(_.send(message))
  }

  override def preStart(): Unit = {
    connect()
  }

  override def postStop(): Unit = {
    disconnect()
  }

  def receive = {
    case Terminated(ref) =>
      listeners = listeners - ref.hashCode()
  }
}
