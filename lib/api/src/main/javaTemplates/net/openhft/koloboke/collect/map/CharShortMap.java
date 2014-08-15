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

package net.openhft.koloboke.collect.map;

import net.openhft.koloboke.collect.Container;
import net.openhft.koloboke.collect.Equivalence;
import net.openhft.koloboke.function.BiConsumer;
import net.openhft.koloboke.function.BiFunction;
import net.openhft.koloboke.function./*f*/CharShortConsumer/**/;
import net.openhft.koloboke.function./*f*/CharShortPredicate/**/;
import net.openhft.koloboke.function./*f*/CharShortToShortFunction/**/;
import net.openhft.koloboke.function./*f*/CharToShortFunction/**/;
import net.openhft.koloboke.function.Function;
import net.openhft.koloboke.function./*f*/ShortBinaryOperator/**/;
import net.openhft.koloboke.collect.ShortCollection;
import net.openhft.koloboke.collect.set.CharSet;
import net.openhft.koloboke.collect.set.ObjSet;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.Map;


/**
 * // if !(obj key obj value) //
 * A {@link Map} specialization with {@code //raw//char} keys and {@code //raw//short} values.
 * // elif obj key obj value //
 * The library's extension of the classic {@link Map} interface.
 * // endif //
 *
 * @see CharShortMapFactory
 */
public interface CharShortMap/*<>*/ extends Map<Character, Short>, Container {

    /* if obj key */
    /**
     * Returns the equivalence strategy for keys in this map. All methods in the {@link Map}
     * interface which defined in terms of {@link Object#equals(Object)} equality of key objects
     * (almost all methods, actually), are supposed to use this equivalence instead.
     *
     * @return the equivalence strategy for keys in this map
     * @see net.openhft.koloboke.collect.map.hash.HashCharShortMapFactory#withKeyEquivalence
     */
    @Nonnull
    Equivalence<Character> keyEquivalence();
    /* endif */

    /* if obj value */
    /**
     * Returns the equivalence strategy for values in this map. All methods in the {@link Map}
     * interface which defined in terms of {@link Object#equals(Object)} equality of value objects,
     * for example, {@link #containsValue(Object)} and {@link #remove(//raw//char, //raw//short)},
     * are supposed to use this equivalence instead.
     *
     * @return the equivalence strategy for values in this map
     * @see CharShortMapFactory#withValueEquivalence
     */
    @Nonnull
    Equivalence<Short> valueEquivalence();
    /* endif */
    /* if !(obj value) */

    /**
     * Returns the default value of this map, which is used instead of {@code null}
     * in primitive specialization methods, when the key is absent in the map.
     *
     * @return the default value of this map
     * @see CharShortMapFactory#withDefaultValue(short)
     */
    short defaultValue();
    /* endif */


    /* if !(obj key) */
    /**
     * {@inheritDoc}
     * @deprecated Use specialization {@link #containsKey(char)} instead
     */
    @Override
    @Deprecated
    boolean containsKey(Object key);

    /**
     * Returns {@code true} if this map contains a mapping for the specified key.
     *
     * @param key the {@code char} key whose presence in this map is to be tested
     * @return {@code true} if this map contains a mapping for the specified key
     */
    boolean containsKey(char key);
    /* endif */

    /* if !(obj value) */
    /**
     * {@inheritDoc}
     * @deprecated Use specialization {@link #containsValue(short)} instead
     */
    @Override
    @Deprecated
    boolean containsValue(Object value);

    /**
     * Returns {@code true} if this map maps one or more keys to the specified value. This operation
     * will probably require time linear in the map size for most implementations
     * of the {@code CharShortMap} interface.
     *
     * @param value the {@code short} value whose presence in this map is to be tested
     * @return {@code true} if this map maps one or more keys to the specified value
     */
    boolean containsValue(short value);
    /* endif */

    /* define valueSuffix */
    /* if obj key short|byte|char|int|long|float|double value //$Short// endif */
    /* enddefine */

