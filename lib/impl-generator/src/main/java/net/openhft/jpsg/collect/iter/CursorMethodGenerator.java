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


public abstract class CursorMethodGenerator extends MethodGenerator {

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
        } else if (m == MOVE_NEXT) {
            generateMoveNext();
        } else if (m == REMOVE) {
            generateRemove();
        } else if (m == FOR_EACH_FORWARD) {
            generateForEachForward();
        } else if (m == ELEM) {
            if (cxt.isKeyView()) {
                generateKey();
            } else if (cxt.isValueView()) {
                generateValue();
            } else if (cxt.isEntryView()) {
                generateEntry();
            } else {
                throw new IllegalStateException();
            }
        } else if (cxt.isMapView()) {
            if (m == KEY) {
                generateKey();
            } else if (m == VALUE) {
                generateValue();
            } else if (m == SET_VALUE) {
                generateSetValue();
            } else {
                throw new IllegalStateException();
            }
        } else {
            throw new IllegalStateException();
        }
    }

    protected abstract void generateFields();

    protected abstract void generateConstructor();

    protected abstract void generateMoveNext();

    protected abstract void generateKey();

    protected abstract void generateValue();

    protected abstract void generateSetValue();

    protected abstract void generateEntry();

    protected abstract void generateRemove();

    protected abstract void generateForEachForward();
}
