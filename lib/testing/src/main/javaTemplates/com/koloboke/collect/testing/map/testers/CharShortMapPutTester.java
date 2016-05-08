/* with
 char|byte|short|int|long|float|double|obj key
 short|byte|char|int|long|float|double|obj value
*/
/*
 * Copyright 2014 the original author or authors.
 * Copyright (C) 2007 The Guava Authors
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

import com.google.common.collect.testing.features.CollectionSize;
import com.google.common.collect.testing.features.MapFeature;
import com.koloboke.collect.testing.Iteration;
import com.koloboke.collect.testing.map.AbstractCharShortMapTester;

import java.util.*;

import static com.google.common.collect.testing.features.CollectionSize.ZERO;
import static com.google.common.collect.testing.features.MapFeature.*;
import static com.google.common.collect.testing.features.MapFeature.ALLOWS_NULL_KEYS;
import static com.google.common.collect.testing.features.MapFeature.ALLOWS_NULL_VALUES;


/**
 * @see com.google.common.collect.testing.testers.MapPutTester
 */
public class CharShortMapPutTester/*<>*/ extends AbstractCharShortMapTester/*<>*/ {
    /* if !(obj key obj value) */
    /* if obj|float|double key */private Map.Entry<Character, Short> specialKeyEntry;/* endif */
    /* if obj|float|double value */private Map.Entry<Character, Short> specialValueEntry;/* endif */
    /* if obj|float|double key obj|float|double value */
    private Map.Entry<Character, Short> specialKeyValueEntry;/* endif */
    /* if obj|float|double value */
    private Map.Entry<Character, Short> presentKeySpecialValueEntry;/* endif */

    @Override public void setUp() throws Exception {
        super.setUp();
        /* if obj|float|double key */
        specialKeyEntry = entry(specialKey(), samples.e3.getValue());/* endif */
        /* if obj|float|double value */
        specialValueEntry = entry(samples.e3.getKey(), specialValue());/* endif */
        /* if obj|float|double key obj|float|double value */
        specialKeyValueEntry = entry(specialKey(), specialValue());/* endif */
        /* if obj|float|double value */
        presentKeySpecialValueEntry = entry(samples.e0.getKey(), specialValue());/* endif */
    }

    @MapFeature.Require(SUPPORTS_PUT)
    public void testPut_supportedNotPresent() {
        assertDefaultValue("put(notPresent, value) should return " + defaultValue(),
                put(samples.e3));
        expectAdded(samples.e3);
    }

    @MapFeature.Require(absent = SUPPORTS_PUT)
    public void testPut_unsupportedNotPresent() {
        try {
            put(samples.e3);
            fail("put(notPresent, value) should throw");
        } catch (UnsupportedOperationException expected) {
        }
        expectUnchanged();
        expectMissing(samples.e3);
    }

    @MapFeature.Require(SUPPORTS_PUT)
    @CollectionSize.Require(absent = ZERO)
    public void testPut_supportedPresentExistingValue() {
        assertEquals("put(present, existingValue) should return present or throw",
                samples.e0.getValue()/* if !(obj value) */.shortValue()/* endif */,
                put(samples.e0));
        expectUnchanged();
    }

    @MapFeature.Require(absent = SUPPORTS_PUT)
    @CollectionSize.Require(absent = ZERO)
    public void testPut_unsupportedPresentExistingValue() {
        try {
            assertEquals("put(present, existingValue) should return present or throw",
                    samples.e0.getValue()/* if !(obj value) */.shortValue()/* endif */,
                    put(samples.e0));
        } catch (UnsupportedOperationException tolerated) {
        }
        expectUnchanged();
    }

    @MapFeature.Require(absent = SUPPORTS_PUT)
    @CollectionSize.Require(absent = ZERO)
    public void testPut_unsupportedPresentDifferentValue() {
        try {
            getMap().put(samples.e0.getKey()/* if !(obj key) */.charValue()/* endif */,
                    samples.e3.getValue()/* if !(obj value) */.shortValue()/* endif */);
            fail("put(present, differentValue) should throw");
        } catch (UnsupportedOperationException expected) {
        }
        expectUnchanged();
    }

    /* if obj|float|double key */
    @MapFeature.Require({SUPPORTS_PUT/* if obj key */, ALLOWS_NULL_KEYS/* endif */})
    public void testPut_specialKeySupportedNotPresent() {
        assertDefaultValue("put(" + specialKey() + ", value) should return " + defaultValue(),
                put(specialKeyEntry));
        expectAdded(specialKeyEntry);
    }

    @MapFeature.Require({SUPPORTS_PUT/* if obj key */, ALLOWS_NULL_KEYS/* endif */})
    @CollectionSize.Require(absent = ZERO)
    public void testPut_specialKeySupportedPresent() {
        Map.Entry<Character, Short> newEntry = entry(specialKey(), samples.e3.getValue());
        initMapWithSpecialKey();
        assertEquals("put(present, value) should return the associated value",
                getValueForSpecialKey(), put(newEntry));

        Map.Entry<Character, Short>[] expected = createArrayWithSpecialKey();
        expected[getSpecialLocation()] = newEntry;
        expectContents(expected);
    }
    /* endif */

    /* if obj key */
    @MapFeature.Require(value = SUPPORTS_PUT, absent = ALLOWS_NULL_KEYS)
    public void testPut_nullKeyUnsupported() {
        try {
            put(specialKeyEntry);
            fail("put(null, value) should throw");
        } catch (NullPointerException expected) {
        }
        expectUnchanged();
        expectNullKeyMissingWhenNullKeysUnsupported(
                "Should not contain null key after unsupported put(null, value)");
    }
    /* endif */

