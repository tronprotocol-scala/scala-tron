package org.tron.cli

import com.google.inject.Guice
import org.tron.application.{Application, Module, PeerApplication}
import org.tron.cli.commands._
import org.tron.peer.{Peer, PeerBuilder}

import scala.io.StdIn

object App {

  val parser = new scopt.OptionParser[Config]("tron") {
    head("tron", "0.1")

    opt[String]('t', "type").action( (x, c) =>
      c.copy(peerType = x) ).text("type can be server or client")

    cmd("help").action( (_, c) => doCopy(c, HelpCommand()))

    cmd("account").action( (_, c) => doCopy(c, AccountCommand())).
      text("Shows the current account")

    cmd("version").action( (_, c) => doCopy(c, VersionCommand())).
      text("Shows the current version")

    cmd("exit").action( (_, c) => doCopy(c, ExitCommand())).
      text("close tron")
  }

  def doCopy(config: Config, command: Command): Config = {
    config.copy(command = Some(command))
  }

  def main(args: Array[String]) = {
    handleArgs(args)

    while(true){
      val args = StdIn.readLine.trim.split("\\s+")
      if(args.nonEmpty) {
        handleArgs(args)
      }
    }
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
  }

}
