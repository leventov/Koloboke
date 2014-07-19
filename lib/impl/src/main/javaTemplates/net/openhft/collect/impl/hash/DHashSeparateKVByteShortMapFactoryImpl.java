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


/**
 * TODO recheck
 * high probability of copy-paste mistake
 */
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

    /* define configClass */
    /* if !(float|double key) //ByteHashConfig// elif float|double key //HashConfig// endif */
    /* enddefine */

    /* define getHashConfig */
    /* if !(float|double key) //config.getHashConfig()// elif float|double key //config// endif */
    /* enddefine */

    /** For ServiceLoader */
    public DHashSeparateKVByteShortMapFactoryImpl() {
        this(/* configClass */ByteHashConfig/**/.getDefault());
    }

    DHashSeparateKVByteShortMapFactoryImpl(/* configClass */ByteHashConfig/**/ conf) {
        super(conf);
    }

    /* if obj key */
    @Override
    public <KE> HashObjShortMapFactory<KE/*andV*/> withKeyEquivalence(
            @Nullable Equivalence<KE> keyEquivalence) {
        if (keyEquivalence == null) {
            // noinspection unchecked
            return (HashObjShortMapFactory<KE/*andV*/>) this;
        }
        return new WithCustomKeyEquivalence<KE/*andV*/>(getConfig(), keyEquivalence);
    }
    /* endif */


    /* if !(obj value) */
    @Override
    public HashByteShortMapFactory/*<>*/ withDefaultValue(short defaultValue) {
        if (defaultValue == /* const value 0 */0)
            return this;
        return new WithCustomDefaultValue/*<>*/(getConfig(), defaultValue);
    }
    /* elif obj value */
    @Override
    public <VE> HashByteObjMapFactory</*kAnd*/VE> withValueEquivalence(
            @Nullable Equivalence<VE> valueEquivalence) {
        if (valueEquivalence == null) {
            // noinspection unchecked
            return (HashByteObjMapFactory</*kAnd*/VE>) this;
        }
        return new WithCustomValueEquivalence</*kAnd*/VE>(getConfig(), valueEquivalence);
    }
    /* endif */

    @Override
    public HashByteShortMapFactory/*<>*/ withConfig(/* configClass */ByteHashConfig/**/ config) {
        if (configIsSuitableForMutableLHash(/* getHashConfig */config.getHashConfig()/**/))
            return new LHashSeparateKVByteShortMapFactoryImpl/*<>*/(config);
        /* with DHash|QHash hash */
        return new DHashSeparateKVByteShortMapFactoryImpl/*<>*/(config);
        /* endwith */
    }


    /* if obj key */
    static class WithCustomKeyEquivalence<K/*andV*/>
            extends DHashSeparateKVObjShortMapFactoryGO<K/*andV*/> {

        private final Equivalence<K> keyEquivalence;

        WithCustomKeyEquivalence(ObjHashConfig conf, Equivalence<K> keyEquivalence) {
            super(conf);
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
                return new DHashSeparateKVObjShortMapFactoryImpl<KE/*andV*/>(conf);
            if (keyEquivalence.equals(this.keyEquivalence)) {
                // noinspection unchecked
                return (HashObjShortMapFactory<KE/*andV*/>) this;
            }
            return new WithCustomKeyEquivalence<KE/*andV*/>(getConfig(), keyEquivalence);
        }

        /* if !(obj value) */
        @Override
        public HashObjShortMapFactory<K/*andV*/> withDefaultValue(short defaultValue) {
            if (defaultValue == /* const value 0 */0)
                return this;
            return new WithCustomKeyEquivalenceAndDefaultValue<K/*andV*/>(
                    getConfig(), keyEquivalence, defaultValue);
        }
        /* elif obj value */
        @Override
        public <VE> HashObjObjMapFactory<K, VE> withValueEquivalence(
                @Nullable Equivalence<VE> valueEquivalence) {
            if (valueEquivalence == null) {
                // noinspection unchecked
                return (HashObjObjMapFactory<K, VE>) this;
            }
            return new WithCustomEquivalences<K, VE>(getConfig(), keyEquivalence, valueEquivalence);
        }
        /* endif */

        @Override
        public HashObjShortMapFactory<K/*andV*/> withConfig(ObjHashConfig config) {
            if (configIsSuitableForMutableLHash(/* getHashConfig */config.getHashConfig()/**/)) {
                return new LHashSeparateKVByteShortMapFactoryImpl
                        .WithCustomKeyEquivalence<K/*andV*/>(
                            config, keyEquivalence);
            }
            /* with DHash|QHash hash */
            return new DHashSeparateKVByteShortMapFactoryImpl.WithCustomKeyEquivalence<K/*andV*/>(
                    config, keyEquivalence);
            /* endwith */
        }
    }
    /* endif */

    /* if !(obj value) */
    static final class WithCustomDefaultValue/*<>*/ extends DHashSeparateKVByteShortMapFactoryGO/*<>*/ {
        private final short defaultValue;

        WithCustomDefaultValue(/* configClass */ByteHashConfig/**/ conf, short defaultValue) {
            super(conf);
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
            return new WithCustomKeyEquivalenceAndDefaultValue<KE>(
                    getConfig(), keyEquivalence, defaultValue);
        }
        /* endif */

        @Override
        public HashByteShortMapFactory/*<>*/ withDefaultValue(short defaultValue) {
            if (defaultValue == /* const value 0 */0)
                return new DHashSeparateKVByteShortMapFactoryImpl/*<>*/(getConfig());
            if (defaultValue == this.defaultValue)
                return this;
            return new WithCustomDefaultValue/*<>*/(getConfig(), defaultValue);
        }

        @Override
        public HashByteShortMapFactory/*<>*/ withConfig(
                /* configClass */ByteHashConfig/**/ config) {
            if (configIsSuitableForMutableLHash(/* getHashConfig */config.getHashConfig()/**/)) {
                return new LHashSeparateKVByteShortMapFactoryImpl.WithCustomDefaultValue/*<>*/(
                        config, defaultValue);
            }
            /* with DHash|QHash hash */
            return new DHashSeparateKVByteShortMapFactoryImpl.WithCustomDefaultValue/*<>*/(
                    config, defaultValue);
            /* endwith */
        }
    }
    /* elif obj value */
    static final class WithCustomValueEquivalence</*kAnd*/V>
            extends DHashSeparateKVByteObjMapFactoryGO</*kAnd*/V> {

        private final Equivalence<V> valueEquivalence;
        WithCustomValueEquivalence(/* configClass */ByteHashConfig/**/ conf,
                Equivalence<V> valueEquivalence) {
            super(conf);
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
            return new WithCustomEquivalences<KE, V>(getConfig(), keyEquivalence, valueEquivalence);
        }
        /* endif */

        @Override
        public <VE> HashByteObjMapFactory</*kAnd*/VE> withValueEquivalence(
                @Nullable Equivalence<VE> valueEquivalence) {
            if (valueEquivalence == null)
                return new DHashSeparateKVByteObjMapFactoryImpl</*kAnd*/VE>(getConfig());
            if (valueEquivalence.equals(this.valueEquivalence))
                // noinspection unchecked
                return (HashByteObjMapFactory</*kAnd*/VE>) this;
            return new WithCustomValueEquivalence</*kAnd*/VE>(
                    getConfig(), valueEquivalence);
        }

        @Override
        public HashByteObjMapFactory</*kAnd*/V> withConfig(
                /* configClass */ByteHashConfig/**/ config) {
            if (configIsSuitableForMutableLHash(/* getHashConfig */config.getHashConfig()/**/)) {
                return new LHashSeparateKVByteShortMapFactoryImpl
                        .WithCustomValueEquivalence</*kAnd*/V>(
                            config, valueEquivalence);
            }
            /* with DHash|QHash hash */
            return new DHashSeparateKVByteShortMapFactoryImpl.WithCustomValueEquivalence</*kAnd*/V>(
                    config, valueEquivalence);
            /* endwith */
        }
    }
    /* endif */

    /* if obj key && !(obj value) */
    static final class WithCustomKeyEquivalenceAndDefaultValue<K>
            extends DHashSeparateKVObjShortMapFactoryGO<K> {
        private final Equivalence<K> keyEquivalence;
        private final short defaultValue;

        WithCustomKeyEquivalenceAndDefaultValue(ObjHashConfig conf,
                Equivalence<K> keyEquivalence, short defaultValue) {
            super(conf);
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
                return new WithCustomDefaultValue<KE>(getConfig(), defaultValue);
            if (keyEquivalence.equals(this.keyEquivalence)) {
                // noinspection unchecked
                return (HashObjShortMapFactory<KE>) this;
            }
            return new WithCustomKeyEquivalenceAndDefaultValue<KE>(
                    getConfig(), keyEquivalence, defaultValue);
        }

        @Override
        public HashObjShortMapFactory<K> withDefaultValue(short defaultValue) {
            if (defaultValue == /* const value 0 */0)
                return new WithCustomKeyEquivalence<K>(getConfig(), keyEquivalence);
            if (defaultValue == this.defaultValue)
                return this;
            return new WithCustomKeyEquivalenceAndDefaultValue<K>(
                    getConfig(), keyEquivalence, defaultValue);
        }

        @Override
        public HashObjShortMapFactory<K> withConfig(ObjHashConfig config) {
            if (configIsSuitableForMutableLHash(/* getHashConfig */config.getHashConfig()/**/)) {
                return new LHashSeparateKVByteShortMapFactoryImpl
                        .WithCustomKeyEquivalenceAndDefaultValue<K>(
                            config, keyEquivalence, defaultValue);
            }
            /* with DHash|QHash hash */
            return new DHashSeparateKVByteShortMapFactoryImpl.WithCustomKeyEquivalenceAndDefaultValue<K>(
                    config, keyEquivalence, defaultValue);
            /* endwith */
        }
    }
    /* elif obj key obj value */
    static final class WithCustomEquivalences<K, V>
            extends DHashSeparateKVObjObjMapFactoryGO<K, V> {
        private final Equivalence<K> keyEquivalence;
        private final Equivalence<V> valueEquivalence;

        WithCustomEquivalences(ObjHashConfig conf,
                Equivalence<K> keyEquivalence, Equivalence<V> valueEquivalence) {
            super(conf);
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
                return new WithCustomValueEquivalence<KE, V>(getConfig(), valueEquivalence);
            if (keyEquivalence.equals(this.keyEquivalence)) {
                // noinspection unchecked
                return (HashObjObjMapFactory<KE, V>) this;
            }
            return new WithCustomEquivalences<KE, V>(getConfig(), keyEquivalence, valueEquivalence);
        }

        @Override
        public <VE> HashObjObjMapFactory<K, VE> withValueEquivalence(
                @Nullable Equivalence<VE> valueEquivalence) {
            if (valueEquivalence == null)
                return new WithCustomKeyEquivalence<K, VE>(getConfig(), keyEquivalence);
            if (valueEquivalence.equals(this.valueEquivalence)) {
                // noinspection unchecked
                return (HashObjObjMapFactory<K, VE>) this;
            }
            return new WithCustomEquivalences<K, VE>(getConfig(), keyEquivalence, valueEquivalence);
        }

        @Override
        public HashObjObjMapFactory<K, V> withConfig(ObjHashConfig config) {
            if (configIsSuitableForMutableLHash(/* getHashConfig */config.getHashConfig()/**/)) {
                return new LHashSeparateKVByteShortMapFactoryImpl.WithCustomEquivalences<K, V>(
                        config, keyEquivalence, valueEquivalence);
            }
            /* with DHash|QHash hash */
            return new DHashSeparateKVByteShortMapFactoryImpl.WithCustomEquivalences<K, V>(
                    config, keyEquivalence, valueEquivalence);
            /* endwith */
        }
    }
    /* endif */
}
