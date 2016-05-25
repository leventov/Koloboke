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

package com.koloboke.collect.set.hash;

import com.koloboke.collect.set.CharSet;
import com.koloboke.compile.KolobokeSet;
import com.koloboke.function.Consumer;

import javax.annotation.Nonnull;
import java.util.*;


/**
 * This class consists only of static factory methods to construct {@code HashCharSet}s, and
 * the default {@link HashCharSetFactory} static provider ({@link #getDefaultFactory()}).
 *
 * @see HashCharSet
 * @see KolobokeSet @KolobokeSet
 */
public final class HashCharSets {

    private static class DefaultFactoryHolder {
        private static final HashCharSetFactory defaultFactory =
                ServiceLoader.load(HashCharSetFactory.class).iterator().next();
    }

    /**
     * Returns the default implementation of {@link HashCharSetFactory}, to which all static methods
     * in this class delegate.
     *
     * @return the default implementation of {@link HashCharSetFactory}
    // if obj elem // * @param <E> the most general element type of the sets that could
                               constructed by the returned factory // endif //
     * @throws RuntimeException if no implementations of {@link HashCharSetFactory} are provided
     */
    @Nonnull
    public static /*<>*/ HashCharSetFactory/*<>*/ getDefaultFactory() {
        return (HashCharSetFactory/*<>*/) DefaultFactoryHolder.defaultFactory;
    }
    
    /* define ep */
    /* if obj elem //<? extends E>// elif !(obj elem) //<Character>// endif */
    /* enddefine */

    /* define typeParam //
     // if obj elem // * @param <E> the element type of the returned set // endif //
    // enddefine */

    /* with Mutable|Updatable|Immutable mutability */

    /* if !(Immutable mutability) */
    /**
     * Constructs a new empty mutable set of the default expected size.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashCharSetFactory#newMutableSet() newMutableSet()}.
     *
     // typeParam //
     * @return a new empty mutable set
     */
    @Nonnull
    public static /*<>*/ HashCharSet/*<>*/ newMutableSet() {
        return getDefaultFactory()./*<>*/newMutableSet();
    }

    /**
     * Constructs a new empty mutable set of the given expected size.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashCharSetFactory#newMutableSet(int) newMutableSet(expectedSize)}.
     *
     * @param expectedSize the expected size of the returned set
    // typeParam //
     * @return a new empty mutable set of the given expected size
     */
    @Nonnull
    public static /*<>*/ HashCharSet/*<>*/ newMutableSet(int expectedSize) {
        return getDefaultFactory()./*<>*/newMutableSet(expectedSize);
    }
    /* endif*/

    /* with with|without expectedSize */
    /* define arg *//* if with expectedSize //, int expectedSize// endif *//* enddefine */
    /* define apply *//* if with expectedSize //, expectedSize// endif *//* enddefine */
    /* define lk *//* if with expectedSize //, int// endif *//* enddefine */

    /* define expectedParam //
    * // if with expectedSize //@param expectedSize the expected size of the returned set// endif //
    // enddefine */

    /**
     * Constructs a new mutable set containing the elements in the specified iterable.
     *
     * // if with expectedSize //
     * <p>If the specified iterable is // if !(obj elem) // a {@link java.util.Set}
     * // elif obj elem // an instance of {@code CharSet} and has the same {@linkplain
     * CharSet#equivalence() equivalence} with this factory (and thus the constructed set),
     * // endif // the {@code expectedSize} argument is ignored.
     * // endif //
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashCharSetFactory#newMutableSet(
     * Iterable//lk//) newMutableSet(elements//apply//)}.
     *
     * @param elements the iterable whose elements are to be placed into the returned set
    // expectedParam //
    // typeParam //
     * @return a new mutable set of the elements of the specified iterable
     */
    @Nonnull
    public static /*<>*/ HashCharSet/*<>*/ newMutableSet(
            @Nonnull Iterable/*ep*/<Character>/**/ elements/*arg*/) {
        return getDefaultFactory()./*<>*/newMutableSet(elements/*apply*/);
    }

    /**
     * Constructs a new mutable set which merge the elements of the specified iterables.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashCharSetFactory#newMutableSet(
     * Iterable, Iterable//lk//) newMutableSet(elems1, elems2//apply//)}.
     *
     * @param elems1 the first source of elements for the returned set
     * @param elems2 the second source of elements for the returned set
    // expectedParam //
    // typeParam //
     * @return a new mutable set which merge the elements of the specified iterables
     */
    @Nonnull
    public static /*<>*/ HashCharSet/*<>*/ newMutableSet(
            @Nonnull Iterable/*ep*/<Character>/**/ elems1,
            @Nonnull Iterable/*ep*/<Character>/**/ elems2/*arg*/) {
        return getDefaultFactory()./*<>*/newMutableSet(elems1, elems2/*apply*/);
    }

