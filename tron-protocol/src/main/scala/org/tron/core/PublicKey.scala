package org.tron.core

import org.tron.crypto.ECKey
import org.tron.utils.ByteArray

case class PublicKey(key: Array[Byte]) {
  def address = ECKey.computeAddress(key)
  def hex = ByteArray.toHexString(address)
}
