package org.tron.storage

import java.io.File

import akka.actor.ActorSystem
import akka.util.ByteString
import org.tron.utils.{ByteArrayUtils, ByteStringUtils}
import redis.RedisClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RedisDbDataSourceImpl(
  client: RedisClient,
  dbFolder: File,
  name: String = "default") extends DataSource[Array[Byte], Array[Byte]] {

  def initDB() = {}

  def get(key: Array[Byte]): Future[Option[Array[Byte]]] = {
    client.hget(name, ByteArrayUtils.toString(key)).map {
      case Some(result) =>
        Some(result.toArray)
      case None =>
        None
    }
  }

  def put(key: Array[Byte], value: Array[Byte]): Future[Unit] = {
    client.hset(name, ByteArrayUtils.toString(key), ByteArrayUtils.toString(key))
      .flatMap(_ => Future.unit)
  }

  def delete(key: Array[Byte]): Future[Unit] = {
    client.hdel(name, ByteArrayUtils.toString(key))
      .flatMap(_ => Future.unit)
  }

  def close(): Unit = {
    client.stop()
  }

  def allKeys = {
    client.hkeys(name)
      .map(keys => keys.map(ByteArrayUtils.fromHexString).toSet)
  }

  def resetDB(): Future[Unit] = {
    client.flushdb()
      .flatMap(_ => Future.unit)
  }
}
