package org.tron.cli.commands

import org.tron.application.Application

abstract class Command {
  def execute(peer: Application, parameters: Array[String]): Unit
}
