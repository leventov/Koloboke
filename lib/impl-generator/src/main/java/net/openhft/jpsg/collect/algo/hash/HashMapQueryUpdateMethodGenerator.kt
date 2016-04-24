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

import net.openhft.jpsg.PrimitiveType
import net.openhft.jpsg.collect.*
import net.openhft.jpsg.collect.mapqu.MapQueryUpdateMethod
import net.openhft.jpsg.collect.mapqu.MapQueryUpdateMethodGenerator

import java.lang.Math.min
import java.lang.StrictMath.max
import net.openhft.jpsg.collect.Permission.REMOVE
import net.openhft.jpsg.collect.Permission.SET_VALUE
import net.openhft.jpsg.collect.algo.hash.HashMethodGeneratorCommons.assertHash
import net.openhft.jpsg.collect.algo.hash.HashMethodGeneratorCommons.capacityMask
import net.openhft.jpsg.collect.algo.hash.HashMethodGeneratorCommons.copyArrays
import net.openhft.jpsg.collect.algo.hash.HashMethodGeneratorCommons.copyTable
import net.openhft.jpsg.collect.algo.hash.HashMethodGeneratorCommons.doubleSizedParallel
import net.openhft.jpsg.collect.algo.hash.HashMethodGeneratorCommons.isDHash
import net.openhft.jpsg.collect.algo.hash.HashMethodGeneratorCommons.isFree
import net.openhft.jpsg.collect.algo.hash.HashMethodGeneratorCommons.isLHash
import net.openhft.jpsg.collect.algo.hash.HashMethodGeneratorCommons.isNotFree
import net.openhft.jpsg.collect.algo.hash.HashMethodGeneratorCommons.isNotRemoved
import net.openhft.jpsg.collect.algo.hash.HashMethodGeneratorCommons.isQHash
import net.openhft.jpsg.collect.algo.hash.HashMethodGeneratorCommons.isRemoved
import net.openhft.jpsg.collect.algo.hash.HashMethodGeneratorCommons.localTableVar
import net.openhft.jpsg.collect.algo.hash.HashMethodGeneratorCommons.parallelKV
import net.openhft.jpsg.collect.algo.hash.HashMethodGeneratorCommons.possibleRemovedSlots
import net.openhft.jpsg.collect.algo.hash.HashMethodGeneratorCommons.readKeyOrEntry
import net.openhft.jpsg.collect.algo.hash.HashMethodGeneratorCommons.readValue
import net.openhft.jpsg.collect.algo.hash.HashMethodGeneratorCommons.removed
import net.openhft.jpsg.collect.algo.hash.HashMethodGeneratorCommons.tableEntryType
import net.openhft.jpsg.collect.algo.hash.HashMethodGeneratorCommons.tableType
import net.openhft.jpsg.collect.algo.hash.HashMethodGeneratorCommons.writeKey
import net.openhft.jpsg.collect.algo.hash.HashMethodGeneratorCommons.writeKeyAndValue
import net.openhft.jpsg.collect.algo.hash.HashMethodGeneratorCommons.writeValue
import net.openhft.jpsg.collect.algo.hash.KeySearch.curAssignment
import net.openhft.jpsg.collect.algo.hash.KeySearch.innerLoopBodies
import net.openhft.jpsg.collect.algo.hash.KeySearch.tryPrecomputeStep
import net.openhft.jpsg.collect.mapqu.BasicMapQueryUpdateOp.*
import net.openhft.jpsg.collect.mapqu.Branch.KEY_ABSENT
import net.openhft.jpsg.collect.mapqu.Branch.KEY_PRESENT


class HashMapQueryUpdateMethodGenerator : MapQueryUpdateMethodGenerator() {

    private var method: MapQueryUpdateMethod? = null
    private var beforeBranches: Int = 0
    private var firstPresentLine: Int = 0
    private var firstAbsentLine: Int = 0

    // inline index/insert state

    private var presentBranchSize: Int = 0
    private var absentBranchSize: Int = 0
    private var separatePresent: Boolean = false
    private var separateAbsentFreeSlot: Boolean = false
    private var separateAbsentRemovedSlot: Boolean = false
    // for generating key absent branch
    private var removedSlot: Boolean = false
    private var earlyAbsentLabel: Boolean = false

