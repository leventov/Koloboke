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

package com.koloboke.collect.testing.testers;

import com.google.common.collect.testing.MinimalCollection;
import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;
import com.koloboke.collect.set.hash.HashCharSets;
import com.koloboke.collect.testing.AbstractCharCollectionTester;

import java.util.*;

import static com.google.common.collect.testing.features.CollectionFeature.ALLOWS_NULL_VALUES;
import static com.google.common.collect.testing.features.CollectionFeature.SUPPORTS_REMOVE;
import static com.google.common.collect.testing.features.CollectionSize.ONE;
import static com.google.common.collect.testing.features.CollectionSize.ZERO;


/**
 * @see com.google.common.collect.testing.testers.CollectionRetainAllTester
 */
public class CharCollectionRetainAllTester/*<>*/ extends AbstractCharCollectionTester/*<>*/ {
    /**
     * A collection of elements to retain, along with a description for use in
     * failure messages.
     */
    private class Target {
        private final Collection<Character> toRetain;
        private final String description;

        private Target(Collection<Character> toRetain, String description) {
            this.toRetain = toRetain;
            this.description = description;
        }

        @Override public String toString() {
            return description;
        }
    }

    private Target empty;
    private Target disjoint;
    private Target superset;
    private Target nonEmptyProperSubset;
    private Target sameElements;
    private Target partialOverlap;
    /* if obj|float|double elem */
    private Target specialSingleton;
    /* endif */

    //TODO return "duplicates" when collections with 

    @Override public void setUp() throws Exception {
        super.setUp();

        empty = new Target(emptyCollection(), "empty");
        List<Character> disjointList = Arrays.asList(samples.e3(), samples.e4());
        disjoint = new Target(disjointList, "disjoint");
        superset = new Target(MinimalCollection.of(
                samples.e0(), samples.e1(), samples.e2(), samples.e3(), samples.e4()),
                "superset");
        nonEmptyProperSubset = new Target(MinimalCollection.of(samples.e1()), "subset");
        sameElements = new Target(Arrays.asList(createSamplesArray()), "sameElements");
        partialOverlap = new Target(MinimalCollection.of(samples.e2(), samples.e3()),
                "partialOverlap");
        /* if obj|float|double elem */
        specialSingleton =
                new Target(Collections.<Character>singleton(special()), "specialSingleton");
        /* endif */
    }

    /* with Simple|Specialized retainCollection */

    // retainAll(empty)

    /* with No|Some removed */
    @CollectionFeature.Require(SUPPORTS_REMOVE)
    @CollectionSize.Require(/* if No removed */ZERO/* elif Some removed //ONE// endif */)
    public void testRetainAll_emptyPreviouslyEmpty_simpleRetainCollection_noRemoved() {
        /* if Some removed */remove();/* endif */
        expectReturnsFalse(simple(empty));
        expectContents(noRemoved(getOrderedElements()));
    }
    /* endwith */

    @CollectionFeature.Require(absent = SUPPORTS_REMOVE)
    @CollectionSize.Require(ZERO)
    public void testRetainAll_emptyPreviouslyEmptyUnsupported_simpleRetainCollection() {
        expectReturnsFalseOrThrows(simple(empty));
        expectUnchanged();
    }

    /* with No|Some removed */
    @CollectionFeature.Require(SUPPORTS_REMOVE)
    @CollectionSize.Require(absent = {ZERO/* if Some removed */, ONE/* endif */})
    public void testRetainAll_emptyPreviouslyNonEmpty_simpleRetainCollection_noRemoved() {
        /* if Some removed */remove();/* endif */
        expectReturnsTrue(simple(empty));
        expectContents();
        expectMissing(samples.e0(), samples.e1(), samples.e2());
    }
    /* endwith */

    @CollectionFeature.Require(absent = SUPPORTS_REMOVE)
    @CollectionSize.Require(absent = ZERO)
    public void testRetainAll_emptyPreviouslyNonEmptyUnsupported_simpleRetainCollection() {
        expectThrows(simple(empty));
        expectUnchanged();
    }

    // retainAll(disjoint)

