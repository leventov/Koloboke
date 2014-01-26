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

import net.openhft.jpsg.collect.MethodContext;
import net.openhft.jpsg.collect.Method;
import net.openhft.jpsg.collect.MethodGenerator;


public abstract class BulkMethod implements Method {

    protected BulkMethodGenerator gen;
    protected MethodContext cxt;

    public EntryType entryType() {
        return EntryType.SIMPLE;
    }

    @Override
    public final void init(MethodGenerator g, MethodContext c) {
        this.gen = (BulkMethodGenerator) g;
        this.cxt = c;
    }

    public void beginning() {
    }

    public void rightBeforeLoop() {
    }

    final void escapeIfEmpty() {
        gen.lines(
                "if (isEmpty())",
                "    return;"
        );
    }

    public abstract void loopBody();

    public void end() {
    }
}
