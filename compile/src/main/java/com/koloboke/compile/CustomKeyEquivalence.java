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

package com.koloboke.compile;

import com.koloboke.collect.Equivalence;
import com.koloboke.collect.map.ObjObjMap;
import com.koloboke.collect.map.hash.HashObjObjMapFactory;
import com.koloboke.collect.set.ObjSet;
import com.koloboke.collect.set.hash.HashObjSetFactory;

import java.lang.annotation.*;
import java.util.*;


/**
 * Indicates that the annotated {@link Map}- or {@link Set}-like class or interface with a reference
 * <a href="{@docRoot}/com/koloboke/compile/KolobokeMap.html#model">key type</a> uses a custom
 * equivalence strategy when comparing keys. If this annotation is applied to a {@link
 * KolobokeMap @KolobokeMap}- or {@link KolobokeSet @KolobokeSet}-annotated class or interface,
 * Koloboke Compile checks that
 * <ol>
 *     <li><a href="{@docRoot}/com/koloboke/compile/KolobokeMap.html#model">the key type</a> of
 *     the annotated class or interface is a reference type (a declared type, or an array type, or
 *     a type variable);</li>
 *     <li>there are two non-abstract, non-private methods are declared or inherited in the
 *     annotated type,
 *     <a href="{@docRoot}/com/koloboke/compile/KolobokeMap.html#method-form-matching">matching the
 *     following forms</a>:
 *     <table BORDER CELLPADDING=3 CELLSPACING=1 summary="Custom key equivalence forms">
 *         <tr><td ALIGN=RIGHT>{@code boolean}</td>
 *             <td>{@code keyEquals(KeyType queriedKey, KeyType keyInContainer)}</td></tr>
 *         <tr><td ALIGN=RIGHT>{@code int}</td>
 *             <td>{@code keyHashCode(KeyType key)}</td></tr>
 *     </table></li>
 * </ol>
 *
 * If either of the conditions is not true, Koloboke Compile emits a compilation error and doesn't
 * generate an implementation for the annotated type.
 *
 * <p>{@code keyEquals()} and {@code keyHashCode()} must obey general equivalence relation rules
 * (see {@link Object#equals(Object)} and {@link Object#hashCode()} specifications for details).
 *
 * <p>Koloboke Compile provides the following guarantees on how it calls {@code keyEquals()} and
 * {@code keyHashCode()} inside generated implementations:
 * <ul>
 *     <li>Both arguments of {@code keyEquals()} are never {@code null}.</li>
 *     <li>The argument of {@code keyHashCode()} is never {@code null}.</li>
 *     <li>The second argument of {@code keyEquals()} call is present in the map (or the set) or
 *     used to be present, but removed later. It means that if the implemented type has some
 *     restrictions on the keys that it may contain, those restrictions apply to the second
 *     argument. From performance perspective, since the second argument has already been inserted
 *     into the set or map, if it caches hashcode a-la {@code String}, the {@code keyEquals()}
 *     implementation may benefit from the knowledge that the hashcode of the second argument
 *     is already computed and cached.</li>
 *     <li>Arguments of {@code keyEquals()} are never identical (i. e. not references to the same
 *     object). In particular, this means that to have {@link IdentityHashMap}-like behaviour
 *     {@code keyEquals()} should be implemented as just returning {@code false}: <pre><code>
 * &#064;KolobokeMap
 * &#064;CustomKeyEquivalence
 * abstract class IdentityToIntMap&lt;K&gt; implements Map&lt;K, Integer&gt; {
 *
 *     static &lt;K&gt; IdentityToIntMap&lt;K&gt; withExpectedSize(int expectedSize) {
 *         return new KolobokeIdentityToIntMap&lt;K&gt;(expectedSize);
 *     }
 *
 *     abstract int getInt(K key);
 *     abstract int put(K key, int value);
 *
 *     final boolean keyEquals(K queriedKey, K keyInMap) {
 *         return false;
 *     }
 *
 *     final int keyHashCode(K key) {
 *         return System.identityHashCode(key);
 *     }
 * }</code></pre>
 *     </li>
 * </ul>
 *
 * <p>{@code keyEquals()} and {@code keyHashCode()} don't belong to any public interface provided
 * by the Koloboke Collections API or Koloboke Compile. So {@code @CustomKeyEquivalence} is kind
 * of a substitute of the {@code @Override} annotation for these methods. Koloboke Compile just
 * checks that methods are present, accessible from the generated subclass and have proper argument
 * types. It's <i>a user responsibility</i> to make these methods consistent with each other and
 * providing a valid equivalence relationship in the domain of the keys. See examples of valid
 * equivalences in the documentation to the following methods: {@link Equivalence#charSequence()},
 * {@link Equivalence#caseInsensitive()}.
 *
 * <p>If the annotated type implements {@link ObjSet} or {@link ObjObjMap} or other
 * {@code ObjXxxMap} interface from the Koloboke Collections API, <i>and</i> it declares {@code
 * equals()} method abstract to make Koloboke Compile implement it, or it declares {@code keySet()}
 * or {@code entrySet()} methods abstract and expects Koloboke Compile
 * to implement {@code equals()} methods correctly in the returned {@code Set} views of the map,
 * <i>it's a user responsibility</i> to implement {@link
 * ObjSet#equivalence()} or {@link ObjObjMap#keyEquivalence()} or similar method in the extended
 * {@code ObjXxxMap} interface and return from it an {@link Equivalence} object that reflects {@code
 * keyEquals()} and {@code keyHashCode()} strategy. For example, here is how a {@code @KolobokeMap}
 * with configurable key equivalence should be declared: <a name="configurable-key-equivalence-map">
 * </a><pre><code>
 * &#064;KolobokeMap
 * &#064;CustomKeyEquivalence
 * abstract class ConfigurableKeyEquivalenceMap&lt;K&gt; implements HashObjLongMap&lt;K&gt; {
 *
 *     static &lt;K&gt; HashObjLongMap&lt;K&gt; with(
 *             &#064;Nonnull Equivalence&lt;? super K&gt; keyEquivalence, int expectedSize) {
 *         return new KolobokeConfigurableKeyEquivalenceMap&lt;K&gt;(keyEquivalence, expectedSize);
 *     }
 *
 *     &#064;Nonnull
 *     private final Equivalence&lt;? super K&gt; keyEquivalence;
 *
 *     ConfigurableKeyEquivalenceMap(&#064;Nonnull Equivalence&lt;? super K&gt; keyEquivalence) {
 *         this.keyEquivalence = keyEquivalence;
 *     }
 *
 *     final boolean keyEquals(K queriedKey, K keyInMap) {
 *         return keyEquivalence.equivalent(queriedKey, keyInMap);
 *     }
 *
 *     final int keyHashCode(K key) {
 *         return keyEquivalence.hash(key);
 *     }
 *
 *     &#064;SuppressWarnings("unchecked")
 *     &#064;Nonnull
 *     &#064;Override
 *     public final Equivalence&lt;K&gt; keyEquivalence() {
 *         return (Equivalence&lt;K&gt;) keyEquivalence;
 *     }
 * }</code></pre>
 *
 * <p>In the Koloboke Collections's API, custom key equivalence strategy for sets or maps is
 * configured by methods {@link HashObjSetFactory#withEquivalence(Equivalence)} or {@link
 * HashObjObjMapFactory#withKeyEquivalence(Equivalence)} or similar methods in {@code
 * HashObjXxxMapFactory} interfaces.
 *
 * @see <a href="{@docRoot}/com/koloboke/compile/KolobokeMap.html#custom-key-equivalence">Custom
 * key equivalence section in the <code>@KolobokeMap</code> specification</a>
 * @see <a href="{@docRoot}/com/koloboke/compile/KolobokeMap.html#set-custom-key-equivalence">Custom
 * key equivalence section in the <code>@KolobokeMap</code> specification</a>
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface CustomKeyEquivalence {
}
