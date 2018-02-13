package org.tron.network

import akka.NotUsed
import akka.actor.{Actor, ActorRef, PoisonPill, Props, Terminated}
import akka.stream.OverflowStrategy
import akka.stream.scaladsl.Source
import com.typesafe.config.Config
import io.scalecube.cluster.membership.MembershipEvent.Type
import io.scalecube.cluster.{Cluster, ClusterConfig, Member}
import io.scalecube.transport.{Address, Message}
import org.tron.network.GossipLocalNodeActor.{Connect, Connected, NodeState}
import org.tron.network.message.{MessageDeserializer, MessageTypes, Message => TronMessage}
import org.tron.network.peer.PeerConnection
import rx.Subscription
import rx.subscriptions.CompositeSubscription

import scala.collection.JavaConverters._

object GossipLocalNodeActor {

  case class Connect(seedAddresses: Seq[Address] = List.empty)
  case class Connected()

  def props(port: Int, config: Config) = Props(classOf[GossipLocalNodeActor], port, config)

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
  *
  * The node automatically connects when seednodes are configured in cluster.seedNodes
  *
  * If there are no seedNodes configured then the cluster connection should be started
  * by sending a Connect() message to the actor
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

  def buildClusterConfig = ClusterConfig.builder
    .seedMembers(seedAddresses.asJava)
    .portAutoIncrement(false)
    .port(port)

  def seedAddresses = {
    config
      .getStringList("cluster.seedNodes")
      .asScala
      .map(Address.from)
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
  def connect(clusterConfig: ClusterConfig) = {
    println(s"$port: CONNECTING: ", clusterConfig.getSeedMembers)

    val cluster = Cluster.joinAwait(clusterConfig)

    state = NodeState(cluster)
        .addPeers(cluster.otherMembers().asScala.toSeq)

    subscribe {
      cluster.listenMembership()
        .subscribe(event => {
          println(s"$port: MEMBERSHIP", event)
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
        println(s"$port: MESSAGE", msg)
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
    println(s"$port: DISCONNECTING SHUTDOWN")
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
    if (seedAddresses.nonEmpty) {
      connect(buildClusterConfig.build())
    }
  }

  override def postStop(): Unit = {
    if (cluster != null) {
      disconnect()
    }
  }

  def receive = {
    case Connect(addresses) =>
      val clusterConfig  = buildClusterConfig
        .seedMembers(addresses.asJava)

      connect(clusterConfig.build)

      sender() ! Connected()

    case Terminated(ref) =>
      listeners = listeners - ref.hashCode()
  }
}
