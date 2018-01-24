package org.tron.core

import org.tron.protos.core.TronBlock.Block

import scala.concurrent.Await
import scala.concurrent.duration._

class BlockchainIterator(blockchain: Blockchain) extends Iterator[Block] {

  var index = blockchain.currentHash

  def hasNext = Option(index).exists(_.length > 0)

  override def next() = {
    if (hasNext) {
      val value = Await.result(blockchain.blockDB.get(index), 5 seconds).get
      val block = Block.parseFrom(value)
      index = block.blockHeader.get.parentHash.toByteArray
      block
    } else {
      null // scalastyle:ignore
    }
  }
}
