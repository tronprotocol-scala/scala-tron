package org.tron.cli

import com.google.inject.Guice
import org.tron.application.{Application, Module, PeerApplication}
import org.tron.cli.commands._
import org.tron.peer.PeerBuilder

import scala.io.StdIn

object App {

  val appParser = new scopt.OptionParser[AppConfig]("tron") {
    head("tron", "0.1")

    opt[String]('t', "type").action( (x, c) =>
      c.copy(peerType = x) ).text("type can be server or client")
  }

  val commandParser = new scopt.OptionParser[CommandConfig]("tron") {
    head("tron", "0.1")

    help("help").text("How to use")

    cmd("account").action( (_, c) => c.copy(command = Some(AccountCommand()))).
      text("Shows the current account")

    cmd("server").action( (_, c) => c.copy(command = Some(ServerCommand()))).
      text("Start the API server")

    cmd("version").action( (_, c) => c.copy(command = Some(VersionCommand()))).
      text("Shows the current version")

    cmd("balance").action( (_, c) => c.copy(command = Some(GetBalanceCommand()))).
      text("show balance")

    cmd("exit").action( (_, c) => c.copy(command = Some(ExitCommand()))).
      text("close tron")
  }


  def main(args: Array[String]) = {
    appParser.parse(args, AppConfig(peerType = "client")).foreach { config =>

      val injector = Guice.createInjector(new Module())

      val app = new Application(injector) with PeerApplication {
        val peer = injector.getInstance(classOf[PeerBuilder]).build(config.peerType)
      }

      handleCommandArgs(app, StdIn.readLine.trim.split("\\s+"))
    }

  }

  def handleCommandArgs(app: Application, args: Array[String]): Unit = {
    commandParser.parse(args, CommandConfig()).foreach { config =>
      handleCommand(app, config)
    }
  }

  def handleCommand(app: Application, config: CommandConfig) = {
    config.command match {
      case Some(command) =>
        command.execute(app, Array())
      case _ =>
    }

    handleCommandArgs(app, StdIn.readLine.trim.split("\\s+"))
  }

}
