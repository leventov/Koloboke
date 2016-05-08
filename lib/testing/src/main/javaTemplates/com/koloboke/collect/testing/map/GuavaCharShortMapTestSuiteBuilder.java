/* with
 char|byte|short|int|long|float|double|obj key
 short|byte|char|int|long|float|double|obj value
*/
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

package com.koloboke.collect.testing.map;

import com.google.common.collect.testing.*;
import com.google.common.collect.testing.features.*;
import com.google.common.testing.SerializableTester;
import junit.framework.TestSuite;
import com.koloboke.collect.testing.GuavaShortCollectionTestSuiteBuilder;
import com.koloboke.collect.testing.map.testers.*;
import com.koloboke.collect.testing.set.GuavaCharSetTestSuiteBuilder;
import com.koloboke.collect.testing.set.GuavaObjSetTestSuiteBuilder;

import java.util.*;


public class GuavaCharShortMapTestSuiteBuilder/*<>*/ extends MapTestSuiteBuilder<Character, Short> {

    @Override
    protected List<Class<? extends AbstractTester>> getTesters() {
        List<Class<? extends AbstractTester>> testers = new ArrayList<>(super.getTesters());
        testers.add(CharShortMapPutTester.class);
        /* if !(obj key) */testers.add(CharShortMapContainsKeyTester.class);/* endif */
        /* if !(obj value) */testers.add(CharShortMapContainsValueTester.class);/* endif */
        /* if !(obj key obj value) */testers.add(CharShortMapGetTester.class);/* endif */
        testers.add(CharShortMapCursorTester.class);
        testers.add(CharShortMapRemoveIfTester.class);
        testers.add(CharShortMapMergeTester.class);
        return testers;
    }

    @Override
    protected MapTestSuiteBuilder<Character, Short> usingGenerator(
            TestMapGenerator<Character, Short> subjectGenerator) {
        return super.usingGenerator(subjectGenerator);
    }

    @Override
    protected SetTestSuiteBuilder<Map.Entry<Character, Short>> createDerivedEntrySetSuite(
            TestSetGenerator<Map.Entry<Character, Short>> entrySetGenerator) {
        return new GuavaObjSetTestSuiteBuilder<Map.Entry<Character, Short>>()
                .usingGenerator(entrySetGenerator);
    }

    @Override
    protected SetTestSuiteBuilder<Character> createDerivedKeySetSuite(
            TestSetGenerator<Character> keySetGenerator) {
        return new GuavaCharSetTestSuiteBuilder/* if obj key //<K>// endif */()
                .usingGenerator(keySetGenerator);
    }

    @Override
    protected CollectionTestSuiteBuilder<Short> createDerivedValueCollectionSuite(
            TestCollectionGenerator<Short> valueCollectionGenerator) {
        return new GuavaShortCollectionTestSuiteBuilder/* if obj value //<V>// endif */()
                .usingGenerator(valueCollectionGenerator);
    }
}
