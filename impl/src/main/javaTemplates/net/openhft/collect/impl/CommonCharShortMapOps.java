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


package net.openhft.collect.impl;

import net.openhft.collect.NullableObjects;
import net.openhft.function.*;
import net.openhft.collect.map.CharShortMap;

import java.util.Map;

public final class CommonCharShortMapOps {

    public static boolean containsAllEntries(final InternalCharShortMapOps/*<?>*/ map,
            Map<?, ?> another) {
        if ( map == another )
            throw new IllegalArgumentException();
        if (another instanceof CharShortMap) {
            CharShortMap m2 = (CharShortMap) another;
            /* if obj key || obj value */
            if (
                // if obj key //
                    NullableObjects.equals(m2.keyEquivalence(), map.keyEquivalence())
                // endif //
                /* if obj key obj value */ && /* endif */
                // if obj value //
                    NullableObjects.equals(m2.valueEquivalence(), map.valueEquivalence())
                // endif //
            ) {
            /* endif */
                if (map.size() < m2.size())
                    return false;
                if (m2 instanceof InternalCharShortMapOps) {
                    //noinspection unchecked
                    return ((InternalCharShortMapOps) m2).allEntriesContainingIn(map);
                }
            /* if obj key || obj value */
            }
            // noinspection unchecked
            /* endif */
            return m2.forEachWhile(new
                   /* if !(obj key obj value) */CharShortPredicate
                   /* elif obj key obj value //BiPredicate// endif */() {
                @Override
                public boolean test(/* raw */char a, /* raw */short b) {
                    return map.containsEntry(a, b);
                }
            });
        }
        for ( Map.Entry<?, ?> e : another.entrySet() ) {
            if ( !map.containsEntry(/* if !(obj key) */(Character) /* endif */e.getKey(),
                    /* if !(obj value) */(Short) /* endif */e.getValue()))
                return false;
        }
        return true;
    }

    public static /*<>*/ void putAll(final InternalCharShortMapOps/*<>*/ map,
            Map<? extends Character, ? extends Short> another) {
        if (map == another)
            throw new IllegalArgumentException();
        int maxPossibleSize = map.size() + another.size();
        map.ensureCapacity(maxPossibleSize >= 0 ? maxPossibleSize : Integer.MAX_VALUE);
        if (another instanceof CharShortMap) {
            if (another instanceof InternalCharShortMapOps) {
                ((InternalCharShortMapOps) another).reversePutAllTo(map);
            } else {
                ((CharShortMap) another).forEach(new /*f*/CharShortConsumer/*<>*/() {
                    @Override
                    public void accept(char key, short value) {
                        map.justPut(key, value);
                    }
                });
            }
        } else {
            for (Map.Entry<? extends Character, ? extends Short> e : another.entrySet()) {
                map.justPut(e.getKey(), e.getValue());
            }
        }
    }

    private CommonCharShortMapOps() {}
}
