package org.tron.network.message

import org.tron.core.Sha256Hash

trait Message {
  def hash: Sha256Hash = Sha256Hash.of(toBytes)

  def toBytes: Array[Byte]

  def messageType: MessageTypes
}
