/* with
 DHash|QHash|LHash hash
 byte|char|short|int|long|float|double|obj key
 short|byte|char|int|long|float|double|obj value
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

package net.openhft.collect.impl.hash;

import net.openhft.collect.*;
import net.openhft.collect.hash.*;
import net.openhft.collect.impl.*;
import net.openhft.collect.map.hash.HashByteShortMapFactory;
import net.openhft.function./*f*/ByteShortConsumer/**/;
import net.openhft.function.Predicate;
import net.openhft.collect.map.hash.HashByteShortMap;

import javax.annotation.Nonnull;
import java.util.*;

import static net.openhft.collect.impl.Containers.sizeAsInt;
import static net.openhft.collect.impl.hash.LHashCapacities.configIsSuitableForMutableLHash;


public abstract class DHashSeparateKVByteShortMapFactoryGO/*<>*/
        extends DHashSeparateKVByteShortMapFactorySO/*<>*/ {

    DHashSeparateKVByteShortMapFactoryGO(HashConfig hashConf, int defaultExpectedSize
            /* if obj key */, boolean isNullKeyAllowed
            /* elif !(float|double key) */, byte lower, byte upper/* endif */) {
        super(hashConf, defaultExpectedSize/* if obj key //, isNullKeyAllowed
            // elif !(float|double key) */, lower, upper/* endif */);
    }

    /* define commonArgDef //
    HashConfig hashConf, int defaultExpectedSize// if obj key //, boolean isNullKeyAllowed
            // elif !(float|double key) //, byte lower, byte upper// endif //
    // enddefine */

    abstract HashByteShortMapFactory/*<>*/ thisWith(/* commonArgDef */);

    abstract HashByteShortMapFactory/*<>*/ lHashLikeThisWith(/* commonArgDef */);

    /* with DHash|QHash hash */
    abstract HashByteShortMapFactory/*<>*/ dHashLikeThisWith(/* commonArgDef */);
    /* endwith */

    @Override
    public final HashByteShortMapFactory/*<>*/ withHashConfig(@Nonnull HashConfig hashConf) {
        if (configIsSuitableForMutableLHash(hashConf))
            return lHashLikeThisWith(hashConf, getDefaultExpectedSize()
            /* if obj key */, isNullKeyAllowed()/* elif !(float|double key) */
                    , getLowerKeyDomainBound(), getUpperKeyDomainBound()/* endif */);
        /* with DHash|QHash hash */
        return dHashLikeThisWith(hashConf, getDefaultExpectedSize()
            /* if obj key */, isNullKeyAllowed()/* elif !(float|double key) */
                , getLowerKeyDomainBound(), getUpperKeyDomainBound()/* endif */);
        /* endwith */
    }

    @Override
    public final HashByteShortMapFactory/*<>*/ withDefaultExpectedSize(int defaultExpectedSize) {
        if (defaultExpectedSize == getDefaultExpectedSize())
            return this;
        return thisWith(getHashConfig(), defaultExpectedSize
                /* if obj key */, isNullKeyAllowed()/* elif !(float|double key) */
                , getLowerKeyDomainBound(), getUpperKeyDomainBound()/* endif */);
    }

    /* if obj key */
    @Override
    public final HashByteShortMapFactory/*<>*/ withNullKeyAllowed(boolean nullKeyAllowed) {
        if (nullKeyAllowed == isNullKeyAllowed())
            return this;
        return thisWith(getHashConfig(), getDefaultExpectedSize(), nullKeyAllowed);
    }
    /* elif !(float|double key) */
    final HashByteShortMapFactory/*<>*/ withDomain(byte lower, byte upper) {
        if (lower == getLowerKeyDomainBound() && upper == getUpperKeyDomainBound())
            return this;
        return thisWith(getHashConfig(), getDefaultExpectedSize(), lower, upper);
    }

    @Override
    public final HashByteShortMapFactory/*<>*/ withKeysDomain(byte lower, byte upper) {
        if (lower > upper)
            throw new IllegalArgumentException("minPossibleKey shouldn't be greater " +
                    "than maxPossibleKey");
        return withDomain(lower, upper);
    }

    @Override
    public final HashByteShortMapFactory/*<>*/ withKeysDomainComplement(byte lower, byte upper) {
        if (lower > upper)
            throw new IllegalArgumentException("minImpossibleKey shouldn't be greater " +
                    "than maxImpossibleKey");
        return withDomain((byte) (upper + 1), (byte) (lower - 1));
    }
    /* endif */

    @Override
    public String toString() {
        return "HashByteShortMapFactory[" + commonString() + keySpecialString() +
                /* if obj value */",valueEquivalence=" + getValueEquivalence() +
                /* elif !(obj value) */",defaultValue=" + getDefaultValue() +/* endif */
        "]";
    }

    @Override
    public int hashCode() {
        int hashCode = keySpecialHashCode(commonHashCode());
        /* if obj value */
        hashCode = hashCode * 31 + getValueEquivalence().hashCode();
        /* elif !(obj value) */
        hashCode = hashCode * 31 + Primitives.hashCode(getDefaultValue());
        /* endif */
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj instanceof HashByteShortMapFactory) {
            HashByteShortMapFactory factory = (HashByteShortMapFactory) obj;
            return commonEquals(factory) && keySpecialEquals(factory) &&
                    /* if obj value */
                    getValueEquivalence().equals(factory.getValueEquivalence())
                    /* elif !(obj value) */
                    // boxing to treat NaNs correctly
                   ((Short) getDefaultValue()).equals(factory.getDefaultValue())
                    /* endif */;
        } else {
            return false;
        }
    }

    /* if !(obj value) */
    @Override
    public short getDefaultValue() {
        return /* const value 0 */0;
    }
    /* elif obj value */
    @Override
    @Nonnull
    public Equivalence<Short> getValueEquivalence() {
        return Equivalence.defaultEquality();
    }
    /* endif */

    /* define p1 */
    /* if obj key obj value //<K2 extends K, V2 extends V>// elif obj key //<K2 extends K>
    // elif obj value //<V2 extends V>// endif */
    /* enddefine */

    /* define p2 */
    /* if obj key obj value //<K2, V2>// elif obj key //<K2>// elif obj value //<V2>// endif */
    /* enddefine */

    /* define ek */
    /* if obj key //? extends K2// elif !(obj key) //Byte// endif */
    /* enddefine */

    /* define ev */
    /* if obj value //? extends V2// elif !(obj value) //Short// endif */
    /* enddefine */

    /* define ep //<// ek //, // ev //>// enddefine */

    /* define ep2 */
    /* if obj key obj value //<? extends K2, ? extends V2>// elif obj key //<? extends K2>
    // elif obj value //<? extends V2>// endif */
    /* enddefine */

    /* define pk *//* if !(obj key) //byte// elif obj key //K2// endif *//* enddefine */
    /* define pv *//* if !(obj value) //short// elif obj value //V2// endif *//* enddefine */

    /* define gk *//* if !(obj key) //Byte// elif obj key //K2// endif *//* enddefine */
    /* define gv *//* if !(obj value) //Short// elif obj value //V2// endif *//* enddefine */

    private /*p1*/ UpdatableDHashSeparateKVByteShortMapGO/*p2*/ shrunk(
            UpdatableDHashSeparateKVByteShortMapGO/*p2*/ map) {
        Predicate<HashContainer> shrinkCondition;
        if ((shrinkCondition = hashConf.getShrinkCondition()) != null) {
            if (shrinkCondition.test(map))
                map.shrink();
        }
        return map;
    }

    /* with Updatable|Mutable mutability */
    @Override
    public /*p1*/ UpdatableDHashSeparateKVByteShortMapGO/*p2*/ newUpdatableMap() {
        return newUpdatableMap(getDefaultExpectedSize());
    }
    /* endwith */

    @Override
    public /*p1*/ UpdatableDHashSeparateKVByteShortMapGO/*p2*/ newUpdatableMap(
            Map/*ep*/<Byte, Short>/**/ map) {
        /* if !(obj key) */
        return shrunk(super.newUpdatableMap(map));
        /* elif obj key //
        return newUpdatableMap(map, map.size());
        // endif */
    }

    @Override
    public /*p1*/ UpdatableDHashSeparateKVByteShortMapGO/*p2*/ newUpdatableMap(
            Map/*ep*/<Byte, Short>/**/ map1, Map/*ep*/<Byte, Short>/**/ map2) {
        long expectedSize = (long) map1.size();
        expectedSize += (long) map2.size();
        return newUpdatableMap(map1, map2, sizeAsInt(expectedSize));
    }

    @Override
    public /*p1*/ UpdatableDHashSeparateKVByteShortMapGO/*p2*/ newUpdatableMap(
            Map/*ep*/<Byte, Short>/**/ map1, Map/*ep*/<Byte, Short>/**/ map2,
            Map/*ep*/<Byte, Short>/**/ map3) {
        long expectedSize = (long) map1.size();
        expectedSize += (long) map2.size();
        expectedSize += (long) map3.size();
        return newUpdatableMap(map1, map2, map3, sizeAsInt(expectedSize));
    }

    @Override
    public /*p1*/ UpdatableDHashSeparateKVByteShortMapGO/*p2*/ newUpdatableMap(
            Map/*ep*/<Byte, Short>/**/ map1, Map/*ep*/<Byte, Short>/**/ map2,
            Map/*ep*/<Byte, Short>/**/ map3, Map/*ep*/<Byte, Short>/**/ map4) {
        long expectedSize = (long) map1.size();
        expectedSize += (long) map2.size();
        expectedSize += (long) map3.size();
        expectedSize += (long) map4.size();
        return newUpdatableMap(map1, map2, map3, map4, sizeAsInt(expectedSize));
    }

    @Override
    public /*p1*/ UpdatableDHashSeparateKVByteShortMapGO/*p2*/ newUpdatableMap(
            Map/*ep*/<Byte, Short>/**/ map1, Map/*ep*/<Byte, Short>/**/ map2,
            Map/*ep*/<Byte, Short>/**/ map3, Map/*ep*/<Byte, Short>/**/ map4,
            Map/*ep*/<Byte, Short>/**/ map5) {
        long expectedSize = (long) map1.size();
        expectedSize += (long) map2.size();
        expectedSize += (long) map3.size();
        expectedSize += (long) map4.size();
        expectedSize += (long) map5.size();
        return newUpdatableMap(map1, map2, map3, map4, map5, sizeAsInt(expectedSize));
    }

    /* if obj key */
    @Override
    public /*p1*/ UpdatableDHashSeparateKVByteShortMapGO/*p2*/ newUpdatableMap(
            Map/*ep*/<Byte, Short>/**/ map, int expectedSize) {
        return shrunk(super.newUpdatableMap(map, expectedSize));
    }
    /* endif */

    @Override
    public /*p1*/ UpdatableDHashSeparateKVByteShortMapGO/*p2*/ newUpdatableMap(
            Map/*ep*/<Byte, Short>/**/ map1, Map/*ep*/<Byte, Short>/**/ map2,
            int expectedSize) {
        UpdatableDHashSeparateKVByteShortMapGO/*p2*/ map = newUpdatableMap(expectedSize);
        map.putAll(map1);
        map.putAll(map2);
        return shrunk(map);
    }

    @Override
    public /*p1*/ UpdatableDHashSeparateKVByteShortMapGO/*p2*/ newUpdatableMap(
            Map/*ep*/<Byte, Short>/**/ map1, Map/*ep*/<Byte, Short>/**/ map2,
            Map/*ep*/<Byte, Short>/**/ map3, int expectedSize) {
        UpdatableDHashSeparateKVByteShortMapGO/*p2*/ map = newUpdatableMap(expectedSize);
        map.putAll(map1);
        map.putAll(map2);
        map.putAll(map3);
        return shrunk(map);
    }

    @Override
    public /*p1*/ UpdatableDHashSeparateKVByteShortMapGO/*p2*/ newUpdatableMap(
            Map/*ep*/<Byte, Short>/**/ map1, Map/*ep*/<Byte, Short>/**/ map2,
            Map/*ep*/<Byte, Short>/**/ map3, Map/*ep*/<Byte, Short>/**/ map4,
            int expectedSize) {
        UpdatableDHashSeparateKVByteShortMapGO/*p2*/ map = newUpdatableMap(expectedSize);
        map.putAll(map1);
        map.putAll(map2);
        map.putAll(map3);
        map.putAll(map4);
        return shrunk(map);
    }

    @Override
    public /*p1*/ UpdatableDHashSeparateKVByteShortMapGO/*p2*/ newUpdatableMap(
            Map/*ep*/<Byte, Short>/**/ map1, Map/*ep*/<Byte, Short>/**/ map2,
            Map/*ep*/<Byte, Short>/**/ map3, Map/*ep*/<Byte, Short>/**/ map4,
            Map/*ep*/<Byte, Short>/**/ map5, int expectedSize) {
        UpdatableDHashSeparateKVByteShortMapGO/*p2*/ map = newUpdatableMap(expectedSize);
        map.putAll(map1);
        map.putAll(map2);
        map.putAll(map3);
        map.putAll(map4);
        map.putAll(map5);
        return shrunk(map);
    }


    @Override
    public /*p1*/ UpdatableDHashSeparateKVByteShortMapGO/*p2*/ newUpdatableMap(
            net.openhft.function.Consumer</*f*/ByteShortConsumer/*p2*/> entriesSupplier) {
        return newUpdatableMap(entriesSupplier, getDefaultExpectedSize());
    }

    @Override
    public /*p1*/ UpdatableDHashSeparateKVByteShortMapGO/*p2*/ newUpdatableMap(
            net.openhft.function.Consumer</*f*/ByteShortConsumer/*p2*/> entriesSupplier,
            int expectedSize) {
        final UpdatableDHashSeparateKVByteShortMapGO/*p2*/ map = newUpdatableMap(expectedSize);
        entriesSupplier.accept(new /*f*/ByteShortConsumer/*p2*/() {
             @Override
             public void accept(/*pk*/byte/**/ k, /*pv*/short/**/ v) {
                 map.put(k, v);
             }
         });
        return shrunk(map);
    }

    @Override
    public /*p1*/ UpdatableDHashSeparateKVByteShortMapGO/*p2*/ newUpdatableMap(/*pk*/byte/**/[] keys,
            /*pv*/short/**/[] values) {
        return newUpdatableMap(keys, values, keys.length);
    }

    @Override
    public /*p1*/ UpdatableDHashSeparateKVByteShortMapGO/*p2*/ newUpdatableMap(/*pk*/byte/**/[] keys,
            /*pv*/short/**/[] values, int expectedSize) {
        UpdatableDHashSeparateKVByteShortMapGO/*p2*/ map = newUpdatableMap(expectedSize);
        int keysLen = keys.length;
        if (keysLen != values.length)
            throw new IllegalArgumentException("keys and values arrays must have the same size");
        for (int i = 0; i < keys.length; i++) {
            map.put(keys[i], values[i]);
        }
        return shrunk(map);
    }

    /* if !(obj key obj value) */
    @Override
    public /*p1*/ UpdatableDHashSeparateKVByteShortMapGO/*p2*/ newUpdatableMap(
            /*gk*/Byte/**/[] keys, /*gv*/Short/**/[] values) {
        return newUpdatableMap(keys, values, keys.length);
    }

    @Override
    public /*p1*/ UpdatableDHashSeparateKVByteShortMapGO/*p2*/ newUpdatableMap(
            /*gk*/Byte/**/[] keys, /*gv*/Short/**/[] values, int expectedSize) {
        UpdatableDHashSeparateKVByteShortMapGO/*p2*/ map = newUpdatableMap(expectedSize);
        int keysLen = keys.length;
        if (keysLen != values.length)
            throw new IllegalArgumentException("keys and values arrays must have the same size");
        for (int i = 0; i < keys.length; i++) {
            map.put(keys[i], values[i]);
        }
        return shrunk(map);
    }
    /* endif */

    @Override
    public /*p1*/ UpdatableDHashSeparateKVByteShortMapGO/*p2*/ newUpdatableMap(Iterable</*ek*/Byte/**/> keys,
            Iterable</*ev*/Short/**/> values) {
        int expectedSize = keys instanceof Collection ? ((Collection) keys).size() :
                getDefaultExpectedSize();
        return newUpdatableMap(keys, values, expectedSize);
    }

    @Override
    public /*p1*/ UpdatableDHashSeparateKVByteShortMapGO/*p2*/ newUpdatableMap(Iterable</*ek*/Byte/**/> keys,
            Iterable</*ev*/Short/**/> values, int expectedSize) {
        UpdatableDHashSeparateKVByteShortMapGO/*p2*/ map = newUpdatableMap(expectedSize);
        Iterator</*ek*/Byte/**/> keysIt = keys.iterator();
        Iterator</*ev*/Short/**/> valuesIt = values.iterator();
        try {
            while (keysIt.hasNext()) {
                map.put(keysIt.next(), valuesIt.next());
            }
            return shrunk(map);
        } catch (NoSuchElementException e) {
            throw new IllegalArgumentException(
                    "keys and values iterables must have the same size", e);
        }
    }

    @Override
    public /*p1*/ UpdatableDHashSeparateKVByteShortMapGO/*p2*/ newUpdatableMapOf(
            /*pk*/byte/**/ k1, /*pv*/short/**/ v1) {
        UpdatableDHashSeparateKVByteShortMapGO/*p2*/ map = newUpdatableMap(1);
        map.put(k1, v1);
        return map;
    }

    @Override
    public /*p1*/ UpdatableDHashSeparateKVByteShortMapGO/*p2*/ newUpdatableMapOf(
            /*pk*/byte/**/ k1, /*pv*/short/**/ v1, /*pk*/byte/**/ k2, /*pv*/short/**/ v2) {
        UpdatableDHashSeparateKVByteShortMapGO/*p2*/ map = newUpdatableMap(2);
        map.put(k1, v1);
        map.put(k2, v2);
        return map;
    }

    @Override
    public /*p1*/ UpdatableDHashSeparateKVByteShortMapGO/*p2*/ newUpdatableMapOf(
            /*pk*/byte/**/ k1, /*pv*/short/**/ v1, /*pk*/byte/**/ k2, /*pv*/short/**/ v2,
            /*pk*/byte/**/ k3, /*pv*/short/**/ v3) {
        UpdatableDHashSeparateKVByteShortMapGO/*p2*/ map = newUpdatableMap(3);
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        return map;
    }

    @Override
    public /*p1*/ UpdatableDHashSeparateKVByteShortMapGO/*p2*/ newUpdatableMapOf(
            /*pk*/byte/**/ k1, /*pv*/short/**/ v1, /*pk*/byte/**/ k2, /*pv*/short/**/ v2,
            /*pk*/byte/**/ k3, /*pv*/short/**/ v3, /*pk*/byte/**/ k4, /*pv*/short/**/ v4) {
        UpdatableDHashSeparateKVByteShortMapGO/*p2*/ map = newUpdatableMap(4);
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        return map;
    }

    @Override
    public /*p1*/ UpdatableDHashSeparateKVByteShortMapGO/*p2*/ newUpdatableMapOf(
            /*pk*/byte/**/ k1, /*pv*/short/**/ v1, /*pk*/byte/**/ k2, /*pv*/short/**/ v2,
            /*pk*/byte/**/ k3, /*pv*/short/**/ v3, /*pk*/byte/**/ k4, /*pv*/short/**/ v4,
            /*pk*/byte/**/ k5, /*pv*/short/**/ v5) {
        UpdatableDHashSeparateKVByteShortMapGO/*p2*/ map = newUpdatableMap(5);
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        map.put(k5, v5);
        return map;
    }

    /* with Mutable|Immutable mutability */
    /* with with|without expectedSize */
    /* define arg *//* if with expectedSize //, int expectedSize// endif *//* enddefine */
    /* define apply *//* if with expectedSize //, expectedSize// endif *//* enddefine */

     /* if obj key || without expectedSize */
    @Override
    public /*p1*/ HashByteShortMap/*p2*/ newMutableMap(
            Map/*ep*/<Byte, Short>/**/ map/*arg*/) {
        MutableDHashSeparateKVByteShortMapGO/*p2*/ res = uninitializedMutableMap();
        res.move(newUpdatableMap(map/*apply*/));
        return res;
    }
    /* endif */

    @Override
    public /*p1*/ HashByteShortMap/*p2*/ newMutableMap(Map/*ep*/<Byte, Short>/**/ map1,
            Map/*ep*/<Byte, Short>/**/ map2/*arg*/) {
        MutableDHashSeparateKVByteShortMapGO/*p2*/ res = uninitializedMutableMap();
        res.move(newUpdatableMap(map1, map2/*apply*/));
        return res;
    }

    @Override
    public /*p1*/ HashByteShortMap/*p2*/ newMutableMap(Map/*ep*/<Byte, Short>/**/ map1,
            Map/*ep*/<Byte, Short>/**/ map2, Map/*ep*/<Byte, Short>/**/ map3/*arg*/) {
        MutableDHashSeparateKVByteShortMapGO/*p2*/ res = uninitializedMutableMap();
        res.move(newUpdatableMap(map1, map2, map3/*apply*/));
        return res;
    }

    @Override
    public /*p1*/ HashByteShortMap/*p2*/ newMutableMap(Map/*ep*/<Byte, Short>/**/ map1,
            Map/*ep*/<Byte, Short>/**/ map2, Map/*ep*/<Byte, Short>/**/ map3,
            Map/*ep*/<Byte, Short>/**/ map4/*arg*/) {
        MutableDHashSeparateKVByteShortMapGO/*p2*/ res = uninitializedMutableMap();
        res.move(newUpdatableMap(map1, map2, map3, map4/*apply*/));
        return res;
    }

    @Override
    public /*p1*/ HashByteShortMap/*p2*/ newMutableMap(Map/*ep*/<Byte, Short>/**/ map1,
            Map/*ep*/<Byte, Short>/**/ map2, Map/*ep*/<Byte, Short>/**/ map3,
            Map/*ep*/<Byte, Short>/**/ map4, Map/*ep*/<Byte, Short>/**/ map5/*arg*/) {
        MutableDHashSeparateKVByteShortMapGO/*p2*/ res = uninitializedMutableMap();
        res.move(newUpdatableMap(map1, map2, map3, map4, map5/*apply*/));
        return res;
    }

    /* endwith */

    @Override
    public /*p1*/ HashByteShortMap/*p2*/ newMutableMap(
            net.openhft.function.Consumer</*f*/ByteShortConsumer/*p2*/> entriesSupplier) {
        MutableDHashSeparateKVByteShortMapGO/*p2*/ map = uninitializedMutableMap();
        map.move(newUpdatableMap(entriesSupplier));
        return map;
    }

    @Override
    public /*p1*/ HashByteShortMap/*p2*/ newMutableMap(
            net.openhft.function.Consumer</*f*/ByteShortConsumer/*p2*/> entriesSupplier,
            int expectedSize) {
        MutableDHashSeparateKVByteShortMapGO/*p2*/ map = uninitializedMutableMap();
        map.move(newUpdatableMap(entriesSupplier, expectedSize));
        return map;
    }

    @Override
    public /*p1*/ HashByteShortMap/*p2*/ newMutableMap(/*pk*/byte/**/[] keys,
            /*pv*/short/**/[] values) {
        MutableDHashSeparateKVByteShortMapGO/*p2*/ map = uninitializedMutableMap();
        map.move(newUpdatableMap(keys, values));
        return map;
    }

    @Override
    public /*p1*/ HashByteShortMap/*p2*/ newMutableMap(/*pk*/byte/**/[] keys,
            /*pv*/short/**/[] values, int expectedSize) {
        MutableDHashSeparateKVByteShortMapGO/*p2*/ map = uninitializedMutableMap();
        map.move(newUpdatableMap(keys, values, expectedSize));
        return map;
    }

    /* if !(obj key obj value) */
    @Override
    public /*p1*/ HashByteShortMap/*p2*/ newMutableMap(
            /*gk*/Byte/**/[] keys, /*gv*/Short/**/[] values) {
        MutableDHashSeparateKVByteShortMapGO/*p2*/ map = uninitializedMutableMap();
        map.move(newUpdatableMap(keys, values));
        return map;
    }

    @Override
    public /*p1*/ HashByteShortMap/*p2*/ newMutableMap(
            /*gk*/Byte/**/[] keys, /*gv*/Short/**/[] values, int expectedSize) {
        MutableDHashSeparateKVByteShortMapGO/*p2*/ map = uninitializedMutableMap();
        map.move(newUpdatableMap(keys, values, expectedSize));
        return map;
    }
    /* endif */

    @Override
    public /*p1*/ HashByteShortMap/*p2*/ newMutableMap(Iterable</*ek*/Byte/**/> keys,
            Iterable</*ev*/Short/**/> values) {
        MutableDHashSeparateKVByteShortMapGO/*p2*/ map = uninitializedMutableMap();
        map.move(newUpdatableMap(keys, values));
        return map;
    }

    @Override
    public /*p1*/ HashByteShortMap/*p2*/ newMutableMap(Iterable</*ek*/Byte/**/> keys,
            Iterable</*ev*/Short/**/> values, int expectedSize) {
        MutableDHashSeparateKVByteShortMapGO/*p2*/ map = uninitializedMutableMap();
        map.move(newUpdatableMap(keys, values, expectedSize));
        return map;
    }

    @Override
    public /*p1*/ HashByteShortMap/*p2*/ newMutableMapOf(/*pk*/byte/**/ k1, /*pv*/short/**/ v1) {
        MutableDHashSeparateKVByteShortMapGO/*p2*/ map = uninitializedMutableMap();
        map.move(newUpdatableMapOf(k1, v1));
        return map;
    }

    @Override
    public /*p1*/ HashByteShortMap/*p2*/ newMutableMapOf(/*pk*/byte/**/ k1, /*pv*/short/**/ v1,
             /*pk*/byte/**/ k2, /*pv*/short/**/ v2) {
        MutableDHashSeparateKVByteShortMapGO/*p2*/ map = uninitializedMutableMap();
        map.move(newUpdatableMapOf(k1, v1, k2, v2));
        return map;
    }

    @Override
    public /*p1*/ HashByteShortMap/*p2*/ newMutableMapOf(/*pk*/byte/**/ k1, /*pv*/short/**/ v1,
             /*pk*/byte/**/ k2, /*pv*/short/**/ v2, /*pk*/byte/**/ k3, /*pv*/short/**/ v3) {
        MutableDHashSeparateKVByteShortMapGO/*p2*/ map = uninitializedMutableMap();
        map.move(newUpdatableMapOf(k1, v1, k2, v2, k3, v3));
        return map;
    }

    @Override
    public /*p1*/ HashByteShortMap/*p2*/ newMutableMapOf(/*pk*/byte/**/ k1, /*pv*/short/**/ v1,
             /*pk*/byte/**/ k2, /*pv*/short/**/ v2, /*pk*/byte/**/ k3, /*pv*/short/**/ v3,
             /*pk*/byte/**/ k4, /*pv*/short/**/ v4) {
        MutableDHashSeparateKVByteShortMapGO/*p2*/ map = uninitializedMutableMap();
        map.move(newUpdatableMapOf(k1, v1, k2, v2, k3, v3, k4, v4));
        return map;
    }

    @Override
    public /*p1*/ HashByteShortMap/*p2*/ newMutableMapOf(/*pk*/byte/**/ k1, /*pv*/short/**/ v1,
             /*pk*/byte/**/ k2, /*pv*/short/**/ v2, /*pk*/byte/**/ k3, /*pv*/short/**/ v3,
             /*pk*/byte/**/ k4, /*pv*/short/**/ v4, /*pk*/byte/**/ k5, /*pv*/short/**/ v5) {
        MutableDHashSeparateKVByteShortMapGO/*p2*/ map = uninitializedMutableMap();
        map.move(newUpdatableMapOf(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5));
        return map;
    }
    /* endwith */
}
