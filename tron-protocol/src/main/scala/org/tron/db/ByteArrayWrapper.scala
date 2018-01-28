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
package org.tron.db

import org.spongycastle.util.encoders.Hex
import org.tron.utils.FastByteComparisons
import java.io.Serializable
import java.util

case class ByteArrayWrapper(data: Array[Byte]) extends Comparable[ByteArrayWrapper] with Serializable {

  require(data != null, "Data must not be null")

  override def hashCode = {
    util.Arrays.hashCode(data)
  }

  override def equals(other: Any) = {
    other match {
      case o: ByteArrayWrapper =>
        compareTo(o) == 0
      case _ =>
        false
    }
  }

  def compareTo(o: ByteArrayWrapper): Int = {
    FastByteComparisons.compareTo(data, 0, data.length, o.getData, 0, o.getData.length)
  }

  def getData: Array[Byte] = data

  override def toString = {
    Hex.toHexString(data)
  }
}