    private var presentValueUsed = false
    // if values copy should be lifted from branches
    private var commonValuesCopy = false
    private var commonTableCopy = false

    private var commonCapacityMaskCopy = false

    private var index = "index"

    private fun inline(): Boolean {
        return method!!.inline() || parallelKV(cxt) && !doubleSizedParallel(cxt) && presentValueUsed
    }

    override fun defaultValue(): String {
        if (cxt.isObjectValue || cxt.genericVersion()) return "null"
        return "defaultValue()"
    }

    override fun valueEquals(valueToCompare: String): String {
        if (cxt.isObjectValue) {
            return "nullableValueEquals(" + value() + ", (V) " + valueToCompare + ")"
        } else {
            return unwrappedValue() + " == " + unwrapValue(valueToCompare)
        }
    }

    override fun insert(value: String) {
        permissions.add(Permission.INSERT)
        if (inline()) {
            incrementModCount()
            writeKeyAndValue(this, cxt, table(), "keys", values(), indexF(), unwrappedKey(),
                    { unwrapValue(value) }, true, cxt.isMapView)
            if (removedSlot) {
                lines("postRemovedSlotInsertHook();")
            } else {
                lines(if (possibleRemovedSlots(cxt))
                    "postFreeSlotInsertHook();"
                else
                    "postInsertHook();")
            }
        } else {
            lines("insertAt(insertionIndex, " + unwrappedKey() + ", " + value + ");")
        }
    }

    override fun generateLines(m: Method) {
        method = m as MapQueryUpdateMethod
        determineBranchFeatures()
        if (inline()) {
            generateInline()
        } else {
            generateNotInlined()
        }
        replaceKey()
    }

    private fun generateNotInlined() {
        if (method!!.baseOp() == INSERT)
            permissions.add(Permission.INSERT)
        method!!.beginning()
        getIndex()
        if (commonValuesCopy)
            copyValues()
        if (commonTableCopy)
            copyTable(this, cxt)
        beforeBranches = lines.size
        if (absentBranchSize == 0 && presentBranchSize == 0)
            throw IllegalStateException()
        if (absentBranchSize == 0) {
            ifBlock(indexPresent())
            generatePresent()
            blockEnd()
        } else if (presentBranchSize == 0) {
            ifBlock(indexAbsent())
            generateAbsent(false)
            blockEnd()
        } else {
            if (method!!.mostProbableBranch() == KEY_PRESENT) {
                ifBlock(indexPresent())
                generatePresent()
                elseBlock()
                generateAbsent(false)
            } else {
                ifBlock(indexAbsent())
                generateAbsent(false)
                elseBlock()
                generatePresent()
            }
            blockEnd()
        }
        liftValueCopies()
    }

    private fun liftValueCopies() {
        if (lines[firstAbsentLine] == lines[firstPresentLine]) {
            // copying values to local array in both branches
            val first = min(firstAbsentLine, firstPresentLine)
            var last = max(firstPresentLine, firstAbsentLine)
            val valuesCopyLine = lines.removeAt(last).substring(4)
            lines.removeAt(first)
            lines.add(beforeBranches, valuesCopyLine)
            beforeBranches++
            last--
            if (lines[first] == lines[last]) {
                // copying current value to local
                var valCopyLine = lines.removeAt(last).substring(4)
                lines.removeAt(first)
                lines.add(beforeBranches, valCopyLine)
                beforeBranches++
                last--
                if (method!!.baseOp() == CUSTOM_INSERT && lines[first] == lines[last]) {
                    // int index = insertionIndex.get() was the first equal line
                    valCopyLine = lines.removeAt(last).substring(4)
                    lines.removeAt(first)
                    lines.add(beforeBranches, valCopyLine)
                }
            }
        }
    }

