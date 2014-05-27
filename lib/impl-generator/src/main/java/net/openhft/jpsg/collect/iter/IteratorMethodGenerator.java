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

import net.openhft.jpsg.collect.Method;
import net.openhft.jpsg.collect.MethodGenerator;

import static net.openhft.jpsg.collect.iter.IterMethod.*;


public abstract class IteratorMethodGenerator extends MethodGenerator {

    @Override
    protected void generateLines(Method method) {
        if (!(method instanceof IterMethod)) {
            throw new IllegalArgumentException();
        }
        IterMethod m = (IterMethod) method;
        if (m == FIELDS) {
            generateFields();
        } else if (m == CONSTRUCTOR) {
            generateConstructor();
        } else if (m == HAS_NEXT) {
            generateHasNext();
        } else if (m == NEXT) {
            generateNext();
        } else if (m == REMOVE) {
            generateRemove();
        } else if (m == FOR_EACH_REMAINING) {
            generateForEachRemaining();
        } else {
            throw new IllegalStateException();
        }
    }

    protected abstract void generateFields();

    protected abstract void generateConstructor();

    protected abstract void generateHasNext();

    protected abstract void generateNext();

    protected abstract void generateRemove();

    protected abstract void generateForEachRemaining();
}
