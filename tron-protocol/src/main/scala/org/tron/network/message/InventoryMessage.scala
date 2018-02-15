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

case class InventoryMessage(inventory: Inventory) extends InventoryBaseMessage {
  val messageType = MessageTypes.INVENTORY
}

trait InventoryBaseMessage extends Message {
  val inventory: Inventory
  def hashes = inventory.ids.map(id => Sha256Hash.wrap(id.toByteArray)).toList
  def toBytes = inventory.toByteArray
}
