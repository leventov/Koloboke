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

import com.koloboke.collect.hash.HashConfig;
import com.koloboke.compile.KolobokeMap;
import java.util.Map;

@KolobokeMap
abstract class MyMap<K, V> implements Map<K, V> {
    static <K, V> Map<K, V> withExpectedSize(int expectedSize) {
        return new KolobokeMyMap<K, V>(expectedSize);
    }

    static <K, V> Map<K, V> sparseWithExpectedSize(int expectedSize) {
        return new KolobokeMyMap<K, V>(HashConfig.fromLoads(0.25, 0.375, 0.5), expectedSize);
    }
}
