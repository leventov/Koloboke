/* with
 DHash|QHash|LHash hash
 byte|char|short|int|long|float|double|obj elem
*/
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

package com.koloboke.collect.impl.hash;

import com.koloboke.collect.hash.*;
import com.koloboke.collect.set.hash.HashByteSetFactory;
import com.koloboke.function.Consumer;
import com.koloboke.function.Predicate;
import com.koloboke.collect.set.hash.HashByteSet;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Iterator;

import static com.koloboke.collect.impl.Containers.sizeAsInt;
import static com.koloboke.collect.impl.hash.LHashCapacities.configIsSuitableForMutableLHash;


public abstract class DHashByteSetFactoryGO/*<>*/ extends DHashByteSetFactorySO/*<>*/ {

    public DHashByteSetFactoryGO(HashConfig hashConf, int defaultExpectedSize
            /* if obj elem */, boolean isNullAllowed
            /* elif !(float|double elem) */, byte lower, byte upper/* endif */) {
        super(hashConf, defaultExpectedSize/* if obj elem //, isNullAllowed
            // elif !(float|double elem) */, lower, upper/* endif */);
    }

    /* define commonArgDef //
    HashConfig hashConf, int defaultExpectedSize// if obj elem //, boolean isNullAllowed
            // elif !(float|double elem) //, byte lower, byte upper// endif //
    // enddefine */

    abstract HashByteSetFactory/*<>*/ thisWith(/* commonArgDef */);

    abstract HashByteSetFactory/*<>*/ lHashLikeThisWith(/* commonArgDef */);

    /* with DHash|QHash hash */
    abstract HashByteSetFactory/*<>*/ dHashLikeThisWith(/* commonArgDef */);
    /* endwith */

    @Override
    public final HashByteSetFactory/*<>*/ withHashConfig(@Nonnull HashConfig hashConf) {
        if (configIsSuitableForMutableLHash(hashConf))
            return lHashLikeThisWith(hashConf, getDefaultExpectedSize()
            /* if obj elem */, isNullKeyAllowed()/* elif !(float|double elem) */
                    , getLowerKeyDomainBound(), getUpperKeyDomainBound()/* endif */);
        /* with DHash|QHash hash */
        return dHashLikeThisWith(hashConf, getDefaultExpectedSize()
            /* if obj elem */, isNullKeyAllowed()/* elif !(float|double elem) */
                , getLowerKeyDomainBound(), getUpperKeyDomainBound()/* endif */);
        /* endwith */
    }

    @Override
    public final HashByteSetFactory/*<>*/ withDefaultExpectedSize(int defaultExpectedSize) {
        if (defaultExpectedSize == getDefaultExpectedSize())
            return this;
        return thisWith(getHashConfig(), defaultExpectedSize
                /* if obj elem */, isNullKeyAllowed()/* elif !(float|double elem) */
                , getLowerKeyDomainBound(), getUpperKeyDomainBound()/* endif */);
    }


    @Override
    public String toString() {
        return "HashByteSetFactory[" + commonString() + keySpecialString() + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj instanceof HashByteSetFactory) {
            HashByteSetFactory factory = (HashByteSetFactory) obj;
            return commonEquals(factory) && keySpecialEquals(factory);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return keySpecialHashCode(commonHashCode());
    }

    /* define p1 *//* if obj elem // <E2 extends E>// endif *//* enddefine */

    /* define p2 *//* if obj elem //<E2>// endif *//* enddefine */

    /* define ep */
    /* if obj elem //<? extends E2>// elif !(obj elem) //<Byte>// endif */
    /* enddefine */

    private/*p1*/ UpdatableDHashByteSetGO/*p2*/ shrunk(UpdatableDHashByteSetGO/*p2*/ set) {
        Predicate<HashContainer> shrinkCondition;
        if ((shrinkCondition = hashConf.getShrinkCondition()) != null) {
            if (shrinkCondition.test(set))
                set.shrink();
        }
        return set;
    }

