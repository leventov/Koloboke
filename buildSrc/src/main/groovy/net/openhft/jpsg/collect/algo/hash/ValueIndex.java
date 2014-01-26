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

package net.openhft.jpsg.collect.algo.hash;

import net.openhft.jpsg.collect.bulk.BulkMethod;


public class ValueIndex extends BulkMethod {

    @Override
    public void beginning() {
        gen.lines("if (isEmpty())");
        gen.lines("    return -1;");
        if (cxt.isObjectValue())
            gen.lines(cxt.valueType() + " val = (" + cxt.valueType() + ") value;");
        gen.lines("int index = -1;");
    }

    @Override
    public void loopBody() {
        String cond;
        if (cxt.isPrimitiveValue()) {
            cond = "value == " + gen.value();
        } else if (cxt.isObjectValue()) {
            cond = "valueEquals(val, " + gen.value() + ")";
        } else if (cxt.isNullValue()) {
            cond = gen.value() + " == null";
        } else {
            throw new IllegalStateException();
        }
        gen.ifBlock(cond);
        gen.lines("index = " + ((HashBulkMethodGenerator) gen).index() + ";");
        gen.lines("break;");
        gen.blockEnd();
    }

    @Override
    public void end() {
        gen.ret("index");
    }
}
