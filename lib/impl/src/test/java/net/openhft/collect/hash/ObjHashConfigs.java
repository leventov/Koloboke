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

package net.openhft.collect.hash;

import java.util.*;


public final class ObjHashConfigs {

    public static List<ObjHashConfig> all() {
        List<ObjHashConfig> configs = Arrays.<ObjHashConfig>asList(new ObjHashConfig() {
            @Override
            public <T extends ObjHashFactory<T>> T apply(T factory) {
                return factory.withNullKeyAllowed(true);
            }
        });
        List<ObjHashConfig> all = new ArrayList<ObjHashConfig>();
        for (final ObjHashConfig config : configs) {
            for (final HashConfig hashConf : HashConfigs.all()) {
                all.add(new ObjHashConfig() {
                    @Override
                    public <T extends ObjHashFactory<T>> T apply(T factory) {
                        factory = factory.withHashConfig(hashConf);
                        return config.apply(factory);
                    }
                });
            }
        }
        return all;
    }

    private ObjHashConfigs() {}
}
