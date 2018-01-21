package org.tron.cli.commands

import org.tron.application.{Application, CliGlobals, PeerApplication}
import org.tron.core.Key
import org.tron.peer.Peer

case class GetBalanceCommand() extends Command {
  def execute(app: Application, parameters: Array[String]): Unit = {

    val peerApp = app.asInstanceOf[PeerApplication]
    val peer = peerApp.peer

    app match  {
      case cli: CliGlobals if cli.activeWallet.nonEmpty =>
        val wallet = cli.activeWallet.get

        val pubKeyHash = Key(wallet.ecKey)
        val utxos = peer.uTXOSet.findUTXO(pubKeyHash.addressHex)

        val balance = utxos.map(_.value).sum

        println(balance) // scalastyle:ignore
      case _ =>
        println("Before checking balance you need to open a wallet using 'wallet --key <private key>'")
    }
  }
}
