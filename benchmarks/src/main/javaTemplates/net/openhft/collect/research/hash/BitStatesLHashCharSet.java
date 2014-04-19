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

package net.openhft.collect.research.hash;

import net.openhft.collect.impl.Primitives;
import net.openhft.collect.impl.UnsafeConstants;

import java.util.Arrays;


public class BitStatesLHashCharSet implements UnsafeConstants {

    public static final int WORD_INDEX_SHIFT = 6;

    public int capacityMask;
    public int size = 0;
    public long[] stateBits;
    public char[] set;

    public BitStatesLHashCharSet(int capacity) {
        if ((capacity & (capacity - 1)) != 0) {
            throw new IllegalArgumentException();
        }
        stateBits = new long[capacity >> WORD_INDEX_SHIFT];
        set = new char[capacity];
        capacityMask = capacity - 1;
    }

    public void clear() {
        if (size != 0) {
            size = 0;
            Arrays.fill(stateBits, 0L);
        }
    }

    public int indexBinaryStateSimpleIndexing(char key) {
        long[] stateBits = this.stateBits;
        char[] keys = set;
        int capacityMask = keys.length - 1;
        int index = Primitives.hashCode(key) & capacityMask;
        long curStatesWord = stateBits[index >> WORD_INDEX_SHIFT] << index;
        if (curStatesWord < 0L) {
            if (keys[index] == key)
                return index;
            curStatesWord <<= 1L;
            while (true) {
                index = (index + 1) & capacityMask;
                if ((index & 63) == 0)
                    curStatesWord = stateBits[index >> WORD_INDEX_SHIFT];
                if (curStatesWord < 0L) {
                    if (keys[index] == key)
                        return index;
                } else {
                    return -1;
                }
                curStatesWord <<= 1L;
            }
        } else {
            return -1;
        }
    }

    public int indexBinaryStateUnsafeIndexing(char key) {
        long[] stateBits = this.stateBits;
        char[] keys = set;
        int capacityMask = this.capacityMask;
        long index0 = (long) (Primitives.hashCode(key) & capacityMask);
        long curStatesWord = U.getLong(stateBits,
                LONG_BASE + (index0 >> (WORD_INDEX_SHIFT - LONG_SCALE_SHIFT))) << index0;
        if (curStatesWord < 0L) {
            long offset = index0 << CHAR_SCALE_SHIFT;
            if (U.getChar(keys, CHAR_BASE + offset) == key)
                return (int) index0;
            long capacityOffsetMask = ((long) capacityMask) << CHAR_SCALE_SHIFT;
            curStatesWord <<= 1L;
            while (true) {
                offset = offset + CHAR_SCALE & capacityOffsetMask;
                if ((offset & (63L << CHAR_SCALE_SHIFT)) == 0L) {
                    long wordOffset =
                            offset >> (WORD_INDEX_SHIFT + CHAR_SCALE_SHIFT - LONG_SCALE_SHIFT);
                    curStatesWord = U.getLong(stateBits, LONG_BASE + wordOffset);
                }
                if (curStatesWord < 0L) {
                    if (U.getChar(keys, CHAR_BASE + offset) == key)
                        return (int) (offset >> CHAR_SCALE_SHIFT);
                } else {
                    return -1;
                }
                curStatesWord <<= 1L;
            }
        } else {
            return -1;
        }
    }

    public boolean addBinaryState(char key) {
        long[] stateBits = this.stateBits;
        char[] keys = set;
        int capacityMask = this.capacityMask;
        int index = Primitives.hashCode(key) & capacityMask;
        long curBitMask = 1L << index;
        int statesWordIndex = index >> WORD_INDEX_SHIFT;
        long curStatesWord = stateBits[statesWordIndex];
        keyAbsent:
        if ((curStatesWord & curBitMask) != 0) {
            if (keys[index] == key)
                return false;
            while (true) {
                index = (index + 1) & capacityMask;
                curBitMask = curBitMask << 1;
                if (curBitMask == 0) {
                    curBitMask = 1L;
                    curStatesWord = stateBits[statesWordIndex = index >> WORD_INDEX_SHIFT];
                }
                if ((curStatesWord & curBitMask) == 0) {
                    break keyAbsent;
                } else if (keys[index] == key) {
                    return false;
                }
            }
        }
        stateBits[statesWordIndex] = curStatesWord | curBitMask;
        keys[index] = key;
        size++;
        return true;
    }
}
