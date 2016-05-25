/* with
 char|byte|short|int|long|float|double|obj key
 short|byte|char|int|long|float|double|obj value
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

package com.koloboke.collect.map.hash;

import com.koloboke.collect.hash.HashContainer;
import com.koloboke.collect.map.CharShortMap;
import com.koloboke.collect.set.hash.HashCharSet;
import com.koloboke.collect.set.hash.HashObjSet;
import com.koloboke.compile.KolobokeMap;

import javax.annotation.Nonnull;


/**
 * An interface for {@code CharShortMap}s, based on hash tables.
 *
 * <p>This interface doesn't carry own specific behaviour, just combines it's superinterfaces.
 *
 * <p>Looking for a way to instantiate a {@code HashCharShortMap}? See static factory methods
 * in {@link HashCharShortMaps} class.
 *
 * @see HashCharShortMapFactory
 * @see HashCharShortMaps
 * @see KolobokeMap @KolobokeMap
 */
public interface HashCharShortMap/*<>*/ extends CharShortMap/*<>*/, HashContainer {

    /* with key view */
    @Override
    @Nonnull
    HashCharSet/*<>*/ keySet();
    /* endwith */

    @Override
    @Nonnull
    HashObjSet<Entry<Character, Short>> entrySet();
}
