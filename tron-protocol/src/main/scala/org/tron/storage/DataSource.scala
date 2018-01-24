package org.tron.storage

import akka.util.ByteString

import scala.concurrent.Future

trait DataSource[K, V] {

  def initDB(): Unit
  def resetDB(): Future[Boolean]

  def allKeys: Future[Seq[String]]

  def put(key: K, value: V): Future[Boolean]

  def get(key: K): Future[Option[V]]

  def delete(key: K): Future[Long]

  def close(): Unit

}
