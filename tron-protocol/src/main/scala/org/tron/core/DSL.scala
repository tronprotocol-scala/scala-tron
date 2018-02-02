package org.tron.core

import org.tron.wallet.Wallet

/**
  * Domain Specific Language for Tron
  */
object DSL {

  trait WeiUnit {
    def conversionFactor: Double
    def value: Double
    def +(wei: WeiUnit) = Wei((value * conversionFactor) + (wei.value * wei.conversionFactor))
    def -(wei: WeiUnit) = Wei((value * conversionFactor) - (wei.value * wei.conversionFactor))
    def /(wei: WeiUnit) = Wei((value * conversionFactor) / (wei.value * wei.conversionFactor))

    override def equals(other: Any): Boolean = other match {
      case wei: WeiUnit =>
        (value * conversionFactor) == (wei.value * wei.conversionFactor)
      case _ =>
        false
    }
  }

  case class Wei(value: Double) extends WeiUnit {
    def conversionFactor: Double = 1
  }

  case class Ether(value: Double) extends WeiUnit {
    def conversionFactor: Double = 1e18
    def +(ether: Ether) = Ether(value + ether.value)
    def -(ether: Ether) = Ether(value - ether.value)
    def /(ether: Ether) = Ether(value / ether.value)
  }

  implicit class NumberImplicits(number: Double) {
    def ether = Ether(number)
    def wei = Wei(number)
  }

  case class TransactionBuilder(wallet: Wallet, amount: WeiUnit = Wei(0), to: Address = Address.EMPTY) {
    def info = s"SENDING $amount FROM ${wallet.address} TO ${to.hex}"
    def to(toAddress: Address) = copy(to = toAddress)
  }

  implicit class WalletImplicits(wallet: Wallet) {
    def send(wei: WeiUnit) = TransactionBuilder(wallet, wei)
  }
}