    /* with No|Some removed */
    @CollectionFeature.Require(SUPPORTS_REMOVE)
    @CollectionSize.Require(/* if No removed */ZERO/* elif Some removed //ONE// endif */)
    public void testRetainAll_disjointPreviouslyEmpty_simpleRetainCollection_noRemoved() {
        /* if Some removed */remove();/* endif */
        expectReturnsFalse(simple(disjoint));
        expectContents(noRemoved(getOrderedElements()));
    }
    /* endwith */

    @CollectionFeature.Require(absent = SUPPORTS_REMOVE)
    @CollectionSize.Require(ZERO)
    public void testRetainAll_disjointPreviouslyEmptyUnsupported_simpleRetainCollection() {
        expectReturnsFalseOrThrows(simple(disjoint));
        expectUnchanged();
    }

    /* with No|Some removed */
    @CollectionFeature.Require(SUPPORTS_REMOVE)
    @CollectionSize.Require(absent = {ZERO/* if Some removed */, ONE/* endif */})
    public void testRetainAll_disjointPreviouslyNonEmpty_simpleRetainCollection_noRemoved() {
        /* if Some removed */remove();/* endif */
        expectReturnsTrue(simple(disjoint));
        expectContents();
        expectMissing(samples.e0(), samples.e1(), samples.e2());
    }
    /* endwith */

    @CollectionFeature.Require(absent = SUPPORTS_REMOVE)
    @CollectionSize.Require(absent = ZERO)
    public void testRetainAll_disjointPreviouslyNonEmptyUnsupported_simpleRetainCollection() {
        expectThrows(simple(disjoint));
        expectUnchanged();
    }

    // retainAll(superset)

    /* with No|Some removed */
    @CollectionFeature.Require(SUPPORTS_REMOVE)
    /* if Some removed */@CollectionSize.Require(absent = ZERO)/* endif */
    public void testRetainAll_superset_simpleRetainCollection_noRemoved() {
        /* if Some removed */remove();/* endif */
        expectReturnsFalse(simple(superset));
        expectContents(noRemoved(getOrderedElements()));
    }
    /* endwith */

    @CollectionFeature.Require(absent = SUPPORTS_REMOVE)
    public void testRetainAll_supersetUnsupported_simpleRetainCollection() {
        expectReturnsFalseOrThrows(simple(superset));
        expectUnchanged();
    }

    // retainAll(subset)

    /* with No|Some removed */
    @CollectionFeature.Require(SUPPORTS_REMOVE)
    @CollectionSize.Require(absent = {ZERO, ONE})
    public void testRetainAll_subset_simpleRetainCollection_noRemoved() {
        /* if Some removed */remove();/* endif */
        expectReturnsTrue(simple(nonEmptyProperSubset));
        expectContents(noRemoved(nonEmptyProperSubset.toRetain));
    }
    /* endwith */

    @CollectionFeature.Require(absent = SUPPORTS_REMOVE)
    @CollectionSize.Require(absent = {ZERO, ONE})
    public void testRetainAll_subsetUnsupported_simpleRetainCollection() {
        expectThrows(simple(nonEmptyProperSubset));
        expectUnchanged();
    }

    // retainAll(sameElements)

    /* with No|Some removed */
    @CollectionFeature.Require(SUPPORTS_REMOVE)
    /* if Some removed */@CollectionSize.Require(absent = ZERO)/* endif */
    public void testRetainAll_sameElements_simpleRetainCollection_noRemoved() {
        /* if Some removed */remove();/* endif */
        expectReturnsFalse(simple(sameElements));
        expectContents(noRemoved(getOrderedElements()));
    }
    /* endwith */

    @CollectionFeature.Require(absent = SUPPORTS_REMOVE)
    public void testRetainAll_sameElementsUnsupported_simpleRetainCollection() {
        expectReturnsFalseOrThrows(simple(sameElements));
        expectUnchanged();
    }

    // retainAll(partialOverlap)

    /* with No|Some removed */
    @CollectionFeature.Require(SUPPORTS_REMOVE)
    @CollectionSize.Require(absent = {ZERO, ONE})
    public void testRetainAll_partialOverlap_simpleRetainCollection_noRemoved() {
        /* if Some removed */remove();/* endif */
        expectReturnsTrue(simple(partialOverlap));
        expectContents(samples.e2());
    }
    /* endwith */