    /* if !(obj key obj value) */
    /**
     * {@inheritDoc}
     * @deprecated Use specialization {@link #get// valueSuffix //(//raw//char)} instead
     */
    @Nullable
    @Override
    @Deprecated
    Short get(Object key);

    /* define nullValue //
    // if obj value //{@code null}// elif !(obj value) //{@linkplain #defaultValue() default
    value}// endif //
    // enddefine */

    /**
     * Returns the value to which the specified key is mapped, or // nullValue // if this map
     * contains no mapping for the key.
     *
     * // if obj key //
     * <p>More formally, if this map contains a mapping from a key {@code k} to a value {@code v}
     * such that {@code keyEquivalence() == null ? (key==null ? k==null : key.equals(k)) :
     * keyEquivalence().nullableEquivalent(k, key)}, then this method returns {@code v}; otherwise
     * it returns // nullValue //. (There can be at most one such mapping.)// endif //
     *
     * // if obj value //
     * <p>If this map permits null values, then a return value of {@code null} does not
     * <i>necessarily</i> indicate that the map contains no mapping for the key; it's also possible
     * that the map explicitly maps the key to {@code null}. The {@link #containsKey}
     * operation may be used to distinguish these two cases.// endif //
     *
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or // nullValue // if this map
     *         contains no mapping for the key
     // if obj key //
     * @throws ClassCastException if the key is of an inappropriate type for
     *         this map (optional restriction)
     * @throws NullPointerException if the specified key is null and this map does not permit
     *         null keys (optional restriction)
     // endif //
     */
    /* if obj value */@Nullable/* endif */
    short get/* valueSuffix */(/* raw */char key);
    /* endif */


    /* if !(obj key obj value) */
    /**
     * // if !(JDK8 jdk) //Returns the value to which the specified key is mapped, or
     * {@code defaultValue} if this map contains no mapping for the key.
     *
     * @param key the key whose associated value is to be returned
     * @param defaultValue the default mapping of the key
     * @return the value to which the specified key is mapped, or
     *         {@code defaultValue} if this map contains no mapping for the key
     // if obj key //
     * @throws ClassCastException if the key is of an inappropriate type for
     *         this map (optional restriction)
     * @throws NullPointerException if the specified key is null and this map
     *         does not permit null keys (optional restriction)
     // endif //// elif JDK8 jdk //{@inheritDoc}// endif //
     * @deprecated Use specialization {@link #getOrDefault(//raw//char, //raw//short)} instead
     */
    /* if JDK8 jdk */@Override/* endif */
    @Deprecated
    Short getOrDefault(Object key, Short defaultValue);
    /* endif */

    /* if !(obj key obj value JDK8 jdk) */
    /**
     * Returns the value to which the specified key is mapped, or {@code defaultValue} if this map
     * contains no mapping for the key.
     *
     * @param key the key whose associated value is to be returned
     * @param defaultValue the value to return if the specified {@code key} is absent in the map
     * @return the value to which the specified key is mapped, or
     *         {@code defaultValue} if this map contains no mapping for the key
     // if obj key //
     * @throws ClassCastException if the key is of an inappropriate type for
     *         this map (optional restriction)
     * @throws NullPointerException if the specified key is null and this map
     *         does not permit null keys (optional restriction)
     // endif //
     */
    short getOrDefault(/* raw */char key, short defaultValue);
    /* endif */


    /* if !(obj key obj value) && JDK8 jdk */
    /**
     * {@inheritDoc}
     * @deprecated Use specialization {@link #forEach(CharShortConsumer)} instead
     */
    /* if JDK8 jdk //@Override// endif */
    @Deprecated
    void forEach(@Nonnull BiConsumer<? super Character, ? super Short> action);
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
     * @see <a href="{@docRoot}/overview-summary.html#iteration">
     *     Comparison of iteration options in the library</a>
     */
    void forEach(@Nonnull /*f*/CharShortConsumer action);
    /* endif */


