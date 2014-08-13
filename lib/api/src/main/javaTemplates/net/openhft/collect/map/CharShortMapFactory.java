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

package net.openhft.collect.map;

import net.openhft.collect.*;
import net.openhft.function.Consumer;

import javax.annotation.Nonnull;

import java.util.Map;

/* define bp */
/* if obj key obj value //K, V, // elif obj key //K, // elif obj value //V, // endif */
/* enddefine */

/**
 * An immutable factory of {@code CharShortMap}s.
 *
 * // if obj key// @param <K> the most general key type of the maps that could be constructed
 *                        by this factory // endif //
 * // if obj value // @param <V> the most general value type of the maps that could be constructed
 *                           by this factory // endif //
 * @param <F> the concrete factory type which extends this interface
 * @see CharShortMap
 */
public interface CharShortMapFactory</*bp*/F extends CharShortMapFactory</*bp*/F>>
        extends ContainerFactory<F> {

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
     * Returns the equivalence to which {@linkplain CharShortMap#keyEquivalence() key equivalence}
     * of the maps constructed by this factory is set.
     *
     * @return the key equivalence of the maps constructed by this factory
     */
    @Nonnull Equivalence<Character> getKeyEquivalence();
    /* endif */

    /* if obj value */
    /**
     * Returns the equivalence to which {@linkplain CharShortMap#valueEquivalence() value
     * equivalence} of the maps constructed by this factory is set. Defaults
     * to {@link Equivalence#defaultEquality()}.
     *
     * @return the value equivalence of the maps constructed by this factory
     */
    @Nonnull Equivalence<Short> getValueEquivalence();

    /**
     * Returns a copy of this factory, with exception that it constructs maps with
     * {@linkplain CharShortMap#valueEquivalence() value equivalence} set to the given
     * {@code Equivalence}.
     *
     * @param valueEquivalence the new value equivalence
     * @return a copy of this factory, which constructs maps with the given {@code valueEquivalence}
     */
    @Nonnull
    F withValueEquivalence(@Nonnull Equivalence<? super V> valueEquivalence);

    /* elif !(obj value) */

    /**
     * Returns the value to which {@linkplain CharShortMap#defaultValue() default value} of the maps
     * constructed by this factory is set. Default value is {@code
     * // const value 0 //0// endconst //}.
     *
     * @return the default value of the maps constructed by this factory
     */
    short getDefaultValue();

    /**
     * Returns a copy of this factory, with exception that it constructs maps with
     * {@linkplain CharShortMap#defaultValue() default value} set to the given {@code short} value.
     *
     * @param defaultValue the new default {@code short} value
     * @return a copy of this factory, which constructs maps with the given {@code defaultValue}
     */
    @Nonnull
    F withDefaultValue(short defaultValue);
    /* endif */

    /* define typeParams //
     // if obj key // * @param <K2> the key type of the returned map // endif //
     // if obj value // * @param <V2> the value type of the returned map // endif //
    // enddefine */

    /* with Mutable|Updatable|Immutable mutability */
    /* if !(Immutable mutability) */
    /**
     * Constructs a new empty mutable map of the {@linkplain #getDefaultExpectedSize()
     * default expected size}.
     *
    // typeParams //
     * @return a new empty mutable map
     */
    @Nonnull
    /*p1*/ CharShortMap/*p2*/ newMutableMap();

    /**
     * Constructs a new empty mutable map of the given expected size.
     *
     * @param expectedSize the expected size of the returned map
    // typeParams //
     * @return a new empty mutable map of the given expected size
     */
    @Nonnull
    /*p1*/ CharShortMap/*p2*/ newMutableMap(int expectedSize);
    /* endif */

    /* with with|without expectedSize */
    /* define arg *//* if with expectedSize //, int expectedSize// endif *//* enddefine */

    /* define expectedParam //
    * // if with expectedSize //@param expectedSize the expected size of the returned map// endif //
    // enddefine */

    /* if obj key || without expectedSize */
    /**
     * Constructs a new mutable map with the same mappings as the specified {@code map}.
     *
     * // if with expectedSize //
     * <p>If the specified map is an instance of {@code CharShortMap} and has the same
     * {@linkplain CharShortMap#keyEquivalence() key equivalence} with this factory (and thus
     * the constructed map), the {@code expectedSize} argument is ignored.
     * // endif //
     *
     * @param map the map whose mappings are to be placed in the returned map
     // expectedParam //
    // typeParams //
     * @return a new mutable map with the same mappings as the specified {@code map}
     */
    @Nonnull
    /*p1*/ CharShortMap/*p2*/ newMutableMap(@Nonnull Map/*ep*/<Character, Short>/**/ map/*arg*/);
    /* endif */

    /**
     * Constructs a new mutable map which merge the mappings of the specified maps. On conflict,
     * mappings from the {@code map2} have priority over mappings from the {@code map1} with
     * the same keys.
     *
     * @param map1 the first map to merge
     * @param map2 the second map to merge
     // expectedParam //
    // typeParams //
     * @return a new mutable map which merge the mappings of the specified maps
     */
    @Nonnull
    /*p1*/ CharShortMap/*p2*/ newMutableMap(@Nonnull Map/*ep*/<Character, Short>/**/ map1,
            @Nonnull Map/*ep*/<Character, Short>/**/ map2/*arg*/);

    /**
     * Constructs a new mutable map which merge the mappings of the specified maps. On conflict,
     * mappings from the maps passed later in the argument list have priority over mappings
     * from the maps passed earlier with the same keys.
     *
     * @param map1 the first map to merge
     * @param map2 the second map to merge
     * @param map3 the third map to merge
     // expectedParam //
    // typeParams //
     * @return a new mutable map which merge the mappings of the specified maps
     */
    @Nonnull
    /*p1*/ CharShortMap/*p2*/ newMutableMap(@Nonnull Map/*ep*/<Character, Short>/**/ map1,
            @Nonnull Map/*ep*/<Character, Short>/**/ map2,
            @Nonnull Map/*ep*/<Character, Short>/**/ map3/*arg*/);

    /**
     * Constructs a new mutable map which merge the mappings of the specified maps. On conflict,
     * mappings from the maps passed later in the argument list have priority over mappings
     * from the maps passed earlier with the same keys.
     *
     * @param map1 the first map to merge
     * @param map2 the second map to merge
     * @param map3 the third map to merge
     * @param map4 the fourth map to merge
     // expectedParam //
    // typeParams //
     * @return a new mutable map which merge the mappings of the specified maps
     */
    @Nonnull
    /*p1*/ CharShortMap/*p2*/ newMutableMap(@Nonnull Map/*ep*/<Character, Short>/**/ map1,
            @Nonnull Map/*ep*/<Character, Short>/**/ map2,
            @Nonnull Map/*ep*/<Character, Short>/**/ map3,
            @Nonnull Map/*ep*/<Character, Short>/**/ map4/*arg*/);

    /**
     * Constructs a new mutable map which merge the mappings of the specified maps. On conflict,
     * mappings from the maps passed later in the argument list have priority over mappings
     * from the maps passed earlier with the same keys.
     *
     * @param map1 the first map to merge
     * @param map2 the second map to merge
     * @param map3 the third map to merge
     * @param map4 the fourth map to merge
     * @param map5 the fifth map to merge
     // expectedParam //
    // typeParams //
     * @return a new mutable map which merge the mappings of the specified maps
     */
    @Nonnull
    /*p1*/ CharShortMap/*p2*/ newMutableMap(@Nonnull Map/*ep*/<Character, Short>/**/ map1,
            @Nonnull Map/*ep*/<Character, Short>/**/ map2,
            @Nonnull Map/*ep*/<Character, Short>/**/ map3,
            @Nonnull Map/*ep*/<Character, Short>/**/ map4,
            @Nonnull Map/*ep*/<Character, Short>/**/ map5/*arg*/);



    /**
     * Constructs a new mutable map filled with mappings consumed by the callback within the given
     * closure. Mappings supplied later within the closure have priority over the mappings
     * passed earlier with the same keys.
     *
     * <p>Example: TODO
     *
     * @param entriesSupplier the function which supply mappings for the returned map via
     *        the callback passed in
     // expectedParam //
    // typeParams //
     * @return a new mutable map with mappings consumed by the callback within the given closure
     */
    @Nonnull
    /*p1*/ CharShortMap/*p2*/ newMutableMap(
            @Nonnull Consumer<net.openhft.function./*f*/CharShortConsumer/*p2*/> entriesSupplier
            /*arg*/);

    /**
     * Constructs a new mutable map with the given mappings, i. e. pairs of elements from
     * the {@code keys} and {@code values} arrays at the same index. If {@code keys} array have
     * duplicate elements, value corresponding the key with the highest index is left
     * in the returned map.
     *
     * @param keys the keys of the returned map
     * @param values the values of the returned map, each value is associated with the element
     *        of the {@code keys} array at the same index
     // expectedParam //
    // typeParams //
     * @return a new mutable map with the given mappings
     * @throws IllegalArgumentException if {@code keys} and {@code values} arrays have different
     *         length
     */
    @Nonnull
    /*p1*/ CharShortMap/*p2*/ newMutableMap(
            @Nonnull /*pk*/char/**/[] keys, @Nonnull /*pv*/short/**/[] values/*arg*/);

    /* if !(obj key obj value) */
    /**
     * Constructs a new mutable map with the given mappings, i. e. pairs of elements from
     * the {@code keys} and {@code values} arrays at the same index. If {@code keys} array have
     * duplicate elements, value corresponding the key with the highest index is left
     * in the returned map.
     *
     * @param keys the keys of the returned map
     * @param values the values of the returned map, each value is associated with the element
     *        of the {@code keys} array at the same index
     // expectedParam //
    // typeParams //
     * @return a new mutable map with the given mappings
     * @throws IllegalArgumentException if {@code keys} and {@code values} arrays have different
     *         length
     * @throws NullPointerException if // if !(obj key) //{@code keys}// endif //
     *         // if !(obj key) && !(obj value) //or// endif // // if !(obj value) //{@code
     *         values}// endif // contain {@code null} elements
     */
    @Nonnull
    /*p1*/ CharShortMap/*p2*/ newMutableMap(
            @Nonnull /*gk*/Character/**/[] keys, @Nonnull /*gv*/Short/**/[] values/*arg*/);
    /* endif */

    /**
     * Constructs a new mutable map with the given mappings, i. e. pairs of elements from
     * the {@code keys} and {@code values} iterables at the same iteration position. If {@code keys}
     * have duplicate elements, value corresponding the key appeared last in the iteration is left
     * in the returned map.
     *
     * @param keys the keys of the returned map
     * @param values the values of the returned map, each value is associated with the element
     *        of the {@code keys} iterable at the same iteration position
     // expectedParam //
    // typeParams //
     * @return a new mutable map with the given mappings
     * @throws IllegalArgumentException if {@code keys} and {@code values} have different size
     */
    @Nonnull
    /*p1*/ CharShortMap/*p2*/ newMutableMap(@Nonnull Iterable</*ek*/Character/**/> keys,
            @Nonnull Iterable</*ev*/Short/**/> values/*arg*/);
    /* endwith */

    /**
     * Constructs a new mutable map of the single specified mapping.
     *
     * @param k1 the key of the sole mapping
     * @param v1 the value of the sole mapping
    // typeParams //
     * @return a new mutable map of the single specified mapping
     */
    @Nonnull
    /*p1*/ CharShortMap/*p2*/ newMutableMapOf(/*pk*/char/**/ k1, /*pv*/short/**/ v1);

    /**
     * Constructs a new mutable map of the two specified mappings.
     *
     * @param k1 the key of the first mapping
     * @param v1 the value of the first mapping
     * @param k2 the key of the second mapping
     * @param v2 the value of the second mapping
    // typeParams //
     * @return a new mutable map of the two specified mappings
     */
    @Nonnull
    /*p1*/ CharShortMap/*p2*/ newMutableMapOf(/*pk*/char/**/ k1, /*pv*/short/**/ v1,
            /*pk*/char/**/ k2, /*pv*/short/**/ v2);

    /**
     * Constructs a new mutable map of the three specified mappings.
     *
     * @param k1 the key of the first mapping
     * @param v1 the value of the first mapping
     * @param k2 the key of the second mapping
     * @param v2 the value of the second mapping
     * @param k3 the key of the third mapping
     * @param v3 the value of the third mapping
    // typeParams //
     * @return a new mutable map of the three specified mappings
     */
    @Nonnull
    /*p1*/ CharShortMap/*p2*/ newMutableMapOf(/*pk*/char/**/ k1, /*pv*/short/**/ v1,
            /*pk*/char/**/ k2, /*pv*/short/**/ v2, /*pk*/char/**/ k3, /*pv*/short/**/ v3);

    /**
     * Constructs a new mutable map of the four specified mappings.
     *
     * @param k1 the key of the first mapping
     * @param v1 the value of the first mapping
     * @param k2 the key of the second mapping
     * @param v2 the value of the second mapping
     * @param k3 the key of the third mapping
     * @param v3 the value of the third mapping
     * @param k4 the key of the fourth mapping
     * @param v4 the value of the fourth mapping
    // typeParams //
     * @return a new mutable map of the four specified mappings
     */
    @Nonnull
    /*p1*/ CharShortMap/*p2*/ newMutableMapOf(/*pk*/char/**/ k1, /*pv*/short/**/ v1,
            /*pk*/char/**/ k2, /*pv*/short/**/ v2, /*pk*/char/**/ k3, /*pv*/short/**/ v3,
            /*pk*/char/**/ k4, /*pv*/short/**/ v4);

    /**
     * Constructs a new mutable map of the five specified mappings.
     *
     * @param k1 the key of the first mapping
     * @param v1 the value of the first mapping
     * @param k2 the key of the second mapping
     * @param v2 the value of the second mapping
     * @param k3 the key of the third mapping
     * @param v3 the value of the third mapping
     * @param k4 the key of the fourth mapping
     * @param v4 the value of the fourth mapping
     * @param k5 the key of the fifth mapping
     * @param v5 the value of the fifth mapping
    // typeParams //
     * @return a new mutable map of the five specified mappings
     */
    @Nonnull
    /*p1*/ CharShortMap/*p2*/ newMutableMapOf(/*pk*/char/**/ k1, /*pv*/short/**/ v1,
            /*pk*/char/**/ k2, /*pv*/short/**/ v2, /*pk*/char/**/ k3, /*pv*/short/**/ v3,
            /*pk*/char/**/ k4, /*pv*/short/**/ v4, /*pk*/char/**/ k5, /*pv*/short/**/ v5);
    /* endwith */
}
