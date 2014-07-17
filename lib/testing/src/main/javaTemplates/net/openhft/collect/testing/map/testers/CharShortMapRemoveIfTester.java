/* with
 char|byte|short|int|long|float|double|obj key
 short|byte|char|int|long|float|double|obj value
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

package net.openhft.collect.testing.map.testers;

import com.google.common.collect.Lists;
import com.google.common.collect.testing.features.CollectionSize;
import com.google.common.collect.testing.features.MapFeature;
import net.openhft.collect.testing.map.AbstractCharShortMapTester;
import net.openhft.function.*;

import java.util.*;

import static com.google.common.collect.testing.features.CollectionSize.ONE;
import static com.google.common.collect.testing.features.CollectionSize.ZERO;
import static com.google.common.collect.testing.features.MapFeature.SUPPORTS_REMOVE;


/**
 * @see net.openhft.collect.testing.testers.CharCollectionRemoveIfTester
 */
public class CharShortMapRemoveIfTester/*<>*/ extends AbstractCharShortMapTester/*<>*/ {
    /* with No|Some removed */
    @MapFeature.Require(SUPPORTS_REMOVE)
    public void testRemoveIf_iteratorOrder_noRemoved() {
        /* if Some removed */remove();/* endif */
        List<Map.Entry<Character, Short>> iteratorElements = new ArrayList<>();
        for (Map.Entry<Character, Short> element : getMap().entrySet()) { // uses iterator()
            iteratorElements.add(element);
        }
        List<Map.Entry<Character, Short>> removeIfElements = new ArrayList<>();
        /*f*/CharShortPredicate collectAndReturnFalse = (k, v) -> {
            removeIfElements.add(entry(k, v));
            return false;
        };
        getMap().removeIf(collectAndReturnFalse);
        assertEquals("removeIf() order is different from iteration order",
                iteratorElements, removeIfElements);
        expectContents(noRemoved(getOrderedElements()));
    }

    /* if Some removed */@CollectionSize.Require(absent = ZERO)/* endif */
    @MapFeature.Require(SUPPORTS_REMOVE)
    public void testRemoveIf_removeAll_noRemoved() {
        /* if Some removed */remove();/* endif */
        /*f*/CharShortPredicate alwaysTrue = (k, v) -> true;
        boolean shouldRemove = !getMap().isEmpty();
        assertEquals(shouldRemove, getMap().removeIf(alwaysTrue));
        expectContents();
        expectMissing(samples.e0, samples.e1, samples.e2);
    }

    /* if Some removed */@CollectionSize.Require(absent = ZERO)/* endif */
    @MapFeature.Require(SUPPORTS_REMOVE)
    public void testRemoveIf_removeNothing_noRemoved() {
        /* if Some removed */remove();/* endif */
        /*f*/CharShortPredicate alwaysFalse = (k, v) -> false;
        assertFalse("removeIf() which don't remove anything should return false",
                getMap().removeIf(alwaysFalse));
        expectContents(noRemoved(getOrderedElements()));
    }

    @CollectionSize.Require(absent = {ZERO/* if Some removed */, ONE/* endif */})
    @MapFeature.Require(SUPPORTS_REMOVE)
    public void testRemoveIf_removeFirst_noRemoved() {
        /* if Some removed */remove();/* endif */
        Iterator<Map.Entry<Character, Short>> iterator = getMap().entrySet().iterator();
        Map.Entry<Character, Short> firstElement = iterator.next();
        ArrayList<Map.Entry<Character, Short>> restElements = Lists.newArrayList(iterator);
        /*f*/CharShortPredicate removeFirst = new /*f*/CharShortPredicate/*<>*/() {
            private boolean first = true;
            @Override
            public boolean test(char key, short value) {
                if (first) {
                    first = false;
                    return true;
                }
                return false;
            }
        };
        assertTrue(getMap().removeIf(removeFirst));
        expectMissing(firstElement);
        expectContents(restElements);
    }
    /* endwith */

    @MapFeature.Require(absent = SUPPORTS_REMOVE)
    public void testRemoveIf_unsupported() {
        try {
            /*f*/CharShortPredicate alwaysTrue = (k, v) -> true;
            getMap().removeIf(alwaysTrue);
            fail("removeIf() should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
            // expected
        }
    }
}
