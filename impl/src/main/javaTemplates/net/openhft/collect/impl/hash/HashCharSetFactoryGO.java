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

package net.openhft.collect.impl.hash;

import net.openhft.collect.*;
import net.openhft.collect.set.hash.HashCharSetFactory;
import net.openhft.function.*;
import net.openhft.collect.set.hash.HashCharSet;

import java.util.Collection;
import java.util.Iterator;


public abstract class HashCharSetFactoryGO/*<>*/ extends HashCharSetFactorySO/*<>*/ {

    public HashCharSetFactoryGO(CharHashConfig conf) {
        super(conf);
    }

    @Override
    public String toString() {
        return "HashCharSetFactory[config=" + getConfig() +
                /* if obj elem */",equivalence=" + getEquivalence() +/* endif */
        "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj instanceof HashCharSetFactory) {
            HashCharSetFactory factory = (HashCharSetFactory) obj;
            return getConfig().equals(factory.getConfig())
                    /* if obj elem */
                    && NullableObjects.equals(getEquivalence(), factory.getEquivalence())
                    /* endif */;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hashCode = 17;
        hashCode = hashCode * 31 + getConfig().hashCode();
        /* if obj elem */
        hashCode = hashCode * 31 + NullableObjects.hashCode(getEquivalence());
        /* endif */
        return hashCode;
    }

    /* define p1 *//* if obj elem // <E2 extends E>// endif *//* enddefine */

    /* define p2 *//* if obj elem //<E2>// endif *//* enddefine */

    /* define ep */
    /* if obj elem //<? extends E2>// elif !(obj elem) //<Character>// endif */
    /* enddefine */

    private/*p1*/ MutableDHashCharSetGO/*p2*/ shrunk(MutableDHashCharSetGO/*p2*/ set) {
        Predicate<HashContainer> shrinkCondition;
        if ((shrinkCondition = hashConf.getShrinkCondition()) != null) {
            if (shrinkCondition.test(set))
                set.shrink();
        }
        return set;
    }

    @Override
    public/*p1*/ MutableDHashCharSetGO/*p2*/ newMutableSet() {
        return newMutableSet(hashConf.getDefaultExpectedSize());
    }

    @Override
    public/*p1*/ MutableDHashCharSetGO/*p2*/ newMutableSet(Iterable/*ep*/<Character>/**/ elements) {
        int expectedSize = elements instanceof Collection ?
                ((Collection) elements).size() :
                hashConf.getDefaultExpectedSize();
        return newMutableSet(elements, expectedSize);
    }

    @Override
    public/*p1*/ MutableDHashCharSetGO/*p2*/ newMutableSet(Iterable/*ep*/<Character>/**/ elements,
            int expectedSize) {
        return shrunk(super.newMutableSet(elements, expectedSize));
    }

    @Override
    public/*p1*/ MutableDHashCharSetGO/*p2*/ newMutableSet(Iterator/*ep*/<Character>/**/ elements) {
        return newMutableSet(elements, hashConf.getDefaultExpectedSize());
    }

    @Override
    public/*p1*/ MutableDHashCharSetGO/*p2*/ newMutableSet(Iterator/*ep*/<Character>/**/ elements,
            int expectedSize) {
        MutableDHashCharSetGO/*p2*/ set = newMutableSet(expectedSize);
        while (elements.hasNext()) {
            set.add(elements.next());
        }
        return shrunk(set);
    }

    @Override
    public/*p1*/ MutableDHashCharSetGO/*p2*/ newMutableSet(
            Consumer</*f*/CharConsumer/*p2*/> elementsSupplier) {
        return newMutableSet(elementsSupplier, hashConf.getDefaultExpectedSize());
    }

    /* define pe *//* if !(obj elem) //char// elif obj elem //E2// endif *//* enddefine */

    @Override
    public/*p1*/ MutableDHashCharSetGO/*p2*/ newMutableSet(
            Consumer</*f*/CharConsumer/*p2*/> elementsSupplier, int expectedSize) {
        final MutableDHashCharSetGO/*p2*/ set = newMutableSet(expectedSize);
        elementsSupplier.accept(new /*f*/CharConsumer/*p2*/() {
            @Override
            public void accept(/*pe*/char/**/ e) {
                set.add(e);
            }
        });
        return shrunk(set);
    }

