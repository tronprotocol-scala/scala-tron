package org.tron.wallet

import org.tron.api.api.{Coin, Return, WalletGrpc}
import org.tron.core.Address
import org.tron.protos.Tron.{Account, Transaction}

import scala.concurrent.Future

class WalletApi(wallet: Wallet) extends WalletGrpc.Wallet {

  import scala.concurrent.ExecutionContext.Implicits.global

  def getBalance(request: Account): Future[Account] = Future  {
    if (request.address != null) {
      val balance = wallet.getBalance(Address(request.address))
      Account(balance = balance)
    } else {
      null
    }
  }

  def createTransaction(request: Coin): Future[Transaction] = Future  {
    val fromBs = request.from
    val toBs = request.to
    val amount = request.amount
    if (fromBs != null && toBs != null && amount > 0) {
      val fromAddress = Address(fromBs)
      val toAddress = Address(fromBs)
      wallet.createTransaction(fromAddress, toAddress, amount)
    } else {
      null
    }
  }

  def broadcastTransaction(transaction: Transaction): Future[Return] = Future {
    val result = wallet.broadcastTransaction(transaction)
    Return(result)
  }
}
