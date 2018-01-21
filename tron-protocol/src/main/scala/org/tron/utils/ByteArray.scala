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

  def toHexString(byteArray: Array[Byte]): String = {
    if (byteArray == null) ""
    else Hex.toHexString(byteArray)
  }

  def fromHexString(string: String): Array[Byte] = {
    Option(string) match {
      case Some(x) if string.startsWith("0x") =>
        Hex.decode(x.substring(2))
      case Some(x) if string.length % 2 == 1 =>
        Hex.decode("0" + x)
      case Some(x) =>
        Hex.decode(x)
      case None =>
        EMPTY_BYTE_ARRAY
    }
  }

  def toLong(byteArray: Array[Byte]): Long = {
    if (byteArray == null || byteArray.length == 0) 0
    else new BigInteger(1, byteArray).longValue
  }

  def fromLong(value: Long): Array[Byte] = {
    ByteBuffer.allocate(8).putLong(value).array
  }

  def toString(byteArray: Array[Byte]): String = {
    if (byteArray == null) ""
    else new String(byteArray)
  }

  def fromString(string: String): Array[Byte] = {
    if (string == null) new Array[Byte](0)
    else string.getBytes
  }

  def reverseBytes(bytes: Array[Byte]): Array[Byte] = { // We could use the XOR trick here but it's easier to understand if we don't. If we find this is really a
    // performance issue the matter can be revisited.
    bytes.reverse
  }
}