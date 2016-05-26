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

import com.koloboke.compile.KolobokeMap;
import com.koloboke.compile.MethodForm;


@KolobokeMap
public abstract class SynchronizedMap<K, V> {
    public static <K, V> SynchronizedMap<K, V> withExpectedSize(int expectedSize) {
        return new KolobokeSynchronizedMap<K, V>(expectedSize);
    }

    public final synchronized V get(K key) {
        return subGet(key);
    }

    public final synchronized V put(K key, V value) {
        return subPut(key, value);
    }

    public final synchronized int size() {
        return subSize();
    }

    @MethodForm("get")
    abstract V subGet(K key);

    @MethodForm("put")
    abstract V subPut(K key, V value);

    @MethodForm("size")
    abstract int subSize();
}
