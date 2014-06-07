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


package net.openhft.collect.impl;

import net.openhft.collect.NullableObjects;
import net.openhft.collect.CharCollection;
import net.openhft.function.*;
import net.openhft.collect.set.CharSet;

import java.util.Collection;


public final class CommonCharCollectionOps {

    public static boolean containsAll(final CharCollection/*<?>*/ collection,
            Collection<?> another) {
        if (collection == another)
            return true;
        if (another instanceof CharCollection) {
            CharCollection c2 = (CharCollection) another;
            /* if obj elem */
            if (NullableObjects.equals(collection.equivalence(), c2.equivalence())) {
            /* endif */
            if (collection instanceof CharSet && c2 instanceof CharSet &&
                    collection.size() < another.size()) {
                return false;
            }
            if (c2 instanceof InternalCharCollectionOps) {
                // noinspection unchecked
                return ((InternalCharCollectionOps) c2).allContainingIn(collection);
            }
            /* if obj elem */
            }
            // noinspection unchecked
            /* endif */
            return c2.forEachWhile(new
                    /*f*/CharPredicate/* if CommentForFuncProcessor elem //
                        a template should start after function name to suppress params generation
                        // endif */() {
                @Override
                public boolean test(/* raw */char value) {
                    return collection.contains(value);
                }
            });
        } else {
            for (Object o : another) {
                if (!collection.contains(/* if !(obj elem) */((Character) o).charValue()
                        /* elif obj elem //o// endif */))
                    return false;
            }
            return true;
        }
    }

    public static /*<>*/ boolean addAll(final CharCollection/*<>*/ collection,
            Collection<? extends Character> another) {
        if (collection == another)
            throw new IllegalArgumentException();
        long maxPossibleSize = collection.sizeAsLong() + Containers.sizeAsLong(another);
        collection.ensureCapacity(maxPossibleSize);
        if (another instanceof CharCollection) {
            if (another instanceof InternalCharCollectionOps) {
                return ((InternalCharCollectionOps) another).reverseAddAllTo(collection);
            } else {
                class AddAll implements /*f*/CharConsumer/*<>*/ {
                    boolean collectionChanged = false;
                    @Override
                    public void accept(char value) {
                        collectionChanged |= collection.add(value);
                    }
                }
                AddAll addAll = new AddAll();
                ((CharCollection) another).forEach(addAll);
                return addAll.collectionChanged;
            }
        } else {
            boolean collectionChanged = false;
            for (Character v : another) {
                collectionChanged |= collection.add(v/* if !(obj elem) */.charValue()/* endif */);
            }
            return collectionChanged;
        }
    }


    private CommonCharCollectionOps() {}
}
