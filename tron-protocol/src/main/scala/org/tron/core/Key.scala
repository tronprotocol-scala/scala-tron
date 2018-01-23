package org.tron.core

import org.tron.crypto.ECKey
import org.tron.utils.ByteArrayUtils

case class Key(ecKey: ECKey) {
  def address = ECKey.computeAddress(ecKey.getPubKey)
  def addressHex = ByteArrayUtils.toHexString(address)
  def privateKeyCompressed = Base58.encode(ecKey.getPrivKeyBytes)

  def info =
    s"""Address: $addressHex
      |Private Key $privateKeyCompressed
    """.stripMargin.trim
}
