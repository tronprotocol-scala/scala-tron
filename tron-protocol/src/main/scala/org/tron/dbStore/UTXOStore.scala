package org.tron.dbStore

import org.tron.storage.DataSource

class UTXOStore(db: DataSource[Array[Byte], Array[Byte]]) {

  def find(key: Array[Byte]) = db.get(key)

  def saveUTXO(key: Array[Byte], data: Array[Byte]) = {
    db.put(key, data)
  }

}
