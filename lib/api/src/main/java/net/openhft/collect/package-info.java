/*
 * Copyright 2014 Higher Frequency Trading
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * <h2><a name="mutability">Mutability profiles</a></h2>
 *
 * <p>Factories allow to construct containers of several distinct degrees of mutability.
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
 * permits faster implementation of {@link net.openhft.collect.HashContainer hash containers}
 * and iterators over many data structures.
 *
 * <h3>Mutable</h3>
 *
 * <p>All operations are allowed.
 */
package net.openhft.collect;