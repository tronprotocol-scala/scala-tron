package org.tron.core

import org.tron.peer.Peer
import org.tron.utils.KeyUtils
import org.tron.wallet.Wallet

class TransactionFacade(peer: Peer, transactionStrategy: TransactionStrategy) {

  def newTransaction(fromAddressPrivateKey: String, toAddress: Address, amount: Int) = {
    val from = KeyUtils.fromPrivateKey(fromAddressPrivateKey)
    val fromWallet = Wallet(from.ecKey)

    for {
      transaction <- TransactionUtils.newTransaction(fromWallet, toAddress, amount, peer.uTXOSet)
    } transactionStrategy.newTransaction(transaction)
  }

}
