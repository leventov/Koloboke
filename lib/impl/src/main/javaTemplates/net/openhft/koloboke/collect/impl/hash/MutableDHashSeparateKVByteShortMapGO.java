/* with
 DHash|QHash|LHash hash
 byte|char|short|int|long|float|double|obj key
 short|byte|char|int|long|float|double|obj value
 Mutable|Updatable|Immutable mutability
 Separate|Parallel kv
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

package net.openhft.koloboke.collect.impl.hash;

import net.openhft.koloboke.collect.*;
import net.openhft.koloboke.collect.hash.HashConfig;
import net.openhft.koloboke.collect.impl.*;
import net.openhft.koloboke.collect.map.*;
import net.openhft.koloboke.collect.map.hash.*;
import net.openhft.koloboke.collect.set.*;
import net.openhft.koloboke.collect.set.hash.*;
import net.openhft.koloboke.function.BiConsumer;
import net.openhft.koloboke.function.BiFunction;
import net.openhft.koloboke.function./*f*/BytePredicate/**/;
import net.openhft.koloboke.function./*f*/ByteShortConsumer/**/;
import net.openhft.koloboke.function./*f*/ByteShortPredicate/**/;
import net.openhft.koloboke.function./*f*/ByteShortToShortFunction/**/;
import net.openhft.koloboke.function./*f*/ByteToShortFunction/**/;
import net.openhft.koloboke.function.Consumer;
import net.openhft.koloboke.function.Function;
import net.openhft.koloboke.function.Predicate;
import net.openhft.koloboke.function./*f*/ShortBinaryOperator/**/;
import net.openhft.koloboke.function./*f*/ShortConsumer/**/;
import net.openhft.koloboke.function./*f*/ShortPredicate/**/;

import javax.annotation.Nonnull;
import java.util.*;


