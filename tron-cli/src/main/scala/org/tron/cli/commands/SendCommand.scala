package org.tron.cli.commands

import org.tron.application.{Application, CliGlobals, PeerApplication}
import org.tron.cluster.ClusterTransactionStrategy
import org.tron.core.{Address, TransactionFacade}

case class SendCommand(to: String = "", amount: Int = 0) extends Command {
  def execute(app: Application, parameters: Array[String]): Unit = {

    val peerApp = app.asInstanceOf[PeerApplication]

    app match {
      case globals: CliGlobals =>
        globals.activeAddress match {
          case Some(wallet) =>
            val fromAddressPrivateKey = wallet.privateKeyCompressed
            val transactionFacade = new TransactionFacade(peerApp.peer, app.injector.getInstance(classOf[ClusterTransactionStrategy]))
            transactionFacade.newTransaction(fromAddressPrivateKey, Address(to), amount)
          case None =>
            println(AddressCommand.openAddressInstructions)
        }
    }
  }
}
