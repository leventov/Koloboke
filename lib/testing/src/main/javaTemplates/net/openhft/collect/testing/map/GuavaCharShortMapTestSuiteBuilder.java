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

package net.openhft.collect.testing.map;

import com.google.common.collect.testing.*;
import com.google.common.collect.testing.features.*;
import com.google.common.testing.SerializableTester;
import junit.framework.TestSuite;
import net.openhft.collect.testing.GuavaShortCollectionTestSuiteBuilder;
import net.openhft.collect.testing.map.testers.*;
import net.openhft.collect.testing.set.GuavaCharSetTestSuiteBuilder;
import net.openhft.collect.testing.set.GuavaObjSetTestSuiteBuilder;

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

    // All the following stuff is just copied from MapTestSuiteBuilder
    // TODO simplify when Guava issue 1801 will be fixed

    @Override
    protected List<TestSuite> createDerivedSuites(
            FeatureSpecificTestSuiteBuilder<
                    ?,
                    ? extends OneSizeTestContainerGenerator<
                            Map<Character, Short>,
                            Map.Entry<Character, Short>>>
                    parentBuilder) {
        List<TestSuite> derivedSuites = new ArrayList<>();

        if (parentBuilder.getFeatures().contains(CollectionFeature.SERIALIZABLE)) {
            derivedSuites.add(MapTestSuiteBuilder.using(
                    new ReserializedMapGenerator<>(parentBuilder.getSubjectGenerator()))
                    .withFeatures(computeReserializedMapFeatures(parentBuilder.getFeatures()))
                    .named(parentBuilder.getName() + " reserialized")
                    .suppressing(parentBuilder.getSuppressedTests())
                    .createTestSuite());
        }

        derivedSuites.add(
                new GuavaObjSetTestSuiteBuilder<Map.Entry<Character, Short>>().usingGenerator(
                        new DerivedCollectionGenerators.MapEntrySetGenerator<>(
                                parentBuilder.getSubjectGenerator()))
                        .withFeatures(computeEntrySetFeatures(parentBuilder.getFeatures()))
                        .named(parentBuilder.getName() + " entrySet")
                        .suppressing(parentBuilder.getSuppressedTests())
                        .createTestSuite());

        /* with key view */
        derivedSuites.add(new GuavaCharSetTestSuiteBuilder/*<>*/().usingGenerator(
                keySetGenerator(parentBuilder.getSubjectGenerator()))
                .withFeatures(computeKeySetFeatures(parentBuilder.getFeatures()))
                .named(parentBuilder.getName() + " keys")
                .suppressing(parentBuilder.getSuppressedTests())
                .createTestSuite());
        /* endwith */

        /* with value view */
        derivedSuites.add(new GuavaShortCollectionTestSuiteBuilder/*<>*/().usingGenerator(
                new DerivedCollectionGenerators.MapValueCollectionGenerator<>(
                        parentBuilder.getSubjectGenerator()))
                .named(parentBuilder.getName() + " values")
                .withFeatures(computeValuesCollectionFeatures(
                        parentBuilder.getFeatures()))
                .suppressing(parentBuilder.getSuppressedTests())
                .createTestSuite());
        /* endwith */

        return derivedSuites;
    }

    private static Set<Feature<?>> computeReserializedMapFeatures(
            Set<Feature<?>> mapFeatures) {
        Set<Feature<?>> derivedFeatures = Helpers.copyToSet(mapFeatures);
        derivedFeatures.remove(CollectionFeature.SERIALIZABLE);
        derivedFeatures.remove(CollectionFeature.SERIALIZABLE_INCLUDING_VIEWS);
        return derivedFeatures;
    }

    private static Set<Feature<?>> computeEntrySetFeatures(
            Set<Feature<?>> mapFeatures) {
        Set<Feature<?>> entrySetFeatures =
                computeCommonDerivedCollectionFeatures(mapFeatures);
        if (mapFeatures.contains(MapFeature.ALLOWS_NULL_ENTRY_QUERIES)) {
            entrySetFeatures.add(CollectionFeature.ALLOWS_NULL_QUERIES);
        }
        return entrySetFeatures;
    }

    private static Set<Feature<?>> computeKeySetFeatures(
            Set<Feature<?>> mapFeatures) {
        Set<Feature<?>> keySetFeatures =
                computeCommonDerivedCollectionFeatures(mapFeatures);
        keySetFeatures.add(CollectionFeature.SUBSET_VIEW);
        if (mapFeatures.contains(MapFeature.ALLOWS_NULL_KEYS)) {
            keySetFeatures.add(CollectionFeature.ALLOWS_NULL_VALUES);
        } else if (mapFeatures.contains(MapFeature.ALLOWS_NULL_KEY_QUERIES)) {
            keySetFeatures.add(CollectionFeature.ALLOWS_NULL_QUERIES);
        }

        return keySetFeatures;
    }

    private static Set<Feature<?>> computeValuesCollectionFeatures(
            Set<Feature<?>> mapFeatures) {
        Set<Feature<?>> valuesCollectionFeatures =
                computeCommonDerivedCollectionFeatures(mapFeatures);
        if (mapFeatures.contains(MapFeature.ALLOWS_NULL_VALUE_QUERIES)) {
            valuesCollectionFeatures.add(CollectionFeature.ALLOWS_NULL_QUERIES);
        }
        if (mapFeatures.contains(MapFeature.ALLOWS_NULL_VALUES)) {
            valuesCollectionFeatures.add(CollectionFeature.ALLOWS_NULL_VALUES);
        }

        return valuesCollectionFeatures;
    }

    public static Set<Feature<?>> computeCommonDerivedCollectionFeatures(
            Set<Feature<?>> mapFeatures) {
        mapFeatures = new HashSet<>(mapFeatures);
        Set<Feature<?>> derivedFeatures = new HashSet<>();
        mapFeatures.remove(CollectionFeature.SERIALIZABLE);
        if (mapFeatures.remove(CollectionFeature.SERIALIZABLE_INCLUDING_VIEWS)) {
            derivedFeatures.add(CollectionFeature.SERIALIZABLE);
        }
        if (mapFeatures.contains(MapFeature.SUPPORTS_REMOVE)) {
            derivedFeatures.add(CollectionFeature.SUPPORTS_REMOVE);
        }
        if (mapFeatures.contains(MapFeature.REJECTS_DUPLICATES_AT_CREATION)) {
            derivedFeatures.add(CollectionFeature.REJECTS_DUPLICATES_AT_CREATION);
        }
        if (mapFeatures.contains(MapFeature.FAILS_FAST_ON_CONCURRENT_MODIFICATION)) {
            derivedFeatures.add(CollectionFeature.FAILS_FAST_ON_CONCURRENT_MODIFICATION);
        }
        // add the intersection of CollectionFeature.values() and mapFeatures
        for (CollectionFeature feature : CollectionFeature.values()) {
            if (mapFeatures.contains(feature)) {
                derivedFeatures.add(feature);
            }
        }
        // add the intersection of CollectionSize.values() and mapFeatures
        for (CollectionSize size : CollectionSize.values()) {
            if (mapFeatures.contains(size)) {
                derivedFeatures.add(size);
            }
        }
        return derivedFeatures;
    }

    static <K, V> TestSetGenerator<K> keySetGenerator(
            OneSizeTestContainerGenerator<Map<K, V>, Map.Entry<K, V>> mapGenerator) {
        TestContainerGenerator<Map<K, V>, Map.Entry<K, V>> generator =
                mapGenerator.getInnerGenerator();
        if (generator instanceof TestSortedMapGenerator &&
                ((TestSortedMapGenerator<K, V>) generator).create().keySet() instanceof SortedSet) {
            return new DerivedCollectionGenerators.MapSortedKeySetGenerator<>(mapGenerator);
        } else {
            return new DerivedCollectionGenerators.MapKeySetGenerator<>(mapGenerator);
        }
    }

    private static class ReserializedMapGenerator<K, V>
            implements TestMapGenerator<K, V> {
        private final OneSizeTestContainerGenerator<Map<K, V>, Map.Entry<K, V>>
                mapGenerator;

        public ReserializedMapGenerator(
                OneSizeTestContainerGenerator<
                        Map<K, V>, Map.Entry<K, V>> mapGenerator) {
            this.mapGenerator = mapGenerator;
        }

        @Override
        public SampleElements<Map.Entry<K, V>> samples() {
            return mapGenerator.samples();
        }

        @Override
        public Map.Entry<K, V>[] createArray(int length) {
            return mapGenerator.createArray(length);
        }

        @Override
        public Iterable<Map.Entry<K, V>> order(
                List<Map.Entry<K, V>> insertionOrder) {
            return mapGenerator.order(insertionOrder);
        }

        @Override
        public Map<K, V> create(Object... elements) {
            return SerializableTester.reserialize(mapGenerator.create(elements));
        }

        @Override
        public K[] createKeyArray(int length) {
            return ((TestMapGenerator<K, V>) mapGenerator.getInnerGenerator())
                    .createKeyArray(length);
        }

        @Override
        public V[] createValueArray(int length) {
            return ((TestMapGenerator<K, V>) mapGenerator.getInnerGenerator())
                    .createValueArray(length);
        }
    }
}
