/* with
 DHash|QHash|LHash hash
 byte|char|short|int|long|float|double|obj key
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

package com.koloboke.collect.impl.hash;

import com.koloboke.collect.*;
import com.koloboke.collect.hash.HashConfig;
import com.koloboke.function./*f*/ByteConsumer/**/;
import com.koloboke.function./*f*/BytePredicate/**/;
import com.koloboke.function.Consumer;
import com.koloboke.function.Predicate;
import com.koloboke.collect.impl.*;
import com.koloboke.collect.set.ByteSet;
import com.koloboke.collect.set.hash.HashByteSet;
import javax.annotation.Nonnull;

import java.util.*;


public abstract class MutableDHashSeparateKVByteKeyMap/*<>*/
        extends MutableSeparateKVByteDHashGO/*<>*/ {

    /* if obj key */
    public Equivalence<Byte> keyEquivalence() {
        return Equivalence.defaultEquality();
    }
    /* endif */


    public final boolean containsKey(Object key) {
        return contains(key);
    }

    /* if !(obj key) */
    public boolean containsKey(byte key) {
        return contains(key);
    }
    /* endif */


    @Nonnull
    public HashByteSet keySet() {
        return new KeyView();
    }


    abstract boolean justRemove(/* raw */byte key);

    /* if float|double key */
    @Override
    abstract boolean justRemove(/* bits */byte key);
    /* endif */

    class KeyView extends AbstractByteKeyView/*<>*/
            implements HashByteSet/*<>*/, InternalByteCollectionOps/*<>*/, SeparateKVByteDHash {

        /* if obj key */
        /* if obj key //@Override// endif */
        @Nonnull
        public Equivalence<Byte> equivalence() {
            return MutableDHashSeparateKVByteKeyMap.this.keyEquivalence();
        }
        /* endif */

        @Nonnull
        @Override
        public HashConfig hashConfig() {
            return MutableDHashSeparateKVByteKeyMap.this.hashConfig();
        }

        @Override
        public HashConfigWrapper configWrapper() {
            return MutableDHashSeparateKVByteKeyMap.this.configWrapper();
        }

        @Override
        public int size() {
            return MutableDHashSeparateKVByteKeyMap.this.size();
        }

        @Override
        public double currentLoad() {
            return MutableDHashSeparateKVByteKeyMap.this.currentLoad();
        }

        /* if !(obj|float|double key) */
        @Override
        public byte freeValue() {
            return MutableDHashSeparateKVByteKeyMap.this.freeValue();
        }

        @Override
        public boolean supportRemoved() {
            return MutableDHashSeparateKVByteKeyMap.this.supportRemoved();
        }

        @Override
        public byte removedValue() {
            return MutableDHashSeparateKVByteKeyMap.this.removedValue();
        }
        /* endif */

        /* if Separate kv */
        @Nonnull
        @Override
        public /* bits *//* raw */byte[] keys() {
            return MutableDHashSeparateKVByteKeyMap.this.keys();
        }
        /* elif Parallel kv */
        @Nonnull
        @Override
        public /* if !(obj key) */char/* elif obj key //Object// endif */[] table() {
            return MutableDHashSeparateKVByteKeyMap.this.table();
        }
        /* endif */

        @Override
        public int capacity() {
            return MutableDHashSeparateKVByteKeyMap.this.capacity();
        }

        @Override
        public int freeSlots() {
            return MutableDHashSeparateKVByteKeyMap.this.freeSlots();
        }

        @Override
        public boolean noRemoved() {
            return MutableDHashSeparateKVByteKeyMap.this.noRemoved();
        }

        @Override
        public int removedSlots() {
            return MutableDHashSeparateKVByteKeyMap.this.removedSlots();
        }

        @Override
        public int modCount() {
            return MutableDHashSeparateKVByteKeyMap.this.modCount();
        }

        @Override
        public final boolean contains(Object o) {
            return MutableDHashSeparateKVByteKeyMap.this.contains(o);
        }

        /* if !(obj key) */
        @Override
        public boolean contains(byte key) {
            return MutableDHashSeparateKVByteKeyMap.this.contains(key);
        }

        /* if float|double key */
        @Override
        public boolean contains(/* bits */byte bits) {
            return MutableDHashSeparateKVByteKeyMap.this.contains(bits);
        }
        /* endif */
        /* endif */


        /* if obj key || JDK8 jdk //@Override// endif */
        public void forEach(Consumer<? super Byte> action) {
            MutableDHashSeparateKVByteKeyMap.this.forEach(action);
        }

        /* if !(obj key) */
        @Override
        public void forEach(ByteConsumer action) {
            MutableDHashSeparateKVByteKeyMap.this.forEach(action);
        }
        /* endif */

        @Override
        public boolean forEachWhile(/*f*/BytePredicate/*<super>*/
                predicate) {
            return MutableDHashSeparateKVByteKeyMap.this.forEachWhile(predicate);
        }

        @Override
        public boolean allContainingIn(ByteCollection/*<?>*/ c) {
            return MutableDHashSeparateKVByteKeyMap.this.allContainingIn(c);
        }

        @Override
        public boolean reverseAddAllTo(ByteCollection/*<super>*/ c) {
            return MutableDHashSeparateKVByteKeyMap.this.reverseAddAllTo(c);
        }

        @Override
        public boolean reverseRemoveAllFrom(ByteSet/*<?>*/ s) {
            return MutableDHashSeparateKVByteKeyMap.this.reverseRemoveAllFrom(s);
        }

        @Override
        @Nonnull
        public ByteIterator/*<>*/ iterator() {
            return MutableDHashSeparateKVByteKeyMap.this.iterator();
        }

        @Override
        @Nonnull
        public ByteCursor/*<>*/ cursor() {
            return setCursor();
        }

        @Override
        @Nonnull
        public Object[] toArray() {
            return MutableDHashSeparateKVByteKeyMap.this.toArray();
        }

        @Override
        @Nonnull
        public <T> T[] toArray(@Nonnull T[] a) {
            return MutableDHashSeparateKVByteKeyMap.this.toArray(a);
        }

        /* if !(obj key) */
        @Override
        public byte[] toByteArray() {
            return MutableDHashSeparateKVByteKeyMap.this.toByteArray();
        }

        @Override
        public byte[] toArray(byte[] a) {
            return MutableDHashSeparateKVByteKeyMap.this.toArray(a);
        }
        /* endif */


        @Override
        public int hashCode() {
            return setHashCode();
        }

        @Override
        public String toString() {
            return setToString();
        }


        @Override
        public boolean shrink() {
            return MutableDHashSeparateKVByteKeyMap.this.shrink();
        }


        @Override
        public final boolean remove(Object o) {
            return justRemove(/* if !(obj key) */(Byte) /* endif */o);
        }

        /* if !(obj key) */
        @Override
        public boolean removeByte(byte v) {
            return justRemove(v);
        }

        /* if float|double key */
        @Override
        public boolean removeByte(/* bits */byte bits) {
            return justRemove(bits);
        }
        /* endif */
        /* endif */


        /* if obj key || JDK8 jdk //@Override// endif */
        public boolean removeIf(Predicate<? super Byte> filter) {
            return MutableDHashSeparateKVByteKeyMap.this.removeIf(filter);
        }

        /* if !(obj key) */
        @Override
        public boolean removeIf(BytePredicate filter) {
            return MutableDHashSeparateKVByteKeyMap.this.removeIf(filter);
        }
        /* endif */

        @Override
        public boolean removeAll(@Nonnull Collection<?> c) {
            /* if !(obj key) */
            if (c instanceof ByteCollection) {
            /* endif */
                if (c instanceof InternalByteCollectionOps) {
                    InternalByteCollectionOps c2 = (InternalByteCollectionOps) c;
                    if (c2.size() < this.size()/* if obj key //
                            && equivalence().equals(c2.equivalence())
                            // endif */) {
                        /* if obj key */// noinspection unchecked/* endif */
                        return c2.reverseRemoveAllFrom(this);
                    }
                }
            /* if !(obj key) */
                return MutableDHashSeparateKVByteKeyMap.this.removeAll(this, (ByteCollection) c);
            }
            /* endif */
            return MutableDHashSeparateKVByteKeyMap.this.removeAll(this, c);
        }

        @Override
        public boolean retainAll(@Nonnull Collection<?> c) {
            return MutableDHashSeparateKVByteKeyMap.this.retainAll(this, c);
        }

        @Override
        public void clear() {
            MutableDHashSeparateKVByteKeyMap.this.clear();
        }
    }
}
