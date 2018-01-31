package org.tron.grpc

import org.tron.api.api.{Account, Coin, Return, WalletGrpc}
import org.tron.protos.Tron.Transaction

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class WalletRpc() extends WalletGrpc.Wallet {

  def getBalance(request: Account): Future[Account] = Future {
    Account(balance = 100)
  }

  def createTransaction(request: Coin): Future[Transaction] = {
    ???
  }

  def broadcastTransaction(request: Transaction): Future[Return] = {
    ???
  }
}