    /* with Updatable|Mutable mutability */
    @Override
    @Nonnull
    public/*p1*/ UpdatableDHashByteSetGO/*p2*/ newUpdatableSet() {
        return this./*p2*/newUpdatableSet(getDefaultExpectedSize());
    }
    /* endwith */

    private static int sizeOr(Iterable elems, int defaultSize) {
        return elems instanceof Collection ? ((Collection) elems).size() : defaultSize;
    }

    @Override
    @Nonnull
    public/*p1*/ UpdatableDHashByteSetGO/*p2*/ newUpdatableSet(Iterable/*ep*/<Byte>/**/ elements) {
        return this./*p2*/newUpdatableSet(elements, sizeOr(elements, getDefaultExpectedSize()));
    }

    @Override
    @Nonnull
    public/*p1*/ UpdatableDHashByteSetGO/*p2*/ newUpdatableSet(Iterable/*ep*/<Byte>/**/ elems1,
            Iterable/*ep*/<Byte>/**/ elems2) {
        long expectedSize = (long) sizeOr(elems1, 0);
        expectedSize += (long) sizeOr(elems2, 0);
        return this./*p2*/newUpdatableSet(elems1, elems2, sizeAsInt(expectedSize));
    }

    @Override
    @Nonnull
    public/*p1*/ UpdatableDHashByteSetGO/*p2*/ newUpdatableSet(Iterable/*ep*/<Byte>/**/ elems1,
            Iterable/*ep*/<Byte>/**/ elems2, Iterable/*ep*/<Byte>/**/ elems3) {
        long expectedSize = (long) sizeOr(elems1, 0);
        expectedSize += (long) sizeOr(elems2, 0);
        expectedSize += (long) sizeOr(elems3, 0);
        return this./*p2*/newUpdatableSet(elems1, elems2, elems3, sizeAsInt(expectedSize));
    }

    @Override
    @Nonnull
    public/*p1*/ UpdatableDHashByteSetGO/*p2*/ newUpdatableSet(Iterable/*ep*/<Byte>/**/ elems1,
            Iterable/*ep*/<Byte>/**/ elems2, Iterable/*ep*/<Byte>/**/ elems3,
            Iterable/*ep*/<Byte>/**/ elems4) {
        long expectedSize = (long) sizeOr(elems1, 0);
        expectedSize += (long) sizeOr(elems2, 0);
        expectedSize += (long) sizeOr(elems3, 0);
        expectedSize += (long) sizeOr(elems4, 0);
        return this./*p2*/newUpdatableSet(elems1, elems2, elems3, elems4, sizeAsInt(expectedSize));
    }

    @Override
    @Nonnull
    public/*p1*/ UpdatableDHashByteSetGO/*p2*/ newUpdatableSet(Iterable/*ep*/<Byte>/**/ elems1,
            Iterable/*ep*/<Byte>/**/ elems2, Iterable/*ep*/<Byte>/**/ elems3,
            Iterable/*ep*/<Byte>/**/ elems4, Iterable/*ep*/<Byte>/**/ elems5) {
        long expectedSize = (long) sizeOr(elems1, 0);
        expectedSize += (long) sizeOr(elems2, 0);
        expectedSize += (long) sizeOr(elems3, 0);
        expectedSize += (long) sizeOr(elems4, 0);
        expectedSize += (long) sizeOr(elems5, 0);
        return this./*p2*/newUpdatableSet(
                elems1, elems2, elems3, elems4, elems5, sizeAsInt(expectedSize));
    }

    @Override
    @Nonnull
    public/*p1*/ UpdatableDHashByteSetGO/*p2*/ newUpdatableSet(Iterable/*ep*/<Byte>/**/ elements,
            int expectedSize) {
        return shrunk(super./*p2*/newUpdatableSet(elements, expectedSize));
    }


