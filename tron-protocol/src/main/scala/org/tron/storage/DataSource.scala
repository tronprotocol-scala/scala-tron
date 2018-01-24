package org.tron.storage

import scala.concurrent.Future

trait DataSource[K, V] {

  def initDB(): Unit
  def resetDB(): Future[Unit]

  def allKeys: Future[Set[Array[Byte]]]

  def put(key: K, value: V): Future[Unit]

  def get(key: K): Future[Option[V]]

  def delete(key: K): Future[Unit]

  def close(): Unit

}
