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

package net.openhft.collect.set;

import com.google.common.collect.testing.SampleElements;
import com.google.common.collect.testing.SetTestSuiteBuilder;
import com.google.common.collect.testing.features.*;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.lang.reflect.Method;
import java.util.*;

import static com.google.common.collect.testing.SetTestSuiteBuilder.*;


public class CharSetTestSuiteBuilder/*<>*/ {

    private List<CharSetFactory/*<>*/> factories;
    private List<SampleElements<? extends Character>> samples;
    private List<Feature<?>> specialFeatures = new ArrayList<Feature<?>>();
    private List<Method> suppressing = new ArrayList<Method>();

    public CharSetTestSuiteBuilder/*<>*/ setFactories(CharSetFactory/*<>*/... factories) {
        this.factories = Arrays.asList(factories);
        return this;
    }

    public CharSetTestSuiteBuilder/*<>*/ setFactories(
            List<CharSetFactory/*<>*/> factories) {
        this.factories = factories;
        return this;
    }

    public CharSetTestSuiteBuilder/*<>*/ setSamples(
            SampleElements<? extends Character>... samples) {
        this.samples = Arrays.asList(samples);
        return this;
    }

    public CharSetTestSuiteBuilder/*<>*/ setSamples(
            List<SampleElements<? extends Character>> samples) {
        this.samples = samples;
        return this;
    }

    public CharSetTestSuiteBuilder/*<>*/ withSpecialFeatures(
            List<Feature<?>> specialFeatures) {
        this.specialFeatures.addAll(specialFeatures);
        return this;
    }

    public CharSetTestSuiteBuilder/*<>*/ withSpecialFeatures(
            Feature<?>... specialFeatures) {
        this.specialFeatures.addAll(Arrays.asList(specialFeatures));
        return this;
    }

    public CharSetTestSuiteBuilder/*<>*/ suppressing(Method... methods) {
        suppressing.addAll(Arrays.asList(methods));
        return this;
    }

    public TestSuite create() {
        TestSuite suite = new TestSuite(subSuiteName(factories, samples, ""));
        for (CharSetFactory/*<>*/ factory : factories) {
            for (SampleElements<? extends Character> elems : samples) {
                Test mutableTests = forEachTestSuiteBuilder(
                        using(TestCharSetGenerator.mutable(factory, elems)))
                        .named(subSuiteName(factory, elems.asList(), "mutable"))
                        .withFeatures(SetFeature.GENERAL_PURPOSE)
                        .createTestSuite();
                suite.addTest(mutableTests);
                TestSuite immutableTests = forEachTestSuiteBuilder(
                        using(TestCharSetGenerator.immutable(factory, elems)))
                        .named(subSuiteName(factory, elems.asList(), "immutable"))
                        .createTestSuite();
                suite.addTest(immutableTests);
            }
        }
        return suite;
    }

    private SetTestSuiteBuilder<Character> forEachTestSuiteBuilder(
            SetTestSuiteBuilder<Character> builder) {
        return builder.suppressing(suppressing)
                .withFeatures(CollectionFeature.FAILS_FAST_ON_CONCURRENT_MODIFICATION)
                .withFeatures(CollectionSize.ANY)
                .withFeatures(specialFeatures);
    }

    private String subSuiteName(Object factory, Object elems, String infix) {
        return "Tests of " + infix + " CharShortMaps. Elem samples: " + elems + ". " +
                "Factory: " + factory + ". " + "Special features: " + specialFeatures + ".";
    }
}
