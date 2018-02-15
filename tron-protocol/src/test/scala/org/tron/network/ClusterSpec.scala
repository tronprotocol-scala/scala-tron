package org.tron.network

import akka.actor.{ActorRef, ActorSystem, PoisonPill}
import akka.testkit.{ImplicitSender, TestKit}
import com.google.inject.Guice
import com.typesafe.config.Config
import io.scalecube.transport.Address
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}
import org.specs2.matcher.Matchers
import org.tron.application.Module
import org.tron.core.Constant
import org.tron.network.GossipLocalNodeActor._
import scala.concurrent.duration._

class ClusterSpec extends TestKit(ActorSystem()) with ImplicitSender with WordSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll {
    println("SHUTDOWN")
    shutdown()
  }

  def disconnect(ref: ActorRef) = {
    ref ! Disconnect()
    expectMsg(20.seconds, Disconnected())
    ref ! PoisonPill
  }

  "Cluster" should {

    "Start master node" in {

      val module = Guice.createInjector(new Module(Constant.ENV_TEST))
      val config = module.getInstance(classOf[Config])

      val masterNode = system.actorOf(GossipLocalNodeActor.props(7000, config))
      masterNode ! Connect()

      expectMsg(Connected())

      val clientNode = system.actorOf(GossipLocalNodeActor.props(7001, config))
      clientNode ! Connect(List(Address.from("127.0.0.1:7000")))

      expectMsg(Connected())

      val clientNode2 = system.actorOf(GossipLocalNodeActor.props(7002, config))
      clientNode2 ! Connect(List(Address.from("127.0.0.1:7000")))

      expectMsg(Connected())

      disconnect(clientNode2)
      disconnect(clientNode)
      disconnect(masterNode)
    }

    "start and leave cluster" in {

      println("LEAVE")

      val module = Guice.createInjector(new Module(Constant.ENV_TEST))
      val config = module.getInstance(classOf[Config])

      // First get some nodes running

      val masterNode = system.actorOf(GossipLocalNodeActor.props(7000, config))
      masterNode ! Connect()

      expectMsg(Connected())

      val clientNode = system.actorOf(GossipLocalNodeActor.props(7001, config))
      clientNode ! Connect(List(Address.from("127.0.0.1:7000")))

      expectMsg(Connected())

      val clientNode2 = system.actorOf(GossipLocalNodeActor.props(7002, config))
      clientNode2 ! Connect(List(Address.from("127.0.0.1:7000")))

      expectMsg(Connected())

      // Then slowly leave cluster and see if members are all visible
      masterNode ! RequestState()

      // Verify the master node sees all nodes
      expectMsgPF() {
        case NodeState(_, members) if members.size == 2 => true
      }

      clientNode2 ! RequestState()
      expectMsgPF() {
        case NodeState(_, members) if members.size == 2 => true
      }

      // TODO hacky way to wait for the nodes to detect each other
      Thread.sleep(5000)

      clientNode ! RequestState()
      expectMsgPF() {
        case NodeState(_, members) if members.size == 2 => true
      }

      disconnect(clientNode2)

      // TODO hacky way to wait for the nodes to detect each other
      Thread.sleep(5000)

      masterNode ! RequestState()
      expectMsgPF() {
        case NodeState(_, members) if members.size == 1 => true
      }

      clientNode ! RequestState()
      expectMsgPF() {
        case NodeState(_, members) if members.size == 1 => true
      }

      disconnect(clientNode)
      masterNode ! RequestState()
      expectMsgPF() {
        case NodeState(_, members) if members.size == 0 => true
      }

      disconnect(masterNode)
    }
  }
}
