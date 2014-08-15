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

import java.util.Iterator;


/**
 * A mutable pointer to the element in an iteration. {@code Cursor} is a kind of hybrid between
 * Java standard {@link Iterator} interface and
 * <a href="http://msdn.microsoft.com/en-us/library/system.collections.ienumerator.aspx">
 *     {@code System.Collections.IEnumerator}</a> interface from .NET framework.
 *
 * <p>{@code Cursor} interface design typically permits slightly faster implementation,
 * than {@link Iterator}, so it is preferred in performance-critical code.
 * On the other hand, isn't supported by Java's "for-each" syntax.
 *
 * <p>See the <a href="{@docRoot}/overview-summary.html#iteration">comparison of iteration ways</a>
 * in the library.
 *
 * <p>Cursors of updatable and immutable containers don't support {@link #remove()} operation.
 * <a href="{@docRoot}/overview-summary.html#mutability">More about mutability profiles.</a>
 *
 * @see <a href="http://web.archive.org/web/20130603171836/http://codechaos.me/?p=22">Java Iterator
 *      vs .NET IEnumerator – The Small Things Matter</a>
 */
public interface Cursor {

    /**
     * Moves the cursor forward to the next element (to the first element, if the cursor is in front
     * of the first element). Returns {@code true} if it exists, {@code false} otherwise.
     * The cursor is located after the last element in the iteration and doesn't point to any
     * element after the unsuccessful movement.
     *
     * @return {@code true} if the cursor has moved forward to the next element,
     *         {@code false} if the iteration has no more elements
     */
    boolean moveNext();

    /**
     * Removes the element to which the cursor currently points (optional operation).
     *
     * <p>Throws {@link IllegalStateException} if the cursor isn't pointing to any element: if it
     * is in front of the first element, after the last, or the current element has been already
     * removed.
     *
     * @throws UnsupportedOperationException if the {@code remove} operation is not supported
     *         by this cursor
     * @throws IllegalStateException if this cursor is initially in front of the first element
     *         and {@link #moveNext()} hasn't been called yet,
     *         or the previous call of {@code moveNext} returned {@code false},
     *         or {@code remove()} has been already performed after the previous cursor movement
     */
    void remove();
}
