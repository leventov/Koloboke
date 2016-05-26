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

import com.koloboke.collect.Equivalence;
import com.koloboke.collect.hash.HashConfig;
import com.koloboke.collect.map.hash.HashObjLongMap;
import com.koloboke.compile.CustomKeyEquivalence;
import com.koloboke.compile.KolobokeMap;

import javax.annotation.Nonnull;


@KolobokeMap
@CustomKeyEquivalence
abstract class ConfigurableKeyEquivalenceMap<K> implements HashObjLongMap<K> {

    static <K> HashObjLongMap<K> with(
            @Nonnull Equivalence<? super K> keyEquivalence, int expectedSize) {
        return new KolobokeConfigurableKeyEquivalenceMap<K>(keyEquivalence, expectedSize);
    }

    static <K> HashObjLongMap<K> sparseWith(
            @Nonnull Equivalence<? super K> keyEquivalence, int expectedSize) {
        return new KolobokeConfigurableKeyEquivalenceMap<K>(
                keyEquivalence, HashConfig.fromLoads(0.25, 0.375, 0.5), expectedSize);
    }

    @Nonnull
    private final Equivalence<? super K> keyEquivalence;

    ConfigurableKeyEquivalenceMap(@Nonnull Equivalence<? super K> keyEquivalence) {
        this.keyEquivalence = keyEquivalence;
    }

    final boolean keyEquals(K queriedKey, K keyInMap) {
        return keyEquivalence.equivalent(queriedKey, keyInMap);
    }

    final int keyHashCode(K key) {
        return keyEquivalence.hash(key);
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    @Override
    public final Equivalence<K> keyEquivalence() {
        return (Equivalence<K>) keyEquivalence;
    }
}