    /**
     * Checks the given {@code predicate} on each entry in this map until all entries
     * have been processed or the predicate returns {@code false} for some entry,
     * or throws an {@code Exception}. Exceptions thrown by the predicate are relayed to the caller.
     *
     * <p>The entries will be processed in the same order as the entry set iterator unless that
     * order is unspecified in which case implementations may use an order which differs from
     * the entry set iterator.
     *
     * <p>If the map is empty, this method returns {@code true} immediately.
     *
     * @return {@code true} if the predicate returned {@code true} for all entries of the map,
     *         {@code false} if it returned {@code false} for the entry
     * @param predicate the predicate to be checked for each entry
     * @see <a href="{@docRoot}/overview-summary.html#iteration">
     *     Comparison of iteration options in the library</a>
     */
    boolean forEachWhile(@Nonnull /*f*/CharShortPredicate predicate);

    /**
     * Returns a new cursor over the entries of this map. It's order is always correspond to the
     * entry set iterator order.
     *
     * @return a new cursor over the entries of this map
     * @see <a href="{@docRoot}/overview-summary.html#iteration">
     *     Comparison of iteration options in the library</a>
     */
    @Nonnull
    CharShortCursor/*<>*/ cursor();

    /* with key view */

    @Override
    @Nonnull
    CharSet/*<>*/ keySet();
    /* endwith */

    /* with value view */
    @Override
    @Nonnull
    ShortCollection/*<>*/ values();
    /* endwith */

    @Override
    @Nonnull
    ObjSet<Entry<Character, Short>> entrySet();

    /* define objKeyOrValue //
    // if obj key obj value //key or value// elif obj value //value// elif obj key //key// endif //
    // enddefine */

    /* define nonObjKeyOrValue //
// if !(obj key) && !(obj value) //key or value// elif !(obj value) //value// elif !(obj key) //key// endif //
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

    /* define putExceptions //
    * @throws UnsupportedOperationException if the {@code put} operation
    *         is not supported by this map
    // replaceNpeCceIae //
    // enddefine */

    /* if !(obj key obj value) */
    /**
     * {@inheritDoc}
     * @deprecated Use specialization {@link #put(//raw//char, //raw//short)} instead
     */
    @Override
    @Deprecated
    Short put(Character key, Short value);

    /**
     * Associates the specified value with the specified key in this map (optional operation).
     * If the map previously contained a mapping for the key, the old value is replaced
     * by the specified value. (A map {@code m} is said to contain a mapping for a key {@code k}
     * if and only if {@link #containsKey(//raw//char) m.containsKey(k)} would return {@code true}.)
     *
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with {@code key}, or // nullValue // if there was
     *         no mapping for {@code key}. (A // nullValue // return can also indicate that the map
     *         previously associated // nullValue // with {@code key}.)
     // putExceptions //
     */
    short put(char key, short value);
    /* endif */

    /* define orMappedToNull //
    // if obj value // (or is mapped to {@code null})// endif //
    // enddefine */

    /* define putNotSupported //
     * @throws UnsupportedOperationException if the {@code put} operation
     *         is not supported by this map
    // enddefine */

    /* if !(obj key obj value) */
    /**
     * If the specified key is not already associated with a value// orMappedToNull //, associates
     * it with the given value and returns {@code null}, else returns the current value.
     *
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with the specified key, or
     *         {@code null} if there was no mapping for the key.
     *         (A {@code null} return can also indicate that the map
     *         previously associated {@code null} with the key,
     *         if the implementation supports null values.)
     // putNotSupported //
     * @throws ClassCastException if the key or value is of an inappropriate type for this map
     * @throws NullPointerException if the specified // nonObjKeyOrValue // is null
     * @throws IllegalArgumentException if some property of the specified key
     *         or value prevents it from being stored in this map
     * @deprecated Use specialization {@link #putIfAbsent(//raw//char, //raw//short)} instead
     */
    /* if JDK8 jdk */@Override/* endif */
    @Nullable
    @Deprecated
    Short putIfAbsent(Character key, Short value);
    /* endif */

