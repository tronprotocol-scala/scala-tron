package org.tron.network.message

import org.tron.core.Sha256Hash
import org.tron.protos.Tron.Inventory

object TransactionInventoryMessage extends MessageReader {
  def fromBytes(data: Array[Byte]) = {
    TransactionInventoryMessage(Inventory.parseFrom(data))
  }
  def fromHashes(hashes: List[Sha256Hash]) = {
    TransactionInventoryMessage(Inventory(ids = hashes.map(_.getByteString)))
  }
}

case class TransactionInventoryMessage(inventory: Inventory) extends Message with InventoryBaseMessage {
  val messageType = MessageTypes.TRX_INVENTORY
}
