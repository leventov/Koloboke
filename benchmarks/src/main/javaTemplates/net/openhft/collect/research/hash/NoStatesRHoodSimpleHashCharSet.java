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


public class NoStatesRHoodSimpleHashCharSet extends NoStatesRHoodHashCharSet {

    public NoStatesRHoodSimpleHashCharSet(int capacity) {
        super(capacity);
    }

    public int indexBinaryStateSimpleIndexing(char key) {
        char free = freeValue;
        if (key != free) {
            char[] keys = set;
            int capacity = keys.length;
            int capacityMask = capacity - 1;
            int index = Primitives.hashCode(key) & capacityMask;
            char cur = keys[index];
            if (cur == key) {
                return index;
            } else {
                if (cur == free) {
                return -1;
                } else {
                    int distance = 1;
                    while (true) {
                        index = (index + 1) & capacityMask;
                        if ((cur = keys[index]) == key) {
                            return index;
                        } else if (cur == free || distance >
                                ((index + capacity - Primitives.hashCode(cur)) & capacityMask)) {
                            return -1;
                        }
                        distance++;
                    }
                }
            }
        } else {
            return -1;
        }
    }

    public int indexBinaryStateUnsafeIndexing(char key) {
        char free = freeValue;
        if (key != free) {
            char[] keys = set;
            long capacityMask = (long) (keys.length - 1);
            long index = Primitives.hashCode(key) & capacityMask;
            char cur = U.getChar(keys, CHAR_BASE + (index << CHAR_SCALE_SHIFT));
            if (cur == key) {
                return (int) index;
            } else {
                if (cur == free) {
                    return -1;
                } else {
                    long distance = 1L;
                    long capacity = capacityMask + 1L;
                    while (true) {
                        index = (index + 1L) & capacityMask;
                        cur = U.getChar(keys, CHAR_BASE + (index << CHAR_SCALE_SHIFT));
                        if (cur == key) {
                            return (int) index;
                        } else if (cur == free || distance >
                                ((index + capacity - Primitives.hashCode(cur)) & capacityMask)) {
                            return -1;
                        }
                        distance++;
                    }
                }
            }
        } else {
            return -1;
        }
    }

    public boolean removeSimpleIndexing(char key) {
        char free = freeValue;
        if (key != free) {
            char[] keys = set;
            int capacity = keys.length;
            int capacityMask = capacity - 1;
            int index = Primitives.hashCode(key) & capacityMask;
            char cur = keys[index];
            keyPresent:
            if (cur != key) {
                if (cur == free) {
                    return false;
                } else {
                    int distance = 1;
                    while (true) {
                        index = (index + 1) & capacityMask;
                        if ((cur = keys[index]) == key) {
                            break keyPresent;
                        } else if (cur == free || distance >
                                ((index + capacity - Primitives.hashCode(cur)) & capacityMask)) {
                            return false;
                        }
                        distance++;
                    }
                }
            }
            int prev = index;
            while (true) {
                index = (index + 1) & capacityMask;
                if ((cur = keys[index]) == free ||
                        index == (Primitives.hashCode(cur) & capacityMask)) {
                    break;
                }
                keys[prev] = cur;
                prev = index;
            }
            keys[prev] = free;
            size--;
            return true;
        } else {
            return false;
        }
    }

    public boolean removeUnsafeIndexing(char key) {
        char free = freeValue;
        if (key != free) {
            char[] keys = set;
            int capacity = keys.length;
            int capacityMask = capacity - 1;
            long capacityMaskAsLong = (long) capacityMask;
            long index = ((long) Primitives.hashCode(key)) & capacityMaskAsLong;
            char cur = U.getChar(keys, CHAR_BASE + (index << CHAR_SCALE_SHIFT));
            keyPresent:
            if (cur != key) {
                if (cur == free) {
                    return false;
                } else {
                    int distance = 1;
                    while (true) {
                        index = (index + 1L) & capacityMaskAsLong;
                        if ((cur = U.getChar(keys,
                                CHAR_BASE + (index << CHAR_SCALE_SHIFT))) == key) {
                            break keyPresent;
                        } else if (cur == free || distance >
                                ((((int) index) + capacity - Primitives.hashCode(cur)) &
                                        capacityMask)) {
                            return false;
                        }
                        distance++;
                    }
                }
            }
            long prevIndex = index;
            while (true) {
                index = (index + 1L) & capacityMaskAsLong;
                if ((cur = U.getChar(keys, CHAR_BASE + (index << CHAR_SCALE_SHIFT))) == free ||
                        index == (Primitives.hashCode(cur) & capacityMask)) {
                    break;
                }
                U.putChar(keys, CHAR_BASE + (prevIndex << CHAR_SCALE_SHIFT), cur);
                prevIndex = index;
            }
            U.putChar(keys, CHAR_BASE + (prevIndex << CHAR_SCALE_SHIFT), free);
            size--;
            return true;
        } else {
            return false;
        }
    }
}
