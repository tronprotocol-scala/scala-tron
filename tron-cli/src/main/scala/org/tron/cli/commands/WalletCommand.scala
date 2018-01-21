package org.tron.cli.commands

import org.tron.application.{Application, CliGlobals}
import org.tron.core.Key

case class WalletCommand(key: Option[String] = None) extends Command {
  override def execute(app: Application, parameters: Array[String]): Unit = {
    app match  {
      case cli: CliGlobals =>
        key.foreach { privateKey =>
          val wallet = org.tron.crypto.ECKey.fromPrivate(org.tron.core.Base58.decodeToBigInteger(privateKey), true)
          val walletKey = Key(wallet)
          cli.activeWallet = Some(walletKey)
          println("Opened wallet " + walletKey.addressHex)
        }
    }
  }
}
