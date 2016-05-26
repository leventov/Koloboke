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

import java.util.Map;


@KolobokeMap
public interface MyGenericMap<T extends Map<T, S>, S> extends Map<T, S> {

    default int keyHashCode(T key) {
        return System.identityHashCode(key);
    }

    default boolean keyEquals(T a, T b) {
        return a == b;
    }

    Object[] table();
}
