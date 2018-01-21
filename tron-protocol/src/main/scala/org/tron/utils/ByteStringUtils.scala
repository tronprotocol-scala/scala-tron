package org.tron.utils

import com.google.protobuf.ByteString

object ByteStringUtils {
  implicit def bytesToByteString(bytes: Array[Byte]) =
    ByteString.copyFrom(bytes)

  implicit def byteStringToBytes(byteString: ByteString) =
    byteString.toByteArray
}
