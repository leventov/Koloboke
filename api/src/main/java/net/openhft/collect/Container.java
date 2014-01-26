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

public interface Container {

    /**
     * @return number of elements (entries) in the container
     */
    int size();


    /**
     * If the container is array-based data structure, increases the capacity of this container,
     * if necessary, to ensure that it can hold at least the number of elements specified
     * by the {@code minSize} argument. Returns {@code true}, if the capacity has been increased,
     * {@code false} if it isn't necessary.
     *
     * <p>If the container is linked data structure, does nothing and returns {@code false}.
     *
     * @param minSize the desired minimum size
     * @return {@code true} if the capacity has been increased, {@code false} if it isn't necessary
     * @throws java.lang.UnsupportedOperationException if the container doesn't support insertions
     */
    boolean ensureCapacity(int minSize);

    /**
     * If the container is array-based data structure, and the memory is overused due to preventive
     * expansion on elements insertion, decreases the capacity and returns {@code true},
     * returns {@code false} if the capacity is already minimum needed to hold the current
     * number of elements.
     *
     * <p>If the container is linked data structure, does nothing and returns {@code false}.
     *
     * @return {@code true} if the container has been actually shrunk, {@code false} otherwise
     * @throws java.lang.UnsupportedOperationException if the container is immutable
     */
    boolean shrink();
}
