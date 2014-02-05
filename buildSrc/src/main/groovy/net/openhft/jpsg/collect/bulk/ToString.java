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


public class ToString extends BulkMethod {

    @Override
    public void beginning() {
        gen.lines(
                "if (isEmpty())",
                "    return \"" + (cxt.isMapView() ? "{}" : "[]") + "\";",
                "StringBuilder sb = new StringBuilder();",
                "int elementCount = 0;"
        );
    }

    @Override
    public void loopBody() {
        String thisContainer = cxt.isMapView() ? "Map" : "Collection";
        thisContainer = "\"(this " + thisContainer + ")\"";
        if (cxt.isMapView() || cxt.isEntryView()) {
            gen.lines("sb.append(' ');");
            String key = gen.key();
            if (cxt.isObjectKey()) {
                key = key + " != this ? " + key + " : " + thisContainer;
            }
            gen.lines("sb.append(" + key + ");");
            gen.lines("sb.append(\'=\');");
            String value = gen.value();
            if (cxt.isObjectValue()) {
                gen.lines("Object val = " + value + ";");

                value = "val != this ? val : " + thisContainer;
            }
            gen.lines("sb.append(" + value + ");");
            gen.lines("sb.append(',');");
        } else {
            String value = gen.viewElem();
            if (cxt.isObjectView()) {
                value = value + " != this ? " + value + " : " + thisContainer;
            }
            gen.lines("sb.append(' ').append(" + value + ").append(',');");
        }
        gen.lines("// heuristic to reduce data recopying inside string builder");
        gen.ifBlock("++elementCount == 8");
        gen.lines("int expectedLength = sb.length() * (size() / 8);");
        gen.lines("sb.ensureCapacity(expectedLength + (expectedLength / 2));");
        gen.blockEnd();
    }

    @Override
    public void end() {
        gen.lines(
                "sb.setCharAt(0, \'" + (cxt.isMapView() ? "{" : "[") + "\');",
                "sb.setCharAt(sb.length() - 1, \'" + (cxt.isMapView() ? "}" : "]") + "\');"
        );
        gen.ret("sb.toString()");
    }
}
