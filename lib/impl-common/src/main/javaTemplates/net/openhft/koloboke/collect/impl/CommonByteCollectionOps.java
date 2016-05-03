/* with byte|char|short|int|long|float|double|obj elem */
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

package net.openhft.koloboke.collect.impl;

import net.openhft.koloboke.collect.ByteCollection;
import net.openhft.koloboke.function./*f*/ByteConsumer/**/;
import net.openhft.koloboke.function./*f*/BytePredicate/**/;
import net.openhft.koloboke.collect.set.ByteSet;

import java.util.Collection;


public final class CommonByteCollectionOps {

    public static boolean containsAll(final ByteCollection/*<?>*/ collection,
            Collection<?> another) {
        if (collection == another)
            return true;
        if (another instanceof ByteCollection) {
            ByteCollection c2 = (ByteCollection) another;
            /* if obj elem */
            if (collection.equivalence().equals(c2.equivalence())) {
            /* endif */
            if (collection instanceof ByteSet && c2 instanceof ByteSet &&
                    collection.size() < another.size()) {
                return false;
            }
            if (c2 instanceof InternalByteCollectionOps) {
                // noinspection unchecked
                return ((InternalByteCollectionOps) c2).allContainingIn(collection);
            }
            /* if obj elem */
            }
            // noinspection unchecked
            /* endif */
            return c2.forEachWhile(new
                    /*f*/BytePredicate/**/() {
                @Override
                public boolean test(/* raw */byte value) {
                    return collection.contains(value);
                }
            });
        } else {
            for (Object o : another) {
                if (!collection.contains(/* if !(obj elem) */((Byte) o).byteValue()
                        /* elif obj elem //o// endif */))
                    return false;
            }
            return true;
        }
    }

    public static /*<>*/ boolean addAll(final ByteCollection/*<>*/ collection,
            Collection<? extends Byte> another) {
        if (collection == another)
            throw new IllegalArgumentException();
        long maxPossibleSize = collection.sizeAsLong() + Containers.sizeAsLong(another);
        collection.ensureCapacity(maxPossibleSize);
        if (another instanceof ByteCollection) {
            if (another instanceof InternalByteCollectionOps) {
                return ((InternalByteCollectionOps) another).reverseAddAllTo(collection);
            } else {
                class AddAll implements /*f*/ByteConsumer/*<>*/ {
                    boolean collectionChanged = false;
                    @Override
                    public void accept(byte value) {
                        collectionChanged |= collection.add(value);
                    }
                }
                AddAll addAll = new AddAll();
                ((ByteCollection) another).forEach(addAll);
                return addAll.collectionChanged;
            }
        } else {
            boolean collectionChanged = false;
            for (Byte v : another) {
                collectionChanged |= collection.add(v/* if !(obj elem) */.byteValue()/* endif */);
            }
            return collectionChanged;
        }
    }


    private CommonByteCollectionOps() {}
}
