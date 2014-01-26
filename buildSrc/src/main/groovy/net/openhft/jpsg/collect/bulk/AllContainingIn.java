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

public class AllContainingIn extends BulkMethod {

    @Override
    public EntryType entryType() {
        return EntryType.REUSABLE;
    }

    @Override
    public void beginning() {
        gen.lines(
                "if (isEmpty())",
                "    return true;",
                "boolean containsAll = true;"
        );
    }

    @Override
    public void loopBody() {
        String method = cxt.isMapView() ? "m.containsEntry" : "c.contains";
        gen.ifBlock("!" + method + "(" + gen.viewValues() + ")");
        gen.lines(
                "containsAll = false;",
                "break;"
        ).blockEnd();
    }

    @Override
    public void end() {
        gen.ret("containsAll");
    }
}
