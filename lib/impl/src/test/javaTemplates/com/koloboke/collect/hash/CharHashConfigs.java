/* with char|byte|short|int|long t */
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

import java.util.*;


public final class CharHashConfigs {

    public static List<CharHashConfig> all() {
        List<CharHashConfig> configs = Arrays.asList(
                new CharHashConfig() {
                    @Override
                    public <T extends CharHashFactory<T>> T apply(T factory) {
                        return factory;
                    }
                }
                /* if !(float|double t) */
                , new CharHashConfig() {

                    @Override
                    public <T extends CharHashFactory<T>> T apply(T factory) {
                        return factory.withKeysDomain(
                                /* const t 1 */(char) 1/* endconst */,
                                /* const t max */Character.MAX_VALUE/* endconst*/);
                    }
                }
                /* endif */
        );
        List<CharHashConfig> all = new ArrayList<CharHashConfig>();
        for (final CharHashConfig config : configs) {
            for (final HashConfig hashConf : HashConfigs.all()) {
                all.add(new CharHashConfig() {
                    @Override
                    public <T extends CharHashFactory<T>> T apply(T factory) {
                        factory = factory.withHashConfig(hashConf);
                        return config.apply(factory);
                    }
                });
            }
        }
        return all;
    }

    private CharHashConfigs() {}
}
