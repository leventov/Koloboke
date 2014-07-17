/* with char|byte|short|int|long|float|double|obj elem */
/*
 * Copyright (C) 2008 The Guava Authors
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

package net.openhft.collect.testing.testers;

import com.google.common.collect.testing.MinimalCollection;
import com.google.common.collect.testing.WrongType;
import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;
import net.openhft.collect.set.hash.HashCharSets;
import net.openhft.collect.set.hash.HashObjSets;
import net.openhft.collect.testing.AbstractCharCollectionTester;

import java.util.*;

import static com.google.common.collect.testing.features.CollectionFeature.*;
import static com.google.common.collect.testing.features.CollectionFeature.SUPPORTS_REMOVE;
import static com.google.common.collect.testing.features.CollectionSize.ONE;
import static com.google.common.collect.testing.features.CollectionSize.SEVERAL;
import static com.google.common.collect.testing.features.CollectionSize.ZERO;


/**
 * @see com.google.common.collect.testing.testers.CollectionRemoveAllTester
 */
public class CharCollectionRemoveAllTester/*<>*/ extends AbstractCharCollectionTester/*<>*/ {

    /* with Simple|Specialized removeCollection */

    /* with No|Some removed */

    @CollectionFeature.Require(SUPPORTS_REMOVE)
    /* if Some removed */@CollectionSize.Require(absent = ZERO)/* endif */
    public void testRemoveAll_emptyCollection_simpleRemoveCollection_noRemoved() {
        /* if Some removed */remove();/* endif */
        assertFalse("removeAll(emptyCollection) should return false",
                collection.removeAll(simple(MinimalCollection.of())));
        expectContents(noRemoved(getOrderedElements()));
    }

    @CollectionFeature.Require(SUPPORTS_REMOVE)
    /* if Some removed */@CollectionSize.Require(absent = ZERO)/* endif */
    public void testRemoveAll_nonePresent_simpleRemoveCollection_noRemoved() {
        /* if Some removed */remove();/* endif */
        assertFalse("removeAll(disjointCollection) should return false",
                collection.removeAll(simple(MinimalCollection.of(samples.e3))));
        expectContents(noRemoved(getOrderedElements()));
    }

    @CollectionFeature.Require(SUPPORTS_REMOVE)
    @CollectionSize.Require(absent = {ZERO/* if Some removed */, ONE/* endif */})
    public void testRemoveAll_allPresent_simpleRemoveCollection_noRemoved() {
        /* if Some removed */remove();/* endif */
        Character e = samples./* if No removed */e0/* elif Some removed //e1// endif */;
        assertTrue("removeAll(intersectingCollection) should return true",
                collection.removeAll(simple(MinimalCollection.of(e))));
        expectMissing(e);
    }

    @CollectionFeature.Require(SUPPORTS_REMOVE)
    @CollectionSize.Require(absent = {ZERO/* if Some removed */, ONE/* endif */})
    public void testRemoveAll_somePresent_simpleRemoveCollection_noRemoved() {
        /* if Some removed */remove();/* endif */
        Character e = samples./* if No removed */e0/* elif Some removed //e1// endif */;
        assertTrue("removeAll(intersectingCollection) should return true",
                collection.removeAll(simple(MinimalCollection.of(e, samples.e3))));
        expectMissing(e);
    }

    @CollectionFeature.Require({SUPPORTS_REMOVE,
            FAILS_FAST_ON_CONCURRENT_MODIFICATION})
    @CollectionSize.Require(SEVERAL)
    public void testRemoveAllSomePresentConcurrentWithIteration_simpleRemoveCollection_noRemoved() {
        try {
            /* if Some removed */remove();/* endif */
            Iterator<Character> iterator = collection.iterator();
            assertTrue(collection.removeAll(simple(MinimalCollection.of(samples.e1, samples.e3))));
            iterator.next();
            fail("Expected ConcurrentModificationException");
        } catch (ConcurrentModificationException expected) {
            // success
        }
    }

    /* endwith */

