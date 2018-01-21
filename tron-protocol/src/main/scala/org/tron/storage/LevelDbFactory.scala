package org.tron.storage

import java.nio.file.Path

import org.tron.BlockChainDb

class LevelDbFactory(databaseFolder: Path) extends DbFactory(databaseFolder) {
  override def build(name: String): DataSource[Array[Byte], Array[Byte]] = {
    val db = new LevelDbDataSourceImpl(databaseFolder.toFile, name)
    db.initDB()
    db
  }
}
