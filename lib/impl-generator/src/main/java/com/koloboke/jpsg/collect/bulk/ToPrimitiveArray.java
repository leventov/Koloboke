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

package com.koloboke.jpsg.collect.bulk;

import com.koloboke.jpsg.PrimitiveType;


public final class ToPrimitiveArray extends BulkMethod {

    @Override
    public void beginning() {
        PrimitiveType elemType = (PrimitiveType) cxt.viewOption();
        gen.lines(
                "int size = size();",
                "if (a.length < size)",
                "    a = new " + elemType.standalone + "[size];",
                "if (size == 0) {",
                "    if (a.length > 0)",
                "        a[0] = " + elemType.formatValue("0") + ";",
                "    return a;",
                "}",
                "int resultIndex = 0;"
        );
    }

    @Override
    public void loopBody() {
        gen.lines("a[resultIndex++] = " + gen.viewElem() + ";");
    }

    @Override
    public void end() {
        PrimitiveType elemType = (PrimitiveType) cxt.viewOption();
        gen.lines(
                "if (a.length > resultIndex)",
                "    a[resultIndex] = " + elemType.formatValue("0") + ";"
        );
        gen.ret("a");
    }
}
