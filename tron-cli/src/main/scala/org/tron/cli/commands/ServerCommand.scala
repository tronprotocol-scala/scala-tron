package org.tron.cli.commands

import org.tron.api.{Controller, HttpServer}
import org.tron.application.{Application, PeerApplication}

import scala.io.StdIn

case class ServerCommand() extends Command {
  def execute(app: Application, parameters: Array[String]): Unit = {

    val peerApp = app.asInstanceOf[PeerApplication]

    val components = new HttpServer(new Controller(peerApp.peer.blockchain, peerApp.peer.uTXOSet))
    val server = components.server

    println("Press Enter to stop server")

    StdIn.readLine()

    server.stop()
  }
}
