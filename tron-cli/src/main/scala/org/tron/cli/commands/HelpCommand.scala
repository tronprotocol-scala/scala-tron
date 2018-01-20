package org.tron.cli.commands
import org.tron.application.{Application, PeerApplication}

case class HelpCommand() extends Command {
  override def execute(app: Application, parameters: Array[String]): Unit = {
    app match  {
      case peer: PeerApplication =>
        println("How to use")
    }
  }
}
