package org.tron.storage

import akka.actor.ActorSystem
import org.tron.BlockChainDb
import redis.RedisClient

class RedisDbFactory(actorSystem: ActorSystem) extends DbFactory {

  def build(name: String): BlockChainDb = {
    implicit val system = actorSystem
    val db = new RedisDbDataSourceImpl(RedisClient(), name)
    db.initDB()
    db
  }

  def exists(name: String) = {
    true
  }
}
