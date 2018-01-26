package org.tron.cli.commands

import org.tron.application.{Application, PeerApplication}
import org.tron.utils.ByteArrayUtils

case class AccountCommand() extends Command {
  def execute(app: Application, parameters: Array[String]): Unit = {

    app match {
      case peer: PeerApplication =>
        println(ByteArrayUtils.toHexString(peer.peer.key.getAddress)) // scalastyle:ignore
    }
  }
}
