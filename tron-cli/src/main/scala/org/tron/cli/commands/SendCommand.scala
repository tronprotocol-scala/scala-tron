package org.tron.cli.commands

import org.tron.application.{Application, PeerApplication}
import org.tron.core.TransactionUtils

case class SendCommand() extends Command {
  def execute(app: Application, parameters: Array[String]): Unit = {

    app match {
      case peerApp: PeerApplication =>
        val peer = peerApp.peer
        val to = parameters(0)
        val amount = parameters(1).toLong
        val transaction = TransactionUtils.newTransaction(peer.wallet, to, amount, peer.uTXOSet)
    }
  }
}