    /* if !(obj key obj value JDK8 jdk) */
    /**
     * If the specified key is not already associated with a value// orMappedToNull //, associates
     * it with the given value and returns // nullValue //, else returns the current value.
     *
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with the specified key, or // nullValue //
     *         if there was no mapping for the key. (A // nullValue // return
     *         can also indicate that the map previously associated // nullValue //
     *         with the key, if the implementation supports such values.)
     // putExceptions //
     */
    /* if obj value */@Nullable/* endif */
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
     * {@inheritDoc}
     * @deprecated Use specialization
     *             {@link #compute(//raw//char, //f//CharShortToShortFunction////)} instead
     */
    /* if JDK8 jdk //@Override// endif */
    @Deprecated
    Short compute(Character key,
            @Nonnull BiFunction<? super Character, ? super Short, ? extends Short> remappingFunction
    );
    /* endif */

    /* if !(obj key obj value JDK8 jdk) */
    /**
     * Attempts to compute a mapping for the specified key and its current mapped value
     * (or // nullValue // if there is no current mapping).
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
     // npeIfKeyNull //
     // computeCCE //
     // putNotSupported //
     */
    short compute(char key, @Nonnull /*f*/CharShortToShortFunction remappingFunction);
    /* endif */


    /* if !(obj key obj value) && JDK8 jdk */
    /**
     * {@inheritDoc}
     * @deprecated Use specialization
     *             {@link #computeIfAbsent(//raw//char, //f//CharToShortFunction////)} instead
     */
    /* if JDK8 jdk //@Override// endif */
    @Deprecated
    Short computeIfAbsent(Character key,
            @Nonnull Function<? super Character, ? extends Short> mappingFunction);
    /* endif */

    /* if !(obj key obj value JDK8 jdk) */
    /**
     * If the specified key is not already associated with a value// orMappedToNull //, attempts
     * to compute its value using the given mapping function and enters it into this map
     * // if obj value // unless {@code null}// endif //. The most common usage is to construct
     * a new object serving as an initial mapped value or memoized result.
     *
     // if obj value //
     * <p>If the function returns {@code null} no mapping is recorded.
     *
     // endif //
     * <p>If the function itself throws an (unchecked) exception, the exception is rethrown,
     * and no mapping is recorded.
     *
     * @param key key with which the specified value is to be associated
     * @param mappingFunction the function to compute a value
     * @return the current (existing or computed) value associated with
     *         the specified key
     // npeIfKeyNull //
     // computeCCE //
     // putNotSupported //
     */
    short computeIfAbsent(char key, @Nonnull /*f*/CharToShortFunction mappingFunction);
    /* endif */


    /* if !(obj key obj value) && JDK8 jdk */
    /**
     * {@inheritDoc}
     * @deprecated Use specialization
     *             {@link #computeIfPresent(//raw//char, //f//CharShortToShortFunction////)} instead
     */
    /* if JDK8 jdk //@Override// endif */
    @Deprecated
    Short computeIfPresent(Character key,
            @Nonnull BiFunction<? super Character, ? super Short, ? extends Short> remappingFunction
    );
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
     // npeIfKeyNull //
     // computeCCE //
     // putNotSupported //
     */
    short computeIfPresent(char key, @Nonnull /*f*/CharShortToShortFunction remappingFunction);
    /* endif */


    /* if !(obj key obj value) && JDK8 jdk */
    /**
     * {@inheritDoc}
     * @deprecated Use specialization
     *             {@link #merge(//raw//char, //raw//short, //f//ShortBinaryOperator////)} instead
     */
    /* if JDK8 jdk //@Override// endif */
    @Deprecated
    Short merge(Character key, Short value,
            @Nonnull BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction);
    /* endif */

    /* if !(obj key obj value JDK8 jdk) */
    /**
     * If the specified key is not already associated with a value// orMappedToNull //, associates
     * it with the given value, otherwise, replaces the value with the results of the given
     * remapping function.
     *
     * This method may be of use when combining multiple mapped values for a key.
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
    short merge(char key, short value, @Nonnull /*f*/ShortBinaryOperator remappingFunction);
    /* endif */