    private fun replaceKey() {
        if (cxt.isFloatingKey && !cxt.internalVersion()) {
            replaceAll(0, KEY_SUB, "k")
            var key = "key"
            if (cxt.genericVersion() &&
                    !permissions.contains(Permission.INSERT) &&
                    !permissions.contains(Permission.SET_VALUE)) {
                // key is Object, need to cast before unwrapping
                key = "(" + (cxt.keyOption() as PrimitiveType).className + ") " + key
            }
            val keyUnwrap = indent + cxt.keyUnwrappedType() + " k = " + unwrapKey(key) + ";"
            lines.add(0, keyUnwrap)
        } else if (cxt.isPrimitiveKey && cxt.genericVersion()) {
            val keyType = cxt.keyOption() as PrimitiveType
            var keyUsages = 0
            for (line in lines) {
                keyUsages += MethodGenerator.countOccurrences(line, KEY_SUB)
            }
            if (permissions.contains(Permission.INSERT) || permissions.contains(SET_VALUE)) {
                // insert or setValue => key couldn't be Object, key is Character/Integer/...
                // if more than 1 usage, unbox
                if (keyUsages > 1) {
                    replaceAll(0, KEY_SUB, "k")
                    val keyCopy = indent + cxt.keyType() + " k = key;"
                    lines.add(0, keyCopy)
                }
            } else if (!inline()) {
                // key is Object
                // the only usage - in index() call
                if (keyUsages != 1)
                    throw IllegalStateException()
                // for this usage, cast the key to Character/Integer/...
                val key = "(" + keyType.className + ") key"
                replaceAll(0, KEY_SUB, key)
            } else {
                // inlined remove() op
                // cast & unbox
                replaceAll(0, KEY_SUB, "k")
                val keyCopy = indent + cxt.keyType() + " k = (" + keyType.className + ") key;"
                lines.add(0, keyCopy)
            }
        }
        val key: String
        if (cxt.isNullKey) {
            key = "null"
        } else if (cxt.isObjectKey && inline() && method!!.baseOp() == GET) {
            key = "k"
        } else {
            key = "key"
        }
        replaceAll(0, KEY_SUB, key)
    }

    private fun generatePresent(replaceValues: Boolean = true) {
        lines("// key is present")
        firstPresentLine = lines.size
        val branchStart = firstPresentLine
        method!!.ifPresent()
        if (replaceValues)
            replaceValues(branchStart)
    }

    private fun generateAbsent(removedSlot: Boolean, replaceValues: Boolean = true,
                               slotTypeComment: Boolean = true) {
        val time = if (method!!.baseOp() == INSERT && !inline()) "was" else "is"
        var comment = "// key $time absent"
        if (slotTypeComment && cxt.mutable() && inline() && !isLHash(cxt)) {
            comment += if (removedSlot) ", removed slot" else ", free slot"
        }
        lines(comment)
        firstAbsentLine = lines.size
        val branchStart = firstAbsentLine
        this.removedSlot = removedSlot
        if (inline() && !method!!.inline() && method!!.baseOp() == INSERT) {
            assert(cxt.isMapView)
            insert("value")
        }
        method!!.ifAbsent()
        if (replaceValues)
            replaceValues(branchStart)
        this.removedSlot = false
    }

    private fun getIndex() {
        if (method!!.baseOp() == GET) {
            lines("int index = index(" + unwrappedKey() + ");")
        } else if (method!!.baseOp() == INSERT) {
            val insertArgs = if (cxt.isMapView)
                unwrappedKey() + ", " + unwrapValue("value")
            else
                unwrappedKey()
            lines("int index = insert($insertArgs);")
        } else if (method!!.baseOp() == CUSTOM_INSERT) {
            lines("InsertionIndex insertionIndex = insertionIndex(" + unwrappedKey() + ");")
        } else {
            throw IllegalStateException()
        }
    }

    private fun indexPresent(): String {
        if (method!!.baseOp() == GET || method!!.baseOp() == INSERT) {
            return "index >= 0"
        } else if (method!!.baseOp() == CUSTOM_INSERT) {
            return "insertionIndex.existing()"
        } else {
            throw IllegalStateException()
        }
    }

    private fun indexAbsent(): String {
        if (method!!.baseOp() == GET || method!!.baseOp() == INSERT) {
            return "index < 0"
        } else if (method!!.baseOp() == CUSTOM_INSERT) {
            return "insertionIndex.absent()"
        } else {
            throw IllegalStateException()
        }
    }

