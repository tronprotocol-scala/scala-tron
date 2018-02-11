package org.tron.network.message

import org.tron.protos.Tron.Inventory

object SyncBlockChainMessage extends MessageReader {
  def fromBytes(data: Array[Byte]) = {
    SyncBlockChainMessage(Inventory.parseFrom(data))
  }
}

case class SyncBlockChainMessage(override val inventory: Inventory) extends InventoryMessage(inventory) {
  override val messageType = MessageTypes.SYNC_BLOCK_CHAIN
}
