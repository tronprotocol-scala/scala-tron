package org.tron.cli.commands

import org.tron.application.Application
import org.tron.peer.Peer

case class VersionCommand() extends Command {
  def execute(peer: Application, parameters: Array[String]): Unit = {
    println("show version!") // scalastyle:ignore
  }
}
