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
 * Contains annotations specifying that Koloboke Compile should generate implementations of the
 * annotated types with a particular <a
 * href="http://leventov.github.io/Koloboke/api/1.0/java8/overview-summary.html#mutability"><i>
 * mutability profile</i></a>.
 *
 * <p>At most one annotation of an annotation type from this package could be applied to a class or
 * a interface.
 *
 * <p>If none of the annotations from this package is applied to a {@link
 * com.koloboke.compile.KolobokeMap @KolobokeMap}- or {@link
 * com.koloboke.compile.KolobokeSet @KolobokeSet}-annotated type, Koloboke Compile generates
 * an implementation with <i>the least mutability, which supports all the abstract methods in
 * an implemented type</i>. For example, if a {@code @KolobokeSet}-annotated class has only four
 * abstract methods: {@code add(e)}, {@code contains(e)}, {@code size()} and {@code clear()},
 * Koloboke Compile generates an {@linkplain com.koloboke.compile.mutability.Updatable updatable}
 * implementation for this class, because all abstract methods are supported by updatable mutability
 * profile. If a {@code @KolobokeMap}-annotated class or interface has an abstract {@code
 * remove(key)} method, by default Koloboke Compile generates a {@linkplain
 * com.koloboke.compile.mutability.Mutable mutable} implementation for this type, because {@code
 * remove(key)} method is supported by Mutable and not supported by Updatable mutability profile.
 *
 * <p>See full list of methods, supported by different mutability profiles, for <a
 * href="http://leventov.github.io/Koloboke/api/1.0/java8/overview-summary.html#collection-mutability">
 * {@code Set}-like</a> and <a
 * href="http://leventov.github.io/Koloboke/api/1.0/java8/overview-summary.html#maps-mutability">
 * {@code Map}-like</a> types.
 *
 * <p>In the Koloboke Collections API, the client makes the mutability profile choice at the moment
 * of a set or map construction. Factory methods offer methods called {@code newMutableXxx} and
 * {@code newUpdatableXxx}, e. g. {@link com.koloboke.collect.set.IntSetFactory#newUpdatableSet()},
 * {@link com.koloboke.collect.set.IntSetFactory#newMutableSet()}.
 *
 * @see <a href="{@docRoot}/com/koloboke/compile/KolobokeMap.html#mutability">Mutability section in
 * the <code>@KolobokeMap</code> specification</a>
 * @see <a href="{@docRoot}/com/koloboke/compile/KolobokeSet.html#set-mutability">Mutability section
 * in the <code>@KolobokeSet</code> specification</a>
 */
package com.koloboke.compile.mutability;