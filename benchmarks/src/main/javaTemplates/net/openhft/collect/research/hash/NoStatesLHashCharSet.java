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


public class NoStatesLHashCharSet implements UnsafeConstants {

    public int capacityMask;
    public int size = 0;
    public char freeValue = Character.MIN_VALUE;
    public char[] set;

    public NoStatesLHashCharSet(int capacity) {
        if ((capacity & (capacity - 1)) != 0) {
            throw new IllegalArgumentException();
        }
        set = new char[capacity];
        Arrays.fill(set, freeValue);
        capacityMask = capacity - 1;
    }

    public void clear() {
        if (size != 0) {
            size = 0;
            Arrays.fill(set, freeValue);
        }
    }

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
                        index = (index + 1) & capacityMask;
                        if ((cur = keys[index]) == key) {
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

    public int indexBinaryStateUnsafeIndexing(char key) {
        char free = freeValue;
        if (key != free) {
            char[] keys = set;
            long capacityMask = (long) this.capacityMask;
            long index = ((long) Primitives.hashCode(key)) & capacityMask;
            long offset = index << CHAR_SCALE_SHIFT;
            long CHAR_BASE = UnsafeConstants.CHAR_BASE;
            char cur = U.getChar(keys, CHAR_BASE + offset);
            if (cur == key) {
                return (int) index;
            } else {
                if (cur == free) {
                    return -1;
                } else {
                    long capacityOffsetMask = capacityMask << CHAR_SCALE_SHIFT;
                    while (true) {
                        offset = (offset + CHAR_SCALE) & capacityOffsetMask;
                        if ((cur = U.getChar(keys, CHAR_BASE + offset)) == key) {
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

    public boolean addBinaryStateSimpleIndexing(char key) {
        char free = freeValue;
        if (key == free) {
            return false;
        }
        char[] keys = set;
        int capacityMask = this.capacityMask;
        int index = Primitives.hashCode(key) & capacityMask;
        char cur = keys[index];
        keyAbsent:
        if (cur != free) {
            if (cur == key) {
                return false;
            } else {
                while (true) {
                    index = (index + 1) & capacityMask;
                    if ((cur = keys[index]) == free) {
                        break keyAbsent;
                    } else if (cur == key) {
                        return false;
                    }
                }
            }
        }
        // key is absent
        keys[index] = key;
        size++;
        return true;
    }

    public boolean addBinaryStateUnsafeIndexing(char key) {
        char free = freeValue;
        if (key == free) {
            return false;
        }
        char[] keys = set;
        long capacityMask = (long) this.capacityMask;
        long index = ((long) Primitives.hashCode(key)) & capacityMask;
        long offset = index << CHAR_SCALE_SHIFT;
        char cur = U.getChar(keys, CHAR_BASE + offset);
        keyAbsent:
        if (cur != free) {
            if (cur == key) {
                return false;
            } else {
                long capacityOffsetMask = capacityMask << CHAR_SCALE_SHIFT;
                while (true) {
                    offset = (offset + CHAR_SCALE) & capacityOffsetMask;
                    if ((cur = U.getChar(keys, CHAR_BASE + offset)) == free) {
                        break keyAbsent;
                    } else if (cur == key) {
                        return false;
                    }
                }
            }
        }
        // key is absent
        U.putChar(keys, CHAR_BASE + offset, key);
        size++;
        return true;
    }

    public boolean removeSimpleIndexing(char key) {
        char free = freeValue;
        if (key == free) {
            return false;
        }
        char[] keys = set;
        int capacity = set.length;
        int capacityMask = capacity - 1;
        int index = Primitives.hashCode(key) & capacityMask;
        char cur = keys[index];
        keyPresent:
        if (cur != key) {
            if (cur == free)
                return false;
            while (true) {
                index = (index + 1) & capacityMask;
                if ((cur = keys[index]) == key) {
                    break keyPresent;
                } else if (cur == free) {
                    return false;
                }
            }
        }
        // key is present
        int indexToRemove = index;
        int indexToShift = indexToRemove;
        int shiftDistance = 1;
        while (true) {
            indexToShift = (indexToShift + 1) & capacityMask;
            char keyToShift = keys[indexToShift];
            if (keyToShift == free) {
                keys[indexToRemove] = free;
                return true;
            }
            int keyDistance = (indexToShift + capacity - Primitives.hashCode(keyToShift)) &
                    capacityMask;
            if (keyDistance >= shiftDistance) {
                keys[indexToRemove] = keyToShift;
                indexToRemove = indexToShift;
                shiftDistance = 1;
            } else {
                shiftDistance++;
            }
        }
    }

    public boolean removeUnsafeIndexing(char key) {
        char free = freeValue;
        if (key == free) {
            return false;
        }
        char[] keys = set;
        int capacity = set.length;
        int capacityMask = capacity - 1;
        long capacityOffsetMask = ((long) capacityMask) << CHAR_SCALE_SHIFT;
        long index = (long) (Primitives.hashCode(key) & capacityMask);
        long offset = index << CHAR_SCALE_SHIFT;
        char cur = U.getChar(keys, CHAR_BASE + offset);
        keyPresent:
        if (cur != key) {
            if (cur == free)
                return false;
            while (true) {
                offset = (offset + CHAR_SCALE) & capacityOffsetMask;
                if ((cur = U.getChar(keys, CHAR_BASE + offset)) == key) {
                    break keyPresent;
                } else if (cur == free) {
                    return false;
                }
            }
        }
        // key is present
        long offsetToRemove = offset;
        long offsetToShift = offset;
        int shiftDistance = 1;
        while (true) {
            offsetToShift = (offsetToShift + CHAR_SCALE) & capacityOffsetMask;
            char keyToShift =  U.getChar(keys, CHAR_BASE + offsetToShift);
            if (keyToShift == free)
                break;
            int indexToShift = (int) (offsetToShift >> CHAR_SCALE_SHIFT);
            int keyDistance = (indexToShift + capacity - Primitives.hashCode(keyToShift)) &
                    capacityMask;
            if (keyDistance >= shiftDistance) {
                U.putChar(keys, CHAR_BASE + offsetToRemove, keyToShift);
                offsetToRemove = offsetToShift;
                shiftDistance = 1;
            } else {
                shiftDistance++;
            }
        }
        U.putChar(keys, CHAR_BASE + offsetToRemove, free);
        return true;
    }
}
