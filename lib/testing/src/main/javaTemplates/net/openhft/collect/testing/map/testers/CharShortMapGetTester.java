/* with
 char|byte|short|int|long|float|double|obj key
 short|byte|char|int|long|float|double|obj value
*/
/* if !(obj key obj value) */
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

package net.openhft.collect.testing.map.testers;

import com.google.common.collect.testing.WrongType;
import com.google.common.collect.testing.features.*;
import net.openhft.collect.testing.map.AbstractCharShortMapTester;

import static com.google.common.collect.testing.features.CollectionSize.ZERO;
import static com.google.common.collect.testing.features.MapFeature.ALLOWS_NULL_KEYS;
import static com.google.common.collect.testing.features.MapFeature.ALLOWS_NULL_KEY_QUERIES;


/**
 * @see com.google.common.collect.testing.testers.MapGetTester
 */
public class CharShortMapGetTester/*<>*/ extends AbstractCharShortMapTester/*<>*/ {
    @CollectionSize.Require(absent = ZERO)
    public void testGet_yes() {
        assertEquals("get(present) should return the associated value",
                samples.e0.getValue()/* if !(obj value) */.shortValue()/* endif */,
                specializedGet(samples.e0.getKey()));
    }

    public void testGet_no() {
        assertDefaultValue("get(notPresent) should return " + defaultValue(),
                specializedGet(samples.e3.getKey()));
    }

    /* if obj|float|double key */
    /* if obj key */@MapFeature.Require(ALLOWS_NULL_KEY_QUERIES)/* endif */
    public void testGet_specialNotContainedButAllowed() {
        assertDefaultValue("get(" + specialKey() + ") should return " + defaultValue(),
                specializedGet(specialKey()));
    }

    /* if obj key */@MapFeature.Require(ALLOWS_NULL_KEYS)/* endif */
    @CollectionSize.Require(absent = ZERO)
    public void testGet_nonNullWhenNullContained() {
        initMapWithSpecialKey();
        assertDefaultValue("get(notPresent) should return " + defaultValue(),
               specializedGet(samples.e3.getKey()));
    }

    /* if obj key */@MapFeature.Require(ALLOWS_NULL_KEYS)/* endif */
    @CollectionSize.Require(absent = ZERO)
    public void testGet_nullContained() {
        initMapWithSpecialKey();
        assertEquals("get(" + specialKey() + ") should return the associated value",
                getValueForSpecialKey(), specializedGet(specialKey()));
    }
    /* endif */

    /* if obj key */
    @MapFeature.Require(absent = ALLOWS_NULL_KEY_QUERIES)
    public void testGet_nullNotContainedAndUnsupported() {
        try {
            assertDefaultValue("get(null) should return " + defaultValue() + " or throw",
                    getMap().getShort(null));
        } catch (NullPointerException tolerated) {
        }
    }

    public void testGet_wrongType() {
        try {
            assertDefaultValue("get(wrongType) should return " + defaultValue() + " or throw",
                    getMap().getShort(WrongType.VALUE));
        } catch (ClassCastException tolerated) {
        }
    }
    /* endif */

    private short specializedGet(Character key) {
        return getMap().get/* if obj key //$Short// endif */(
                key/* if !(obj key) */.charValue()/* endif */
        );
    }
}