    private fun replaceValues(branchStart: Int) {
        val valUsages = countUsages(branchStart, VAL_SUB)
        if (valUsages > 0) {
            val `val` = readValue(cxt, table(), values(), indexF())
            if (valUsages >= 2) {
                replaceFirstDifferent(branchStart, VAL_SUB, "(val = $`val`)", "val")
                lines.add(branchStart, indent + cxt.valueUnwrappedType() + " val;")
            } else {
                replaceAll(branchStart, VAL_SUB, `val`)
            }
        }
        if (commonValuesCopy) {
            replaceAll(branchStart, VALUES_SUB, "vals")
        } else {
            val valArrayUsages = countUsages(branchStart, VALUES_SUB)
            if (valArrayUsages >= 2) {
                replaceAll(branchStart, VALUES_SUB, "vals")
                lines.add(branchStart, indent + cxt.valueUnwrappedType() + "[] vals = values;")
            } else {
                replaceAll(branchStart, VALUES_SUB, "values")
            }
        }
        if (inline() || commonTableCopy) {
            replaceAll(branchStart, TABLE_SUB, "tab")
        } else {
            val tableUsages = countUsages(branchStart, TABLE_SUB)
            if (tableUsages >= 2) {
                replaceAll(branchStart, TABLE_SUB, "tab")
                lines.add(branchStart, indent + tableType(cxt) + "[] tab = table;")
            } else {
                replaceAll(branchStart, TABLE_SUB, "table")
            }
        }
        if (method!!.baseOp() == CUSTOM_INSERT && !inline()) {
            for (i in branchStart..lines.size - 1) {
                val line = lines[i]
                if (MethodGenerator.countOccurrences(line, "index") > 0) {
                    lines.add(branchStart, indent + "int index = insertionIndex.get();")
                    break
                }
            }
        }
    }

    internal fun removedValue(): String {
        return if (inline() || !cxt.isIntegralKey) removed(cxt) else "removedValue"
    }

    fun indexF(): String {
        return if (removedSlot) "firstRemoved" else index
    }

    override fun key(): String {
        return wrapKey(unwrappedKey())
    }

    private fun unwrappedKey(): String {
        return KEY_SUB
    }

    override fun value(): String {
        return wrapValue(unwrappedValue())
    }

    private fun unwrappedValue(): String {
        return VAL_SUB
    }

    private fun table(): String {
        return TABLE_SUB
    }

    private fun values(): String {
        return VALUES_SUB
    }

    override fun remove(): MethodGenerator {
        permissions.add(REMOVE)
        if (isLHash(cxt) && !method!!.removeIsHighlyProbable()) {
            lines("removeAt(" + indexF() + ");")
        } else {
            if (isLHash(cxt)) {
                if (!inline() || !commonCapacityMaskCopy) {
                    lines("int capacityMask = " + capacityMask(cxt) + ";")
                }
                if (inline()) {
                    commonCapacityMaskCopy = true
                }
                LHashShiftRemove(this, cxt, indexF(), TABLE_SUB, VALUES_SUB).generate()
            } else {
                incrementModCount()
                var keys: String
                val table: String
                if (inline()) {
                    keys = "keys"
                    if (cxt.isObjectOrNullKey)
                        keys = "((Object[]) $keys)"
                    table = "tab"
                } else {
                    keys = "set"
                    table = "table"
                }
                writeKey(this, cxt, table, keys, indexF(), removedValue())
                if (cxt.isObjectValue)
                    writeValue(this, cxt, table(), values(), indexF(), "null")
                lines("postRemoveHook();")
            }
        }
        return this
    }

    override fun setValue(newValue: String): MethodGenerator {
        writeValue(this, cxt, table(), values(), indexF(), unwrapValue(newValue))
        permissions.add(SET_VALUE)
        return this
    }


