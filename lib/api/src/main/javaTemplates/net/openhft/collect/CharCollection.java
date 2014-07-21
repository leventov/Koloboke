/* with char|byte|short|int|long|float|double elem */
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


import net.openhft.function.CharConsumer;
import net.openhft.function.CharPredicate;
import net.openhft.function.Consumer;
import net.openhft.function.Predicate;

import javax.annotation.Nonnull;

import java.util.Collection;
import java.util.Iterator;


/**
 * A {@link Collection} specialization with {@code char} elements.
 */
public interface CharCollection extends Collection<Character>, Container {

    /**
     * {@inheritDoc}
     * @deprecated Use specialization {@link #contains(char)} instead
     */
    @Override
    @Deprecated
    boolean contains(Object o);

    /**
     * Returns {@code true} if this collection contains at least one element
     * equals to the specified one.
     *
     * @param v element whose presence in this collection is to be tested
     * @return {@code true} if this collection contains the specified element
     */
    boolean contains(char v);


    /**
     * {@inheritDoc}
     * @deprecated Use specialization {@link #toCharArray()} instead
     */
    @Override
    @Deprecated
    @Nonnull
    Object[] toArray();

    /**
     * {@inheritDoc}
     * @deprecated Use specialization {@link #toArray(char[])} instead
     */
    @Override
    @Deprecated
    @Nonnull
    <T> T[] toArray(@Nonnull T[] array);

    /**
     * Returns an array containing all of the elements in this collection.
     * If this collection makes any guarantees as to what order its elements
     * are returned by its iterator, this method must return the elements
     * in the same order.
     *
     * <p>The returned array will be "safe" in that no references to it
     * are maintained by this collection.  (In other words, this method must
     * allocate a new array even if this collection is backed by an array).
     * The caller is thus free to modify the returned array.
     *
     * <p>This method acts as bridge between array-based and collection-based APIs.
     *
     * @return an array containing all the elements in this collection
     * @see #toArray(char[])
     */
    @Nonnull
    char[] toCharArray();

    /**
     * Returns an array containing elements in this collection.
     *
     * <p>If this collection fits in the specified array with room to spare
     * (i.e., the array has more elements than this collection), the element in
     * the array immediately following the end of the collection is set
     * to {@code // const elem 0 //0// endconst //} (zero).  This is useful in determining
     * the length of this collection <i>only</i> if the caller knows that this
     * collection does not contain any elements equal to {@code // const elem 0 //0// endconst //}.
     *
     * <p>If the native array is smaller than the collection size,
     * the array will be filled with elements in Iterator order
     * until it is full and exclude the remainder.
     *
     * <p>If this collection makes any guarantees as to what order its elements
     * are returned by its iterator, this method must return the elements
     * in the same order.
     *
     * @param a the array into which the elements of this collection are to be
     *        stored.
     * @return an {@code char[]} containing all the elements in this collection
     * @throws NullPointerException if the specified array is {@code null}
     * @see #toCharArray()
     */
    @Nonnull char[] toArray(@Nonnull char[] a);

    /**
     * Returns a new cursor over this collection's elements. Cursor iteration order is always
     * corresponds to the {@link #iterator() iterator}'s order.
     *
     * @return a new cursor over this collection's elements
     * @see <a href="package-summary.html#iteration">
     *     Comparison of iteration options in the library</a>
     */
    @Nonnull
    CharCursor cursor();

    /**
     * Returns a new iterator over this collection's elements.
     *
     * @return a new iterator over this collection's elements
     * @see <a href="package-summary.html#iteration">
     *     Comparison of iteration options in the library</a>
     */
    @Override
    @Nonnull
    CharIterator iterator();

    /* if JDK8 jdk */
    /**
     * {@inheritDoc}
     * @deprecated Use specialization {@link #forEach(CharConsumer)} instead
     */
    @Override
    @Deprecated
    void forEach(@Nonnull Consumer<? super Character> action);
    /* endif */

