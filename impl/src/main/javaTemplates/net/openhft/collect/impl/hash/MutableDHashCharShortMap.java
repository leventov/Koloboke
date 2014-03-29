/* with
 char|byte|short|int|long|float|double|object key
 short|byte|char|int|long|float|double|object value
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

import net.openhft.collect.Equivalence;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


final class MutableDHashCharShortMap/*<>*/ extends MutableDHashCharShortMapGO/*<>*/ {

    /* define andV *//* if obj value //, V// endif *//* enddefine */

    /* if obj key */
    static final class WithCustomKeyEquivalence<K/*andV*/>
            extends MutableDHashObjShortMapGO<K/*andV*/> {
        Equivalence<? super K> keyEquivalence;

        /* define keyMethods */
        @Override
        public Equivalence<K> keyEquivalence() {
            // noinspection unchecked
            return (Equivalence<K>) keyEquivalence;
        }

        @Override
        boolean nullableKeyEquals(@Nullable K a, @Nullable K b) {
            return keyEquivalence.nullableEquivalent(a, b);
        }

        @Override
        boolean keyEquals(@Nonnull K a, @Nullable K b) {
            return b != null && keyEquivalence.equivalent(a, b);
        }

        @Override
        int nullableKeyHashCode(@Nullable K key) {
            return keyEquivalence.nullableHash(key);
        }

        @Override
        int keyHashCode(@Nonnull K key) {
            return keyEquivalence.hash(key);
        }
        /* enddefine */

        /* keyMethods */
    }
    /* endif */

    /* if !(obj value) */
    static final class WithCustomDefaultValue/*<>*/ extends MutableDHashCharShortMapGO/*<>*/ {
        short defaultValue;

        /* define defaultValueMethods */
        @Override
        public short defaultValue() {
            return defaultValue;
        }
        /* enddefine */

        /* defaultValueMethods */
    }
    /* elif obj value */
    /* define kAnd *//* if obj key //K, // endif *//* enddefine */
    static final class WithCustomValueEquivalence</*kAnd*/V>
            extends MutableDHashCharObjMapGO</*kAnd*/V> {
        Equivalence<? super V> valueEquivalence;

        /* define valueMethods */
        @Override
        public Equivalence<V> valueEquivalence() {
            // noinspection unchecked
            return (Equivalence<V>) valueEquivalence;
        }

        @Override
        boolean nullableValueEquals(@Nullable V a, @Nullable V b) {
            return valueEquivalence.nullableEquivalent(a, b);
        }

        @Override
        boolean valueEquals(@Nonnull V a, @Nullable V b) {
            return b != null && valueEquivalence.equivalent(a, b);
        }

        @Override
        int nullableValueHashCode(@Nullable V value) {
            return valueEquivalence.nullableHash(value);
        }

        @Override
        int valueHashCode(@Nonnull V value) {
            return valueEquivalence.hash(value);
        }
        /* enddefine */

        /* valueMethods */
    }
    /* endif */


    /* if obj key && !(obj value) */
    static final class WithCustomKeyEquivalenceAndDefaultValue/*<>*/
            extends MutableDHashCharShortMapGO/*<>*/ {
        Equivalence<? super Character> keyEquivalence;
        short defaultValue;

        /* keyMethods */

        /* defaultValueMethods */
    }
    /* elif obj key obj value */
    static final class WithCustomEquivalences/*<>*/ extends MutableDHashCharShortMapGO/*<>*/ {
        Equivalence<? super Character> keyEquivalence;
        Equivalence<? super Short> valueEquivalence;

        /* keyMethods */

        /* valueMethods */
    }
    /* endif */
}
