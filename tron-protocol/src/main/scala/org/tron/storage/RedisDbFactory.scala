package org.tron.storage

import java.nio.file.Path

import akka.actor.ActorSystem
import org.tron.BlockChainDb

class RedisDbFactory(actorSystem: ActorSystem, databaseFolder: Path) extends DbFactory(databaseFolder) {

  override def build(name: String): BlockChainDb = {
    val db = new RedisDbDataSourceImpl(actorSystem, databaseFolder.toFile, name)
    db.initDB()
    db
  }
}
