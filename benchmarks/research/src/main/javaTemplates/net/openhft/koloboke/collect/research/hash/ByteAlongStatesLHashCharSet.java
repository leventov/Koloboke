/* with char|byte|short|int|long key */
/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.openhft.koloboke.collect.research.hash;

import net.openhft.koloboke.collect.impl.Primitives;
import net.openhft.koloboke.collect.impl.UnsafeConstants;

import java.util.Arrays;


public class ByteAlongStatesLHashCharSet implements UnsafeConstants {
    public static final int FREE = 0, FULL = 1;
    public static final long ENTRY_SCALE = BYTE_SCALE + CHAR_SCALE;

    public int capacityMask;
    public int size = 0;
    public byte[] table;

    public ByteAlongStatesLHashCharSet(int capacity) {
        if ((capacity & (capacity - 1)) != 0) {
            throw new IllegalArgumentException();
        }
        table = new byte[capacity * (int) ENTRY_SCALE];
        capacityMask = capacity - 1;
    }

    public void clear() {
        if (size != 0) {
            size = 0;
            Arrays.fill(table, (byte) FREE);
        }
    }

    public int indexBinaryStateUnsafeIndexing(char key) {
        byte[] table = this.table;
        int capacityMask = this.capacityMask;
        int index = Primitives.hashCode(key) & capacityMask;
        long stateOffset = ((long) index) * ENTRY_SCALE;
        if ((int) U.getByte(table, BYTE_BASE + stateOffset) > 0) {
            if (U.getChar(table, BYTE_BASE + stateOffset + BYTE_SCALE) == key)
                return index;
            long capacityOffset = (long) table.length;
            while (true) {
                index = (index - 1) & capacityMask;
                if ((stateOffset -= ENTRY_SCALE) < 0L) stateOffset += capacityOffset;
                if ((int) U.getByte(table, BYTE_BASE + stateOffset) > 0) {
                    if (U.getChar(table, BYTE_BASE + stateOffset + BYTE_SCALE) == key)
                        return index;
                } else {
                    return -1;
                }
            }
        } else {
            return -1;
        }
    }

    public boolean addBinaryStateUnsafeIndexing(char key) {
        byte[] table = this.table;
        int capacityMask = this.capacityMask;
        int index = Primitives.hashCode(key) & capacityMask;
        long stateOffset = ((long) index) * ENTRY_SCALE;
        keyAbsent:
        if ((int) U.getByte(table, BYTE_BASE + stateOffset) > 0) {
            if (U.getChar(table, BYTE_BASE + stateOffset + BYTE_SCALE) == key)
                return false;
            long capacityOffset = (long) table.length;
            while (true) {
                if ((stateOffset -= ENTRY_SCALE) < 0L) stateOffset += capacityOffset;
                if ((int) U.getByte(table, BYTE_BASE + stateOffset) == FREE) {
                    break keyAbsent;
                } else if (U.getChar(table, BYTE_BASE + stateOffset + BYTE_SCALE) == key) {
                    return false;
                }
            }
        }
        U.putByte(table, BYTE_BASE + stateOffset, (byte) FULL);
        U.putChar(table, BYTE_BASE + stateOffset + BYTE_SCALE, key);
        size++;
        return true;
    }
}
