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


public class NoStatesLSelfAdjHashCharSet extends NoStatesLHashCharSet {

    public NoStatesLSelfAdjHashCharSet(int capacity) {
        super(capacity);
    }

    @Override
    public int indexBinaryStateSimpleIndexing(char key) {
        char free = freeValue;
        if (key != free) {
            char[] keys = set;
            int capacityMask = this.capacityMask;
            int index = Primitives.hashCode(key) & capacityMask;
            char cur = keys[index];
            if (cur == key) {
                return index;
            } else {
                if (cur == free) {
                    return -1;
                } else {
                    while (true) {
                        char prev = cur;
                        int prevIndex = index;
                        index = (index + 1) & capacityMask;
                        if ((cur = keys[index]) == key) {
                            keys[index] = prev;
                            keys[prevIndex] = key;
                            return index;
                        } else if (cur == free) {
                            return -1;
                        }
                    }
                }
            }
        } else {
            return -1;
        }
    }

    @Override
    public int indexBinaryStateUnsafeIndexing(char key) {
        char free = freeValue;
        if (key != free) {
            char[] keys = set;
            int capacityMask = this.capacityMask;
            long index = (long) (Primitives.hashCode(key) & capacityMask);
            long offset = index << CHAR_SCALE_SHIFT;
            char cur = U.getChar(keys, CHAR_BASE + offset);
            if (cur == key) {
                return (int) index;
            } else {
                if (cur == free) {
                    return -1;
                } else {
                    long capacityOffsetMask = ((long) capacityMask) << CHAR_SCALE_SHIFT;
                    while (true) {
                        char prev = cur;
                        long prevOffset = offset;
                        offset = (offset + CHAR_SCALE) & capacityOffsetMask;
                        if ((cur = U.getChar(keys, CHAR_BASE + offset)) == key) {
                            U.putChar(keys, CHAR_BASE + offset, prev);
                            U.putChar(keys, CHAR_BASE + prevOffset, key);
                            return (int) (offset >> CHAR_SCALE_SHIFT);
                        } else if (cur == free) {
                            return -1;
                        }
                    }
                }
            }
        } else {
            return -1;
        }
    }
}
