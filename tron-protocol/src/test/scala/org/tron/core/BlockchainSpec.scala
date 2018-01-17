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
      val key = KeyUtils.newKey

      blockchain.addGenesisBlock(key.hex)

      val utxoSet = new UTXOSet(dbFactory.build(Constant.TRANSACTION_DB_NAME), blockchain)
      utxoSet.reindex()

      utxoSet.getBalance(key) must equalTo(10L)

      blockchain.blockDB.close()
      utxoSet.txDB.close()

      ok
    }

    "make a transaction between wallets" in {

      val blockchain = module.buildBlockchain().asInstanceOf[BlockchainImpl]
      val sender = KeyUtils.newKey
      val senderWallet = Wallet(sender.ecKey)
      val receiver = KeyUtils.newKey
      val receiverWallet = Wallet(receiver.ecKey)

      blockchain.addGenesisBlock(sender.hex)

      val utxoSet = new UTXOSet(dbFactory.build(Constant.TRANSACTION_DB_NAME), blockchain)
      utxoSet.reindex()

      // First check if the sender has enough
      utxoSet.getBalance(sender) must equalTo(10L)

      // Make transaction
      val transaction = TransactionUtils.newTransaction(senderWallet, receiverWallet.address, 10, utxoSet)

      val newBlock = blockchain.addBlock(List(transaction))
      blockchain.receiveBlock(newBlock, utxoSet)

      utxoSet.getBalance(sender) must equalTo(0L)
      utxoSet.getBalance(receiver) must equalTo(10L)

      blockchain.blockDB.close()
      utxoSet.txDB.close()

      ok
    }
  }
}
