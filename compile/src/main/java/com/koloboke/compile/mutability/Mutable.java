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
 * Specifies that Koloboke Compile should generate a <a
 * href="http://leventov.github.io/Koloboke/api/1.0/java8/overview-summary.html#mutability"><i>
 * mutable</i></a> (general-purpose) implementation of the annotated class or interface. Element or
 * entry removal operations are <i>allowed</i> in mutable collections.
 *
 * <p>This annotation should be applied only for source code self-documentation purposes, because
 * not applying any of the annotations from the
 * <a href="{@docRoot}/com/koloboke/compile/mutability/package-summary.html"><code>
 * com.koloboke.compile.mutability</code></a> package has the same effect.
 *
 * @see Updatable
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Mutable {
}