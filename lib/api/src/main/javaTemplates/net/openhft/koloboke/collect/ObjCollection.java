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

import net.openhft.koloboke.function.Consumer;
import net.openhft.koloboke.function.Predicate;
import javax.annotation.Nonnull;

import java.util.Collection;


/**
 * A collection of objects, the library's extension of the classic {@link Collection} interface.
 *
 * <p>All methods of {@link Collection} interface defined in terms of elements equality and
 * referring to {@link Object#equals(Object)} method are supposed to use {@link #equivalence()}
 * in this interface. Thus in some sense {@code ObjCollection} violates general {@link Collection}
 * contract, but this approach provides a great flexibility. If you need strict {@link Collection}
 * implementation, you can always construct an {@code ObjCollection} with default equality,
 * i. e. {@code equivalence() == }{@link Equivalence#defaultEquality()}.
 *
 * <p>See <a href="{@docRoot}/overview-summary.html#collection-mutability">{@code Collection}
 * mutability matrix</a> for methods which are supported by {@code ObjCollections} with the specific
 * mutability. // if JDK8 jdk //All methods defined in this interface directly are supported
 * by {@code ObjCollections} with any mutability profile.// elif !(JDK8 jdk) //Among methods,
 * defined in this interface directly, {@link #removeIf(Predicate)} is supported only by collections
 * of <i>mutable</i> profile, others are supported by {@code ObjCollections} with any mutability
 * profile.// endif //
 *
 * @param <E> the type of elements in this collection
 */
public interface ObjCollection<E> extends Collection<E>, Container {

    /**
     * Returns the equivalence strategy for elements in this collection. All methods
     * in {@link Collection} interface which defined in terms of {@link Object#equals(Object)}
     * equality of elements, for example, {@link #contains(Object)} and {@link #remove(Object)},
     * are supposed to use this equivalence instead.
     *
     * @return the equivalence strategy for elements in this collection
     */
    @Nonnull
    Equivalence<E> equivalence();

    /* if !(JDK8 jdk) */
    /**
     * Performs the given action for each element of this collection until all elements have been
     * processed or the action throws an exception.  Unless otherwise specified by the implementing
     * class, actions are performed in the order of iteration (if an iteration order is specified).
     * Exceptions thrown by the action are relayed to the caller.
     *
     * @param action the action to be performed for each element
     * @see <a href="{@docRoot}/overview-summary.html#iteration">
     *     Comparison of iteration options in the library</a>
     */
    void forEach(@Nonnull Consumer<? super E> action);
    /* endif */

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
     * @see <a href="{@docRoot}/overview-summary.html#iteration">
     *     Comparison of iteration options in the library</a>
     */
    boolean forEachWhile(@Nonnull Predicate<? super E> predicate);

    /**
     * Returns a new cursor over this collection's elements. Cursor iteration order is always
     * corresponds to the {@linkplain #iterator() iterator}'s order.
     *
     * @return a new cursor over this collection's elements
     * @see <a href="{@docRoot}/overview-summary.html#iteration">
     *     Comparison of iteration options in the library</a>
     */
    @Nonnull
    ObjCursor<E> cursor();

    /**
     * Returns a new iterator over this collection's elements.
     *
     * @return a new iterator over this collection's elements
     * @see <a href="{@docRoot}/overview-summary.html#iteration">
     *     Comparison of iteration options in the library</a>
     */
    @Override
    @Nonnull
    ObjIterator<E> iterator();

    /* if !(JDK8 jdk) */
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
     * @see <a href="{@docRoot}/overview-summary.html#iteration">
     *     Comparison of iteration options in the library</a>
     */
    boolean removeIf(@Nonnull Predicate<? super E> filter);
    /* endif */
}
