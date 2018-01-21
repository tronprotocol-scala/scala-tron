/*
 * java-tron is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * java-tron is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *//*
 * java-tron is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * java-tron is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.tron.utils

import java.math.BigInteger
import java.nio.ByteBuffer

import org.spongycastle.util.encoders.Hex

object ByteArray {
  val EMPTY_BYTE_ARRAY = new Array[Byte](0)

  def toHexString(data: Array[Byte]): String = {
    if (data == null) ""
    else Hex.toHexString(data)
  }

  def fromHexString(data: String): Array[Byte] = {
    Option(data) match {
      case Some(x) if data.startsWith("0x") =>
        Hex.decode(x.substring(2))
      case Some(x) if data.length % 2 == 1 =>
        Hex.decode("0" + x)
      case Some(x) =>
        Hex.decode(x)
      case None =>
        EMPTY_BYTE_ARRAY
    }
  }

  def toLong(b: Array[Byte]): Long = {
    if (b == null || b.length == 0) 0
    else new BigInteger(1, b).longValue
  }

  def fromString(str: String): Array[Byte] = {
    if (str == null) null
    else str.getBytes
  }

  def toStr(byteArray: Array[Byte]): String = {
    if (byteArray == null) null
    else new String(byteArray)
  }

  def fromLong(value: Long): Array[Byte] = {
    ByteBuffer.allocate(8).putLong(value).array
  }

  def reverseBytes(bytes: Array[Byte]): Array[Byte] = { // We could use the XOR trick here but it's easier to understand if we don't. If we find this is really a
    // performance issue the matter can be revisited.
    bytes.reverse
  }
}