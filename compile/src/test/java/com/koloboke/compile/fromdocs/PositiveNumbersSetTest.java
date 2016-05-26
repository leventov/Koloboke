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

import com.google.common.primitives.Ints;
import com.koloboke.collect.impl.InternalIntCollectionOps;
import com.koloboke.collect.set.IntSet;
import com.koloboke.compile.KolobokeSet;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.fail;


public class PositiveNumbersSetTest {

    @SuppressWarnings("deprecation")
    @Test
    public void testPositiveNumbersSet() {
        IntSet set = PositiveNumbersSet.withExpectedSize(1);
        Assert.assertTrue(set.add(1));
        try {
            set.add(-1);
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException ignore) {
        }

        try {
            set.add(new Integer(-1));
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException ignore) {
        }

        try {
            set.addAll(Arrays.asList(-1));
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException ignore) {
        }

        try {
            set.addAll(NormalIntSet.of(-1));
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException ignore) {
        }
    }

    @KolobokeSet
    static abstract class NormalIntSet implements IntSet, InternalIntCollectionOps {
        static IntSet of(int... elements) {
            IntSet set = new KolobokePositiveNumbersSetTest_NormalIntSet(elements.length);
            set.addAll(Ints.asList(elements));
            return set;
        }
    }
}
