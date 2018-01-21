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
import org.tron.protos.core.TronTXInput.TXInput;
import org.tron.utils.ByteArrayUtils;

public class TXInputUtils {

    /**
     * new transaction input
     *
     * @param txID      byte[] txID
     * @param vout      int vout
     * @param signature byte[] signature
     * @param pubKey    byte[] pubKey
     * @return {@link TXInput}
     */
    public static TXInput newTXInput(
            byte[] txID,
            long vout,
            byte[] signature,
            byte[] pubKey) {
        return new TXInput(
                ByteString.copyFrom(txID),
                vout,
                ByteString.copyFrom(signature),
                ByteString.copyFrom(pubKey));
    }

    /**
     * getData print string of the transaction input
     *
     * @param txi {@link TXInput} txi
     * @return String format string of the transaction input
     */
    public static String toPrintString(TXInput txi) {
        if (txi == null) {
            return "";
        }

        return "\nTXInput {\n" +
                "\ttxID=" + ByteArrayUtils.toHexString(txi.txID().toByteArray()) +
                ",\n\tvout=" + txi.vout() +
                ",\n\tsignature=" + ByteArrayUtils.toHexString(txi.signature()
                .toByteArray()) +
                ",\n\tpubKey=" + ByteArrayUtils.toString(txi.pubKey().toByteArray
                ()) +
                "\n}\n";
    }
}
