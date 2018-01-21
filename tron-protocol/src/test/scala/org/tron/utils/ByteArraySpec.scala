package org.tron.utils

import org.specs2.mutable._

class ByteArraySpec extends Specification {
  "ByteArray Spec" should {
    "toHexString" in {
      ByteArray.toHexString(Array(127)) mustEqual "7f"
      ByteArray.toHexString(Array(-128)) mustEqual "80"
      ByteArray.toHexString(null) mustEqual ""
    }
    "fromHexString" in {
      ByteArray.fromHexString("0x7f") mustEqual Array(127)
      ByteArray.fromHexString("7ff") mustEqual Array(7, -1)
      ByteArray.fromHexString("7f") mustEqual Array(127)
      ByteArray.fromHexString("80") mustEqual Array(-128)
      ByteArray.fromHexString("") mustEqual Array()
    }
    "toLong" in {
      ByteArray.toLong(null) mustEqual 0L
      ByteArray.toLong(Array()) mustEqual 0L
      ByteArray.toLong(Array(0, 0, 0, 0, 0, 0, 0, 127)) mustEqual 127L
    }
    "fromLong" in {
      ByteArray.fromLong(127L) mustEqual Array(0, 0, 0, 0, 0, 0, 0, 127)
    }
    "toString" in {
      ByteArray.toString(null) mustEqual ""
      ByteArray.toString(Array(84, 114, 111, 110)) mustEqual "Tron"
    }
    "fromString" in {
      ByteArray.fromString(null) mustEqual Array.empty[Byte]
      ByteArray.fromString("Tron") mustEqual Array(84, 114, 111, 110)
    }
  }
}
