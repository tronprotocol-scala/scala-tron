package org.tron.core

import org.tron.protos.core.TronBlock.Block

class BlockchainIterator(blockchain: Blockchain) extends Iterator[Block] {

  var index = blockchain.currentHash

  def hasNext = Option(index).nonEmpty || index.length > 0

  override def next() = {
    if (hasNext) {
      val value = blockchain.blockDB.get(index).get
      val block: Block = Block.parseFrom(value)
      index = block.getBlockHeader.parentHash.toByteArray
      block
    } else {
      null // scalastyle:ignore
    }
  }
}
