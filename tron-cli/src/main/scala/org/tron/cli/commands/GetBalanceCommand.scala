package org.tron.cli.commands

import org.tron.application.{Application, CliGlobals, PeerApplication}
import org.tron.core.Key

case class GetBalanceCommand() extends Command {
  def execute(app: Application, parameters: Array[String]): Unit = {

    val peerApp = app.asInstanceOf[PeerApplication]
    val peer = peerApp.peer

    app match  {
      case cli: CliGlobals if cli.activeAddress.nonEmpty =>
        val wallet = cli.activeAddress.get

        val pubKeyHash = Key(wallet.ecKey)
        val utxos = peer.uTXOSet.findUTXO(pubKeyHash.address)

        val balance = utxos.map(_.value).sum

        println(balance) // scalastyle:ignore
      case _ =>
        println(AddressCommand.openAddressInstructions)
    }
  }
}
