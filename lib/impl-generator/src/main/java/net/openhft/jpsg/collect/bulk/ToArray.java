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

package net.openhft.jpsg.collect.bulk;

import net.openhft.jpsg.PrimitiveType;


public class ToArray extends BulkMethod {

    @Override
    public void beginning() {
        String arrayType = "Object";
        if (cxt.isPrimitiveView() && !cxt.genericVersion()) {
            arrayType = ((PrimitiveType) cxt.viewOption()).standalone;
        }
        gen.lines(
                "int size = size();",
                arrayType + "[] result = new " + arrayType + "[size];",
                "if (size == 0)",
                "    return result;",
                "int resultIndex = 0;"
        );
    }

    @Override
    public void loopBody() {
        gen.lines("result[resultIndex++] = " + gen.viewElem() + ";");
    }

    @Override
    public void end() {
        gen.ret("result");
    }
}
