/* with
 DHash|LHash hash
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
    /* if Mutable mutability && !(LHash hash) */
    char removedValue;
    /* endif */
    char[] set;

    void copy(CharDHash hash) {
        super.copy(hash);
        freeValue = hash.freeValue();
        /* if Mutable mutability && !(LHash hash) */
        if (hash.supportRemoved())
            removedValue = hash.removedValue();
        /* endif */
        set = hash.keys().clone();
        /* if Mutable mutability && !(LHash hash) */
        if (!hash.supportRemoved()) {
            removedValue = freeValue;
            removedValue = findNewFreeOrRemoved();
        }
        /* endif */
    }

    void move(CharDHash hash) {
        super.copy(hash);
        freeValue = hash.freeValue();
        /* if Mutable mutability && !(LHash hash) */
        if (hash.supportRemoved())
            removedValue = hash.removedValue();
        /* endif */
        set = hash.keys();
        /* if Mutable mutability && !(LHash hash) */
        if (!hash.supportRemoved()) {
            removedValue = freeValue;
            removedValue = findNewFreeOrRemoved();
        }
        /* endif */
    }

    final void init(HashConfigWrapper configWrapper, int size, char freeValue
            /* if Mutable mutability && !(LHash hash) */, char removedValue/* endif */) {
        this.freeValue = freeValue;
        /* if Mutable mutability && !(LHash hash) */
        this.removedValue = removedValue;
        /* endif */
        // calls allocateArrays, fill keys with this.freeValue => assign it before
        super.init(configWrapper, size);
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
        return /* if Mutable mutability && !(LHash hash) */true
                /* elif !(Mutable mutability) || LHash hash //false// endif */;
    }

    @Override
    public char removedValue() {
        /* if Mutable mutability && !(LHash hash) */
        return removedValue;
        /* elif !(Mutable mutability) || LHash hash //
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

    /* if !(Immutable mutability) */
    private char findNewFreeOrRemoved() {
        /* if byte|char|short elem */
        int mc = modCount();
        int size = size();
        if (size >= CHAR_CARDINALITY -
                /* if Mutable mutability && !(LHash hash) */2
                /* elif !(Mutable mutability) || LHash hash //1// endif */) {
            throw new HashOverflowException();
        }
        /* endif */
        char free = this.freeValue;
        /* if Mutable mutability && !(LHash hash) */char removed = this.removedValue;/* endif */
        char[] keys = this.set;
        int capacity = keys.length;
        Random random = ThreadLocalRandom.current();
        char newFree;
        /* if byte|char|short elem */
        searchForFree:
        if (size > CHAR_CARDINALITY / 2) {
            int searchStart = random.nextInt(capacity);
            for (int i = searchStart; i >= 0; i--) {
                if (keys[i] == free) {
                    newFree = (char) i;
                    if (newFree != free/* if Mutable mutability && !(LHash hash) */ &&
                            newFree != removed/* endif */) {
                        break searchForFree;
                    }
                }
            }
            for (int i = capacity - 1; i > searchStart; i--) {
                if (keys[i] == free) {
                    newFree = (char) i;
                    if (newFree != free/* if Mutable mutability && !(LHash hash) */ &&
                            newFree != removed/* endif */) {
                        break searchForFree;
                    }
                }
            }
            newFree = (char) (free + capacity);
            if (newFree != free &&
                    /* if Mutable mutability && !(LHash hash) */newFree != removed &&/* endif */
                    index(newFree) < 0) {
                break searchForFree;
            }
            /* if Mutable mutability && !(LHash hash) */
            newFree = (char) (removed + capacity);
            if (newFree != free && newFree != removed && index(newFree) < 0) {
                break searchForFree;
            }
            /* endif */
            if (mc != modCount())
                throw new ConcurrentModificationException();
            // Will fail on tests which are executed with assertions enabled.
            assert false : "Impossible state";
            // As I see it's already impossible state. But if the above implementation has bugs
            // (as it was already twice), try ALL char values for being 146% sure.
            for (int i = 0; i < CHAR_CARDINALITY; i++) {
                newFree = (char) i;
                if (newFree != free &&
                        /* if Mutable mutability && !(LHash hash) */newFree != removed &&/* endif */
                        index(newFree) < 0) {
                    break searchForFree;
                }
            }
            if (mc != modCount())
                throw new ConcurrentModificationException();
            throw new AssertionError("Surely impossible state");
        }
        else /* endif */ {
            do {
                newFree = (char) random./* if byte|char|short|int elem */nextInt()
                                        /* elif long elem //nextLong()// endif */;
            } while (newFree == free ||
                    /* if Mutable mutability && !(LHash hash) */newFree == removed ||/* endif */
                    index(newFree) >= 0);
        }
        return newFree;
    }


    char changeFree() {
        int mc = modCount();
        char newFree = findNewFreeOrRemoved();
        incrementModCount();
        mc++;
        CharArrays.replaceAll(set, freeValue, newFree);
        this.freeValue = newFree;
        if (mc != modCount())
            throw new ConcurrentModificationException();
        return newFree;
    }

    /* if Mutable mutability && !(LHash hash) */
    char changeRemoved() {
        int mc = modCount();
        char newRemoved = findNewFreeOrRemoved();
        incrementModCount();
        mc++;
        if (!noRemoved()) {
            CharArrays.replaceAll(set, removedValue, newRemoved);
        }
        this.removedValue = newRemoved;
        if (mc != modCount())
            throw new ConcurrentModificationException();
        return newRemoved;
    }
    /* endif */

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

    /* if Mutable mutability && !(LHash hash) */
    @Override
    void removeAt(int index) {
        set[index] = removedValue;
    }
    /* endif */
    /* endif */
}
