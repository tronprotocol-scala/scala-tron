package org.tron.core

import org.tron.utils.ByteArrayUtils

object Address {
  def fromString(address: String) = {
    Address(ByteArrayUtils.fromHexString(address))
  }
}

case class Address(value: Array[Byte]) extends AnyVal {
  def hex = ByteArrayUtils.toHexString(value)
}
