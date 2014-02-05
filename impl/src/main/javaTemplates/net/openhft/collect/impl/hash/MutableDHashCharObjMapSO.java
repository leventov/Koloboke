/* with
 char|byte|short|int|long|float|double|obj key
 obj value
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

import net.openhft.collect.*;
import net.openhft.collect.impl.*;
import net.openhft.function.*;
import net.openhft.collect.map.hash.HashCharObjMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;


public abstract class MutableDHashCharObjMapSO</* if obj key //K, // endif */V>
        extends MutableDHashCharKeyMap/* if obj key //<K>// endif */
        implements HashCharObjMap</* if obj key //K, // endif */V>,
        InternalCharObjMapOps</* if obj key //K, // endif */V>, CharObjDHash {

    V[] values;


    final void copy(CharObjDHash hash) {
        super.copy(hash);
        // noinspection unchecked
        values = (V[]) hash.valueArray().clone();
    }

    final void move(CharObjDHash hash) {
        super.move(hash);
        // noinspection unchecked
        values = (V[]) hash.valueArray();
    }

    @Override
    @NotNull
    public Object[] valueArray() {
        return values;
    }


    boolean nullableValueEquals(@Nullable V a, @Nullable V b) {
        return a == b || (a != null && a.equals(b));
    }

    boolean valueEquals(@NotNull V a, @Nullable V b) {
        return a.equals(b);
    }

    int nullableValueHashCode(@Nullable V value) {
        return value != null ? value.hashCode() : 0;
    }

    int valueHashCode(@NotNull V value) {
        return value.hashCode();
    }


    /* with key view */
    @Override
    public CharIterator/*<>*/ iterator() {
        /* if Mutable mutability //
        if (!noRemoved()) return new SomeRemovedKeyIterator();
        // endif */
        return new NoRemovedKeyIterator();
    }

    @Override
    public CharCursor/*<>*/ setCursor() {
        /* if Mutable mutability //
        if (!noRemoved()) return new SomeRemovedKeyCursor();
        // endif */
        return new NoRemovedKeyCursor();
    }
    /* endwith */


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
        /* elif Immutable mutability //
        throw new UnsupportedOperationException();
        // endif */
    }


    /* if Mutable mutability */
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
        values[index] = null;
        super.removeAt(index);
    }
    /* endif */

    /* if !(Immutable mutability) */
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
    public boolean removeAll(@NotNull Collection<?> c) {
        /* template RemoveAll */ throw new NotGenerated(); /* endtemplate */
    }

    /* if !(obj key) */
    @Override
    boolean removeAll(@NotNull CharCollection c) {
        /* template RemoveAll */ throw new NotGenerated(); /* endtemplate */
    }

    /* if float|double key */
    @Override
    boolean removeAll(@NotNull InternalCharCollectionOps c) {
        /* template RemoveAll with internal version */ throw new NotGenerated(); /* endtemplate */
    }
    /* endif */
    /* endif */

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        /* if !(obj key) */
        if (c instanceof CharCollection)
            return retainAll((CharCollection) c);
        /* endif */
        /* template RetainAll with generic version */ throw new NotGenerated(); /* endtemplate */
    }

    /* if !(obj key) */
    private boolean retainAll(@NotNull CharCollection c) {
        /* template RetainAll */ throw new NotGenerated(); /* endtemplate */
    }

    /* if float|double key */
    private boolean retainAll(@NotNull InternalCharCollectionOps c) {
        /* template RetainAll with internal version */ throw new NotGenerated(); /* endtemplate */
    }
    /* endif */
    /* endif */

    /* endwith*/
    /* endif */


    /* with key view No|Some removed */
    /* if !(Immutable mutability Some removed) */

    class NoRemovedKeyIterator extends NoRemovedIterator {
        final V[] vals;

        private NoRemovedKeyIterator() {
            super();
            vals = values;
        }

        @Override
        public void remove() {
            /* template Iterator.remove */ throw new NotGenerated(); /* endtemplate */
        }
    }


    class NoRemovedKeyCursor extends NoRemovedCursor{
        final V[] vals;

        private NoRemovedKeyCursor() {
            super();
            vals = values;
        }

        @Override
        public void remove() {
            /* template Cursor.remove */ throw new NotGenerated(); /* endtemplate */
        }
    }

    /* endif */
    /* endwith */
}
