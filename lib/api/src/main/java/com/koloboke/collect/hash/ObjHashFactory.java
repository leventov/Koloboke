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

package com.koloboke.collect.hash;


import com.koloboke.compile.NullKeyAllowed;


/**
 * Common configuration for factories of hash containers with {@code Object} keys.
 *
 * <p>Currently {@code ObjHashFactory} allows to specify only if {@code null} key is allowed
 * or disallowed in hash containers, constructed by the factory. This is a performance hint:
 * hash containers might, but aren't required to throw {@link NullPointerException} on putting
 * {@code null} key, if {@code null} key is disallowed.
 *
 * <p>By default, {@code null} key is <em>disallowed</em>. Because in 99% of cases {@code null}
 * key isn't possible (moreover, it is a bad practice to use {@code null} along with ordinary
 * objects), on the other side, when {@code null} key is disallowed, substantial optimizations
 * of hash table implementations become possible.
 *
 * <p>To construct hash containers which strictly follow {@link java.util.HashMap}
 * and {@link java.util.HashSet} behaviour (these collections support {@code null} keys), you
 * <em>must</em> configure the corresponding factory to allow {@code null} keys:
 * <pre>{@code
 * factory = factory.withNullKeyAllowed(true);}</pre>
 *
 * <p>Koloboke Compile's counterpart of the {@code withNullKeyAllowed()} configuration is the {@link
 * NullKeyAllowed @NullKeyAllowed} annotation. An important difference is that {@code
 * ObjHashFactory} doesn't guarantee that constructed containers throws {@code NullPointerException}
 * to enforce the {@code null} key disallowance in runtime, while {@code @NullKeyAllowed} does
 * guarantee that.
 *
 * @param <F> the concrete factory type which extends this interface
 */
public interface ObjHashFactory<F extends ObjHashFactory<F>> extends HashContainerFactory<F> {

    /**
     * Returns {@code true} if {@code null} key is allowed, {@code false} otherwise.
     *
     * <p>Default: {@code false}.
     *
     * @return {@code true} if null key is allowed, {@code false} otherwise
     */
    boolean isNullKeyAllowed();

    /**
     * Returns a copy of this factory with {@code null} key allowed or disallowed, as specified.
     *
     * <p>This is a performance hint: hash containers might, but aren't required to throw
     * {@link NullPointerException} on putting {@code null} key, if {@code null} key is disallowed.
     * @param nullKeyAllowed if {@code null} should be allowed in the containers contructed
     *        by the returned factory
     * @return a copy of this factory with {@code null} key allowed or disallowed, as specified
     */
    F withNullKeyAllowed(boolean nullKeyAllowed);
}
