package org.tron.network.message

import org.tron.core.Sha256Hash
import org.tron.protos.Tron.{Block, Inventory}


object InventoryMessage extends MessageReader {
  def fromBytes(data: Array[Byte]) = {
    InventoryMessage(Inventory.parseFrom(data))
  }

  def fromHashes(hashes: Seq[Sha256Hash]): Unit = {
    InventoryMessage(Inventory(ids = hashes.map(_.getByteString)))
  }
}

case class InventoryMessage(inventory: Inventory) extends Message {
  def hashes = inventory.ids.map(id => Sha256Hash.wrap(id.toByteArray)).toList

  val messageType = MessageTypes.INVENTORY
  def toBytes = inventory.toByteArray
}
