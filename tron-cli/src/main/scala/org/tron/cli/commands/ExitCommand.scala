package org.tron.cli.commands

import org.tron.application.{Application, PeerApplication}
import org.tron.utils.ByteArrayUtils

case class ExitCommand() extends Command {
  def execute(app: Application, parameters: Array[String]): Unit = {
    System.exit(0)
  }
}
