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

package com.koloboke.compile.hash.algo.openaddressing;

import java.lang.annotation.*;


/**
 * Specifies that Koloboke Compile should generate an implementation of the
 * annotated class or interface based on a hash table with the
 * <a href="https://en.wikipedia.org/wiki/Open_addressing">open addressing</a> strategy of collision
 * resolution, using <a href="https://en.wikipedia.org/wiki/Double_hashing">double hashing</a>.
 *
 * <p>Hash table capacities are chosen from a predefined set of prime numbers, with max difference
 * below 0.5% between neighbouring ones (this condition starts to be true only from capacities of
 * a few thousands). The smallest possible capacities are 5, 7, 11, 13, 19, 23, 29, 41, 43, ...
 * Density of the predefined capacities and the smallest possible capacities could be changed in any
 * future version of Koloboke Compile.
 *
 * <p>This algorithm usually performs slower than {@link QuadraticHashing @QuadraticHashing}, while
 * it is not considerably better by other characteristics. So between {@code @DoubleHashing} and
 * {@code @QuadraticHashing} the latter should be the default choice.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface DoubleHashing {
}