    /**
     * Constructs a new mutable set which merge the elements of the specified iterables.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashCharSetFactory#newMutableSet(
     * Iterable, Iterable, Iterable//lk//) newMutableSet(elems1, elems2, elems3//apply//)}.
     *
     * @param elems1 the first source of elements for the returned set
     * @param elems2 the second source of elements for the returned set
     * @param elems3 the third source of elements for the returned set
    // expectedParam //
    // typeParam //
     * @return a new mutable set which merge the elements of the specified iterables
     */
    @Nonnull
    public static /*<>*/ HashCharSet/*<>*/ newMutableSet(
            @Nonnull Iterable/*ep*/<Character>/**/ elems1,
            @Nonnull Iterable/*ep*/<Character>/**/ elems2,
            @Nonnull Iterable/*ep*/<Character>/**/ elems3/*arg*/) {
        return getDefaultFactory()./*<>*/newMutableSet(elems1, elems2, elems3/*apply*/);
    }

    /**
     * Constructs a new mutable set which merge the elements of the specified iterables.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashCharSetFactory#newMutableSet(Iterable,
     * Iterable, Iterable, Iterable//lk//) newMutableSet(elems1, elems2, elems3, elems4//apply//)}.
     *
     * @param elems1 the first source of elements for the returned set
     * @param elems2 the second source of elements for the returned set
     * @param elems3 the third source of elements for the returned set
     * @param elems4 the fourth source of elements for the returned set
    // expectedParam //
    // typeParam //
     * @return a new mutable set which merge the elements of the specified iterables
     */
    @Nonnull
    public static /*<>*/ HashCharSet/*<>*/ newMutableSet(
            @Nonnull Iterable/*ep*/<Character>/**/ elems1,
            @Nonnull Iterable/*ep*/<Character>/**/ elems2,
            @Nonnull Iterable/*ep*/<Character>/**/ elems3,
            @Nonnull Iterable/*ep*/<Character>/**/ elems4/*arg*/) {
        return getDefaultFactory()./*<>*/newMutableSet(elems1, elems2, elems3, elems4/*apply*/);
    }

    /**
     * Constructs a new mutable set which merge the elements of the specified iterables.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashCharSetFactory#newMutableSet(Iterable, Iterable, Iterable,
     * Iterable, Iterable//lk//) newMutableSet(elems1, elems2, elems3, elems4, elems5//apply//)}.
     *
     * @param elems1 the first source of elements for the returned set
     * @param elems2 the second source of elements for the returned set
     * @param elems3 the third source of elements for the returned set
     * @param elems4 the fourth source of elements for the returned set
     * @param elems5 the fifth source of elements for the returned set
    // expectedParam //
    // typeParam //
     * @return a new mutable set which merge the elements of the specified iterables
     */
    @Nonnull
    public static /*<>*/ HashCharSet/*<>*/ newMutableSet(
            @Nonnull Iterable/*ep*/<Character>/**/ elems1,
            @Nonnull Iterable/*ep*/<Character>/**/ elems2,
            @Nonnull Iterable/*ep*/<Character>/**/ elems3,
            @Nonnull Iterable/*ep*/<Character>/**/ elems4,
            @Nonnull Iterable/*ep*/<Character>/**/ elems5/*arg*/) {
        return getDefaultFactory()./*<>*/newMutableSet(elems1, elems2, elems3, elems4, elems5/*apply*/);
    }

    /**
     * Constructs a new mutable set containing the elements traversed by the specified iterator.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashCharSetFactory#newMutableSet(
     * Iterator//lk//) newMutableSet(elements//apply//)}.
     *
     * @param elements the iterator from which elements are to be placed into the returned set
    // expectedParam //
    // typeParam //
     * @return a new mutable set containing the elements traversed by the specified iterator
     */
    @Nonnull
    public static /*<>*/ HashCharSet/*<>*/ newMutableSet(
            @Nonnull Iterator/*ep*/<Character>/**/ elements/*arg*/) {
        return getDefaultFactory()./*<>*/newMutableSet(elements/*apply*/);
    }

    /**
     * Constructs a new mutable set of elements consumed by the callback within the given closure.
     *
     * <p>Example: TODO
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashCharSetFactory#newMutableSet(
     * Consumer//lk//) newMutableSet(elementsSupplier//apply//)}.
     *
     * @param elementsSupplier the function which supply mappings for the returned set via
     *        the callback passed in
    // expectedParam //
    // typeParam //
     * @return a new mutable set of elements consumed by the callback within the given closure
     */
    @Nonnull
    public static /*<>*/ HashCharSet/*<>*/ newMutableSet(@Nonnull
            Consumer<com.koloboke.function./*f*/CharConsumer/*<>*/> elementsSupplier
            /*arg*/) {
        return getDefaultFactory()./*<>*/newMutableSet(elementsSupplier/*apply*/);
    }

