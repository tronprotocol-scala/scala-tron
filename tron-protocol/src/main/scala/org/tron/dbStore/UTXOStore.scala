package org.tron
package dbStore

import org.tron.storage.DataSource

class UTXOStore(db: DataSource[Array[Byte], Array[Byte]]) {

  def find(key: Array[Byte]) = db.get(key)

  def save(key: Array[Byte], data: Array[Byte]) = {
    awaitResult(db.put(key, data))
  }
}
