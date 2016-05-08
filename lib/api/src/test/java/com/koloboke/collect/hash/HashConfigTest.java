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

import com.koloboke.function.Predicate;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class HashConfigTest {

    @Test
    public void testEquals() {
        HashConfig conf2 = HashConfig.getDefault();
        double minLoad = conf2.getMinLoad();
        HashConfig conf3 = conf2.withMinLoad(minLoad - 0.01);
        assertEquals(conf2, conf3.withMinLoad(minLoad));

        double targetLoad = conf3.getTargetLoad();
        HashConfig conf4 = conf3.withTargetLoad(targetLoad + 0.01);
        assertEquals(conf3, conf4.withTargetLoad(targetLoad));

        double maxLoad = conf4.getMaxLoad();
        HashConfig conf5 = conf4.withMaxLoad(maxLoad + 0.01);
        assertEquals(conf4, conf5.withMaxLoad(maxLoad));

        double growFactor = conf5.getGrowFactor();
        HashConfig conf6 = conf5.withGrowFactor(growFactor + 0.01);
        assertEquals(conf5, conf6.withGrowFactor(growFactor));

        Predicate<HashContainer> shrinkCondition = conf6.getShrinkCondition();
        HashConfig conf7 = conf6.withShrinkCondition(new Predicate<HashContainer>() {
            @Override
            public boolean test(HashContainer hashContainer) {
                return false;
            }
        });
        assertEquals(conf6, conf7.withShrinkCondition(shrinkCondition));
    }

    @Test
    public void testToString() {
        assertEquals(
                "HashConfig{" +
                        "getMinLoad=" + (1.0 / 3.0) + ", getTargetLoad=" + 0.5 + ", " +
                        "getMaxLoad=" + (2.0 / 3.0) + ", getGrowFactor=" + 2.0 + ", " +
                        "getShrinkCondition=null}",
                HashConfig.getDefault().withMinLoad(1.0 / 3.0).withTargetLoad(0.5)
                        .withMaxLoad(2.0 / 3.0).withGrowFactor(2.0).withShrinkCondition(null)
                        .toString()
        );
    }
}
