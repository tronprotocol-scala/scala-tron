package org.tron.storage

import java.io.File
import java.nio.file.Path

import org.tron.BlockChainDb

abstract class DbFactory(databaseFolder: Path) {

  def exists(name: String) = {
    val dbFile = new File(databaseFolder.toFile, name)
    dbFile.exists()
  }

  def build(name: String): BlockChainDb
}
