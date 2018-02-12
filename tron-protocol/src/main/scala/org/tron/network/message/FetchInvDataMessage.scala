package org.tron.network.message

import org.tron.core.Sha256Hash
import org.tron.protos.Tron.Inventory
import org.tron.protos.Tron.Inventory.InventoryType

object FetchInvDataMessage extends MessageReader {

  def fromBytes(data: Array[Byte]) = {
    FetchInvDataMessage(Inventory.parseFrom(data))
  }

  def fromHashes(hashes: List[Sha256Hash]) = {
    FetchInvDataMessage(Inventory(ids = hashes.map(_.getByteString)))
  }

  def fromHashes(hashes: List[Sha256Hash], inventoryType: InventoryType) = {
    FetchInvDataMessage(
      Inventory(
        `type` = inventoryType,
        ids = hashes.map(_.getByteString)))
  }
}

case class FetchInvDataMessage(inventory: Inventory) extends Message with InventoryBaseMessage {
  val messageType = MessageTypes.FETCH_INV_DATA
  def fetchType = if (messageType == MessageTypes.BLOCK) MessageTypes.BLOCK else MessageTypes.TRX
}
