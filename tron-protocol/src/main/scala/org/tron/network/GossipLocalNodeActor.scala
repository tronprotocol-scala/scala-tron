package org.tron.network

import akka.NotUsed
import akka.actor.{Actor, ActorRef, PoisonPill, Props, Terminated}
import akka.stream.OverflowStrategy
import akka.stream.scaladsl.Source
import com.typesafe.config.Config
import io.scalecube.cluster.membership.MembershipEvent.Type
import io.scalecube.cluster.{Cluster, ClusterConfig, Member}
import io.scalecube.transport.{Address, Message}
import org.tron.network.GossipLocalNodeActor._
import org.tron.network.message.{MessageDeserializer, MessageTypes, Message => TronMessage}
import org.tron.network.peer.PeerConnection
import rx.Subscription
import rx.subscriptions.CompositeSubscription
import akka.pattern._

import scala.collection.JavaConverters._
import scala.concurrent.{Future, Promise}

object GossipLocalNodeActor {

  case class Connect(seedAddresses: Seq[Address] = List.empty)
  case class Connected()
  case class Disconnect()
  case class Disconnected()
  case class RequestState()

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

    println(s"$port INITIAL STATE", state.members.size)

    subscribe {
      cluster.listenMembership()
        .subscribe(event => {
          println(s"$port: MEMBERSHIP", event)
          event.`type`() match {
            case Type.REMOVED =>

              println(s"$port: REMOVE", event.oldMember().address().port())
              state = state.removePeer(event.oldMember())

            case Type.ADDED | Type.UPDATED =>
              println(s"$port: ADD", event.newMember().address().port())
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
  def disconnect(): Future[Boolean] = {

    val promise = Promise[Boolean]()

    if (cluster != null) {
      subscriptions.clear()
      listeners.values.foreach(_ ! PoisonPill)
      listeners = Map.empty

      cluster.shutdown().whenComplete { (done, x) =>
        promise.success(true)
      }
      state = state.copy(cluster = null)

    } else {
      promise.success(false)
    }

    promise.future
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
    disconnect()
  }

  def receive = {
    case Connect(addresses) =>
      val clusterConfig  = buildClusterConfig
        .seedMembers(addresses.asJava)
      connect(clusterConfig.build)
      sender() ! Connected()

    case RequestState() =>
      println(s"$port: STATE REQUEST", state)
      sender() ! state

    case Terminated(ref) =>
      listeners = listeners - ref.hashCode()

    case Disconnect() =>
      import context.dispatcher
      val reply = sender()
      disconnect().foreach { _ =>
        reply ! Disconnected()
      }
  }
}
