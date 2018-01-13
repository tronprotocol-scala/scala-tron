package org.tron.cli.commands

import org.tron.application.{Application, PeerApplication}
import org.tron.peer.Peer
import org.tron.utils.ByteArray

case class AccountCommand() extends Command {
  def execute(app: Application, parameters: Array[String]): Unit = {

    app match {
      case peer: PeerApplication =>
        println(ByteArray.toHexString(peer.peer.key.getAddress)) // scalastyle:ignore
    }
  }
}