    /**
     * Constructs a new mutable set of elements from the given array.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashCharSetFactory#newMutableSet(
     * //raw//char[]//lk//) newMutableSet(elements//apply//)}.
     *
     * @param elements the array whose elements are to be placed into the returned set
    // expectedParam //
    // typeParam //
     * @return a new mutable set of elements from the given array
     */
    @Nonnull
    public static /*<>*/ HashCharSet/*<>*/ newMutableSet(@Nonnull char[] elements/*arg*/) {
        return getDefaultFactory()./*<>*/newMutableSet(elements/*apply*/);
    }
    
    /* if !(obj elem) */
    /**
     * Constructs a new mutable set of elements from the given array.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashCharSetFactory#newMutableSet(
     * Character[]//lk//) newMutableSet(elements//apply//)}.
     *
     * @param elements the array whose elements are to be placed into the returned set
    // expectedParam //
    // typeParam //
     * @return a new mutable set of elements from the given array
     * @throws NullPointerException if {@code elements} array contain {@code null} elements
     */
    @Nonnull
    public static HashCharSet newMutableSet(@Nonnull Character[] elements/*arg*/) {
        return getDefaultFactory()./*<>*/newMutableSet(elements/*apply*/);
    }
    /* endif */

    /* endwith */

    /**
     * Constructs a new mutable singleton set of the given element.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashCharSetFactory#newMutableSetOf(
     * //raw//char) newMutableSetOf(e1)}.
     *
     * @param e1 the sole element
    // typeParam //
     * @return a new mutable singleton set of the given element
     */
    @Nonnull
    public static /*<>*/ HashCharSet/*<>*/ newMutableSetOf(char e1) {
        return getDefaultFactory()./*<>*/newMutableSetOf(e1);
    }

    /**
     * Constructs a new mutable set of the two specified elements.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashCharSetFactory#newMutableSetOf(
     * //raw//char, //raw//char) newMutableSetOf(e1, e2)}.
     *
     * @param e1 the first element
     * @param e2 the second element
    // typeParam //
     * @return a new mutable set of the two specified elements
     */
    @Nonnull
    public static /*<>*/ HashCharSet/*<>*/ newMutableSetOf(char e1, char e2) {
        return getDefaultFactory()./*<>*/newMutableSetOf(e1, e2);
    }

    /**
     * Constructs a new mutable set of the three specified elements.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashCharSetFactory#newMutableSetOf(
     * //raw//char, //raw//char, //raw//char) newMutableSetOf(e1, e2, e3)}.
     *
     * @param e1 the first element
     * @param e2 the second element
     * @param e3 the third element
    // typeParam //
     * @return a new mutable set of the three specified elements
     */
    @Nonnull
    public static /*<>*/ HashCharSet/*<>*/ newMutableSetOf(char e1, char e2, char e3) {
        return getDefaultFactory()./*<>*/newMutableSetOf(e1, e2, e3);
    }

    /**
     * Constructs a new mutable set of the four specified elements.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashCharSetFactory#newMutableSetOf(
     * //raw//char, //raw//char, //raw//char, //raw//char) newMutableSetOf(e1, e2, e3, e4)}.
     *
     * @param e1 the first element
     * @param e2 the second element
     * @param e3 the third element
     * @param e4 the fourth element
    // typeParam //
     * @return a new mutable set of the four specified elements
     */
    @Nonnull
    public static /*<>*/ HashCharSet/*<>*/ newMutableSetOf(char e1, char e2, char e3, char e4) {
        return getDefaultFactory()./*<>*/newMutableSetOf(e1, e2, e3, e4);
    }

    /**
     * Constructs a new mutable set of the specified elements.
     *
     * <p>This method simply delegates to {@link #getDefaultFactory()
     * }<tt>.</tt>{@link HashCharSetFactory#newMutableSetOf(//raw//char, //raw//char, //raw//char,
     * //raw//char, //raw//char, //raw//char...) newMutableSetOf(e1, e2, e3, e4, e5, restElements)}.
     *
     * @param e1 the first element
     * @param e2 the second element
     * @param e3 the third element
     * @param e4 the fourth element
     * @param e5 the fifth element
     * @param restElements the rest elements to be placed into the set
    // typeParam //
     * @return a new mutable set of the specified elements
     */
    @Nonnull
    public static /*<>*/ HashCharSet/*<>*/ newMutableSetOf(char e1, char e2, char e3, char e4,
            char e5, char... restElements) {
        return getDefaultFactory()./*<>*/newMutableSetOf(e1, e2, e3, e4, e5, restElements);
    }
    /* endwith */

    private HashCharSets() {}
}
