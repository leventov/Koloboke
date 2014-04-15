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

import net.openhft.collect.Container;

import java.util.Collection;
import java.util.Map;


public final class Containers {

    public static long sizeAsLong(Collection c) {
        return c instanceof Container ? ((Container) c).sizeAsLong() : (long) c.size();
    }

    public static long sizeAsLong(Map m) {
        return m instanceof Container ? ((Container) m).sizeAsLong() : (long) m.size();
    }

    private Containers() {}
}