    private fun generateInline() {
        inlineBeginning()
        inlineLocals()
        val curAssignment = curAssignment(cxt, unwrappedKey(), commonCapacityMaskCopy)
        if (method!!.mostProbableBranch() == KEY_ABSENT) {
            firstIndexFreeCheck(curAssignment)
            if (separatePresent) {
                lines("keyPresent:")
                var keyNotEqualsCond = "cur != " + unwrappedKey()
                if (cxt.isObjectKey && !possibleRemovedSlots(cxt)) {
                    keyNotEqualsCond += " && !keyEquals(" + unwrappedKey() + ", cur)"
                }
                ifBlock(keyNotEqualsCond)
            } else {
                var keyEqualsCond = "cur == " + unwrappedKey()
                if (cxt.isObjectKey && !possibleRemovedSlots(cxt)) {
                    keyEqualsCond += " || keyEquals(" + unwrappedKey() + ", cur)"
                }
                ifBlock(keyEqualsCond)
                generatePresent()
                elseBlock()
            }
            innerInline()
            blockEnd()
            if (separatePresent)
                generatePresent()
            blockEnd()
            if (separateAbsentFreeSlot && !earlyAbsentLabel)
                generateAbsent(false)
        } else {
            // most probable branch - key is present
            if (separatePresent) {
                lines("keyPresent:")
                ifBlock(curAssignment + " != " + unwrappedKey())
            } else {
                ifBlock(curAssignment + " == " + unwrappedKey())
                generatePresent()
                elseBlock()
            }
            firstIndexFreeCheck("cur")
            if (cxt.isObjectKey && !possibleRemovedSlots(cxt)) {
                ifBlock("keyEquals(" + unwrappedKey() + ", cur)")
                generateOrGoToPresent { }
                elseBlock()
            }
            innerInline()
            if (cxt.isObjectKey && !possibleRemovedSlots(cxt))
                blockEnd()
            blockEnd()
            if (separateAbsentFreeSlot && !earlyAbsentLabel)
                generateAbsent(false)
            blockEnd()
            if (separatePresent)
                generatePresent()
        }
        inlineEnd()
    }

    private fun innerInline() {
        if (possibleRemovedSlots(cxt) && (cxt.isObjectKey || method!!.baseOp() != GET)) {
            if (method!!.baseOp() != GET)
                lines("int firstRemoved;")
            val stepPrecomputed = !cxt.isObjectKey && tryPrecomputeStep(this, cxt)
            ifBlock(isNotRemoved(cxt, "cur"))
            if (cxt.isObjectKey) {
                if (method!!.mostProbableBranch() == KEY_PRESENT) {
                    ifBlock("keyEquals(" + unwrappedKey() + ", cur)")
                    generateOrGoToPresent { }
                    elseBlock()
                } else {
                    ifBlock("!keyEquals(" + unwrappedKey() + ", cur)")
                }
            }
            ifBlock("noRemoved()")
            keySearchLoop(true, stepPrecomputed)
            if (method!!.baseOp() != GET) {
                elseBlock()
                lines("firstRemoved = -1;")
            }
            blockEnd()
            if (cxt.isObjectKey) {
                if (method!!.mostProbableBranch() != KEY_PRESENT) {
                    elseBlock()
                    generateOrGoToPresent { }
                }
                blockEnd()
            }
            if (method!!.baseOp() != GET) {
                elseBlock()
                lines("firstRemoved = index;")
            }
            blockEnd()
            if (method!!.baseOp() == GET) {
                keySearchLoop(false, stepPrecomputed)
            } else {
                keySearchLoopDifferentRemovedHandling(stepPrecomputed)
            }
        } else {
            if (isLHash(cxt) && cxt.isNullKey && !commonCapacityMaskCopy)
                lines("capacityMask = " + capacityMask(cxt) + ";")
            keySearchLoop(true, false)
        }
    }

    private fun inlineLocals() {
        copyArrays(this, cxt, commonValuesCopy)
        val locals: String
        if (isLHash(cxt)) {
            if (commonCapacityMaskCopy) {
                lines("int capacityMask = " + capacityMask(cxt) + ";")
                locals = ""
            } else {
                locals = "capacityMask, "
            }
        } else if (isQHash(cxt)) {
            if (cxt.isNullKey) {
                lines("int capacity = " + localTableVar(cxt) + ".length;")
                locals = ""
            } else {
                locals = "capacity, "
            }
        } else {
            assertHash(cxt, isDHash(cxt))
            locals = if (!cxt.isNullKey) "capacity, hash, " else ""
        }
        lines("int " + locals + "index;")
        lines(cxt.keyUnwrappedType() + " cur;")
        if (parallelKV(cxt) && !doubleSizedParallel(cxt))
            lines(tableEntryType(cxt) + " entry;")
    }

    private fun copyValues() {
        lines(cxt.valueUnwrappedType() + "[] vals = values;")
    }

