package org.tron.core

import org.specs2.mutable._
import org.tron.application.Module
import org.tron.utils.KeyUtils

class BlockchainSpec extends Specification {

  val module = new Module()
  val dbFactory = module.buildDbFactory()

  sequential

  "Blockchain" should {

    "start blockchain with new genesis block" in {

      val blockchain = module.buildBlockchain()
      val key = KeyUtils.newKey

      blockchain.addGenesisBlock(key.hex)

      val utxoSet = new UTXOSet(dbFactory.build(Constant.TRANSACTION_DB_NAME), blockchain)
      utxoSet.reindex()

      utxoSet.getBalance(key) must equalTo(10L)
    }

  }

}
