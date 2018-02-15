package org.tron.network.message

import org.tron.core.Sha256Hash
import org.tron.protos.Tron.Inventory


trait InventoryBaseMessage extends Message {
  val inventory: Inventory
  def hashes = inventory.ids.map(id => Sha256Hash.wrap(id.toByteArray)).toList
  def toBytes = inventory.toByteArray
}
