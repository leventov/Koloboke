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

package com.koloboke.compile.mutability;

import java.lang.annotation.*;


/**
 * Specifies that Koloboke Compile should generate an <a
 * href="http://leventov.github.io/Koloboke/api/1.0/java8/overview-summary.html#mutability"><i>
 * updatable</i></a> implementation of the annotated class or interface. Updatable
 * collections and maps disallow removals of <i>individual</i> elements or entries,
 * typically names of these operations include "remove" or "retain" verb. {@link
 * UnsupportedOperationException} is thrown in implementations of such methods. Emphasis on
 * "individual" elements or entries means that {@code clear()} operation is allowed. See exact list
 * of allowed and disallowed operations for <a
 * href="http://leventov.github.io/Koloboke/api/1.0/java8/overview-summary.html#collection-mutability">
 * {@code Collections} (including {@code Sets})</a> and <a
 * href="http://leventov.github.io/Koloboke/api/1.0/java8/overview-summary.html#map-mutability">
 * {@code Maps}</a>.
 *
 * <p>Think about updatable containers as "non-decreasing", which could be "reset"
 * from time to time by calling {@code clear()}.
 *
 * <p>In real practice individual element or entry removals are often not needed. On the other hand,
 * prohibition of removals permits faster implementation of hash table-based containers and
 * iterators over many data structures.
 *
 * @see <a href="{@docRoot}/com/koloboke/compile/mutability/package-summary.html">
 *     <code>com.koloboke.compile.mutability</code></a>
 * @see Mutable
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Updatable {
}