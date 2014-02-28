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


import net.openhft.collect.Container;
import net.openhft.collect.Equivalence;
import net.openhft.function.*;
import net.openhft.collect.ShortCollection;
import net.openhft.collect.set.CharSet;
import net.openhft.collect.set.ObjSet;
import org.jetbrains.annotations.NotNull;

import java.util.Map;


/**
 * Interface for a {@link java.util.Map} specialization with // raw //char keys
 * and // raw //short values.
 */
public interface CharShortMap/*<>*/ extends Map<Character, Short>, Container {

    /* if obj key */
    Equivalence<Character> keyEquivalence();
    /* endif */

    /* if obj value */
    Equivalence<Short> valueEquivalence();
    /* endif */
    /* if !(obj value) */
    short defaultValue();
    /* endif */


    /* if !(obj key) */
    /**
     * @deprecated Use specialization {@link #containsKey(char)} instead
     */
    @Override
    @Deprecated
    boolean containsKey (Object key);

    boolean containsKey(char key);
    /* endif */

    /* if !(obj value) */
    /**
     * @deprecated Use specialization {@link #containsValue(short)} instead
     */
    @Override
    @Deprecated
    boolean containsValue(Object value);

    boolean containsValue(short value);
    /* endif */

    /* define valueSuffix */
    /* if obj key short|byte|char|int|long|float|double value //$Short// endif */
    /* enddefine */

    /* if !(obj key obj value) */
    /**
     * @deprecated Use specialization {@link #get// valueSuffix //(// raw //char)} instead
     */
    @Override
    @Deprecated
    Short get(Object key);

    short get/* valueSuffix */(/* raw */char key);
    /* endif */


    /* if !(obj key obj value) && JDK8 jdk */
    /**
     * @deprecated Use specialization {@link #getOrDefault(char, short)} instead
     */
    /* if JDK8 jdk //@Override// endif */
    @Deprecated
    Short getOrDefault(Object key, Short defaultValue);
    /* endif */

    /* if !(obj key obj value JDK8 jdk) */
    /**
     * Returns the value to which the specified key is mapped,
     * or {@code defaultValue} if this map contains no mapping for the key.
     *
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or
     *         {@code defaultValue} if this map contains no mapping for the key
     // if obj key //
     * @throws ClassCastException if the key is of an inappropriate type for
     *         this map (optional restriction).
     * @throws NullPointerException if the specified key is null and this map
     *         does not permit null keys (optional restriction).
     // endif //
     */
    short getOrDefault(/* raw */char key, short defaultValue);
    /* endif */


    /* if !(obj key obj value) && JDK8 jdk */
    /**
     * @deprecated Use specialization {@link #forEach(CharShortConsumer//<super>//)} instead
     */
    /* if JDK8 jdk //@Override// endif */
    @Deprecated
    void forEach(BiConsumer<? super Character, ? super Short> action);
    /* endif */

    /* if !(obj key obj value JDK8 jdk) */
    /**
     * Performs the given {@code action} on each entry in this map until all entries
     * have been processed or the action throws an {@code Exception}.
     * Exceptions thrown by the action are relayed to the caller. The entries
     * will be processed in the same order as the entry set iterator unless that
     * order is unspecified in which case implementations may use an order which
     * differs from the entry set iterator.
     *
     * @param action The action to be performed for each entry
     * @throws NullPointerException if the specified action is null
     */
    void forEach(/*f*/CharShortConsumer action);
    /* endif */


    /**
     * Checks the given {@code predicate} on each entry in this map until all entries
     * have been processed or the predicate returns {@code false} for some entry,
     * or the action throws an {@code Exception}.
     * Exceptions thrown by the action are relayed to the caller. The entries
     * will be processed in the same order as the entry set iterator unless that
     * order is unspecified in which case implementations may use an order which
     * differs from the entry set iterator.
     *
     * @return {@code true} if the predicate returned {@code true} for all entries of the map,
     *         {@code false} otherwise
     * @param predicate The predicate to be checked for each entry
     * @throws NullPointerException if the specified predicate is null
     */
    boolean forEachWhile(/*f*/CharShortPredicate predicate);

    @NotNull
    CharShortCursor/*<>*/ cursor();

    /* with key view */
    @Override
    @NotNull
    CharSet/*<>*/ keySet();
    /* endwith */

    /* with value view */
    @Override
    @NotNull
    ShortCollection/*<>*/ values();
    /* endwith */

