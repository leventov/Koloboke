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


public final class ToTypedArray extends BulkMethod {

    @Override
    public void beginning() {
        gen.lines(
                "int size = size();",
                "if (a.length < size) {",
                "    Class<?> elementType = a.getClass().getComponentType();",
                "    a = (T[]) java.lang.reflect.Array.newInstance(elementType, size);",
                "}",
                "if (size == 0) {",
                "    if (a.length > 0)",
                "        a[0] = null;",
                "    return a;",
                "}",
                "int resultIndex = 0;"
        );
    }

    @Override
    public void loopBody() {
        String viewValue = gen.viewElem();
        if (cxt.isPrimitiveView()) {
            viewValue = ((PrimitiveType) cxt.viewOption()).className + ".valueOf(" + viewValue + ")";
        }
        gen.lines("a[resultIndex++] = (T) " + viewValue + ";");
    }

    @Override
    public void end() {
        gen.lines(
                "if (a.length > resultIndex)",
                "    a[resultIndex] = null;"
        );
        gen.ret("a");
    }
}
