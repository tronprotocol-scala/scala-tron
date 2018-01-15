package org.tron.cli.commands

import org.tron.api.HttpServer
import org.tron.application.Application

import scala.io.StdIn

case class ServerCommand() extends Command {
  def execute(app: Application, parameters: Array[String]): Unit = {

    val components = new HttpServer
    val server = components.server

    println("Press Enter to stop server")

    StdIn.readLine()

    server.stop()
  }
}
