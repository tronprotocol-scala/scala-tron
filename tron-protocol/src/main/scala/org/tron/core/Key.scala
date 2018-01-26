package org.tron.core

import org.tron.crypto.ECKey
import org.tron.utils.ByteArrayUtils

case class Key(ecKey: ECKey) {
  def address = Address(ECKey.computeAddress(ecKey.getPubKey))
  def privateKeyCompressed = Base58.encode(ecKey.getPrivKeyBytes)

  def info =
    s"""Address: ${address.hex}
      |Private Key $privateKeyCompressed
    """.stripMargin.trim
}
