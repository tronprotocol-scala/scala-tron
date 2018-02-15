package org.tron.core

import com.google.protobuf.ByteString
import org.tron.utils.ByteArrayUtils

object Address {
  def apply(address: String): Address = Address(ByteArrayUtils.fromHexString(address))
  def apply(address: ByteString): Address = Address(address.toByteArray)
}

case class Address(value: Array[Byte]) extends AnyVal {
  def hex = ByteArrayUtils.toHexString(value)
}
