/* with
 char|byte|short|int|long elem
 Mutable|Immutable mutability
*/
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

import net.openhft.collect.HashOverflowException;
import net.openhft.collect.impl.*;
import javax.annotation.Nonnull;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;


public abstract class MutableCharDHashSO extends MutableDHash
        implements CharDHash/* if byte|char|short elem */, CharConstants /* endif */ {

    char freeValue;
    /* if Mutable mutability */
    char removedValue;
    /* endif */
    char[] set;

    final void copy(CharDHash hash) {
        super.copy(hash);
        freeValue = hash.freeValue();
        /* if Mutable mutability */
        if (hash.supportRemoved())
            removedValue = hash.removedValue();
        /* endif */
        set = hash.keys().clone();
    }

    final void move(CharDHash hash) {
        super.copy(hash);
        freeValue = hash.freeValue();
        /* if Mutable mutability */
        if (hash.supportRemoved())
            removedValue = hash.removedValue();
        /* endif */
        set = hash.keys();
    }

    final void init(float loadFactor, int size,
            char freeValue/* if Mutable mutability */, char removedValue/* endif */) {
        this.freeValue = freeValue;
        /* if Mutable mutability */
        this.removedValue = removedValue;
        /* endif */
        // calls allocateArrays, fill keys with this.freeValue => assign it before
        super.init(loadFactor, size);
    }



    @Nonnull
    @Override
    public char[] keys() {
        return set;
    }

    @Override
    public int capacity() {
        return set.length;
    }

    @Override
    public char freeValue() {
        return freeValue;
    }

    @Override
    public boolean supportRemoved() {
        return /* if Mutable mutability */true/* elif Immutable mutability //false// endif */;
    }

    @Override
    public char removedValue() {
        /* if Mutable mutability */
        return removedValue;
        /* elif !(Mutable mutability) //
        throw new UnsupportedOperationException();
        // endif */
    }

    public boolean contains(Object key) {
        return contains(((Character) key).charValue());
    }

    public boolean contains(char key) {
        return index(key) >= 0;
    }

    int index(char key) {
        /* template Index */ throw new NotGenerated(); /* endtemplate */
    }

    /* if Mutable mutability */
    private char findNewFreeOrRemoved() {
        int size = size();
        /* if byte|char|short elem */
        if (size >= CHAR_CARDINALITY -
                /* if Mutable mutability */2/* elif Immutable mutability //1// endif */) {
            throw new HashOverflowException();
        }
        /* endif */
        char free = this.freeValue;
        /* if Mutable mutability */char removed = this.removedValue;/* endif */
        char[] keys = this.set;
        int capacity = keys.length;
        Random random = ThreadLocalRandom.current();
        char newFree;
        /* if byte|char|short elem */
        searchForFree:
        if (size > CHAR_CARDINALITY / 2) {
            int searchStart = random.nextInt(capacity);
            for (int i = searchStart; i >= 0; i--) {
                if ( keys[ i ] == free ) {
                    newFree = (char) i;
                    if (newFree != free
                            /* if Mutable mutability */ && newFree != removed/* endif */)
                        break searchForFree;
                }
            }
            for (int i = capacity - 1; i > searchStart; i--) {
                if ( keys[ i ] == free ) {
                    newFree = (char) i;
                    if (newFree != free
                            /* if Mutable mutability */ && newFree != removed/* endif */)
                        break searchForFree;
                }
            }
            throw new RuntimeException("Impossible state");
        }
        else /* endif */ {
            do {
                newFree = (char) random./* if byte|char|short|int elem */nextInt()
                                        /* elif long elem //nextLong()// endif */;
            } while (newFree == free/* if Mutable mutability */ || newFree == removed/* endif */ ||
                    index(newFree) >= 0);
        }
        return newFree;
    }


    char changeFree() {
        int mc = modCount();
        char newFree = findNewFreeOrRemoved();
        incrementModCount();
        mc++;
        char[] keys = this.set;
        CharArrays.replaceAll(keys, freeValue, newFree);
        this.freeValue = newFree;
        if (mc != modCount())
            throw new ConcurrentModificationException();
        return newFree;
    }

    char changeRemoved() {
        int mc = modCount();
        char newRemoved = findNewFreeOrRemoved();
        incrementModCount();
        mc++;
        if (!noRemoved()) {
            char[] keys = this.set;
            CharArrays.replaceAll(keys, removedValue, newRemoved);
        }
        this.removedValue = newRemoved;
        if (mc != modCount())
            throw new ConcurrentModificationException();
        return newRemoved;
    }


    @Override
    void allocateArrays(int capacity) {
        set = new char[capacity];
        if (freeValue != 0)
            Arrays.fill(set, freeValue);
    }

    @Override
    public void clear() {
        super.clear();
        Arrays.fill(set, freeValue);
    }

    @Override
    void removeAt(int index) {
        set[index] = removedValue;
        postRemoveHook();
    }
    /* endif */
}
