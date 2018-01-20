package org.tron.cli

import com.google.inject.Guice
import org.tron.application.{Application, Module, PeerApplication}
import org.tron.cli.commands._
import org.tron.peer.{Peer, PeerBuilder}
import scopt.OptionParser

import scala.io.StdIn

object App {

  val commandParser: OptionParser[CommandConfig]
    = new scopt.OptionParser[CommandConfig]("tron") {
    head("tron", "0.1")

    cmd("help")
      .action(withCommand(HelpCommand()))
      .text("Shows how to use the application")

    cmd("account")
      .action(withCommand(AccountCommand()))
      .text("Shows the current account")

    cmd("server")
      .action(withCommand(ServerCommand()))
      .text("Start the API server")

    cmd("send")
      .action(withCommand(SendCommand()))
      .children {
        opt[String]("to")
          .action { (y, c) =>
            val cmd = c.command.map {
              case cluster: SendCommand =>
                cluster.copy(to = y)
              case x =>
                x
            }

            c.copy(command = cmd)
          }
          .text("from address")
        opt[Int]("amount")
          .action { (y, c) =>
            val cmd = c.command.map {
              case cluster: SendCommand =>
                cluster.copy(amount = y)
              case x =>
                x
            }

            c.copy(command = cmd)
          }
          .text("amount to send")
      }
      .text("Start the API server")

    cmd("version")
      .action(withCommand(VersionCommand()))
      .text("Shows the current version")

    cmd("balance")
      .action(withCommand(GetBalanceCommand()))
      .text("show balance")

    cmd("cluster")
      .action(withCommand(ClusterCommand()))
      .children {
        opt[String]("join")
          .action { (y, c) =>
            val cmd = c.command.map {
              case cluster: ClusterCommand =>
                cluster.copy(seedNode = Some(y))
              case x =>
                x
            }

            c.copy(command = cmd)
          }
          .text("disable keep alive")
      }
      .text("start cluster")

    cmd("exit").action(withCommand(ExitCommand())).
      text("close tron")
  }

  def withCommand(cmd: Command): (Unit, CommandConfig) => CommandConfig =
    (_, c) => c.copy(command = Some(cmd))

  def main(args: Array[String]): Unit = {

    val injector = Guice.createInjector(new Module())

    val app = new Application(injector) with PeerApplication {
      val peer: Peer = injector.getInstance(classOf[PeerBuilder]).build("client")
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
    commandParser.parse(args, CommandConfig()) match {
      case Some(config) =>
        handleCommand(app, config)
      case _ =>
        handleCommandArgs(app, readArgs())
    }
  }

  def handleCommand(app: Application, config: CommandConfig): Unit = {
    config.command match {
      case Some(command) =>
        command.execute(app, Array())
      case _ =>
    }
  }
}
