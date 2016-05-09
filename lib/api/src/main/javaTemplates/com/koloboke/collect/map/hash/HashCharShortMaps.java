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

import com.koloboke.collect.map.CharShortMap;
import com.koloboke.function.Consumer;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.ServiceLoader;


/**
 * This class consists only of static factory methods to construct {@code HashCharShortMap}s, and
 * the default {@link HashCharShortMapFactory} static provider ({@link #getDefaultFactory()}).
 *
 * @see HashCharShortMap
 */
public final class HashCharShortMaps {
    
    private static class DefaultFactoryHolder {
        private static final HashCharShortMapFactory defaultFactory =
                ServiceLoader.load(HashCharShortMapFactory.class).iterator().next();
    }

    /**
     * Returns the default {@link HashCharShortMapFactory} implementation, to which
     * all static methods in this class delegate.
     *
     // if obj key // * @param <K> the most general key type of the maps that could
                               be constructed by the returned factory // endif //
     // if obj value // * @param <V> the most general value type of the maps that could
                               be constructed by the returned factory // endif //
     * @return the default {@link HashCharShortMapFactory} implementation
     * @throws RuntimeException if no implementations
     *         of {@link HashCharShortMapFactory} are provided
     */
    @Nonnull
    public static /*<>*/ HashCharShortMapFactory/*<>*/ getDefaultFactory() {
        return (HashCharShortMapFactory/*<>*/) DefaultFactoryHolder.defaultFactory;
    }

    /* define ek */
    /* if obj key //? extends K// elif !(obj key) //Character// endif */
    /* enddefine */

    /* define ev */
    /* if obj value //? extends V// elif !(obj value) //Short// endif */
    /* enddefine */

    /* define ep //<// ek //, // ev //>// enddefine */

    /* define typeParams //
     // if obj key // * @param <K> the key type of the returned map // endif //
     // if obj value // * @param <V> the value type of the returned map // endif //
    // enddefine */

    /* with Mutable|Updatable|Immutable mutability */

    /* if !(Immutable mutability) */
    /**
     * Constructs a new empty mutable map of the default expected size.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashCharShortMapFactory#newMutableMap() newMutableMap()}.
     *
     // typeParams //
     * @return a new empty mutable map
     */
    @Nonnull
    public static /*<>*/ HashCharShortMap/*<>*/ newMutableMap() {
        return getDefaultFactory()./*<>*/newMutableMap();
    }

    /**
     * Constructs a new empty mutable map of the given expected size.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashCharShortMapFactory#newMutableMap(int) newMutableMap(expectedSize)}.
     *
     * @param expectedSize the expected size of the returned map
    // typeParams //
     * @return a new empty mutable map of the given expected size
     */
    @Nonnull
    public static /*<>*/ HashCharShortMap/*<>*/ newMutableMap(int expectedSize) {
        return getDefaultFactory()./*<>*/newMutableMap(expectedSize);
    }
    /* endif */

    /* with with|without expectedSize */
    /* define arg *//* if with expectedSize //, int expectedSize// endif *//* enddefine */
    /* define apply *//* if with expectedSize //, expectedSize// endif *//* enddefine */
    /* define lk *//* if with expectedSize //, int// endif *//* enddefine */
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
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashCharShortMapFactory#newMutableMap(
     * Map//lk//) newMutableMap(map//apply//)}.
     *
     * @param map the map whose mappings are to be placed in the returned map
     // expectedParam //
    // typeParams //
     * @return a new mutable map with the same mappings as the specified {@code map}
     */
    @Nonnull
    public static /*<>*/ HashCharShortMap/*<>*/ newMutableMap(
            @Nonnull Map/*ep*/<Character, Short>/**/ map/*arg*/) {
        return getDefaultFactory()./*<>*/newMutableMap(map/*apply*/);
    }
    /* endif */

    /**
     * Constructs a new mutable map which merge the mappings of the specified maps. On conflict,
     * mappings from the {@code map2} have priority over mappings from the {@code map1} with
     * the same keys.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashCharShortMapFactory#newMutableMap(Map,
     * Map//lk//) newMutableMap(map1, map2//apply//)}.
     *
     * @param map1 the first map to merge
     * @param map2 the second map to merge
    // expectedParam //
    // typeParams //
     * @return a new mutable map which merge the mappings of the specified maps
     */
    @Nonnull
    public static /*<>*/ HashCharShortMap/*<>*/ newMutableMap(
            @Nonnull Map/*ep*/<Character, Short>/**/ map1,
            @Nonnull Map/*ep*/<Character, Short>/**/ map2/*arg*/) {
        return getDefaultFactory()./*<>*/newMutableMap(map1, map2/*apply*/);
    }

