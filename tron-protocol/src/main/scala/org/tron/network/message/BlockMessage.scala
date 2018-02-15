package org.tron.network.message

import org.tron.protos.Tron.Block

object BlockMessage extends MessageReader {
  def fromBytes(data: Array[Byte]) = {
    BlockMessage(Block.parseFrom(data))
  }
}

case class BlockMessage(block: Block) extends Message {
  val messageType = MessageTypes.BLOCK
  def toBytes = block.toByteArray
}
