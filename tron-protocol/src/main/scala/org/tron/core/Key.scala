package org.tron.core

import org.tron.crypto.ECKey
import org.tron.utils.ByteArray

case class Key(ecKey: ECKey) {
  def address = ECKey.computeAddress(ecKey.getPubKey)
  def addressHex = ByteArray.toHexString(address)
  def privateKeyCompressed = Base58.encode(ecKey.getPrivKeyBytes)

  def info =
    s"""
      |Address: $addressHex
      |Private Key $privateKeyCompressed
    """.stripMargin
}
