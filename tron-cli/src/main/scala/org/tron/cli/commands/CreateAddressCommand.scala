package org.tron.cli.commands

import org.tron.application.{Application, CliGlobals}
import org.tron.utils.KeyUtils

case class CreateAddressCommand() extends Command {
  override def execute(app: Application, parameters: Array[String]): Unit = {
    app match  {
      case cli: CliGlobals =>
        val wallet = KeyUtils.generateKey
        println("Wallet Generated!")
        println(wallet.info)
    }
  }
}
