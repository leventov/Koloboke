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

package com.koloboke.collect.map;

import com.koloboke.collect.Cursor;
import com.koloboke.function./*f*/CharShortConsumer/**/;

import javax.annotation.Nonnull;


/**
 * A mutable pointer to the entry in an iteration of entries with {@code // raw //char} keys and
 * {@code // raw //short} values.
 *
 * <p>Basic {@code CharShortCursor} usage idiom is: <pre>{@code
 * for (CharShortCursor//<>// cur = map.cursor(); cur.moveNext();) {
 *     // Work with cur.key() and cur.value()
 *     // Call cur.remove() to remove the current entry
 * }}</pre>
 *
 * <p>See the <a href="{@docRoot}/overview-summary.html#iteration">comparison of iteration ways</a>
 * in the library.
 *
 * <p>{@code CharShortCursors} of immutable maps don't support {@link #setValue(//raw//short)}
 * operation. <a href="{@docRoot}/overview-summary.html#mutability">More about mutability profiles.
 * </a>
 *
 * @see CharShortMap#cursor()
 */
public interface CharShortCursor/*<>*/ extends Cursor {

    /**
     * Performs the given action for each entry of the iteration after the cursor in forward
     * direction until all entries have been processed or the action throws an exception.
     * Exceptions thrown by the action are relayed to the caller.
     *
     * {@code cur.forEachForward(action)} is exact equivalent of
     * <pre> {@code
     * while (cur.moveNext())
     *     action.accept(cur.key(), cur.value());}</pre>
     *
     * @param action the action to be performed for each entry
     */
    void forEachForward(@Nonnull /*f*/CharShortConsumer action);

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
