/* with
 DHash|QHash|LHash hash
 char|byte|short|int|long|float|double|obj key
 obj value
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
import net.openhft.collect.set.hash.HashCharSet;
import net.openhft.function.*;
import net.openhft.collect.map.hash.HashCharObjMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.*;


public abstract class MutableDHashCharObjMapSO</* if obj key //K, // endif */V>
        extends MutableDHashCharKeyMap/* if obj key //<K>// endif */
        implements HashCharObjMap</* if obj key //K, // endif */V>,
        InternalCharObjMapOps</* if obj key //K, // endif */V>, CharObjDHash {

    V[] values;


    void copy(CharObjDHash hash) {
        super.copy(hash);
        // noinspection unchecked
        values = (V[]) hash.valueArray().clone();
    }

    void move(CharObjDHash hash) {
        super.move(hash);
        // noinspection unchecked
        values = (V[]) hash.valueArray();
    }

    @Override
    @Nonnull
    public Object[] valueArray() {
        return values;
    }


    boolean nullableValueEquals(@Nullable V a, @Nullable V b) {
        return a == b || (a != null && a.equals(b));
    }

    boolean valueEquals(@Nonnull V a, @Nullable V b) {
        return a.equals(b);
    }

    int nullableValueHashCode(@Nullable V value) {
        return value != null ? value.hashCode() : 0;
    }

    int valueHashCode(@Nonnull V value) {
        return value.hashCode();
    }


    /* if Mutable mutability */
    /* with key view */
    @Override
    public CharIterator/*<>*/ iterator() {
        /* if !(Immutable mutability) */int mc = modCount();/* endif */
        /* if Mutable mutability && !(LHash hash) //
        if (!noRemoved())
            return new SomeRemovedKeyIterator(// if !(Immutable mutability) //mc// endif //);
        // endif */
        return new NoRemovedKeyIterator(/* if !(Immutable mutability) */mc/* endif */);
    }

    @Override
    public CharCursor/*<>*/ setCursor() {
        /* if !(Immutable mutability) */int mc = modCount();/* endif */
        /* if Mutable mutability && !(LHash hash) //
        if (!noRemoved())
            return new SomeRemovedKeyCursor(// if !(Immutable mutability) //mc// endif //);
        // endif */
        return new NoRemovedKeyCursor(/* if !(Immutable mutability) */mc/* endif */);
    }
    /* endwith */
    /* endif */


    int valueIndex(@Nullable Object value) {
        if (value == null)
            return nullValueIndex();
        /* template ValueIndex */ throw new NotGenerated(); /* endtemplate */
    }

    private int nullValueIndex() {
        /* template ValueIndex with null value */ throw new NotGenerated(); /* endtemplate */
    }

    @Override
    public boolean containsValue(Object value) {
        return valueIndex(value) >= 0;
    }

    boolean removeValue(@Nullable Object value) {
        /* if Mutable mutability */
        int index = valueIndex(value);
        if (index >= 0) {
            removeAt(index);
            return true;
        } else {
            return false;
        }
        /* elif !(Mutable mutability) //
        throw new UnsupportedOperationException();
        // endif */
    }


    /* if !(Immutable mutability) */
    @Override
    void rehash(int newCapacity) {
        /* template Rehash */
    }

    int insert(/* bits */char key, V value) {
        /* template Insert with internal version */ throw new NotGenerated(); /* endtemplate */
    }

    /* if obj key */
    int insertNullKey(V value) {
        /* template Insert with null key */ throw new NotGenerated(); /* endtemplate */
    }
    /* endif */


    @Override
    void allocateArrays(int capacity) {
        super.allocateArrays(capacity);
        // noinspection unchecked
        values = (V[]) new Object[capacity];
    }

    @Override
    public void clear() {
        super.clear();
        Arrays.fill(values, null);
    }
    /* endif */


    /* if Mutable mutability */
    @Override
    void removeAt(int index) {
        // if !(LHash hash) */
        incrementModCount();
        super.removeAt(index);
        values[index] = null;
        postRemoveHook();
        /* elif LHash hash //
        /* template RemoveAt */
        // endif */
    }


    // removing operations are overridden in order to set value to null on removing
    // (for garbage collection)
    /* with key view */

    @Override
    public boolean removeIf(Predicate<? super Character> filter) {
        /* template RemoveIf */ throw new NotGenerated(); /* endtemplate */
    }

    /* if !(obj key) */
    @Override
    public boolean removeIf(CharPredicate filter) {
        /* template RemoveIf */ throw new NotGenerated(); /* endtemplate */
    }
    /* endif */

    @Override
    public boolean removeAll(@Nonnull HashCharSet/*<>*/ thisC, @Nonnull Collection<?> c) {
        /* template RemoveAll with given this */ throw new NotGenerated(); /* endtemplate */
    }

    /* if !(obj key) */
    @Override
    boolean removeAll(@Nonnull HashCharSet/*<>*/ thisC, @Nonnull CharCollection c) {
        /* template RemoveAll with given this */ throw new NotGenerated(); /* endtemplate */
    }

    /* if float|double key */
    @Override
    boolean removeAll(@Nonnull HashCharSet/*<>*/ thisC, @Nonnull InternalCharCollectionOps c) {
        /* template RemoveAll with internal version with given this */ throw new NotGenerated();
        /* endtemplate */
    }
    /* endif */
    /* endif */

    @Override
    public boolean retainAll(@Nonnull HashCharSet/*<>*/ thisC, @Nonnull Collection<?> c) {
        /* if !(obj key) */
        if (c instanceof CharCollection)
            return retainAll(thisC, (CharCollection) c);
        /* endif */
        /* template RetainAll with generic version given this */ throw new NotGenerated();
        /* endtemplate */
    }

    /* if !(obj key) */
    private boolean retainAll(@Nonnull HashCharSet/*<>*/ thisC, @Nonnull CharCollection c) {
        /* template RetainAll given this */ throw new NotGenerated(); /* endtemplate */
    }

    /* if float|double key */
    private boolean retainAll(@Nonnull HashCharSet/*<>*/ thisC,
            @Nonnull InternalCharCollectionOps c) {
        /* template RetainAll with internal version given this */ throw new NotGenerated();
        /* endtemplate */
    }
    /* endif */
    /* endif */

    /* endwith*/


    /* with key view No|Some removed */
    /* if !(LHash hash Some removed) */

    class NoRemovedKeyIterator extends NoRemovedIterator {
        /* if just comment */
        // vals non-final because could be updated in shift-removing procedure
        /* endif */
        /* if !(LHash hash) */final/* endif */ V[] vals;

        private NoRemovedKeyIterator(/* if !(Immutable mutability) */int mc/* endif */) {
            super(mc);
            vals = values;
        }

        @Override
        public void remove() {
            /* template Iterator.remove */ throw new NotGenerated(); /* endtemplate */
        }
    }


    class NoRemovedKeyCursor extends NoRemovedCursor {
        /* if !(LHash hash) */final/* endif */ V[] vals;

        private NoRemovedKeyCursor(/* if !(Immutable mutability) */int mc/* endif */) {
            super(mc);
            vals = values;
        }

        @Override
        public void remove() {
            /* template Cursor.remove */ throw new NotGenerated(); /* endtemplate */
        }
    }

    /* endif */
    /* endwith */

    /* endif */
}