    /**
     * Constructs a new mutable map which merge the mappings of the specified maps. On conflict,
     * mappings from the maps passed later in the argument list have priority over mappings
     * from the maps passed earlier with the same keys.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashCharShortMapFactory#newMutableMap(Map, Map,
     * Map//lk//) newMutableMap(map1, map2, map3//apply//)}.
     *
     * @param map1 the first map to merge
     * @param map2 the second map to merge
     * @param map3 the third map to merge
    // expectedParam //
    // typeParams //
     * @return a new mutable map which merge the mappings of the specified maps
     */
    @Nonnull
    public static /*<>*/ HashCharShortMap/*<>*/ newMutableMap(
            @Nonnull Map/*ep*/<Character, Short>/**/ map1,
            @Nonnull Map/*ep*/<Character, Short>/**/ map2,
            @Nonnull Map/*ep*/<Character, Short>/**/ map3/*arg*/) {
        return getDefaultFactory()./*<>*/newMutableMap(map1, map2, map3/*apply*/);
    }

    /**
     * Constructs a new mutable map which merge the mappings of the specified maps. On conflict,
     * mappings from the maps passed later in the argument list have priority over mappings
     * from the maps passed earlier with the same keys.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashCharShortMapFactory#newMutableMap(Map, Map,
     * Map, Map//lk//) newMutableMap(map1, map2, map3, map4//apply//)}.
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
    public static /*<>*/ HashCharShortMap/*<>*/ newMutableMap(
            @Nonnull Map/*ep*/<Character, Short>/**/ map1,
            @Nonnull Map/*ep*/<Character, Short>/**/ map2,
            @Nonnull Map/*ep*/<Character, Short>/**/ map3,
            @Nonnull Map/*ep*/<Character, Short>/**/ map4/*arg*/) {
        return getDefaultFactory()./*<>*/newMutableMap(map1, map2, map3, map4/*apply*/);
    }

    /**
     * Constructs a new mutable map which merge the mappings of the specified maps. On conflict,
     * mappings from the maps passed later in the argument list have priority over mappings
     * from the maps passed earlier with the same keys.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashCharShortMapFactory#newMutableMap(Map, Map,
     * Map, Map, Map//lk//) newMutableMap(map1, map2, map3, map4, map5//apply//)}.
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
    public static /*<>*/ HashCharShortMap/*<>*/ newMutableMap(
            @Nonnull Map/*ep*/<Character, Short>/**/ map1,
            @Nonnull Map/*ep*/<Character, Short>/**/ map2,
            @Nonnull Map/*ep*/<Character, Short>/**/ map3,
            @Nonnull Map/*ep*/<Character, Short>/**/ map4,
            @Nonnull Map/*ep*/<Character, Short>/**/ map5/*arg*/) {
        return getDefaultFactory()./*<>*/newMutableMap(map1, map2, map3, map4, map5/*apply*/);
    }

    /**
     * Constructs a new mutable map filled with mappings consumed by the callback within the given
     * closure. Mappings supplied later within the closure have priority over the mappings
     * passed earlier with the same keys.
     *
     * <p>Example: TODO
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashCharShortMapFactory#newMutableMap(
     * Consumer//lk//) newMutableMap(entriesSupplier//apply//)}.
     *
     * @param entriesSupplier the function which supply mappings for the returned map via
     *        the callback passed in
    // expectedParam //
    // typeParams //
     * @return a new mutable map with mappings consumed by the callback within the given closure
     */
    @Nonnull
    public static /*<>*/ HashCharShortMap/*<>*/ newMutableMap(@Nonnull
            Consumer<com.koloboke.function./*f*/CharShortConsumer/*<>*/> entriesSupplier
            /*arg*/) {
        return getDefaultFactory()./*<>*/newMutableMap(entriesSupplier/*apply*/);
    }

    /**
     * Constructs a new mutable map with the given mappings, i. e. pairs of elements from
     * the {@code keys} and {@code values} arrays at the same index. If {@code keys} array have
     * duplicate elements, value corresponding the key with the highest index is left
     * in the returned map.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashCharShortMapFactory#newMutableMap(//raw//char[], //raw//short[]//lk//
     * ) newMutableMap(keys, values//apply//)}.
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
    public static /*<>*/ HashCharShortMap/*<>*/ newMutableMap(
            @Nonnull char[] keys, @Nonnull short[] values/*arg*/) {
        return getDefaultFactory()./*<>*/newMutableMap(keys, values/*apply*/);
    }

