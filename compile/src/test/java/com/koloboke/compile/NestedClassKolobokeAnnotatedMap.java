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

package com.koloboke.compile;

import com.koloboke.compile.mutability.Updatable;

import java.util.Map;

@KolobokeMap
@Updatable
public abstract class NestedClassKolobokeAnnotatedMap<K, V> implements Map<K, V> {

    @KolobokeSet
    interface NestedSet<K> {
        boolean add(K key);

        @KolobokeMap
        abstract class InterfaceNested<K, V> {
            abstract V put(K key, V value);
        }
    }

    @KolobokeMap
    static abstract class InnerClass<V> {
        abstract V put(int key, V value);
    }
}
