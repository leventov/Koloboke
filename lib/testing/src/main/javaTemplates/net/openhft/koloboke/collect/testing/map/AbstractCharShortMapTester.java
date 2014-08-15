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

package net.openhft.koloboke.collect.testing.map;

import com.google.common.collect.testing.AbstractMapTester;
import net.openhft.koloboke.collect.map.CharShortMap;
import net.openhft.koloboke.collect.testing.Specials;

import java.util.*;


/**
 * @see com.google.common.collect.testing.AbstractMapTester
 */
public abstract class AbstractCharShortMapTester/*<>*/ extends AbstractMapTester<Character, Short> {

    @Override
    protected CharShortMap/*<>*/ getMap() {
        return (CharShortMap/*<>*/) super.getMap();
    }

    /* if obj|float|double key */
    protected char specialKey() {
        return Specials.getChar();
    }
    /* endif */

    /* if obj|float|double value */
    protected short specialValue() {
        return Specials.getShort();
    }
    /* endif */

    protected void assertDefaultValue(String message, short actual) {
        /* if !(obj value) */
        assertEquals(message, getMap().defaultValue(), actual);
        /* elif obj value */
        assertNull(message, actual);
        /* endif */
    }

    protected String defaultValue() {
        return "// if !(obj value) //default value// elif obj value //null// endif //";
    }

    /* if obj|float|double key */
    /**
     * @return an array of the proper size with {@link #specialKey()} as the key of the
     * middle element.
     */
    protected Map.Entry<Character, Short>[] createArrayWithSpecialKey() {
        Map.Entry<Character, Short>[] array = createSamplesArray();
        final int specialKeyLocation = getSpecialLocation();
        final Map.Entry<Character, Short> oldEntry = array[specialKeyLocation];
        array[specialKeyLocation] = entry(specialKey(), oldEntry.getValue());
        return array;
    }

    protected short getValueForSpecialKey() {
        return getEntrySpecialReplaces().getValue();
    }

    protected void initMapWithSpecialKey() {
        resetMap(createArrayWithSpecialKey());
    }
    /* endif */

    /* if obj|float|double value */
    /**
     * @return an array of the proper size with {@link #specialValue()} as the value of the
     * middle element.
     */
    protected Map.Entry<Character, Short>[] createArrayWithSpecialValue() {
        Map.Entry<Character, Short>[] array = createSamplesArray();
        final int specialValueLocation = getSpecialLocation();
        final Map.Entry<Character, Short> oldEntry = array[specialValueLocation];
        array[specialValueLocation] = entry(oldEntry.getKey(), specialValue());
        return array;
    }

    protected void initMapWithSpecialValue() {
        resetMap(createArrayWithSpecialValue());
    }

    protected char getKeyForSpecialValue() {
        return getEntrySpecialReplaces().getKey();
    }
    /* endif */

    /* if obj|float|double key || obj|float|double value */
    private Map.Entry<Character, Short> getEntrySpecialReplaces() {
        Iterator<Map.Entry<Character, Short>> entries = getSampleElements().iterator();
        for (int i = 0; i < getSpecialLocation(); i++) {
            entries.next();
        }
        return entries.next();
    }

    protected int getSpecialLocation() {
        return getNullLocation();
    }
    /* endif */

    protected void remove() {
        getMap().remove/* if obj key && !(obj value) //AsShort// endif */(
                samples.e0.getKey()/* if !(obj key) */.charValue()/* endif */
        );
    }

    protected Collection<Map.Entry<Character, Short>> noRemoved(
            Collection<Map.Entry<Character, Short>> elements) {
        return elements;
    }

    protected Collection<Map.Entry<Character, Short>> someRemoved(
            Collection<Map.Entry<Character, Short>> elements) {
        elements = new ArrayList<>(elements);
        elements.remove(samples.e0);
        return elements;
    }
}
