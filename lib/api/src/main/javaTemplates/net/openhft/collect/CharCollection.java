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


//TODO doc
public interface CharCollection extends Collection<Character>, Container {

    /**
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
     * @see #contains(Object)
     */
    boolean contains(char v);


    /**
     * @deprecated Use specialization {@link #toCharArray()} instead
     */
    @Override
    @Deprecated
    @Nonnull
    Object[] toArray();

    /**
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
     * @see #toArray()
     * @see #toArray(char[])
     */
    char[] toCharArray();

    /**
     * Returns an array containing elements in this collection.
     *
     * <p>If this collection fits in the specified array with room to spare
     * (i.e., the array has more elements than this collection), the element in
     * the array immediately following the end of the collection is set
     * to {@code // const 0 elem //0} (zero).  This is useful in determining
     * the length of this collection <i>only</i> if the caller knows that this
     * collection does not contain any elements representing null.
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
     * @see #toArray(Object[])
     */
    char[] toArray(char[] a);

    @Nonnull
    CharCursor cursor();


    /* if JDK8 jdk */
    /**
     * @deprecated Use specialization {@link #forEach(CharConsumer)} instead
     */
    @Override
    @Deprecated
    void forEach(Consumer<? super Character> action);
    /* endif */

    void forEach(CharConsumer action);

    boolean forEachWhile(CharPredicate predicate);


    /**
     * @deprecated Use specialization {@link #add(char)} instead
     */
    @Override
    @Deprecated
    boolean add(Character e);

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
     * @deprecated Use specialization {@link #removeIf(CharPredicate)} instead
     */
    @Override
    @Deprecated
    boolean removeIf(Predicate<? super Character> filter);
    /* endif */

    boolean removeIf(CharPredicate filter);
}
