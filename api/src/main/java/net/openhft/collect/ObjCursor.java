package net.openhft.collect;

import net.openhft.function.Consumer;

/**
 * A mutable pointer to the element in an iteration of objects.
 *
 * @see Cursor
 */
public interface ObjCursor<E> extends Cursor {

    /**
     * Performs the given action for each element of the iteration after the cursor in forward
     * direction.
     * <pre> {@code
     * cur.forEachForward(action)
     * }</pre>
     * is exact equivalent of
     * <pre> {@code
     * while (cur.moveNext())
     *     action.accept(cur.elem());
     * }</pre>
     *
     * @param action the action to be performed for each element
     */
    void forEachForward(Consumer<? super E> action);

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
    E elem();
}
