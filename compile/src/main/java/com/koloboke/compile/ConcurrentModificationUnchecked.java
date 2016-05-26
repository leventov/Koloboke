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

import com.koloboke.collect.Cursor;

import java.lang.annotation.*;
import java.util.*;


/**
 * Specifies that a Koloboke Compile-generated implementation of the annotated {@code Set}- or
 * {@code Map}-like class or interface <i>shouldn't</i> try to identify concurrent structural
 * modifications in returned {@link Iterator Iterators}, {@link Cursor Cursors} and during bulk
 * operations. If a {@link KolobokeSet @KolobokeSet}- or {@link KolobokeMap @KolobokeMap}-annotated
 * type is <i>not</i> annotated with {@code @ConcurrentModificationUnchecked}, i. e. by default,
 * Koloboke Compile generates an implementation that <i>does</i> try to identify concurrent
 * structural modifications (except through the iterator's or cursor's own {@code remove()} method)
 * and throws a {@link ConcurrentModificationException} if it spots such a modification, just like
 * {@link HashSet} or {@link HashMap} (see "fail-fast" sections in the specifications of these
 * classes). If the implemented type is annotated with {@code @ConcurrentModificationUnchecked},
 * Koloboke Compile generates an implementation that <i>doesn't</i> perform any checks aimed to
 * identify concurrent modifications.
 *
 * <p>Note that the fail-fast behavior cannot be guaranteed as it is, generally speaking, impossible
 * to make any hard guarantees in the presence of unsynchronized concurrent modification. Koloboke
 * Compile-generated implementations throw {@code ConcurrentModificationException} on a best-effort
 * basis. Therefore, it would be wrong to write a program that depended on this exception for its
 * correctness: <i>the fail-fast behavior of iterators should be used only to detect bugs.</i> This
 * is the reason why there is not such annotation as "ConcurrentModificationChecked" in Koloboke
 * Compile: it is impossible to guarantee that all concurrent modifications will be spotted.
 *
 * <p>Concurrent modification checks help to identify not only (and not mainly) concurrent
 * unsynchronized to the map or set instance from different thread, but primarily erroneous usage
 * of iterators, cursors and bulk operations (like {@code forEach()}, {@code removeIf()}, {@code
 * replaceAll()}), when the iterated collection or map is modified in the loop not via iterator's
 * or cursor's own methods, or in the body of the lambda, passed into the bulk operation. So <i>even
 * if there is a confidence that instances of the implementation of the annotated type won't be
 * accessed from multiple threads, it is strongly recommended <b>not</b> to disable concurrent
 * modification checks if any usage of iteration or bulk operations accepting lambdas on set or map
 * instances (or map's collection views) is planned</i>. Keeping concurrent modification checks
 * prevents bugs that occur really often.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface ConcurrentModificationUnchecked {
}
