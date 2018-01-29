package org.tron
package core


import org.specs2.mutable._
import DSL._
import org.tron.utils.KeyUtils
import org.tron.wallet.Wallet

class DslSpec extends Specification {

  sequential

  "DSL" should {

    "addition" in {
      Ether(1) + Ether(1)   must equalTo(Ether(2))
      Wei(500) + Wei(1500)  must equalTo(Wei(2000))
      Wei(500) + Ether(1)   must equalTo(Wei(1000000000000000500D))
    }

    "subtraction" in {
      Ether(1) - Ether(1)       must equalTo(Ether(0))
      Wei(500) - Wei(200)       must equalTo(Wei(300))
      Ether(2) - Wei(250)       must equalTo(Wei(1999999999999999700D))
    }

    "division" in {
      Ether(6) / Ether(2)       must equalTo(Ether(3))
      Wei(1000) / Wei(200)      must equalTo(Wei(5))
      Ether(2) / Wei(250)       must equalTo(Wei(8e15))
    }

    "equals" in {
      Ether(1) == Ether(1)                      must beTrue
      Ether(1) == Wei(1e18)                     must beTrue
      (Ether(2) + Wei(500)) == Wei(2e18 + 500)  must beTrue
    }

    "number implicits" in {
      1.ether + 200.wei must equalTo((1e18 + 200).wei)
      1.ether + 1.ether must equalTo(2 ether)
      100.wei + 100.wei must equalTo(200 wei)
    }

    "make transaction" in {

      val wallet = Wallet()
      val receivingAddress = KeyUtils.generateKey.address

      val transaction = wallet send 1.ether to receivingAddress

      transaction.amount must equalTo(1 ether)
      transaction.to.hex must equalTo(receivingAddress.hex)
    }
  }
}
