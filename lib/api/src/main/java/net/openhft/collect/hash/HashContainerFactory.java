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

package net.openhft.collect.hash;

import javax.annotation.Nonnull;


/**
 * Common configuration for factories of hash containers.
 *
 * <p>Configurations from this interface, hash config and default expected size, don't affect
 * application semantics in any way, only performance and memory footprint characteristics.
 *
 * @param <T> the concrete factory type which extends this interface
 */
public interface HashContainerFactory<T extends HashContainerFactory<T>> {

    /**
     * Returns the hash config, with which containers constructed by this factory are initialized.
     *
     * <p>Default hash config is {@link HashConfig#getDefault()}.
     *
     * @return the hash config of this factory
     */
    @Nonnull HashConfig getHashConfig();

    /**
     * Returns a copy of this factory with hash config set to the given one.
     *
     * @param config the new hash config
     * @return a copy of this factory with hash config set to the given one
     */
    T withHashConfig(@Nonnull HashConfig config);

    /**
     * Returns the default expected size. This size is used to initialize hash containers
     * in no-arg factory methods and methods that accept uncountable sources of elements:
     * {@link Iterable iterables}, {@link java.util.Iterator iterators}, supplier functions, etc.
     *
     * <p>Default value of the default expected size is {@literal 10}.
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
    T withDefaultExpectedSize(int defaultExpectedSize);
}
