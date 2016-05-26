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

import com.koloboke.collect.map.ObjIntMap;
import com.koloboke.function.ObjIntToIntFunction;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class MinusOneDefaultValueObjIntMapTest {

    @Test
    public void testMinusOneDefaultValueObjIntMap() {
        ObjIntMap<String> map = MinusOneDefaultValueObjIntMap.withExpectedSize(10);
        map.put("apples", 10);
        assertEquals(-1, map.getInt("bananas"));
        assertEquals(-1, map.getInt("foo"));
        assertEquals(-1, map.put("foo", 1));
        assertEquals(1, map.removeAsInt("foo"));
        assertEquals(-1, map.removeAsInt("foo"));
        assertEquals(-1, map.replace("foo", 1));
        map.removeAsInt("foo");
        assertEquals(-1, map.putIfAbsent("foo", 1));
        map.removeAsInt("foo");
        assertEquals(-1, map.compute("foo", new ObjIntToIntFunction<String>() {
            @Override
            public int applyAsInt(String key, int value) {
                return value;
            }
        }));
    }
}