public class MutableDHashSeparateKVByteShortMapGO/*<>*/
        extends MutableDHashSeparateKVByteShortMapSO/*<>*/ {

    /* if Separate kv */@Override/* endif */
    final void copy(SeparateKVByteShortDHash hash) {
        int myMC = modCount(), hashMC = hash.modCount();
        super.copy(hash);
        if (myMC != modCount() || hashMC != hash.modCount())
            throw new ConcurrentModificationException();
    }

    /* if Separate kv */@Override/* endif */
    final void move(SeparateKVByteShortDHash hash) {
        int myMC = modCount(), hashMC = hash.modCount();
        super.move(hash);
        if (myMC != modCount() || hashMC != hash.modCount())
            throw new ConcurrentModificationException();
    }

    /* if obj value */
    @Override
    @Nonnull
    public Equivalence<Short> valueEquivalence() {
        return Equivalence.defaultEquality();
    }
    /* endif */

    /* if !(obj value) */
    @Override
    public short defaultValue() {
        return /* const value 0 */(short) 0/* endconst */;
    }
    /* endif */

    @Override
    public boolean containsEntry(/* raw */byte key, /* raw */short value) {
        /* template ContainsEntry */ throw new NotGenerated(); /* endtemplate */
    }

    /* if float|double key || float|double value */
    @Override
    public boolean containsEntry(/* bits *//* raw */byte key, /* bits *//* raw */short value) {
        /* template ContainsEntry with internal version */ throw new NotGenerated(); /*endtemplate*/
    }
    /* endif */

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
    public short get/* valueSuffix */(/* raw */byte key) {
        /* template Get */ throw new NotGenerated(); /* endtemplate */
    }

    /* if !(obj key obj value) */
    @Override
    public Short getOrDefault(Object key, Short defaultValue) {
        /* template GetOrDefault with generic version */ throw new NotGenerated(); /* endtemplate */
    }
    /* endif */

    @Override
    public short getOrDefault(/* raw */byte key, short defaultValue) {
        /* template GetOrDefault */ throw new NotGenerated(); /* endtemplate */
    }

    /* if obj key obj value || JDK8 jdk */@Override/* endif */
    public void forEach(BiConsumer<? super Byte, ? super Short> action) {
        /* template ForEach */
    }

    /* if !(obj key obj value) */
    @Override
    public void forEach(ByteShortConsumer/*<super>*/ action) {
        /* template ForEach */
    }
    /* endif */

    @Override
    public boolean forEachWhile(/*f*/ByteShortPredicate predicate) {
        /* template ForEachWhile */ throw new NotGenerated(); /* endtemplate */
    }

    @Nonnull
    @Override
    public ByteShortCursor/*<>*/ cursor() {
        /* if !(Immutable mutability) */int mc = modCount();/* endif */
        /* if Mutable mutability && !(LHash hash) //
        if (!noRemoved())
            return new SomeRemovedMapCursor(// if !(Immutable mutability) //mc// endif //);
        // endif */
        return new NoRemovedMapCursor(/* if !(Immutable mutability) */mc/* endif */);
    }


    @Override
    public boolean containsAllEntries(Map<?, ?> m) {
        return CommonByteShortMapOps.containsAllEntries(this, m);
    }

    @Override
    public boolean allEntriesContainingIn(InternalByteShortMapOps/*<?>*/ m) {
        /* template AllContainingIn with internal version */ throw new NotGenerated();
        /* endtemplate */
    }

    @Override
    public void reversePutAllTo(InternalByteShortMapOps/*<super>*/ m) {
        /* template ReversePutAllTo with internal version */
    }

    @Override
    @Nonnull
    public HashObjSet<Map.Entry<Byte, Short>> entrySet() {
        return new EntryView();
    }

    @Override
    @Nonnull
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


    /* if !(Immutable mutability) */
    @Override
    void rehash(int newCapacity) {
        /* template Rehash */
    }
    /* endif */


    /* if !(obj key obj value) */
    @Override
    public Short put(Byte key, Short value) {
        /* template Put with generic version */ throw new NotGenerated(); /* endtemplate */
    }
    /* endif */

    @Override
    public short put(byte key, short value) {
        /* template Put */ throw new NotGenerated(); /* endtemplate */
    }

    /* if !(obj key obj value) */
    @Override
    public Short putIfAbsent(Byte key, Short value) {
        /* template PutIfAbsent with generic version */ throw new NotGenerated(); /* endtemplate */
    }
    /* endif */

    @Override
    public short putIfAbsent(byte key, short value) {
        /* template PutIfAbsent */ throw new NotGenerated(); /* endtemplate */
    }

    @Override
    public void justPut(byte key, short value) {
        /* template JustPut */
    }

    /* if float|double key || float|double value */
    @Override
    public void justPut(/* bits */byte key, /* bits */short value) {
        /* template JustPut with internal version */
    }
    /* endif */

    /* if obj key obj value || JDK8 jdk */@Override/* endif */
    public Short compute(Byte key,
            BiFunction<? super Byte, ? super Short, ? extends Short> remappingFunction) {
        /* template Compute with generic version */ throw new NotGenerated(); /* endtemplate */
    }

    /* if obj key */
    Short computeNullKey(
            BiFunction<? super Byte, ? super Short, ? extends Short> remappingFunction) {
        /* template Compute with null key generic version */ throw new NotGenerated();
        /* endtemplate */
    }
    /* endif */

    /* if !(obj key obj value) */
    @Override
    public short compute(byte key, /*f*/ByteShortToShortFunction remappingFunction) {
        /* template Compute */ throw new NotGenerated(); /* endtemplate */
    }

    /* if obj key */
    short computeNullKey(/*f*/ByteShortToShortFunction remappingFunction) {
        /* template Compute with null key */ throw new NotGenerated(); /* endtemplate */
    }
    /* endif */
    /* endif */

    /* if obj key obj value || JDK8 jdk */@Override/* endif */
    public Short computeIfAbsent(Byte key,
            Function<? super Byte, ? extends Short> mappingFunction) {
        /* template ComputeIfAbsent with generic version */ throw new NotGenerated();
        /* endtemplate */
    }

    /* if obj key */
    Short computeIfAbsentNullKey(
            Function<? super Byte, ? extends Short> mappingFunction) {
        /* template ComputeIfAbsent with null key generic version */ throw new NotGenerated();
        /* endtemplate */
    }
    /* endif */

    /* if !(obj key obj value) */
    @Override
    public short computeIfAbsent(byte key, /*f*/ByteToShortFunction mappingFunction) {
        /* template ComputeIfAbsent */ throw new NotGenerated(); /* endtemplate */
    }

    /* if obj key */
    short computeIfAbsentNullKey(/*f*/ByteToShortFunction mappingFunction) {
        /* template ComputeIfAbsent with null key */ throw new NotGenerated(); /* endtemplate */
    }
    /* endif */
    /* endif */

    /* if obj key obj value || JDK8 jdk */@Override/* endif */
    public Short computeIfPresent(Byte key,
            BiFunction<? super Byte, ? super Short, ? extends Short> remappingFunction) {
        /* template ComputeIfPresent with generic version */ throw new NotGenerated();
        /* endtemplate */
    }

    /* if !(obj key obj value) */
    @Override
    public short computeIfPresent(byte key, /*f*/ByteShortToShortFunction remappingFunction) {
        /* template ComputeIfPresent */ throw new NotGenerated(); /* endtemplate */
    }
    /* endif */

    /* if obj key obj value || JDK8 jdk */@Override/* endif */
    public Short merge(Byte key, Short value,
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
    public short merge(byte key, short value, /*f*/ShortBinaryOperator remappingFunction) {
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
    public short addValue(byte key, short value) {
        /* template AddValue */ throw new NotGenerated(); /* endtemplate */
    }

    /* if obj key */
    short addValueNullKey(short value) {
        /* template AddValue with null key */ throw new NotGenerated(); /* endtemplate */
    }
    /* endif */

    @Override
    public short addValue(byte key, short addition, short defaultValue) {
        /* template AddValueWithDefault */ throw new NotGenerated(); /* endtemplate */
    }

    /* if obj key */
    short addValueWithDefaultNullKey(short addition, short defaultValue) {
        /* template AddValueWithDefault with null key */ throw new NotGenerated(); /* endtemplate */
    }
    /* endif */
    /* endif */


    @Override
    public void putAll(@Nonnull Map<? extends Byte, ? extends Short> m) {
        CommonByteShortMapOps.putAll(this, m);
    }


    @Override
    public Short replace(Byte key, Short value) {
        /* template Replace with generic version */ throw new NotGenerated(); /* endtemplate */
    }

    /* if !(obj key obj value) */
    @Override
    public short replace(byte key, short value) {
        /* template Replace */ throw new NotGenerated(); /* endtemplate */
    }
    /* endif */

    /* if !(obj key obj value) */
    @Override
    public boolean replace(Byte key, Short oldValue, Short newValue) {
        return replace(key/* if !(obj key) */.byteValue()/* endif */,
                oldValue/* if !(obj value) */.shortValue()/* endif */,
                newValue/* if !(obj value) */.shortValue()/* endif */);
    }
    /* endif */

    @Override
    public boolean replace(byte key, short oldValue, short newValue) {
        /* template ReplaceEntry */ throw new NotGenerated(); /* endtemplate */
    }


    /* if obj key obj value || JDK8 jdk */@Override/* endif */
    public void replaceAll(
            BiFunction<? super Byte, ? super Short, ? extends Short> function) {
        /* template ReplaceAll with generic version */ throw new NotGenerated(); /* endtemplate */
    }

    /* if !(obj key obj value) */
    @Override
    public void replaceAll(/*f*/ByteShortToShortFunction function) {
        /* template ReplaceAll*/ throw new NotGenerated(); /* endtemplate */
    }
    /* endif */


    /* if !(Immutable mutability) */
    @Override
    public void clear() {
        int mc = modCount() + 1;
        super.clear();
        if (mc != modCount())
            throw new ConcurrentModificationException();
    }
    /* endif */


    /* if Mutable mutability */
    @Override
    void removeAt(int index) {
        // if !(LHash hash) */
        incrementModCount();
        super.removeAt(index);
        /* if Separate kv obj value */
        values[index] = null;
        /* elif Parallel kv obj value */
        table[index + 1] = null;
        /* endif */
        postRemoveHook();
        /* elif LHash hash //
        /* template LHashRemoveAt */
        // endif */
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
    public boolean justRemove(/* raw */byte key) {
        /* template JustRemove */ throw new NotGenerated(); /* endtemplate */
    }

    /* if float|double key */
    @Override
    public boolean justRemove(/* bits */byte key) {
        /* template JustRemove with internal version */ throw new NotGenerated(); /* endtemplate */
    }
    /* elif obj key */
    boolean justRemoveNullKey() {
        /* template JustRemove with null key */ throw new NotGenerated(); /* endtemplate */
    }
    /* endif */


    /* define asValueSuffix */
    /* if obj key short|byte|char|int|long|float|double value //AsShort// endif */
    /* enddefine */

    /* if !(obj key obj value) */
    @Override
    public short remove/* asValueSuffix */(/* raw */byte key) {
        /* template Remove with as suffix */ throw new NotGenerated(); /* endtemplate */
    }

    /* if obj key */
    short removeAsShortNullKey() {
        /* template Remove with null key */ throw new NotGenerated(); /* endtemplate */
    }
    /* endif */
    /* endif */


    /* if !(obj key obj value) */
    @Override
    public boolean remove(Object key, Object value) {
        return remove(/* if !(obj key) */((Byte) key).byteValue()/* elif obj key //key// endif */,
                /* if !(obj value) */((Short) value).shortValue()
                /* elif obj value //value// endif */);
    }
    /* endif */

    @Override
    public boolean remove(/* raw */byte key, /* raw */short value) {
        /* template RemoveEntry */ throw new NotGenerated(); /* endtemplate */
    }

    /* if obj key */
    boolean removeEntryNullKey(/* raw */short value) {
        /* template RemoveEntry with null key */ throw new NotGenerated(); /* endtemplate */
    }
    /* endif */

    @Override
    public boolean removeIf(/*f*/ByteShortPredicate filter) {
        /* template RemoveIf */ throw new NotGenerated(); /* endtemplate */
    }



    /* if Mutable mutability obj value || Mutable mutability LHash hash */
    /* with key view */

    // under this condition - operations, overridden from MutableSeparateKVByteDHashGO
    // when values are objects - in order to set values to null on removing (for garbage collection)
    // when algo is LHash - because shift deletion should shift values to

    @Override
    public boolean removeIf(Predicate<? super Byte> filter) {
        /* template RemoveIf */ throw new NotGenerated(); /* endtemplate */
    }

    /* if !(obj key) */
    @Override
    public boolean removeIf(BytePredicate filter) {
        /* template RemoveIf */ throw new NotGenerated(); /* endtemplate */
    }
    /* endif */

    @Override
    public boolean removeAll(@Nonnull HashByteSet/*<>*/ thisC, @Nonnull Collection<?> c) {
        /* template RemoveAll with given this */ throw new NotGenerated(); /* endtemplate */
    }

    /* if !(obj key) */
    @Override
    boolean removeAll(@Nonnull HashByteSet/*<>*/ thisC, @Nonnull ByteCollection c) {
        /* template RemoveAll with given this */ throw new NotGenerated(); /* endtemplate */
    }

    /* if float|double key */
    @Override
    boolean removeAll(@Nonnull HashByteSet/*<>*/ thisC, @Nonnull InternalByteCollectionOps c) {
        /* template RemoveAll with internal version given this */ throw new NotGenerated();
        /* endtemplate */
    }
    /* endif */
    /* endif */

    @Override
    public boolean retainAll(@Nonnull HashByteSet/*<>*/ thisC, @Nonnull Collection<?> c) {
        /* if !(obj key) */
        if (c instanceof ByteCollection)
            return retainAll(thisC, (ByteCollection) c);
        /* endif */
        /* template RetainAll with generic version given this */ throw new NotGenerated();
        /* endtemplate */
    }

    /* if !(obj key) */
    private boolean retainAll(@Nonnull HashByteSet/*<>*/ thisC, @Nonnull ByteCollection c) {
        /* template RetainAll with given this */ throw new NotGenerated(); /* endtemplate */
    }

    /* if float|double key */
    private boolean retainAll(@Nonnull HashByteSet/*<>*/ thisC,
            @Nonnull InternalByteCollectionOps c) {
        /* template RetainAll with internal version given this */ throw new NotGenerated();
        /* endtemplate */
    }
    /* endif */
    /* endif */

    /* if LHash hash */
    @Override
    void closeDelayedRemoved(int firstDelayedRemoved
            /* if !(obj|float|double key) */, /* bits */byte delayedRemoved/* endif */) {
        /* template LHashCloseDelayedRemoved */ throw new NotGenerated(); /* endtemplate */
    }
    /* endif */



    @Override
    public ByteIterator/*<>*/ iterator() {
        /* if !(Immutable mutability) */int mc = modCount();/* endif */
        /* if !(LHash hash) //
        if (!noRemoved())
            return new SomeRemovedKeyIterator(// if !(Immutable mutability) //mc// endif //);
        // endif */
        return new NoRemovedKeyIterator(/* if !(Immutable mutability) */mc/* endif */);
    }

    @Override
    public ByteCursor/*<>*/ setCursor() {
        /* if !(Immutable mutability) */int mc = modCount();/* endif */
        /* if !(LHash hash) //
        if (!noRemoved())
            return new SomeRemovedKeyCursor(// if !(Immutable mutability) //mc// endif //);
        // endif */
        return new NoRemovedKeyCursor(/* if !(Immutable mutability) */mc/* endif */);
    }

    /* with No|Some removed */
    /* if !(LHash hash Some removed) */

    class NoRemovedKeyIterator extends NoRemovedIterator {
        /* if CommentOn hash */
        // vals non-final because could be updated in shift-removing procedure
        /* endif */
        /* if Separate kv */
        /* if !(LHash hash) */final/* endif */ /* bits */short[] vals;
        /* endif */

        private NoRemovedKeyIterator(/* if !(Immutable mutability) */int mc/* endif */) {
            super(mc);
            /* if Separate kv */vals = values;/* endif */
        }

        @Override
        public void remove() {
            /* template Iterator.remove */ throw new NotGenerated(); /* endtemplate */
        }
    }


    class NoRemovedKeyCursor extends NoRemovedCursor {
        /* if Separate kv */
        /* if !(LHash hash) */final/* endif */ /* bits */short[] vals;
        /* endif */

        private NoRemovedKeyCursor(/* if !(Immutable mutability) */int mc/* endif */) {
            super(mc);
            /* if Separate kv */vals = values;/* endif */
        }

        @Override
        public void remove() {
            /* template Cursor.remove */ throw new NotGenerated(); /* endtemplate */
        }
    }

    /* endif */
    /* endwith */

    /* endwith */
    /* endif */



    /* with entry view */
    class EntryView extends AbstractSetView<Map.Entry<Byte, Short>>
            implements HashObjSet<Map.Entry<Byte, Short>>,
            InternalObjCollectionOps<Map.Entry<Byte, Short>> {

        @Nonnull
        @Override
        public Equivalence<Entry<Byte, Short>> equivalence() {
            return Equivalence.entryEquivalence(
                    /* if !(obj key) */Equivalence.<Byte>defaultEquality()
                    /* elif obj key //keyEquivalence()// endif */,
                    /* if !(obj value) */Equivalence.<Short>defaultEquality()
                    /* elif obj value //valueEquivalence()// endif */
            );
        }

        @Nonnull
        @Override
        public HashConfig hashConfig() {
            return MutableDHashSeparateKVByteShortMapGO.this.hashConfig();
        }


        @Override
        public int size() {
            return MutableDHashSeparateKVByteShortMapGO.this.size();
        }

        @Override
        public double currentLoad() {
            return MutableDHashSeparateKVByteShortMapGO.this.currentLoad();
        }


        @Override
        @SuppressWarnings("unchecked")
        public boolean contains(Object o) {
            try {
                Map.Entry<Byte, Short> e = (Map.Entry<Byte, Short>) o;
                return containsEntry(e.getKey(), e.getValue());
            } catch (NullPointerException e) {
                return false;
            } catch (ClassCastException e) {
                return false;
            }
        }


        @Override
        @Nonnull
        public final Object[] toArray() {
            /* template ToArray */ throw new NotGenerated(); /* endtemplate */
        }

        @Override
        @SuppressWarnings("unchecked")
        @Nonnull
        public final <T> T[] toArray(@Nonnull T[] a) {
            /* template ToTypedArray */ throw new NotGenerated(); /* endtemplate */
        }

        @Override
        public final void forEach(@Nonnull Consumer<? super Map.Entry<Byte, Short>> action) {
            /* template ForEach */ throw new NotGenerated(); /* endtemplate */
        }

        @Override
        public boolean forEachWhile(@Nonnull  Predicate<? super Map.Entry<Byte, Short>> predicate) {
            /* template ForEachWhile */ throw new NotGenerated(); /* endtemplate */
        }

        @Override
        @Nonnull
        public ObjIterator<Map.Entry<Byte, Short>> iterator() {
            /* if !(Immutable mutability) */int mc = modCount();/* endif */
            /* if Mutable mutability && !(LHash hash) //
            if (!noRemoved())
                return new SomeRemovedEntryIterator(// if !(Immutable mutability) //mc// endif //);
            // endif */
            return new NoRemovedEntryIterator(/* if !(Immutable mutability) */mc/* endif */);
        }

        @Nonnull
        @Override
        public ObjCursor<Map.Entry<Byte, Short>> cursor() {
            /* if !(Immutable mutability) */int mc = modCount();/* endif */
            /* if Mutable mutability && !(LHash hash) //
            if (!noRemoved())
                return new SomeRemovedEntryCursor(// if !(Immutable mutability) //mc// endif //);
            // endif */
            return new NoRemovedEntryCursor(/* if !(Immutable mutability) */mc/* endif */);
        }

        @Override
        public final boolean containsAll(@Nonnull Collection<?> c) {
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
        public final boolean reverseAddAllTo(ObjCollection<? super Map.Entry<Byte, Short>> c) {
            /* template ReverseAddAllTo */ throw new NotGenerated(); /* endtemplate */
        }


        public int hashCode() {
            return MutableDHashSeparateKVByteShortMapGO.this.hashCode();
        }

        @Override
        public String toString() {
            /* template ToString */ throw new NotGenerated(); /* endtemplate */
        }

        @Override
        public boolean shrink() {
            return MutableDHashSeparateKVByteShortMapGO.this.shrink();
        }


        @Override
        @SuppressWarnings("unchecked")
        public boolean remove(Object o) {
            try {
                Map.Entry<Byte, Short> e = (Map.Entry<Byte, Short>) o;
                byte key = e.getKey();
                short value = e.getValue();
                return MutableDHashSeparateKVByteShortMapGO.this.remove(key, value);
            } catch (NullPointerException e) {
                return false;
            } catch (ClassCastException e) {
                return false;
            }
        }


        @Override
        public final boolean removeIf(@Nonnull Predicate<? super Map.Entry<Byte, Short>> filter) {
            /* template RemoveIf */ throw new NotGenerated(); /* endtemplate */
        }

        @Override
        public final boolean removeAll(@Nonnull Collection<?> c) {
            if (c instanceof InternalObjCollectionOps) {
                InternalObjCollectionOps c2 = (InternalObjCollectionOps) c;
                if (equivalence().equals(c2.equivalence()) && c2.size() < this.size()) {
                    // noinspection unchecked
                    c2.reverseRemoveAllFrom(this);
                }
            }
            /* template RemoveAll */ throw new NotGenerated(); /* endtemplate */
        }

        @Override
        public final boolean retainAll(@Nonnull Collection<?> c) {
            /* template RetainAll */ throw new NotGenerated(); /* endtemplate */
        }

        @Override
        public void clear() {
            MutableDHashSeparateKVByteShortMapGO.this.clear();
        }
    }
    /* endwith */


    abstract class ByteShortEntry extends AbstractEntry<Byte, Short> {

        abstract /* bits */byte key();

        @Override
        public final Byte getKey() {
            return /* wrap key */key();
        }

        abstract /* bits */short value();

        @Override
        public final Short getValue() {
            return /* wrap value */value();
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean equals(Object o) {
            Map.Entry e2;
            /* bits */byte k2;
            /* bits */short v2;
            try {
                e2 = (Map.Entry) o;
                k2 = /* unwrap key */(Byte) e2.getKey()/**/;
                v2 = /* unwrap value */(Short) e2.getValue()/**/;
                return /* if !(obj key) */key() == k2
                        /* elif obj key //
                        // if false nullKeyAllowed //
                        keyEquals(key(), k2)
                        // elif true nullKeyAllowed //
                        nullableKeyEquals(key(), k2)
                        // endif //
                        // endif */
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


    /* if !(Immutable mutability) */
    /* with Mutable mutability */
    class MutableEntry extends ByteShortEntry {
        final int modCount;
        private final int index;
        final /* bits */byte key;
        private /* bits */short value;

        MutableEntry(int modCount, int index, /* bits */byte key, /* bits */short value) {
            this.modCount = modCount;
            this.index = index;
            this.key = key;
            this.value = value;
        }

        @Override
        public /* bits */byte key() {
            return key;
        }

        @Override
        public /* bits */short value() {
            return value;
        }

        @Override
        public Short setValue(Short newValue) {
            if (modCount != modCount())
                throw new IllegalStateException();
            short oldValue = /* wrap value */value;
            /* bits */short unwrappedNewValue = /* unwrap value */newValue;
            value = unwrappedNewValue;
            updateValueInTable(unwrappedNewValue);
            return oldValue;
        }

        void updateValueInTable(/* bits */short newValue) {
            /* if Separate kv */
            values[index] = newValue;
            /* elif Parallel kv */
            /* if !(long|double|obj value) */
            U./* if !(float key) */putShort/* elif float key //putInt// endif */(
                    table, CHAR_BASE + SHORT_VALUE_OFFSET + (((long) index) << CHAR_SCALE_SHIFT),
                    newValue);
            /* elif long|double|obj value */
            table[index + 1] = newValue;
            /* endif */
            /* endif */
        }
    }
    /* endwith */

    /* elif Immutable mutability */
    private class ImmutableEntry extends ByteShortEntry {
        private final /* bits */byte key;
        private final /* bits */short value;

        ImmutableEntry(/* bits */byte key, /* bits */short value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public /* bits */byte key() {
            return key;
        }

        @Override
        public /* bits */short value() {
            return value;
        }
    }
    /* endif */


    class ReusableEntry extends ByteShortEntry {
        private /* bits */byte key;
        private /* bits */short value;

        ReusableEntry with(/* bits */byte key, /* bits */short value) {
            this.key = key;
            this.value = value;
            return this;
        }

        @Override
        public /* bits */byte key() {
            return key;
        }

        @Override
        public /* bits */short value() {
            return value;
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
            return MutableDHashSeparateKVByteShortMapGO.this.size();
        }

        @Override
        public boolean shrink() {
            return MutableDHashSeparateKVByteShortMapGO.this.shrink();
        }

        @Override
        public boolean contains(Object o) {
            return MutableDHashSeparateKVByteShortMapGO.this.containsValue(o);
        }

        /* if !(obj value) */
        @Override
        public boolean contains(short v) {
            return MutableDHashSeparateKVByteShortMapGO.this.containsValue(v);
        }

        /* if float|double value */
        @Override
        public boolean contains(/* bits */short bits) {
            return MutableDHashSeparateKVByteShortMapGO.this.containsValue(bits);
        }
        /* endif */
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

        /* if float|double value */
        private boolean allContainingIn(InternalShortCollectionOps c) {
            /* template AllContainingIn with internal version */ throw new NotGenerated();
            /* endtemplate */
        }
        /* endif */

        @Override
        public boolean reverseAddAllTo(ShortCollection/*<super>*/ c) {
            /* template ReverseAddAllTo */ throw new NotGenerated(); /* endtemplate */
        }

        /* if float|double value */
        private boolean reverseAddAllTo(InternalShortCollectionOps c) {
            /* template ReverseAddAllTo with internal version */ throw new NotGenerated();
            /* endtemplate */
        }
        /* endif */

        @Override
        public boolean reverseRemoveAllFrom(ShortSet/*<?>*/ s) {
            /* template ReverseRemoveAllFrom */ throw new NotGenerated(); /* endtemplate */
        }

        /* if float|double value */
        private boolean reverseRemoveAllFrom(InternalShortCollectionOps s) {
            /* template ReverseRemoveAllFrom with internal version */ throw new NotGenerated();
            /* endtemplate */
        }
        /* endif */


        @Override
        @Nonnull
        public ShortIterator/*<>*/ iterator() {
            /* if !(Immutable mutability) */int mc = modCount();/* endif */
            /* if Mutable mutability && !(LHash hash) //
            if (!noRemoved())
                return new SomeRemovedValueIterator(// if !(Immutable mutability) //mc// endif //);
            // endif */
            return new NoRemovedValueIterator(/* if !(Immutable mutability) */mc/* endif */);
        }

        @Nonnull
        @Override
        public ShortCursor/*<>*/ cursor() {
            /* if !(Immutable mutability) */int mc = modCount();/* endif */
            /* if Mutable mutability && !(LHash hash) //
            if (!noRemoved())
                return new SomeRemovedValueCursor(// if !(Immutable mutability) //mc// endif //);
            // endif */
            return new NoRemovedValueCursor(/* if !(Immutable mutability) */mc/* endif */);
        }

        @Override
        @Nonnull
        public Object[] toArray() {
            /* template ToArray with generic version */ throw new NotGenerated(); /* endtemplate */
        }

        @Override
        @SuppressWarnings("unchecked")
        @Nonnull
        public <T> T[] toArray(@Nonnull T[] a) {
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

        /* if float|double value */
        @Override
        public boolean removeShort(/* bits */short bits) {
            return removeValue(bits);
        }
        /* endif */
        /* endif */


        @Override
        public void clear() {
            MutableDHashSeparateKVByteShortMapGO.this.clear();
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
        public boolean removeAll(@Nonnull Collection<?> c) {
            /* if !(obj value) && Mutable mutability */
            if (c instanceof ShortCollection)
                return removeAll((ShortCollection) c);
            /* endif */
            /* template RemoveAll with generic version */ throw new NotGenerated(); /* endtemplate*/
        }

        /* if !(obj value) && Mutable mutability */
        private boolean removeAll(ShortCollection c) {
            /* template RemoveAll */ throw new NotGenerated(); /* endtemplate */
        }

        /* if float|double value */
        private boolean removeAll(InternalShortCollectionOps c) {
            /* template RemoveAll with internal version */ throw new NotGenerated(); /*endtemplate*/
        }
        /* endif */
        /* endif */

        @Override
        public boolean retainAll(@Nonnull Collection<?> c) {
            /* if !(obj value) && Mutable mutability */
            if (c instanceof ShortCollection)
                return retainAll((ShortCollection) c);
            /* endif */
            /* template RetainAll with generic version */ throw new NotGenerated(); /* endtemplate*/
        }

        /* if !(obj value) && Mutable mutability */
        private boolean retainAll(ShortCollection c) {
            /* template RetainAll */ throw new NotGenerated(); /* endtemplate */
        }

        /* if float|double value */
        private boolean retainAll(InternalShortCollectionOps c) {
            /* template RetainAll with internal version */ throw new NotGenerated(); /*endtemplate*/
        }
        /* endif */
        /* endif */
    }
    /* endwith */


    /* with entry view No|Some removed */
    /* if !(Updatable|Immutable mutability Some removed) && !(LHash hash Some removed) */

    class NoRemovedEntryIterator implements ObjIterator<Map.Entry<Byte, Short>> {
        /* template Iterator.fields */

        NoRemovedEntryIterator(/* if !(Immutable mutability) */int mc/* endif */) {
            /* template Iterator.constructor */
        }

        @Override
        public void forEachRemaining(@Nonnull Consumer<? super Map.Entry<Byte, Short>> action) {
            /* template Iterator.forEachRemaining */ throw new NotGenerated(); /* endtemplate */
        }

        @Override
        public boolean hasNext() {
            /* template Iterator.hasNext */ throw new NotGenerated(); /* endtemplate */
        }

        @Override
        public Map.Entry<Byte, Short> next() {
            /* template Iterator.next */ throw new NotGenerated(); /* endtemplate */
        }

        @Override
        public void remove() {
            /* template Iterator.remove */ throw new NotGenerated(); /* endtemplate */
        }
    }


    class NoRemovedEntryCursor implements ObjCursor<Map.Entry<Byte, Short>> {
        /* template Cursor.fields */

        NoRemovedEntryCursor(/* if !(Immutable mutability) */int mc/* endif */) {
            /* template Cursor.constructor */
        }

        @Override
        public void forEachForward(Consumer<? super Map.Entry<Byte, Short>> action) {
            /* template Cursor.forEachForward */ throw new NotGenerated(); /* endtemplate */
        }

        @Override
        public Map.Entry<Byte, Short> elem() {
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
    /* if !(Updatable|Immutable mutability Some removed) && !(LHash hash Some removed) */

    class NoRemovedValueIterator implements ShortIterator/*<>*/ {
        /* template Iterator.fields */

        NoRemovedValueIterator(/* if !(Immutable mutability) */int mc/* endif */) {
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

        NoRemovedValueCursor(/* if !(Immutable mutability) */int mc/* endif */) {
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
    /* if !(Updatable|Immutable mutability Some removed) && !(LHash hash Some removed) */

    class NoRemovedMapCursor implements ByteShortCursor/*<>*/ {
        /* template Cursor.fields */

        NoRemovedMapCursor(/* if !(Immutable mutability) */int mc/* endif */) {
            /* template Cursor.constructor */
        }

        @Override
        public void forEachForward(/*f*/ByteShortConsumer action) {
            /* template Cursor.forEachForward */ throw new NotGenerated(); /* endtemplate */
        }

        @Override
        public byte key() {
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
