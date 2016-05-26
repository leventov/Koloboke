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

package com.koloboke.compile;

import com.google.common.collect.Multiset;
import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;
import com.google.common.collect.testing.google.MultisetTestSuiteBuilder;
import com.google.common.collect.testing.google.TestStringMultisetGenerator;
import junit.framework.TestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.AllTests;


@RunWith(AllTests.class)
public final class KolobokeMapBackedMultisetTest {
    public static TestSuite suite() {
        TestStringMultisetGenerator generator = new TestStringMultisetGenerator() {
            @Override
            protected Multiset<String> create(String[] elements) {
                return KolobokeMapBackedMultiset.withElements(elements);
            }
        };
        return MultisetTestSuiteBuilder
                .using(generator)
                .withFeatures(CollectionSize.ANY,
                        CollectionFeature.FAILS_FAST_ON_CONCURRENT_MODIFICATION,
                        CollectionFeature.ALLOWS_NULL_VALUES,
                        CollectionFeature.GENERAL_PURPOSE)
                .named(KolobokeMapBackedMultisetTest.class.getSimpleName())
                .createTestSuite();
    }
}
