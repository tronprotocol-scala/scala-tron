package org.tron.peer

import javax.inject.Inject

import org.tron.core._
import org.tron.storage.DbFactory
import org.tron.utils.{ByteArrayUtils, KeyUtils}
import org.tron.wallet.Wallet


class PeerBuilder @Inject() (
  nodeKeyFactory: NodeKeyFactory,
  dbFactory: DbFactory) {

  def build(peerType: String) = {
    val key = nodeKeyFactory.build()

    // Build the wallet
    val wallet = Wallet(key)

    // Build the blockchain
    val blockchain = new BlockchainImpl(dbFactory.build(Constant.BLOCK_DB_NAME))
    if (blockchain.lastHash == null) {
      val sender = Key(key)
      println(sender.info)
      blockchain.addGenesisBlock(sender.address.hex)
    }

    val utxoSet = new UTXOSet(dbFactory.build(Constant.TRANSACTION_DB_NAME), blockchain)
    utxoSet.reindex()

    Peer(key, wallet, blockchain, utxoSet, peerType)
  }
}
