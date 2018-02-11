package org.tron.network.message

import org.tron.core.Sha256Hash
import org.tron.protos.Tron.Inventory

object BlockInventoryMessage extends MessageReader {
  def fromBytes(data: Array[Byte]) = {
    BlockInventoryMessage(Inventory.parseFrom(data))
  }
  def fromHashes(hashes: List[Sha256Hash]) = {
    BlockInventoryMessage(Inventory(ids = hashes.map(_.getByteString)))
  }
}

case class BlockInventoryMessage(override val inventory: Inventory) extends InventoryMessage(inventory) {
  override val messageType = MessageTypes.BLOCK_INVENTORY
}
