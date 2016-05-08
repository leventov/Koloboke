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

package com.koloboke.collect.research.hash;

import com.koloboke.collect.impl.Primitives;
import com.koloboke.collect.impl.UnsafeConstants;
import com.koloboke.function.CharConsumer;

import java.util.Arrays;


public class ByteAlongStatesDHashCharSet implements UnsafeConstants {
    public static final int FREE = 0, REMOVED = -1, FULL = 1;
    public static final long ENTRY_SCALE = BYTE_SCALE + CHAR_SCALE;

    public int capacity;
    public int size = 0;
    public int freeSlots;
    public int removedSlots = 0;
    public byte[] table;

    public ByteAlongStatesDHashCharSet(int capacity) {
        this.capacity = capacity;
        table = new byte[capacity * (int) ENTRY_SCALE];
        freeSlots = capacity;
    }

    public void clear() {
        if (size != 0) {
            size = 0;
            freeSlots = capacity;
            removedSlots = 0;
            Arrays.fill(table, (byte) FREE);
        }
    }

    public int indexTernaryStateUnsafeIndexing(char key) {
        byte[] table = this.table;
        int capacity = this.capacity;
        int hash = Primitives.hashCode(key) & Integer.MAX_VALUE;
        int index = hash % capacity;
        long stateOffset = ((long) index) * ENTRY_SCALE;
        int state = (int) U.getByte(table, BYTE_BASE + stateOffset);
        if (state > 0) {
            if (U.getChar(table, BYTE_BASE + stateOffset + BYTE_SCALE) == key)
                return index;
        } else if (state == FREE) {
            return -1;
        }
        int step = ((hash % (capacity - 2)) + 1);
        long stepOffset = ((long) step) * ENTRY_SCALE;
        long capacityOffset = (long) table.length;
        while (true) {
            stateOffset -= stepOffset;
            if ((index -= step) < 0) {
                stateOffset += capacityOffset;
                index += capacity;
            }
            if ((state = (int) U.getByte(table, BYTE_BASE + stateOffset)) > 0) {
                if (U.getChar(table, BYTE_BASE + stateOffset + BYTE_SCALE) == key)
                    return index;
            } else if (state == FREE) {
                return -1;
            }
        }
    }

    public int indexBinaryStateUnsafeIndexing(char key) {
        byte[] table = this.table;
        int capacity = this.capacity;
        int hash = Primitives.hashCode(key) & Integer.MAX_VALUE;
        int index = hash % capacity;
        long stateOffset = ((long) index) * ENTRY_SCALE;
        if ((int) U.getByte(table, BYTE_BASE + stateOffset) > 0) {
            if (U.getChar(table, BYTE_BASE + stateOffset + BYTE_SCALE) == key)
                return index;
            int step = ((hash % (capacity - 2)) + 1);
            long stepOffset = ((long) step) * ENTRY_SCALE;
            long capacityOffset = (long) table.length;
            while (true) {
                stateOffset -= stepOffset;
                if ((index -= step) < 0) {
                    stateOffset += capacityOffset;
                    index += capacity;
                }
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

    public boolean addTernaryStateUnsafeIndexing(char key) {
        byte[] table = this.table;
        int capacity = this.capacity;
        int hash = Primitives.hashCode(key) & Integer.MAX_VALUE;
        int index0 = hash % capacity;
        long stateOffset = ((long) index0) * ENTRY_SCALE;
        long firstRemovedOffset;
        int state = (int) U.getByte(table, BYTE_BASE + stateOffset);
        keyAbsentFreeSlot:
        if (state != FREE) {
            if (state > 0) {
                if (U.getChar(table, BYTE_BASE + stateOffset + BYTE_SCALE) == key) {
                    return false;
                } else {
                    firstRemovedOffset = -1L;
                }
            } else {
                firstRemovedOffset = stateOffset;
            }
            long stepOffset = ((long) ((hash % (capacity - 2)) + 1)) * ENTRY_SCALE;
            long capacityOffset = (long) table.length;
            while (true) {
                if ((stateOffset -= stepOffset) < 0L) stateOffset += capacityOffset;
                if ((state = (int) U.getByte(table, BYTE_BASE + stateOffset)) == FREE) {
                    if (firstRemovedOffset < 0L) {
                        break keyAbsentFreeSlot;
                    } else {
                        // key is absent, removed slot
                        U.putByte(table, BYTE_BASE + firstRemovedOffset, (byte) FULL);
                        U.putChar(table, BYTE_BASE + firstRemovedOffset + BYTE_SCALE, key);
                        size++;
                        removedSlots--;
                        return true;
                    }
                } else if (state > 0) {
                    if (U.getChar(table, BYTE_BASE + stateOffset + BYTE_SCALE) == key)
                        return false;
                } else if (firstRemovedOffset < 0L) {
                    firstRemovedOffset = stepOffset;
                }
            }
        }
        U.putByte(table, BYTE_BASE + stateOffset, (byte) FULL);
        U.putChar(table, BYTE_BASE + stateOffset + BYTE_SCALE, key);
        size++;
        freeSlots--;
        return true;
    }

    public boolean addBinaryStateUnsafeIndexing(char key) {
        byte[] table = this.table;
        int capacity = this.capacity;
        int hash = Primitives.hashCode(key) & Integer.MAX_VALUE;
        int index = hash % capacity;
        long stateOffset = ((long) index) * ENTRY_SCALE;
        keyAbsent:
        if ((int) U.getByte(table, BYTE_BASE + stateOffset) != FREE) {
            if (U.getChar(table, BYTE_BASE + stateOffset + BYTE_SCALE) == key)
                return false;
            long stepOffset = ((long) ((hash % (capacity - 2)) + 1)) * ENTRY_SCALE;
            long capacityOffset = (long) table.length;
            while (true) {
                if ((stateOffset -= stepOffset) < 0L) stateOffset += capacityOffset;
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
        freeSlots--;
        return true;
    }

    public void forEachBinaryState(CharConsumer action) {
        byte[] table = this.table;
        for (long off = ((long) capacity) * ENTRY_SCALE; (off -= ENTRY_SCALE) >= 0L;) {
            if (U.getByte(table, BYTE_BASE + off) > 0)
                action.accept(U.getChar(table, BYTE_BASE + off + BYTE_SCALE));
        }
    }
}
