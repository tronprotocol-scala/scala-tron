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
package org.tron.dbStore;


import org.spongycastle.util.encoders.Hex;
import org.tron.utils.FastByteComparisons;

import java.io.Serializable;
import java.util.Arrays;


public class ByteArrayWrapper implements Comparable<ByteArrayWrapper>, Serializable {

    private final byte[] data;
    private int hashCode = 0;

    public ByteArrayWrapper(byte[] data) {
        if (data == null)
            throw new NullPointerException("Data must not be null");
        this.data = data;
        this.hashCode = Arrays.hashCode(data);
    }

    public boolean equals(Object other) {
        if (!(other instanceof ByteArrayWrapper))
            return false;
        byte[] otherData = ((ByteArrayWrapper) other).getData();
        return FastByteComparisons.compareTo(
                data, 0, data.length,
                otherData, 0, otherData.length) == 0;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public int compareTo(ByteArrayWrapper o) {
        return FastByteComparisons.compareTo(
                data, 0, data.length,
                o.getData(), 0, o.getData().length);
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public String toString() {
        return Hex.toHexString(data);
    }
}
