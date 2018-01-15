package org.tron.peer

import javax.inject.Inject

import org.tron.core._
import org.tron.storage.DbFactory
import org.tron.utils.ByteArray
import org.tron.wallet.Wallet


class PeerBuilder @Inject() (
  nodeKeyFactory: NodeKeyFactory,
  dbFactory: DbFactory) {

  def build(peerType: String) = {
    val key = nodeKeyFactory.build()

    // Build the wallet
    val wallet = Wallet(PublicKey(key.getAddress))

    // Build the blockhain
    val blockchain = new BlockchainImpl(
      dbFactory.buildOrCreate(Constant.BLOCK_DB_NAME, PublicKey(key.getAddress).hex),
      ByteArray.toHexString(wallet.address.key))

    val utxoSet = new UTXOSet(dbFactory.build(Constant.TRANSACTION_DB_NAME), blockchain)
    utxoSet.reindex()

    Peer(key, wallet, blockchain, utxoSet, peerType)
  }
}
