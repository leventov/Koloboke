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

import com.koloboke.jpsg.ObjectType
import com.koloboke.jpsg.PrimitiveType
import com.koloboke.jpsg.PrimitiveType.*
import com.koloboke.jpsg.SimpleOption
import com.koloboke.jpsg.collect.MethodContext
import com.koloboke.jpsg.collect.MethodGenerator


internal object HashMethodGeneratorCommons {

    private val DHASH = SimpleOption("DHash")
    private val QHASH = SimpleOption("QHash")
    private val LHASH = SimpleOption("LHash")
    private val SEPARATE_KV = SimpleOption("Separate")
    private val PARALLEL_KV = SimpleOption("Parallel")

    fun isDHash(cxt: MethodContext): Boolean {
        return DHASH == cxt.getOption("hash")
    }

    fun isQHash(cxt: MethodContext): Boolean {
        return QHASH == cxt.getOption("hash")
    }

    fun isLHash(cxt: MethodContext): Boolean {
        return LHASH == cxt.getOption("hash")
    }

    fun separateKV(cxt: MethodContext): Boolean {
        return SEPARATE_KV == cxt.getOption("kv")
    }

    fun parallelKV(cxt: MethodContext): Boolean {
        return PARALLEL_KV == cxt.getOption("kv")
    }

    fun tableType(cxt: MethodContext): String {
        if (cxt.isObjectOrNullKey) {
            return "Object"
        } else {
            return TableType.INSTANCE.apply(cxt.keyOption() as PrimitiveType).standalone
        }
    }

    fun tableEntryType(cxt: MethodContext): String {
        if (cxt.isObjectOrNullKey) {
            return "Object"
        } else {
            val tableType = TableType.INSTANCE.apply(cxt.keyOption() as PrimitiveType)
            // jvm stack slot type
            if (tableType === PrimitiveType.LONG) {
                return LONG.standalone
            } else {
                return INT.standalone
            }
        }
    }

    fun doubleSizedParallel(cxt: MethodContext): Boolean {
        val key = cxt.keyOption()
        return parallelKV(cxt) && (key === LONG || key === DOUBLE || key is ObjectType ||
                cxt.isNullKey)
    }

    fun declareEntry(g: MethodGenerator, cxt: MethodContext) {
        if (parallelKV(cxt) && !doubleSizedParallel(cxt))
            g.lines(tableEntryType(cxt) + " entry;")
    }

    fun slots(slots: Int, cxt: MethodContext): Int {
        return slots * if (doubleSizedParallel(cxt)) 2 else 1
    }

    fun forLoop(g: MethodGenerator, cxt: MethodContext, limit: String, index: String,
                includeLimit: Boolean) {
        var declaration = "int $index = $limit"
        if (!includeLimit)
            declaration = declaration + " - " + slots(1, cxt)
        val decrement = index + if (doubleSizedParallel(cxt)) " -= 2" else "--"
        g.lines("for ($declaration; $index >= 0; $decrement)").block()
    }

    fun localTableVar(cxt: MethodContext): String {
        return if (parallelKV(cxt)) "tab" else "keys"
    }

    @JvmOverloads fun capacityMask(cxt: MethodContext, table: String = localTableVar(cxt)): String {
        return table + ".length - " + slots(1, cxt)
    }

    fun keyHash(cxt: MethodContext, key: String, distinctNullKey: Boolean): String {
        val hash: String
        var mixingClass = (if (parallelKV(cxt)) "Parallel" else "Separate") + "KV"
        if (distinctNullKey && cxt.isNullKey) {
            return "0"
        } else if (cxt.isObjectOrNullKey) {
            val nullImpossible = distinctNullKey || !cxt.nullKeyAllowed()
            hash = (if (nullImpossible) "keyHashCode" else "nullableKeyHashCode") + "(" + key + ")"
            mixingClass += "Obj"
        } else {
            hash = key
            val keyOption = cxt.keyOption() as PrimitiveType
            mixingClass += keyOption.title
        }
        mixingClass += "KeyMixing"
        return "$mixingClass.mix($hash)"
    }

    fun isFree(cxt: MethodContext, key: String): String {
        return key + " == " + free(cxt)
    }

    fun isNotFree(cxt: MethodContext, key: String): String {
        return key + " != " + free(cxt)
    }