    /* if !(obj value) */
    /**
     * Adds the given {@code addition} value to the value associated with the specified key,
     * or // nullValue // if this map contains no mapping for the key, and associates the resulting
     * value with the key.
     *
     * @param key the key to which value add the given value
     * @param addition the value to add
     * @return the new value associated with the specified key
     // npeIfKeyNull //
     // computeCCE //
     // putNotSupported //
     */
    short addValue(char key, short addition);

    /**
     * Adds the given {@code addition} value to the value associated with the specified key,
     * or {@code defaultValue} if this map contains no mapping for the key, and associates
     * the resulting value with the key.
     *
     * <p>This version of {@link #addValue(//raw//char, short)} is useful if you want to count
     * values from the different initial value, than the {@linkplain #defaultValue() default value}
     * of this map.
     *
     * @param key the key to which value add the given value
     * @param addition the value to add
     * @param defaultValue the value to be used if the map contains no mapping for the given key
     * @return the new value associated with the specified key
     // npeIfKeyNull //
     // computeCCE //
     // putNotSupported //
     */
    short addValue(char key, short addition, short defaultValue);
    /* endif */

    /* if !(obj key obj value) */
    /**
     * Replaces the entry for the specified key only if it is currently mapped to some value.
     *
     * @param key key with which the specified value is associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with the specified key, or {@code null} if there was
     *         no mapping for the key. // if obj value //(A {@code null} return can also indicate
     *         that the map previously associated {@code null} with the key,
     *         if the implementation supports null values.)// endif //
     // putNotSupported //
     * @throws NullPointerException if the specified // nonObjKeyOrValue // is null
     * @throws IllegalArgumentException if some property of the specified value
     *         prevents it from being stored in this map
     * @deprecated Use specialization {@link #replace(//raw//char, //raw//short)} instead
     */
    /* if JDK8 jdk */@Override/* endif */
    @Nullable
    @Deprecated
    Short replace(Character key, Short value);
    /* endif */

    /* if !(obj key obj value JDK8 jdk) */
    /**
     * Replaces the entry for the specified key only if it is currently mapped to some value.
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
    /* if obj value */@Nullable/* endif */
    short replace(char key, short value);
    /* endif */


    /* if !(obj key obj value) */
    /**
     * Replaces the entry for the specified key only if currently mapped to the specified value.
     *
     * @param key key with which the specified value is associated
     * @param oldValue value expected to be associated with the specified key
     * @param newValue value to be associated with the specified key
     * @return {@code true} if the value was replaced
     // putNotSupported //
     * @throws NullPointerException if the specified // nonObjKeyOrValue // is null
     * @throws IllegalArgumentException if some property of the specified value
     *         prevents it from being stored in this map
     * @deprecated Use specialization
     *             {@link #replace(//raw//char, //raw//short, //raw//short)} instead
     */
    /* if JDK8 jdk */@Override/* endif */
    @Deprecated
    boolean replace(Character key, Short oldValue, Short newValue);
    /* endif */

    /* if !(obj key obj value JDK8 jdk) */
    /**
     * Replaces the entry for the specified key only if currently mapped to the specified value.
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
     * {@inheritDoc}
     * @deprecated Use specialization {@link #replaceAll(//f//CharShortToShortFunction////)} instead
     */
    /* if JDK8 jdk //@Override// endif */
    @Deprecated
    void replaceAll(
            @Nonnull BiFunction<? super Character, ? super Short, ? extends Short> function);
    /* endif */

    /* if !(obj key obj value JDK8 jdk) */
    /**
     * Replaces each entry's value with the result of invoking the given function on that entry,
     * in the order entries are returned by an entry set iterator, until all entries have been
     * processed or the function throws an exception.
     *
     * @param function the function to apply to each entry
     * @throws UnsupportedOperationException if the {@code set} operation
     *         is not supported by this map's entry set iterator
     // if obj value //
     *         or the specified replacement value is null, and this map does not permit
     *         null values (optional restriction)
     * @throws ClassCastException if the class of a replacement value
     *         prevents it from being stored in this map
     // endif //
     * @throws IllegalArgumentException if some property of a replacement value
     *         prevents it from being stored in this map (optional restriction)
     */
    void replaceAll(@Nonnull /*f*/CharShortToShortFunction function);
    /* endif */


