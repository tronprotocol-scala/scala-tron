package org.tron.cli.commands

import org.tron.application.{Application, PeerApplication}
import org.tron.core.PublicKey
import org.tron.peer.Peer

case class GetBalanceCommand() extends Command {
  def execute(app: Application, parameters: Array[String]): Unit = {

    app match {
      case peerApp: PeerApplication =>
        val peer: Peer = peerApp.peer

        val pubKeyHash = PublicKey(peer.wallet.address.key)
        val utxos = peer.uTXOSet.findUTXO(pubKeyHash)

        val balance = utxos.map(_.value).sum

        println(balance) // scalastyle:ignore
    }
  }
}
