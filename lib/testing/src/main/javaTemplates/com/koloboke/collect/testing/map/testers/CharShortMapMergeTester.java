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
import com.koloboke.collect.testing.Iteration;
import com.koloboke.collect.testing.map.AbstractCharShortMapTester;
import com.koloboke.function.*;

import java.util.ConcurrentModificationException;
import java.util.Map;

import static com.google.common.collect.testing.features.CollectionSize.ZERO;
import static com.google.common.collect.testing.features.MapFeature.*;


/**
 * @see CharShortMapPutTester
 */
public class CharShortMapMergeTester/*<>*/ extends AbstractCharShortMapTester/*<>*/ {

    /* if obj|float|double key */private Map.Entry<Character, Short> specialKeyEntry;/* endif */
    /* if obj|float|double value */private Map.Entry<Character, Short> specialValueEntry;/* endif */
    /* if obj|float|double value */
    private Map.Entry<Character, Short> presentKeySpecialValueEntry;/* endif */

    @Override public void setUp() throws Exception {
        super.setUp();
        /* if obj|float|double key */
        specialKeyEntry = entry(specialKey(), samples.e3().getValue());/* endif */
        /* if obj|float|double value */
        specialValueEntry = entry(samples.e3().getKey(), specialValue());/* endif */
        /* if obj|float|double value */
        presentKeySpecialValueEntry = entry(samples.e0().getKey(), specialValue());/* endif */
    }

    /* with Primitive|Generic kind */
    /* if obj value Generic kind || short|byte|char|int|long|float|double value Primitive kind */

    @Require(SUPPORTS_PUT)
    public void testMerge_supportedNotPresent_primitive() {
        assertEquals("merge(notPresent, value, remappingFunction) should return the given value",
                samples.e3().getValue(),
                (Short) primitiveMerge(samples.e3(), (v1, v2) -> {
                    fail("merge(notPresent, value, remappingFunction) shouldn't call the function");
                    return v2;
                }));
        expectAdded(samples.e3());
    }

    @Require(absent = SUPPORTS_PUT)
    public void testMerge_unsupportedNotPresent_primitive() {
        try {
            primitiveMerge(samples.e3(), (v1, v2) -> v2);
            fail("merge(notPresent, value, remappingFunction) should throw");
        } catch (UnsupportedOperationException expected) {
        }
        expectUnchanged();
        expectMissing(samples.e3());
    }

    @Require(SUPPORTS_PUT)
    @CollectionSize.Require(absent = ZERO)
    public void testMerge_supportedPresentExistingValue_primitive() {
        assertEquals("merge(present, existingValue, remappingFunction) should remap value",
                samples.e3().getValue(),
                (Short) primitiveMerge(samples.e0(), (v1, v2) -> samples.e3().getValue()));
        assertEquals(samples.e3().getValue(),
                (Short) getMap().get/* if obj key && !(obj value) //$Short// endif */(
                        samples.e0().getKey()/* if !(obj key) */.charValue()/* endif */));
    }

    /* if obj|float|double key */
    @Require({SUPPORTS_PUT/* if obj key */, ALLOWS_NULL_KEYS/* endif */})
    public void testMerge_specialKeySupportedNotPresent_primitive() {
        assertEquals("merge(" + specialKey() + ", value, remappingFunction) " +
                        "should return the given value",
                samples.e3().getValue(),
                (Short) primitiveMerge(specialKeyEntry, (v1, v2) -> {
                    fail("merge(" + specialKey() + ", value, remappingFunction) " +
                            "shouldn't call the function");
                    return v2;
                }));
        expectAdded(specialKeyEntry);
    }

    @Require({SUPPORTS_PUT/* if obj key */, ALLOWS_NULL_KEYS/* endif */})
    @CollectionSize.Require(absent = ZERO)
    public void testMerge_specialKeySupportedPresent_primitive() {
        Map.Entry<Character, Short> newEntry = entry(specialKey(), samples.e3().getValue());
        initMapWithSpecialKey();
        assertEquals("merge(present, value, remappingFunction) should remap value",
                samples.e4().getValue(),
                (Short) primitiveMerge(newEntry, (v1, v2) -> samples.e4().getValue()));

        Map.Entry<Character, Short>[] expected = createArrayWithSpecialKey();
        expected[getSpecialLocation()] = entry(specialKey(), samples.e4().getValue());
        expectContents(expected);
    }
    /* endif */

