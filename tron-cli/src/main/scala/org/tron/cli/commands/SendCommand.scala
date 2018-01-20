package org.tron.cli.commands

import org.tron.application.{Application, PeerApplication}
import org.tron.cluster.ClusterTransactionStrategy
import org.tron.core.TransactionFacade
import org.tron.utils.ByteArray

case class SendCommand(to: String = "", amount: Int = 0) extends Command {
  def execute(app: Application, parameters: Array[String]): Unit = {

    app match {
      case peerApp: PeerApplication =>
        val peer = peerApp.peer
        val fromAddress = ByteArray.toHexString(peer.wallet.key.getPrivKeyBytes)
        val transactionFacade = new TransactionFacade(peer, app.injector.getInstance(classOf[ClusterTransactionStrategy]))
        transactionFacade.newTransaction(fromAddress, to, amount)
    }
  }
}
