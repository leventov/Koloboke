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

package net.openhft.collect.map;

import com.google.common.collect.testing.*;
import com.google.common.collect.testing.features.*;
import junit.framework.*;

import java.lang.reflect.Method;
import java.util.*;

import static com.google.common.collect.testing.MapTestSuiteBuilder.*;


public class CharShortMapTestSuiteBuilder/*<>*/ {

    private List<CharShortMapFactory/*<>*/> factories;
    private List<SampleElements<? extends Character>> keySamples;
    private List<SampleElements<? extends Short>> valueSamples;
    private List<Feature<?>> specialFeatures = new ArrayList<Feature<?>>();
    private List<Method> suppressing = new ArrayList<Method>();

    public CharShortMapTestSuiteBuilder/*<>*/ setFactories(CharShortMapFactory/*<>*/... factories) {
        this.factories = Arrays.asList(factories);
        return this;
    }

    public CharShortMapTestSuiteBuilder/*<>*/ setFactories(
            List<CharShortMapFactory/*<>*/> factories) {
        this.factories = factories;
        return this;
    }

    public CharShortMapTestSuiteBuilder/*<>*/ setKeySamples(
            SampleElements<? extends Character>... keySamples) {
        this.keySamples = Arrays.asList(keySamples);
        return this;
    }

    public CharShortMapTestSuiteBuilder/*<>*/ setKeySamples(
            List<SampleElements<? extends Character>> keySamples) {
        this.keySamples = keySamples;
        return this;
    }

    public CharShortMapTestSuiteBuilder/*<>*/ setValueSamples(
            SampleElements<? extends Short>... valueSamples) {
        this.valueSamples = Arrays.asList(valueSamples);
        return this;
    }

    public CharShortMapTestSuiteBuilder/*<>*/ setValueSamples(
            List<SampleElements<? extends Short>> valueSamples) {
        this.valueSamples = valueSamples;
        return this;
    }

    public CharShortMapTestSuiteBuilder/*<>*/ withSpecialFeatures(
            List<Feature<?>> specialFeatures) {
        this.specialFeatures.addAll(specialFeatures);
        return this;
    }

    public CharShortMapTestSuiteBuilder/*<>*/ withSpecialFeatures(
            Feature<?>... specialFeatures) {
        this.specialFeatures.addAll(Arrays.asList(specialFeatures));
        return this;
    }

    public CharShortMapTestSuiteBuilder/*<>*/ suppressing(Method... methods) {
        suppressing.addAll(Arrays.asList(methods));
        return this;
    }

    public TestSuite create() {
        TestSuite suite = new TestSuite(subSuiteName(factories, keySamples, valueSamples, ""));
        for (CharShortMapFactory/*<>*/ factory : factories) {
            TestCharShortMapGenerator.Builder builder = new TestCharShortMapGenerator.Builder()
                    .setFactory(factory);
            for (SampleElements<? extends Character> keys : keySamples) {
                for (SampleElements<? extends Short> values : valueSamples) {
                    builder.setKeys(keys).setValues(values);
                    Test mutableTests = forEachTestSuiteBuilder(using(builder.mutable()))
                            .named(subSuiteName(factory, keys.asList(), values.asList(), "mutable"))
                            .withFeatures(MapFeature.GENERAL_PURPOSE)
                            .withFeatures(CollectionFeature.REMOVE_OPERATIONS)
                            .createTestSuite();
                    suite.addTest(mutableTests);
                    TestSuite immutableTests = forEachTestSuiteBuilder(using(builder.immutable()))
                            .named(subSuiteName(factory, keys.asList(), values.asList(),
                                    "immutable"))
                            .createTestSuite();
                    suite.addTest(immutableTests);
                }
            }
        }
        return suite;
    }

    private MapTestSuiteBuilder<Character, Short> forEachTestSuiteBuilder(
            MapTestSuiteBuilder<Character, Short> builder) {
        return builder.suppressing(suppressing)
                .withFeatures(MapFeature.FAILS_FAST_ON_CONCURRENT_MODIFICATION)
                .withFeatures(CollectionSize.ANY)
                .withFeatures(specialFeatures);
    }

    private String subSuiteName(Object factory, Object keys, Object values, String infix) {
        return "Tests of " + infix + " CharShortMaps. Key samples: " + keys + ". " +
                "Value samples: " + values + ". Factory: " + factory + ". " +
                "Special features: " + specialFeatures + ".";
    }
}
