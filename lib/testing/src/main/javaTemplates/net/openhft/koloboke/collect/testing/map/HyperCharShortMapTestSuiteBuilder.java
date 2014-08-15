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

package net.openhft.koloboke.collect.testing.map;

import com.google.common.collect.testing.*;
import com.google.common.collect.testing.features.*;
import com.google.common.collect.testing.testers.CollectionClearTester;
import com.google.common.collect.testing.testers.MapClearTester;
import junit.framework.*;
import net.openhft.koloboke.collect.testing.Mutability;
import net.openhft.koloboke.collect.map.CharShortMapFactory;

import java.lang.reflect.Method;
import java.util.*;


public class HyperCharShortMapTestSuiteBuilder/*<>*/ {

    /* define ps */
    // if obj key obj value //K, V, // elif obj key //K, // elif obj value //V, // endif //
    /* enddefine */

    private List<CharShortMapFactory</*ps*/?>> factories;
    private List<SampleElements<? extends Character>> keySamples;
    private List<SampleElements<? extends Short>> valueSamples;
    private List<Feature<?>> specialFeatures = new ArrayList<Feature<?>>();
    private List<Method> suppressing = new ArrayList<Method>();

    public HyperCharShortMapTestSuiteBuilder/*<>*/ setFactories(
            CharShortMapFactory</*ps*/?>... factories) {
        this.factories = Arrays.asList(factories);
        return this;
    }

    public HyperCharShortMapTestSuiteBuilder/*<>*/ setFactories(
            List<CharShortMapFactory</*ps*/?>> factories) {
        this.factories = factories;
        return this;
    }

    public HyperCharShortMapTestSuiteBuilder/*<>*/ setKeySamples(
            SampleElements<? extends Character>... keySamples) {
        this.keySamples = Arrays.asList(keySamples);
        return this;
    }

    public HyperCharShortMapTestSuiteBuilder/*<>*/ setKeySamples(
            List<SampleElements<? extends Character>> keySamples) {
        this.keySamples = keySamples;
        return this;
    }

    public HyperCharShortMapTestSuiteBuilder/*<>*/ setValueSamples(
            SampleElements<? extends Short>... valueSamples) {
        this.valueSamples = Arrays.asList(valueSamples);
        return this;
    }

    public HyperCharShortMapTestSuiteBuilder/*<>*/ setValueSamples(
            List<SampleElements<? extends Short>> valueSamples) {
        this.valueSamples = valueSamples;
        return this;
    }

    public HyperCharShortMapTestSuiteBuilder/*<>*/ withSpecialFeatures(
            List<Feature<?>> specialFeatures) {
        this.specialFeatures.addAll(specialFeatures);
        return this;
    }

    public HyperCharShortMapTestSuiteBuilder/*<>*/ withSpecialFeatures(
            Feature<?>... specialFeatures) {
        this.specialFeatures.addAll(Arrays.asList(specialFeatures));
        return this;
    }

    public HyperCharShortMapTestSuiteBuilder/*<>*/ suppressing(Method... methods) {
        suppressing.addAll(Arrays.asList(methods));
        return this;
    }

    public TestSuite create() {
        TestSuite suite = new TestSuite(subSuiteName(factories, keySamples, valueSamples, ""));
        for (CharShortMapFactory</*ps*/?> factory : factories) {
            TestCharShortMapGenerator.Builder builder = new TestCharShortMapGenerator.Builder()
                    .setFactory(factory);
            for (SampleElements<? extends Character> keys : keySamples) {
                for (SampleElements<? extends Short> values : valueSamples) {
                    builder.setKeys(keys).setValues(values);
                    for (Mutability mutability : Mutability.values()) {
                        FeatureSpecificTestSuiteBuilder b =
                                forEachTestSuiteBuilder(
                                        new GuavaCharShortMapTestSuiteBuilder/*<>*/()
                                                .usingGenerator(builder.withMutability(mutability)))
                                .named(subSuiteName(factory, keys.asList(), values.asList(),
                                        mutability.toString()))
                                .withFeatures(mutability.mapFeatures)
                                .withFeatures(mutability.mapViewFeatures);
                        if (mutability == Mutability.UPDATABLE) {
                            try {
                                b.suppressing(MapClearTester.class
                                                .getDeclaredMethod("testClear_unsupported"),
                                        CollectionClearTester.class
                                                .getDeclaredMethod("testClear_unsupported"));
                            } catch (NoSuchMethodException e) {
                                throw new AssertionError(e);
                            }
                        }
                        Test mutableTests = b.createTestSuite();
                        suite.addTest(mutableTests);
                    }
                }
            }
        }
        return suite;
    }

    private MapTestSuiteBuilder<Character, Short> forEachTestSuiteBuilder(
            MapTestSuiteBuilder<Character, Short> builder) {
        return builder.suppressing(suppressing)
                .withFeatures(MapFeature.FAILS_FAST_ON_CONCURRENT_MODIFICATION)
                .withFeatures(MapFeature.ALLOWS_NULL_ENTRY_QUERIES)
                .withFeatures(CollectionSize.ANY)
                .withFeatures(specialFeatures);
    }

    private String subSuiteName(Object factory, Object keys, Object values, String infix) {
        return "Tests of " + infix + " CharShortMaps. Key samples: " + keys + ". " +
                "Value samples: " + values + ". Factory: " + factory + ". " +
                "Special features: " + specialFeatures + ".";
    }
}
