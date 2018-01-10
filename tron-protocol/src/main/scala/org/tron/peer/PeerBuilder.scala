package org.tron.peer

import javax.inject.Inject

import org.tron.core._
import org.tron.utils.ByteArray
import org.tron.wallet.Wallet


class PeerBuilder @Inject() (nodeKeyFactory: NodeKeyFactory) {

  def build(peerType: String) = {
    val key = nodeKeyFactory.build()

    // Build the wallet
    val wallet = Wallet(PublicKey(key.getAddress))

    // Build the blockhain
    val blockchain = new Blockchain(ByteArray.toHexString(wallet.address.key))

    val utxoSet = new UTXOSet(blockchain)

    Peer(key, wallet, blockchain, utxoSet, peerType)
  }
}