    @CollectionFeature.Require(absent = SUPPORTS_REMOVE)
    public void testRemoveAll_unsupportedEmptyCollection_simpleRemoveCollection() {
        try {
            assertFalse("removeAll(emptyCollection) should return false or throw "
                            + "UnsupportedOperationException",
                    collection.removeAll(simple(MinimalCollection.of())));
        } catch (UnsupportedOperationException tolerated) {
        }
        expectUnchanged();
    }

    @CollectionFeature.Require(absent = SUPPORTS_REMOVE)
    public void testRemoveAll_unsupportedNonePresent_simpleRemoveCollection() {
        try {
            assertFalse("removeAll(disjointCollection) should return false or throw "
                            + "UnsupportedOperationException",
                    collection.removeAll(simple(MinimalCollection.of(samples.e3))));
        } catch (UnsupportedOperationException tolerated) {
        }
        expectUnchanged();
    }

    @CollectionFeature.Require(absent = SUPPORTS_REMOVE)
    @CollectionSize.Require(absent = ZERO)
    public void testRemoveAll_unsupportedPresent_simpleRemoveCollection() {
        try {
            collection.removeAll(simple(MinimalCollection.of(samples.e0)));
            fail("removeAll(intersectingCollection) should throw "
                    + "UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
        }
        expectUnchanged();
        assertTrue(collection.contains(samples.e0));
    }

    /* if obj elem Specialized removeCollection */
    @CollectionFeature.Require(value = SUPPORTS_REMOVE,
            absent = ALLOWS_NULL_QUERIES)
    public void testRemoveAll_containsNullNo_simpleRemoveCollection() {
        Collection<Character> containsNull = simple(MinimalCollection.of((Character) null));
        try {
            assertFalse("removeAll(containsNull) should return false or throw",
                    collection.removeAll(containsNull));
        } catch (NullPointerException tolerated) {
        }
        expectUnchanged();
    }

    @CollectionFeature.Require(SUPPORTS_REMOVE)
    public void testRemoveAll_containsWrongType_simpleRemoveCollection() {
        try {
            Collection<WrongType> wrong = HashObjSets.newImmutableSetOf(WrongType.VALUE);
            assertFalse("removeAll(containsWrongType) should return false or throw",
                    collection.removeAll(wrong));
        } catch (ClassCastException tolerated) {
        }
        expectUnchanged();
    }
    /* endif */

    /* if obj|float|double elem */
    @CollectionFeature.Require({SUPPORTS_REMOVE/* if obj elem */, ALLOWS_NULL_QUERIES/* endif */})
    public void testRemoveAll_containsSpecialNoButAllowed_simpleRemoveCollection() {
        Collection<Character> containsSpecial = simple(MinimalCollection.of(special()));
        assertFalse("removeAll(containsSpecial) should return false",
                collection.removeAll(containsSpecial));
        expectUnchanged();
    }

    @CollectionFeature.Require({SUPPORTS_REMOVE/* if obj elem */, ALLOWS_NULL_VALUES/* endif */})
    @CollectionSize.Require(absent = ZERO)
    public void testRemoveAll_containsSpecialYes_simpleRemoveCollection() {
        initCollectionWithSpecialElement();
        assertTrue("removeAll(containsSpecial) should return true",
                collection.removeAll(simple(Collections.singleton(special()))));
    }
    /* endif */

    /* endwith */

      /*
   * AbstractCollection fails the removeAll(null) test when the subject
   * collection is empty, but we'd still like to test removeAll(null) when we
   * can. We split the test into empty and non-empty cases. This allows us to
   * suppress only the former.
   */

    @CollectionFeature.Require(SUPPORTS_REMOVE)
    @CollectionSize.Require(ZERO)
    public void testRemoveAll_nullCollectionReferenceEmptySubject() {
        try {
            collection.removeAll(null);
            // Returning successfully is not ideal, but tolerated.
        } catch (NullPointerException expected) {
        }
    }

    @CollectionFeature.Require(SUPPORTS_REMOVE)
    @CollectionSize.Require(absent = ZERO)
    public void testRemoveAll_nullCollectionReferenceNonEmptySubject() {
        try {
            collection.removeAll(null);
            fail("removeAll(null) should throw NullPointerException");
        } catch (NullPointerException expected) {
        }
    }
}
