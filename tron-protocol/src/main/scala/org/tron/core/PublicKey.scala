package org.tron.core

import org.tron.crypto.ECKey
import org.tron.utils.ByteArray

case class PublicKey(ecKey: ECKey) {
  def key = ecKey.getAddress
  def address = ECKey.computeAddress(key)
  def hex = ByteArray.toHexString(address)
}
