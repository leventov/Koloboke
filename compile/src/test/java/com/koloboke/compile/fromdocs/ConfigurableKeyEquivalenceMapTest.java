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

import com.koloboke.collect.map.hash.HashObjLongMap;
import org.junit.Test;

import static com.koloboke.collect.Equivalence.caseInsensitive;
import static com.koloboke.collect.Equivalence.charSequence;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;


public class ConfigurableKeyEquivalenceMapTest {

    @Test
    public void testCustomizableKeyEquivalenceMap() {
        HashObjLongMap<String> map1 = ConfigurableKeyEquivalenceMap.with(caseInsensitive(), 1);
        map1.put("FOO", 1L);
        assertEquals(1L, map1.getLong("foo"));

        HashObjLongMap<CharSequence> map2 = ConfigurableKeyEquivalenceMap.with(charSequence(), 1);
        map2.put(new StringBuilder("foo"), 1L);
        assertEquals(1L, map2.getLong("foo"));
        assertNotEquals(map1, map2);
        assertNotEquals(map2, map1);
    }
}
