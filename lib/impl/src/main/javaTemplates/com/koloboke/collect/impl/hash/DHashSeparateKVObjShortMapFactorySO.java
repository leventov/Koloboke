/* with
 DHash|QHash|LHash hash
 obj key
 short|byte|char|int|long|float|double|obj value
 Separate|Parallel kv
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

package com.koloboke.collect.impl.hash;

import com.koloboke.collect.*;
import com.koloboke.collect.hash.*;
import com.koloboke.collect.map.ObjShortMap;
import com.koloboke.collect.map.hash.HashObjShortMapFactory;

import javax.annotation.Nonnull;

import java.util.Map;


public abstract class DHashSeparateKVObjShortMapFactorySO<K/* if obj value //, V// endif */>
        extends ObjHashFactorySO<K>
        implements HashObjShortMapFactory<K/* if obj value //, V// endif */> {

    DHashSeparateKVObjShortMapFactorySO(HashConfig hashConf, int defaultExpectedSize,
            boolean isNullKeyAllowed) {
        super(hashConf, defaultExpectedSize, isNullKeyAllowed);
    }

    @Nonnull
    @Override
    public Equivalence<K> getKeyEquivalence() {
        return Equivalence.defaultEquality();
    }

    @Nonnull
    @Override
    Equivalence<K> getEquivalence() {
        return getKeyEquivalence();
    }

    String keySpecialString() {
        return ",keyEquivalence=" + getKeyEquivalence() +
                ",nullKeyAllowed=" + isNullKeyAllowed();
    }

    boolean keySpecialEquals(HashObjShortMapFactory/*<?>*/ other) {
        return getKeyEquivalence().equals(other.getKeyEquivalence()) &&
                isNullKeyAllowed() == other.isNullKeyAllowed();
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

    /* with Mutable|Updatable|Immutable mutability */
    /*p1*/<K2 extends K>/**/ MutableDHashSeparateKVObjShortMapGO/*p2*/<K2>/**/
    uninitializedMutableMap() {
        return new MutableDHashSeparateKVObjShortMap/*p2*/<K2>/**/();
    }
    /* endwith */

    /* with Mutable|Updatable mutability */
    @Override
    @Nonnull
    public /*p1*/<K2 extends K>/**/ MutableDHashSeparateKVObjShortMapGO/*p2*/<K2>/**/ newMutableMap(
            int expectedSize) {
        MutableDHashSeparateKVObjShortMapGO/*p2*/<K2>/**/ map = uninitializedMutableMap();
        map.init(configWrapper, expectedSize);
        return map;
    }

    /* if Updatable mutability */
    @Override
    @Nonnull
    public /*p1*/<K2 extends K>/**/ MutableDHashSeparateKVObjShortMapGO/*p2*/<K2>/**/ newMutableMap(
            Map/*ep*/<? extends K2, Short>/**/ map, int expectedSize) {
        if (map instanceof ObjShortMap) {
            // noinspection unchecked
            ObjShortMap/*p2*/<K2>/**/ objShortMap = (ObjShortMap/*p2*/<K2>/**/) map;
            if (map instanceof SeparateKVObjShortDHash) {
                SeparateKVObjShortDHash hash = (SeparateKVObjShortDHash) map;
                if (hash.hashConfig().equals(hashConf) &&
                        objShortMap.keyEquivalence().equals(getKeyEquivalence())) {
                    MutableDHashSeparateKVObjShortMapGO/*p2*/<K2>/**/ res =
                            uninitializedMutableMap();
                    res.copy(hash);
                    return res;
                }
            }
            MutableDHashSeparateKVObjShortMapGO/*p2*/<K2>/**/ res = newMutableMap(expectedSize);
            res.putAll(map);
            return res;
        } else {
            MutableDHashSeparateKVObjShortMapGO/*p2*/<K2>/**/ res = newMutableMap(expectedSize);
            for (Map.Entry<? extends K2, /*ev*/Short/**/> entry : map.entrySet()) {
                res.put(entry.getKey(), entry.getValue());
            }
            return res;
        }
    }
    /* endif */
    /* endwith */
}
