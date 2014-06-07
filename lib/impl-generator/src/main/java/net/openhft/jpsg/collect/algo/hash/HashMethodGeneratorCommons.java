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

    private static final SimpleOption DHASH = new SimpleOption("DHash");
    private static final SimpleOption QHASH = new SimpleOption("QHash");
    private static final SimpleOption LHASH = new SimpleOption("LHash");

    static boolean isDHash(MethodContext cxt) {
        return DHASH.equals(cxt.getOption("hash"));
    }

    static boolean isQHash(MethodContext cxt) {
        return QHASH.equals(cxt.getOption("hash"));
    }

    static boolean isLHash(MethodContext cxt) {
        return LHASH.equals(cxt.getOption("hash"));
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
