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

package net.openhft.koloboke.collect;

import javax.annotation.Nonnull;


/**
 * Root container factory interface. It isn't useful itself, hence there are no direct
 * implementations, it only defines common configuration methods.
 *
 * @param <F> the concrete factory type which extends this interface
 */
public interface ContainerFactory<F extends ContainerFactory<F>> {

    /**
     * Returns the default expected size. This size is used to initialize containers
     * in no-arg factory methods and methods that accept uncountable sources of elements:
     * {@linkplain Iterable iterables}, {@linkplain java.util.Iterator iterators}, supplier
     * functions, etc.
     *
     * <p>Default value of the default expected size is 10.
     *
     * @return the default expected size
     */
    int getDefaultExpectedSize();

    /**
     * Returns a copy of this factory with default expected size set to the given value.
     *
     * @param defaultExpectedSize the new default expected size
     * @return a copy of this factory with default expected size set to the given value
     * @throws IllegalArgumentException if {@code defaultExpectedSize} is non-positive
     */
    @Nonnull
    F withDefaultExpectedSize(int defaultExpectedSize);
}
