package org.tron.cluster

import akka.actor
import akka.actor.{ActorSystem, Address, Props}
import akka.cluster.Cluster
import org.tron.peer.Peer
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class PeerCluster(peer: Peer, system: ActorSystem) {

  val clusterListener = system.actorOf(Props[ClusterListener], "listener")

  def start() = {

  }

  def joinSeedNode(seedNode: String) = {

    val Array(host, port) = seedNode.split(":")

    val seedNodeAddress = Address("akka.tcp", "TronCluster", host, port.toInt)

    system.scheduler.scheduleOnce(2.seconds) {
      Cluster.get(system).joinSeedNodes(List(seedNodeAddress))
    }

    Cluster.get(system).registerOnMemberUp()
  }

  def startLeader() = {

    val selfAddress = Cluster.get(system).selfAddress

    system.scheduler.scheduleOnce(2.seconds) {
      Cluster.get(system).joinSeedNodes(List(selfAddress))
    }
  }

}
