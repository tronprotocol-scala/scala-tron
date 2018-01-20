package org.tron.cli

import com.google.inject.Guice
import org.tron.application.{Application, Module, PeerApplication}
import org.tron.cli.commands._
import org.tron.peer.{Peer, PeerBuilder}
import scopt.OptionParser

import scala.io.StdIn

object App {

  val parser: OptionParser[Config] = new scopt.OptionParser[Config]("tron") {
    head("tron", "0.1")

    cmd("help").action(withCommand(HelpCommand()))

    cmd("account").action(withCommand(AccountCommand())).
      text("Shows the current account")

    cmd("version").action(withCommand(VersionCommand())).
      text("Shows the current version")

    cmd("exit").action(withCommand(ExitCommand())).
      text("close tron")
  }

  def withCommand(cmd: Command): (Unit, Config) => Config =
    (_, c) => c.copy(command = Some(cmd))

  def main(args: Array[String]): Unit = {
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
        val peer: Peer = injector.getInstance(classOf[PeerBuilder]).build(config.peerType)
      }

      handleCommand(app, config)
    }
  }

  def handleCommand(app: Application, config: Config): Unit = {
    config.command match {
      case Some(command) =>
        command.execute(app, Array())
      case _ =>
    }
  }

}
