/* with
 char|byte|short|int|long|float|double key
 short|byte|char|int|long|float|double|obj value
*/
/*
 * Copyright 2014 the original author or authors.
 * Copyright (C) 2008 The Guava Authors
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

import com.google.common.collect.testing.features.CollectionSize;
import net.openhft.collect.testing.map.AbstractCharShortMapTester;

import static com.google.common.collect.testing.features.CollectionSize.ZERO;


/**
 * @see com.google.common.collect.testing.testers.MapContainsKeyTester
 */
public class CharShortMapContainsKeyTester/*<>*/ extends AbstractCharShortMapTester/*<>*/ {
    @CollectionSize.Require(absent = ZERO)
    public void testContains_yes() {
        assertTrue("containsKey(present) should return true",
                getMap().containsKey(samples.e0.getKey().charValue()));
    }

    public void testContains_no() {
        assertFalse("containsKey(notPresent) should return false",
                getMap().containsKey(samples.e3.getKey().charValue()));
    }

    /* if float|double key */
    public void testContains_specialNotContainedButAllowed() {
        assertFalse("containsKey(" + specialKey() + ") should return false",
                getMap().containsKey(specialKey()));
    }

    @CollectionSize.Require(absent = ZERO)
    public void testContains_nonSpecialWhenSpecialContained() {
        initMapWithSpecialKey();
        assertFalse("containsKey(notPresent) should return false",
                getMap().containsKey(samples.e3.getKey().charValue()));
    }

    @CollectionSize.Require(absent = ZERO)
    public void testContains_specialContained() {
        initMapWithSpecialKey();
        assertTrue("containsKey(" + specialKey() + ") should return true",
                getMap().containsKey(specialKey()));
    }
    /* endif */
}