    @Override
    @NotNull
    ObjSet<Entry<Character, Short>> entrySet();


    /* if !(obj key obj value) */
    /**
     * @deprecated Use specialization {@link #put(char, short)} instead
     */
    @Override
    @Deprecated
    Short put(Character key, Short value);

    short put(char key, short value);
    /* endif */


    /* define nullValue //
    {@code // if obj value //null// elif !(obj value) //defaultValue()// endif //}
    // enddefine */

    /* define orMappedToNull //
    // if obj value // (or is mapped to {@code null})// endif //
    // enddefine */

    /* define putNotSupported //
     * @throws UnsupportedOperationException if the {@code put} operation
     *         is not supported by this map
    // enddefine */

    /* if !(obj key obj value) && JDK8 jdk */
    /**
     * @deprecated Use specialization {@link #putIfAbsent(char, short)} instead
     */
    /* if JDK8 jdk //@Override// endif */
    @Deprecated
    Short putIfAbsent(Character key, Short value);
    /* endif */

    /* if !(obj key obj value JDK8 jdk) */
    /**
     * If the specified key is not already associated with
     * a value// orMappedToNull //,
     * associates it with the given value and returns // nullValue //,
     * else returns the current value.
     *
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with the specified key, or // nullValue //
     *         if there was no mapping for the key. (A // nullValue // return
     *         can also indicate that the map previously associated // nullValue //
     *         with the key, if the implementation supports such values.)
     * @throws IllegalArgumentException if some property of the specified key
     *         or value prevents it from being stored in this map (optional restriction)
     // putNotSupported //
     */
    short putIfAbsent(char key, short value);
    /* endif */


    /* define npeIfKeyNull //
     // if obj key //
     *         or if the specified key is null and this map does not support null keys
     // endif //
    // enddefine */

    /* define npeIfValueNull //
     // if obj key //
     *         or if the specified value is null and this map does not support null values
     // endif //
    // enddefine */

    /* define computeCCE //
     // if obj key || obj value //
     * @throws ClassCastException if the class of the
     *         // if obj key obj value //specified key or computed value
     *         // elif obj key //specified key// elif obj value //computed value// endif //
     *         prevents it from being stored in this map (optional restriction)
     // endif //
    // enddefine */

    /* define removeIfNull //
     // if obj value //
     * <p>If the function returns {@code null}, the mapping is removed.
     *
     // endif //
    // enddefine */

    /* if !(obj key obj value) && JDK8 jdk */
    /**
     * @deprecated Use specialization {@link #compute(char, CharShortToShortFunction//ef//)} instead
     */
    /* if JDK8 jdk //@Override// endif */
    @Deprecated
    Short compute(Character key,
            BiFunction<? super Character, ? super Short, ? extends Short> remappingFunction);
    /* endif */

    /* if !(obj key obj value JDK8 jdk) */
    /**
     * Attempts to compute a mapping for the specified key and its current
     * mapped value (or // nullValue // if there is no current mapping).
     *
     // if obj value //
     * <p>If the function returns {@code null}, the mapping is removed (or
     * remains absent if initially absent).
     *
     // endif //
     * <p>If the function itself throws an (unchecked) exception,
     * the exception is rethrown, and the current mapping is left unchanged.
     *
     * @param key key with which the specified value is to be associated
     * @param remappingFunction the function to compute a value
     * @return the new value associated with the specified key
     * @throws NullPointerException if the remappingFunction is null
     // npeIfKeyNull //
     // computeCCE //
     // putNotSupported //
     */
    short compute(char key, /*f*/CharShortToShortFunction remappingFunction);
    /* endif */


    /* if !(obj key obj value) && JDK8 jdk */
    /**
     * @deprecated Use specialization
     *             {@link #computeIfAbsent(char, CharToShortFunction//ef//)} instead
     */
    /* if JDK8 jdk //@Override// endif */
    @Deprecated
    Short computeIfAbsent(Character key,
            Function<? super Character, ? extends Short> mappingFunction);
    /* endif */