    /* if obj key */
    @Require(value = SUPPORTS_PUT, absent = ALLOWS_NULL_KEYS)
    public void testMerge_nullKeyUnsupported_primitive() {
        try {
            primitiveMerge(specialKeyEntry, (v1, v2) -> v2);
            fail("merge(null, value, remappingFunction) should throw");
        } catch (NullPointerException expected) {
        }
        expectUnchanged();
        expectNullKeyMissingWhenNullKeysUnsupported(
                "Should not contain null key after unsupported " +
                        "merge(null, value, remappingFunction)");
    }
    /* endif */

    /* if float|double value */
    @Require({SUPPORTS_PUT/* if obj value */, ALLOWS_NULL_VALUES/* endif */})
    public void testMerge_specialValueSupported_primitive() {
        assertEquals("merge(key, " + specialValue() + ", remappingFunction) " +
                        "should return " + specialValue(),
                (Short) specialValue(),
                (Short) primitiveMerge(specialValueEntry, (v1, v2) -> {
                    fail("merge(key, " + specialValue() + ", remappingFunction) " +
                            "shouldn't call the function");
                    return v2;
                }));
        expectAdded(specialValueEntry);
    }

    @Require({SUPPORTS_PUT})
    @CollectionSize.Require(absent = ZERO)
    public void testMerge_replaceWithSpecialValueSupported_primitive() {
        assertEquals("merge(present, " + specialValue() + ", remappingFunction) should remap value",
                (Short) specialValue(),
                (Short) primitiveMerge(presentKeySpecialValueEntry, (v1, v2) -> v2));
        expectReplacement(presentKeySpecialValueEntry);
    }
    /* endif */

    /* if obj value */
    @Require({SUPPORTS_PUT, ALLOWS_NULL_VALUES})
    @CollectionSize.Require(absent = ZERO)
    public void testMerge_existingNullValue() {
        initMapWithSpecialValue();
        Map.Entry<Character, Short> entry = entry(getKeyForSpecialValue(), samples.e4().getValue());
        assertEquals("merge(keyMappingToNull, value, remappingFunction) " +
                        "should return the given value",
                entry.getValue(),
                genericMerge(entry, (v1, v2) -> {
                    fail("merge(keyMappingToNull, value, remappingFunction) " +
                            "shouldn't call the function");
                    return v2;
                }));

        Map.Entry<Character, Short>[] expected = createSamplesArray();
        expected[getSpecialLocation()] = entry;
        expectContents(expected);
    }
    /* endif */

    /* if Generic kind */
    @Require({SUPPORTS_PUT, SUPPORTS_REMOVE})
    @CollectionSize.Require(absent = ZERO)
    public void testMerge_remapToNull_removeSupported() {
        assertNull("merge(present, value, (v1, v2) -> null) should return null",
                genericMerge(samples.e0(), (v1, v2) -> null));
        assertFalse("merge(present, value, (v1, v2) -> null) should remove the entry",
                getMap().containsKey(samples.e0().getKey()/* if !(obj key) */.charValue()/* endif */)
        );
    }

    @Require(value = SUPPORTS_PUT, absent = SUPPORTS_REMOVE)
    @CollectionSize.Require(absent = ZERO)
    public void testMerge_remapToNull_removeUnsupported() {
        try {
            genericMerge(samples.e0(), (v1, v2) -> null);
            fail("merge(present, value, (v1, v2) -> null) should throw");
        } catch (UnsupportedOperationException expected) {
            // expected
        }
        expectUnchanged();
    }

    /* if obj|float|double key */
    @Require({SUPPORTS_PUT, SUPPORTS_REMOVE/* if obj key */, ALLOWS_NULL_KEYS/* endif */})
    @CollectionSize.Require(absent = ZERO)
    public void testMerge_specialKeyRemapToNull_removeSupported() {
        initMapWithSpecialKey();
        assertNull("merge(" + specialKey() + ", value, (v1, v2) -> null) should return null",
                genericMerge(specialKeyEntry, (v1, v2) -> null));
        assertFalse("merge(" + specialKey() + ", value, (v1, v2) -> null) should remove the entry",
                getMap().containsKey(
                        specialKeyEntry.getKey()/* if !(obj key) */.charValue()/* endif */));
    }

