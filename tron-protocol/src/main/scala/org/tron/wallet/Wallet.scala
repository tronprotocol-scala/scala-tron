package org.tron.wallet

import org.tron.api.api.Return
import org.tron.core.{Address, Key}
import org.tron.crypto.ECKey
import org.tron.protos.Tron.Transaction
import org.tron.utils.Utils

import scala.concurrent.Future

case class Wallet(
  key: ECKey = new ECKey(Utils.getRandom)) {

  def broadcastTransaction(transaction: Transaction): Boolean = ???

  def address = Key(key).address

  def getBalance(address: Address): Long = {
    ???
  }
  def createTransaction(fromAddress: Address, toAddress: Address, amount: Long) = ???
}