    private static /*<>*/ void addAll(UpdatableDHashByteSetGO/*<>*/ set,
            Iterable<? extends Byte> elems) {
        if (elems instanceof Collection) {
            // noinspection unchecked
            set.addAll((Collection<? extends Byte>) elems);
        } else {
            for (byte e : elems) {
                set.add(e);
            }
        }
    }

    @Override
    @Nonnull
    public/*p1*/ UpdatableDHashByteSetGO/*p2*/ newUpdatableSet(Iterable/*ep*/<Byte>/**/ elems1,
            Iterable/*ep*/<Byte>/**/ elems2, int expectedSize) {
        UpdatableDHashByteSetGO/*p2*/ set = newUpdatableSet(expectedSize);
        addAll(set, elems1);
        addAll(set, elems2);
        return shrunk(set);
    }

    @Override
    @Nonnull
    public/*p1*/ UpdatableDHashByteSetGO/*p2*/ newUpdatableSet(Iterable/*ep*/<Byte>/**/ elems1,
            Iterable/*ep*/<Byte>/**/ elems2, Iterable/*ep*/<Byte>/**/ elems3,
            int expectedSize) {
        UpdatableDHashByteSetGO/*p2*/ set = newUpdatableSet(expectedSize);
        addAll(set, elems1);
        addAll(set, elems2);
        addAll(set, elems3);
        return shrunk(set);
    }

    @Override
    @Nonnull
    public/*p1*/ UpdatableDHashByteSetGO/*p2*/ newUpdatableSet(Iterable/*ep*/<Byte>/**/ elems1,
            Iterable/*ep*/<Byte>/**/ elems2, Iterable/*ep*/<Byte>/**/ elems3,
            Iterable/*ep*/<Byte>/**/ elems4, int expectedSize) {
        UpdatableDHashByteSetGO/*p2*/ set = newUpdatableSet(expectedSize);
        addAll(set, elems1);
        addAll(set, elems2);
        addAll(set, elems3);
        addAll(set, elems4);
        return shrunk(set);
    }

    @Override
    @Nonnull
    public/*p1*/ UpdatableDHashByteSetGO/*p2*/ newUpdatableSet(Iterable/*ep*/<Byte>/**/ elems1,
            Iterable/*ep*/<Byte>/**/ elems2, Iterable/*ep*/<Byte>/**/ elems3,
            Iterable/*ep*/<Byte>/**/ elems4, Iterable/*ep*/<Byte>/**/ elems5,
            int expectedSize) {
        UpdatableDHashByteSetGO/*p2*/ set = newUpdatableSet(expectedSize);
        addAll(set, elems1);
        addAll(set, elems2);
        addAll(set, elems3);
        addAll(set, elems4);
        addAll(set, elems5);
        return shrunk(set);
    }


    @Override
    @Nonnull
    public/*p1*/ UpdatableDHashByteSetGO/*p2*/ newUpdatableSet(Iterator/*ep*/<Byte>/**/ elements) {
        return this./*p2*/newUpdatableSet(elements, getDefaultExpectedSize());
    }

    @Override
    @Nonnull
    public/*p1*/ UpdatableDHashByteSetGO/*p2*/ newUpdatableSet(Iterator/*ep*/<Byte>/**/ elements,
            int expectedSize) {
        UpdatableDHashByteSetGO/*p2*/ set = newUpdatableSet(expectedSize);
        while (elements.hasNext()) {
            set.add(elements.next());
        }
        return shrunk(set);
    }

    @Override
    @Nonnull
    public/*p1*/ UpdatableDHashByteSetGO/*p2*/ newUpdatableSet(
            Consumer<com.koloboke.function./*f*/ByteConsumer/*p2*/> elementsSupplier) {
        return this./*p2*/newUpdatableSet(elementsSupplier, getDefaultExpectedSize());
    }

    /* define pe *//* if !(obj elem) //byte// elif obj elem //E2// endif *//* enddefine */

