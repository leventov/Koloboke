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

package net.openhft.koloboke.collect.map.hash;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;


public class ObjMapTest {

    @Test
    public void test() {
        Map<Integer, Integer> map = HashObjObjMaps.newMutableMap(10);
        int val1 = 1024*1024;
        int val2 = 1024*1024 + 1;
        int val3 = 1024*1024 + 2;
        int val4 = 1024*1024 + 3;
        map.put(val1, val1);
        map.put(val2, val2);
        map.put(val3, val3);
        map.put(val4, val4);

        assertEquals((Integer) val1, map.get(val1));
        assertEquals((Integer) val2, map.get(val2));
        assertEquals((Integer) val3, map.get(val3));
        assertEquals((Integer) val4, map.get(val4));
    }
}
