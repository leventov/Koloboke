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

package com.koloboke.compile.fromdocs;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;


@SuppressWarnings({"ImplicitNumericConversion", "AutoBoxing"})
public class TutorialTest {

    @Test
    public void testMyMap() {
        Map<String, String> tickers = MyMap.withExpectedSize(10);
        tickers.put("AAPL", "Apple, Inc.");
        assertEquals(1, tickers.size());
        assertEquals("Apple, Inc.", tickers.get("AAPL"));
    }

    @Test
    public void testSparseMyMap() {
        Map<String, String> tickers = MyMap.sparseWithExpectedSize(10);
        tickers.put("AAPL", "Apple, Inc.");
        assertEquals(1, tickers.size());
        assertEquals("Apple, Inc.", tickers.get("AAPL"));
    }

    @Test
    public void testTickers() {
        Tickers tickers = Tickers.withExpectedSize(10);
        tickers.put("AAPL", "Apple, Inc.");
        assertEquals(1, tickers.size());
        assertEquals("Apple, Inc.", tickers.get("AAPL"));

        Map<String, String> m = new HashMap<String, String>();
        m.put("GOOG", "Alphabet, Inc.");
        m.put("IBM", "IBM, Inc.");
        Tickers tickers2 = Tickers.fromMap(m);
        assertEquals(2, tickers2.size());
    }

    @Test
    public void testCache() {
        Cache<Integer, String> cache = Cache.withExpectedSize(1);
        cache.put(1, "foo");
        assertEquals("foo", cache.get(1));
        assertEquals(1, cache.size());
        cache.clear();
        assertEquals(0, cache.size());
        assertNull(cache.get(1));
    }

    @Test
    public void testExtendedMap() {
        ExtendedMap<String> map = ExtendedMap.withExpectedSize(10);
        assertTrue(map.ensureCapacity(1000));
        map.put("foo", "bar");
        assertTrue(map.shrink());
    }

    @Test
    public void testMyIntLongMap() {
        Map<Integer, Long> map = MyIntLongMap.withExpectedSize(10);
        try {
            map.put(1, null);
            throw new AssertionError(
                    "should throw NPE, because cannot store null as a primitive long value");
        } catch (NullPointerException expected) {
            // ignore expected exception
        }
    }

    @Test
    public void testStringIntMap() {
        StringIntMap map = StringIntMap.withExpectedSize(10);
        assertEquals(0, map.put("foo", 42));
        assertEquals(42, map.getInt("foo"));
        assertEquals(42, map.removeAsInt("foo"));
        assertEquals(0, map.getInt("foo"));
        assertEquals(0, map.removeAsInt("foo"));
    }

    @Test
    public void testSmallerLongIntMap() {
        SmallerLongIntMap map = SmallerLongIntMap.withExpectedSize(10);
        map.put(2, 3);
        assertEquals(1, map.size());
        assertEquals(2, (long) map.keySet().iterator().next());
    }

    @Test
    public void testQuadraticHashingMap() {
        QuadraticHashingMap map = QuadraticHashingMap.withExpectedSize(10);
        assertNull(map.put(1, "foo"));
    }

    @Test
    public void testCharArrayMap() {
        Map<char[], String> map = CharArrayMap.withExpectedSize(10);
        map.put(new char[] {'h', 'e', 'l', 'l', 'o'}, "hello");
        assertEquals("hello", map.get("hello".toCharArray()));
    }

    @Test
    public void testOptimizedMap() {
        OptimizedMap<String> map = OptimizedMap.withExpectedSize(10);
        map.justPut("apples", 10);
        assertEquals(10, map.getInt("apples"));
        assertTrue(map.justRemove("apples"));
        assertEquals(0, map.getInt("apples"));
    }
}
