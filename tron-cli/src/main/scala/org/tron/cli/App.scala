package org.tron.cli

import com.google.inject.Guice
import org.tron.application.{Application, Module, PeerApplication}
import org.tron.cli.commands.{AccountCommand, ExitCommand, ServerCommand, VersionCommand}
import org.tron.peer.PeerBuilder

import scala.io.StdIn

object App {

  val parser = new scopt.OptionParser[Config]("tron") {
    head("tron", "0.1")

    opt[String]('t', "type").action( (x, c) =>
      c.copy(peerType = x) ).text("type can be server or client")

    help("help").text("How to use")

    cmd("account").action( (_, c) => c.copy(command = Some(AccountCommand()))).
      text("Shows the current account")

    cmd("server").action( (_, c) => c.copy(command = Some(ServerCommand()))).
      text("Start the API server")

    cmd("version").action( (_, c) => c.copy(command = Some(VersionCommand()))).
      text("Shows the current version")

    cmd("exit").action( (_, c) => c.copy(command = Some(ExitCommand()))).
      text("close tron")
  }


  def main(args: Array[String]) = {
    handleArgs(args)
  }

  def handleArgs(args: Array[String]): Unit = {
    parser.parse(args, Config(peerType = "client")).foreach { config =>

      val injector = Guice.createInjector(new Module())

      val app = new Application(injector) with PeerApplication {
        val peer = injector.getInstance(classOf[PeerBuilder]).build(config.peerType)
      }

      handleCommand(app, config)
    }
  }

  def handleCommand(app: Application, config: Config) = {
    config.command match {
      case Some(command) =>
        command.execute(app, Array())
      case _ =>
    }

    handleArgs(StdIn.readLine.trim.split("\\s+"))
  }

}