    @Override
    public/*p1*/ MutableDHashCharSetGO/*p2*/ newMutableSet(/*pe*/char/**/[] elements) {
        return newMutableSet(elements, elements.length);
    }

    @Override
    public/*p1*/ MutableDHashCharSetGO/*p2*/ newMutableSet(/*pe*/char/**/[] elements,
            int expectedSize) {
        final MutableDHashCharSetGO/*p2*/ set = newMutableSet(expectedSize);
        for (/*pe*/char/**/ e : elements) {
            set.add(e);
        }
        return shrunk(set);
    }

    /* if !(obj elem) */
    @Override
    public MutableDHashCharSetGO newMutableSet(Character[] elements) {
        return newMutableSet(elements, elements.length);
    }

    @Override
    public MutableDHashCharSetGO newMutableSet(Character[] elements,
            int expectedSize) {
        final MutableDHashCharSetGO set = newMutableSet(expectedSize);
        for (char e : elements) {
            set.add(e);
        }
        return shrunk(set);
    }
    /* endif */

    @Override
    public/*p1*/ MutableDHashCharSetGO/*p2*/ newMutableSetOf(/*pe*/char/**/ e1) {
        MutableDHashCharSetGO/*p2*/ set = newMutableSet(1);
        set.add(e1);
        return set;
    }

    @Override
    public/*p1*/ MutableDHashCharSetGO/*p2*/ newMutableSetOf(/*pe*/char/**/ e1, /*pe*/char/**/ e2) {
        MutableDHashCharSetGO/*p2*/ set = newMutableSet(2);
        set.add(e1);
        set.add(e2);
        return set;
    }

    @Override
    public/*p1*/ MutableDHashCharSetGO/*p2*/ newMutableSetOf(/*pe*/char/**/ e1, /*pe*/char/**/ e2,
            /*pe*/char/**/ e3) {
        MutableDHashCharSetGO/*p2*/ set = newMutableSet(3);
        set.add(e1);
        set.add(e2);
        set.add(e3);
        return set;
    }

    @Override
    public/*p1*/ MutableDHashCharSetGO/*p2*/ newMutableSetOf(/*pe*/char/**/ e1, /*pe*/char/**/ e2,
            /*pe*/char/**/ e3, /*pe*/char/**/ e4) {
        MutableDHashCharSetGO/*p2*/ set = newMutableSet(4);
        set.add(e1);
        set.add(e2);
        set.add(e3);
        set.add(e4);
        return set;
    }

    @Override
    public/*p1*/ MutableDHashCharSetGO/*p2*/ newMutableSetOf(/*pe*/char/**/ e1, /*pe*/char/**/ e2,
            /*pe*/char/**/ e3, /*pe*/char/**/ e4, /*pe*/char/**/ e5,
            /*pe*/char/**/... restElements) {
        MutableDHashCharSetGO/*p2*/ set = newMutableSet(5 + restElements.length);
        set.add(e1);
        set.add(e2);
        set.add(e3);
        set.add(e4);
        set.add(e5);
        for (/*pe*/char/**/ e : restElements) {
            set.add(e);
        }
        return shrunk(set);
    }

    @Override
    public/*p1*/ HashCharSet/*p2*/ newImmutableSet(Iterable/*ep*/<Character>/**/ elements) {
        ImmutableDHashCharSetGO/*p2*/ set = uninitializedImmutableSet();
        set.move(newMutableSet(elements));
        return set;
    }

    @Override
    public/*p1*/ HashCharSet/*p2*/ newImmutableSet(Iterable/*ep*/<Character>/**/ elements,
            int expectedSize) {
        ImmutableDHashCharSetGO/*p2*/ set = uninitializedImmutableSet();
        set.move(newMutableSet(elements, expectedSize));
        return set;
    }

    @Override
    public/*p1*/ HashCharSet/*p2*/ newImmutableSet(Iterator/*ep*/<Character>/**/ elements) {
        ImmutableDHashCharSetGO/*p2*/ set = uninitializedImmutableSet();
        set.move(newMutableSet(elements));
        return set;
    }

