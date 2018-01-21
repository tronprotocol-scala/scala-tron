package org.tron.wallet

import org.specs2.mutable._

class WalletSpec extends Specification {

  "Wallet Spec" should {

    "Generate new address" in {

      val wallet = Wallet()

      wallet.address.addressHex must not beEmpty
    }
  }
}