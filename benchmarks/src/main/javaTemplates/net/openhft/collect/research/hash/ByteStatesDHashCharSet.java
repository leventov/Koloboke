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


public class ByteStatesDHashCharSet implements UnsafeConstants {
    public static final int FREE = 0, REMOVED = -1, FULL = 1;

    public int size = 0;
    public int freeSlots;
    public int removedSlots = 0;
    public byte[] states;
    public char[] set;

    public ByteStatesDHashCharSet(int capacity) {
        states = new byte[capacity];
        set = new char[capacity];
        freeSlots = capacity;
    }

    public void clear() {
        if (size != 0) {
            size = 0;
            freeSlots = states.length;
            removedSlots = 0;
            Arrays.fill(states, (byte) FREE);
        }
    }

    public int indexTernaryStateSimpleIndexing(char key) {
        byte[] states = this.states;
        char[] keys = set;
        int capacity = keys.length;
        int hash = Primitives.hashCode(key) & Integer.MAX_VALUE;
        int index = hash % capacity;
        int state = states[index];
        if (state > 0) {
            if (keys[index] == key)
                return index;
        } else if (state == FREE) {
            return -1;
        }
        int step = (hash % (capacity - 2)) + 1;
        while (true) {
            if ((index -= step) < 0) index += capacity; // nextIndex
            if ((state = states[index]) > 0) {
                if (keys[index] == key)
                    return index;
            } else if (state == FREE) {
                return -1;
            }
        }
    }

    public int indexTernaryStateUnsafeIndexing(char key) {
        byte[] states = this.states;
        char[] keys = set;
        int capacity = keys.length;
        int hash = Primitives.hashCode(key) & Integer.MAX_VALUE;
        long index = (long) (hash % capacity);
        int state = (int) U.getByte(states, BYTE_BASE + index);
        if (state > 0) {
            if (U.getChar(keys, CHAR_BASE + (index << CHAR_SCALE_SHIFT)) == key)
                return (int) index;
        } else if (state == FREE) {
            return -1;
        }
        long step = (long) ((hash % (capacity - 2)) + 1);
        while (true) {
            if ((index -= step) < 0L) index += capacity; // nextIndex
            if ((state = (int) U.getByte(states, BYTE_BASE + index)) > 0) {
                if (U.getChar(keys, CHAR_BASE + (index << CHAR_SCALE_SHIFT)) == key)
                    return (int) index;
            } else if (state == FREE) {
                return -1;
            }
        }
    }

    public int indexBinaryStateSimpleIndexing(char key) {
        byte[] states = this.states;
        char[] keys = set;
        int capacity = keys.length;
        int hash = Primitives.hashCode(key) & Integer.MAX_VALUE;
        int index = hash % capacity;
        if (states[index] > 0) {
            if (keys[index] == key)
                return index;
            int step = (hash % (capacity - 2)) + 1;
            while (true) {
                if ((index -= step) < 0) index += capacity; // nextIndex
                if (states[index] > 0) {
                    if (keys[index] == key)
                        return index;
                } else {
                    return -1;
                }
            }
        } else {
            return -1;
        }
    }

    public int indexBinaryStateUnsafeIndexing(char key) {
        byte[] states = this.states;
        char[] keys = set;
        int capacity = keys.length;
        int hash = Primitives.hashCode(key) & Integer.MAX_VALUE;
        long index = (long) (hash % capacity);
        if ((int) U.getByte(states, BYTE_BASE + index) > 0) {
            if (U.getChar(keys, CHAR_BASE + (index << CHAR_SCALE_SHIFT)) == key)
                return (int) index;
            long step = (long) ((hash % (capacity - 2)) + 1);
            while (true) {
                if ((index -= step) < 0L) index += capacity; // nextIndex
                if ((int) U.getByte(states, BYTE_BASE + index) > 0) {
                    if (U.getChar(keys, CHAR_BASE + (index << CHAR_SCALE_SHIFT)) == key)
                        return (int) index;
                } else {
                    return -1;
                }
            }
        } else {
            return -1;
        }
    }

    public boolean addTernaryState(char key) {
        byte[] states = this.states;
        char[] keys = set;
        int capacity = keys.length;
        int hash = Primitives.hashCode(key) & Integer.MAX_VALUE;
        int index = hash % capacity;
        int firstRemoved;
        int state = states[index];
        keyAbsentFreeSlot:
        if (state != FREE) {
            if (state > 0) {
                if (keys[index] == key) {
                    return false;
                } else {
                    firstRemoved = -1;
                }
            } else {
                firstRemoved = index;
            }
            int step = (hash % (capacity - 2)) + 1;
            while (true) {
                if ((index -= step) < 0) index += capacity; // nextIndex
                if ((state = states[index]) == FREE) {
                    if (firstRemoved < 0) {
                        break keyAbsentFreeSlot;
                    } else {
                        // key is absent, removed slot
                        states[firstRemoved] = FULL;
                        keys[firstRemoved] = key;
                        size++;
                        removedSlots--;
                        return true;
                    }
                } else if (state > 0) {
                    if (keys[index] == key)
                        return false;
                } else if (firstRemoved < 0) {
                    firstRemoved = index;
                }
            }
        }
        states[index] = FULL;
        keys[index] = key;
        size++;
        freeSlots--;
        return true;
    }

    public boolean addBinaryState(char key) {
        byte[] states = this.states;
        char[] keys = set;
        int capacity = keys.length;
        int hash = Primitives.hashCode(key) & Integer.MAX_VALUE;
        int index = hash % capacity;
        keyAbsent:
        if (states[index] != FREE) {
            if (keys[index] == key)
                return false;
            int step = (hash % (capacity - 2)) + 1;
            while (true) {
                if ((index -= step) < 0) index += capacity; // nextIndex
                if (states[index] == FREE) {
                    break keyAbsent;
                } else if (keys[index] == key) {
                    return false;
                }
            }
        }
        states[index] = FULL;
        keys[index] = key;
        size++;
        freeSlots--;
        return true;
    }
}
