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

/**
 * The root interface of all collections within the library.
 *
 * <p>Methods {@link #ensureCapacity(long)} and {@link #shrink()} make sense only for array-based
 * containers.
 *
 * <h2><a name="mutability"></a><a href="{@docRoot}/overview-summary.html#mutability">Mutability</a>
 * matrix</h2>
 *
 * <table BORDER CELLPADDING=3 CELLSPACING=1>
 *   <caption>This matrix shows which methods of this
 *   interface are supported by containers with different mutability profiles.</caption>
 *   <tr><td>Method \ Mutability</td><td>Mutable</td><td>Updatable</td><td>Immutable</td></tr>
 *   <tr><td>{@link #size()}</td>              <td>✓</td><td>✓</td><td>✓</td></tr>
 *   <tr><td>{@link #sizeAsLong()}</td>        <td>✓</td><td>✓</td><td>✓</td></tr>
 *   <tr><td>{@link #isEmpty()}</td>           <td>✓</td><td>✓</td><td>✓</td></tr>
 *   <tr><td>{@link #ensureCapacity(long)}</td><td>✓</td><td>✓</td><td>-</td></tr>
 *   <tr><td>{@link #shrink()}</td>            <td>✓</td><td>✓</td><td>-</td></tr>
 *   <tr><td>{@link #clear()}</td>             <td>✓</td><td>✓</td><td>-</td></tr>
 * </table>
 */
public interface Container {

    /**
     * Returns the number of elements (entries) in this container. If this container contains more
     * than {@code Integer.MAX_VALUE} elements (entries), returns {@code Integer.MAX_VALUE}.
     *
     * @return the number of elements (entries) in this container
     * @see #sizeAsLong()
     */
    int size();

    /**
     * Returns the number of elements (entries) in this container.
     *
     * @return the number of elements (entries) in this container
     */
    long sizeAsLong();

    /**
     * Prepares the container for insertion of {@code minSize - }{@link #sizeAsLong()} new elements
     * without excessive capacity increases.
     *
     * <p>If the container is an array-based data structure, increases the capacity of this
     * container, if necessary, to ensure that it can hold at least {@code minSize} elements.
     * Returns {@code true}, if the capacity has been increased, {@code false}
     * if it isn't necessary.
     *
     * <p>If the container is a linked data structure, does nothing and returns {@code false}.
     *
     * @param minSize the desired minimum size, which the container is expected to reach soon
     * @return {@code true} if the capacity has been increased, {@code false} if it isn't necessary
     * @throws UnsupportedOperationException if the container doesn't support insertions
     */
    boolean ensureCapacity(long minSize);

    /**
     * Decreases this container's capacity, if there is memory overuse.
     *
     * <p>If the container is array-based data structure, and the memory is overused due to
     * preventive expansion on elements insertion, decreases the capacity and returns {@code true},
     * returns {@code false} if the capacity is already minimum needed to hold the current
     * number of elements.
     *
     * <p>If the container is linked data structure, does nothing and returns {@code false}.
     *
     * @return {@code true} if the container has been actually shrunk, {@code false} otherwise
     * @throws UnsupportedOperationException if the container is immutable
     */
    boolean shrink();

    /**
     * Removes all of the elements (entries) from this container.
     * The container will be empty after this method returns.
     *
     * @throws UnsupportedOperationException if the container is immutable
     */
    void clear();

    /**
     * Returns {@code true} if this container contains no elements.
     *
     * @return {@code true} if this container contains no elements
     */
    boolean isEmpty();
}
