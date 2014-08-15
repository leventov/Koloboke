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

import net.openhft.koloboke.collect.Container;

import javax.annotation.Nonnull;


/**
 * The root interface of sets and maps, based on hash tables.
 *
 * <p>Methods in this interface mostly regard to the specific operation on hash tables -
 * <em>rehash</em>. During rehash, entries of this container is moved from the old table
 * to the new one.
 *
 * <p>If the capacity of new table is greater than the old, such rehash is also referred
 * as <em>expansion</em> or <em>growth</em>. It is performed when the hash container's
 * {@linkplain #currentLoad() load} becomes too high, and operations' performance suffers,
 * or it's simply no place to insert the new entries.
 *
 * <p>If the capacity of new table is lesser than the old, such rehash is also called
 * <em>compaction</em> or <em>shrink</em>. It could be performed automatically after hash container
 * construction, if the {@linkplain HashConfig#getShrinkCondition() shrink condition} of the hash
 * container's {@linkplain #hashConfig() hash config} triggers, or manually via {@link #shrink()}
 * method. Shrink is useful for controlling hash container's memory consumption.
 *
 * <p>See <a href="../Container.html#mutability">{@code Container} mutability matrix</a> for methods
 * which are supported by hash containers with the specific mutability profile. All methods defined
 * in this interface directly are supported by hash containers with any mutability profile.
 *
 * @see HashContainerFactory
 */
public interface HashContainer extends Container {

    /**
     * Returns the hash config which holds all "magic" parameters of this hash container:
     * load and growth factors.
     *
     * @return the hash config of this container
     */
    @Nonnull HashConfig hashConfig();

    /**
     * Returns fullness of the internal tables, the fraction of taken slots. If the current load
     * exceeds {@link #hashConfig()}.{@link HashConfig#getMaxLoad() getMaxLoad()},
     * expansion is triggered.
     *
     * @return fullness of the hash container
     */
    double currentLoad();


    /**
     * Prepares the hash for insertion of {@code minSize - }{@link #size()} new elements without
     * excessive rehashes. Call of this method is a hint, but not a strict
     * guarantee that the next {@code minSize - size()} insertions will be done in real time.
     *
     * <p>If {@code minSize} is less than the current container size, the method returns
     * {@code false} immediately.
     *
     * @param minSize the desired minimum size, which the container is expected to reach soon
     * @return {@code true} if rehash has been actually performed to ensure capacity,
     *         and the next {@code minSize - size()} insertions won't cause rehash for sure.
     * @throws IllegalArgumentException if {@code minSize} is negative
     * @throws UnsupportedOperationException if the container doesn't support insertions
     */
    @Override
    boolean ensureCapacity(long minSize);

    /**
     * If the {@link #currentLoad() current load} is less than
     * {@link #hashConfig()}.{@link HashConfig#getTargetLoad() getTargetLoad()},
     * compaction is performed to fix this.
     *
     * @return {@code true} if the hash has been actually shrunk
     * @throws UnsupportedOperationException if the container is immutable
     */
    @Override
    boolean shrink();
}
