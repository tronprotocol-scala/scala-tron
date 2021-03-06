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
package org.tron.utxo

import com.google.protobuf.ByteString
import org.tron.protos.Tron.TXInput

object TXInputUtils {

  /**
    * new transaction input
    *
    * @param txID      byte[] txID
    * @param vout      int vout
    * @param signature byte[] signature
    * @param pubKey    byte[] pubKey
    * @return { @link TXInput}
    */
    def newTXInput(txID: Array[Byte], vout: Long, signature: Array[Byte], pubKey: Array[Byte]) = {
      TXInput(
        ByteString.copyFrom(txID),
        vout,
        ByteString.copyFrom(signature),
        ByteString.copyFrom(pubKey))
    }
}