package org.tron
package core

import org.specs2.mutable._
import org.tron.application.{AppFactory, Module}
import org.tron.blockchain.{Blockchain, BlockchainImpl}
import org.tron.storage.DbFactory
import org.tron.utils.KeyUtils
import org.tron.utxo.{TransactionUtils, UTXOSet}
import org.tron.wallet.Wallet

class BlockchainSpec extends Specification {

  sequential

  "Blockchain" should {

    "start blockchain with new genesis block" in {
      val injector = AppFactory.buildInjector
      val dbFactory = injector.getInstance(classOf[DbFactory])
      val blockchain = injector.getInstance(classOf[Blockchain])
      val key = KeyUtils.generateKey

      blockchain.addGenesisBlock(key.address)

      val utxoSet = new UTXOSet(dbFactory.build(Constant.TRANSACTION_DB_NAME), blockchain)
      utxoSet.reindex()

      utxoSet.getBalance(key) must equalTo(10L)

      blockchain.blockDB.close()
      utxoSet.txDB.close()

      ok
    }

    "make a transaction between addresses" in {
      val injector = AppFactory.buildInjector
      val dbFactory = injector.getInstance(classOf[DbFactory])
      val blockchain = injector.getInstance(classOf[Blockchain]).asInstanceOf[BlockchainImpl]

      val sender = KeyUtils.generateKey
      val senderWallet = Wallet(sender.ecKey)
      val receiver = KeyUtils.generateKey
      val receiverWallet = Wallet(receiver.ecKey)

      blockchain.addGenesisBlock(sender.address)

      val utxoSet = new UTXOSet(dbFactory.build(Constant.TRANSACTION_DB_NAME), blockchain)
      utxoSet.reindex()

      // First check if the sender has enough
      utxoSet.getBalance(sender) must equalTo(10L)

      // Make transaction
      TransactionUtils.newTransaction(senderWallet, receiverWallet.address, 10, utxoSet).map { transaction =>
        val newBlock = blockchain.addBlock(List(transaction))
        awaitResult(blockchain.receiveBlock(newBlock, utxoSet))
      }

      utxoSet.getBalance(sender) must equalTo(0L)
      utxoSet.getBalance(receiver) must equalTo(10L)

      blockchain.blockDB.close()
      utxoSet.txDB.close()

      ok
    }
  }
}
