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

import net.openhft.jpsg.*;
import net.openhft.jpsg.collect.MethodContext;
import net.openhft.jpsg.collect.MethodGenerator;

import java.util.function.Supplier;

import static java.lang.String.format;
import static net.openhft.jpsg.PrimitiveType.*;


final class HashMethodGeneratorCommons {

    private static final SimpleOption DHASH = new SimpleOption("DHash");
    private static final SimpleOption QHASH = new SimpleOption("QHash");
    private static final SimpleOption LHASH = new SimpleOption("LHash");
    private static final SimpleOption SEPARATE_KV = new SimpleOption("Separate");
    private static final SimpleOption PARALLEL_KV = new SimpleOption("Parallel");

    static boolean isDHash(MethodContext cxt) {
        return DHASH.equals(cxt.getOption("hash"));
    }

    static boolean isQHash(MethodContext cxt) {
        return QHASH.equals(cxt.getOption("hash"));
    }

    static boolean isLHash(MethodContext cxt) {
        return LHASH.equals(cxt.getOption("hash"));
    }

    static boolean separateKV(MethodContext cxt) {
        return SEPARATE_KV.equals(cxt.getOption("kv"));
    }

    static boolean parallelKV(MethodContext cxt) {
        return PARALLEL_KV.equals(cxt.getOption("kv"));
    }

    static String tableType(MethodContext cxt) {
        if (cxt.isObjectOrNullKey()) {
            return "Object";
        } else {
            return TableType.INSTANCE.apply((PrimitiveType) cxt.keyOption()).standalone;
        }
    }

    static String tableEntryType(MethodContext cxt) {
        if (cxt.isObjectOrNullKey()) {
            return "Object";
        } else {
            PrimitiveType tableType = TableType.INSTANCE.apply((PrimitiveType) cxt.keyOption());
            // jvm stack slot type
            if (tableType == PrimitiveType.LONG) {
                return LONG.standalone;
            } else {
                return INT.standalone;
            }
        }
    }

    static boolean doubleSizedParallel(MethodContext cxt) {
        Option key = cxt.keyOption();
        return parallelKV(cxt) && (key == LONG || key == DOUBLE || key instanceof ObjectType ||
                cxt.isNullKey());
    }

    static void declareEntry(MethodGenerator g, MethodContext cxt) {
        if (parallelKV(cxt) && !doubleSizedParallel(cxt))
            g.lines(tableEntryType(cxt) + " entry;");
    }

    static int slots(int slots, MethodContext cxt) {
        return slots * (doubleSizedParallel(cxt) ? 2 : 1);
    }

    static void forLoop(MethodGenerator g, MethodContext cxt, String limit, String index,
            boolean includeLimit) {
        String declaration = "int " + index + " = " + limit;
        if (!includeLimit)
            declaration = declaration + " - " + slots(1, cxt);
        String decrement = index + (doubleSizedParallel(cxt) ?  " -= 2" : "--");
        g.lines("for (" + declaration + "; " + index + " >= 0; " + decrement + ")").block();
    }

    static String capacityMask(MethodContext cxt) {
        return capacityMask(cxt, localTableVar(cxt));
    }

    static String localTableVar(MethodContext cxt) {
        return parallelKV(cxt) ? "tab" : "keys";
    }

    static String capacityMask(MethodContext cxt, String table) {
        return table + ".length - " + slots(1, cxt);
    }

    static String keyHash(MethodContext cxt, String key, boolean distinctNullKey) {
        String hash;
        String mixingClass = (parallelKV(cxt) ? "Parallel" : "Separate") + "KV";
        if (distinctNullKey && cxt.isNullKey()) {
            return "0";
        } else if (cxt.isObjectOrNullKey()) {
            hash = (distinctNullKey ? "keyHashCode" : "nullableKeyHashCode") + "(" + key + ")";
            mixingClass += "Obj";
        } else {
            hash = key;
            PrimitiveType keyOption = (PrimitiveType) cxt.keyOption();
            mixingClass += keyOption.title;
        }
        mixingClass += "KeyMixing";
        return mixingClass + ".mix(" + hash + ")";
    }

    static String isFree(MethodContext cxt, String key) {
        return key + " == " + free(cxt);
    }

    static String isNotFree(MethodContext cxt, String key) {
        return key + " != " + free(cxt);
    }

    static String isRemoved(MethodContext cxt, String key) {
        if (cxt.isFloatingKey()) {
            return key + " > FREE_BITS";
        } else {
            return key + " == " + removed(cxt);
        }
    }

