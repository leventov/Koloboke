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

package com.koloboke.collect.hash;

import com.koloboke.collect.set.hash.*;
import org.junit.Test;

import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static com.koloboke.collect.Equivalence.charSequence;


public class CharSequenceEquivalenceTest {

    @Test
    public void test() {
        HashObjSetFactory<CharSequence> factory = HashObjSets.<CharSequence>getDefaultFactory()
                .withEquivalence(charSequence());

        Set<CharSequence> s1 = factory.newMutableSet();
        Set<CharSequence> s2 = factory.newMutableSet();

        String aString = "a";
        StringBuilder aSB = new StringBuilder(aString);

        s1.add(aString);
        assertTrue(s1.contains(aSB));

        s2.add(aSB);
        assertTrue(s2.contains(aString));

        assertEquals(s1, s2);
        assertEquals(s1.hashCode(), s2.hashCode());

        s1.add(aSB);
        assertEquals(1, s1.size());

        s2.add(aString);
        assertEquals(1, s2.size());

        s1.remove(aSB);
        assertTrue(s1.isEmpty());

        s2.remove(aString);
        assertTrue(s2.isEmpty());
    }
}
