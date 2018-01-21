package org.tron.utils

import com.google.protobuf.ByteString

object ByteStringUtils {
  implicit class ByteStringImplicits(byteString: ByteString) {
    def hex = ByteArrayUtils.toHexString(byteString.toByteArray)
  }

  implicit def bytesToByteString(bytes: Array[Byte]) =
    ByteString.copyFrom(bytes)

  implicit def byteStringToBytes(byteString: ByteString) =
    byteString.toByteArray
}
