/* with
 obj key
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

import net.openhft.collect.*;
import net.openhft.function.*;
import net.openhft.collect.map.ObjShortMap;
import net.openhft.collect.map.hash.HashObjShortMapFactory;
import org.jetbrains.annotations.Nullable;

import java.util.Map;


public abstract class HashObjShortMapFactorySO<K/* if obj value //, V// endif */>
         implements HashObjShortMapFactory<K/* if obj value //, V// endif */> {

    final ObjHashConfig conf;
    final HashConfig hashConf;

    HashObjShortMapFactorySO(ObjHashConfig conf) {
        this.conf = conf;
        this.hashConf = conf.getHashConfig();
    }

    @Override
    public ObjHashConfig getConfig() {
        return conf;
    }

    @Nullable
    @Override
    public Equivalence<K> getKeyEquivalence() {
        return null;
    }

    /* define p1 */
    /* if obj key obj value //<K2 extends K, V2 extends V>// elif obj key //<K2 extends K>
    // elif obj value //<V2 extends V>// endif */
    /* enddefine */

    /* define p2 */
    /* if obj key obj value //<K2, V2>// elif obj key //<K2>// elif obj value //<V2>// endif */
    /* enddefine */

    /* define ek */
    /* if obj key //? extends K2// elif !(obj key) //Character// endif */
    /* enddefine */

    /* define ev */
    /* if obj value //? extends V2// elif !(obj value) //Short// endif */
    /* enddefine */

    /* define ep //<// ek //, // ev //>// enddefine */

    /* define ep2 */
    /* if obj key obj value //<? extends K2, ? extends V2>// elif obj key //<? extends K2>
    // elif obj value //<? extends V2>// endif */
    /* enddefine */

    /* define pv *//* if !(obj value) //short// elif obj value //V2// endif *//* enddefine */

    /*p1*/<K2 extends K>/**/ MutableDHashObjShortMapGO/*p2*/<K2>/**/ uninitializedMutableMap() {
        return new MutableDHashObjShortMap/*p2*/<K2>/**/();
    }

    /*p1*/<K2 extends K>/**/ ImmutableDHashObjShortMapGO/*p2*/<K2>/**/ uninitializedImmutableMap() {
        return new ImmutableDHashObjShortMap/*p2*/<K2>/**/();
    }

    @Override
    public /*p1*/<K2 extends K>/**/ MutableDHashObjShortMapGO/*p2*/<K2>/**/ newMutableMap(
            int expectedSize) {
        MutableDHashObjShortMapGO/*p2*/<K2>/**/ map = uninitializedMutableMap();
        map.init(hashConf.getLoadFactor(), expectedSize);
        return map;
    }

    @Override
    public /*p1*/<K2 extends K>/**/ MutableDHashObjShortMapGO/*p2*/<K2>/**/ newMutableMap(
            Map/*ep*/<? extends K2, Short>/**/ map, int expectedSize) {
        if (map instanceof ObjShortMap) {
            // noinspection unchecked
            ObjShortMap/*p2*/<K2>/**/ objShortMap = (ObjShortMap/*p2*/<K2>/**/) map;
            if (map instanceof ObjShortDHash) {
                ObjShortDHash hash = (ObjShortDHash) map;
                if (hash.loadFactor() == hashConf.getLoadFactor() &&
                        NullableObjects.equals(objShortMap.keyEquivalence(), getKeyEquivalence())) {
                    MutableDHashObjShortMapGO/*p2*/<K2>/**/ res = uninitializedMutableMap();
                    res.copy(hash);
                    return res;
                }
            }
            final MutableDHashObjShortMapGO/*p2*/<K2>/**/ res = newMutableMap(expectedSize);
            objShortMap.forEach(new /*f*/ObjShortConsumer/*p2*/<K2>/**/() {
                @Override
                public void accept(K2 k, /*pv*/short/**/ v) {
                    res.put(k, v);
                }
            });
            return res;
        } else {
            MutableDHashObjShortMapGO/*p2*/<K2>/**/ res = newMutableMap(expectedSize);
            for (Map.Entry<? extends K2, /*ev*/Short/**/> entry : map.entrySet()) {
                res.put(entry.getKey(), entry.getValue());
            }
            return res;
        }
    }
}
