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
 */
package org.tron.core;

import com.google.protobuf.ByteString;
import org.tron.protos.core.TronTXOutput.TXOutput;
import org.tron.utils.ByteArrayUtils;

public class TXOutputUtils {

    /**
     * new transaction output
     *
     * @param value   int value
     * @param address String address
     * @return {@link TXOutput}
     */
    public static TXOutput newTXOutput(long value, String address) {
        return new TXOutput(
            value,
            ByteString.copyFrom(ByteArrayUtils.fromHexString(address)));
    }

    /**
     * getData print string of the transaction out
     *
     * @param txo {@link TXOutput} txo
     * @return String format string of the transaction output
     */
    public static String toPrintString(TXOutput txo) {
        if (txo == null) {
            return "";
        }

        return "\nTXOutput {\n" +
                "\tvalue=" + txo.value() +
                ",\n\tpubKeyHash=" + ByteArrayUtils.toHexString(txo.pubKeyHash().toByteArray()) +
                "\n}\n";
    }
}
