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

package com.koloboke.jpsg.collect.algo.hash

import com.koloboke.jpsg.collect.algo.hash.HashMethodGeneratorCommons.capacityMask
import com.koloboke.jpsg.collect.algo.hash.HashMethodGeneratorCommons.isDHash
import com.koloboke.jpsg.collect.algo.hash.HashMethodGeneratorCommons.isFree
import com.koloboke.jpsg.collect.algo.hash.HashMethodGeneratorCommons.isLHash
import com.koloboke.jpsg.collect.algo.hash.HashMethodGeneratorCommons.isNotFree
import com.koloboke.jpsg.collect.algo.hash.HashMethodGeneratorCommons.keyArrayType
import com.koloboke.jpsg.collect.algo.hash.HashMethodGeneratorCommons.parallelKV
import com.koloboke.jpsg.collect.algo.hash.HashMethodGeneratorCommons.readKeyOnly
import com.koloboke.jpsg.collect.algo.hash.HashMethodGeneratorCommons.tableType
import com.koloboke.jpsg.collect.algo.hash.HashMethodGeneratorCommons.writeKeyAndValue
import com.koloboke.jpsg.collect.bulk.BulkMethod

class Rehash : BulkMethod() {

    override fun rightBeforeLoop() {
        gen.lines("initForRehash(newCapacity);")
        gen.lines("mc++; // modCount is incremented in initForRehash()")
        if (!parallelKV(cxt)) {
            gen.lines("${keyArrayType(cxt)}[] newKeys = set;")
        } else {
            gen.lines(tableType(cxt) + "[] newTab = table;")
        }
        val table = if (parallelKV(cxt)) "newTab" else "newKeys"
        gen.lines(if (isLHash(cxt))
            "int capacityMask = " + capacityMask(cxt, table) + ";"
        else
            "int capacity = $table.length;")
        if (cxt.isMapView && !parallelKV(cxt))
            gen.lines(cxt.valueUnwrappedType() + "[] newVals = values;")
    }

    override fun loopBody() {
        val key = gen.unwrappedKey()
        if (isDHash(cxt))
            gen.lines("int hash;")
        gen.lines("int index;")
        val firstKey = KeySearch.firstKey(cxt, "newTab", "newKeys", key, true, false, true)
        gen.ifBlock(isNotFree(cxt, firstKey))
        run {
            KeySearch.innerLoop(gen, cxt, { firstIndex, index ->
                gen.ifBlock(isFree(cxt, readKeyOnly(cxt, "newTab", "newKeys", firstIndex)))
                run {
                    if (index != "index")
                        gen.lines("index = $index;")
                    gen.lines("break;")
                }
                gen.blockEnd()
            }, false).generate()
        }
        gen.blockEnd()
        writeKeyAndValue(gen, cxt, "newTab", "newKeys", "newVals", "index", key,
                { gen.unwrappedValue() }, false, cxt.isMapView)
    }
}
