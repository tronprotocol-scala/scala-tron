package org.tron.network

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import com.google.inject.Guice
import com.typesafe.config.Config
import io.scalecube.transport.Address
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}
import org.specs2.matcher.Matchers
import org.tron.application.Module
import org.tron.core.Constant
import org.tron.network.GossipLocalNodeActor._


class ClusterSpec extends TestKit(ActorSystem()) with ImplicitSender with WordSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll {
    shutdown()
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

      Thread.sleep(10000)

    }
  }
}
