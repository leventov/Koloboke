/* with
 char|byte|short|int|long|float|double|obj key
 short|byte|char|int|long|float|double|obj value
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
import net.openhft.function.*;
import net.openhft.collect.impl.*;
import net.openhft.collect.map.CharShortCursor;
import net.openhft.collect.set.ObjSet;
import net.openhft.collect.set.ShortSet;
import net.openhft.collect.set.hash.HashObjSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;


public class MutableDHashCharShortMapGO/*<>*/
        extends MutableDHashCharShortMapSO/*<>*/ {

    /* if obj value */
    @Override
    public Equivalence<Short> valueEquivalence() {
        return null;
    }
    /* endif */

    /* if !(obj value) */
    @Override
    public short defaultValue() {
        return /* const value 0 */(short) 0/* endconst */;
    }
    /* endif */

    @Override
    public boolean containsEntry(/* raw */char key, /* raw */short value) {
        /* template ContainsEntry */ throw new NotGenerated(); /* endtemplate */
    }

    /* if !(obj key obj value) */
    @Override
    public Short get(Object key) {
        /* template Get with generic version */ throw new NotGenerated(); /* endtemplate */
    }
    /* endif */

    /* define valueSuffix */
    /* if obj key short|byte|char|int|long|float|double value //$Short// endif */
    /* enddefine */

    @Override
    public short get/* valueSuffix */(/* raw */char key) {
        /* template Get */ throw new NotGenerated(); /* endtemplate */
    }

    /* if !(obj key obj value) */
    /* if JDK8 jdk */@Override/* endif */
    public Short getOrDefault(Object key, Short defaultValue) {
        /* template GetOrDefault with generic version */ throw new NotGenerated(); /* endtemplate */
    }
    /* endif */

    @Override
    public short getOrDefault(/* raw */char key, short defaultValue) {
        /* template GetOrDefault */ throw new NotGenerated(); /* endtemplate */
    }

    /* if obj key obj value || JDK8 jdk */@Override/* endif */
    public void forEach(BiConsumer<? super Character, ? super Short> action) {
        /* template ForEach */
    }

    /* if !(obj key obj value) */
    @Override
    public void forEach(CharShortConsumer/*<super>*/ action) {
        /* template ForEach */
    }
    /* endif */

    @Override
    public boolean forEachWhile(/*f*/CharShortPredicate predicate) {
        /* template ForEachWhile */ throw new NotGenerated(); /* endtemplate */
    }

    @NotNull
    @Override
    public CharShortCursor/*<>*/ cursor() {
        /* if Mutable mutability //
        if (!noRemoved()) return new SomeRemovedMapCursor();
        // endif */
        return new NoRemovedMapCursor();
    }


    @Override
    public boolean containsAllEntries(Map<?, ?> m) {
        return CommonCharShortMapOps.containsAllEntries(this, m);
    }

    @Override
    public boolean allEntriesContainingIn(InternalCharShortMapOps/*<?>*/ m) {
        /* template AllContainingIn */ throw new NotGenerated(); /* endtemplate */
    }

    @Override
    public void reversePutAllTo(InternalCharShortMapOps/*<super>*/ m) {
        /* template ReversePutAllTo */
    }

    @Override
    @NotNull
    public HashObjSet<Map.Entry<Character, Short>> entrySet() {
        return new EntryView();
    }

    @Override
    @NotNull
    public ShortCollection/* if obj value //<V>// endif */ values() {
        return new ValueView();
    }


    @Override
    public boolean equals(Object o) {
        return CommonMapOps.equals(this, o);
    }

    @Override
    public int hashCode() {
        /* template SetHashCode */ throw new NotGenerated(); /* endtemplate */
    }

    @Override
    public String toString() {
        /* template ToString */ throw new NotGenerated(); /* endtemplate */
    }


    /* if !(obj key obj value) */
    @Override
    public Short put(Character key, Short value) {
        /* template Put with generic version */ throw new NotGenerated(); /* endtemplate */
    }
    /* endif */

    @Override
    public short put(char key, short value) {
        /* template Put */ throw new NotGenerated(); /* endtemplate */
    }

    /* if !(obj key obj value) */
    /* if JDK8 jdk */@Override/* endif */
    public Short putIfAbsent(Character key, Short value) {
        /* template PutIfAbsent with generic version */ throw new NotGenerated(); /* endtemplate */
    }
    /* endif */

    @Override
    public short putIfAbsent(char key, short value) {
        /* template PutIfAbsent */ throw new NotGenerated(); /* endtemplate */
    }

    @Override
    public void justPut(char key, short value) {
        /* template JustPut */
    }

    /* if obj key obj value || JDK8 jdk */@Override/* endif */
    public Short compute(Character key,
            BiFunction<? super Character, ? super Short, ? extends Short> remappingFunction) {
        /* template Compute with generic version */ throw new NotGenerated(); /* endtemplate */
    }

    /* if obj key */
    Short computeNullKey(
            BiFunction<? super Character, ? super Short, ? extends Short> remappingFunction) {
        /* template Compute with null key generic version */ throw new NotGenerated();
        /* endtemplate */
    }
    /* endif */

    /* if !(obj key obj value) */
    @Override
    public short compute(char key, /*f*/CharShortToShortFunction remappingFunction) {
        /* template Compute */ throw new NotGenerated(); /* endtemplate */
    }

    /* if obj key */
    short computeNullKey(/*f*/CharShortToShortFunction remappingFunction) {
        /* template Compute with null key */ throw new NotGenerated(); /* endtemplate */
    }
    /* endif */
    /* endif */

    /* if obj key obj value || JDK8 jdk */@Override/* endif */
    public Short computeIfAbsent(Character key,
            Function<? super Character, ? extends Short> mappingFunction) {
        /* template ComputeIfAbsent with generic version */ throw new NotGenerated();
        /* endtemplate */
    }

    /* if obj key */
    Short computeIfAbsentNullKey(
            Function<? super Character, ? extends Short> mappingFunction) {
        /* template ComputeIfAbsent with null key generic version */ throw new NotGenerated();
        /* endtemplate */
    }
    /* endif */

    /* if !(obj key obj value) */
    @Override
    public short computeIfAbsent(char key, /*f*/CharToShortFunction mappingFunction) {
        /* template ComputeIfAbsent */ throw new NotGenerated(); /* endtemplate */
    }

    /* if obj key */
    short computeIfAbsentNullKey(/*f*/CharToShortFunction mappingFunction) {
        /* template ComputeIfAbsent with null key */ throw new NotGenerated(); /* endtemplate */
    }
    /* endif */
    /* endif */

    /* if obj key obj value || JDK8 jdk */@Override/* endif */
    public Short computeIfPresent(Character key,
            BiFunction<? super Character, ? super Short, ? extends Short> remappingFunction) {
        /* template ComputeIfPresent with generic version */ throw new NotGenerated();
        /* endtemplate */
    }

    /* if !(obj key obj value) */
    @Override
    public short computeIfPresent(char key, /*f*/CharShortToShortFunction remappingFunction) {
        /* template ComputeIfPresent */ throw new NotGenerated(); /* endtemplate */
    }
    /* endif */

    /* if obj key obj value || JDK8 jdk */@Override/* endif */
    public Short merge(Character key, Short value,
            BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction) {
        /* template Merge with generic version */ throw new NotGenerated(); /* endtemplate */
    }

    /* if obj key */
    Short mergeNullKey(Short value,
            BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction) {
        /* template Merge with null key generic version */ throw new NotGenerated();
        /* endtemplate */
    }
    /* endif */

    /* if !(obj key obj value) */
    @Override
    public short merge(char key, short value, /*f*/ShortBinaryOperator remappingFunction) {
        /* template Merge */ throw new NotGenerated(); /* endtemplate */
    }

    /* if obj key */
    short mergeNullKey(short value, /*f*/ShortBinaryOperator remappingFunction) {
        /* template Merge with null key */ throw new NotGenerated(); /* endtemplate */
    }
    /* endif */
    /* endif */

    /* if !(obj value) */
    @Override
    public short incrementValue(char key, short value) {
        /* template IncrementValue */ throw new NotGenerated(); /* endtemplate */
    }

    @Override
    public short incrementValue(char key, short increment, short defaultValue) {
        /* template IncrementValueWithDefault */ throw new NotGenerated(); /* endtemplate */
    }
    /* endif */


    @Override
    public void putAll(@NotNull Map<? extends Character, ? extends Short> m) {
        CommonCharShortMapOps.putAll(this, m);
    }


    /* if obj key obj value || JDK8 jdk */@Override/* endif */
    public Short replace(Character key, Short value) {
        /* template Replace with generic version */ throw new NotGenerated(); /* endtemplate */
    }

    /* if !(obj key obj value) */
    @Override
    public short replace(char key, short value) {
        /* template Replace */ throw new NotGenerated(); /* endtemplate */
    }
    /* endif */

    /* if !(obj key obj value) */
    /* if JDK8 jdk */@Override/* endif */
    public boolean replace(Character key, Short oldValue, Short newValue) {
        return replace(key/* if !(obj key) */.charValue()/* endif */,
                oldValue/* if !(obj value) */.shortValue()/* endif */,
                newValue/* if !(obj value) */.shortValue()/* endif */);
    }
    /* endif */

    @Override
    public boolean replace(char key, short oldValue, short newValue) {
        /* template ReplaceEntry */ throw new NotGenerated(); /* endtemplate */
    }


    /* if obj key obj value || JDK8 jdk */@Override/* endif */
    public void replaceAll(
            BiFunction<? super Character, ? super Short, ? extends Short> function) {
        /* template ReplaceAll with generic version */ throw new NotGenerated(); /* endtemplate */
    }

    /* if !(obj key obj value) */
    @Override
    public void replaceAll(/*f*/CharShortToShortFunction function) {
        /* template ReplaceAll*/ throw new NotGenerated(); /* endtemplate */
    }
    /* endif */


    @Override
    public Short remove(Object key) {
        /* template Remove with generic version */ throw new NotGenerated(); /* endtemplate */
    }

    /* if obj key */
    Short removeNullKey() {
        /* template Remove with null key generic version */ throw new NotGenerated();
        /* endtemplate */
    }
    /* endif */

    @Override
    public boolean justRemove(/* raw */char key) {
        /* template JustRemove */ throw new NotGenerated(); /* endtemplate */
    }

    /* if obj key */
    boolean justRemoveNullKey() {
        /* template JustRemove with null key */ throw new NotGenerated(); /* endtemplate */
    }
    /* endif */


    /* define asValueSuffix */
    /* if obj key short|byte|char|int|long|float|double value //AsShort// endif */
    /* enddefine */

    /* if !(obj key obj value) */
    @Override
    public short remove/* asValueSuffix */(/* raw */char key) {
        /* template Remove with as suffix */ throw new NotGenerated(); /* endtemplate */
    }

    /* if obj key */
    short removeAsShortNullKey() {
        /* template Remove with null key */ throw new NotGenerated(); /* endtemplate */
    }
    /* endif */
    /* endif */


    /* if !(obj key obj value) */
    /* if JDK8 jdk */@Override/* endif */
    public boolean remove(Character key, Short value) {
        return remove(key/* if !(obj key) */.charValue()/* endif */,
                value/* if !(obj value) */.shortValue()/* endif */);
    }
    /* endif */

    @Override
    public boolean remove(/* raw */char key, /* raw */short value) {
        /* template RemoveEntry */ throw new NotGenerated(); /* endtemplate */
    }

    /* if obj key */
    boolean removeEntryNullKey(/* raw */short value) {
        /* template RemoveEntry with null key */ throw new NotGenerated(); /* endtemplate */
    }
    /* endif */

    @Override
    public boolean removeIf(/*f*/CharShortPredicate filter) {
        /* template RemoveIf */ throw new NotGenerated(); /* endtemplate */
    }


    /* with entry view */
    class EntryView extends AbstractSetView<Map.Entry<Character, Short>>
            implements HashObjSet<Map.Entry<Character, Short>>,
            InternalObjCollectionOps<Map.Entry<Character, Short>> {

        @Nullable
        @Override
        public Equivalence<Entry<Character, Short>> equivalence() {
            return Equivalence.entryEquivalence(
                    /* if !(obj key) */null/* elif obj key //keyEquivalence()// endif */,
                    /* if !(obj value) */null/* elif obj value //valueEquivalence()// endif */
            );
        }

        @Override
        public float loadFactor() {
            return MutableDHashCharShortMapGO.this.loadFactor();
        }


        @Override
        public int size() {
            return MutableDHashCharShortMapGO.this.size();
        }

        @Override
        public float currentLoad() {
            return MutableDHashCharShortMapGO.this.currentLoad();
        }


        @Override
        @SuppressWarnings("unchecked")
        public boolean contains(Object o) {
            try {
                Map.Entry<Character, Short> e = (Map.Entry<Character, Short>) o;
                return containsEntry(e.getKey(), e.getValue());
            } catch (NullPointerException e) {
                return false;
            } catch (ClassCastException e) {
                return false;
            }
        }


        @Override
        @NotNull
        public final Object[] toArray() {
            /* template ToArray */ throw new NotGenerated(); /* endtemplate */
        }

        @Override
        @SuppressWarnings("unchecked")
        @NotNull
        public final <T> T[] toArray(@NotNull T[] a) {
            /* template ToTypedArray */ throw new NotGenerated(); /* endtemplate */
        }

        @Override
        public final void forEach(Consumer<? super Map.Entry<Character, Short>> action) {
            /* template ForEach */ throw new NotGenerated(); /* endtemplate */
        }

        @Override
        public boolean forEachWhile(Predicate<? super Map.Entry<Character, Short>> predicate) {
            /* template ForEachWhile */ throw new NotGenerated(); /* endtemplate */
        }

        @Override
        @NotNull
        public Iterator<Map.Entry<Character, Short>> iterator() {
            /* if Mutable mutability //
            if (!noRemoved()) return new SomeRemovedEntryIterator();
            // endif */
            return new NoRemovedEntryIterator();
        }

        @NotNull
        @Override
        public ObjCursor<Map.Entry<Character, Short>> cursor() {
            /* if Mutable mutability //
            if (!noRemoved()) return new SomeRemovedEntryCursor();
            // endif */
            return new NoRemovedEntryCursor();
        }

        @Override
        public final boolean containsAll(@NotNull Collection<?> c) {
            return CommonObjCollectionOps.containsAll(this, c);
        }

        @Override
        public final boolean allContainingIn(ObjCollection<?> c) {
            /* template AllContainingIn */ throw new NotGenerated(); /* endtemplate */
        }

        @Override
        public boolean reverseRemoveAllFrom(ObjSet<?> s) {
            /* template ReverseRemoveAllFrom */ throw new NotGenerated(); /* endtemplate */
        }

        @Override
        public final boolean reverseAddAllTo(ObjCollection<? super Map.Entry<Character, Short>> c) {
            /* template ReverseAddAllTo */ throw new NotGenerated(); /* endtemplate */
        }


        public int hashCode() {
            return MutableDHashCharShortMapGO.this.hashCode();
        }

        @Override
        public String toString() {
            /* template ToString */ throw new NotGenerated(); /* endtemplate */
        }

        @Override
        public boolean shrink() {
            return MutableDHashCharShortMapGO.this.shrink();
        }


        @Override
        @SuppressWarnings("unchecked")
        public boolean remove(Object o) {
            try {
                Map.Entry<Character, Short> e = (Map.Entry<Character, Short>) o;
                char key = e.getKey();
                short value = e.getValue();
                return MutableDHashCharShortMapGO.this.remove(key, value);
            } catch (NullPointerException e) {
                return false;
            } catch (ClassCastException e) {
                return false;
            }
        }


        @Override
        public final boolean removeIf( Predicate<? super Map.Entry<Character, Short>> filter ) {
            /* template RemoveIf */ throw new NotGenerated(); /* endtemplate */
        }

        @Override
        public final boolean removeAll(@NotNull Collection<?> c) {
            if (c instanceof InternalObjCollectionOps) {
                InternalObjCollectionOps c2 = (InternalObjCollectionOps) c;
                if (NullableObjects.equals(this.equivalence(), c2.equivalence()) &&
                        c2.size() < this.size()) {
                    // noinspection unchecked
                    c2.reverseRemoveAllFrom(this);
                }
            }
            /* template RemoveAll */ throw new NotGenerated(); /* endtemplate */
        }

        @Override
        public final boolean retainAll(@NotNull Collection<?> c) {
            /* template RetainAll */ throw new NotGenerated(); /* endtemplate */
        }

        @Override
        public void clear() {
            MutableDHashCharShortMapGO.this.clear();
        }
    }
    /* endwith */


    abstract class CharShortEntry extends AbstractEntry<Character, Short> {
        abstract char key();

        @Override
        public final Character getKey() {
            return key();
        }

        abstract short value();

        @Override
        public final Short getValue() {
            return value();
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean equals(Object o) {
            Map.Entry e2;
            char k2;
            short v2;
            try {
                e2 = (Map.Entry) o;
                k2 = (Character) e2.getKey();
                v2 = (Short) e2.getValue();
                return /* if !(obj key) */key() == k2
                        /* elif obj key //nullableKeyEquals(key(), k2)// endif */
                        &&
                        /* if !(obj value) */value() == v2
                        /* elif obj value //nullableValueEquals(value(), v2)// endif */;
            } catch (ClassCastException e) {
                return false;
            } catch (NullPointerException e) {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return /* if !(obj key) */Primitives.hashCode(key())
                    /* elif obj key //nullableKeyHashCode(key())// endif */
                    ^
                    /* if !(obj value) */Primitives.hashCode(value())
                    /* elif obj value //nullableValueHashCode(value())// endif */;
        }


    }


    /* if Mutable mutability */
    class MutableEntry extends CharShortEntry {
        int modCount;
        private final int index;
        private final char key;
        private short value;

        MutableEntry(int modCount, int index, char key, short value) {
            this.modCount = modCount;
            this.index = index;
            this.key = key;
            this.value = value;
        }

        @Override
        public char key() {
            return key;
        }

        @Override
        public short value() {
            return value;
        }

        @Override
        public Short setValue(Short newValue) {
            if (modCount != modCount())
                throw new IllegalStateException();
            short oldValue = value;
            value = values[index] = newValue;
            return oldValue;
        }
    }


    /* elif Immutable mutability */
    private class ImmutableEntry extends CharShortEntry {
        private final char key;
        private final short value;

        ImmutableEntry(char key, short value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public char key() {
            return key;
        }

        @Override
        public short value() {
            return value;
        }

        @Override
        public Short setValue(Short newValue) {
            throw new UnsupportedOperationException();
        }
    }
    /* endif */


    class ReusableEntry extends CharShortEntry {
        private char key;
        private short value;

        ReusableEntry with(char key, short value) {
            this.key = key;
            this.value = value;
            return this;
        }

        @Override
        public char key() {
            return key;
        }

        @Override
        public short value() {
            return value;
        }

        @Override
        public Short setValue(Short value) {
            throw new UnsupportedOperationException();
        }
    }


    /* with value view */
    class ValueView extends AbstractShortValueView/*<>*/ {

        /* if obj value //
        @Override
        public Equivalence<Short> equivalence() {
            return valueEquivalence();
        }
        // endif */

        @Override
        public int size() {
            return MutableDHashCharShortMapGO.this.size();
        }

        @Override
        public boolean shrink() {
            return MutableDHashCharShortMapGO.this.shrink();
        }

        @Override
        public boolean contains(Object o) {
            return MutableDHashCharShortMapGO.this.containsValue(o);
        }

        /* if !(object value) */
        @Override
        public boolean contains(short v) {
            return MutableDHashCharShortMapGO.this.containsValue(v);
        }
        /* endif */


        /* if obj value || JDK8 jdk //@Override// endif */
        public void forEach(Consumer<? super Short> action) {
            /* template ForEach */
        }

        /* if !(obj value) */
        @Override
        public void forEach(ShortConsumer/*<super>*/ action) {
            /* template ForEach */
        }
        /* endif */

        @Override
        public boolean forEachWhile(/*f*/ShortPredicate predicate) {
            /* template ForEachWhile */ throw new NotGenerated(); /* endtemplate */
        }

        @Override
        public boolean allContainingIn(ShortCollection/*<?>*/ c) {
            /* template AllContainingIn */ throw new NotGenerated(); /* endtemplate */
        }

        @Override
        public boolean reverseAddAllTo(ShortCollection/*<super>*/ c) {
            /* template ReverseAddAllTo */ throw new NotGenerated(); /* endtemplate */
        }

        @Override
        public boolean reverseRemoveAllFrom(ShortSet/*<?>*/ s) {
            /* template ReverseRemoveAllFrom */ throw new NotGenerated(); /* endtemplate */
        }

        @Override
        @NotNull
        public ShortIterator/*<>*/ iterator() {
        /* if Mutable mutability //
        if (!noRemoved()) return new SomeRemovedValueIterator();
        // endif */
            return new NoRemovedValueIterator();
        }

        @NotNull
        @Override
        public ShortCursor/*<>*/ cursor() {
        /* if Mutable mutability //
        if (!noRemoved()) return new SomeRemovedValueCursor();
        // endif */
            return new NoRemovedValueCursor();
        }

        @Override
        @NotNull
        public Object[] toArray() {
            /* template ToArray with generic version */ throw new NotGenerated(); /* endtemplate */
        }

        @Override
        @SuppressWarnings("unchecked")
        @NotNull
        public <T> T[] toArray(@NotNull T[] a) {
            /* template ToTypedArray */ throw new NotGenerated(); /* endtemplate */
        }

        /* if !(obj value) */
        @Override
        public short[] toShortArray() {
            /* template ToArray */ throw new NotGenerated(); /* endtemplate */
        }

        @Override
        public short[] toArray(short[] a) {
            /* template ToPrimitiveArray */ throw new NotGenerated(); /* endtemplate */
        }
        /* endif */


        @Override
        public String toString() {
            /* template ToString */ throw new NotGenerated(); /* endtemplate */
        }


        @Override
        public boolean remove(Object o) {
            /* if !(obj value) */
            return removeShort(( Short ) o);
            /* elif obj value //
            return removeValue(o);
            // endif */
        }

        /* if !(obj value) */
        @Override
        public boolean removeShort(short v) {
            return removeValue(v);
        }
        /* endif */


        @Override
        public void clear() {
            MutableDHashCharShortMapGO.this.clear();
        }

        /* if obj value //@Override// endif */
        public boolean removeIf(Predicate<? super Short> filter) {
            /* template RemoveIf */ throw new NotGenerated(); /* endtemplate */
        }

        /* if !(obj value) */
        @Override
        public boolean removeIf(ShortPredicate filter) {
            /* template RemoveIf */ throw new NotGenerated(); /* endtemplate */
        }
        /* endif */

        @Override
        public boolean removeAll(@NotNull Collection<?> c) {
            /* if !(obj value) */
            if (c instanceof ShortCollection)
                return removeAll((ShortCollection) c);
            /* endif */
            /* template RemoveAll */ throw new NotGenerated(); /* endtemplate */
        }

        /* if !(obj value) */
        private boolean removeAll(ShortCollection c) {
            /* template RemoveAll */ throw new NotGenerated(); /* endtemplate */
        }
        /* endif */

        @Override
        public boolean retainAll(@NotNull Collection<?> c) {
            /* if !(obj value) && Mutable mutability */
            if (c instanceof ShortCollection)
                return retainAll((ShortCollection) c);
            /* endif */
            /* template RetainAll */ throw new NotGenerated(); /* endtemplate */
        }

        /* if !(obj value) && Mutable mutability */
        private boolean retainAll(ShortCollection c) {
            /* template RetainAll */ throw new NotGenerated(); /* endtemplate */
        }
        /* endif */
    }
    /* endwith */


    /* with entry view No|Some removed */
    /* if !(Immutable mutability Some removed) */

    class NoRemovedEntryIterator implements ObjIterator<Map.Entry<Character, Short>> {
        /* template Iterator.fields */

        NoRemovedEntryIterator() {
            /* template Iterator.constructor */
        }

        @Override
        public void forEachRemaining(Consumer<? super Map.Entry<Character, Short>> action) {
            /* template Iterator.forEachRemaining */ throw new NotGenerated(); /* endtemplate */
        }

        @Override
        public boolean hasNext() {
            /* template Iterator.hasNext */ throw new NotGenerated(); /* endtemplate */
        }

        @Override
        public Map.Entry<Character, Short> next() {
            /* template Iterator.next */ throw new NotGenerated(); /* endtemplate */
        }

        @Override
        public void remove() {
            /* template Iterator.remove */ throw new NotGenerated(); /* endtemplate */
        }
    }


    class NoRemovedEntryCursor implements ObjCursor<Map.Entry<Character, Short>> {
        /* template Cursor.fields */

        NoRemovedEntryCursor() {
            /* template Cursor.constructor */
        }

        @Override
        public void forEachForward(Consumer<? super Map.Entry<Character, Short>> action) {
            /* template Cursor.forEachForward */ throw new NotGenerated(); /* endtemplate */
        }

        @Override
        public Map.Entry<Character, Short> elem() {
            /* template Cursor.elem */ throw new NotGenerated(); /* endtemplate */
        }

        @Override
        public boolean moveNext() {
            /* template Cursor.moveNext */ throw new NotGenerated(); /* endtemplate */
        }

        @Override
        public void remove() {
            /* template Cursor.remove */ throw new NotGenerated(); /* endtemplate */
        }
    }

    /* endif */
    /* endwith */


    /* with value view No|Some removed */
    /* if !(Immutable mutability Some removed) */

    class NoRemovedValueIterator implements ShortIterator/*<>*/ {
        /* template Iterator.fields */

        NoRemovedValueIterator() {
            /* template Iterator.constructor */
        }

        /* if !(obj value) */
        @Override
        public short nextShort() {
            /* template Iterator.next */ throw new NotGenerated(); /* endtemplate */
        }
        /* endif */

        /* if obj value || JDK8 jdk //@Override// endif */
        public void forEachRemaining(Consumer<? super Short> action) {
            /* template Iterator.forEachRemaining */ throw new NotGenerated(); /* endtemplate */
        }

        /* if !(obj value) */
        @Override
        public void forEachRemaining(ShortConsumer action) {
            /* template Iterator.forEachRemaining */ throw new NotGenerated(); /* endtemplate */
        }
        /* endif */

        @Override
        public boolean hasNext() {
            /* template Iterator.hasNext */ throw new NotGenerated(); /* endtemplate */
        }

        @Override
        public Short next() {
            /* if !(obj value) */
            return nextShort();
            /* elif obj value */
            /* template Iterator.next */
            /* endif */
        }

        @Override
        public void remove() {
            /* template Iterator.remove */ throw new NotGenerated(); /* endtemplate */
        }
    }


    class NoRemovedValueCursor implements ShortCursor/*<>*/ {
        /* template Cursor.fields */

        NoRemovedValueCursor() {
            /* template Cursor.constructor */
        }

        @Override
        public void forEachForward(/*f*/ShortConsumer action) {
            /* template Cursor.forEachForward */ throw new NotGenerated(); /* endtemplate */
        }

        @Override
        public short elem() {
            /* template Cursor.elem */ throw new NotGenerated(); /* endtemplate */
        }

        @Override
        public boolean moveNext() {
            /* template Cursor.moveNext */ throw new NotGenerated(); /* endtemplate */
        }

        @Override
        public void remove() {
            /* template Cursor.remove */ throw new NotGenerated(); /* endtemplate */
        }
    }

    /* endif */
    /* endwith */

    /* with No|Some removed */
    /* if !(Immutable mutability Some removed) */

    class NoRemovedMapCursor implements CharShortCursor/*<>*/ {
        /* template Cursor.fields */

        NoRemovedMapCursor() {
            /* template Cursor.constructor */
        }

        @Override
        public void forEachForward(/*f*/CharShortConsumer action) {
            /* template Cursor.forEachForward */ throw new NotGenerated(); /* endtemplate */
        }

        @Override
        public char key() {
            /* template Cursor.key */ throw new NotGenerated(); /* endtemplate */
        }

        @Override
        public short value() {
            /* template Cursor.value */ throw new NotGenerated(); /* endtemplate */
        }


        @Override
        public void setValue(short value) {
            /* template Cursor.setValue */
        }

        @Override
        public boolean moveNext() {
            /* template Cursor.moveNext */ throw new NotGenerated(); /* endtemplate */
        }

        @Override
        public void remove() {
            /* template Cursor.remove */ throw new NotGenerated(); /* endtemplate */
        }
    }
    /* endif */
    /* endwith */
}