    static String isNotRemoved(MethodContext cxt, String key) {
        if (cxt.isFloatingKey()) {
            return key + " <= FREE_BITS";
        } else {
            return key + " != " + removed(cxt);
        }
    }

    static String free(MethodContext cxt) {
        if (!cxt.isPrimitiveKey()) {
            return "FREE";
        } else if (cxt.isIntegralKey()) {
            return "free";
        } else {
            return "FREE_BITS";
        }
    }

    static String removed(MethodContext cxt) {
        if (isLHash(cxt))
            return free(cxt);
        if (!cxt.isPrimitiveKey()) {
            return "REMOVED";
        } else if (cxt.isIntegralKey()) {
            return "removed";
        } else {
            return "REMOVED_BITS";
        }
    }

    static void assertHash(MethodContext cxt, boolean isHash) {
        assert isHash : "Unknown hash dimension value: " +
                cxt.getOption("hash") + ", either LHash, QHash or DHash is expected";
    }

    static boolean possibleRemovedSlots(MethodContext cxt) {
        return cxt.mutable() && !isLHash(cxt);
    }

    static void eraseSlot(MethodGenerator g, MethodContext cxt,
            String indexForKeys, String indexForValues) {
        eraseSlot(g, cxt, indexForKeys, indexForValues, true, "vals");
    }

    static void eraseSlot(MethodGenerator g, MethodContext cxt,
            String indexForKeys, String indexForValues, boolean genericKeys, String values) {
        String keys = cxt.isObjectOrNullKey() && genericKeys ? "((Object[]) keys)" : "keys";
        writeKey(g, cxt, "tab", keys, indexForKeys, removed(cxt));
        if (cxt.isObjectValue())
            writeValue(g, cxt, "tab", values, indexForValues, "null");
    }

    static boolean noRemoved(MethodContext cxt) {
        Option removed = cxt.getOption("removed");
        return removed != null && removed.toString().equalsIgnoreCase("no");
    }

    static void copyUnwrappedKeys(MethodGenerator g, MethodContext cxt) {
        if (cxt.isObjectOrNullKey())
            g.lines("// noinspection unchecked");
        g.lines(cxt.keyUnwrappedType() + "[] keys = " +
                (cxt.isObjectOrNullKey() ? "(" + cxt.keyUnwrappedType() + "[]) " : "") +
                "set;");
    }

    static void copyTable(MethodGenerator g, MethodContext cxt) {
        g.lines(tableType(cxt) + "[] tab = table;");
    }

    static void copyArrays(MethodGenerator g, MethodContext cxt, boolean copyValues) {
        if (!parallelKV(cxt)) {
            copyUnwrappedKeys(g, cxt);
            if (copyValues)
                g.lines(cxt.valueUnwrappedType() + "[] vals = values;");
        } else {
            copyTable(g, cxt);
        }
    }

    static String readKeyOrEntry(MethodContext cxt, String index) {
        return readKeyOrEntry(cxt, "tab", "keys", index);
    }

    static String readKeyOrEntry(MethodContext cxt, String table, String keys, String index) {
        if (!parallelKV(cxt)) {
            return keys + "[" + index + "]";
        } else if (doubleSizedParallel(cxt)) {
            String key = table + "[" + index + "]";
            if (cxt.isObjectOrNullKey())
                key = "(" + cxt.keyUnwrappedType() + ") " + key;
            return key;
        } else {
            return "(" + cxt.keyUnwrappedType() + ") (entry = " + table + "[" + index + "])";
        }
    }

    static String readKeyOnly(MethodContext cxt, String index) {
        return readKeyOnly(cxt, "tab", "keys", index);
    }

    static String readKeyOnly(MethodContext cxt, String table, String keys, String index) {
        if (!parallelKV(cxt)) {
            return keys + "[" + index + "]";
        } else if (doubleSizedParallel(cxt)) {
            String key = table + "[" + index + "]";
            if (cxt.isObjectOrNullKey())
                key = "(" + cxt.keyUnwrappedType() + ") " + key;
            return key;
        } else {
            PrimitiveType keyType = (PrimitiveType) cxt.keyOption();
            String entryTableUpper = TableType.INSTANCE.apply(keyType).upper;
            String base = entryTableUpper + "_BASE";
            String offset = base + " + " + keyType.upper + "_KEY_OFFSET + " +
                    "(((long) (" + index + ")) << " + entryTableUpper + "_SCALE_SHIFT)";
            return cxt.unsafeGetKeyBits(table, offset);
        }
    }

