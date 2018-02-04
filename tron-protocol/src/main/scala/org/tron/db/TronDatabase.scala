package org.tron
package db

trait TronDatabase {

  val dbSource: DefaultDB

  def close(): Unit = {
    dbSource.close()
  }

  def addItem(key: Array[Byte], value: Array[Byte]) = {
    dbSource.put(key, value)
  }
}