    private fun inlineBeginning() {
        if (cxt.isObjectKey) {
            ifBlock("key != null")
            if (method!!.baseOp() == GET) {
                lines(
                        "// noinspection unchecked",
                        cxt.keyType() + " " + unwrappedKey() + " = (" + cxt.keyType() + ") key;")
            }
        }
        method!!.beginning()
        earlyAbsentLabel = false
        if (cxt.isIntegralKey) {
            if (method!!.baseOp() == GET) {
                val isRemoveOp = permissions.contains(Permission.REMOVE)
                lines(cxt.keyType() + " free" +
                        (if (isRemoveOp && possibleRemovedSlots(cxt)) ", removed" else "") + ";")
                if (separateAbsentFreeSlot) {
                    lines("keyAbsent:")
                    earlyAbsentLabel = true
                }

                val removed = if (isRemoveOp) "(removed = removedValue)" else "removedValue"
                ifBlock(unwrappedKey() + " != (free = freeValue)" +
                        if (possibleRemovedSlots(cxt))
                            " && " + unwrappedKey() + " != " + removed
                        else
                            "")
            } else {
                lines(cxt.keyType() + " free;")
                if (possibleRemovedSlots(cxt))
                    lines(cxt.keyType() + " removed = removedValue;")
                ifBlock(unwrappedKey() + " == (free = freeValue)")
                lines("free = changeFree();")
                if (possibleRemovedSlots(cxt)) {
                    elseIf(unwrappedKey() + " == removed")
                    lines("removed = changeRemoved();")
                }
                blockEnd()
            }
        }
    }

    private fun inlineEnd() {
        if (method!!.baseOp() == GET) {
            if (earlyAbsentLabel) {
                blockEnd()
                generateAbsent(false)
            } else if (cxt.isIntegralKey) {
                elseBlock()
                generateAbsent(false, true, false)
                blockEnd()
            }
        }
        if (cxt.isObjectKey) {
            elseBlock()
            lines("return " + method!!.name() + "NullKey(" + method!!.nullArgs() + ");")
            blockEnd()
        }
    }

    private fun firstIndexFreeCheck(cur: String) {
        if (separateAbsentFreeSlot) {
            if (!earlyAbsentLabel) {
                lines(absentLabel(false) + ":")
            }
            ifBlock(isNotFree(cxt, cur))
        } else {
            ifBlock(isFree(cxt, cur))
            generateAbsent(false)
            elseBlock()
        }
    }

    private fun keySearchLoop(noRemoved: Boolean, stepPrecomputed: Boolean) {
        KeySearch.innerLoop(this, cxt, { firstIndex, index ->
            val prevIndex = this.index
            this.index = index
            val beforeBreak = {
                if (index != prevIndex)
                    lines("$prevIndex = $index;")
            }
            val key = readKeyOrEntry(cxt, firstIndex)
            if (method!!.mostProbableBranch() == KEY_PRESENT) {
                ifBlock("(cur = " + key + ") == " + unwrappedKey())
                generateOrGoToPresent(beforeBreak)
                elseIf(isFree(cxt, "cur"))
                generateOrGoToAbsent(false, beforeBreak)
                blockEnd()
                if (cxt.isObjectKey) {
                    lines("else if (" + objectKeyEqualsCond(noRemoved) + ")").block()
                    generateOrGoToPresent(beforeBreak)
                    blockEnd()
                }
            } else {
                ifBlock(isFree(cxt, "(cur = $key)"))
                generateOrGoToAbsent(false, beforeBreak)
                var presentCond = "cur == " + unwrappedKey()
                if (cxt.isObjectKey) {
                    presentCond += " || (" + objectKeyEqualsCond(noRemoved) + ")"
                }
                elseIf(presentCond)
                generateOrGoToPresent(beforeBreak)
                blockEnd()
            }
            this.index = prevIndex
        }, stepPrecomputed).generate()
    }