    @Override
    @Nonnull
    public/*p1*/ UpdatableDHashByteSetGO/*p2*/ newUpdatableSet(
            Consumer<com.koloboke.function./*f*/ByteConsumer/*p2*/> elementsSupplier,
            int expectedSize) {
        final UpdatableDHashByteSetGO/*p2*/ set = newUpdatableSet(expectedSize);
        elementsSupplier.accept(new com.koloboke.function./*f*/ByteConsumer/*p2*/() {
            @Override
            public void accept(/*pe*/byte/**/ e) {
                set.add(e);
            }
        });
        return shrunk(set);
    }

    @Override
    @Nonnull
    public/*p1*/ UpdatableDHashByteSetGO/*p2*/ newUpdatableSet(/*pe*/byte/**/[] elements) {
        return this./*p2*/newUpdatableSet(elements, elements.length);
    }

    @Override
    @Nonnull
    public/*p1*/ UpdatableDHashByteSetGO/*p2*/ newUpdatableSet(/*pe*/byte/**/[] elements,
            int expectedSize) {
        UpdatableDHashByteSetGO/*p2*/ set = newUpdatableSet(expectedSize);
        for (/*pe*/byte/**/ e : elements) {
            set.add(e);
        }
        return shrunk(set);
    }

    /* if !(obj elem) */
    @Override
    @Nonnull
    public UpdatableDHashByteSetGO newUpdatableSet(Byte[] elements) {
        return this./*p2*/newUpdatableSet(elements, elements.length);
    }

    @Override
    @Nonnull
    public UpdatableDHashByteSetGO newUpdatableSet(Byte[] elements,
            int expectedSize) {
        UpdatableDHashByteSetGO set = newUpdatableSet(expectedSize);
        for (byte e : elements) {
            set.add(e);
        }
        return shrunk(set);
    }
    /* endif */

    @Override
    @Nonnull
    public/*p1*/ UpdatableDHashByteSetGO/*p2*/ newUpdatableSetOf(/*pe*/byte/**/ e1) {
        UpdatableDHashByteSetGO/*p2*/ set = newUpdatableSet(1);
        set.add(e1);
        return set;
    }

    @Override
    @Nonnull
    public/*p1*/ UpdatableDHashByteSetGO/*p2*/ newUpdatableSetOf(
            /*pe*/byte/**/ e1, /*pe*/byte/**/ e2) {
        UpdatableDHashByteSetGO/*p2*/ set = newUpdatableSet(2);
        set.add(e1);
        set.add(e2);
        return set;
    }

    @Override
    @Nonnull
    public/*p1*/ UpdatableDHashByteSetGO/*p2*/ newUpdatableSetOf(
            /*pe*/byte/**/ e1, /*pe*/byte/**/ e2, /*pe*/byte/**/ e3) {
        UpdatableDHashByteSetGO/*p2*/ set = newUpdatableSet(3);
        set.add(e1);
        set.add(e2);
        set.add(e3);
        return set;
    }

    @Override
    @Nonnull
    public/*p1*/ UpdatableDHashByteSetGO/*p2*/ newUpdatableSetOf(
            /*pe*/byte/**/ e1, /*pe*/byte/**/ e2, /*pe*/byte/**/ e3, /*pe*/byte/**/ e4) {
        UpdatableDHashByteSetGO/*p2*/ set = newUpdatableSet(4);
        set.add(e1);
        set.add(e2);
        set.add(e3);
        set.add(e4);
        return set;
    }

    @Override
    @Nonnull
    public/*p1*/ UpdatableDHashByteSetGO/*p2*/ newUpdatableSetOf(/*pe*/byte/**/ e1,
            /*pe*/byte/**/ e2, /*pe*/byte/**/ e3, /*pe*/byte/**/ e4, /*pe*/byte/**/ e5,
            /*pe*/byte/**/... restElements) {
        UpdatableDHashByteSetGO/*p2*/ set = newUpdatableSet(5 + restElements.length);
        set.add(e1);
        set.add(e2);
        set.add(e3);
        set.add(e4);
        set.add(e5);
        for (/*pe*/byte/**/ e : restElements) {
            set.add(e);
        }
        return shrunk(set);
    }

