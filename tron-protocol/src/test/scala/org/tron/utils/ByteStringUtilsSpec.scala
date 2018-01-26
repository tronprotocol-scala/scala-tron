package org.tron.utils

import com.google.protobuf.ByteString
import org.specs2.mutable._

class ByteStringUtilsSpec extends Specification {
  "ByteStringUtils Spec" should {
    "bytesToByteString" in {
      ByteStringUtils.bytesToByteString(Array()) mustEqual ByteString.EMPTY
    }
    "byteStringToBytes" in {
      ByteStringUtils.byteStringToBytes(ByteString.EMPTY) mustEqual Array()
    }
  }
}
