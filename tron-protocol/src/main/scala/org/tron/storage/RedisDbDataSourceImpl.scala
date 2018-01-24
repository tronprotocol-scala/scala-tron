package org.tron.storage

import java.io.File

import akka.util.ByteString
import org.tron.utils.{ByteArrayUtils, ByteStringUtils}
import redis.RedisClient

import scala.concurrent.Future

class RedisDbDataSourceImpl(dbFolder: File, name: String = "default") extends DataSource[Array[Byte], ByteString] {

  var database: Option[RedisClient] = None

  override def initDB(): Unit = {
    implicit val akkaSystem = akka.actor.ActorSystem()
    database = Some(RedisClient())
  }

  override def get(key: Array[Byte]): Future[Option[ByteString]] = {
    database.get.hget(name,ByteArrayUtils.toString(key))
  }

  override def put(key: Array[Byte], value: ByteString): Future[Boolean] = {
    database.get.hset(name,ByteArrayUtils.toString(key),ByteArrayUtils.toString(key))
  }

  override def delete(key: Array[Byte]): Future[Long] = {
    database.get.hdel(name,ByteArrayUtils.toString(key))
  }

  override def close(): Unit = {
    database.get.stop()
  }

  override def allKeys: Future[Seq[String]] = {
    database.get.hkeys(name)
  }

  override def resetDB(): Future[Boolean] = {
    database.get.flushdb()
  }
}
