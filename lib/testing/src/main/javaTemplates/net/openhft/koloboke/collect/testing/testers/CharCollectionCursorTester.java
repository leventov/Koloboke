/* with char|byte|short|int|long|float|double|obj elem */
/*
 * Copyright 2014 the original author or authors.
 * Copyright (C) 2008 The Guava Authors
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

import com.google.common.collect.testing.Helpers;
import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;
import net.openhft.koloboke.collect.CharCursor;
import net.openhft.koloboke.collect.testing.*;
import net.openhft.koloboke.function.Consumer;

import java.util.*;

import static com.google.common.collect.testing.features.CollectionFeature.KNOWN_ORDER;
import static com.google.common.collect.testing.features.CollectionFeature.SUPPORTS_ITERATOR_REMOVE;
import static com.google.common.collect.testing.features.CollectionFeature.SUPPORTS_REMOVE;
import static com.google.common.collect.testing.features.CollectionSize.ZERO;


/**
 * @see com.google.common.collect.testing.testers.CollectionIteratorTester
 */
public class CharCollectionCursorTester/*<>*/ extends AbstractCharCollectionTester/*<>*/ {

    /* with No|Some removed */
    /* if Some removed */
    @CollectionFeature.Require(SUPPORTS_REMOVE)
    @CollectionSize.Require(absent = ZERO)
    /* endif */
    public void testIterator_noRemoved() {
        /* if Some removed */remove();/* endif */
        List<Character> cursorElements = new ArrayList<>();
        for (CharCursor/*<>*/ cur = c().cursor(); cur.moveNext();) {
            cursorElements.add(cur.elem());
        }
        Helpers.assertEqualIgnoringOrder(
                noRemoved(Arrays.asList(createSamplesArray())), cursorElements);
    }

    @CollectionFeature.Require({KNOWN_ORDER/* if Some removed */, SUPPORTS_REMOVE/* endif */})
    /* if Some removed */@CollectionSize.Require(absent = ZERO)/* endif */
    public void testIterationOrdering_noRemoved() {
        /* if Some removed */remove();/* endif */
        List<Character> cursorElements = new ArrayList<>();
        for (CharCursor/*<>*/ cur = c().cursor(); cur.moveNext();) {
            cursorElements.add(cur.elem());
        }
        List<Character> expected = Helpers.copyToList(noRemoved(getOrderedElements()));
        assertEquals("Different ordered iteration", expected, cursorElements);
    }

    @CollectionFeature.Require({KNOWN_ORDER, SUPPORTS_ITERATOR_REMOVE
            /* if Some removed */, SUPPORTS_REMOVE/* endif */})
    /* if Some removed */@CollectionSize.Require(absent = ZERO)/* endif */
    public void testIterator_knownOrderRemoveSupported_noRemoved() {
        runIteratorTest_noRemoved(CursorFeature.MODIFIABLE, CursorKnownOrder.KNOWN_ORDER,
                noRemoved(getOrderedElements()));
    }

    /* if No removed */
    @CollectionFeature.Require(value = KNOWN_ORDER, absent = SUPPORTS_ITERATOR_REMOVE)
    public void testIterator_knownOrderRemoveUnsupported() {
        runIteratorTest_noRemoved(CursorFeature.UNMODIFIABLE, CursorKnownOrder.KNOWN_ORDER,
                getOrderedElements());
    }
    /* endif */

    @CollectionFeature.Require(absent = KNOWN_ORDER,
            value = {SUPPORTS_ITERATOR_REMOVE/* if Some removed */, SUPPORTS_REMOVE/* endif */})
    /* if Some removed */@CollectionSize.Require(absent = ZERO)/* endif */
    public void testIterator_unknownOrderRemoveSupported_noRemoved() {
        runIteratorTest_noRemoved(CursorFeature.MODIFIABLE, CursorKnownOrder.UNKNOWN_ORDER,
                noRemoved(getSampleElements()));
    }

    /* if No removed */
    @CollectionFeature.Require(absent = {KNOWN_ORDER, SUPPORTS_ITERATOR_REMOVE})
    public void testIterator_unknownOrderRemoveUnsupported() {
        runIteratorTest_noRemoved(CursorFeature.UNMODIFIABLE, CursorKnownOrder.UNKNOWN_ORDER,
                getSampleElements());
    }
    /* endif */

    private void runIteratorTest_noRemoved(Set<CursorFeature> features,
            CursorKnownOrder knownOrder, Iterable<Character> elements) {
        /* if Some removed */remove();/* endif */
        int steps = Math.min(3 + c().size(), 5);
        new CursorTester<Character, CharCursor/*<>*/>(steps, features, elements, knownOrder) {
            @Override
            protected CharCursor/*<>*/ newTargetCursor() {
                resetCollection();
                /* if Some removed */remove();/* endif */
                return c().cursor();
            }

            @Override
            protected Character current(CharCursor/*<>*/ cursor) {
                return cursor.elem();
            }

            @Override
            protected void forEachForward(
                    CharCursor/*<>*/ cursor, Consumer<? super Character> action) {
                cursor.forEachForward(action::accept);
            }

            @Override
            protected void verify(List<Character> elements) {
                expectContents(elements);
            }
        }.test();
    }
    /* endwith */

    public void testIteratorNoSuchElementException() {
        CharCursor/*<>*/ cur = c().cursor();
        while (cur.moveNext());
        try {
            cur.elem();
            fail("cursor.elem() should throw IllegalStateException");
        } catch (IllegalStateException expected) {}
    }
}