    /* if !(obj key obj value) */
    /**
     * Constructs a new mutable map with the given mappings, i. e. pairs of elements from
     * the {@code keys} and {@code values} arrays at the same index. If {@code keys} array have
     * duplicate elements, value corresponding the key with the highest index is left
     * in the returned map.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashCharShortMapFactory#newMutableMap(//raw//Character[],
     * //raw//Short[]//lk//) newMutableMap(keys, values//apply//)}.
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
    public static /*<>*/ HashCharShortMap/*<>*/ newMutableMap(
            @Nonnull Character[] keys, @Nonnull Short[] values/*arg*/) {
        return getDefaultFactory()./*<>*/newMutableMap(keys, values/*apply*/);
    }
    /* endif */

    /**
     * Constructs a new mutable map with the given mappings, i. e. pairs of elements from
     * the {@code keys} and {@code values} iterables at the same iteration position. If {@code keys}
     * have duplicate elements, value corresponding the key appeared last in the iteration is left
     * in the returned map.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashCharShortMapFactory#newMutableMap(Iterable, Iterable//lk//
     * ) newMutableMap(keys, values//apply//)}.
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
    public static /*<>*/ HashCharShortMap/*<>*/ newMutableMap(
            @Nonnull Iterable</*ek*/Character/**/> keys, @Nonnull Iterable</*ev*/Short/**/> values
            /*arg*/) {
        return getDefaultFactory()./*<>*/newMutableMap(keys, values/*apply*/);
    }
    /* endwith */

    /**
     * Constructs a new mutable map of the single specified mapping.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashCharShortMapFactory#newMutableMapOf(//raw//char, //raw//short
     * ) newMutableMapOf(k1, v1)}.
     *
     * @param k1 the key of the sole mapping
     * @param v1 the value of the sole mapping
    // typeParams //
     * @return a new mutable map of the single specified mapping
     */
    @Nonnull
    public static /*<>*/ HashCharShortMap/*<>*/ newMutableMapOf(
            char k1, short v1) {
        return getDefaultFactory()./*<>*/newMutableMapOf(k1, v1);
    }

    /**
     * Constructs a new mutable map of the two specified mappings.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashCharShortMapFactory#newMutableMapOf(//raw//char, //raw//short,
     * //raw//char, //raw//short) newMutableMapOf(k1, v1, k2, v2)}.
     *
     * @param k1 the key of the first mapping
     * @param v1 the value of the first mapping
     * @param k2 the key of the second mapping
     * @param v2 the value of the second mapping
    // typeParams //
     * @return a new mutable map of the two specified mappings
     */
    @Nonnull
    public static /*<>*/ HashCharShortMap/*<>*/ newMutableMapOf(
            char k1, short v1, char k2, short v2) {
        return getDefaultFactory()./*<>*/newMutableMapOf(k1, v1, k2, v2);
    }

    /**
     * Constructs a new mutable map of the three specified mappings.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashCharShortMapFactory#newMutableMapOf(//raw//char, //raw//short,
     * //raw//char, //raw//short, //raw//char, //raw//short) newMutableMapOf(k1, v1, k2, v2,
     * k3, v3)}.
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
    public static /*<>*/ HashCharShortMap/*<>*/ newMutableMapOf(
            char k1, short v1, char k2, short v2,
            char k3, short v3) {
        return getDefaultFactory()./*<>*/newMutableMapOf(k1, v1, k2, v2, k3, v3);
    }

    /**
     * Constructs a new mutable map of the four specified mappings.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashCharShortMapFactory#newMutableMapOf(//raw//char, //raw//short,
     * //raw//char, //raw//short, //raw//char, //raw//short, //raw//char, //raw//short
     * ) newMutableMapOf(k1, v1, k2, v2, k3, v3, k4, v4)}.
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
    public static /*<>*/ HashCharShortMap/*<>*/ newMutableMapOf(
            char k1, short v1, char k2, short v2,
            char k3, short v3, char k4, short v4) {
        return getDefaultFactory()./*<>*/newMutableMapOf(k1, v1, k2, v2, k3, v3, k4, v4);
    }

    /**
     * Constructs a new mutable map of the five specified mappings.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashCharShortMapFactory#newMutableMapOf(//raw//char, //raw//short,
     * //raw//char, //raw//short, //raw//char, //raw//short, //raw//char, //raw//short,
     * //raw//char, //raw//short) newMutableMapOf(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5)}.
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
    public static /*<>*/ HashCharShortMap/*<>*/ newMutableMapOf(
            char k1, short v1, char k2, short v2,
            char k3, short v3, char k4, short v4,
            char k5, short v5) {
        return getDefaultFactory()./*<>*/newMutableMapOf(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5);
    }
    /* endwith */

    private HashCharShortMaps() {}
}
