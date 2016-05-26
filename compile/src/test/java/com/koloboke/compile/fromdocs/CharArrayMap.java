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

import java.util.Arrays;
import java.util.Map;


@KolobokeMap
@CustomKeyEquivalence
public abstract class CharArrayMap<V> implements Map<char[], V> {

    static <V> Map<char[], V> withExpectedSize(int expectedSize) {
        return new KolobokeCharArrayMap<V>(expectedSize);
    }

    final boolean keyEquals(char[] queriedKey, char[] keyInMap) {
        return Arrays.equals(queriedKey, keyInMap);
    }

    final int keyHashCode(char[] key) {
        return Arrays.hashCode(key);
    }
}
