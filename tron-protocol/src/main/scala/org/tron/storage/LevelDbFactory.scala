package org.tron.storage

import java.nio.file.Path

import org.tron.BlockChainDb

class LevelDbFactory(databaseFolder: Path) extends DbFactory(databaseFolder) {
  override def build(name: String): BlockChainDb = {
    val db = new LevelDbDataSourceImpl(databaseFolder.toFile, name)
    db.initDB()
    db
  }
}
