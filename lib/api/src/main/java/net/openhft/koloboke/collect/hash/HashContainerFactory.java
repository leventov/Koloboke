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

package net.openhft.koloboke.collect.hash;

import net.openhft.koloboke.collect.ContainerFactory;

import javax.annotation.Nonnull;


/**
 * Common configuration for factories of hash containers.
 *
 * <p>Configurations from this interface, hash config and default expected size, don't affect
 * application semantics in any way, only performance and memory footprint characteristics.
 *
 * @param <F> the concrete factory type which extends this interface
 * @see HashContainer
 */
public interface HashContainerFactory<F extends HashContainerFactory<F>>
        extends ContainerFactory<F> {

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
    F withHashConfig(@Nonnull HashConfig config);
}
