package org.tron.storage

import java.nio.file.Path

import org.tron.BlockChainDb

class RedisDbFactory (databaseFolder: Path) extends DbFactory(databaseFolder){
  override def build(name: String): BlockChainDb = {
    new RedisDbDataSourceImpl(databaseFolder.toFile, name)
  }
}
