/* with
  char|byte|short|int|long|float|double|obj key
  short|byte|char|int|long|float|double|obj value
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
import net.openhft.collect.map.hash.*;
import org.jetbrains.annotations.Nullable;


/**
 * TODO recheck
 * high probability of copy-paste mistake
 */
public final class HashCharShortMapFactoryImpl/*<>*/ extends HashCharShortMapFactoryGO/*<>*/ {

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
    /* if !(float|double key) //CharHashConfig// elif float|double key //HashConfig// endif */
    /* enddefine */

    /**
     * For ServiceLoader
     */
    public HashCharShortMapFactoryImpl() {
        this(/* configClass */CharHashConfig/**/.DEFAULT);
    }

    HashCharShortMapFactoryImpl(/* configClass */CharHashConfig/**/ conf) {
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
    public HashCharShortMapFactory/*<>*/ withDefaultValue(short defaultValue) {
        if (defaultValue == /* const value 0 */0)
            return this;
        return new WithCustomDefaultValue/*<>*/(getConfig(), defaultValue);
    }
    /* elif obj value */
    @Override
    public <VE> HashCharObjMapFactory</*kAnd*/VE> withValueEquivalence(
            @Nullable Equivalence<VE> valueEquivalence) {
        if (valueEquivalence == null) {
            // noinspection unchecked
            return (HashCharObjMapFactory</*kAnd*/VE>) this;
        }
        return new WithCustomValueEquivalence</*kAnd*/VE>(getConfig(), valueEquivalence);
    }
    /* endif */

    @Override
    public HashCharShortMapFactory/*<>*/ withConfig(/* configClass */CharHashConfig/**/ config) {
        if (getConfig().equals(config))
            return this;
        return new HashCharShortMapFactoryImpl/*<>*/(config);
    }


    /* if obj key */
    static class WithCustomKeyEquivalence<K/*andV*/> extends HashObjShortMapFactoryGO<K/*andV*/> {

        private final Equivalence<K> keyEquivalence;

        WithCustomKeyEquivalence(ObjHashConfig conf, Equivalence<K> keyEquivalence) {
            super(conf);
            this.keyEquivalence = keyEquivalence;
        }

        @Override
        public Equivalence<K> getKeyEquivalence() {
            return keyEquivalence;
        }

        @Override
        <K2 extends K/*andP1*/> MutableDHashObjShortMapGO<K2/*andP2*/> uninitializedMutableMap() {
            MutableDHashObjShortMap.WithCustomKeyEquivalence<K2/*andP2*/> map =
                    new MutableDHashObjShortMap.WithCustomKeyEquivalence<K2/*andP2*/>();
            map.keyEquivalence = keyEquivalence;
            return map;
        }

        @Override
        <K2 extends K/*andP1*/> ImmutableDHashObjShortMapGO<K2/*andP2*/>
        uninitializedImmutableMap() {
            ImmutableDHashObjShortMap.WithCustomKeyEquivalence<K2/*andP2*/> map =
                    new ImmutableDHashObjShortMap.WithCustomKeyEquivalence<K2/*andP2*/>();
            map.keyEquivalence = keyEquivalence;
            return map;
        }

        @Override
        public <KE> HashObjShortMapFactory<KE/*andV*/> withKeyEquivalence(
                @Nullable Equivalence<KE> keyEquivalence) {
            if (keyEquivalence == null)
                return new HashObjShortMapFactoryImpl<KE/*andV*/>(conf);
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
            if (getConfig().equals(config))
                return this;
            return new WithCustomKeyEquivalence<K/*andV*/>(config, keyEquivalence);
        }
    }
    /* endif */

    /* if !(obj value) */
    static final class WithCustomDefaultValue/*<>*/ extends HashCharShortMapFactoryGO/*<>*/ {
        private final short defaultValue;

        WithCustomDefaultValue(/* configClass */CharHashConfig/**/ conf, short defaultValue) {
            super(conf);
            this.defaultValue = defaultValue;
        }

        @Override
        public short getDefaultValue() {
            return defaultValue;
        }

        @Override
        /*p1*/ MutableDHashCharShortMapGO/*p2*/ uninitializedMutableMap() {
            MutableDHashCharShortMap.WithCustomDefaultValue/*p2*/ map =
                    new MutableDHashCharShortMap.WithCustomDefaultValue/*p2*/();
            map.defaultValue = defaultValue;
            return map;
        }

        @Override
        /*p1*/ ImmutableDHashCharShortMapGO/*p2*/ uninitializedImmutableMap() {
            ImmutableDHashCharShortMap.WithCustomDefaultValue/*p2*/ map =
                    new ImmutableDHashCharShortMap.WithCustomDefaultValue/*p2*/();
            map.defaultValue = defaultValue;
            return map;
        }

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
        public HashCharShortMapFactory/*<>*/ withDefaultValue(short defaultValue) {
            if (defaultValue == /* const value 0 */0)
                return new HashCharShortMapFactoryImpl/*<>*/(getConfig());
            if (defaultValue == this.defaultValue)
                return this;
            return new WithCustomDefaultValue/*<>*/(getConfig(), defaultValue);
        }

        @Override
        public HashCharShortMapFactory/*<>*/ withConfig(
                /* configClass */CharHashConfig/**/ config) {
            if (getConfig().equals(config))
                return this;
            return new WithCustomDefaultValue/*<>*/(config, defaultValue);
        }
    }
    /* elif obj value */
    static final class WithCustomValueEquivalence</*kAnd*/V>
            extends HashCharObjMapFactoryGO</*kAnd*/V> {

        private final Equivalence<V> valueEquivalence;
        WithCustomValueEquivalence(/* configClass */CharHashConfig/**/ conf,
                Equivalence<V> valueEquivalence) {
            super(conf);
            this.valueEquivalence = valueEquivalence;
        }

        @Override
        public Equivalence<V> getValueEquivalence() {
            return valueEquivalence;
        }

        @Override
        </*p1And*/V2 extends V> MutableDHashCharObjMapGO</*p2And*/V2> uninitializedMutableMap() {
            MutableDHashCharObjMap.WithCustomValueEquivalence</*p2And*/V2> map =
                    new MutableDHashCharObjMap.WithCustomValueEquivalence</*p2And*/V2>();
            map.valueEquivalence = valueEquivalence;
            return map;
        }

        @Override
        </*p1And*/V2 extends V> ImmutableDHashCharObjMapGO</*p2And*/V2>
        uninitializedImmutableMap() {
            ImmutableDHashCharObjMap.WithCustomValueEquivalence</*p2And*/V2> map =
                    new ImmutableDHashCharObjMap.WithCustomValueEquivalence</*p2And*/V2>();
            map.valueEquivalence = valueEquivalence;
            return map;
        }

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
        public <VE> HashCharObjMapFactory</*kAnd*/VE> withValueEquivalence(
                @Nullable Equivalence<VE> valueEquivalence) {
            if (valueEquivalence == null)
                return new HashCharObjMapFactoryImpl</*kAnd*/VE>(getConfig());
            if (valueEquivalence.equals(this.valueEquivalence))
                // noinspection unchecked
                return (HashCharObjMapFactory</*kAnd*/VE>) this;
            return new WithCustomValueEquivalence</*kAnd*/VE>(
                    getConfig(), valueEquivalence);
        }

        @Override
        public HashCharObjMapFactory</*kAnd*/V> withConfig(
                /* configClass */CharHashConfig/**/ config) {
            if (getConfig().equals(config))
                return this;
            return new WithCustomValueEquivalence</*kAnd*/V>(config, valueEquivalence);
        }
    }
    /* endif */

    /* if obj key && !(obj value) */
    static final class WithCustomKeyEquivalenceAndDefaultValue<K>
            extends HashObjShortMapFactoryGO<K> {
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

        @Override
        <K2 extends K> MutableDHashObjShortMapGO<K2> uninitializedMutableMap() {
            MutableDHashObjShortMap.WithCustomKeyEquivalenceAndDefaultValue<K2> map =
                    new MutableDHashObjShortMap.WithCustomKeyEquivalenceAndDefaultValue<K2>();
            map.keyEquivalence = keyEquivalence;
            map.defaultValue = defaultValue;
            return map;
        }

        @Override
        <K2 extends K> ImmutableDHashObjShortMapGO<K2> uninitializedImmutableMap() {
            ImmutableDHashObjShortMap.WithCustomKeyEquivalenceAndDefaultValue<K2> map =
                    new ImmutableDHashObjShortMap.WithCustomKeyEquivalenceAndDefaultValue<K2>();
            map.keyEquivalence = keyEquivalence;
            map.defaultValue = defaultValue;
            return map;
        }

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
            if (getConfig().equals(config))
                return this;
            return new WithCustomKeyEquivalenceAndDefaultValue<K>(
                    config, keyEquivalence, defaultValue);
        }
    }
    /* elif obj key obj value */
    static final class WithCustomEquivalences<K, V>
            extends HashObjObjMapFactoryGO<K, V> {
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

        @Override
        <K2 extends K, V2 extends V> MutableDHashObjObjMapGO<K2, V2> uninitializedMutableMap() {
            MutableDHashObjObjMap.WithCustomEquivalences<K2, V2> map =
                    new MutableDHashObjObjMap.WithCustomEquivalences<K2, V2>();
            map.keyEquivalence = keyEquivalence;
            map.valueEquivalence = valueEquivalence;
            return map;
        }

        @Override
        <K2 extends K, V2 extends V> ImmutableDHashObjObjMapGO<K2, V2>
        uninitializedImmutableMap() {
            ImmutableDHashObjObjMap.WithCustomEquivalences<K2, V2> map =
                    new ImmutableDHashObjObjMap.WithCustomEquivalences<K2, V2>();
            map.keyEquivalence = keyEquivalence;
            map.valueEquivalence = valueEquivalence;
            return map;
        }

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
            if (getConfig().equals(config))
                return this;
            return new WithCustomEquivalences<K, V>(config, keyEquivalence, valueEquivalence);
        }
    }
    /* endif */
}
