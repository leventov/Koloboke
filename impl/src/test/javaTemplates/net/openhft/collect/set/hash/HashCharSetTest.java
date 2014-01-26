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

package net.openhft.collect.set.hash;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import com.google.common.collect.testing.features.CollectionFeature;
import junit.framework.*;
import net.openhft.collect.*;
import net.openhft.collect.set.*;


public class HashCharSetTest extends TestCase {

    public static Test suite() {
        CharSetTestSuiteBuilder builder = new CharSetTestSuiteBuilder()
                .setSamples(CharSamples.allKeys());
        builder.setFactories(Lists.transform(CharHashConfigs.all(),
                new Function<CharHashConfig, CharSetFactory/* if obj elem //<Object>// endif */>() {
                    @Override
                    public CharSetFactory/* if obj elem //<Object>// endif */ apply(
                            CharHashConfig config) {
                        return HashCharSets.getDefaultFactory().withConfig(config);
                    }
                }));
        /* if obj elem */
        builder.withSpecialFeatures(CollectionFeature.ALLOWS_NULL_VALUES);
        /* endif */
        return builder.create();
    }
}
