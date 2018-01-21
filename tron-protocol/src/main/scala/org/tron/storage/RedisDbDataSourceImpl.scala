package org.tron.storage

import java.io.File

class RedisDbDataSourceImpl(dbFolder: File, name: String = "default") extends DataSource[Array[Byte], Array[Byte]] {
  override def put(key: Array[Byte], value: Array[Byte]): Unit = ???

  override def get(key: Array[Byte]): Option[Array[Byte]] = ???

  override def delete(key: Array[Byte]): Unit = ???

  override def close(): Unit = ???

  override def resetDB(): Unit = ???

  override def allKeys: Set[Array[Byte]] = ???
}
