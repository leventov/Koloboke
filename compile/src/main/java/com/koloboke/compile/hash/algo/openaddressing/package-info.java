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
 * Contains annotation specifying that Koloboke Compile should generate implementations of the
 * annotated types based on hash tables with <a href="https://en.wikipedia.org/wiki/Open_addressing"
 * >open addressing</a> strategy of collision resolution, using
 * a particular probing scheme and related aspects of the hash table implementation, such us table
 * capacity choice.
 *
 * <p>At most one annotation of an annotation type from this package could be applied to a class or
 * a interface.
 *
 * <p>If none of the annotations from this package is applied to a {@link
 * com.koloboke.compile.KolobokeMap @KolobokeMap}- or {@link
 * com.koloboke.compile.KolobokeSet @KolobokeSet}-annotated type, Koloboke Compile generates an
 * implementation using the {@linkplain com.koloboke.compile.hash.algo.openaddressing.LinearProbing
 * linear probing} scheme. However, <i>the default algorithm might be changed</i> in any future
 * version of Koloboke Compile.
 *
 * <p>In the Koloboke Collections implementation library, implementation probing scheme is chosen
 * per-factory, depending on the configured {@link com.koloboke.collect.hash.HashConfig} via
 * the {@link com.koloboke.collect.hash.HashContainerFactory#withHashConfig} method.
 */
package com.koloboke.compile.hash.algo.openaddressing;