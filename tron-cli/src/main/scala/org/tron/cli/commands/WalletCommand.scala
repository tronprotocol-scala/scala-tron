package org.tron.cli.commands

import org.tron.application.{Application, CliGlobals}
import org.tron.utils.KeyUtils

case class WalletCommand(key: Option[String] = None) extends Command {
  override def execute(app: Application, parameters: Array[String]): Unit = {
    app match  {
      case cli: CliGlobals =>
        key.foreach { privateKey =>
          val wallet = KeyUtils.fromPrivateKey(privateKey)
          cli.activeWallet = Some(wallet)
          println("Opened wallet " + wallet.addressHex)
        }
    }
  }
}
