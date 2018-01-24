package org.tron.storage

import java.io.File

import akka.actor.ActorSystem
import akka.util.ByteString
import org.tron.utils.{ByteArrayUtils, ByteStringUtils}
import redis.RedisClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RedisDbDataSourceImpl(
  system: ActorSystem,
  dbFolder: File,
  name: String = "default") extends DataSource[Array[Byte], Array[Byte]] {

  var database: Option[RedisClient] = None

  def initDB(): Unit = {
    implicit val actorSystem = system
    database = Some(RedisClient())
  }

  def get(key: Array[Byte]): Future[Option[Array[Byte]]] = {
    database.get.hget(name, ByteArrayUtils.toString(key)).map {
      case Some(result) =>
        Some(result.toArray)
      case None =>
        None
    }
  }

  def put(key: Array[Byte], value: Array[Byte]): Future[Unit] = {
    database.get.hset(name, ByteArrayUtils.toString(key), ByteArrayUtils.toString(key))
      .flatMap(_ => Future.unit)
  }

  def delete(key: Array[Byte]): Future[Unit] = {
    database.get.hdel(name, ByteArrayUtils.toString(key))
      .flatMap(_ => Future.unit)
  }

  def close(): Unit = {
    database.get.stop()
  }

  def allKeys = {
    database.get.hkeys(name)
      .map(keys => keys.map(ByteArrayUtils.fromHexString).toSet)
  }

  def resetDB(): Future[Unit] = {
    database.get.flushdb()
      .flatMap(_ => Future.unit)
  }
}
