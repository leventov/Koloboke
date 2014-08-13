/* with char|byte|short|int|long|float|double|obj elem */
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

import net.openhft.function./*f*/CharConsumer/**/;

import javax.annotation.Nonnull;


/**
 * A mutable pointer to the element in an iteration
 * of // if !(obj elem) //{@code char}// elif obj elem //object// endif //s.
 *
 * @see CharCollection#cursor()
 */
public interface CharCursor/*<>*/ extends Cursor {

    /**
     * Performs the given action for each element of the iteration after the cursor in forward
     * direction until all elements have been processed or the action throws an exception.
     * Exceptions thrown by the action are relayed to the caller.
     *
     * <p>{@code cur.forEachForward(action)} is exact equivalent of
     * <pre> {@code
     * while (cur.moveNext())
     *     action.accept(cur.elem());}</pre>
     *
     * @param action the action to be performed for each element
     */
    void forEachForward(@Nonnull /*f*/CharConsumer action);

    /**
     * Returns the element to which the cursor currently points.
     *
     * <p>Throws {@code IllegalStateException}, if the cursor isn't pointing to any element: if it
     * is in front of the first element, after the last, or the current element has been removed
     * using {@link #remove()} operation.
     *
     * @return the element to which the cursor currently points
     * @throws IllegalStateException if this cursor is initially in front of the first element
     *         and {@link #moveNext()} hasn't been called yet,
     *         or the previous call of {@code moveNext} returned {@code false},
     *         or {@code remove()} has been performed after the previous cursor movement
     */
    char elem();
}
