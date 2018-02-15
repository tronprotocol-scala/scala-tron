package org.tron.cli.commands

import akka.actor.ActorSystem
import org.tron.application.{Application, PeerApplication}

// scalastyle:off regex
case class ClusterCommand(seedNode: Option[String] = None) extends Command {
  def execute(app: Application, parameters: Array[String]): Unit = {

    app match {
      case peer: PeerApplication =>
//        val peerCluster = new PeerCluster(peer.peer, app.injector.getInstance(classOf[ActorSystem]))
//        peerCluster.start()
//        seedNode match {
//          case Some(seedNodeAddress) =>
//            println("JOINING CLUSTER AS CLIENT")
//            peerCluster.joinSeedNode(seedNodeAddress)
//          case None =>
//            println("STARTING CLUSTER AS LEADER")
//            peerCluster.startLeader()
//        }
    }
  }
}
