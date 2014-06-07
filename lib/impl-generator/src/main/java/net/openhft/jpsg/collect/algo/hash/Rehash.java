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

public final class Rehash extends BulkMethod {

    @Override
    public void rightBeforeLoop() {
        gen.lines(
                "initForRehash(newCapacity);",
                "mc++; // modCount is incremented in initForRehash()",
                cxt.keyUnwrappedRawType() + "[] newKeys = set;",
                isLHash(cxt) ?
                        "int capacityMask = newKeys.length - 1;" :
                        "int capacity = newKeys.length;"
        );
        if (cxt.isMapView())
            gen.lines(cxt.valueUnwrappedType() + "[] newVals = values;");
    }

    @Override
    public void loopBody() {
        String key = gen.unwrappedKey();
        if (isDHash(cxt))
            gen.lines("int hash;");
        gen.lines("int index;");
        gen.ifBlock(isNotFree(cxt, KeySearch.firstKey(cxt, "newKeys", key, true, false))); {
            KeySearch.innerLoop(gen, cxt, index -> {
                gen.ifBlock(isNotFree(cxt, "newKeys[" + index + "]")); {
                    if (!index.equals("index"))
                        gen.lines("index = " + index + ";");
                    gen.lines("break;");
                } gen.blockEnd();
            }, false).generate();
        } gen.blockEnd();
        gen.lines("newKeys[index] = " + key + ";");
        if (cxt.isMapView())
            gen.lines("newVals[index] = " + gen.unwrappedValue() + ";");
    }
}
