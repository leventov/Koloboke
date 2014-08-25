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
import net.openhft.koloboke.function.CharConsumer;

import java.util.Arrays;


public class ZeroMaskingStatesLHashCharSet implements UnsafeConstants {

    public int size = 0;
    public char zeroMask = CharOps.randomExcept((char) 0);
    public char[] set;

    public ZeroMaskingStatesLHashCharSet(int capacity) {
        if ((capacity & (capacity - 1)) != 0) {
            throw new IllegalArgumentException();
        }
        set = new char[capacity];
    }

    public void clear() {
        if (size != 0) {
            size = 0;
            zeroMask = CharOps.randomExcept((char) 0);
            Arrays.fill(set, (char) 0);
        }
    }

    public int indexBinaryStateSimpleIndexing(char key) {
        if (key != 0 && key != zeroMask) {
            char[] keys = set;
            int capacityMask = keys.length - 1;
            int index = Primitives.hashCode(key) & capacityMask;
            char cur = keys[index];
            if (cur == key) {
                return index;
            } else {
                if (cur == 0) {
                    return -1;
                } else {
                    while (true) {
                        index = (index + 1) & capacityMask;
                        if ((cur = keys[index]) == key) {
                            return index;
                        } else if (cur == 0) {
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
        if (key != 0 && key != zeroMask) {
            char[] keys = set;
            long capacityMask = (long) (keys.length - 1);
            long index = ((long) Primitives.hashCode(key)) & capacityMask;
            long offset = index << CHAR_SCALE_SHIFT;
            long CHAR_BASE = UnsafeConstants.CHAR_BASE;
            char cur = U.getChar(keys, CHAR_BASE + offset);
            if (cur == key) {
                return (int) index;
            } else {
                if (cur == 0) {
                    return -1;
                } else {
                    long capacityOffsetMask = capacityMask << CHAR_SCALE_SHIFT;
                    while (true) {
                        offset = (offset + CHAR_SCALE) & capacityOffsetMask;
                        if ((cur = U.getChar(keys, CHAR_BASE + offset)) == key) {
                            return (int) (offset >> CHAR_SCALE_SHIFT);
                        } else if (cur == 0) {
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
        if (key == 0 || key == zeroMask) {
            return false;
        }
        char[] keys = set;
        int capacityMask = keys.length - 1;
        int index = Primitives.hashCode(key) & capacityMask;
        char cur = keys[index];
        keyAbsent:
        if (cur != 0) {
            if (cur == key) {
                return false;
            } else {
                while (true) {
                    index = (index + 1) & capacityMask;
                    if ((cur = keys[index]) == 0) {
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
        if (key == 0 || key == zeroMask) {
            return false;
        }
        char[] keys = set;
        long capacityMask = (long) (keys.length - 1);
        long index = ((long) Primitives.hashCode(key)) & capacityMask;
        long offset = index << CHAR_SCALE_SHIFT;
        char cur = U.getChar(keys, CHAR_BASE + offset);
        keyAbsent:
        if (cur != 0) {
            if (cur == key) {
                return false;
            } else {
                long capacityOffsetMask = capacityMask << CHAR_SCALE_SHIFT;
                while (true) {
                    offset = (offset + CHAR_SCALE) & capacityOffsetMask;
                    if ((cur = U.getChar(keys, CHAR_BASE + offset)) == 0) {
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
        if (key == 0 || key == zeroMask) {
            return false;
        }
        char[] keys = set;
        int capacity = set.length;
        int capacityMask = capacity - 1;
        int index = Primitives.hashCode(key) & capacityMask;
        char cur = keys[index];
        keyPresent:
        if (cur != key) {
            if (cur == 0)
                return false;
            while (true) {
                index = (index + 1) & capacityMask;
                if ((cur = keys[index]) == key) {
                    break keyPresent;
                } else if (cur == 0) {
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
            if (keyToShift == 0) {
                keys[indexToRemove] = (char) 0;
                return true;
            }
            int keyDistance = (indexToShift - Primitives.hashCode(keyToShift)) & capacityMask;
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
        if (key == 0 || key == zeroMask) {
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
            if (cur == 0)
                return false;
            while (true) {
                offset = (offset + CHAR_SCALE) & capacityOffsetMask;
                if ((cur = U.getChar(keys, CHAR_BASE + offset)) == key) {
                    break keyPresent;
                } else if (cur == 0) {
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
            if (keyToShift == 0)
                break;
            int indexToShift = (int) (offsetToShift >> CHAR_SCALE_SHIFT);
            int keyDistance = (indexToShift - Primitives.hashCode(keyToShift)) & capacityMask;
            if (keyDistance >= shiftDistance) {
                U.putChar(keys, CHAR_BASE + offsetToRemove, keyToShift);
                offsetToRemove = offsetToShift;
                shiftDistance = 1;
            } else {
                shiftDistance++;
            }
        }
        U.putChar(keys, CHAR_BASE + offsetToRemove, (char) 0);
        return true;
    }

    public void forEachBinaryState(CharConsumer action) {
        char zeroMask = this.zeroMask;
        char[] keys = set;
        int i = keys.length - 1;
        for (; i >= 0; i--) {
            char k;
            if ((k = keys[i]) != 0) {
                action.accept(k != zeroMask ? k : (char) 0);
                if (k == zeroMask)
                    break;
            } else {
                break;
            }
        }
        i--;
        for (; i >= 0; i--) {
            char k;
            if ((k = keys[i]) != 0)
                action.accept(k);
        }
    }
}
