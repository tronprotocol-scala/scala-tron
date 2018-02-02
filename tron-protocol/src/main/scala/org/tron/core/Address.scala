package org.tron.core

import org.tron.utils.ByteArrayUtils

object Address {
  def apply(address: String): Address = Address(ByteArrayUtils.fromHexString(address))
  val EMPTY = Address("")
}

case class Address(value: Array[Byte]) extends AnyVal {
  def hex = ByteArrayUtils.toHexString(value)
}
