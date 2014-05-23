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

import net.openhft.function.IntConsumer;
import net.openhft.lang.io.Bytes;


public class Tiered2VanillaShortShortMultiMap extends SimpleVanillaShortShortMultiMap {

    private final long halfCapacity;
    private final long halfCapacityMask;
    private final long halfCapacityMask2;

    public Tiered2VanillaShortShortMultiMap(long minCapacity) {
        super(minCapacity);
        halfCapacity = capacity / 2L;
        halfCapacityMask = halfCapacity - 1L;
        halfCapacityMask2 = halfCapacityMask * ENTRY_SIZE;
    }

    public Tiered2VanillaShortShortMultiMap(Bytes bytes) {
        super(bytes);
        halfCapacity = capacity / 2L;
        halfCapacityMask = halfCapacity - 1L;
        halfCapacityMask2 = halfCapacityMask * ENTRY_SIZE;
    }

    @Override
    public void put(int key, int value) {
        if (key == UNSET_KEY)
            key = HASH_INSTEAD_OF_UNSET_KEY;
        else if ((key & ~0xFFFF) != 0)
            throw new IllegalArgumentException("Key out of range, was " + key);
        if ((value & ~0xFFFF) != 0)
            throw new IllegalArgumentException("Value out of range, was " + value);
        long pos = indexToPos(key & halfCapacityMask);

        // probe 1
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
        pos = (pos + ENTRY_SIZE) & halfCapacityMask2;

        // probe 2
        entry = bytes.readInt(pos);
        hash2 = entry >>> 16;
        if (hash2 == UNSET_KEY) {
            bytes.writeInt(pos, ((key << 16) | value));
            return;
        }
        if (hash2 == key) {
            int value2 = entry & 0xFFFF;
            if (value2 == value)
                return;
        }
        pos = halfCapacity * ENTRY_SIZE + ((pos + ENTRY_SIZE) & halfCapacityMask2);

        while (true) {
            entry = bytes.readInt(pos);
            hash2 = entry >>> 16;
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

    private int probe;

    @Override
    public int startSearch(int key) {
        if (key == UNSET_KEY)
            key = HASH_INSTEAD_OF_UNSET_KEY;

        probe = 0;
        searchPos = indexToPos(key & halfCapacityMask);
        return searchHash = key;
    }

    public int firstPos() {
        probe = 1;
        int entry = bytes.readInt(searchPos);
        int hash2 = entry >>> 16;
        if (hash2 == UNSET_KEY) {
            return UNSET_VALUE;
        }
        searchPos = (searchPos + ENTRY_SIZE) & halfCapacityMask2;
        if (hash2 == searchHash) {
            return entry & 0xFFFF;
        }
        return secondPos();
    }

    public int secondPos() {
        probe = -1;
        int entry = bytes.readInt(searchPos);
        int hash2 = entry >>> 16;
        if (hash2 == UNSET_KEY) {
            return UNSET_VALUE;
        }
        searchPos = halfCapacity * ENTRY_SIZE + ((searchPos + ENTRY_SIZE) & halfCapacityMask2);
        if (hash2 == searchHash) {
            return entry & 0xFFFF;
        }
        return ordinaryNextPos();
    }

    public int ordinaryNextPos() {
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

    @Override
    public int nextPos() {
        if (probe == 0) {
            return firstPos();
        } else if (probe > 0) {
            return secondPos();
        } else {
            return ordinaryNextPos();
        }
    }

    @Override
    public void forEachValue(int key, IntConsumer action) {
        if (key == UNSET_KEY)
            key = HASH_INSTEAD_OF_UNSET_KEY;
        else if ((key & ~0xFFFF) != 0)
            throw new IllegalArgumentException("Key out of range, was " + key);
        long pos = indexToPos(key & halfCapacityMask);

        // probe 1
        int entry = bytes.readInt(pos);
        int hash2 = entry >>> 16;
        if (hash2 == UNSET_KEY) {
            return;
        }
        if (hash2 == key) {
            int value2 = entry & 0xFFFF;
            action.accept(value2);
        }
        pos = (pos + ENTRY_SIZE) & halfCapacityMask2;

        // probe 2
        entry = bytes.readInt(pos);
        hash2 = entry >>> 16;
        if (hash2 == UNSET_KEY) {
            return;
        }
        if (hash2 == key) {
            int value2 = entry & 0xFFFF;
            action.accept(value2);
        }
        pos = halfCapacity * ENTRY_SIZE + ((pos + ENTRY_SIZE) & halfCapacityMask2);

        while (true) {
            entry = bytes.readInt(pos);
            hash2 = entry >>> 16;
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
}
