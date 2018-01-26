package org.tron.cli.commands

import org.tron.application.Application

case class ExitCommand() extends Command {
  def execute(app: Application, parameters: Array[String]): Unit = {
    System.exit(0)
  }
}
