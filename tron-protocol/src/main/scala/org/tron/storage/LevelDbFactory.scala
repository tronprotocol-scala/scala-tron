package org.tron.storage

import java.io.File
import java.nio.file.Path

import org.tron.BlockChainDb

class LevelDbFactory(databaseFolder: Path) extends DbFactory {
  def build(name: String): BlockChainDb = {
    val db = new LevelDbDataSourceImpl(databaseFolder.toFile, name)
    db.initDB()
    db
  }

  def exists(name: String) = {
    val dbFile = new File(databaseFolder.toFile, name)
    dbFile.exists()
  }
}
