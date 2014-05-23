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


package net.openhft.collect;


/**
 * Common interface of sets and maps, based on hash tables.
 */
public interface HashContainer extends Container {

    /**
     * Returns the hash config which holds all "magic" parameters of this hash:
     * load and growth factors.
     */
    HashConfig hashConfig();

    /**
     * Returns fullness of the internal tables, the fraction of taken slots. If current load
     * reaches load factor of the hash, expansion is triggered.
     *
     * @return fullness of the hash
     */
    float currentLoad();


    /**
     * Prepares hash for inserting {@code minSize - size()} new elements without
     * excessive rehashes. Call of this method is a hint, but not a strict
     * guarantee that the next {@code minSize - size()} insertions will be done in real time.
     *
     * <p>If {@code minSize} is less than the current container size, the method returns
     * {@code false} immediately.
     *
     * @param minSize the number of additional elements that will be inserted in the hash soon
     * @return {@code true} if rehash has been actually performed to ensure capacity,
     *         and the next {@code minSize - size()} insertions won't cause rehash for sure.
     * @throws java.lang.IllegalArgumentException if {@code minSize} is negative
     * @throws java.lang.UnsupportedOperationException if the container doesn't support insertions
     */
    @Override
    boolean ensureCapacity(long minSize);

    /**
     * If {@link #currentLoad()} is less than load factor, compaction is performed to fix this.
     *
     * @return {@code true} if the hash has been actually shrunk
     * @throws java.lang.UnsupportedOperationException if the container is immutable
     */
    @Override
    boolean shrink();
}