    /* if !(obj key obj value JDK8 jdk) */
    /**
     * If the specified key is not already associated with
     * a value// orMappedToNull //,
     * attempts to compute its value using the given mapping function
     * and enters it into this map// if obj value // unless {@code null}// endif //.
     * The most common usage is to construct a new object serving as an initial
     * mapped value or memoized result.
     *
     // if obj value //
     * <p>If the function returns {@code null} no mapping is recorded.
     *
     // endif //
     * <p>If the function itself throws an (unchecked) exception, the
     * exception is rethrown, and no mapping is recorded.
     *
     * @param key key with which the specified value is to be associated
     * @param mappingFunction the function to compute a value
     * @return the current (existing or computed) value associated with
     *         the specified key
     * @throws NullPointerException if the mappingFunction is null
     // npeIfKeyNull //
     // computeCCE //
     // putNotSupported //
     */
    short computeIfAbsent(char key, /*f*/CharToShortFunction mappingFunction);
    /* endif */


    /* if !(obj key obj value) && JDK8 jdk */
    /**
     * @deprecated Use specialization
     *             {@link #computeIfPresent(char, CharShortToShortFunction//ef//)} instead
     */
    /* if JDK8 jdk //@Override// endif */
    @Deprecated
    Short computeIfPresent(Character key,
            BiFunction<? super Character, ? super Short, ? extends Short> remappingFunction);
    /* endif */

    /* if !(obj key obj value JDK8 jdk) */
    /**
     * If the value for the specified key is present// if obj value// and non-null// endif //,
     * attempts to compute a new mapping given the key and its current mapped value.
     *
     // removeIfNull //
     * <p>If the function itself throws an (unchecked) exception,
     * the exception is rethrown, and the current mapping is left unchanged.
     *
     * @param key key with which the specified value is to be associated
     * @param remappingFunction the function to compute a value
     * @return the new value associated with the specified key,
     *         or "no entry" value
     * @throws NullPointerException if the remappingFunction is null
     // npeIfKeyNull //
     // computeCCE //
     // putNotSupported //
     */
    short computeIfPresent(char key, /*f*/CharShortToShortFunction remappingFunction);
    /* endif */


    /* if !(obj key obj value) && JDK8 jdk */
    /**
     * @deprecated Use specialization
     *             {@link #merge(char, short, ShortBinaryOperator//ef//)} instead
     */
    /* if JDK8 jdk //@Override// endif */
    @Deprecated
    Short merge(Character key, Short value,
            BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction);
    /* endif */

    /* if !(obj key obj value JDK8 jdk) */
    /**
     * If the specified key is not already associated with
     * a value// orMappedToNull //,
     * associates it with the given value. Otherwise, replaces the value with
     * the results of the given remapping function.
     *
     * This method may be of use
     * when combining multiple mapped values for a key.
     *
     // removeIfNull //
     * <p>If the remappingFunction itself throws an (unchecked) exception,
     * the exception is rethrown, and the current mapping is left unchanged.
     *
     * @param key key with which the specified value is to be associated
     * @param value the value to use if absent
     * @param remappingFunction the function to recompute a value if present
     * @return the new value associated with the specified key
     * @throws NullPointerException if the {@code remappingFunction} is null
     // npeIfKeyNull //
     // npeIfValueNull //
     // computeCCE //
     // putNotSupported //
     */
    short merge(char key, short value, /*f*/ShortBinaryOperator remappingFunction);
    /* endif */


    /* if !(obj value) */
    /**
     * {@code map.incrementValue(key, inc)} is equivalent
     * of {@code map.compute(key, (k, v) -> v + inc)}.
     * @return the new value associated with the specified key
     */
    short incrementValue(char key, short increment);

    /**
     * {@code map.incrementValue(key, increment, defaultValue)} is equivalent to
     * <pre>{@code
     * short newValue = map.getOrDefault(key, defaultValue) + increment;
     * map.put(key, newValue);
     * return newValue;
     * }</pre>
     *
     * @return the new value associated with the specified key
     */
    short incrementValue(char key, short increment, short defaultValue);
    /* endif */


    /* define objKeyOrValue //
    // if obj key obj value //key or value// elif obj value //value// elif obj key //key// endif //
    // enddefine */

    /* define objKeyOrValues //
    // if obj key obj value //keys or values// elif obj value //values// elif obj key //keys
    // endif //
    // enddefine */

    /* define replaceNpeCceIae //
     // if obj key || obj value //
     * @throws ClassCastException if the class of the specified // objKeyOrValue //
     *         prevents it from being stored in this map
     * @throws NullPointerException if the specified // objKeyOrValue // is null,
     *         and this map does not permit null // objKeyOrValues //
     // endif //
     * @throws IllegalArgumentException if some property of a specified key
     *         or value prevents it from being stored in this map
    // enddefine */

