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

package net.openhft.collect;

import net.openhft.function.Predicate;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class HashConfigTest {

    @Test
    public void testEquals() {
        HashConfig conf1 = HashConfig.DEFAULT;
        int defaultExpectedSize = conf1.getDefaultExpectedSize();
        HashConfig conf2 = conf1.withDefaultExpectedSize(defaultExpectedSize + 1);
        assertEquals(conf1, conf2.withDefaultExpectedSize(defaultExpectedSize));

        float loadFactor = conf2.getLoadFactor();
        HashConfig conf3 = conf2.withLoadFactor(loadFactor + 0.01f);
        assertEquals(conf2, conf3.withLoadFactor(loadFactor));

        Predicate<HashContainer> shrinkCondition = conf3.getShrinkCondition();
        HashConfig conf4 = conf3.withShrinkCondition(new Predicate<HashContainer>() {
            @Override
            public boolean test(HashContainer hashContainer) {
                return false;
            }
        });
        assertEquals(conf3, conf4.withShrinkCondition(shrinkCondition));
    }

    @Test
    public void testToString() {
        assertEquals(
                "HashConfig{getLoadFactor=0.5, getShrinkCondition=null, getDefaultExpectedSize=10}",
                HashConfig.DEFAULT.withLoadFactor(0.5f).withShrinkCondition(null)
                        .withDefaultExpectedSize(10).toString()
        );
    }
}
