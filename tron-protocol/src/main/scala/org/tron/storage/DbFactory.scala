package org.tron.storage

import java.io.File
import java.nio.file.Path

import org.tron.core.{BlockUtils, Constant, TransactionUtils}

class DbFactory(databaseFolder: Path) {

  def exists(name: String) = {
    val dbFile = new File(databaseFolder.toFile, name)
    dbFile.exists()
  }

  def build(name: String) = {
    val db = new LevelDbDataSourceImpl(databaseFolder.toFile, name)
    db.initDB()
    db
  }
}
