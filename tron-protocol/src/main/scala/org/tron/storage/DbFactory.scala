package org.tron.storage

import java.nio.file.Path

class DbFactory(databaseFolder: Path) {

  def build(name: String) = {
    val db = new LevelDbDataSourceImpl(databaseFolder.toFile, name)
    db.initDB()
    db
  }
}
