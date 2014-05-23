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

package net.openhft.jpsg.collect.iter;

import net.openhft.jpsg.collect.MethodContext;
import net.openhft.jpsg.collect.Method;
import net.openhft.jpsg.collect.MethodGenerator;


public enum IterMethod implements Method {
    FIELDS, CONSTRUCTOR, HAS_NEXT, MOVE_NEXT, NEXT, ELEM, KEY, VALUE, SET_VALUE, REMOVE,
    FOR_EACH_REMAINING, FOR_EACH_FORWARD;

    public static IterMethod forName(String name) {
        for (IterMethod method : values()) {
            if (method.name().replace("_", "").equalsIgnoreCase(name)) {
                return method;
            }
        }
        throw new RuntimeException("Unknown method: " + name);
    }

    @Override
    public void init(MethodGenerator g, MethodContext c) {
    }

    @Override
    public Class<? extends MethodGenerator> generatorBase() {
        throw new UnsupportedOperationException();
    }
}
