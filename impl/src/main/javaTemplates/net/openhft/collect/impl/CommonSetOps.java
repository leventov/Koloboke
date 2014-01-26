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

import org.jetbrains.annotations.NotNull;

import java.util.Set;


public final class CommonSetOps {

    public static boolean equals(@NotNull Set<?> set, Object obj) {
        if (set == obj)
            return true;
        if (!(obj instanceof Set))
            return false;
        Set<?> another = (Set<?>) obj;
        if (another.size() != set.size())
            return false;
        try {
            return set.containsAll(another);
        } catch (ClassCastException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
    }


    private CommonSetOps() {}
}
