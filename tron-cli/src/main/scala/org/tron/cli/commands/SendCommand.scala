package org.tron.cli.commands

import org.tron.application.{Application, CliGlobals, PeerApplication}
import org.tron.cluster.ClusterTransactionStrategy
import org.tron.core.TransactionFacade

case class SendCommand(to: String = "", amount: Int = 0) extends Command {
  def execute(app: Application, parameters: Array[String]): Unit = {

    val peerApp = app.asInstanceOf[PeerApplication]

    app match {
      case globals: CliGlobals =>
        globals.activeWallet match {
          case Some(wallet) =>
            val fromAddress = wallet.privateKeyCompressed
            val transactionFacade = new TransactionFacade(peerApp.peer, app.injector.getInstance(classOf[ClusterTransactionStrategy]))
            transactionFacade.newTransaction(fromAddress, to, amount)
          case None =>
            println("Before transferring funds you need to open a wallet using 'wallet --open <private key>'")
        }
    }
  }
}
