package org.tron.core

import org.tron.protos.core.TronBlock.Block

class BlockchainIterator(blockchain: Blockchain) extends Iterator[Block] {

  var index = blockchain.currentHash

  def hasNext = Option(index).exists(_.length > 0)

  def next() = {
    val value = blockchain.blockDB.get(index).get
    val block = Block.parseFrom(value)
    index = block.blockHeader.get.parentHash.toByteArray
    block
  }
}
