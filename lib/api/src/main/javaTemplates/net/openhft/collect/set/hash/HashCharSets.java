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

package net.openhft.collect.set.hash;

import net.openhft.function.*;

import java.util.Iterator;
import java.util.ServiceLoader;


public final class HashCharSets {
    private static final ServiceLoader<HashCharSetFactory> LOADER =
            ServiceLoader.load(HashCharSetFactory.class);
    private static HashCharSetFactory/* if obj elem //<Object>// endif */ defaultFactory = null;

    public static HashCharSetFactory/* if obj elem //<Object>// endif */ getDefaultFactory() {
        if (defaultFactory != null) {
            return defaultFactory;
        } else {
            // synchronization?
            return defaultFactory = LOADER.iterator().next();
        }
    }
    
    /* define ep */
    /* if obj elem //<? extends E>// elif !(obj elem) //<Character>// endif */
    /* enddefine */

    public static /*<>*/ HashCharSet/*<>*/ newMutableSet() {
        return getDefaultFactory().newMutableSet();
    }

    public static /*<>*/ HashCharSet/*<>*/ newMutableSet(int expectedSize) {
        return getDefaultFactory().newMutableSet(expectedSize);
    }


    /* with Mutable|Immutable mutability */

    /* with with|without expectedSize */
    /* define arg *//* if with expectedSize //, int expectedSize// endif *//* enddefine */
    /* define apply *//* if with expectedSize //, expectedSize// endif *//* enddefine */

    /* if with expectedSize *//**
     * If the specified elements is a set// if obj elem //
     * and has the same equivalence with this factory// endif //,
     * {@code expectedSize} is ignored.
     *//* endif*/
    public static /*<>*/ HashCharSet/*<>*/ newMutableSet(
            Iterable/*ep*/<Character>/**/ elements/*arg*/) {
        return getDefaultFactory().newMutableSet(elements/*apply*/);
    }

    public static /*<>*/ HashCharSet/*<>*/ newMutableSet(Iterable/*ep*/<Character>/**/ elems1,
            Iterable/*ep*/<Character>/**/ elems2/*arg*/) {
        return getDefaultFactory().newMutableSet(elems1, elems2/*apply*/);
    }

    public static /*<>*/ HashCharSet/*<>*/ newMutableSet(Iterable/*ep*/<Character>/**/ elems1,
            Iterable/*ep*/<Character>/**/ elems2, Iterable/*ep*/<Character>/**/ elems3/*arg*/) {
        return getDefaultFactory().newMutableSet(elems1, elems2, elems3/*apply*/);
    }

    public static /*<>*/ HashCharSet/*<>*/ newMutableSet(Iterable/*ep*/<Character>/**/ elems1,
            Iterable/*ep*/<Character>/**/ elems2, Iterable/*ep*/<Character>/**/ elems3,
            Iterable/*ep*/<Character>/**/ elems4/*arg*/) {
        return getDefaultFactory().newMutableSet(elems1, elems2, elems3, elems4/*apply*/);
    }

    public static /*<>*/ HashCharSet/*<>*/ newMutableSet(Iterable/*ep*/<Character>/**/ elems1,
            Iterable/*ep*/<Character>/**/ elems2, Iterable/*ep*/<Character>/**/ elems3,
            Iterable/*ep*/<Character>/**/ elems4, Iterable/*ep*/<Character>/**/ elems5/*arg*/) {
        return getDefaultFactory().newMutableSet(elems1, elems2, elems3, elems4, elems5/*apply*/);
    }

    /* endwith */

    public static /*<>*/ HashCharSet/*<>*/ newMutableSet(Iterator/*ep*/<Character>/**/ elements) {
        return getDefaultFactory().newMutableSet(elements);
    }

    public static /*<>*/ HashCharSet/*<>*/ newMutableSet(Iterator/*ep*/<Character>/**/ elements,
            int expectedSize) {
        return getDefaultFactory().newMutableSet(elements, expectedSize);
    }

    public static /*<>*/ HashCharSet/*<>*/ newMutableSet(
            Consumer</*f*/CharConsumer/*<>*/> elementsSupplier) {
        return getDefaultFactory().newMutableSet(elementsSupplier);
    }

    public static /*<>*/ HashCharSet/*<>*/ newMutableSet(
            Consumer</*f*/CharConsumer/*<>*/> elementsSupplier, int expectedSize) {
        return getDefaultFactory().newMutableSet(elementsSupplier, expectedSize);
    }

    public static /*<>*/ HashCharSet/*<>*/ newMutableSet(char[] elements) {
        return getDefaultFactory().newMutableSet(elements);
    }

    public static /*<>*/ HashCharSet/*<>*/ newMutableSet(char[] elements,
            int expectedSize) {
        return getDefaultFactory().newMutableSet(elements, expectedSize);
    }
    
    /* if !(obj elem) */
    public static HashCharSet newMutableSet(Character[] elements) {
        return getDefaultFactory().newMutableSet(elements);
    }

    public static HashCharSet newMutableSet(Character[] elements, int expectedSize) {
        return getDefaultFactory().newMutableSet(elements, expectedSize);
    }
    /* endif */

    public static /*<>*/ HashCharSet/*<>*/ newMutableSetOf(char e1) {
        return getDefaultFactory().newMutableSetOf(e1);
    }

    public static /*<>*/ HashCharSet/*<>*/ newMutableSetOf(char e1, char e2) {
        return getDefaultFactory().newMutableSetOf(e1, e2);
    }

    public static /*<>*/ HashCharSet/*<>*/ newMutableSetOf(char e1, char e2, char e3) {
        return getDefaultFactory().newMutableSetOf(e1, e2, e3);
    }

    public static /*<>*/ HashCharSet/*<>*/ newMutableSetOf(char e1, char e2, char e3, char e4) {
        return getDefaultFactory().newMutableSetOf(e1, e2, e3, e4);
    }

    public static /*<>*/ HashCharSet/*<>*/ newMutableSetOf(char e1, char e2, char e3, char e4,
            char e5, char... restElements) {
        return getDefaultFactory().newMutableSetOf(e1, e2, e3, e4, e5, restElements);
    }
    /* endwith */

    private HashCharSets() {}
}
