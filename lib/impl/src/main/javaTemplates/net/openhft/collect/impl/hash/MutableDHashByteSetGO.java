/* with
 DHash|QHash|LHash hash
 byte|char|short|int|long|float|double|obj elem
 Mutable|Updatable|Immutable mutability
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

import net.openhft.collect.*;
import net.openhft.collect.impl.*;
import net.openhft.collect.set.hash.HashByteSet;
import javax.annotation.Nonnull;

import java.util.*;


public class MutableDHashByteSetGO/*<>*/ extends MutableByteDHashSetSO/*<>*/
        implements HashByteSet/*<>*/, InternalByteCollectionOps/*<>*/ {

    @Override
    final void copy(SeparateKVByteDHash hash) {
        int myMC = modCount(), hashMC = hash.modCount();
        super.copy(hash);
        if (myMC != modCount() || hashMC != hash.modCount())
            throw new ConcurrentModificationException();
    }

    @Override
    final void move(SeparateKVByteDHash hash) {
        int myMC = modCount(), hashMC = hash.modCount();
        super.move(hash);
        if (myMC != modCount() || hashMC != hash.modCount())
            throw new ConcurrentModificationException();
    }

    public int hashCode() {
        return setHashCode();
    }

    @Override
    public String toString() {
        return setToString();
    }

    @Override
    public boolean equals(Object obj) {
        return CommonSetOps.equals(this, obj);
    }

    @Override
    public boolean containsAll(@Nonnull Collection<?> c) {
        return CommonByteCollectionOps.containsAll(this, c);
    }

    @Nonnull
    @Override
    public ByteCursor/*<>*/ cursor() {
        return setCursor();
    }


    /* if !(obj elem) */
    @Override
    public boolean add(Byte e) {
        return add(e.byteValue());
    }
    /* endif */

    @Override
    public boolean add(/* bits */byte key) {
        /* template Add with internal version */ throw new NotGenerated(); /* endtemplate */
    }

    /* if float|double elem */
    @Override
    public boolean add(byte key) {
        return add(/* unwrap elem */key/**/);
    }
    /* elif obj elem */
    private boolean addNullKey() {
        /* template Add with null elem */ throw new NotGenerated(); /* endtemplate */
    }
    /* endif */

    @Override
    public boolean addAll(@Nonnull Collection<? extends Byte> c) {
        return CommonByteCollectionOps.addAll(this, c);
    }

    @Override
    public boolean remove(Object key) {
        /* if !(obj elem) */
        return removeByte(((Byte) key).byteValue());
        /* elif obj elem //
        /* template Remove */
        // endif */
    }

    /* if obj elem */
    private boolean removeNullKey() {
        /* template Remove with null elem */ throw new NotGenerated(); /* endtemplate */
    }
    /* endif */

    @Override
    boolean justRemove(/* bits */byte key) {
        return /* if !(obj elem) */removeByte(key)/* elif obj elem //remove(key)// endif */;
    }

    /* if !(obj elem) */
    @Override
    public boolean removeByte(/* bits */byte key) {
        /* template Remove with internal version */ throw new NotGenerated(); /* endtemplate */
    }

    /* if float|double elem */
    @Override
    public boolean removeByte(byte key) {
        return removeByte(/* unwrap elem */key/**/);
    }
    /* endif */
    /* endif */


    @Override
    public boolean removeAll(@Nonnull Collection<?> c) {
        /* if Mutable mutability */
        /* if !(obj elem) */
        if (c instanceof ByteCollection) {
        /* endif */
            if (c instanceof InternalByteCollectionOps) {
                InternalByteCollectionOps c2 = (InternalByteCollectionOps) c;
                if (c2.size() < this.size()/* if obj elem //
                            && equivalence().equals(c2.equivalence())
                            // endif */) {
                    /* if obj elem */// noinspection unchecked/* endif */
                    return c2.reverseRemoveAllFrom(this);
                }
            }
        /* if !(obj elem) */
            return removeAll(this, (ByteCollection) c);
        }
        /* endif */
        return removeAll(this, c);
        /* elif !(Mutable mutability) //
        throw new UnsupportedOperationException();
        // endif */
    }

    @Override
    public boolean retainAll(@Nonnull Collection<?> c) {
        return retainAll(this, c);
    }
}