    /* with Mutable|Immutable mutability */
    /* with with|without expectedSize */
    /* define arg *//* if with expectedSize //, int expectedSize// endif *//* enddefine */
    /* define apply *//* if with expectedSize //, expectedSize// endif *//* enddefine */

    @Override
    @Nonnull
    public/*p1*/ HashByteSet/*p2*/ newMutableSet(Iterable/*ep*/<Byte>/**/ elements/*arg*/) {
        MutableDHashByteSetGO/*p2*/ set = uninitializedMutableSet();
        set.move(this./*p2*/newUpdatableSet(elements/*apply*/));
        return set;
    }

    @Override
    @Nonnull
    public/*p1*/ HashByteSet/*p2*/ newMutableSet(Iterable/*ep*/<Byte>/**/ elems1,
            Iterable/*ep*/<Byte>/**/ elems2/*arg*/) {
        MutableDHashByteSetGO/*p2*/ set = uninitializedMutableSet();
        set.move(this./*p2*/newUpdatableSet(elems1, elems2/*apply*/));
        return set;
    }

    @Override
    @Nonnull
    public/*p1*/ HashByteSet/*p2*/ newMutableSet(Iterable/*ep*/<Byte>/**/ elems1,
            Iterable/*ep*/<Byte>/**/ elems2, Iterable/*ep*/<Byte>/**/ elems3/*arg*/) {
        MutableDHashByteSetGO/*p2*/ set = uninitializedMutableSet();
        set.move(this./*p2*/newUpdatableSet(elems1, elems2, elems3/*apply*/));
        return set;
    }

    @Override
    @Nonnull
    public/*p1*/ HashByteSet/*p2*/ newMutableSet(Iterable/*ep*/<Byte>/**/ elems1,
            Iterable/*ep*/<Byte>/**/ elems2, Iterable/*ep*/<Byte>/**/ elems3,
            Iterable/*ep*/<Byte>/**/ elems4/*arg*/) {
        MutableDHashByteSetGO/*p2*/ set = uninitializedMutableSet();
        set.move(this./*p2*/newUpdatableSet(elems1, elems2, elems3, elems4/*apply*/));
        return set;
    }

    @Override
    @Nonnull
    public/*p1*/ HashByteSet/*p2*/ newMutableSet(Iterable/*ep*/<Byte>/**/ elems1,
            Iterable/*ep*/<Byte>/**/ elems2, Iterable/*ep*/<Byte>/**/ elems3,
            Iterable/*ep*/<Byte>/**/ elems4, Iterable/*ep*/<Byte>/**/ elems5/*arg*/) {
        MutableDHashByteSetGO/*p2*/ set = uninitializedMutableSet();
        set.move(this./*p2*/newUpdatableSet(elems1, elems2, elems3, elems4, elems5/*apply*/));
        return set;
    }

    /* endwith */

    @Override
    @Nonnull
    public/*p1*/ HashByteSet/*p2*/ newMutableSet(Iterator/*ep*/<Byte>/**/ elements) {
        MutableDHashByteSetGO/*p2*/ set = uninitializedMutableSet();
        set.move(this./*p2*/newUpdatableSet(elements));
        return set;
    }

    @Override
    @Nonnull
    public/*p1*/ HashByteSet/*p2*/ newMutableSet(Iterator/*ep*/<Byte>/**/ elements,
            int expectedSize) {
        MutableDHashByteSetGO/*p2*/ set = uninitializedMutableSet();
        set.move(this./*p2*/newUpdatableSet(elements, expectedSize));
        return set;
    }

    @Override
    @Nonnull
    public/*p1*/ HashByteSet/*p2*/ newMutableSet(
            Consumer<com.koloboke.function./*f*/ByteConsumer/*p2*/> elementsSupplier) {
        MutableDHashByteSetGO/*p2*/ set = uninitializedMutableSet();
        set.move(this./*p2*/newUpdatableSet(elementsSupplier));
        return set;
    }

