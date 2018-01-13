package org.tron.storage

trait DataSource[K, V] {

  def put(key: K, value: V): Unit

  def get(key: K): Option[V]

  def delete(key: K): Unit

  def close(): Unit
}
