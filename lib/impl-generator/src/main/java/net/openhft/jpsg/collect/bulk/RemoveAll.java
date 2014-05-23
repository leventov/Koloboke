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

public class RemoveAll extends BulkRemoveUsingAnotherCollection {

    @Override
    public void beginning() {
        super.beginning();
        gen.lines(
                "if (isEmpty() || c.isEmpty())",
                "    return false;",
                "boolean changed = false;"
        );
    }

    @Override
    public void loopBody() {
        gen.ifBlock("c.contains(" + gen.viewValues() + ")");
        gen.remove();
        gen.lines("changed = true;");
        gen.blockEnd();
    }
}
