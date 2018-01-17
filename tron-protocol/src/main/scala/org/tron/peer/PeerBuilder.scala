package org.tron.peer

import javax.inject.Inject

import org.tron.core._
import org.tron.storage.DbFactory
import org.tron.utils.{ByteArray, KeyUtils}
import org.tron.wallet.Wallet


class PeerBuilder @Inject() (
  nodeKeyFactory: NodeKeyFactory,
  dbFactory: DbFactory) {

  def build(peerType: String) = {
    val key = nodeKeyFactory.build()

    // Build the wallet
    val wallet = Wallet(key)

    // Build the blockchain
    val blockchain = new BlockchainImpl(dbFactory.buildOrCreate(Constant.BLOCK_DB_NAME))
    if (blockchain.lastHash == null) {

      val sender = KeyUtils.newKey

      println("Adding genesis for address", ByteArray.toHexString(sender.ecKey.getPubKey))

      blockchain.addGenesisBlock(sender.hex)
    }


    val utxoSet = new UTXOSet(dbFactory.build(Constant.TRANSACTION_DB_NAME), blockchain)
    utxoSet.reindex()

    Peer(key, wallet, blockchain, utxoSet, peerType)
  }
}
