package org.tron.core

import org.tron.crypto.ECKey
import org.tron.peer.Peer
import org.tron.utils.ByteArray
import org.tron.wallet.Wallet

class TransactionFacade(peer: Peer, transactionStrategy: TransactionStrategy) {

  def newTransaction(fromAddress: String, toAddress: String, amount: Int) = {
    val pubKeyHash = ByteArray.fromHexString(fromAddress)
    val from = Wallet(ECKey.fromPrivate(pubKeyHash))

    val transaction = TransactionUtils.newTransaction(from, toAddress, amount, peer.uTXOSet)
    transactionStrategy.newTransaction(transaction)
  }

}