    /* define removeNotSupported //
     * @throws UnsupportedOperationException if the {@code remove} operation
     *         is not supported by this map
    // enddefine */

    /* if !(obj key obj value) */
    /**
     * {@inheritDoc}
     * @deprecated Use specialization
     *             {@link #remove// if obj key //AsShort// endif //(//raw//char)} instead
     */
    @Override
    @Nullable
    @Deprecated
    Short remove(Object key);

    /**
     * Removes the mapping for a key from this map if it is present (optional operation).
     *
     // if obj key //
     * <p>More formally, if this map contains a mapping from a key {@code k} to a value {@code v}
     * such that {@code keyEquivalence() == null ? (key==null ? k==null : key.equals(k)) :
     * keyEquivalence().nullableEquivalent(k, key)}, that mapping is removed.
     * (The map can contain at most one such mapping.)
     // endif //
     *
     * <p>Returns the value to which this map previously associated the key, or // nullValue //
     * if the map contained no mapping for the key.
     *
     * <p>A return value of // nullValue // does not <i>necessarily</i> indicate that the map
     * contained no mapping for the key; it's also possible that the map
     * explicitly mapped the key to // nullValue //.
     *
     * <p>The map will not contain a mapping for the specified key once the
     * call returns.
     *
     * @param key key whose mapping is to be removed from the map
     * @return the previous value associated with {@code key}, or // nullValue // if there was
     *         no mapping for {@code key}
     // if obj key || obj value //
     * @throws ClassCastException if the class of the specified // objKeyOrValue //
     *         prevents it from being stored in this map (optional restriction)
     * @throws NullPointerException if the specified // objKeyOrValue // is null,
     *         and this map does not permit null // objKeyOrValue //s (optional restriction)
     // endif //
     // removeNotSupported //
     */
    /* if obj value */@Nullable/* endif */
    short remove/* if obj key //AsShort// endif */(/* raw */char key);
    /* endif */


    /* if !(obj key obj value) */
    /**
     * Removes the entry for the specified key only if it is currently mapped to the specified
     * value.
     *
     * @param key key with which the specified value is associated
     * @param value value expected to be associated with the specified key
     * @return {@code true} if the value was removed
     * @throws NullPointerException if the specified // nonObjKeyOrValue // is null
     // if obj key || obj value //
               , or if the specified // objKeyOrValue // is null,
     *         and this map does not permit null // objKeyOrValue //s (optional restriction)
     * @throws ClassCastException if the class of the specified // objKeyOrValue //
     *         prevents it from being stored in this map (optional restriction)
     // endif //
     // removeNotSupported //
     * @deprecated Use specialization {@link #remove(//raw//char, //raw//short)} instead
     */
    /* if JDK8 jdk */@Override/* endif */
    @Deprecated
    boolean remove(Object key, Object value);
    /* endif */

    /* if !(obj key obj value JDK8 jdk) */
    /**
     * Removes the entry for the specified key only if it is currently mapped to the specified
     * value.
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

    /**
     * Removes all of the entries of this collection that satisfy the given predicate.
     * Errors or runtime exceptions thrown during iteration or by the predicate are relayed
     * to the caller.
     *
     * @param filter a predicate which returns {@code true} for elements to be removed
     * @return {@code true} if any elements were removed
     * @throws NullPointerException if the specified filter is null
     * @throws UnsupportedOperationException if elements cannot be removed from this collection.
     *         Implementations may throw this exception if a matching element cannot be removed
     *         or if, in general, removal is not supported.
     * @see <a href="{@docRoot}/overview-summary.html#iteration">
     *     Comparison of iteration options in the library</a>
     */
    boolean removeIf(@Nonnull /*f*/CharShortPredicate filter);
}
