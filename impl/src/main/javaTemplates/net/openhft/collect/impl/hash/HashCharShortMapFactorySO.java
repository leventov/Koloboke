/* with
 char|byte|short|int|long|float|double key
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

package net.openhft.collect.impl.hash;

import net.openhft.collect.CharHashConfig;
import net.openhft.function.CharShortConsumer;
import net.openhft.collect.map.CharShortMap;
import net.openhft.collect.map.hash.HashCharShortMapFactory;

import java.util.Map;


public abstract class HashCharShortMapFactorySO/*<>*/
        extends CharHashFactory<MutableDHashCharShortMapGO/*<>*/>
        implements HashCharShortMapFactory/*<>*/ {

    HashCharShortMapFactorySO(CharHashConfig conf) {
        super(conf);
    }

    /* define p1 *//* if obj value //<V2 extends V>// endif *//* enddefine */

    /* define p2 *//* if obj value //<V2>// endif *//* enddefine */

    /* define pv *//* if !(obj value) //short// elif obj value //V2// endif *//* enddefine */

    @Override
    MutableDHashCharShortMapGO/*<>*/ createNew(float loadFactor, int expectedSize, char free,
            char removed) {
        MutableDHashCharShortMapGO/*<>*/ map = uninitializedMutableMap();
        map.init(loadFactor, expectedSize, free, removed);
        return map;
    }

    /*p1*/ MutableDHashCharShortMapGO/*p2*/ uninitializedMutableMap() {
        return new MutableDHashCharShortMap/*p2*/();
    }

    /*p1*/ ImmutableDHashCharShortMapGO/*p2*/ uninitializedImmutableMap() {
        return new ImmutableDHashCharShortMap/*p2*/();
    }

    @Override
    public /*p1*/ MutableDHashCharShortMapGO/*p2*/ newMutableMap(int expectedSize) {
        // noinspection unchecked
        return (MutableDHashCharShortMapGO/*p2*/) newHash(expectedSize);
    }

    /* define ev */
    /* if !(obj value) //Short// elif obj value //? extends V2// endif */
    /* enddefine */

    @Override
    public /*p1*/ MutableDHashCharShortMapGO/*p2*/ newMutableMap(
            Map<Character, /*ev*/Short/**/> map) {
        if (map instanceof CharShortMap) {
            if (map instanceof CharShortDHash) {
                CharShortDHash hash = (CharShortDHash) map;
                if (hash.loadFactor() == hashConf.getLoadFactor()) {
                    MutableDHashCharShortMapGO/*p2*/ res = uninitializedMutableMap();
                    res.copy(hash);
                    return res;
                }
            }
            final MutableDHashCharShortMapGO/*p2*/ res = newMutableMap(map.size());
            // noinspection unchecked
            ((CharShortMap/* if obj value //<? extends V2>// endif */) map)
                    .forEach(new /*f*/CharShortConsumer/*p2*/() {
                        @Override
                        public void accept(char k, /*pv*/short/**/ v) {
                            res.put(k, v);
                        }
                    });
            return res;
        }
        MutableDHashCharShortMapGO/*p2*/ res = newMutableMap(map.size());
        for (Map.Entry<Character, /*ev*/Short/**/> entry : map.entrySet()) {
            res.put(entry.getKey(), entry.getValue());
        }
        return res;
    }


}
