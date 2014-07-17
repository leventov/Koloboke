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

package net.openhft.collect.testing.testers;

import com.google.common.collect.Lists;
import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;
import net.openhft.collect.testing.AbstractCharCollectionTester;
import net.openhft.function.*;

import java.util.*;

import static com.google.common.collect.testing.features.CollectionFeature.SUPPORTS_REMOVE;
import static com.google.common.collect.testing.features.CollectionSize.ONE;
import static com.google.common.collect.testing.features.CollectionSize.ZERO;


public class CharCollectionRemoveIfTester/*<>*/ extends AbstractCharCollectionTester/*<>*/ {
    /* with No|Some removed */
    @CollectionFeature.Require(SUPPORTS_REMOVE)
    public void testRemoveIf_iteratorOrder_noRemoved() {
        /* if Some removed */remove();/* endif */
        List<Character> iteratorElements = new ArrayList<>();
        for (Character element : c()) { // uses iterator()
            iteratorElements.add(element);
        }
        List<Character> removeIfElements = new ArrayList<>();
        /*f*/CharPredicate collectAndReturnFalse = c -> {
            removeIfElements.add(c);
            return false;
        };
        c().removeIf(collectAndReturnFalse);
        assertEquals("removeIf() order is different from iteration order",
                iteratorElements, removeIfElements);
        expectContents(noRemoved(getOrderedElements()));
    }

    /* if Some removed */@CollectionSize.Require(absent = ZERO)/* endif */
    @CollectionFeature.Require(SUPPORTS_REMOVE)
    public void testRemoveIf_removeAll_noRemoved() {
        /* if Some removed */remove();/* endif */
        /*f*/CharPredicate alwaysTrue = c -> true;
        boolean shouldRemove = !c().isEmpty();
        assertEquals(shouldRemove, c().removeIf(alwaysTrue));
        expectContents();
        expectMissing(samples.e0, samples.e1, samples.e2);
    }

    /* if Some removed */@CollectionSize.Require(absent = ZERO)/* endif */
    @CollectionFeature.Require(SUPPORTS_REMOVE)
    public void testRemoveIf_removeNothing_noRemoved() {
        /* if Some removed */remove();/* endif */
        /*f*/CharPredicate alwaysFalse = c -> false;
        assertFalse("removeIf() which don't remove anything should return false",
                c().removeIf(alwaysFalse));
        expectContents(noRemoved(getOrderedElements()));
    }

    @CollectionSize.Require(absent = {ZERO/* if Some removed */, ONE/* endif */})
    @CollectionFeature.Require(SUPPORTS_REMOVE)
    public void testRemoveIf_removeFirst_noRemoved() {
        /* if Some removed */remove();/* endif */
        Iterator<Character> iterator = c().iterator();
        Character firstElement = iterator.next();
        ArrayList<Character> restElements = Lists.newArrayList(iterator);
        /*f*/CharPredicate removeFirst = new /*f*/CharPredicate/*<>*/() {
            private boolean first = true;
            @Override
            public boolean test(char value) {
                if (first) {
                    first = false;
                    return true;
                }
                return false;
            }
        };
        assertTrue(c().removeIf(removeFirst));
        expectMissing(firstElement);
        expectContents(restElements);
    }
    /* endwith */

    @CollectionFeature.Require(absent = SUPPORTS_REMOVE)
    public void testRemoveIf_unsupported() {
        try {
            /*f*/CharPredicate alwaysTrue = c -> true;
            c().removeIf(alwaysTrue);
            fail("removeIf() should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
            // expected
        }
    }
}
