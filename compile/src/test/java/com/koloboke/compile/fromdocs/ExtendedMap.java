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

import com.koloboke.collect.map.hash.HashObjObjMap;
import com.koloboke.compile.KolobokeMap;
import com.koloboke.function.BiConsumer;
import com.koloboke.function.BiPredicate;


@KolobokeMap
abstract class ExtendedMap<V> implements HashObjObjMap<String, V> {
    static <V> ExtendedMap<V> withExpectedSize(int expectedSize) {
        return new KolobokeExtendedMap<V>(expectedSize);
    }
    public abstract V get(String key);
    @Override
    public abstract V put(String key, V value);
    @Override
    public abstract int size();
    @Override
    public abstract void forEach(BiConsumer<? super String, ? super V> action);
    @Override
    public abstract boolean removeIf(BiPredicate<? super String, ? super V> predicate);
    @Override
    public abstract boolean shrink();
}
