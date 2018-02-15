package org.tron
package storage

import java.io.File
import java.nio.file.Path

abstract class DbFactory(databaseFolder: Path) {

  def exists(name: String) = {
    val dbFile = new File(databaseFolder.toFile, name)
    dbFile.exists()
  }

  def build(name: String): DefaultDB
}
