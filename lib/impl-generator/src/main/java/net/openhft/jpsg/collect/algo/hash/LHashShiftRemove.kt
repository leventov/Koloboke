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

package net.openhft.jpsg.collect.algo.hash

import net.openhft.jpsg.collect.MethodContext
import net.openhft.jpsg.collect.MethodGenerator

import net.openhft.jpsg.collect.algo.hash.HashMethodGeneratorCommons.doubleSizedParallel
import net.openhft.jpsg.collect.algo.hash.HashMethodGeneratorCommons.eraseSlot
import net.openhft.jpsg.collect.algo.hash.HashMethodGeneratorCommons.isFree
import net.openhft.jpsg.collect.algo.hash.HashMethodGeneratorCommons.keyHash
import net.openhft.jpsg.collect.algo.hash.HashMethodGeneratorCommons.readKeyOrEntry
import net.openhft.jpsg.collect.algo.hash.HashMethodGeneratorCommons.readValue
import net.openhft.jpsg.collect.algo.hash.HashMethodGeneratorCommons.slots
import net.openhft.jpsg.collect.algo.hash.HashMethodGeneratorCommons.writeKeyAndValue


internal open class LHashShiftRemove(val g: MethodGenerator, val cxt: MethodContext, val index: String, val table: String,
                                     val values: String) {

    open fun generate() {
        g.incrementModCount()
        closeDeletion()
        postRemoveHook()
    }

    fun closeDeletion() {
        g.lines("int indexToRemove = $index;")
        g.lines("int indexToShift = indexToRemove;")
        g.lines("int shiftDistance = " + slots(1, cxt) + ";")
        g.lines("while (true)").block()
        run {
            g.lines("indexToShift = (indexToShift - " + slots(1, cxt) + ") & capacityMask;")
            g.lines(cxt.keyUnwrappedType() + " keyToShift;")
            var key = readKeyOrEntry(cxt, "indexToShift")
            if (cxt.isObjectKey && rawKeys()) {
                key = "(" + cxt.keyType() + ") " + key
            }
            g.ifBlock(isFree(cxt, "(keyToShift = $key)"))
            run { g.lines("break;") }
            g.blockEnd()
            val keyDistance = "((" + keyHash(cxt, "keyToShift", false) +
                    " - indexToShift) & capacityMask)"
            var shiftCondition = keyDistance + " >= shiftDistance"
            val shiftPrecondition = additionalShiftPrecondition()
            if (!shiftPrecondition.isEmpty()) {
                shiftCondition = "($shiftPrecondition) && ($shiftCondition)"
            }
            g.ifBlock(shiftCondition)
            run {
                beforeShift()
                writeKeyAndValue(g, cxt, table, "keys", values, "indexToRemove", "keyToShift",
                        { readValue(cxt, table, values, "indexToShift") }, false,
                        cxt.hasValues())
                g.lines("indexToRemove = indexToShift;")
                g.lines("shiftDistance = " + slots(1, cxt) + ";")
            }
            g.elseBlock()
            run {
                val increment = if (doubleSizedParallel(cxt)) " += 2" else "++"
                g.lines("shiftDistance$increment;")
                if (cxt.isPrimitiveKey) {
                    // if keys are primitives, free value could change during the close deletion
                    // loop, and if it then be removed immediately (i'm not even sure this
                    // is possible), then we hang on forever, because `keyToShift == free`
                    // is the only way to break the close deletion loop.
                    // TODO understand when this check could be avoided
                    g.ifBlock("indexToShift == " + slots(1, cxt) + " + " + index)
                    run { g.concurrentMod() }
                    g.blockEnd()
                }
            }
            g.blockEnd()
        }
        g.blockEnd()
        eraseSlot(g, cxt, "indexToRemove", "indexToRemove", !rawKeys(), values)
    }

    fun postRemoveHook() {
        g.lines("postRemoveHook();")
    }

    open fun rawKeys(): Boolean {
        return false
    }

    open fun additionalShiftPrecondition(): String {
        return ""
    }

    open fun beforeShift() {
        // no-op by default
    }
}
