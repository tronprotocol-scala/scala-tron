package org.tron
package storage

import java.nio.file.Path

import akka.actor.ActorSystem
import redis.RedisClient

class RedisDbFactory(actorSystem: ActorSystem, databaseFolder: Path) extends DbFactory(databaseFolder) {

  override def build(name: String): DefaultDB = {
    implicit val system = actorSystem
    new RedisDbDataSourceImpl(RedisClient(), databaseFolder.toFile, name)
  }
}
