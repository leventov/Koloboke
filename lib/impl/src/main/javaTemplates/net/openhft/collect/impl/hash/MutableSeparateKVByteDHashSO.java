/* with
 DHash|QHash|LHash hash
 byte|char|short|int|long elem
 Mutable|Updatable|Immutable mutability
 Separate|Parallel kv
*/
/* if (Separate kv) || (Enabled parallelKV) */
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

package net.openhft.collect.impl.hash;

import net.openhft.collect.hash.HashOverflowException;
import net.openhft.collect.impl.*;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;


public abstract class MutableSeparateKVByteDHashSO extends MutableDHash
        implements SeparateKVByteDHash, PrimitiveConstants, UnsafeConstants {

    byte freeValue;
    /* if Mutable mutability && !(LHash hash) */
    byte removedValue;
    /* endif */

    /* if Separate kv */
    byte[] set;
    /* elif Parallel kv */
    char[] table;
    /* endif */

    void copy(SeparateKVByteDHash hash) {
        super.copy(hash);
        freeValue = hash.freeValue();
        /* if Mutable mutability && !(LHash hash) */
        if (hash.supportRemoved())
            removedValue = hash.removedValue();
        /* endif */

        /* if Separate kv */
        set = hash.keys().clone();
        /* elif Parallel kv */
        table = hash.table().clone();
        /* endif */

        /* if Mutable mutability && !(LHash hash) */
        if (!hash.supportRemoved()) {
            removedValue = freeValue;
            removedValue = findNewFreeOrRemoved();
        }
        /* endif */
    }

    void move(SeparateKVByteDHash hash) {
        super.copy(hash);
        freeValue = hash.freeValue();
        /* if Mutable mutability && !(LHash hash) */
        if (hash.supportRemoved())
            removedValue = hash.removedValue();
        /* endif */

        /* if Separate kv */
        set = hash.keys();
        /* elif Parallel kv */
        table = hash.table();
        /* endif */

        /* if Mutable mutability && !(LHash hash) */
        if (!hash.supportRemoved()) {
            removedValue = freeValue;
            removedValue = findNewFreeOrRemoved();
        }
        /* endif */
    }

    final void init(HashConfigWrapper configWrapper, int size, byte freeValue
            /* if Mutable mutability && !(LHash hash) */, byte removedValue/* endif */) {
        this.freeValue = freeValue;
        /* if Mutable mutability && !(LHash hash) */
        this.removedValue = removedValue;
        /* endif */
        // calls allocateArrays, fill keys with this.freeValue => assign it before
        super.init(configWrapper, size);
    }


    @Override
    public byte freeValue() {
        return freeValue;
    }

    @Override
    public boolean supportRemoved() {
        return /* if Mutable mutability && !(LHash hash) */true
                /* elif !(Mutable mutability) || LHash hash //false// endif */;
    }

    @Override
    public byte removedValue() {
        /* if Mutable mutability && !(LHash hash) */
        return removedValue;
        /* elif !(Mutable mutability) || LHash hash //
        throw new UnsupportedOperationException();
        // endif */
    }

    public boolean contains(Object key) {
        return contains(((Byte) key).byteValue());
    }

    public boolean contains(byte key) {
        return index(key) >= 0;
    }

    int index(byte key) {
        /* template Index */ throw new NotGenerated(); /* endtemplate */
    }

    /* if !(Immutable mutability) */
    private byte findNewFreeOrRemoved() {
        /* if byte|char|short elem */
        int mc = modCount();
        int size = size();
        if (size >= BYTE_CARDINALITY -
                /* if Mutable mutability && !(LHash hash) */2
                /* elif !(Mutable mutability) || LHash hash //1// endif */) {
            throw new HashOverflowException();
        }
        /* endif */
        byte free = this.freeValue;
        /* if Mutable mutability && !(LHash hash) */byte removed = this.removedValue;/* endif */
        /* if Separate kv */
        byte[] keys = this.set;
        /* elif Parallel kv */
        char[] tab = this.table;
        /* endif */
        int capacity = capacity();
        Random random = ThreadLocalRandom.current();
        byte newFree;
        /* if byte|char|short elem */
        searchForFree:
        if (size > BYTE_CARDINALITY * 3 / 4) {
            int nf = random.nextInt(BYTE_CARDINALITY) * BYTE_PERMUTATION_STEP;
            for (int i = 0; i < BYTE_CARDINALITY; i++) {
                nf = nf + BYTE_PERMUTATION_STEP;
                newFree = (byte) nf;
                if (newFree != free &&
                        /* if Mutable mutability && !(LHash hash) */newFree != removed &&/* endif */
                        index(newFree) < 0) {
                    break searchForFree;
                }
            }
            if (mc != modCount())
                throw new ConcurrentModificationException();
            throw new AssertionError("Impossible state");
        }
        else /* endif */ {
            do {
                newFree = (byte) random./* if byte|char|short|int elem */nextInt()
                                        /* elif long elem //nextLong()// endif */;
            } while (newFree == free ||
                    /* if Mutable mutability && !(LHash hash) */newFree == removed ||/* endif */
                    index(newFree) >= 0);
        }
        return newFree;
    }


    byte changeFree() {
        int mc = modCount();
        byte newFree = findNewFreeOrRemoved();
        incrementModCount();
        mc++;
        /* if Separate kv */
        ByteArrays.replaceAll(set, freeValue, newFree);
        /* elif Parallel kv */
        ByteArrays.replaceAllKeys(table, freeValue, newFree);
        /* endif */
        this.freeValue = newFree;
        if (mc != modCount())
            throw new ConcurrentModificationException();
        return newFree;
    }

    /* if Mutable mutability && !(LHash hash) */
    byte changeRemoved() {
        int mc = modCount();
        byte newRemoved = findNewFreeOrRemoved();
        incrementModCount();
        mc++;
        if (!noRemoved()) {
            /* if Separate kv */
            ByteArrays.replaceAll(set, removedValue, newRemoved);
            /* elif Parallel kv */
            ByteArrays.replaceAllKeys(table, removedValue, newRemoved);
            /* endif */
        }
        this.removedValue = newRemoved;
        if (mc != modCount())
            throw new ConcurrentModificationException();
        return newRemoved;
    }
    /* endif */

    @Override
    void allocateArrays(int capacity) {
        /* if Separate kv */
        set = new byte[capacity];
        if (freeValue != 0)
            Arrays.fill(set, freeValue);
        /* elif Parallel kv */
        table = new char[capacity/* if long elem */ * 2/* endif */];
        if (freeValue != 0)
            ByteArrays.fillKeys(table, freeValue);
        /* endif */
    }

    @Override
    public void clear() {
        super.clear();
        /* if Separate kv */
        Arrays.fill(set, freeValue);
        /* elif Parallel kv */
        ByteArrays.fillKeys(table, freeValue);
        /* endif */
    }

    /* if Mutable mutability && !(LHash hash) */
    @Override
    void removeAt(int index) {
        /* if Separate kv */
        set[index] = removedValue;
        /* elif Parallel kv */
        /* if !(long elem) */
        U.putByte(table, CHAR_BASE + BYTE_KEY_OFFSET + (((long) index) << CHAR_SCALE_SHIFT),
                removedValue);
        /* elif long elem */
        table[index] = removedValue;
        /* endif */
        /* endif */
    }
    /* endif */
    /* endif */
}
