package org.tron.grpc

import org.tron.api.api.{Balance, WalletGrpc}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class WalletRpc() extends WalletGrpc.Wallet {

  def getBalance(request: Balance): Future[Balance] = Future {
    Balance(balance = 100)
  }
}