    private fun keySearchLoopDifferentRemovedHandling(stepPrecomputed: Boolean) {
        if (separateAbsentRemovedSlot)
            lines(absentLabel(true) + ":").block()
        KeySearch.innerLoop(this, cxt, { firstIndex, index ->
            val prevIndex = this.index
            this.index = index
            val beforeBreak = {
                if (index != prevIndex)
                    lines("$prevIndex = $index;")
            }
            val key = readKeyOrEntry(cxt, firstIndex)
            if (method!!.mostProbableBranch() == KEY_PRESENT) {
                ifBlock("(cur = " + key + ") == " + unwrappedKey())
                generateOrGoToPresent(beforeBreak)
                elseIf(isFree(cxt, "cur"))
                generateAbsentDependingOnFirstRemoved(beforeBreak)
            } else {
                ifBlock(isFree(cxt, "(cur = $key)"))
                generateAbsentDependingOnFirstRemoved(beforeBreak)
                elseIf("cur == " + unwrappedKey())
                generateOrGoToPresent(beforeBreak)
            }
            if (cxt.isObjectKey) {
                elseIf(isNotRemoved(cxt, "cur"))
                ifBlock("keyEquals(" + unwrappedKey() + ", cur)")
                generateOrGoToPresent(beforeBreak)
                blockEnd()
                elseIf("firstRemoved < 0")
            } else {
                elseIf(isRemoved(cxt, "cur") + " && firstRemoved < 0")
            }
            lines("firstRemoved = $index;")
            blockEnd()
            this.index = prevIndex
        }, stepPrecomputed).generate()
        if (separateAbsentRemovedSlot) {
            blockEnd()
            generateAbsent(true)
        }
    }

    private fun generateAbsentDependingOnFirstRemoved(beforeBreak: () -> Unit) {
        ifBlock("firstRemoved < 0")
        run { generateOrGoToAbsent(false, beforeBreak) }
        elseBlock()
        run { generateOrGoToAbsent(true, beforeBreak) }
        blockEnd()
    }

    private fun objectKeyEqualsCond(noRemoved: Boolean): String {
        return (if (possibleRemovedSlots(cxt) && !noRemoved) "cur != REMOVED && " else "") +
                "keyEquals(" + unwrappedKey() + ", cur)"
    }

    private fun determineBranchFeatures() {
        generateAbsent(false, false, false)
        // first line is comment
        absentBranchSize = lines.size - 1
        separateAbsentFreeSlot = absentBranchSize > 1
        val absentBranchValuesUsages = countUsages(0, VALUES_SUB) + countUsages(0, VAL_SUB)
        val absentBranchTableUsages = countUsages(0, TABLE_SUB)
        lines.clear()

        separateAbsentRemovedSlot = separateAbsentFreeSlot && innerLoopBodies(cxt) > 1

        generatePresent(false)
        // first line is comment
        presentBranchSize = lines.size - 1
        separatePresent = presentBranchSize > 1
        val presentValUsages = countUsages(0, VAL_SUB)
        val presentBranchValuesUsages = countUsages(0, VALUES_SUB) + presentValUsages
        val presentBranchTableUsages = countUsages(0, TABLE_SUB)
        lines.clear()

        presentValueUsed = presentValUsages > 0
        commonValuesCopy = absentBranchValuesUsages > 0 && presentBranchValuesUsages > 0
        commonTableCopy = absentBranchTableUsages > 0 && presentBranchTableUsages > 0
    }

    private fun generateOrGoToPresent(beforeBreak: () -> Unit) {
        if (separatePresent) {
            beforeBreak.invoke()
            lines("break keyPresent;")
        } else {
            generatePresent()
        }
    }

    private fun generateOrGoToAbsent(removedSlot: Boolean, beforeBreak: () -> Unit) {
        if (separateAbsentRemovedSlot && removedSlot) {
            lines("break " + absentLabel(true) + ";")
        } else if (separateAbsentFreeSlot && !removedSlot) {
            beforeBreak.invoke()
            lines("break " + absentLabel(false) + ";")
        } else {
            generateAbsent(removedSlot)
        }
    }

    private fun absentLabel(removedSlot: Boolean): String {
        if (method!!.baseOp() != GET && possibleRemovedSlots(cxt)) {
            return "keyAbsent" + if (removedSlot) "RemovedSlot" else "FreeSlot"
        } else {
            return "keyAbsent"
        }
    }

    companion object {

        private val TABLE_SUB = "#tab#"
        private val KEY_SUB = "#key#"
        private val VAL_SUB = "#val#"
        private val VALUES_SUB = "#vals#"
    }
}
