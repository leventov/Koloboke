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


package net.openhft.collect;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.Map;


public abstract class Equivalence<T> {

    public static Equivalence<Object> identity() {
        return IDENTITY;
    }

    private static final Equivalence<Object> IDENTITY = new Identity();

    private static class Identity extends Equivalence<Object> {

        @Override
        public boolean nullableEquivalent(@Nullable Object a, @Nullable Object b) {
            return a == b;
        }

        @Override
        public boolean equivalent(@Nonnull Object a, @Nonnull Object b) {
            return a == b;
        }

        @Override
        public int nullableHash(@Nullable Object o) {
            return System.identityHashCode(o);
        }

        @Override
        public int hash(@Nonnull Object o) {
            return System.identityHashCode(o);
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Identity;
        }

        @Override
        public int hashCode() {
            return Identity.class.hashCode();
        }

        @Override
        public String toString() {
            return "Identity";
        }
    }

    private static class EntryEquivalence<K, V> extends Equivalence<Map.Entry<K, V>> {

        private final Equivalence<K> keyEquivalence;
        private final Equivalence<V> valueEquivalence;

        private EntryEquivalence(Equivalence<K> keyEquivalence, Equivalence<V> valueEquivalence) {
            this.keyEquivalence = keyEquivalence;
            this.valueEquivalence = valueEquivalence;
        }

        @Override
        public boolean equivalent(@Nonnull Map.Entry<K, V> a, @Nonnull Map.Entry<K, V> b) {
            if (a == b)
                return true;
            K aKey = a.getKey();
            K bKey = b.getKey();
            if (keyEquivalence == null) {
                if (!NullableObjects.equals(aKey, bKey))
                    return false;
            } else if (!keyEquivalence.nullableEquivalent(aKey, bKey)) {
                return false;
            }
            V aVal = a.getValue();
            V bVal = b.getValue();
            if (valueEquivalence == null) {
                return NullableObjects.equals(aVal, bVal);
            } else {
                return valueEquivalence.nullableEquivalent(aVal, bVal);
            }
        }

        @Override
        public int hash(@Nonnull Map.Entry<K, V> entry) {
            int keyHash = keyEquivalence == null ?
                    NullableObjects.hashCode(entry.getKey()) :
                    keyEquivalence.nullableHash(entry.getKey());
            int valHash = valueEquivalence == null ?
                    NullableObjects.hashCode(entry.getValue()) :
                    valueEquivalence.nullableHash(entry.getValue());
            return keyHash ^ valHash;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o instanceof EntryEquivalence) {
                EntryEquivalence eq = (EntryEquivalence) o;
                return NullableObjects.equals(this.keyEquivalence, eq.keyEquivalence) &&
                        NullableObjects.equals(this.valueEquivalence, eq.valueEquivalence);
            } else {
                return false;
            }
        }


        @Override
        public int hashCode() {
            return NullableObjects.hashCode(keyEquivalence) ^
                    NullableObjects.hashCode(valueEquivalence);
        }

        @Override
        public String toString() {
            return "EntryEquivalence{keyEquivalence=" + keyEquivalence +
                    ", valueEquivalence=" + valueEquivalence + "}";
        }
    }

    public static <K, V> Equivalence<Map.Entry<K, V>> entryEquivalence(
            @Nullable Equivalence<K> keyEquivalence, @Nullable Equivalence<V> valueEquivalence) {
        if (keyEquivalence == null && valueEquivalence == null)
            return null;
        return new EntryEquivalence<K, V>(keyEquivalence, valueEquivalence);
    }


    public boolean nullableEquivalent(@Nullable T a, @Nullable T b) {
        return a == b || (a != null && b != null && equivalent(a, b));
    }

    public abstract boolean equivalent(@Nonnull T a, @Nonnull T b);


    public int nullableHash(@Nullable T t) {
        return t != null ? hash(t) : 0;
    }

    public abstract int hash(@Nonnull T t);

    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();

    @Override
    public abstract String toString();
}
