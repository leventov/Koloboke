/* with
 char|byte|short|int|long|float|double|obj key
 short|byte|char|int|long|float|double value
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

package net.openhft.collect.testing.map.testers;

import com.google.common.collect.testing.features.CollectionSize;
import net.openhft.collect.testing.map.AbstractCharShortMapTester;

import static com.google.common.collect.testing.features.CollectionSize.ZERO;


/**
 * @see com.google.common.collect.testing.testers.MapContainsValueTester
 */
public class CharShortMapContainsValueTester/*<>*/ extends AbstractCharShortMapTester/*<>*/ {
    @CollectionSize.Require(absent = ZERO)
    public void testContains_yes() {
        assertTrue("containsValue(present) should return true",
                getMap().containsValue(samples.e0.getValue().shortValue()));
    }

    public void testContains_no() {
        assertFalse("containsValue(notPresent) should return false",
                getMap().containsValue(samples.e3.getValue().shortValue()));
    }

    /* if float|double value */
    public void testContains_specialNotContainedButAllowed() {
        assertFalse("containsValue(" + specialValue() + ") should return false",
                getMap().containsValue(specialValue()));
    }

    @CollectionSize.Require(absent = ZERO)
    public void testContains_nonSpecialWhenSpecialContained() {
        initMapWithSpecialValue();
        assertFalse("containsValue(notPresent) should return false",
                getMap().containsValue(samples.e3.getValue().shortValue()));
    }

    @CollectionSize.Require(absent = ZERO)
    public void testContains_specialContained() {
        initMapWithSpecialValue();
        assertTrue("containsValue(" + specialValue() + ") should return true",
                getMap().containsValue(specialValue()));
    }
    /* endif */
}
