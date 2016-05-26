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

package com.koloboke.compile.fromdocs;

import com.koloboke.compile.CustomKeyEquivalence;
import com.koloboke.compile.KolobokeMap;

import java.util.Map;


@KolobokeMap
@CustomKeyEquivalence
abstract class IdentityToIntMap<K> implements Map<K, Integer> {

    static <K> IdentityToIntMap<K> withExpectedSize(int expectedSize) {
        return new KolobokeIdentityToIntMap<K>(expectedSize);
    }

    abstract int getInt(K key);
    abstract int put(K key, int value);

    /**
     * Returns just {@code false} because keyEquals() contract guarantees that arguments are
     * not identical, see {@link CustomKeyEquivalence} javadocs.
     */
    final boolean keyEquals(K queriedKey, K keyInMap) {
        return false;
    }

    final int keyHashCode(K key) {
        return System.identityHashCode(key);
    }
}
