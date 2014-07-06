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
        gen.lines("initForRehash(newCapacity);");
        gen.lines("mc++; // modCount is incremented in initForRehash()");
        if (!parallelKV(cxt)) {
            gen.lines(cxt.keyUnwrappedRawType() + "[] newKeys = set;");
        } else {
            gen.lines(tableType(cxt) + "[] newTab = table;");
        }
        String table = parallelKV(cxt) ? "newTab" : "newKeys";
        gen.lines(isLHash(cxt) ?
                        "int capacityMask = " + capacityMask(cxt, table) + ";" :
                        "int capacity = " + table + ".length;");
        if (cxt.isMapView() && !parallelKV(cxt))
            gen.lines(cxt.valueUnwrappedType() + "[] newVals = values;");
    }

    @Override
    public void loopBody() {
        String key = gen.unwrappedKey();
        if (isDHash(cxt))
            gen.lines("int hash;");
        gen.lines("int index;");
        String firstKey = KeySearch.firstKey(cxt, "newTab", "newKeys", key, true, false, true);
        gen.ifBlock(isNotFree(cxt, firstKey)); {
            KeySearch.innerLoop(gen, cxt, index -> {
                gen.ifBlock(isNotFree(cxt, readKeyOnly(cxt, "newTab", "newKeys", index))); {
                    if (!index.equals("index"))
                        gen.lines("index = " + index + ";");
                    gen.lines("break;");
                } gen.blockEnd();
            }, false).generate();
        } gen.blockEnd();
        writeKeyAndValue(gen, cxt, "newTab", "newKeys", "newVals", "index", key,
                gen::unwrappedValue, false, cxt.isMapView());
    }
}