    @Require(value = {SUPPORTS_PUT/* if obj key */, ALLOWS_NULL_KEYS/* endif */},
            absent = SUPPORTS_REMOVE)
    @CollectionSize.Require(absent = ZERO)
    public void testMerge_specialKeyRemapToNull_removeUnsupported() {
        initMapWithSpecialKey();
        try {
            genericMerge(specialKeyEntry, (v1, v2) -> null);
            fail("merge(" + specialKey() + ", value, (v1, v2) -> null) should throw");
        } catch (UnsupportedOperationException expected) {
            // expected
        }
        expectContents(createArrayWithSpecialKey());
    }
    /* endif */

    @Require(SUPPORTS_PUT)
    public void testMerge_nullValue_putSupported() {
        try {
            genericMerge(entry(samples.e0().getKey(), null), (v1, v2) -> v2);
            fail("merge(key, null, remappingFunction) should throw");
        } catch (NullPointerException expected) {
            // expected
        }
        expectUnchanged();
    }

    @Require(absent = SUPPORTS_PUT)
    public void testMerge_nullValue_putUnsupported() {
        try {
            genericMerge(entry(samples.e0().getKey(), null), (v1, v2) -> v2);
            fail("merge(key, null, remappingFunction) should throw");
        } catch (NullPointerException | UnsupportedOperationException expected) {
            // expected
        }
        expectUnchanged();
    }

    @Require(SUPPORTS_PUT)
    public void testMerge_nullRemappingFunction_putSupported() {
        try {
            genericMerge(samples.e0(), null);
            fail("merge(key, value, null) should throw");
        } catch (NullPointerException expected) {
            // expected
        }
        expectUnchanged();
    }

    @Require(absent = SUPPORTS_PUT)
    public void testMerge_nullRemappingFunction_putUnsupported() {
        try {
            genericMerge(samples.e0(), null);
            fail("merge(key, value, null) should throw");
        } catch (NullPointerException | UnsupportedOperationException expected) {
            // expected
        }
        expectUnchanged();
    }
    /* endif */

    @Require({FAILS_FAST_ON_CONCURRENT_MODIFICATION, SUPPORTS_PUT})
    @CollectionSize.Require(absent = ZERO)
    public void testMergeAbsentConcurrentWithCursor_primitive() {
        putAbsentConcurrentWithIteration_primitive(Iteration.of(getMap().cursor()));
    }

    @Require({FAILS_FAST_ON_CONCURRENT_MODIFICATION, SUPPORTS_PUT})
    @CollectionSize.Require(absent = ZERO)
    public void testMergeAbsentConcurrentWithEntrySetCursor_primitive() {
        putAbsentConcurrentWithIteration_primitive(Iteration.of(getMap().entrySet().cursor()));
    }

    @Require({FAILS_FAST_ON_CONCURRENT_MODIFICATION, SUPPORTS_PUT})
    @CollectionSize.Require(absent = ZERO)
    public void testMergeAbsentConcurrentWithKeySetCursor_primitive() {
        putAbsentConcurrentWithIteration_primitive(Iteration.of(getMap().keySet().cursor()));
    }

    @Require({FAILS_FAST_ON_CONCURRENT_MODIFICATION, SUPPORTS_PUT})
    @CollectionSize.Require(absent = ZERO)
    public void testMergeAbsentConcurrentWithValueCursor_primitive() {
        putAbsentConcurrentWithIteration_primitive(Iteration.of(getMap().values().cursor()));
    }

    private void putAbsentConcurrentWithIteration_primitive(Iteration it) {
        try {
            primitiveMerge(samples.e3(), (v1, v2) -> v2);
            it.next();
            fail("Expected ConcurrentModificationException");
        } catch (ConcurrentModificationException expected) {
            // success
        }
    }

    /* endif */
    /* endwith*/

    /* if !(obj value) */
    private short primitiveMerge(Map.Entry<Character, Short> entry,
            /*f*/ShortBinaryOperator remappingFunction) {
        return getMap().merge(
                entry.getKey()/* if !(obj key) */.charValue()/* endif */,
                entry.getValue()/* if !(obj value) */.shortValue()/* endif */,
                remappingFunction
        );
    }
    /* elif obj value */
    private Short genericMerge(Map.Entry<Character, Short> entry,
            BiFunction<Short, Short, Short> remappingFunction) {
        return getMap().merge(entry.getKey(), entry.getValue(), remappingFunction);
    }
    /* endif */
}
