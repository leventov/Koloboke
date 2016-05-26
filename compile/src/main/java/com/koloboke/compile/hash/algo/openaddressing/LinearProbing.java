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

import com.koloboke.collect.hash.HashConfig;

import java.lang.annotation.*;


/**
 * Specifies that Koloboke Compile should generate an implementation of the
 * annotated class or interface based on a hash table with the
 * <a href="https://en.wikipedia.org/wiki/Open_addressing">open addressing</a> strategy of collision
 * resolution, using <a href="https://en.wikipedia.org/wiki/Linear_probing">linear probing</a>.
 *
 * <p>This is the default probing scheme for the Koloboke Compile generation, but it is not
 * guaranteed to remain so in the future versions of Koloboke Compile.
 *
 * <p>The implementation uses only power-of-two hash table capacities, therefore {@link HashConfig},
 * passed to the <a href="{@docRoot}/com/koloboke/compile/KolobokeMap.html#constructors">second
 * constructor</a> of the implementation class, should have {@linkplain HashConfig#getGrowthFactor()
 * growth factor} equal to 2.0. Otherwise an exception is thrown and map or set instance is not
 * constructed. If the implementation should be configurable with hash configs with the growth
 * factor different from 2.0, the map or set type should be annotated with {@link
 * QuadraticHashing @QuadraticHashing} or {@link DoubleHashing @DoubleHashing}.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface LinearProbing {
}