    static void writeKey(MethodGenerator g, MethodContext cxt, String index, String key) {
        writeKey(g, cxt, "tab", "keys", index, key);
    }

    static void writeKey(MethodGenerator g, MethodContext cxt,
            String table, String keys, String index, String key) {
        if (!parallelKV(cxt)) {
            g.lines(keys + "[" + index + "] = " + key + ";");
        } else if (doubleSizedParallel(cxt)) {
            g.lines(table + "[" + index + "] = " + key + ";");
        } else {
            PrimitiveType keyType = (PrimitiveType) cxt.keyOption();
            String entryTableUpper = TableType.INSTANCE.apply(keyType).upper;
            String base = entryTableUpper + "_BASE";
            String offset = base + " + " + keyType.upper + "_KEY_OFFSET + " +
                    "(((long) (" + index + ")) << " + entryTableUpper + "_SCALE_SHIFT)";
            cxt.unsafePutKeyBits(g, table, offset, key);
        }
    }

    static String readValue(MethodContext cxt, String index) {
        return readValue(cxt, "tab", "vals", index);
    }
    static String readValue(MethodContext cxt, String table, String values, String index) {
        if (!parallelKV(cxt)) {
            return values + "[" + index + "]";
        } else if (doubleSizedParallel(cxt)) {
            String value = table + "[" + index + " + 1]";
            if (cxt.isObjectValue())
                value = "(" + cxt.valueGenericType() + ") " + value;
            return value;
        } else {
            PrimitiveType valueType = (PrimitiveType) cxt.mapValueOption();
            return "(" + valueType.bitsType().standalone + ") " +
                    "(entry >>> " + bitsWidth(valueType) + ")";
        }
    }

    static void writeValue(MethodGenerator g, MethodContext cxt, String index, String value) {
        writeValue(g, cxt, "tab", "vals", index, value);
    }

    static void writeValue(MethodGenerator g, MethodContext cxt, String table,
            String values, String index, String value) {
        if (!parallelKV(cxt)) {
            g.lines(values + "[" + index + "] = " + value + ";");
        } else if (doubleSizedParallel(cxt)) {
            g.lines(table + "[" + index + " + 1] = " + value + ";");
        } else {
            PrimitiveType valueType = (PrimitiveType) cxt.mapValueOption();
            String entryTableUpper = TableType.INSTANCE.apply(valueType).upper;
            String base = entryTableUpper + "_BASE";
            String offset = base + " + " + valueType.upper + "_VALUE_OFFSET + " +
                    "(((long) (" + index + ")) << " + entryTableUpper + "_SCALE_SHIFT)";
            cxt.unsafePutValueBits(g, table, offset, value);
        }
    }

    static void writeKeyAndValue(MethodGenerator g, MethodContext cxt, String table,
            String keys, String values, String index, String key, Supplier<String> value,
            boolean composeEntry, boolean writeValue) {
        if (!writeValue || !parallelKV(cxt) || doubleSizedParallel(cxt)) {
            writeKey(g, cxt, table, keys, index, key);
            if (writeValue)
                writeValue(g, cxt, table, values, index, value.get());
        } else {
            PrimitiveType keyType = (PrimitiveType) cxt.keyOption();
            key = "((" + tableEntryType(cxt) + ") " + key + ")";
            if (keyType != CHAR)
                key = "(" + key + " & " + keyType.upper + "_MASK)";
            String v = value.get();
            v = "((" + tableEntryType(cxt) + ") " + v + ")";
            String entry = composeEntry ?
                    "(" + key + " | (" + v + " << " + bitsWidth(keyType) + "))" :
                    "entry";
            String tableArrayType = TableType.INSTANCE
                    .apply(keyType).standalone;
            boolean needToCastEntry = !tableEntryType(cxt).equals(tableArrayType);
            if (needToCastEntry)
                entry = "(" + tableArrayType + ") " + entry;
            g.lines(table + "[" + index + "] = " + entry + ";");
        }
    }

    private static int bitsWidth(PrimitiveType type) {
        switch (type) {
            case BYTE: return 8;
            case CHAR: case SHORT: return 16;
            case INT: case FLOAT: return 32;
            case LONG: case DOUBLE: return 64;
        }
        throw new AssertionError();
    }

    private HashMethodGeneratorCommons() {}
}
