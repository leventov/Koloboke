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

import com.google.common.collect.testing.SampleElements;
import com.google.common.collect.testing.TestMapGenerator;
import com.google.common.collect.testing.features.CollectionSize;
import com.google.common.collect.testing.features.MapFeature;
import com.koloboke.collect.testing.map.GuavaObjIntMapTestSuiteBuilder;
import junit.framework.TestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.AllTests;

import java.util.List;
import java.util.Map;

import static com.koloboke.collect.testing.Mutability.MUTABLE;


@RunWith(AllTests.class)
public final class StringIntMapTest {

    public static TestSuite suite() {
        return new GuavaObjIntMapTestSuiteBuilder<String>()
                .usingGenerator(new StringIntMapTestGenerator())
                .named(StringIntMapTest.class.getName())
                .withFeatures(MapFeature.FAILS_FAST_ON_CONCURRENT_MODIFICATION)
                .withFeatures(MapFeature.ALLOWS_NULL_ENTRY_QUERIES)
                .withFeatures(CollectionSize.ANY)
                .withFeatures(MUTABLE.mapFeatures)
                .withFeatures(MUTABLE.mapViewFeatures)
                .withFeatures(MapFeature.RESTRICTS_KEYS)
                .withFeatures(MapFeature.RESTRICTS_VALUES)
                .createTestSuite();
    }

    static class StringIntMapTestGenerator implements TestMapGenerator<String, Integer> {

        @Override
        public String[] createKeyArray(int length) {
            return new String[length];
        }

        @Override
        public Integer[] createValueArray(int length) {
            return new Integer[length];
        }

        @Override
        public SampleElements<Map.Entry<String, Integer>> samples() {
            return SampleElements.mapEntries(sampleKeys, new SampleElements.Ints());
        }

        @Override
        public Map<String, Integer> create(Object... elements) {
            StringIntMap map = StringIntMap.withExpectedSize(elements.length);
            for (Object o : elements) {
                @SuppressWarnings("unchecked")
                Map.Entry<String, Integer> e = (Map.Entry<String, Integer>) o;
                map.put(e.getKey(), (int) e.getValue());
            }
            return map;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Map.Entry<String, Integer>[] createArray(int length) {
            return new Map.Entry[length];
        }

        @Override
        public Iterable<Map.Entry<String, Integer>> order(
                List<Map.Entry<String, Integer>> insertionOrder) {
            return insertionOrder;
        }

        private static final SampleElements<String> sampleKeys =
                new SampleElements<String>("AaAaAa", "BBAaAa", "AaBBAa", "AaAaBB", "BBBBAa");

        static {
            for (String sampleKey : sampleKeys) {
                assert sampleKey.hashCode() == sampleKeys.e0().hashCode();
            }
        }
    }
}
