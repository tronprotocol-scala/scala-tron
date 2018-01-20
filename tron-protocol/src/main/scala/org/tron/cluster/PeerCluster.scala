package org.tron.cluster

import akka.actor.{ActorSystem, Address, Props}
import akka.cluster.Cluster
import org.tron.core.Constant
import org.tron.peer.{Peer, PeerActor}

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class PeerCluster(peer: Peer, system: ActorSystem) {

  val clusterListener = system.actorOf(Props[ClusterListener], "listener")
  val peerActor = system.actorOf(Props(new PeerActor(peer)), "peer")

  def start() = {

  }

  /**
    * Join existing cluster
    *
    * @param seedNode address of active cluster node
    */
  def joinSeedNode(seedNode: String) = {

    val Array(host, port) = seedNode.split(":")

    val seedNodeAddress = Address("akka.tcp", Constant.SYSTEM_NAME, host, port.toInt)

    system.scheduler.scheduleOnce(1.seconds) {
      Cluster.get(system).joinSeedNodes(List(seedNodeAddress))
    }

    Cluster.get(system).registerOnMemberUp()
  }

  /**
    * Start cluster as leader
    */
  def startLeader() = {

    val selfAddress = Cluster.get(system).selfAddress

    system.scheduler.scheduleOnce(1.seconds) {
      Cluster.get(system).joinSeedNodes(List(selfAddress))
    }
  }

}