    @Override
    @Nonnull
    public/*p1*/ HashByteSet/*p2*/ newMutableSet(
            Consumer<com.koloboke.function./*f*/ByteConsumer/*p2*/> elementsSupplier,
            int expectedSize) {
        MutableDHashByteSetGO/*p2*/ set = uninitializedMutableSet();
        set.move(this./*p2*/newUpdatableSet(elementsSupplier, expectedSize));
        return set;
    }

    @Override
    @Nonnull
    public/*p1*/ HashByteSet/*p2*/ newMutableSet(/*pe*/byte/**/[] elements) {
        MutableDHashByteSetGO/*p2*/ set = uninitializedMutableSet();
        set.move(this./*p2*/newUpdatableSet(elements));
        return set;
    }

    @Override
    @Nonnull
    public/*p1*/ HashByteSet/*p2*/ newMutableSet(/*pe*/byte/**/[] elements, int expectedSize) {
        MutableDHashByteSetGO/*p2*/ set = uninitializedMutableSet();
        set.move(this./*p2*/newUpdatableSet(elements, expectedSize));
        return set;
    }

    /* if !(obj elem) */
    @Override
    @Nonnull
    public HashByteSet newMutableSet(Byte[] elements) {
        MutableDHashByteSetGO set = uninitializedMutableSet();
        set.move(this./*p2*/newUpdatableSet(elements));
        return set;
    }

    @Override
    @Nonnull
    public HashByteSet newMutableSet(Byte[] elements, int expectedSize) {
        MutableDHashByteSetGO set = uninitializedMutableSet();
        set.move(this./*p2*/newUpdatableSet(elements, expectedSize));
        return set;
    }
    /* endif */

    @Override
    @Nonnull
    public/*p1*/ HashByteSet/*p2*/ newMutableSetOf(/*pe*/byte/**/ e1) {
        MutableDHashByteSetGO/*p2*/ set = uninitializedMutableSet();
        set.move(this./*p2*/newUpdatableSetOf(e1));
        return set;
    }

    @Override
    @Nonnull
    public/*p1*/ HashByteSet/*p2*/ newMutableSetOf(/*pe*/byte/**/ e1, /*pe*/byte/**/ e2) {
        MutableDHashByteSetGO/*p2*/ set = uninitializedMutableSet();
        set.move(this./*p2*/newUpdatableSetOf(e1, e2));
        return set;
    }

    @Override
    @Nonnull
    public/*p1*/ HashByteSet/*p2*/ newMutableSetOf(/*pe*/byte/**/ e1, /*pe*/byte/**/ e2,
            /*pe*/byte/**/ e3) {
        MutableDHashByteSetGO/*p2*/ set = uninitializedMutableSet();
        set.move(this./*p2*/newUpdatableSetOf(e1, e2, e3));
        return set;
    }

    @Override
    @Nonnull
    public/*p1*/ HashByteSet/*p2*/ newMutableSetOf(/*pe*/byte/**/ e1, /*pe*/byte/**/ e2,
            /*pe*/byte/**/ e3, /*pe*/byte/**/ e4) {
        MutableDHashByteSetGO/*p2*/ set = uninitializedMutableSet();
        set.move(this./*p2*/newUpdatableSetOf(e1, e2, e3, e4));
        return set;
    }

    @Override
    @Nonnull
    public/*p1*/ HashByteSet/*p2*/ newMutableSetOf(/*pe*/byte/**/ e1, /*pe*/byte/**/ e2,
            /*pe*/byte/**/ e3, /*pe*/byte/**/ e4, /*pe*/byte/**/ e5,
            /*pe*/byte/**/... restElements) {
        MutableDHashByteSetGO/*p2*/ set = uninitializedMutableSet();
        set.move(this./*p2*/newUpdatableSetOf(e1, e2, e3, e4, e5, restElements));
        return set;
    }
    /* endwith */
}
