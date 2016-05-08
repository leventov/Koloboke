/* with
 char|byte|short|int|long|float|double key
 short|byte|char|int|long|float|double value
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

package com.koloboke.collect.map.hash;

import com.koloboke.function.CharPredicate;
import com.koloboke.function.CharShortPredicate;
import org.apache.mahout.math.list.CharArrayList;
import org.apache.mahout.math.list.ShortArrayList;
import org.junit.Test;

import java.util.*;

import static com.koloboke.collect.map.hash.HashCharShortMaps.newMutableMap;
import static org.junit.Assert.*;


/**
 * These tests were originally taken from Mahout Collections (org.apache.mahout.math.map package).
 */
public class MahoutHashCharShortMapTest {

    /* define keyEpsilon *//* if float|double key //, (char) 0.000001// endif *//* enddefine */
    /* define valueEpsilon *//* if float|double value //, (short) 0.000001// endif *//* enddefine */

    @Test
    public void testClear() {
        HashCharShortMap map = newMutableMap();
        map.put((char) 11, (short) 22);
        assertEquals(1, map.size());
        map.clear();
        assertEquals(0, map.size());
        assertEquals(0, map.get((char) 11), 0.0000001);
    }

    @Test
    public void testClone() {
        HashCharShortMap map = newMutableMap();
        map.put((char) 11, (short) 22);
        HashCharShortMap map2 = HashCharShortMaps.newMutableMap(map);
        map.clear();
        assertEquals(1, map2.size());
    }

    @Test
    public void testContainsKey() {
        HashCharShortMap map = newMutableMap();
        map.put((char) 11, (short) 22);
        assertTrue(map.containsKey((char) 11));
        assertFalse(map.containsKey((char) 12));
    }

    @Test
    public void testContainValue() {
        HashCharShortMap map = newMutableMap();
        map.put((char) 11, (short) 22);
        assertTrue(map.containsValue((short) 22));
        assertFalse(map.containsValue((short) 23));
    }

    @Test
    public void testForEachKey() {
        final CharArrayList keys = new CharArrayList();
        HashCharShortMap map = newMutableMap();
        map.put((char) 11, (short) 22);
        map.put((char) 12, (short) 23);
        map.put((char) 13, (short) 24);
        map.put((char) 14, (short) 25);
        map.remove((char) 13);
        map.keySet().forEachWhile(new CharPredicate() {

            @Override
            public boolean test(char element) {
                keys.add(element);
                return true;
            }
        });

        char[] keysArray = keys.toArray(new char[keys.size()]);
        Arrays.sort(keysArray);

        assertArrayEquals(new char[] {11, 12, 14}, keysArray /* keyEpsilon */);
    }

    private static class Pair implements Comparable<Pair> {

        char k;

        short v;

        Pair(char k, short v) {
            this.k = k;
            this.v = v;
        }

        @Override
        public int compareTo(Pair o) {
            if (k < o.k) {
                return -1;
            } else if (k == o.k) {
                return 0;
            } else {
                return 1;
            }
        }
    }

    @Test
    public void testForEachWhile() {
        final List<Pair> pairs = new ArrayList<Pair>();
        HashCharShortMap map = newMutableMap();
        map.put((char) 11, (short) 22);
        map.put((char) 12, (short) 23);
        map.put((char) 13, (short) 24);
        map.put((char) 14, (short) 25);
        map.remove((char) 13);
        map.forEachWhile(new CharShortPredicate() {

            @Override
            public boolean test(char first, short second) {
                pairs.add(new Pair(first, second));
                return true;
            }
        });

        Collections.sort(pairs);
        assertEquals(3, pairs.size());
        assertEquals((char) 11, pairs.get(0).k /* keyEpsilon */);
        assertEquals((short) 22, pairs.get(0).v /* valueEpsilon */);
        assertEquals((char) 12, pairs.get(1).k /* keyEpsilon */);
        assertEquals((short) 23, pairs.get(1).v /* valueEpsilon */);
        assertEquals((char) 14, pairs.get(2).k /* keyEpsilon */);
        assertEquals((short) 25, pairs.get(2).v /* valueEpsilon */);

        pairs.clear();
        map.forEachWhile(new CharShortPredicate() {
            int count = 0;

            @Override
            public boolean test(char first, short second) {
                pairs.add(new Pair(first, second));
                count++;
                return count < 2;
            }
        });

        assertEquals(2, pairs.size());
    }

