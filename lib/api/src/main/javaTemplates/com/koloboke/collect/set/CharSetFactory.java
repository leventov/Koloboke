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

package com.koloboke.collect.set;

import com.koloboke.collect.ContainerFactory;
import com.koloboke.collect.Equivalence;
import com.koloboke.compile.KolobokeSet;
import com.koloboke.function.Consumer;

import javax.annotation.Nonnull;

import java.util.Iterator;


/**
 * An immutable factory of {@code CharSet}s.
 *
 * // if obj elem // @param <E> the most general element type of the sets that could be constructed
 *                          by this factory // endif //
 * @param <F> the concrete factory type which extends this interface
 * @see CharSet
 * @see KolobokeSet @KolobokeSet
 */
public interface CharSetFactory</* if obj elem //E, // endif */
        F extends CharSetFactory</* if obj elem //E, // endif */F>> extends ContainerFactory<F> {

    /* if obj elem */
    /**
     * Returns the equivalence to which {@linkplain CharSet#equivalence() equivalence} of the sets
     * constructed by this factory is set.
     *
     * @return the key equivalence of the maps constructed by this factory
     */
    @Nonnull
    Equivalence<Character> getEquivalence();
    /* endif */

    /* define p1 *//* if obj elem //<E2 extends E>// endif *//* enddefine */

    /* define p2 *//* if obj elem //<E2>// endif *//* enddefine */

    /* define ep */
    /* if obj elem //<? extends E2>// elif !(obj elem) //<Character>// endif */
    /* enddefine */

    /* define typeParam //
     // if obj elem // * @param <E2> the element type of the returned set // endif //
    // enddefine */

    /* with Mutable|Updatable|Immutable mutability */
    /* if !(Immutable mutability) */

    /**
     * Constructs a new empty mutable set of the {@linkplain #getDefaultExpectedSize() default
     * expected size}.
     *
     // typeParam //
     * @return a new empty mutable set
     */
    @Nonnull
    /*p1*/ CharSet/*p2*/ newMutableSet();

    /**
     * Constructs a new empty mutable set of the given expected size.
     *
     * @param expectedSize the expected size of the returned set
    // typeParam //
     * @return a new empty mutable set of the given expected size
     */
    @Nonnull
    /*p1*/ CharSet/*p2*/ newMutableSet(int expectedSize);
    /* endif */

    /* with with|without expectedSize */
    /* define arg *//* if with expectedSize //, int expectedSize// endif *//* enddefine */

    /* define expectedParam //
    * // if with expectedSize //@param expectedSize the expected size of the returned set// endif //
    // enddefine */

    /**
     * Constructs a new mutable set containing the elements in the specified iterable.
     *
     * // if with expectedSize //
     * <p>If the specified iterable is // if !(obj elem) // a {@link java.util.Set}
     * // elif obj elem // an instance of {@code CharSet} and has the same {@linkplain
     * ObjSet#equivalence() equivalence} with this factory (and thus the constructed set),
     * // endif // the {@code expectedSize} argument is ignored.
     * // endif //
     *
     * @param elements the iterable whose elements are to be placed into the returned set
    // expectedParam //
    // typeParam //
     * @return a new mutable set of the elements of the specified iterable
     */
    @Nonnull
    /*p1*/ CharSet/*p2*/ newMutableSet(@Nonnull Iterable/*ep*/<Character>/**/ elements/*arg*/);

    /**
     * Constructs a new mutable set which merge the elements of the specified iterables.
     *
     * @param elems1 the first source of elements for the returned set
     * @param elems2 the second source of elements for the returned set
    // expectedParam //
    // typeParam //
     * @return a new mutable set which merge the elements of the specified iterables
     */
    @Nonnull
    /*p1*/ CharSet/*p2*/ newMutableSet(@Nonnull Iterable/*ep*/<Character>/**/ elems1,
            @Nonnull Iterable/*ep*/<Character>/**/ elems2/*arg*/);

    /**
     * Constructs a new mutable set which merge the elements of the specified iterables.
     *
     * @param elems1 the first source of elements for the returned set
     * @param elems2 the second source of elements for the returned set
     * @param elems3 the third source of elements for the returned set
    // expectedParam //
    // typeParam //
     * @return a new mutable set which merge the elements of the specified iterables
     */
    @Nonnull
    /*p1*/ CharSet/*p2*/ newMutableSet(@Nonnull Iterable/*ep*/<Character>/**/ elems1,
            @Nonnull Iterable/*ep*/<Character>/**/ elems2,
            @Nonnull Iterable/*ep*/<Character>/**/ elems3/*arg*/);

    /**
     * Constructs a new mutable set which merge the elements of the specified iterables.
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
    /*p1*/ CharSet/*p2*/ newMutableSet(@Nonnull Iterable/*ep*/<Character>/**/ elems1,
            @Nonnull Iterable/*ep*/<Character>/**/ elems2,
            @Nonnull Iterable/*ep*/<Character>/**/ elems3,
            @Nonnull Iterable/*ep*/<Character>/**/ elems4/*arg*/);

    /**
     * Constructs a new mutable set which merge the elements of the specified iterables.
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
    /*p1*/ CharSet/*p2*/ newMutableSet(@Nonnull Iterable/*ep*/<Character>/**/ elems1,
            @Nonnull Iterable/*ep*/<Character>/**/ elems2,
            @Nonnull Iterable/*ep*/<Character>/**/ elems3,
            @Nonnull Iterable/*ep*/<Character>/**/ elems4,
            @Nonnull Iterable/*ep*/<Character>/**/ elems5/*arg*/);

    /**
     * Constructs a new mutable set containing the elements traversed by the specified iterator.
     *
     * @param elements the iterator from which elements are to be placed into the returned set
    // expectedParam //
    // typeParam //
     * @return a new mutable set containing the elements traversed by the specified iterator
     */
    @Nonnull
    /*p1*/ CharSet/*p2*/ newMutableSet(@Nonnull Iterator/*ep*/<Character>/**/ elements/*arg*/);

    /**
     * Constructs a new mutable set of elements consumed by the callback within the given closure.
     *
     * <p>Example: TODO
     *
     * @param elementsSupplier the function which supply mappings for the returned set via
     *        the callback passed in
    // expectedParam //
    // typeParam //
     * @return a new mutable set of elements consumed by the callback within the given closure
     */
    @Nonnull
    /*p1*/ CharSet/*p2*/ newMutableSet(@Nonnull
            Consumer<com.koloboke.function./*f*/CharConsumer/*p2*/> elementsSupplier
            /*arg*/);

    /* define pe *//* if !(obj elem) //char// elif obj elem //E2// endif *//* enddefine */

    /**
     * Constructs a new mutable set of elements from the given array.
     *
     * @param elements the array whose elements are to be placed into the returned set
    // expectedParam //
    // typeParam //
     * @return a new mutable set of elements from the given array
     */
    @Nonnull
    /*p1*/ CharSet/*p2*/ newMutableSet(@Nonnull /*pe*/char/**/[] elements/*arg*/);

    /* if !(obj elem) */
    /**
     * Constructs a new mutable set of elements from the given array.
     *
     * @param elements the array whose elements are to be placed into the returned set
    // expectedParam //
    // typeParam //
     * @return a new mutable set of elements from the given array
     * @throws NullPointerException if {@code elements} array contain {@code null} elements
     */
    @Nonnull
    /*p1*/ CharSet/*p2*/ newMutableSet(@Nonnull Character[] elements/*arg*/);
    /* endif */

    /* endwith */

    /**
     * Constructs a new mutable singleton set of the given element.
     *
     * @param e1 the sole element
    // typeParam //
     * @return a new mutable singleton set of the given element
     */
    @Nonnull
    /*p1*/ CharSet/*p2*/ newMutableSetOf(/*pe*/char/**/ e1);

    /**
     * Constructs a new mutable set of the two specified elements.
     *
     * @param e1 the first element
     * @param e2 the second element
    // typeParam //
     * @return a new mutable set of the two specified elements
     */
    @Nonnull
    /*p1*/ CharSet/*p2*/ newMutableSetOf(/*pe*/char/**/ e1, /*pe*/char/**/ e2);

    /**
     * Constructs a new mutable set of the three specified elements.
     *
     * @param e1 the first element
     * @param e2 the second element
     * @param e3 the third element
    // typeParam //
     * @return a new mutable set of the three specified elements
     */
    @Nonnull
    /*p1*/ CharSet/*p2*/ newMutableSetOf(/*pe*/char/**/ e1, /*pe*/char/**/ e2, /*pe*/char/**/ e3);

    /**
     * Constructs a new mutable set of the four specified elements.
     *
     * @param e1 the first element
     * @param e2 the second element
     * @param e3 the third element
     * @param e4 the fourth element
    // typeParam //
     * @return a new mutable set of the four specified elements
     */
    @Nonnull
    /*p1*/ CharSet/*p2*/ newMutableSetOf(/*pe*/char/**/ e1, /*pe*/char/**/ e2, /*pe*/char/**/ e3,
            /*pe*/char/**/ e4);

    /**
     * Constructs a new mutable set of the specified elements.
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
    /*p1*/ CharSet/*p2*/ newMutableSetOf(/*pe*/char/**/ e1, /*pe*/char/**/ e2, /*pe*/char/**/ e3,
            /*pe*/char/**/ e4, /*pe*/char/**/ e5, /*pe*/char/**/... restElements);
    /* endwith */
}