    /* if !(obj key obj value) && JDK8 jdk */
    /**
     * @deprecated Use specialization {@link #replace(char, short)} instead
     */
    /* if JDK8 jdk //@Override// endif */
    @Deprecated
    Short replace(Character key, Short value);
    /* endif */

    /* if !(obj key obj value JDK8 jdk) */
    /**
     * Replaces the entry for the specified key only if it is
     * currently mapped to some value.
     *
     * @param key key with which the specified value is associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with the specified key,
     *         or // nullValue // if there was no mapping for the key.
     *         (A // nullValue // return can also indicate that the map
     *         previously associated // nullValue // with the key,
     *         if the implementation supports such values.)
     // replaceNpeCceIae //
     // putNotSupported //
     */
    short replace(char key, short value);
    /* endif */


    /* if !(obj key obj value) && JDK8 jdk */
    /**
     * @deprecated Use specialization {@link #replace(char, short, short)} instead
     */
    /* if JDK8 jdk //@Override// endif */
    @Deprecated
    boolean replace(Character key, Short oldValue, Short newValue);
    /* endif */

    /* if !(obj key obj value JDK8 jdk) */
    /**
     * Replaces the entry for the specified key only if currently
     * mapped to the specified value.
     *
     * @param key key with which the specified value is associated
     * @param oldValue value expected to be associated with the specified key
     * @param newValue value to be associated with the specified key
     * @return {@code true} if the value was replaced
     // replaceNpeCceIae //
     // putNotSupported //
     */
    boolean replace(char key, short oldValue, short newValue);
    /* endif */


    /* if !(obj key obj value) && JDK8 jdk */
    /**
     * @deprecated Use specialization {@link #replaceAll(CharShortToShortFunction//ef//)} instead
     */
    /* if JDK8 jdk //@Override// endif */
    @Deprecated
    void replaceAll(BiFunction<? super Character, ? super Short, ? extends Short> function);
    /* endif */

    /* if !(obj key obj value JDK8 jdk) */
    /**
     * Replaces each entry's value with the result of invoking the given
     * function on that entry, in the order entries are returned by an entry
     * set iterator, until all entries have been processed or the function
     * throws an exception.
     *
     * @param function the function to apply to each entry
     * @throws UnsupportedOperationException if the {@code set} operation
     *         is not supported by this map's entry set iterator.
     * @throws NullPointerException if the specified function is null
     // if obj value //
     *         or the specified replacement value is null, and this map does not permit
     *         null values (optional restriction)
     * @throws ClassCastException if the class of a replacement value
     *         prevents it from being stored in this map
     // endif //
     * @throws IllegalArgumentException if some property of a replacement value
     *         prevents it from being stored in this map (optional restriction)
     */
    void replaceAll(/*f*/CharShortToShortFunction function);
    /* endif */


    /* define removeNotSupported //
     * @throws UnsupportedOperationException if the {@code remove} operation
     *         is not supported by this map
    // enddefine */

    /* if !(obj key obj value) */
    /**
     * @deprecated Use specialization
     *             {@link #remove// if obj key //AsShort// endif //(// raw //char)} instead
     */
    @Override
    @Deprecated
    Short remove(Object key);

    short remove/* if obj key //AsShort// endif */(/* raw */char key);
    /* endif */


    /* if !(obj key obj value) && JDK8 jdk */
    /**
     * @deprecated Use specialization {@link #remove(// raw //char, // raw //short)} instead
     */
    /* if JDK8 jdk //@Override// endif */
    @Deprecated
    boolean remove(Character key, Short value);
    /* endif */

    /* if !(obj key obj value JDK8 jdk) */
    /**
     * Removes the entry for the specified key only if it is currently
     * mapped to the specified value.
     *
     * @param key key with which the specified value is associated
     * @param value value expected to be associated with the specified key
     * @return {@code true} if the value was removed
     // if obj key || obj value //
     * @throws ClassCastException if the class of the specified // objKeyOrValue //
     *         prevents it from being stored in this map (optional restriction)
     * @throws NullPointerException if the specified // objKeyOrValue // is null,
     *         and this map does not permit null // objKeyOrValue //s (optional restriction)
     // endif //
     // removeNotSupported //
     */
    boolean remove(/* raw */char key, /* raw */short value);
    /* endif */


    boolean removeIf(/*f*/CharShortPredicate filter);
}
