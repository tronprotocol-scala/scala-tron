package org.tron.cli.commands

import akka.actor.ActorSystem
import org.tron.application.{Application, PeerApplication}
import org.tron.cluster.PeerCluster
import org.tron.core.Constant

// scalastyle:off regex
case class ClusterCommand(seedNode: Option[String] = None) extends Command {
  def execute(app: Application, parameters: Array[String]): Unit = {

    app match {
      case peer: PeerApplication =>
        val actorSystem = ActorSystem(Constant.SYSTEM_NAME)
        val peerCluster = new PeerCluster(peer.peer, actorSystem)
        peerCluster.start()
        seedNode match {
          case Some(seedNodeAddress) =>
            println("JOINING CLUSTER AS CLIENT")
            peerCluster.joinSeedNode(seedNodeAddress)
          case None =>
            println("STARTING CLUSTER AS LEADER")
            peerCluster.startLeader()
        }
    }
  }
}
