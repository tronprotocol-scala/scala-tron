package org.tron.cli

import com.google.inject.Guice
import org.tron.application.{Application, CliGlobals, Module, PeerApplication}
import org.tron.cli.commands._
import org.tron.peer.{Peer, PeerBuilder}
import scopt.OptionParser

import scala.io.StdIn

object App {

  val commandParser = new scopt.OptionParser[CommandConfig]("tron") {
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

    cmd("address")
      .action(withCommand(AddressCommand()))
      .children {
        opt[String]("open")
          .action { (y, c) =>
            val cmd = c.command.map {
              case wallet: AddressCommand =>
                wallet.copy(key = Some(y))
              case x =>
                throw new Exception("Wrong command")
            }

            c.copy(command = cmd)
          }
          .text("private key")
        opt[Unit]("create")
          .action { (_, c) =>
            c.copy(command = Some(CreateAddressCommand()))
          }
          .text("create address")
      }
      .text("Address")

    cmd("send")
      .action(withCommand(SendCommand()))
      .children {
        opt[String]("to")
          .action { (y, c) =>
            val cmd = c.command.map {
              case send: SendCommand =>
                send.copy(to = y)
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
          .text("join cluster as client")
      }
      .text("start cluster")

    cmd("exit").action(withCommand(ExitCommand())).
      text("close tron")
  }

  def withCommand(cmd: Command): (Unit, CommandConfig) => CommandConfig =
    (_, c) => c.copy(command = Some(cmd))

  def main(args: Array[String]): Unit = {

    val injector = Guice.createInjector(new Module())

    val app = new Application(injector) with PeerApplication with CliGlobals {
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
