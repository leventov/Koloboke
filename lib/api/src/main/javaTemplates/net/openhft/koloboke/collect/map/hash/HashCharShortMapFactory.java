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

package net.openhft.koloboke.collect.map.hash;

import net.openhft.koloboke.collect.*;
import net.openhft.koloboke.collect.hash.*;
import net.openhft.koloboke.function.Consumer;
import net.openhft.koloboke.collect.map.*;

import javax.annotation.Nonnull;

import java.util.Map;

/* define bp */
/* if obj key obj value //K, V, // elif obj key //K, // elif obj value //V, // endif */
/* enddefine */

/**
 * An immutable factory of {@code HashCharShortMap}s.
 *
 * @see HashCharShortMap
 * @see HashCharShortMaps#getDefaultFactory()
 */
public interface HashCharShortMapFactory/*<>*/
        extends CharShortMapFactory</*bp*/HashCharShortMapFactory/*<>*/>
        /* if !(float|double key) */, CharHashFactory<HashCharShortMapFactory/*<>*/>
        /* elif float|double key */, HashContainerFactory<HashCharShortMapFactory/*<>*/>/* endif */{

    /* if obj key */
    /**
     * {@inheritDoc} Defaults to {@link Equivalence#defaultEquality()}.
     */
    @Override
    @Nonnull Equivalence<Character> getKeyEquivalence();
    /* endif */

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

    /* define pk *//* if !(obj key) //char// elif obj key //K2// endif *//* enddefine */
    /* define pv *//* if !(obj value) //short// elif obj value //V2// endif *//* enddefine */

    /* define gk *//* if !(obj key) //Character// elif obj key //K2// endif *//* enddefine */
    /* define gv *//* if !(obj value) //Short// elif obj value //V2// endif *//* enddefine */

    /* if obj key */
    /**
     * Returns a copy of this factory, with exception that it constructs maps with
     * {@linkplain HashObjShortMap#keyEquivalence() key equivalence} set to the given
     * {@code Equivalence}.
     *
     * @param keyEquivalence the new key equivalence
     * @return a copy of this factory, which constructs maps with the given {@code keyEquivalence}
     */
    @Nonnull
    HashCharShortMapFactory<K/* if obj value */, Short/* endif */>
    withKeyEquivalence(@Nonnull Equivalence<? super K> keyEquivalence);
    /* endif */

    /* with Mutable|Updatable|Immutable mutability */
    /* if !(Immutable mutability) */
    @Override
    @Nonnull
    /*p1*/ HashCharShortMap/*p2*/ newMutableMap();

    @Override
    @Nonnull
    /*p1*/ HashCharShortMap/*p2*/ newMutableMap(int expectedSize);
    /* endif */

    /* with with|without expectedSize */
    /* define arg *//* if with expectedSize //, int expectedSize// endif *//* enddefine */

    /* if obj key || without expectedSize */
    @Override
    @Nonnull
    /*p1*/ HashCharShortMap/*p2*/ newMutableMap(
            @Nonnull Map/*ep*/<Character, Short>/**/ map/*arg*/);
    /* endif */

    @Override
    @Nonnull
    /*p1*/ HashCharShortMap/*p2*/ newMutableMap(@Nonnull Map/*ep*/<Character, Short>/**/ map1,
            @Nonnull Map/*ep*/<Character, Short>/**/ map2/*arg*/);

    @Override
    @Nonnull
    /*p1*/ HashCharShortMap/*p2*/ newMutableMap(@Nonnull Map/*ep*/<Character, Short>/**/ map1,
            @Nonnull Map/*ep*/<Character, Short>/**/ map2,
            @Nonnull Map/*ep*/<Character, Short>/**/ map3/*arg*/);

    @Override
    @Nonnull
    /*p1*/ HashCharShortMap/*p2*/ newMutableMap(@Nonnull Map/*ep*/<Character, Short>/**/ map1,
            @Nonnull Map/*ep*/<Character, Short>/**/ map2,
            @Nonnull Map/*ep*/<Character, Short>/**/ map3,
            @Nonnull Map/*ep*/<Character, Short>/**/ map4/*arg*/);

    @Override
    @Nonnull
    /*p1*/ HashCharShortMap/*p2*/ newMutableMap(@Nonnull Map/*ep*/<Character, Short>/**/ map1,
            @Nonnull Map/*ep*/<Character, Short>/**/ map2,
            @Nonnull Map/*ep*/<Character, Short>/**/ map3,
            @Nonnull Map/*ep*/<Character, Short>/**/ map4,
            @Nonnull Map/*ep*/<Character, Short>/**/ map5/*arg*/);



    @Override
    @Nonnull
    /*p1*/ HashCharShortMap/*p2*/ newMutableMap(@Nonnull
            Consumer<net.openhft.koloboke.function./*f*/CharShortConsumer/*p2*/> entriesSupplier
            /*arg*/);

    @Override
    @Nonnull
    /*p1*/ HashCharShortMap/*p2*/ newMutableMap(
            @Nonnull /*pk*/char/**/[] keys, @Nonnull /*pv*/short/**/[] values/*arg*/);

    /* if !(obj key obj value) */
    @Override
    @Nonnull
    /*p1*/ HashCharShortMap/*p2*/ newMutableMap(
            @Nonnull /*gk*/Character/**/[] keys, @Nonnull /*gv*/Short/**/[] values/*arg*/);
    /* endif */

    @Override
    @Nonnull
    /*p1*/ HashCharShortMap/*p2*/ newMutableMap(@Nonnull Iterable</*ek*/Character/**/> keys,
            @Nonnull Iterable</*ev*/Short/**/> values/*arg*/);

    /* endwith */

    @Override
    @Nonnull
    /*p1*/ HashCharShortMap/*p2*/ newMutableMapOf(/*pk*/char/**/ k1, /*pv*/short/**/ v1);

    @Override
    @Nonnull
    /*p1*/ HashCharShortMap/*p2*/ newMutableMapOf(/*pk*/char/**/ k1, /*pv*/short/**/ v1,
            /*pk*/char/**/ k2, /*pv*/short/**/ v2);

    @Override
    @Nonnull
    /*p1*/ HashCharShortMap/*p2*/ newMutableMapOf(/*pk*/char/**/ k1, /*pv*/short/**/ v1,
            /*pk*/char/**/ k2, /*pv*/short/**/ v2, /*pk*/char/**/ k3, /*pv*/short/**/ v3);

    @Override
    @Nonnull
    /*p1*/ HashCharShortMap/*p2*/ newMutableMapOf(/*pk*/char/**/ k1, /*pv*/short/**/ v1,
            /*pk*/char/**/ k2, /*pv*/short/**/ v2, /*pk*/char/**/ k3, /*pv*/short/**/ v3,
            /*pk*/char/**/ k4, /*pv*/short/**/ v4);

    @Override
    @Nonnull
    /*p1*/ HashCharShortMap/*p2*/ newMutableMapOf(/*pk*/char/**/ k1, /*pv*/short/**/ v1,
            /*pk*/char/**/ k2, /*pv*/short/**/ v2, /*pk*/char/**/ k3, /*pv*/short/**/ v3,
            /*pk*/char/**/ k4, /*pv*/short/**/ v4, /*pk*/char/**/ k5, /*pv*/short/**/ v5);
    /* endwith */
}
