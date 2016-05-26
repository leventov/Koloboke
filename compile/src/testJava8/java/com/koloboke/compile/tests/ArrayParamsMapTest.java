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

package com.koloboke.compile.tests;

//import com.google.common.collect.Lists;
//import com.koloboke.collect.testing.ObjSamples;
//import com.koloboke.collect.testing.map.HyperCharShortMapTestSuiteBuilder;
//import junit.framework.Test;
//import junit.framework.TestCase;


//public class ArrayParamsMapTest extends TestCase {
//    public static Test suite() {
//        HyperCharShortMapTestSuiteBuilder builder = new HyperCharShortMapTestSuiteBuilder()
//                .setKeySamples(ObjSamples.allKeys()).setValueSamples(ObjSamples.allKeys());
//        /* define pp */
//        /* if obj key obj value //Object, Object, // elif obj key || obj value //Object, // endif */
//        /* enddefine */
//        builder.setFactories(Lists.transform(/* if !(float|double key) */CharHashConfigs
//                /* elif float|double key //HashConfigs// endif */.all(),
//                new Function</* if !(float|double key) */CharHashConfig
//                            /* elif float|double key //HashConfig// endif */,
//                        CharShortMapFactory</*pp*/?>>() {
//                    @Override
//                    public CharShortMapFactory</*pp*/?> apply(
//                            /* if !(float|double key) */CharHashConfig
//                            /* elif float|double key //HashConfig// endif */ config) {
//                        return /* if !(float|double key) */
//                                config.apply(HashCharShortMaps.getDefaultFactory())
//                                /* elif float|double key */
//                        HashCharShortMaps.getDefaultFactory().withHashConfig(config)
//                                /* endif */;
//                    }
//                }));
//        /* if !(obj key) */
//        builder.withSpecialFeatures(MapFeature.RESTRICTS_KEYS);
//        /* elif obj key */
//        builder.withSpecialFeatures(MapFeature.ALLOWS_NULL_KEYS);
//        /* endif */
//        /* if !(obj value) */
//        builder.withSpecialFeatures(MapFeature.RESTRICTS_VALUES);
//        /* elif obj value */
//        builder.withSpecialFeatures(MapFeature.ALLOWS_NULL_VALUES);
//        /* endif */
//        return builder.create();
//    }
//
//}