    /**
     * Performs the given action for each element of this collection until all elements have been
     * processed or the action throws an exception.  Unless otherwise specified by the implementing
     * class, actions are performed in the order of iteration (if an iteration order is specified).
     * Exceptions thrown by the action are relayed to the caller.
     *
     * @param action the action to be performed for each element
     * @see <a href="package-summary.html#iteration">
     *     Comparison of iteration options in the library</a>
     */
    void forEach(@Nonnull CharConsumer action);

    /**
     * Checks the given {@code predicate} on each element of this collection until all element
     * have been processed or the predicate returns {@code false} for some element,
     * or throws an {@code Exception}. Exceptions thrown by the predicate are relayed to the caller.
     *
     * <p>Unless otherwise specified by the implementing class, elements are checked
     * by the predicate in the order of iteration (if an iteration order is specified).
     *
     * <p>If this collection is empty, this method returns {@code true} immediately.
     *
     * @return {@code true} if the predicate returned {@code true} for all elements of this
     *         collection, {@code false} if it returned {@code false} for the element
     * @param predicate the predicate to be checked for each element of this collection
     * @see <a href="package-summary.html#iteration">
     *     Comparison of iteration options in the library</a>
     */
    boolean forEachWhile(@Nonnull CharPredicate predicate);


    /**
     * {@inheritDoc}
     * @deprecated Use specialization {@link #add(char)} instead
     */
    @Override
    @Deprecated
    boolean add(@Nonnull Character e);

    /**
     * Ensures that this collection contains the specified element (optional
     * operation).  Returns {@code true} if this collection changed as a
     * result of the call.  (Returns {@code false} if this collection does
     * not permit duplicates and already contains the specified element.)
     *
     * <p>Collections that support this operation may place limitations on what
     * elements may be added to this collection.
     * Collection classes should clearly specify in their documentation any
     * restrictions on what elements may be added.
     *
     * <p>If a collection refuses to add a particular element for any reason
     * other than that it already contains the element, it <i>must</i> throw
     * an exception (rather than returning {@code false}).  This preserves
     * the invariant that a collection always contains the specified element
     * after this call returns.
     *
     * @param e element whose presence in this collection is to be ensured
     * @return {@code true} if this collection changed as a result of the call
     * @throws UnsupportedOperationException if the {@code add} operation
     *         is not supported by this collection
     * @throws IllegalArgumentException if some property of the element
     *         prevents it from being added to this collection
     * @throws IllegalStateException if the element cannot be added at this
     *         time due to insertion restrictions
     * @see #add(Object)
     */
    boolean add(char e);


    /**
     * {@inheritDoc}
     * @deprecated Use specialization {@link #removeChar(char)} instead
     */
    @Override
    @Deprecated
    boolean remove(Object o);

    /**
     * Removes a single copy of the specified element from this
     * collection, if it is present (optional operation).
     * Returns {@code true} if this collection contained the specified element
     * (or equivalently, if this collection changed as a result of the call).
     *
     * <p>The name of this method is "removeChar", not "remove", because "remove" conflicts
     * with {@link java.util.List#remove(int)}.
     *
     * @param v element to be removed from this collection, if present
     * @return {@code true} if an element was removed as a result of this call
     * @throws UnsupportedOperationException if the {@code remove} operation
     *         is not supported by this collection
     * @see #remove(Object)
     */
    boolean removeChar(char v);


    /* if JDK8 jdk */
    /**
     * {@inheritDoc}
     * @deprecated Use specialization {@link #removeIf(CharPredicate)} instead
     */
    @Override
    @Deprecated
    boolean removeIf(@Nonnull Predicate<? super Character> filter);
    /* endif */

    /**
     * Removes all of the elements of this collection that satisfy the given predicate.
     * Errors or runtime exceptions thrown during iteration or by the predicate are relayed
     * to the caller.
     *
     * @param filter a predicate which returns {@code true} for elements to be removed
     * @return {@code true} if any elements were removed
     * @throws NullPointerException if the specified filter is null
     * @throws UnsupportedOperationException if elements cannot be removed from this collection.
     *         Implementations may throw this exception if a matching element cannot be removed
     *         or if, in general, removal is not supported.
     * @see <a href="package-summary.html#iteration">
     *     Comparison of iteration options in the library</a>
     */
    boolean removeIf(@Nonnull CharPredicate filter);
}
