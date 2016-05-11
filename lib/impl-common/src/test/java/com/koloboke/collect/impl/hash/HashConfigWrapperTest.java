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

package com.koloboke.collect.impl.hash;

import com.koloboke.collect.hash.HashConfig;
import com.koloboke.collect.impl.ScalerTest;
import org.junit.Test;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assert.assertThat;


public class HashConfigWrapperTest {

    private static final HashConfigWrapper[] WRAPPERS = new HashConfigWrapper[] {
            new HashConfigWrapper(HashConfig.getDefault()),
            new HashConfigWrapper(HashConfig.getDefault()
                    .withMinLoad(0.25).withTargetLoad(0.5).withMaxLoad(0.75)),
            new HashConfigWrapper(HashConfig.getDefault()
                    .withMinLoad(0.0).withTargetLoad(0.01).withMaxLoad(0.02)),
            new HashConfigWrapper(HashConfig.getDefault().withGrowthFactor(1.0001)
                    .withMaxLoad(1.0).withTargetLoad(0.99).withMinLoad(0.98))
    };

    @Test
    public void testMinCapacityMaxSizeInverseInvariant() {
        for (HashConfigWrapper w : WRAPPERS) {
            for (int size : ScalerTest.ints) {
                int minCapacity = w.minCapacity(size);
                if (minCapacity < Integer.MAX_VALUE)
                    assertThat(w.toString(), w.maxSize(minCapacity), greaterThanOrEqualTo(size));
            }
            for (long size : ScalerTest.longs) {
                long minCapacity = w.minCapacity(size);
                if (minCapacity < Long.MAX_VALUE)
                    assertThat(w.toString(), w.maxSize(minCapacity), greaterThanOrEqualTo(size));
            }
        }
    }
}