    /* if obj|float|double value */
    @MapFeature.Require({SUPPORTS_PUT/* if obj value */, ALLOWS_NULL_VALUES/* endif */})
    public void testPut_specialValueSupported() {
        assertDefaultValue("put(key, " + specialValue() + ") should return " + defaultValue(),
                put(specialValueEntry));
        expectAdded(specialValueEntry);
    }

    @MapFeature.Require({SUPPORTS_PUT/* if obj value */, ALLOWS_NULL_VALUES/* endif */})
    @CollectionSize.Require(absent = ZERO)
    public void testPut_replaceWithSpecialValueSupported() {
        assertEquals("put(present, " + specialValue() + ") should return the associated value",
                samples.e0.getValue()/* if !(obj value) */.shortValue()/* endif */,
                put(presentKeySpecialValueEntry));
        expectReplacement(presentKeySpecialValueEntry);
    }

    @MapFeature.Require({SUPPORTS_PUT/* if obj value */, ALLOWS_NULL_VALUES/* endif */})
    @CollectionSize.Require(absent = ZERO)
    public void testPut_replaceSpecialValueWithSpecialSupported() {
        initMapWithSpecialValue();
        assertEquals("put(present, " + specialValue() + ") should return the associated value (" +
                        specialValue() + ")",
                specialValue(), getMap().put(getKeyForSpecialValue(), specialValue()));
        expectContents(createArrayWithSpecialValue());
    }

    @MapFeature.Require({SUPPORTS_PUT/* if obj value */, ALLOWS_NULL_VALUES/* endif */})
    @CollectionSize.Require(absent = ZERO)
    public void testPut_replaceSpecialValueWithNonSpecialSupported() {
        Map.Entry<Character, Short> newEntry =
                entry(getKeyForSpecialValue(), samples.e3.getValue());
        initMapWithSpecialValue();
        assertEquals("put(present, value) should return the associated value (" + specialValue() +
                ")", specialValue(), put(newEntry));

        Map.Entry<Character, Short>[] expected = createArrayWithSpecialValue();
        expected[getSpecialLocation()] = newEntry;
        expectContents(expected);
    }
    /* endif */

    /* if obj value */
    @MapFeature.Require(value = SUPPORTS_PUT, absent = ALLOWS_NULL_VALUES)
    public void testPut_nullValueUnsupported() {
        try {
            put(specialValueEntry);
            fail("put(key, null) should throw");
        } catch (NullPointerException expected) {
        }
        expectUnchanged();
        expectNullValueMissingWhenNullValuesUnsupported(
                "Should not contain null value after unsupported put(key, null)");
    }

    @MapFeature.Require(value = SUPPORTS_PUT, absent = ALLOWS_NULL_VALUES)
    @CollectionSize.Require(absent = ZERO)
    public void testPut_replaceWithNullValueUnsupported() {
        try {
            put(presentKeySpecialValueEntry);
            fail("put(present, null) should throw");
        } catch (NullPointerException expected) {
        }
        expectUnchanged();
        expectNullValueMissingWhenNullValuesUnsupported(
                "Should not contain null after unsupported put(present, null)");
    }
    /* endif */

    /* if obj|float|double key obj|float|double value */
    @MapFeature.Require({SUPPORTS_PUT/* if obj key */, ALLOWS_NULL_KEYS/* endif */
            /* if obj value */, ALLOWS_NULL_VALUES/* endif */})
    public void testPut_specialKeyAndValueSupported() {
        assertDefaultValue(
                "put(" + specialKey() + ", " + specialValue() + ") should return " + defaultValue(),
                put(specialKeyValueEntry));
        expectAdded(specialKeyValueEntry);
    }
    /* endif */
    /* endif */

    @MapFeature.Require({FAILS_FAST_ON_CONCURRENT_MODIFICATION, SUPPORTS_PUT})
    @CollectionSize.Require(absent = ZERO)
    public void testPutAbsentConcurrentWithCursor() {
        putAbsentConcurrentWithIteration(Iteration.of(getMap().cursor()));
    }

    @MapFeature.Require({FAILS_FAST_ON_CONCURRENT_MODIFICATION, SUPPORTS_PUT})
    @CollectionSize.Require(absent = ZERO)
    public void testPutAbsentConcurrentWithEntrySetCursor() {
        putAbsentConcurrentWithIteration(Iteration.of(getMap().entrySet().cursor()));
    }

    @MapFeature.Require({FAILS_FAST_ON_CONCURRENT_MODIFICATION, SUPPORTS_PUT})
    @CollectionSize.Require(absent = ZERO)
    public void testPutAbsentConcurrentWithKeySetCursor() {
        putAbsentConcurrentWithIteration(Iteration.of(getMap().keySet().cursor()));
    }

    @MapFeature.Require({FAILS_FAST_ON_CONCURRENT_MODIFICATION, SUPPORTS_PUT})
    @CollectionSize.Require(absent = ZERO)
    public void testPutAbsentConcurrentWithValueCursor() {
        putAbsentConcurrentWithIteration(Iteration.of(getMap().values().cursor()));
    }

    private void putAbsentConcurrentWithIteration(Iteration it) {
        try {
            put(samples.e3);
            it.next();
            fail("Expected ConcurrentModificationException");
        } catch (ConcurrentModificationException expected) {
            // success
        }
    }

    private short put(Map.Entry<Character, Short> entry) {
        return getMap().put(
                entry.getKey()/* if !(obj key) */.charValue()/* endif */,
                entry.getValue()/* if !(obj value) */.shortValue()/* endif */
        );
    }
}