    @CollectionFeature.Require(absent = SUPPORTS_REMOVE)
    @CollectionSize.Require(absent = {ZERO, ONE})
    public void testRetainAll_partialOverlapUnsupported_simpleRetainCollection() {
        expectThrows(simple(partialOverlap));
        expectUnchanged();
    }

    /* if obj|float|double elem */
    // retainAll(specialSingleton)

    @CollectionFeature.Require(SUPPORTS_REMOVE)
    @CollectionSize.Require(ZERO)
    public void testRetainAll_specialSingletonPreviouslyEmpty_simpleRetainCollection() {
        expectReturnsFalse(simple(specialSingleton));
        expectUnchanged();
    }

    @CollectionFeature.Require(SUPPORTS_REMOVE)
    @CollectionSize.Require(absent = ZERO)
    public void testRetainAll_specialSingletonPreviouslyNonEmpty_simpleRetainCollection() {
        expectReturnsTrue(simple(specialSingleton));
        expectContents();
    }

    @CollectionFeature.Require({SUPPORTS_REMOVE/* if obj elem */, ALLOWS_NULL_VALUES/* endif */})
    @CollectionSize.Require(ONE)
    public void
    testRetainAll_specialSingletonPreviouslySingletonWithSpecial_simpleRetainCollection() {
        initCollectionWithSpecialElement();
        expectReturnsFalse(simple(specialSingleton));
        expectContents(createArrayWithSpecialElement());
    }

    @CollectionFeature.Require({SUPPORTS_REMOVE/* if obj elem */, ALLOWS_NULL_VALUES/* endif */})
    @CollectionSize.Require(absent = {ZERO, ONE})
    public void
    testRetainAll_specialSingletonPreviouslySeveralWithSpecial_simpleRetainCollection() {
        initCollectionWithSpecialElement();
        expectReturnsTrue(simple(specialSingleton));
        expectContents(specialSingleton.toRetain);
    }

    // specialSingleton.retainAll()

    @CollectionFeature.Require({SUPPORTS_REMOVE/* if obj elem */, ALLOWS_NULL_VALUES/* endif */})
    @CollectionSize.Require(absent = ZERO)
    public void testRetainAll_containsNonSpecialWithSpecial_simpleRetainCollection() {
        initCollectionWithSpecialElement();
        expectReturnsTrue(simple(disjoint));
        expectContents();
    }
    /* endif */

    /* endwith */

    private Target simple(Target target) {
        return target;
    }

    private Target specialized(Target target) {
        return new Target(HashCharSets.newImmutableSet(target.toRetain),
                "specialized " + target.description);
    }

    // retainAll(null)

  /*
   * AbstractCollection fails the retainAll(null) test when the subject
   * collection is empty, but we'd still like to test retainAll(null) when we
   * can. We split the test into empty and non-empty cases. This allows us to
   * suppress only the former.
   */

    @CollectionFeature.Require(SUPPORTS_REMOVE)
    @CollectionSize.Require(ZERO)
    public void testRetainAll_nullCollectionReferenceEmptySubject() {
        try {
            collection.retainAll(null);
            // Returning successfully is not ideal, but tolerated.
        } catch (NullPointerException expected) {
        }
    }

    @CollectionFeature.Require(SUPPORTS_REMOVE)
    @CollectionSize.Require(absent = ZERO)
    public void testRetainAll_nullCollectionReferenceNonEmptySubject() {
        try {
            collection.retainAll(null);
            fail("retainAll(null) should throw NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    private void expectReturnsTrue(Target target) {
        String message
                = String.format("retainAll(%s) should return true", target);
        assertTrue(message, collection.retainAll(target.toRetain));
    }

    private void expectReturnsFalse(Target target) {
        String message
                = String.format("retainAll(%s) should return false", target);
        assertFalse(message, collection.retainAll(target.toRetain));
    }

    private void expectThrows(Target target) {
        try {
            collection.retainAll(target.toRetain);
            String message = String.format("retainAll(%s) should throw", target);
            fail(message);
        } catch (UnsupportedOperationException expected) {
        }
    }

    private void expectReturnsFalseOrThrows(Target target) {
        String message = String.format("retainAll(%s) should return false or throw", target);
        try {
            assertFalse(message, collection.retainAll(target.toRetain));
        } catch (UnsupportedOperationException tolerated) {
        }
    }
}
