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
import net.openhft.collect.map.hash.*;
import javax.annotation.Nullable;

import static net.openhft.collect.impl.hash.LHashCapacities.configIsSuitableForMutableLHash;


public final class DHashSeparateKVByteShortMapFactoryImpl/*<>*/
        extends DHashSeparateKVByteShortMapFactoryGO/*<>*/ {

    /* define p1 */
    /* if obj key obj value //<K2 extends K, V2 extends V>// elif obj key //<K2 extends K>
    // elif obj value //<V2 extends V>// endif */
    /* enddefine */

    /* define p2 */
    /* if obj key obj value //<K2, V2>// elif obj key //<K2>// elif obj value //<V2>// endif */
    /* enddefine */

    /* define kAnd *//* if obj key //K, // endif *//* enddefine */
    /* define p1And *//* if obj key //K2 extends K, // endif *//* enddefine */
    /* define p2And *//* if obj key //K2, // endif *//* enddefine */

    /* define andV *//* if obj value //, V// endif *//* enddefine */
    /* define andP1 *//* if obj value //, V2 extends V// endif *//* enddefine */
    /* define andP2 *//* if obj value //, V2// endif *//* enddefine */

    /** For ServiceLoader */
    public DHashSeparateKVByteShortMapFactoryImpl() {
        this(HashConfig.getDefault()
            /* if obj key */, true
            /* elif !(float|double key) */, Byte.MIN_VALUE, Byte.MAX_VALUE/* endif */);
    }

    /* define commonArgDef //
    HashConfig hashConf// if obj key //, boolean isNullKeyAllowed
            // elif !(float|double key) //, byte lower, byte upper// endif //
    // enddefine */

    /* define commonArgApply //
    hashConf// if obj key //, isNullKeyAllowed
            // elif !(float|double key) //, lower, upper// endif //
    // enddefine */

    /* define commonArgGet //
    getHashConfig()// if obj key //, isNullKeyAllowed()// elif !(float|double key) //
            , getLowerKeyDomainBound(), getUpperKeyDomainBound()// endif //
    // enddefine */

    DHashSeparateKVByteShortMapFactoryImpl(/* commonArgDef */) {
        super(/* commonArgApply */);
    }

    @Override
    HashByteShortMapFactory/*<>*/ thisWith(/* commonArgDef */) {
        return new DHashSeparateKVByteShortMapFactoryImpl/*<>*/(/* commonArgApply */);
    }

    /* with DHash|QHash|LHash hash */
    @Override
    HashByteShortMapFactory/*<>*/ dHashLikeThisWith(/* commonArgDef */) {
        return new DHashSeparateKVByteShortMapFactoryImpl/*<>*/(/* commonArgApply */);
    }
    /* endwith */

    /* if obj key */
    @Override
    public <KE> HashObjShortMapFactory<KE/*andV*/> withKeyEquivalence(
            @Nullable Equivalence<KE> keyEquivalence) {
        if (keyEquivalence == null) {
            // noinspection unchecked
            return (HashObjShortMapFactory<KE/*andV*/>) this;
        }
        return new WithCustomKeyEquivalence<KE/*andV*/>(/* commonArgGet */, keyEquivalence);
    }
    /* endif */

    /* if !(obj value) */
    @Override
    public HashByteShortMapFactory/*<>*/ withDefaultValue(short defaultValue) {
        if (defaultValue == /* const value 0 */0)
            return this;
        return new WithCustomDefaultValue/*<>*/(/* commonArgGet */, defaultValue);
    }
    /* elif obj value */
    @Override
    public <VE> HashByteObjMapFactory</*kAnd*/VE> withValueEquivalence(
            @Nullable Equivalence<VE> valueEquivalence) {
        if (valueEquivalence == null) {
            // noinspection unchecked
            return (HashByteObjMapFactory</*kAnd*/VE>) this;
        }
        return new WithCustomValueEquivalence</*kAnd*/VE>(/* commonArgGet */, valueEquivalence);
    }
    /* endif */

    /* if obj key */
    static class WithCustomKeyEquivalence<K/*andV*/>
            extends DHashSeparateKVObjShortMapFactoryGO<K/*andV*/> {

        private final Equivalence<K> keyEquivalence;

        WithCustomKeyEquivalence(/* commonArgDef */, Equivalence<K> keyEquivalence) {
            super(/* commonArgApply */);
            this.keyEquivalence = keyEquivalence;
        }

        @Override
        public Equivalence<K> getKeyEquivalence() {
            return keyEquivalence;
        }

        /* with Mutable|Updatable|Immutable mutability */
        @Override
        <K2 extends K/*andP1*/> MutableDHashSeparateKVObjShortMapGO<K2/*andP2*/>
        uninitializedMutableMap() {
            MutableDHashSeparateKVObjShortMap.WithCustomKeyEquivalence<K2/*andP2*/> map =
                    new MutableDHashSeparateKVObjShortMap.WithCustomKeyEquivalence<K2/*andP2*/>();
            map.keyEquivalence = keyEquivalence;
            return map;
        }
        /* endwith */

        @Override
        public <KE> HashObjShortMapFactory<KE/*andV*/> withKeyEquivalence(
                @Nullable Equivalence<KE> keyEquivalence) {
            if (keyEquivalence == null)
                return new DHashSeparateKVObjShortMapFactoryImpl<KE/*andV*/>(/* commonArgGet */);
            if (keyEquivalence.equals(this.keyEquivalence)) {
                // noinspection unchecked
                return (HashObjShortMapFactory<KE/*andV*/>) this;
            }
            return new WithCustomKeyEquivalence<KE/*andV*/>(/* commonArgGet */, keyEquivalence);
        }

        /* if !(obj value) */
        @Override
        public HashObjShortMapFactory<K/*andV*/> withDefaultValue(short defaultValue) {
            if (defaultValue == /* const value 0 */0)
                return this;
            return new WithCustomKeyEquivalenceAndDefaultValue<K/*andV*/>(
                    /* commonArgGet */, keyEquivalence, defaultValue);
        }
        /* elif obj value */
        @Override
        public <VE> HashObjObjMapFactory<K, VE> withValueEquivalence(
                @Nullable Equivalence<VE> valueEquivalence) {
            if (valueEquivalence == null) {
                // noinspection unchecked
                return (HashObjObjMapFactory<K, VE>) this;
            }
            return new WithCustomEquivalences<K, VE>(/* commonArgGet */,
                    keyEquivalence, valueEquivalence);
        }
        /* endif */

        @Override
        HashByteShortMapFactory/*<>*/ thisWith(/* commonArgDef */) {
            return new WithCustomKeyEquivalence<K/*andV*/>(/* commonArgApply */, keyEquivalence);
        }

        /* with DHash|QHash|LHash hash */
        @Override
        HashByteShortMapFactory/*<>*/ dHashLikeThisWith(/* commonArgDef */) {
            return new DHashSeparateKVByteShortMapFactoryImpl.WithCustomKeyEquivalence<K/*andV*/>(
                    /* commonArgApply */, keyEquivalence);
        }
        /* endwith */
    }
    /* endif */

    /* if !(obj value) */
    static final class WithCustomDefaultValue/*<>*/
            extends DHashSeparateKVByteShortMapFactoryGO/*<>*/ {
        private final short defaultValue;

        WithCustomDefaultValue(/* commonArgDef */, short defaultValue) {
            super(/* commonArgApply */);
            this.defaultValue = defaultValue;
        }

        @Override
        public short getDefaultValue() {
            return defaultValue;
        }

        /* with Mutable|Updatable|Immutable mutability */
        @Override
        /*p1*/ MutableDHashSeparateKVByteShortMapGO/*p2*/ uninitializedMutableMap() {
            MutableDHashSeparateKVByteShortMap.WithCustomDefaultValue/*p2*/ map =
                    new MutableDHashSeparateKVByteShortMap.WithCustomDefaultValue/*p2*/();
            map.defaultValue = defaultValue;
            return map;
        }
        /* endwith */

        /* if obj key */
        @Override
        public <KE> HashObjShortMapFactory<KE> withKeyEquivalence(
                @Nullable Equivalence<KE> keyEquivalence) {
            if (keyEquivalence == null) {
                // noinspection unchecked
                return (HashObjShortMapFactory<KE>) this;
            }
            return new WithCustomKeyEquivalenceAndDefaultValue<KE>(/* commonArgGet */,
                    keyEquivalence, defaultValue);
        }
        /* endif */

        @Override
        public HashByteShortMapFactory/*<>*/ withDefaultValue(short defaultValue) {
            if (defaultValue == /* const value 0 */0)
                return new DHashSeparateKVByteShortMapFactoryImpl/*<>*/(/* commonArgGet */);
            if (defaultValue == this.defaultValue)
                return this;
            return new WithCustomDefaultValue/*<>*/(/* commonArgGet */, defaultValue);
        }

        @Override
        HashByteShortMapFactory/*<>*/ thisWith(/* commonArgDef */) {
            return new WithCustomDefaultValue/*<>*/(/* commonArgApply */, defaultValue);
        }

        /* with DHash|QHash|LHash hash */
        @Override
        HashByteShortMapFactory/*<>*/ dHashLikeThisWith(/* commonArgDef */) {
            return new DHashSeparateKVByteShortMapFactoryImpl.WithCustomDefaultValue/*<>*/(
                    /* commonArgApply */, defaultValue);
        }
        /* endwith */
    }
    /* elif obj value */
    static final class WithCustomValueEquivalence</*kAnd*/V>
            extends DHashSeparateKVByteObjMapFactoryGO</*kAnd*/V> {

        private final Equivalence<V> valueEquivalence;
        WithCustomValueEquivalence(/* commonArgDef */,
                Equivalence<V> valueEquivalence) {
            super(/* commonArgApply */);
            this.valueEquivalence = valueEquivalence;
        }

        @Override
        public Equivalence<V> getValueEquivalence() {
            return valueEquivalence;
        }

        /* with Mutable|Updatable|Immutable mutability */
        @Override
        </*p1And*/V2 extends V> MutableDHashSeparateKVByteObjMapGO</*p2And*/V2>
        uninitializedMutableMap() {
            MutableDHashSeparateKVByteObjMap.WithCustomValueEquivalence</*p2And*/V2> map =
                    new MutableDHashSeparateKVByteObjMap.WithCustomValueEquivalence</*p2And*/V2>();
            map.valueEquivalence = valueEquivalence;
            return map;
        }
        /* endwith */

        /* if obj key */
        @Override
        public <KE> HashObjObjMapFactory<KE, V> withKeyEquivalence(
                @Nullable Equivalence<KE> keyEquivalence) {
            if (keyEquivalence == null) {
                // noinspection unchecked
                return (HashObjObjMapFactory<KE, V>) this;
            }
            return new WithCustomEquivalences<KE, V>(/* commonArgGet */,
                    keyEquivalence, valueEquivalence);
        }
        /* endif */

        @Override
        public <VE> HashByteObjMapFactory</*kAnd*/VE> withValueEquivalence(
                @Nullable Equivalence<VE> valueEquivalence) {
            if (valueEquivalence == null)
                return new DHashSeparateKVByteObjMapFactoryImpl</*kAnd*/VE>(/* commonArgGet */);
            if (valueEquivalence.equals(this.valueEquivalence))
                // noinspection unchecked
                return (HashByteObjMapFactory</*kAnd*/VE>) this;
            return new WithCustomValueEquivalence</*kAnd*/VE>(/* commonArgGet */, valueEquivalence);
        }

        @Override
        HashByteShortMapFactory/*<>*/ thisWith(/* commonArgDef */) {
            return new WithCustomValueEquivalence</*kAnd*/V>(/* commonArgApply */,
                    valueEquivalence);
        }

        /* with DHash|QHash|LHash hash */
        @Override
        HashByteShortMapFactory/*<>*/ dHashLikeThisWith(/* commonArgDef */) {
            return new DHashSeparateKVByteShortMapFactoryImpl.WithCustomValueEquivalence</*kAnd*/V>(
                    /* commonArgApply */, valueEquivalence);
        }
        /* endwith */
    }
    /* endif */

    /* if obj key && !(obj value) */
    static final class WithCustomKeyEquivalenceAndDefaultValue<K>
            extends DHashSeparateKVObjShortMapFactoryGO<K> {
        private final Equivalence<K> keyEquivalence;
        private final short defaultValue;

        WithCustomKeyEquivalenceAndDefaultValue(/* commonArgDef */,
                Equivalence<K> keyEquivalence, short defaultValue) {
            super(/* commonArgApply */);
            this.keyEquivalence = keyEquivalence;
            this.defaultValue = defaultValue;
        }

        @Override
        public Equivalence<K> getKeyEquivalence() {
            return keyEquivalence;
        }

        @Override
        public short getDefaultValue() {
            return defaultValue;
        }

        /* with Mutable|Updatable|Immutable mutability */
        @Override
        <K2 extends K> MutableDHashSeparateKVObjShortMapGO<K2> uninitializedMutableMap() {
            MutableDHashSeparateKVObjShortMap.WithCustomKeyEquivalenceAndDefaultValue<K2> map =
                    new MutableDHashSeparateKVObjShortMap
                            .WithCustomKeyEquivalenceAndDefaultValue<K2>();
            map.keyEquivalence = keyEquivalence;
            map.defaultValue = defaultValue;
            return map;
        }
        /* endwith */

        @Override
        public <KE> HashObjShortMapFactory<KE> withKeyEquivalence(
                @Nullable Equivalence<KE> keyEquivalence) {
            if (keyEquivalence == null)
                return new WithCustomDefaultValue<KE>(/* commonArgGet */, defaultValue);
            if (keyEquivalence.equals(this.keyEquivalence)) {
                // noinspection unchecked
                return (HashObjShortMapFactory<KE>) this;
            }
            return new WithCustomKeyEquivalenceAndDefaultValue<KE>(
                    /* commonArgGet */, keyEquivalence, defaultValue);
        }

        @Override
        public HashObjShortMapFactory<K> withDefaultValue(short defaultValue) {
            if (defaultValue == /* const value 0 */0)
                return new WithCustomKeyEquivalence<K>(/* commonArgGet */, keyEquivalence);
            if (defaultValue == this.defaultValue)
                return this;
            return new WithCustomKeyEquivalenceAndDefaultValue<K>(
                    /* commonArgGet */, keyEquivalence, defaultValue);
        }

        @Override
        HashByteShortMapFactory/*<>*/ thisWith(/* commonArgDef */) {
            return new WithCustomKeyEquivalenceAndDefaultValue<K>(/* commonArgApply */,
                    keyEquivalence, defaultValue);
        }

        /* with DHash|QHash|LHash hash */
        @Override
        HashByteShortMapFactory/*<>*/ dHashLikeThisWith(/* commonArgDef */) {
            return new DHashSeparateKVByteShortMapFactoryImpl
                        .WithCustomKeyEquivalenceAndDefaultValue<K>(/* commonArgApply */,
                    keyEquivalence, defaultValue);
        }
        /* endwith */
    }
    /* elif obj key obj value */
    static final class WithCustomEquivalences<K, V>
            extends DHashSeparateKVObjObjMapFactoryGO<K, V> {
        private final Equivalence<K> keyEquivalence;
        private final Equivalence<V> valueEquivalence;

        WithCustomEquivalences(/* commonArgDef */,
                Equivalence<K> keyEquivalence, Equivalence<V> valueEquivalence) {
            super(/* commonArgApply */);
            this.keyEquivalence = keyEquivalence;
            this.valueEquivalence = valueEquivalence;
        }

        @Override
        public Equivalence<K> getKeyEquivalence() {
            return keyEquivalence;
        }

        @Override
        public Equivalence<V> getValueEquivalence() {
            return valueEquivalence;
        }

        /* with Mutable|Updatable|Immutable mutability */
        @Override
        <K2 extends K, V2 extends V> MutableDHashSeparateKVObjObjMapGO<K2, V2>
        uninitializedMutableMap() {
            MutableDHashSeparateKVObjObjMap.WithCustomEquivalences<K2, V2> map =
                    new MutableDHashSeparateKVObjObjMap.WithCustomEquivalences<K2, V2>();
            map.keyEquivalence = keyEquivalence;
            map.valueEquivalence = valueEquivalence;
            return map;
        }
        /* endwith */

        @Override
        public <KE> HashObjObjMapFactory<KE, V> withKeyEquivalence(
                @Nullable Equivalence<KE> keyEquivalence) {
            if (keyEquivalence == null)
                return new WithCustomValueEquivalence<KE, V>(/* commonArgGet */,
                        valueEquivalence);
            if (keyEquivalence.equals(this.keyEquivalence)) {
                // noinspection unchecked
                return (HashObjObjMapFactory<KE, V>) this;
            }
            return new WithCustomEquivalences<KE, V>(/* commonArgGet */,
                    keyEquivalence, valueEquivalence);
        }

        @Override
        public <VE> HashObjObjMapFactory<K, VE> withValueEquivalence(
                @Nullable Equivalence<VE> valueEquivalence) {
            if (valueEquivalence == null)
                return new WithCustomKeyEquivalence<K, VE>(/* commonArgGet */, keyEquivalence);
            if (valueEquivalence.equals(this.valueEquivalence)) {
                // noinspection unchecked
                return (HashObjObjMapFactory<K, VE>) this;
            }
            return new WithCustomEquivalences<K, VE>(/* commonArgGet */,
                    keyEquivalence, valueEquivalence);
        }

        @Override
        HashByteShortMapFactory/*<>*/ thisWith(/* commonArgDef */) {
            return new WithCustomEquivalences<K, V>(/* commonArgApply */,
                    keyEquivalence, valueEquivalence);
        }

        /* with DHash|QHash|LHash hash */
        @Override
        HashByteShortMapFactory/*<>*/ dHashLikeThisWith(/* commonArgDef */) {
            return new DHashSeparateKVByteShortMapFactoryImpl.WithCustomEquivalences<K, V>(
                    /* commonArgApply */, keyEquivalence, valueEquivalence);
        }
        /* endwith */
    }
    /* endif */
}