    fun isRemoved(cxt: MethodContext, key: String): String {
        if (cxt.isFloatingKey) {
            return key + " > FREE_BITS"
        } else {
            return key + " == " + removed(cxt)
        }
    }

    fun isNotRemoved(cxt: MethodContext, key: String): String {
        if (cxt.isFloatingKey) {
            return key + " <= FREE_BITS"
        } else {
            return key + " != " + removed(cxt)
        }
    }

    fun free(cxt: MethodContext): String {
        if (!cxt.isPrimitiveKey) {
            return if (cxt.nullKeyAllowed()) "FREE" else "null";
        } else if (cxt.isIntegralKey) {
            return "free"
        } else {
            return "FREE_BITS"
        }
    }

    fun removed(cxt: MethodContext): String {
        if (isLHash(cxt))
            return free(cxt)
        if (!cxt.isPrimitiveKey) {
            return "REMOVED"
        } else if (cxt.isIntegralKey) {
            return "removed"
        } else {
            return "REMOVED_BITS"
        }
    }

    fun assertHash(cxt: MethodContext, isHash: Boolean) {
        assert(isHash) {
            "Unknown hash dimension value: " +
                    cxt.getOption("hash") + ", either LHash, QHash or DHash is expected"
        }
    }

    fun possibleRemovedSlots(cxt: MethodContext): Boolean {
        return cxt.mutable() && !isLHash(cxt)
    }

    @JvmOverloads fun eraseSlot(g: MethodGenerator, cxt: MethodContext,
                                indexForKeys: String, indexForValues: String, genericKeys: Boolean = true, values: String = "vals") {
        val keys = if (cxt.isObjectOrNullKey && genericKeys) "((Object[]) keys)" else "keys"
        writeKey(g, cxt, "tab", keys, indexForKeys, removed(cxt))
        if (cxt.isObjectValue)
            writeValue(g, cxt, "tab", values, indexForValues, "null")
    }

    fun noRemoved(cxt: MethodContext): Boolean {
        val removed = cxt.getOption("removed")
        return removed != null && removed.toString().equals("no", ignoreCase = true)
    }

    fun copyUnwrappedKeys(g: MethodGenerator, cxt: MethodContext) {
        if (cxt.isObjectOrNullKey)
            g.lines("// noinspection unchecked")
        g.lines(cxt.keyUnwrappedType() + "[] keys = " +
                (if (cxt.isObjectOrNullKey) "(" + cxt.keyUnwrappedType() + "[]) " else "") +
                "set;")
    }

    fun copyTable(g: MethodGenerator, cxt: MethodContext) {
        g.lines(tableType(cxt) + "[] tab = table;")
    }

    fun copyArrays(g: MethodGenerator, cxt: MethodContext, copyValues: Boolean) {
        if (!parallelKV(cxt)) {
            copyUnwrappedKeys(g, cxt)
            if (copyValues)
                g.lines(cxt.valueUnwrappedType() + "[] vals = values;")
        } else {
            copyTable(g, cxt)
        }
    }

    fun readKeyOrEntry(cxt: MethodContext, index: String): String {
        return readKeyOrEntry(cxt, "tab", "keys", index)
    }

    fun readKeyOrEntry(cxt: MethodContext, table: String, keys: String, index: String): String {
        if (!parallelKV(cxt)) {
            return "$keys[$index]"
        } else if (doubleSizedParallel(cxt)) {
            var key = "$table[$index]"
            if (cxt.isObjectOrNullKey)
                key = "(" + cxt.keyUnwrappedType() + ") " + key
            return key
        } else {
            return "(" + cxt.keyUnwrappedType() + ") (entry = " + table + "[" + index + "])"
        }
    }

    fun readKeyOnly(cxt: MethodContext, index: String): String {
        return readKeyOnly(cxt, "tab", "keys", index)
    }

    fun readKeyOnly(cxt: MethodContext, table: String, keys: String, index: String): String {
        if (!parallelKV(cxt)) {
            return "$keys[$index]"
        } else if (doubleSizedParallel(cxt)) {
            var key = "$table[$index]"
            if (cxt.isObjectOrNullKey)
                key = "(" + cxt.keyUnwrappedType() + ") " + key
            return key
        } else {
            val keyType = cxt.keyOption() as PrimitiveType
            val entryTableUpper = TableType.INSTANCE.apply(keyType).upper
            val base = entryTableUpper + "_BASE"
            val offset = base + " + " + keyType.upper + "_KEY_OFFSET + " +
                    "(((long) (" + index + ")) << " + entryTableUpper + "_SCALE_SHIFT)"
            return cxt.unsafeGetKeyBits(table, offset)
        }
    }

