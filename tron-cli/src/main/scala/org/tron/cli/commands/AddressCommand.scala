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
          val address = KeyUtils.fromPrivateKey(privateKey)
          cli.activeAddress = Some(address)
          println("Opened address " + address.addressHex)
        }
    }
  }
}
