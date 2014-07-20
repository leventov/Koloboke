/* with
 char|byte|short|int|long|float|double|object key
 short|byte|char|int|long|float|double|object value
*/
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

package net.openhft.collect.map;

import net.openhft.collect.Cursor;
import net.openhft.function./*f*/CharShortConsumer/**/;


/**
 * A mutable pointer to the entry in an iteration of entries with {@code Object} keys and
 * {@code char} values.
 *
 * @see net.openhft.collect.Cursor
 */
public interface CharShortCursor/*<>*/ extends Cursor {

    /**
     * Moves the cursor forward to the next entry, returns {@code true} if it exists,
     * {@code false} otherwise. The cursor is located after the last entry in the iteration
     * and doesn't point to any entry after the unsuccessful movement.
     *
     * @return {@code true} if the cursor has moved forward to the next entry,
     *         {@code false} if the iteration has no more entries
     */
    @Override
    boolean moveNext();

    /**
     * Removes the entry to which the cursor currently points (optional operation).
     *
     * <p>Throws {@code IllegalStateException} if the cursor isn't pointing to any entry: if it
     * is in front of the first entry, after the last, or the current entry has been already
     * removed.
     *
     * @throws UnsupportedOperationException if the {@code remove} operation is not supported
     *         by this cursor
     * @throws IllegalStateException if this cursor is initially in front of the first entry
     *         and {@link #moveNext()} hasn't been called yet,
     *         or the previous call of {@code moveNext} returned {@code false},
     *         or {@code remove()} has been already performed after the previous cursor movement
     */
    @Override
    void remove();

    /**
     * Performs the given action for each entry of the iteration after the cursor in forward
     * direction.
     * <pre>{@code
     * cur.forEachForward(action)
     * }</pre>
     * is exact equivalent of
     * <pre>{@code
     * while (cur.moveNext())
     *     action.accept(cur.key(), cur.value());
     * }</pre>
     *
     * @param action the action to be performed for each entry
     */
    void forEachForward(/*f*/CharShortConsumer action);

    /**
     * Returns the key of the entry to which the cursor currently points.
     *
     * <p>Throws {@code IllegalStateException}, if the cursor isn't pointing to any entry: if it
     * is in front of the first entry, after the last, or the current entry has been removed
     * using {@link #remove()} operation.
     *
     * @return the key of the entry to which the cursor currently points
     * @throws IllegalStateException if this cursor is initially in front of the first entry
     *         and {@link #moveNext()} hasn't been called yet,
     *         or the previous call of {@code moveNext} returned {@code false},
     *         or {@code remove()} has been performed after the previous cursor movement
     */
    char key();

    /**
     * Returns the value of the entry to which the cursor currently points.
     *
     * <p>Throws {@code IllegalStateException}, if the cursor isn't pointing to any entry: if it
     * is in front of the first entry, after the last, or the current entry has been removed
     * using {@link #remove()} operation.
     *
     * @return the value of the entry to which the cursor currently points
     * @throws IllegalStateException if this cursor is initially in front of the first entry
     *         and {@link #moveNext()} hasn't been called yet,
     *         or the previous call of {@code moveNext} returned {@code false},
     *         or {@code remove()} has been performed after the previous cursor movement
     */
    short value();

    /**
     * Replaces the value of the entry to which the cursor currently points (optional operation).
     *
     * <p>Throws {@code IllegalStateException} if the cursor isn't pointing to any entry: if it
     * is in front of the first entry, after the last, or the current entry has been removed
     * using {@link #remove()} operation.
     *
     * @param value new value to be stored in the entry to which the cursor currently points
     * @throws UnsupportedOperationException if the {@code setValue} operation is not supported
     *         by this cursor
     * @throws IllegalArgumentException if some property this value prevents it from being stored
     *         in the entries of the iteration
     * @throws IllegalStateException if this cursor is initially in front of the first entry
     *         and {@link #moveNext()} hasn't been called yet,
     *         or the previous call of {@code moveNext} returned {@code false},
     *         or {@code remove()} has been performed after the previous cursor movement
     */
    void setValue(short value);
}
