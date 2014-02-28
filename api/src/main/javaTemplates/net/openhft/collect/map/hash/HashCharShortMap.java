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

package net.openhft.collect.map.hash;

import net.openhft.collect.HashContainer;
import net.openhft.collect.map.CharShortMap;
import net.openhft.collect.set.hash.HashCharSet;
import net.openhft.collect.set.hash.HashObjSet;
import org.jetbrains.annotations.NotNull;


/**
 * @see net.openhft.collect.map.hash.HashCharShortMaps
 */
public interface HashCharShortMap/*<>*/ extends CharShortMap/*<>*/, HashContainer {

    /* with key view */
    @Override
    @NotNull
    HashCharSet/*<>*/ keySet();
    /* endwith */

    @Override
    @NotNull
    HashObjSet<Entry<Character, Short>> entrySet();
}
