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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


public class IdentityToIntMapTest {

    @Test
    public void testIdentityMap() {
        IdentityToIntMap<String> map = IdentityToIntMap.withExpectedSize(10);
        String oneFoo = "foo";
        String anotherFoo = new String("foo");
        map.put(oneFoo, 1);
        assertEquals(1, map.getInt(oneFoo));
        assertEquals(0, map.getInt(anotherFoo));
        assertNull(map.get(anotherFoo));
    }
}
