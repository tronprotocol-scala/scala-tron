package org.tron
package blockchain

import org.tron.protos.Tron.Block

class BlockchainIterator(blockchain: Blockchain) extends Iterator[Block] {

  var index = blockchain.currentHash

  def hasNext = Option(index).exists(_.length > 0)

  def next() = {
    val value = awaitResult(blockchain.blockDB.get(index)).get
    val block = Block.parseFrom(value)
    index = block.blockHeader.get.parentHash.toByteArray
    block
  }
}
