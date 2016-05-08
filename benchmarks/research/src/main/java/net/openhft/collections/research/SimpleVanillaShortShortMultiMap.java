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

package net.openhft.collections.research;

import com.koloboke.function.IntConsumer;
import net.openhft.lang.Maths;
import net.openhft.lang.io.Bytes;
import net.openhft.lang.io.DirectStore;

/**
 * Supports a simple interface for int -> int[] off heap.
 */
class SimpleVanillaShortShortMultiMap {
    static final long ENTRY_SIZE = 4L;
    static final int ENTRY_SIZE_SHIFT = 2;

    /**
     * Separate method because it is too easy to forget to cast to long
     * before shifting.
     */
    static long indexToPos(int index) {
        return ((long) index) << ENTRY_SIZE_SHIFT;
    }

    static long indexToPos(long index) {
        return index << ENTRY_SIZE_SHIFT;
    }

    static final int UNSET_KEY = 0;
    static final int HASH_INSTEAD_OF_UNSET_KEY = 0xFFFF;
    static final int UNSET_VALUE = Integer.MIN_VALUE;

    static final int UNSET_ENTRY = 0xFFFF;

    final long capacity;
    final long capacityMask;
    final long capacityMask2;
    final Bytes bytes;

    public SimpleVanillaShortShortMultiMap(long minCapacity) {
        if (minCapacity < 0L || minCapacity > (1L << 16))
            throw new IllegalArgumentException();
        capacity = Maths.nextPower2(minCapacity, 16L);
        capacityMask = capacity - 1L;
        capacityMask2 = capacityMask * ENTRY_SIZE;
        bytes = new DirectStore(null, capacity * ENTRY_SIZE, false).createSlice();
        clear();
    }

    public SimpleVanillaShortShortMultiMap(Bytes bytes) {
        capacity = bytes.capacity() / ENTRY_SIZE;
        assert capacity == Maths.nextPower2(capacity, 16L);
        capacityMask = capacity - 1L;
        capacityMask2 = capacityMask * ENTRY_SIZE;
        this.bytes = bytes;
    }

    public void put(int key, int value) {
        if (key == UNSET_KEY)
            key = HASH_INSTEAD_OF_UNSET_KEY;
        else if ((key & ~0xFFFF) != 0)
            throw new IllegalArgumentException("Key out of range, was " + key);
        if ((value & ~0xFFFF) != 0)
            throw new IllegalArgumentException("Value out of range, was " + value);
        long pos = indexToPos(key & capacityMask);
        while (true) {
            int entry = bytes.readInt(pos);
            int hash2 = entry >>> 16;
            if (hash2 == UNSET_KEY) {
                bytes.writeInt(pos, ((key << 16) | value));
                return;
            }
            if (hash2 == key) {
                int value2 = entry & 0xFFFF;
                if (value2 == value)
                    return;
            }
            pos = (pos + ENTRY_SIZE) & capacityMask2;
        }
    }

    public boolean remove(int key, int value) {
        if (key == UNSET_KEY)
            key = HASH_INSTEAD_OF_UNSET_KEY;
        long pos = indexToPos(key & capacityMask);
        long removedPos = -1L;
        for (long i = 0L; i <= capacityMask; i++) {
            int entry = bytes.readInt(pos);
//            int hash2 = bytes.readInt(pos + KEY);
            int hash2 = entry >>> 16;
            if (hash2 == key) {
//                int value2 = bytes.readInt(pos + VALUE);
                int value2 = entry & 0xFFFF;
                if (value2 == value) {
                    removedPos = pos;
                    break;
                }
            } else if (hash2 == UNSET_KEY) {
                break;
            }
            pos = (pos + ENTRY_SIZE) & capacityMask2;
        }
        if (removedPos < 0L)
            return false;
        long posToShift = removedPos;
        for (long i = 0L; i <= capacityMask; i++) {
            posToShift = (posToShift + ENTRY_SIZE) & capacityMask2;
            int entryToShift = bytes.readInt(posToShift);
            int hash = entryToShift >>> 16;
            if (hash == UNSET_KEY)
                break;
            long insertPos = indexToPos(hash & capacityMask);
            // see comment in VanillaIntIntMultiMap
            boolean cond1 = insertPos <= removedPos;
            boolean cond2 = removedPos <= posToShift;
            if ((cond1 && cond2) ||
                    // chain wrapped around capacity
                    (posToShift < insertPos && (cond1 || cond2))) {
                bytes.writeInt(removedPos, entryToShift);
                removedPos = posToShift;
            }
        }
        bytes.writeInt(removedPos, UNSET_ENTRY);
        return true;
    }

    /////////////////////
    // Stateful methods

    int searchHash = -1;
    long searchPos = -1L;

    public int startSearch(int key) {
        if (key == UNSET_KEY)
            key = HASH_INSTEAD_OF_UNSET_KEY;

        searchPos = indexToPos(key & capacityMask);
        return searchHash = key;
    }

    public int nextPos() {
        for (long i = 0L; i < capacity; i++) {
            int entry = bytes.readInt(searchPos);
            int hash2 = entry >>> 16;
            if (hash2 == UNSET_KEY) {
                return UNSET_VALUE;
            }
            searchPos = (searchPos + ENTRY_SIZE) & capacityMask2;
            if (hash2 == searchHash) {
                return entry & 0xFFFF;
            }
        }
        return UNSET_VALUE;
    }

    public void forEachValue(int key, IntConsumer action) {
        if (key == UNSET_KEY)
            key = HASH_INSTEAD_OF_UNSET_KEY;
        else if ((key & ~0xFFFF) != 0)
            throw new IllegalArgumentException("Key out of range, was " + key);
        long pos = indexToPos(key & capacityMask);
        while (true) {
            int entry = bytes.readInt(pos);
            int hash2 = entry >>> 16;
            if (hash2 == UNSET_KEY) {
                return;
            }
            if (hash2 == key) {
                int value2 = entry & 0xFFFF;
                action.accept(value2);
            }
            pos = (pos + ENTRY_SIZE) & capacityMask2;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{ ");
        for (long i = 0L, pos = 0L; i < capacity; i++, pos += ENTRY_SIZE) {
            int entry = bytes.readInt(pos);
            int key = entry >>> 16;
            int value = entry & 0xFFFF;
            if (key != UNSET_KEY)
                sb.append(key).append('=').append(value).append(", ");
        }
        if (sb.length() > 2) {
            sb.setLength(sb.length() - 2);
            return sb.append(" }").toString();
        }
        return "{ }";
    }

    public void clear() {
        for (long pos = 0L; pos < bytes.capacity(); pos += ENTRY_SIZE) {
            bytes.writeInt(pos, UNSET_ENTRY);
        }
    }
}

