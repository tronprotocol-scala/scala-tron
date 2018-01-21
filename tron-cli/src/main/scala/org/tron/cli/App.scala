package org.tron.cli

import com.google.inject.Guice
import org.tron.application.{Application, CliGlobals, Module, PeerApplication}
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

    cmd("wallet")
      .action(withCommand(WalletCommand()))
      .children {
        opt[String]("key")
          .action { (y, c) =>
            val cmd = c.command.map {
              case wallet: WalletCommand =>
                wallet.copy(key = Some(y))
              case x =>
                throw new Exception("Wrong command")
            }

            c.copy(command = cmd)
          }
          .text("private key")
        opt[Unit]("create")
          .action { (_, c) =>
            c.copy(command = Some(CreateWalletCommand()))
          }
          .text("create wallet")
      }
      .text("Wallet")

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
