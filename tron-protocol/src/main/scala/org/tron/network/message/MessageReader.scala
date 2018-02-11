package org.tron.network.message

trait MessageReader {
  def fromBytes(data: Array[Byte]): Message
}
