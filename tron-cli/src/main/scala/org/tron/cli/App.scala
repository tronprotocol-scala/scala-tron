package org.tron.cli

import com.google.inject.Guice
import com.typesafe.config.Config
import org.tron.application.{Application, CliGlobals, Module, PeerApplication}
import org.tron.peer.{Peer, PeerBuilder}

import scala.io.StdIn
import scala.util.{Failure, Success, Try}

object App {

  def main(args: Array[String]): Unit = {

    val injector = Guice.createInjector(new Module())
    val config = injector.getInstance(classOf[Config])
    val app = new Application(injector, config) with PeerApplication with CliGlobals {
      val peer: Peer = injector.getInstance(classOf[PeerBuilder]).build()
    }

    while (true) {
      val args = readArgs()
      if (args.nonEmpty) {
        handleCommandArgs(app, args)
      }
    }
  }

  def readArgs(): Array[String] = {
    StdIn.readLine.trim.split("\\s+")
  }

  def handleCommandArgs(app: Application, args: Array[String]): Unit = {
    Parameters.commandParser.parse(args, CommandConfig()) match {
      case Some(config) =>
        handleCommand(app, config)
      case _ =>
        handleCommandArgs(app, readArgs())
    }
  }

  def handleCommand(app: Application, config: CommandConfig): Unit = {
    config.command match {
      case Some(command) =>
        Try(command.execute(app, Array())) match {
          case Success(_) =>
          case Failure(exc) =>
            println("Error during command", exc)
        }
      case _ =>
    }
  }
}