    fun writeKey(g: MethodGenerator, cxt: MethodContext, index: String, key: String) {
        writeKey(g, cxt, "tab", "keys", index, key)
    }

    fun writeKey(g: MethodGenerator, cxt: MethodContext,
                 table: String, keys: String, index: String, key: String) {
        if (!parallelKV(cxt)) {
            g.lines("$keys[$index] = $key;")
        } else if (doubleSizedParallel(cxt)) {
            g.lines("$table[$index] = $key;")
        } else {
            val keyType = cxt.keyOption() as PrimitiveType
            val entryTableUpper = TableType.INSTANCE.apply(keyType).upper
            val base = entryTableUpper + "_BASE"
            val offset = base + " + " + keyType.upper + "_KEY_OFFSET + " +
                    "(((long) (" + index + ")) << " + entryTableUpper + "_SCALE_SHIFT)"
            cxt.unsafePutKeyBits(g, table, offset, key)
        }
    }

    fun readValue(cxt: MethodContext, index: String): String {
        return readValue(cxt, "tab", "vals", index)
    }

    fun readValue(cxt: MethodContext, table: String, values: String, index: String): String {
        if (!parallelKV(cxt)) {
            return "$values[$index]"
        } else if (doubleSizedParallel(cxt)) {
            var value = "$table[$index + 1]"
            if (cxt.isObjectValue)
                value = "(" + cxt.valueGenericType() + ") " + value
            return value
        } else {
            val valueType = cxt.mapValueOption() as PrimitiveType
            return "(" + valueType.bitsType().standalone + ") " +
                    "(entry >>> " + bitsWidth(valueType) + ")"
        }
    }

    fun writeValue(g: MethodGenerator, cxt: MethodContext, index: String, value: String) {
        writeValue(g, cxt, "tab", "vals", index, value)
    }

    fun writeValue(g: MethodGenerator, cxt: MethodContext, table: String,
                   values: String, index: String, value: String) {
        if (!parallelKV(cxt)) {
            g.lines("$values[$index] = $value;")
        } else if (doubleSizedParallel(cxt)) {
            g.lines("$table[$index + 1] = $value;")
        } else {
            val valueType = cxt.mapValueOption() as PrimitiveType
            val entryTableUpper = TableType.INSTANCE.apply(valueType).upper
            val base = entryTableUpper + "_BASE"
            val offset = base + " + " + valueType.upper + "_VALUE_OFFSET + " +
                    "(((long) (" + index + ")) << " + entryTableUpper + "_SCALE_SHIFT)"
            cxt.unsafePutValueBits(g, table, offset, value)
        }
    }

    fun writeKeyAndValue(g: MethodGenerator, cxt: MethodContext, table: String,
                         keys: String, values: String, index: String,
                         key: String, value: () -> String,
                         composeEntry: Boolean, writeValue: Boolean) {
        var key = key
        if (!writeValue || !parallelKV(cxt) || doubleSizedParallel(cxt)) {
            writeKey(g, cxt, table, keys, index, key)
            if (writeValue)
                writeValue(g, cxt, table, values, index, value.invoke())
        } else {
            val keyType = cxt.keyOption() as PrimitiveType
            key = "((" + tableEntryType(cxt) + ") " + key + ")"
            if (keyType !== CHAR)
                key = "(" + key + " & " + keyType.upper + "_MASK)"
            var v = value.invoke()
            v = "((" + tableEntryType(cxt) + ") " + v + ")"
            var entry = if (composeEntry)
                "(" + key + " | (" + v + " << " + bitsWidth(keyType) + "))"
            else
                "entry"
            val tableArrayType = TableType.INSTANCE.apply(keyType).standalone
            val needToCastEntry = tableEntryType(cxt) != tableArrayType
            if (needToCastEntry)
                entry = "($tableArrayType) $entry"
            g.lines("$table[$index] = $entry;")
        }
    }

    private fun bitsWidth(type: PrimitiveType): Int {
        when (type) {
            BYTE -> return 8
            CHAR, SHORT -> return 16
            INT, FLOAT -> return 32
            LONG, DOUBLE -> return 64
        }
        throw AssertionError()
    }
}
