package org.tron.utils

import org.specs2.mutable._

class ByteArrayUtilsSpec extends Specification {
  "ByteArrayUtils Spec" should {
    "toHexString" in {
      ByteArrayUtils.toHexString(Array(127)) mustEqual "7f"
      ByteArrayUtils.toHexString(Array(-128)) mustEqual "80"
      ByteArrayUtils.toHexString(null) mustEqual ""
    }
    "fromHexString" in {
      ByteArrayUtils.fromHexString("0x7f") mustEqual Array(127)
      ByteArrayUtils.fromHexString("7ff") mustEqual Array(7, -1)
      ByteArrayUtils.fromHexString("7f") mustEqual Array(127)
      ByteArrayUtils.fromHexString("80") mustEqual Array(-128)
      ByteArrayUtils.fromHexString("") mustEqual Array()
    }
    "toLong" in {
      ByteArrayUtils.toLong(null) mustEqual 0L
      ByteArrayUtils.toLong(Array()) mustEqual 0L
      ByteArrayUtils.toLong(Array(0, 0, 0, 0, 0, 0, 0, 127)) mustEqual 127L
    }
    "fromLong" in {
      ByteArrayUtils.fromLong(127L) mustEqual Array(0, 0, 0, 0, 0, 0, 0, 127)
    }
    "toString" in {
      ByteArrayUtils.toString(null) mustEqual ""
      ByteArrayUtils.toString(Array(84, 114, 111, 110)) mustEqual "Tron"
    }
    "fromString" in {
      ByteArrayUtils.fromString(null) mustEqual Array.empty[Byte]
      ByteArrayUtils.fromString("Tron") mustEqual Array(84, 114, 111, 110)
    }
    "reverseBytes" in {
      ByteArrayUtils.reverseBytes(Array(1,2,3)) mustEqual Array(3,2,1)
    }
  }
}
