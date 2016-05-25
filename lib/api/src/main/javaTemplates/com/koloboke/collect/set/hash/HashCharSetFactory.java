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

import com.koloboke.collect.*;
import com.koloboke.collect.hash.*;
import com.koloboke.compile.CustomKeyEquivalence;
import com.koloboke.compile.KolobokeSet;
import com.koloboke.function.Consumer;
import com.koloboke.collect.set.CharSetFactory;

import javax.annotation.Nonnull;

import java.util.Iterator;


/**
 * An immutable factory of {@code HashCharSet}s.
 *
 * @see HashCharSet
 * @see HashCharSets#getDefaultFactory()
 * @see KolobokeSet @KolobokeSet
 */
public interface HashCharSetFactory/*<>*/
        extends CharSetFactory</* if obj elem //E, // endif */HashCharSetFactory/*<>*/>
        /* if !(float|double elem) */, CharHashFactory<HashCharSetFactory/*<>*/>
        /* elif float|double elem */, HashContainerFactory<HashCharSetFactory/*<>*/>/* endif */ {

    /* if obj elem */
    /**
     * {@inheritDoc} Defaults to {@link Equivalence#defaultEquality()}.
     */
    @Override
    @Nonnull
    Equivalence<Character> getEquivalence();
    /* endif */

    /* if obj elem */
    /**
     * Returns a copy of this factory, with exception that it constructs sets with
     * {@linkplain ObjCollection#equivalence() element equivalence} set to the given
     * {@code Equivalence}.
     *
     * <p>The Koloboke Compile's counterpart of this configuration is the {@link
     * CustomKeyEquivalence @CustomKeyEquivalence} annotation.
     *
     * @param equivalence the new element equivalence
     * @return a copy of this factory, which constructs sets with the given {@code keyEquivalence}
     */
    @Nonnull
    HashCharSetFactory<Character> withEquivalence(
            @Nonnull Equivalence<? super Character> equivalence);
    /* endif */

    /* define p1 *//* if obj elem //<E2 extends E>// endif *//* enddefine */

    /* define p2 *//* if obj elem //<E2>// endif *//* enddefine */

    /* define ep */
    /* if obj elem //<? extends E2>// elif !(obj elem) //<Character>// endif */
    /* enddefine */

    /* with Mutable|Updatable|Immutable mutability */
    /* if !(Immutable mutability) */
    @Override
    @Nonnull
    /*p1*/ HashCharSet/*p2*/ newMutableSet();

    @Override
    @Nonnull
    /*p1*/ HashCharSet/*p2*/ newMutableSet(int expectedSize);
    /* endif */

    /* with with|without expectedSize */
    /* define arg *//* if with expectedSize //, int expectedSize// endif *//* enddefine */

    @Override
    @Nonnull
    /*p1*/ HashCharSet/*p2*/ newMutableSet(@Nonnull Iterable/*ep*/<Character>/**/ elements/*arg*/);

    @Override
    @Nonnull
    /*p1*/ HashCharSet/*p2*/ newMutableSet(@Nonnull Iterable/*ep*/<Character>/**/ elems1,
            @Nonnull Iterable/*ep*/<Character>/**/ elems2/*arg*/);

    @Override
    @Nonnull
    /*p1*/ HashCharSet/*p2*/ newMutableSet(@Nonnull Iterable/*ep*/<Character>/**/ elems1,
            @Nonnull Iterable/*ep*/<Character>/**/ elems2,
            @Nonnull Iterable/*ep*/<Character>/**/ elems3/*arg*/);

    @Override
    @Nonnull
    /*p1*/ HashCharSet/*p2*/ newMutableSet(@Nonnull Iterable/*ep*/<Character>/**/ elems1,
            @Nonnull Iterable/*ep*/<Character>/**/ elems2,
            @Nonnull Iterable/*ep*/<Character>/**/ elems3,
            @Nonnull Iterable/*ep*/<Character>/**/ elems4/*arg*/);

    @Override
    @Nonnull
    /*p1*/ HashCharSet/*p2*/ newMutableSet(@Nonnull Iterable/*ep*/<Character>/**/ elems1,
            @Nonnull Iterable/*ep*/<Character>/**/ elems2,
            @Nonnull Iterable/*ep*/<Character>/**/ elems3,
            @Nonnull Iterable/*ep*/<Character>/**/ elems4,
            @Nonnull Iterable/*ep*/<Character>/**/ elems5/*arg*/);

    @Override
    @Nonnull
    /*p1*/ HashCharSet/*p2*/ newMutableSet(@Nonnull Iterator/*ep*/<Character>/**/ elements/*arg*/);

    @Override
    @Nonnull
    /*p1*/ HashCharSet/*p2*/ newMutableSet(@Nonnull
            Consumer<com.koloboke.function./*f*/CharConsumer/*p2*/> elementsSupplier
            /*arg*/);

    /* define pe *//* if !(obj elem) //char// elif obj elem //E2// endif *//* enddefine */

    @Override
    @Nonnull
    /*p1*/ HashCharSet/*p2*/ newMutableSet(@Nonnull /*pe*/char/**/[] elements/*arg*/);

    /* if !(obj elem) */
    @Override
    @Nonnull
    /*p1*/ HashCharSet/*p2*/ newMutableSet(@Nonnull Character[] elements/*arg*/);
    /* endif */

    /* endwith */

    @Override
    @Nonnull
    /*p1*/ HashCharSet/*p2*/ newMutableSetOf(/*pe*/char/**/ e1);

    @Override
    @Nonnull
    /*p1*/ HashCharSet/*p2*/ newMutableSetOf(/*pe*/char/**/ e1, /*pe*/char/**/ e2);

    @Override
    @Nonnull
    /*p1*/ HashCharSet/*p2*/ newMutableSetOf(/*pe*/char/**/ e1, /*pe*/char/**/ e2,
            /*pe*/char/**/ e3);

    @Override
    @Nonnull
    /*p1*/ HashCharSet/*p2*/ newMutableSetOf(/*pe*/char/**/ e1, /*pe*/char/**/ e2,
            /*pe*/char/**/ e3, /*pe*/char/**/ e4);

    @Override
    @Nonnull
    /*p1*/ HashCharSet/*p2*/ newMutableSetOf(/*pe*/char/**/ e1, /*pe*/char/**/ e2,
            /*pe*/char/**/ e3, /*pe*/char/**/ e4, /*pe*/char/**/ e5,
            /*pe*/char/**/... restElements);
    /* endwith */
}
