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

import static net.openhft.jpsg.collect.algo.hash.HashMethodGeneratorCommons.*;

public class Rehash extends BulkMethod {

    @Override
    public void rightBeforeLoop() {
        gen.lines(
                "initForRehash(newCapacity);",
                "mc++; // modCount is incremented in initForRehash()",
                cxt.keyUnwrappedRawType() + "[] newKeys = set;",
                "int capacity = newKeys.length;"
        );
        if (cxt.isMapView())
            gen.lines(cxt.valueUnwrappedType() + "[] newVals = values;");
    }

    @Override
    public void loopBody() {
        String key = gen.unwrappedKey();
        gen.lines("int hash, index;");
        gen.ifBlock(isNotFree(cxt, firstKey(cxt, "newKeys", key, true)));
        gen.lines(step());
        gen.lines("do").block();
        gen.lines(nextIndex());
        gen.unIndent();
        gen.lines("} while (" + isNotFree(cxt, "newKeys[index]") + ");");
        gen.blockEnd();
        gen.lines("newKeys[index] = " + key + ";");
        if (cxt.isMapView())
            gen.lines("newVals[index] = " + gen.unwrappedValue() + ";");
    }
}