    @Override
    public/*p1*/ HashCharSet/*p2*/ newImmutableSet(Iterator/*ep*/<Character>/**/ elements,
            int expectedSize) {
        ImmutableDHashCharSetGO/*p2*/ set = uninitializedImmutableSet();
        set.move(newMutableSet(elements, expectedSize));
        return set;
    }

    @Override
    public/*p1*/ HashCharSet/*p2*/ newImmutableSet(
            Consumer</*f*/CharConsumer/*p2*/> elementsSupplier) {
        ImmutableDHashCharSetGO/*p2*/ set = uninitializedImmutableSet();
        set.move(newMutableSet(elementsSupplier));
        return set;
    }

    @Override
    public/*p1*/ HashCharSet/*p2*/ newImmutableSet(
            Consumer</*f*/CharConsumer/*p2*/> elementsSupplier, int expectedSize) {
        ImmutableDHashCharSetGO/*p2*/ set = uninitializedImmutableSet();
        set.move(newMutableSet(elementsSupplier, expectedSize));
        return set;
    }

    @Override
    public/*p1*/ HashCharSet/*p2*/ newImmutableSet(/*pe*/char/**/[] elements) {
        ImmutableDHashCharSetGO/*p2*/ set = uninitializedImmutableSet();
        set.move(newMutableSet(elements));
        return set;
    }

    @Override
    public/*p1*/ HashCharSet/*p2*/ newImmutableSet(/*pe*/char/**/[] elements, int expectedSize) {
        ImmutableDHashCharSetGO/*p2*/ set = uninitializedImmutableSet();
        set.move(newMutableSet(elements, expectedSize));
        return set;
    }

    /* if !(obj elem) */
    @Override
    public HashCharSet newImmutableSet(Character[] elements) {
        ImmutableDHashCharSetGO set = uninitializedImmutableSet();
        set.move(newMutableSet(elements));
        return set;
    }

    @Override
    public HashCharSet newImmutableSet(Character[] elements, int expectedSize) {
        ImmutableDHashCharSetGO set = uninitializedImmutableSet();
        set.move(newMutableSet(elements, expectedSize));
        return set;
    }
    /* endif */

    @Override
    public/*p1*/ HashCharSet/*p2*/ newImmutableSetOf(/*pe*/char/**/ e1) {
        ImmutableDHashCharSetGO/*p2*/ set = uninitializedImmutableSet();
        set.move(newMutableSetOf(e1));
        return set;
    }

    @Override
    public/*p1*/ HashCharSet/*p2*/ newImmutableSetOf(/*pe*/char/**/ e1, /*pe*/char/**/ e2) {
        ImmutableDHashCharSetGO/*p2*/ set = uninitializedImmutableSet();
        set.move(newMutableSetOf(e1, e2));
        return set;
    }

    @Override
    public/*p1*/ HashCharSet/*p2*/ newImmutableSetOf(/*pe*/char/**/ e1, /*pe*/char/**/ e2,
            /*pe*/char/**/ e3) {
        ImmutableDHashCharSetGO/*p2*/ set = uninitializedImmutableSet();
        set.move(newMutableSetOf(e1, e2, e3));
        return set;
    }

    @Override
    public/*p1*/ HashCharSet/*p2*/ newImmutableSetOf(/*pe*/char/**/ e1, /*pe*/char/**/ e2,
            /*pe*/char/**/ e3, /*pe*/char/**/ e4) {
        ImmutableDHashCharSetGO/*p2*/ set = uninitializedImmutableSet();
        set.move(newMutableSetOf(e1, e2, e3, e4));
        return set;
    }

    @Override
    public/*p1*/ HashCharSet/*p2*/ newImmutableSetOf(/*pe*/char/**/ e1, /*pe*/char/**/ e2,
            /*pe*/char/**/ e3, /*pe*/char/**/ e4, /*pe*/char/**/ e5,
            /*pe*/char/**/... restElements) {
        ImmutableDHashCharSetGO/*p2*/ set = uninitializedImmutableSet();
        set.move(newMutableSetOf(e1, e2, e3, e4, e5, restElements));
        return set;
    }
}
