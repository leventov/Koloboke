/* with
 byte|char|short|int|long|float|double|obj key
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

package com.koloboke.collect.impl;

import com.koloboke.function./*f*/ByteShortPredicate/**/;
import com.koloboke.function./*f*/ByteShortConsumer/**/;
import com.koloboke.collect.map.ByteShortMap;

import java.util.Map;


public final class CommonByteShortMapOps {

    public static boolean containsAllEntries(final InternalByteShortMapOps/*<?>*/ map,
            Map<?, ?> another) {
        if (map == another)
            throw new IllegalArgumentException();
        if (another instanceof ByteShortMap) {
            ByteShortMap m2 = (ByteShortMap) another;
            /* if obj key || obj value */
            if (
                // if obj key //
                    m2.keyEquivalence().equals(map.keyEquivalence())
                // endif //
                /* if obj key obj value */ && /* endif */
                // if obj value //
                    m2.valueEquivalence().equals(map.valueEquivalence())
                // endif //
            ) {
            /* endif */
                if (map.size() < m2.size())
                    return false;
                if (m2 instanceof InternalByteShortMapOps) {
                    //noinspection unchecked
                    return ((InternalByteShortMapOps) m2).allEntriesContainingIn(map);
                }
            /* if obj key || obj value */
            }
            // noinspection unchecked
            /* endif */
            return m2.forEachWhile(new
                   /*f*/ByteShortPredicate/**/() {
                @Override
                public boolean test(/* raw */byte a, /* raw */short b) {
                    return map.containsEntry(a, b);
                }
            });
        }
        for (Map.Entry<?, ?> e : another.entrySet()) {
            if (!map.containsEntry(/* if !(obj key) */(Byte) /* endif */e.getKey(),
                    /* if !(obj value) */(Short) /* endif */e.getValue()))
                return false;
        }
        return true;
    }

    public static /*<>*/ void putAll(final InternalByteShortMapOps/*<>*/ map,
            Map<? extends Byte, ? extends Short> another) {
        if (map == another)
            throw new IllegalArgumentException();
        long maxPossibleSize = map.sizeAsLong() + Containers.sizeAsLong(another);
        map.ensureCapacity(maxPossibleSize);
        if (another instanceof ByteShortMap) {
            if (another instanceof InternalByteShortMapOps) {
                ((InternalByteShortMapOps) another).reversePutAllTo(map);
            } else {
                ((ByteShortMap) another).forEach(new /*f*/ByteShortConsumer/*<>*/() {
                    @Override
                    public void accept(byte key, short value) {
                        map.justPut(key, value);
                    }
                });
            }
        } else {
            for (Map.Entry<? extends Byte, ? extends Short> e : another.entrySet()) {
                map.justPut(e.getKey(), e.getValue());
            }
        }
    }

    private CommonByteShortMapOps() {}
}
