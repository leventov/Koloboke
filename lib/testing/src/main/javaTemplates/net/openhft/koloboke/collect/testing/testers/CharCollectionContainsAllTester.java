/* with char|byte|short|int|long|float|double|obj elem */
/*
 * Copyright (C) 2007 The Guava Authors
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

package net.openhft.koloboke.collect.testing.testers;

import com.google.common.collect.testing.MinimalCollection;
import com.google.common.collect.testing.WrongType;
import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;
import net.openhft.koloboke.collect.set.hash.HashObjSets;
import net.openhft.koloboke.collect.testing.AbstractCharCollectionTester;

import java.util.Collection;

import static com.google.common.collect.testing.features.CollectionFeature.ALLOWS_NULL_QUERIES;
import static com.google.common.collect.testing.features.CollectionFeature.ALLOWS_NULL_VALUES;
import static com.google.common.collect.testing.features.CollectionFeature.SUPPORTS_REMOVE;
import static com.google.common.collect.testing.features.CollectionSize.ONE;
import static com.google.common.collect.testing.features.CollectionSize.ZERO;
import static java.util.Arrays.asList;


/**
 * @see com.google.common.collect.testing.testers.CollectionContainsAllTester
 */
public class CharCollectionContainsAllTester/*<>*/ extends AbstractCharCollectionTester/*<>*/ {
    /* with Simple|Specialized containsCollection */
    public void testContainsAll_empty_simpleContainsCollection() {
        assertTrue("containsAll(empty) should return true",
                collection.containsAll(simple(MinimalCollection.of())));
    }

    /* with No|Some removed */
    /* if Some removed */@CollectionFeature.Require(SUPPORTS_REMOVE)/* endif */
    @CollectionSize.Require(absent =  {ZERO/* if Some removed */, ONE/* endif */})
    public void testContainsAll_subset_simpleContainsCollection_noRemoved() {
        /* if Some removed */remove();/* endif */
        Character e = samples./* if No removed */e0/* elif Some removed //e1// endif */;
        assertTrue("containsAll(subset) should return true",
                collection.containsAll(simple(MinimalCollection.of(e))));
    }

    /* if Some removed */
    @CollectionSize.Require(absent = ZERO)
    @CollectionFeature.Require(SUPPORTS_REMOVE)
    /* endif */
    public void testContainsAll_sameElements_simpleContainsCollection_noRemoved() {
        /* if Some removed */remove();/* endif */
        assertTrue("containsAll(sameElements) should return true",
                collection.containsAll(
                        simple(MinimalCollection.of((Character[])
                                noRemoved(asList(createSamplesArray()))
                                    .toArray(/* if !(obj elem) */new Character[0]/* endif */))))
        );
    }

    /* if Some removed */
    @CollectionSize.Require(absent = ZERO)
    @CollectionFeature.Require(SUPPORTS_REMOVE)
    /* endif */
    public void testContainsAll_partialOverlap_simpleContainsCollection_noRemoved() {
        /* if Some removed */remove();/* endif */
        Character e = samples./* if No removed */e0/* elif Some removed //e1// endif */;
        assertFalse("containsAll(partialOverlap) should return false",
                collection.containsAll(simple(MinimalCollection.of(e, samples.e3))));
    }

    /* if Some removed */
    @CollectionSize.Require(absent = ZERO)
    @CollectionFeature.Require(SUPPORTS_REMOVE)
    /* endif */
    public void testContainsAll_disjoint_simpleContainsCollection_noRemoved() {
        /* if Some removed */remove();/* endif */
        assertFalse("containsAll(disjoint) should return false",
                collection.containsAll(simple(MinimalCollection.of(samples.e3))));
    }

    /* endwith */

    /* if obj|float|double elem */
    @CollectionFeature.Require(ALLOWS_NULL_QUERIES)
    public void testContainsAll_specialAllowed_simpleContainsCollection() {
        assertFalse(collection.containsAll(simple(MinimalCollection.of(special()))));
    }

    @CollectionFeature.Require(ALLOWS_NULL_VALUES)
    @CollectionSize.Require(absent = ZERO)
    public void testContainsAll_specialPresent_simpleContainsCollection() {
        initCollectionWithSpecialElement();
        assertTrue(collection.containsAll(simple(MinimalCollection.of(special()))));
    }
    /* endif */

    /* endwith */

    /* if obj elem */
    public void testContainsAll_wrongType_specializedContainsCollection() {
        Collection<WrongType> wrong = HashObjSets.newImmutableSetOf(WrongType.VALUE);
        try {
            assertFalse("containsAll(wrongType) should return false or throw",
                    collection.containsAll(wrong));
        } catch (ClassCastException tolerated) {
        }
    }
    /* endif */
}
