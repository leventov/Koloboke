/* with obj key char|byte|short|int|float|long|double|obj value */
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


public class ObjCharMapTest {

    /* define valueType *//* if !(obj value) //Character// elif obj value //Integer// endif */
    /* enddefine */
    
    /* define valuePrimCast *//* if !(obj value) //(char)// endif *//* enddefine */

    @Test
    public void testGetByEqualButNotIdenticalKey() {
        Map<Integer, /*valueType*/Character/**/> map = HashObjCharMaps.newMutableMap(10);
        int val1 = 1024*1024;
        int val2 = 1024*1024 + 1;
        int val3 = 1024*1024 + 2;
        int val4 = 1024*1024 + 3;
        map.put(val1, /*valuePrimCast*/(char)/**/ val1);
        map.put(val2, /*valuePrimCast*/(char)/**/ val2);
        map.put(val3, /*valuePrimCast*/(char)/**/ val3);
        map.put(val4, /*valuePrimCast*/(char)/**/ val4);

        assertEquals((/*valueType*/Character/**/) /*valuePrimCast*/(char)/**/ val1, map.get(val1));
        assertEquals((/*valueType*/Character/**/) /*valuePrimCast*/(char)/**/ val2, map.get(val2));
        assertEquals((/*valueType*/Character/**/) /*valuePrimCast*/(char)/**/ val3, map.get(val3));
        assertEquals((/*valueType*/Character/**/) /*valuePrimCast*/(char)/**/ val4, map.get(val4));
    }

    @Test
    public void testPutEqualButNotIdenticalKey() {
        Map<Integer, /*valueType*/Character/**/> map = HashObjCharMaps.newMutableMap(10);
        int val1 = 1024*1024;
        int val2 = 1024*1024 + 1;
        int val3 = 1024*1024 + 2;
        int val4 = 1024*1024 + 3;
        for (int i = 0; i < 2; i++) {
            map.put(new Integer(val1), /*valuePrimCast*/(char)/**/ val1);
            map.put(new Integer(val2), /*valuePrimCast*/(char)/**/ val2);
            map.put(new Integer(val3), /*valuePrimCast*/(char)/**/ val3);
            map.put(new Integer(val4), /*valuePrimCast*/(char)/**/ val4);
        }

        assertEquals(4, map.size());
    }
}
