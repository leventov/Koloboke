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

/**
 * The root package of the collection library.
 *
 * <h2><a name="jdk-equivalents"></a>Table of equivalents of JDK collection patterns</h2>
 *
 * <table BORDER CELLPADDING=3 CELLSPACING=1 summary="JDK equivalents">
 *     <tr>
 *         <th>JDK</th>
 *         <th>The closest equivalent from this library</th>
 *         <th>The recommended equivalent from this library</th>
 *     </tr>
 *     <tr valign=top>
 *         <td><pre>{@code new HashMap<String, String>();}</pre></td>
 *         <td><pre>{@code HashObjObjMaps.getDefaultFactory()
 *         .withNullKeyAllowed(true)
 *         .<String, String>newMutableMap();}</pre>
 *         </td>
 *         <td><pre>{@code HashObjObjMaps.<String, String>newUpdatableMap();}</pre></td>
 *     </tr>
 *     <tr valign=top>
 *         <td><pre>{@code // unclear how "capacity" (100)
 * // is translated to size. 50? 75? 100?
 * new HashMap<Integer, String>(100);}</pre></td>
 *
 *         <td><pre>{@code HashIntObjMaps.<String>newMutableMap(
 *         (int) (100 * HashConfig.getDefault().getTargetLoad()));}</pre></td>
 *         <td><pre><code>// 50 is expected _size_
 * HashIntObjMaps.&lt;String&gt;newUpdatableMap(50);</code></pre></td>
 *     </tr>
 *     <tr valign=top>
 *         <td><pre>{@code new IdentityHashMap<Object, Double>(map);}</pre></td>
 *         <td><pre>{@code HashObjDoubleMaps.getDefaultFactory()
 *         // these loads used in IdentityHashMap internally
 *         .withHashConfig(HashConfig.fromLoads(1./3., 2./3., 2./3.))
 *         .withNullKeyAllowed(true)
 *         .withKeyEquivalence(Equivalence.identity())
 *         .newMutableMap(map);}</pre></td>
 *         <td><pre>{@code HashObjDoubleMaps.getDefaultFactory()
 *         .withKeyEquivalence(Equivalence.identity())
 *         .newImmutableMap(map);}</pre></td>
 *     </tr>
 *     <tr valign=top>
 *         <td><pre><code> Collections.unmodifiableSet(new HashSet&lt;&gt;() {{
 *     add("Summer");
 *     add("Autumn");
 *     add("Winter");
 *     add("Spring");
 * }});</code></pre></td>
 *         <td colspan=2>
 * <pre>{@code HashObjSets.newImmutableSetOf("Summer", "Autumn", "Winter", "Spring")}</pre></td>
 *     </tr>
 * </table>
 *
 * <h2><a name="mutability">Mutability profiles</a></h2>
 * Container factories allow to construct containers with several distinct degrees of mutability.
 *
 * <h3>Immutable</h3>
 *
 * <p>Any operations that change the conceptual container state, e. g. insertions and removals,
 * as well as that could touch internal representation,
 * e. g. {@link net.openhft.collect.Container#shrink()}, are disallowed. Other ones are allowed.
 *
 * <h3>Updatable</h3>
 *
 * <p>Everything is allowed, except removals of <em>individual</em> elements (entries),
 * typically these operations' names contain word "remove" or "retain". Emphasis on "individual"
 * means that {@link net.openhft.collect.Container#clear()} is allowed.
 *
 * <p>Think about updatable containers as "non-decreasing", which could be "reset"
 * from time to time by {@code clear()}.
 *
 * <p>In real practice individual removals are rarely needed, so most of the time you should use
 * updatable containers rather than fully mutable ones. On the other hand, prohibit of removals
 * permits faster implementation of {@link net.openhft.collect.hash.HashContainer hash containers}
 * and iterators over many data structures.
 *
 * <h3>Mutable</h3>
 *
 * <p>All operations are allowed.
 *
 * <h3><a name="collection-mutability"></a>{@link java.util.Collection} mutability matrix</h3>
 *
 * <table BORDER CELLPADDING=3 CELLSPACING=1>
 *   <caption>This matrix shows which methods of the {@link java.util.Collection Collection}
 *   interface are supported by collections with different mutability profiles.</caption>
 *   <tr><td>Method \ Mutability</td><td>Mutable</td><td>Updatable</td><td>Immutable</td></tr>
 *   <tr><td>{@link java.util.Collection#contains(Object) contains(Object)}</td>
 *          <td>✓</td><td>✓</td><td>✓</td></tr>
 *   <tr><td>{@link java.util.Collection#containsAll(java.util.Collection) containsAll(Collection)}
 *     </td><td>✓</td><td>✓</td><td>✓</td></tr>
 *   <tr><td>{@link java.util.Collection#iterator() iterator()}</td>
 *          <td>✓</td><td>✓</td><td>✓</td></tr>
 *   <tr><td>{@link java.util.Collection#toArray() toArray()}</td>
 *          <td>✓</td><td>✓</td><td>✓</td></tr>
 *   <tr><td>{@link java.util.Collection#toArray(Object[]) toArray(Object[])}</td>
 *          <td>✓</td><td>✓</td><td>✓</td></tr>
 *   <tr><td>{@link java.util.Collection#add(Object) add(Object)}</td>
 *          <td>✓</td><td>✓</td><td>-</td></tr>
 *   <tr><td>{@link java.util.Collection#addAll(java.util.Collection) addAll(Collection)}</td>
 *          <td>✓</td><td>✓</td><td>-</td></tr>
 *   <tr><td>{@link java.util.Collection#remove(Object) remove(Object)}</td>
 *          <td>✓</td><td>-</td><td>-</td></tr>
 *   <tr><td>{@link java.util.Collection#removeAll(java.util.Collection) removeAll(Collection)}</td>
 *          <td>✓</td><td>-</td><td>-</td></tr>
 *   <tr><td>{@link java.util.Collection#retainAll(java.util.Collection) retainAll(Collection)}</td>
 *          <td>✓</td><td>-</td><td>-</td></tr>
 *   // if JDK8 jdk //
 *   <tr><td>{@link java.util.Collection#removeIf(java.util.function.Predicate) removeIf(Predicate)}
 *     </td><td>✓</td><td>-</td><td>-</td></tr>// endif //
 * </table>
 *
 * <h3><a name="map-mutability"></a>{@link java.util.Map} mutability matrix</h3>
 *
 * <table BORDER CELLPADDING=3 CELLSPACING=1>
 *   <caption>This matrix shows which methods of the {@link java.util.Map Map}
 *   interface are supported by maps with different mutability profiles.</caption>
 *   <tr><td>Method \ Mutability</td><td>Mutable</td><td>Updatable</td><td>Immutable</td></tr>
 *   <tr><td>{@link java.util.Map#containsKey(Object) containsKey(Object)}</td>
 *          <td>✓</td><td>✓</td><td>✓</td></tr>
 *   <tr><td>{@link java.util.Map#containsValue(Object) containsValue(Object)}</td>
 *          <td>✓</td><td>✓</td><td>✓</td></tr>
 *   <tr><td>{@link java.util.Map#get(Object) get(Object)}</td>
 *          <td>✓</td><td>✓</td><td>✓</td></tr>
 *   // if JDK8 jdk //
 *   <tr><td>{@link java.util.Map#getOrDefault(Object, Object) getOrDefault(Object, Object)}</td>
 *          <td>✓</td><td>✓</td><td>✓</td></tr>// endif //
 *   <tr><td>{@link java.util.Map#keySet() keySet()}</td>
 *          <td>✓</td><td>✓</td><td>✓</td></tr>
 *   <tr><td>{@link java.util.Map#entrySet() entrySet()}</td>
 *          <td>✓</td><td>✓</td><td>✓</td></tr>
 *   <tr><td>{@link java.util.Map#values() values()}</td>
 *          <td>✓</td><td>✓</td><td>✓</td></tr>
 *   // if JDK8 jdk //
 *   <tr><td>{@link java.util.Map#forEach(java.util.function.BiConsumer) forEach(BiConsumer)}</td>
 *          <td>✓</td><td>✓</td><td>✓</td></tr>// endif //
 *   <tr><td>{@link java.util.Map#put(Object, Object) put(Object, Object)}</td>
 *          <td>✓</td><td>✓</td><td>-</td></tr>
 *   // if JDK8 jdk //
 *   <tr><td>{@link java.util.Map#putIfAbsent(Object, Object) putIfAbsent(Object, Object)}</td>
 *          <td>✓</td><td>✓</td><td>-</td></tr>
 *   <tr><td>{@link java.util.Map#computeIfAbsent(Object, java.util.function.Function)
 *                  computeIfAbsent(Object, Function)}</td>
 *          <td>✓</td><td>✓</td><td>-</td></tr>
 *   <tr><td>{@link java.util.Map#replace(Object, Object) replace(Object, Object)}</td>
 *          <td>✓</td><td>✓</td><td>-</td></tr>
 *   <tr><td>{@link java.util.Map#replace(Object, Object, Object) replace(Object, Object, Object)}
 *   </td>  <td>✓</td><td>✓</td><td>-</td></tr>// endif //
 *   <tr><td>{@link java.util.Map#putAll(java.util.Map) putAll(Map)}</td>
 *          <td>✓</td><td>✓</td><td>-</td></tr>
 *   // if JDK8 jdk //
 *   <tr><td>{@link java.util.Map#replaceAll(java.util.function.BiFunction) replaceAll(BiFunction)}
 *   </td>  <td>✓</td><td>✓</td><td>-</td></tr>
 *   <tr><td>{@link java.util.Map#compute(Object, java.util.function.BiFunction)
 *                  compute(Object, BiFunction)}</td>
 *          <td>✓</td><td>✓, except removing on returning {@code null}</td><td>-</td></tr>
 *   <tr><td>{@link java.util.Map#computeIfPresent(Object, java.util.function.BiFunction)
 *                  computeIfPresent(Object, BiFunction)}</td>
 *          <td>✓</td><td>✓, except removing on returning {@code null}</td><td>-</td></tr>
 *   <tr><td>{@link java.util.Map#merge(Object, Object, java.util.function.BiFunction)
 *                  merge(Object, Object, BiFunction)}</td>
 *          <td>✓</td><td>✓, except removing on returning {@code null}</td><td>-</td></tr>
 *   // endif //
 *   <tr><td>{@link java.util.Map#remove(Object) remove(Object)}</td>
 *          <td>✓</td><td>-</td><td>-</td></tr>
 *   // if JDK8 jdk //
 *   <tr><td>{@link java.util.Map#remove(Object, Object) remove(Object, Object)}</td>
 *          <td>✓</td><td>-</td><td>-</td></tr>// endif //
 * </table>
 *
 * <p>See other matrices for information if the concrete method is supported by the given
 * mutability profile: <a href="Container.html#mutability">{@code Container}</a>.
 *
 * <h2><a name="iteration"></a>Comparison of iteration ways</h2>
 * In addition to the standard way// if JDK8 jdk //s// endif // &mdash;
 * {@link java.util.Iterator iterators}// if JDK8 jdk // and {@code forEach()}-like methods which
 * accept closures// endif //, the library supplies {@link net.openhft.collect.Cursor cursors}
 * for every container// if !(JDK8 jdk) // and {@code forEach()}-like methods which accept closures// endif //.
 *
 * <table BORDER CELLPADDING=3 CELLSPACING=1>
 *   <caption>Overview comparison of the ways to iterate over containers within the library
 *   </caption>
 *   <tr>
 *     <td></td>
 *     <td>{@link java.util.Iterator}</td>
 *     <td>{@link net.openhft.collect.Cursor}</td>
 *     <td>{@code forEach()}</td>
 *     <td>{@code forEachWhile()}</td>
 *     <td>{@code removeIf()}</td>
 *   </tr>
 *   <tr>
 *     <td>Available for {@link java.util.Collection} sub-interfaces in the library</td>
 *     <td colspan=5 align=center>Yes</td>
 *   </tr>
 *   <tr>
 *     <td>Available for {@link java.util.Map} sub-interfaces in the library</td>
 *     <td colspan=5 align=center>Yes</td>
 *   </tr>
 *   <tr>
 *     <td>Coding convenience</td>
 *     <td>High, if elements aren't removed and <em>generic</em> version
 *         of {@link java.util.Iterator#next()} method is used, Java "for-each" syntax
 *         is applicable. Medium otherwise.</td>
 *     <td>Medium</td>
 *     <td colspan=3>
 *         // if JDK8 jdk //High, lambda syntax// elif !(JDK8 jdk) //
 *         Low, nasty anonymous classes declarations// endif //</td>
 *   </tr>
 *   <tr>
 *     <td>Supports early break from the iteration</td>
 *     <td>Yes, by simple break from the loop</td>
 *     <td>Yes, by simple break from the loop</td>
 *     <td>No</td>
 *     <td>Yes, by returning {@code false}</td>
 *     <td>No</td>
 *   </tr>
 *   <tr>
 *     <td>Supports remove of iterated elements (entries)</td>
 *     <td>Yes, by {@link java.util.Iterator#remove()}</td>
 *     <td>Yes, by {@link net.openhft.collect.Cursor#remove()}</td>
 *     <td>No</td>
 *     <td>No</td>
 *     <td>Yes, by returning {@code true}</td>
 *   </tr>
 *   <tr>
 *     <td>Performance, iteration over {@link java.util.Map}</td>
 *     <td>Medium, {@link java.util.Map.Entry} object are allocated</td>
 *     <td colspan=4 align=center>Very high</td>
 *   </tr>
 *   <tr>
 *     <td>Performance, iteration over {@link java.util.Collection}</td>
 *     <td>High, if <em>specialized</em> version of {@link java.util.Iterator#next()} method
 *         is used. Medium otherwise, because every element is boxed.</td>
 *     <td colspan=4 align=center>Very high</td>
 *   </tr>
 * </table>
 */
package net.openhft.collect;