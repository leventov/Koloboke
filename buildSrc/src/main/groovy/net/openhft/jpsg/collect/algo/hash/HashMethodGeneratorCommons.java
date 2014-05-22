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

import static java.lang.String.format;


final class HashMethodGeneratorCommons {
    private HashMethodGeneratorCommons() {}

    static String curAssignment(MethodContext cxt, String keys, String key,
            boolean capacityAssigned) {
        return "(cur = " + firstKey(cxt, keys, key, capacityAssigned, true) + ")";
    }

    static String firstKey(MethodContext cxt, String keys, String key,
            boolean capacityAssigned, boolean distinctNullKey) {
        String indexAssignment;
        if (!cxt.isNullKey()) {
            String modulo;
            if (isLHash(cxt)) {
                modulo = capacityAssigned ? " & capacityMask" :
                        (" & (capacityMask = " + keys + ".length - 1)");
            } else {
                modulo = capacityAssigned ? " % capacity" :
                        (" % (capacity = " + keys + ".length)");
            }
            String hashAssignment = isLHash(cxt) ? keyHash(cxt, key, distinctNullKey) :
                    "(hash = " + positiveKeyHash(cxt, key, distinctNullKey) + ")";
            indexAssignment = "index = " + hashAssignment + modulo;
        } else {
            indexAssignment = "index = 0";
        }
        return keys + "[" + indexAssignment + "]";
    }

    static void computeStep(MethodGenerator g, MethodContext cxt) {
        if (!cxt.isNullKey() && !isLHash(cxt))
            g.lines("int step = (hash % (capacity - 2)) + 1;");
    }

    static void nextIndex(MethodGenerator g, MethodContext cxt) {
        if (isLHash(cxt)) {
            g.lines("index = (index - 1) & capacityMask;");
        } else {
            if (!cxt.isNullKey()) {
                g.lines("if ((index -= step) < 0) index += capacity; // nextIndex");
            } else {
                g.lines("index++;");
            }
        }
    }

    static String keyHash(MethodContext cxt, String key, boolean distinctNullKey) {
        if (distinctNullKey && cxt.isNullKey()) {
            return "0";
        } else if (cxt.isObjectOrNullKey()) {
            return (distinctNullKey ? "keyHashCode" : "nullableKeyHashCode") + "(" + key + ")";
        } else {
            PrimitiveType keyOption = (PrimitiveType) cxt.keyOption();
            switch (keyOption) {
                case BYTE: case SHORT: case CHAR:
                    return "((int) " + key + ")";
                case INT: case FLOAT:
                    return key;
                case LONG: case DOUBLE:
                    return format("((int) (%s ^ (%s >>> 32)))", key, key);
                default:
                    throw new IllegalStateException();
            }
        }
    }

    private static String positiveKeyHash(MethodContext cxt, String key, boolean distinctNullKey) {
        if (distinctNullKey && cxt.isNullKey()) {
            return "0";
        } if (cxt.isObjectOrNullKey()) {
            return (distinctNullKey ? "keyHashCode" : "nullableKeyHashCode") +
                    "(" + key + ") & Integer.MAX_VALUE";
        } else {
            PrimitiveType keyOption = (PrimitiveType) cxt.keyOption();
            switch (keyOption) {
                case BYTE: return key + " & BYTE_MASK";
                case SHORT: return key + " & SHORT_MASK";
                case CHAR: return key;
                case INT:
                case FLOAT:
                    return key + " & Integer.MAX_VALUE";
                case LONG:
                case DOUBLE:
                    return format("((int) (%s ^ (%s >>> 32))) & Integer.MAX_VALUE", key, key);
                default:
                    throw new IllegalStateException();
            }
        }
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

    static final SimpleOption DHASH = new SimpleOption("DHash");
    static final SimpleOption LHASH = new SimpleOption("LHash");

    static boolean isDHash(MethodContext cxt) {
        return DHASH.equals(cxt.getOption("hash"));
    }

    static boolean isLHash(MethodContext cxt) {
        return LHASH.equals(cxt.getOption("hash"));
    }

    static boolean possibleRemovedSlots(MethodContext cxt) {
        return cxt.mutable() && !isLHash(cxt);
    }

    static class ShiftRemove {
        MethodGenerator g;
        MethodContext cxt;
        String values;

        ShiftRemove(MethodGenerator g, MethodContext cxt, String values) {
            this.g = g;
            this.cxt = cxt;
            this.values = values;
        }

        void generate() {
            g.incrementModCount();
            g.lines("int indexToRemove = index;");
            g.lines("int indexToShift = indexToRemove;");
            g.lines("int shiftDistance = 1;");
            g.lines("while (true)").block(); {
                g.lines("indexToShift = (indexToShift - 1) & capacityMask;");
                g.lines(cxt.keyUnwrappedType() + " keyToShift;");
                g.ifBlock(isFree(cxt, "(keyToShift = keys[indexToShift])")); {
                    g.lines("break;");
                } g.blockEnd();
                String keyDistance =  "((" + keyHash(cxt, "keyToShift", false) +
                        " - indexToShift) & capacityMask)";
                g.ifBlock(keyDistance + " >= shiftDistance"); {
                    beforeShift();
                    g.lines("keys[indexToRemove] = keyToShift;");
                    if (cxt.hasValues())
                        g.lines(values + "[indexToRemove] = " + values + "[indexToShift];");
                    g.lines("indexToRemove = indexToShift;");
                    g.lines("shiftDistance = 1;");
                } g.elseBlock(); {
                    g.lines("shiftDistance++;");
                } g.blockEnd();
            } g.blockEnd();
            eraseSlot(g, cxt, "indexToRemove", "indexToRemove", true, values);
            g.lines("postRemoveHook();");
        }

        void beforeShift() {
        }
    }

    static void eraseSlot(MethodGenerator g, MethodContext cxt,
            String indexForKeys, String indexForValues) {
        eraseSlot(g, cxt, indexForKeys, indexForValues, true);
    }

    static void eraseSlot(MethodGenerator g, MethodContext cxt,
            String indexForKeys, String indexForValues, boolean genericKeys) {
        eraseSlot(g, cxt, indexForKeys, indexForValues, genericKeys, "vals");
    }

    static void eraseSlot(MethodGenerator g, MethodContext cxt,
            String indexForKeys, String indexForValues, boolean genericKeys, String values) {
        String keys = cxt.isObjectOrNullKey() && genericKeys ? "((Object[]) keys)" : "keys";
        g.lines(keys + "[" + indexForKeys + "] = " + removed(cxt) + ";");
        if (cxt.isObjectValue()) {
            g.lines(values + "[" + indexForValues + "] = null;");
        }
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
}
