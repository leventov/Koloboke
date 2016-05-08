/* with
 char|byte|short|int|long|float|double|obj key
 short|byte|char|int|long|float|double|obj value
*/
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


package com.koloboke.collect.testing.map.testers;

import com.google.common.collect.testing.Helpers;
import com.google.common.collect.testing.features.*;
import com.koloboke.collect.map.CharShortCursor;
import com.koloboke.collect.testing.*;
import com.koloboke.collect.testing.map.AbstractCharShortMapTester;
import com.koloboke.collect.testing.map.MapCursorTester;
import com.koloboke.function.Consumer;

import java.util.*;

import static com.google.common.collect.testing.features.CollectionFeature.KNOWN_ORDER;
import static com.google.common.collect.testing.features.CollectionSize.ZERO;
import static com.google.common.collect.testing.features.MapFeature.SUPPORTS_REMOVE;


/**
 * @see com.koloboke.collect.testing.testers.CharCollectionCursorTester
 */
public class CharShortMapCursorTester/*<>*/ extends AbstractCharShortMapTester/*<>*/ {

    /* with No|Some removed */
    /* if Some removed */
    @MapFeature.Require(SUPPORTS_REMOVE)
    @CollectionSize.Require(absent = ZERO)
    /* endif */
    public void testIterator_noRemoved() {
        /* if Some removed */remove();/* endif */
        List<Map.Entry<Character, Short>> cursorElements = new ArrayList<>();
        for (CharShortCursor/*<>*/ cur = getMap().cursor(); cur.moveNext();) {
            cursorElements.add(entry(cur.key(), cur.value()));
        }
        Helpers.assertEqualIgnoringOrder(
                noRemoved(Arrays.asList(createSamplesArray())), cursorElements);
    }

    @CollectionFeature.Require(KNOWN_ORDER)
    /* if Some removed */
    @MapFeature.Require(SUPPORTS_REMOVE)
    @CollectionSize.Require(absent = ZERO)
    /* endif */
    public void testIterationOrdering_noRemoved() {
        /* if Some removed */remove();/* endif */
        List<Map.Entry<Character, Short>> cursorElements = new ArrayList<>();
        for (CharShortCursor/*<>*/ cur = getMap().cursor(); cur.moveNext();) {
            cursorElements.add(entry(cur.key(), cur.value()));
        }
        List<Map.Entry<Character, Short>> expected =
                Helpers.copyToList(noRemoved(getOrderedElements()));
        assertEquals("Different ordered iteration", expected, cursorElements);
    }

    @CollectionFeature.Require(KNOWN_ORDER)
    @MapFeature.Require(SUPPORTS_REMOVE)
    /* if Some removed */@CollectionSize.Require(absent = ZERO)/* endif */
    public void testIterator_knownOrderRemoveSupported_noRemoved() {
        runIteratorTest_noRemoved(CursorFeature.MODIFIABLE, CursorKnownOrder.KNOWN_ORDER,
                noRemoved(getOrderedElements()));
    }

    /* if No removed */
    @CollectionFeature.Require(KNOWN_ORDER)
    @MapFeature.Require(absent = SUPPORTS_REMOVE)
    public void testIterator_knownOrderRemoveUnsupported() {
        runIteratorTest_noRemoved(CursorFeature.UNMODIFIABLE, CursorKnownOrder.KNOWN_ORDER,
                getOrderedElements());
    }
    /* endif */

    @CollectionFeature.Require(absent = KNOWN_ORDER)
    @MapFeature.Require(SUPPORTS_REMOVE)
    /* if Some removed */@CollectionSize.Require(absent = ZERO)/* endif */
    public void testIterator_unknownOrderRemoveSupported_noRemoved() {
        runIteratorTest_noRemoved(CursorFeature.MODIFIABLE, CursorKnownOrder.UNKNOWN_ORDER,
                noRemoved(getSampleElements()));
    }

    /* if No removed */
    @CollectionFeature.Require(absent = KNOWN_ORDER)
    @MapFeature.Require(absent = SUPPORTS_REMOVE)
    public void testIterator_unknownOrderRemoveUnsupported() {
        runIteratorTest_noRemoved(CursorFeature.UNMODIFIABLE, CursorKnownOrder.UNKNOWN_ORDER,
                noRemoved(getSampleElements()));
    }
    /* endif */

    private void runIteratorTest_noRemoved(Set<CursorFeature> features,
            CursorKnownOrder knownOrder, Iterable<Map.Entry<Character, Short>> elements) {
        /* if Some removed */remove();/* endif */
        int steps = Math.min(3 + getMap().size(), 5);
        new MapCursorTester<Character, Short, CharShortCursor/*<>*/>(
                steps, features, elements, knownOrder) {
            @Override
            protected CharShortCursor/*<>*/ newTargetCursor() {
                resetMap();
                /* if Some removed */remove();/* endif */
                return getMap().cursor();
            }

            @Override
            protected Map.Entry<Character, Short> current(CharShortCursor/*<>*/ cursor) {
                return entry(cursor.key(), cursor.value());
            }

            @Override
            protected void forEachForward(CharShortCursor/*<>*/ cursor,
                    Consumer<? super Map.Entry<Character, Short>> action) {
                cursor.forEachForward((k, v) -> action.accept(entry(k, v)));
            }

            @Override
            protected void verify(List<Map.Entry<Character, Short>> elements) {
                expectContents(elements);
            }
        }.test();
    }
    /* endwith */

    public void testIteratorNoSuchElementException() {
        CharShortCursor/*<>*/ cur = getMap().cursor();
        while (cur.moveNext());
        try {
            cur.key();
            fail("cursor.elem() should throw IllegalStateException");
        } catch (IllegalStateException expected) {}
    }
}
