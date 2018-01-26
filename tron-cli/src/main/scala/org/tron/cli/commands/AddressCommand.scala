package org.tron.cli.commands

import org.tron.application.{Application, CliGlobals}
import org.tron.utils.KeyUtils

object AddressCommand {
  val openAddressInstructions = "Before transferring funds you need to open a address using 'address --open <private key>'"
}

case class AddressCommand(key: Option[String] = None) extends Command {
  override def execute(app: Application, parameters: Array[String]): Unit = {
    app match  {
      case cli: CliGlobals =>
        key.foreach { privateKey =>
          val privKey = KeyUtils.fromPrivateKey(privateKey)
          cli.activeAddress = Some(privKey)
          println("Opened address " + privKey.address.hex)
        }
    }
  }
}
