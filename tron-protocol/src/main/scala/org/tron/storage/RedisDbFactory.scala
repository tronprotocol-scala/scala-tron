package org.tron.storage

import java.nio.file.Path

import akka.actor.ActorSystem
import org.tron.BlockChainDb
import redis.RedisClient

class RedisDbFactory(actorSystem: ActorSystem, databaseFolder: Path) extends DbFactory(databaseFolder) {

  override def build(name: String): BlockChainDb = {
    implicit val system = actorSystem
    val db = new RedisDbDataSourceImpl(RedisClient(), databaseFolder.toFile, name)
    db.initDB()
    db
  }
}
