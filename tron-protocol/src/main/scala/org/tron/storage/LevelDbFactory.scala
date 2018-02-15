package org.tron
package storage

import java.nio.file.Path

class LevelDbFactory(databaseFolder: Path) extends DbFactory(databaseFolder) {
  override def build(name: String): DefaultDB = {
    new LevelDbDataSourceImpl(databaseFolder.toFile, name)
  }
}