    @Test
    public void testGet() {
        HashCharShortMap map = newMutableMap();
        map.put((char) 11, (short) 22);
        map.put((char) 12, (short) 23);
        assertEquals(22, map.get((char) 11) /* valueEpsilon */);
        assertEquals(0, map.get((char) 0) /* valueEpsilon */);
    }

    @Test
    public void testAdjustOrPutValue() {
        HashCharShortMap map = newMutableMap();
        map.put((char) 11, (short) 22);
        map.put((char) 12, (short) 23);
        map.put((char) 13, (short) 24);
        map.put((char) 14, (short) 25);
        map.addValue((char) 11, (short) 3, (short) 1);
        assertEquals(25, map.get((char) 11) /* valueEpsilon */);
        map.addValue((char) 15, (short) 3, (short) 1);
        assertEquals(4, map.get((char) 15) /* valueEpsilon */);
    }

    @Test
    public void testKeys() {
        HashCharShortMap map = newMutableMap();
        map.put((char) 11, (short) 22);
        map.put((char) 12, (short) 22);
        CharArrayList keys = new CharArrayList(map.keySet().toCharArray());
        keys.sort();
        assertEquals(11, keys.get(0) /* keyEpsilon */);
        assertEquals(12, keys.get(1) /* keyEpsilon */);
    }

    @Test
    public void testPairsMatching() {
        HashCharShortMap map = newMutableMap();
        map.put((char) 11, (short) 22);
        map.put((char) 12, (short) 23);
        map.put((char) 13, (short) 24);
        map.put((char) 14, (short) 25);
        map.remove((char) 13);
        map.removeIf(new CharShortPredicate() {
            @Override
            public boolean test(char first, short second) {
                return (first % 2) != 0;
            }
        });
        CharArrayList keyList = new CharArrayList(map.keySet().toCharArray());
        keyList.sort();
        ShortArrayList valueList = new ShortArrayList(map.values().toShortArray());
        valueList.sort();
        assertEquals(2, keyList.size());
        assertEquals(2, valueList.size());
        assertEquals(12, keyList.get(0) /* keyEpsilon */);
        assertEquals(14, keyList.get(1) /* keyEpsilon */);
        assertEquals(23, valueList.get(0) /* valueEpsilon */);
        assertEquals(25, valueList.get(1) /* valueEpsilon */);
    }

    @Test
    public void testValues() {
        HashCharShortMap map = newMutableMap();
        map.put((char) 11, (short) 22);
        map.put((char) 12, (short) 23);
        map.put((char) 13, (short) 24);
        map.put((char) 14, (short) 25);
        map.remove((char) 13);
        ShortArrayList values = new ShortArrayList(map.values().toShortArray());
        assertEquals(3, values.size());
        values.sort();
        assertEquals(22, values.get(0) /* valueEpsilon */);
        assertEquals(23, values.get(1) /* valueEpsilon */);
        assertEquals(25, values.get(2) /* valueEpsilon */);
    }

    @Test
    public void testCopy() {
        HashCharShortMap map = newMutableMap();
        map.put((char) 11, (short) 22);
        HashCharShortMap map2 = HashCharShortMaps.newMutableMap(map);
        map.clear();
        assertEquals(1, map2.size());
    }

    @Test
    public void testEquals() {
        HashCharShortMap map = newMutableMap();
        map.put((char) 11, (short) 22);
        map.put((char) 12, (short) 23);
        map.put((char) 13, (short) 24);
        map.put((char) 14, (short) 25);
        map.remove((char) 13);
        HashCharShortMap map2 = HashCharShortMaps.newMutableMap(map);
        assertEquals(map, map2);
        assertTrue(map2.equals(map));
        assertFalse("Hello Sailor".equals(map));
        assertFalse(map.equals("hello sailor"));
        map2.remove((char) 11);
        assertFalse(map.equals(map2));
        assertFalse(map2.equals(map));
    }
}
