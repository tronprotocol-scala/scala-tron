package org.tron.core

import org.specs2.mutable._
import org.tron.application.Module
import org.tron.utils.KeyUtils
import org.tron.wallet.Wallet

class BlockchainSpec extends Specification {

  val module = new Module()
  val dbFactory = module.buildDbFactory()

  sequential

  "Blockchain" should {

    "start blockchain with new genesis block" in {

      val blockchain = module.buildBlockchain()
      val key = KeyUtils.generateKey

      blockchain.addGenesisBlock(key.addressHex)

      val utxoSet = new UTXOSet(dbFactory.build(Constant.TRANSACTION_DB_NAME), blockchain)
      utxoSet.reindex()

      utxoSet.getBalance(key) must equalTo(10L)

      blockchain.blockDB.close()
      utxoSet.txDB.close()

      ok
    }

    "make a transaction between addresses" in {

      val blockchain = module.buildBlockchain().asInstanceOf[BlockchainImpl]
      val sender = KeyUtils.generateKey
      val senderWallet = Wallet(sender.ecKey)
      val receiver = KeyUtils.generateKey
      val receiverWallet = Wallet(receiver.ecKey)

      blockchain.addGenesisBlock(sender.addressHex)

      val utxoSet = new UTXOSet(dbFactory.build(Constant.TRANSACTION_DB_NAME), blockchain)
      utxoSet.reindex()

      // First check if the sender has enough
      utxoSet.getBalance(sender) must equalTo(10L)

      // Make transaction
      TransactionUtils.newTransaction(senderWallet, receiverWallet.address.addressHex, 10, utxoSet).map { transaction =>
        val newBlock = blockchain.addBlock(List(transaction))
        blockchain.receiveBlock(newBlock, utxoSet)
      }

      utxoSet.getBalance(sender) must equalTo(0L)
      utxoSet.getBalance(receiver) must equalTo(10L)

      blockchain.blockDB.close()
      utxoSet.txDB.close()

      ok
    }
  }
}
