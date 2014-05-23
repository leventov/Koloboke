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


package net.openhft.collect.impl;

import javax.annotation.Nonnull;

import java.util.Map;


public final class CommonMapOps {

    @SuppressWarnings("unchecked")
    public static boolean equals(@Nonnull InternalMapOps<?, ?> map, Object obj) {
        if (map == obj) { return true; }
        if (!(obj instanceof Map)) {
            return false;
        }
        Map<?, ?> that = (Map<?, ?>) obj;
        if (that.size() != map.size()) { return false; }
        try {
            return map.containsAllEntries(that);
        } catch (ClassCastException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
    }


    private CommonMapOps() {}
}
