/* with char|byte|short|int|long|float|double|obj elem */
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

package net.openhft.koloboke.collect.testing.testers;

import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;
import net.openhft.koloboke.collect.testing.AbstractCharCollectionTester;
import net.openhft.koloboke.function.*;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.testing.features.CollectionFeature.SUPPORTS_REMOVE;
import static com.google.common.collect.testing.features.CollectionSize.ZERO;


public class CharCollectionForEachTester/*<>*/ extends AbstractCharCollectionTester/*<>*/ {
    /* with No|Some removed */
    /* if Some removed */
    @CollectionSize.Require(absent = ZERO)
    @CollectionFeature.Require(SUPPORTS_REMOVE)
    /* endif */
    public void testForEach_noRemoved() {
        /* if Some removed */remove();/* endif */
        List<Character> iteratorElements = new ArrayList<>();
        for (Character element : c()) { // uses iterator()
            iteratorElements.add(element);
        }
        List<Character> forEachElements = new ArrayList<>();
        /*f*/CharConsumer action = (char e) -> forEachElements.add(e);
        c().forEach(action);
        assertEquals("ForEach order is different from iteration order",
                iteratorElements, forEachElements);
    }
    /* endwith */
}
