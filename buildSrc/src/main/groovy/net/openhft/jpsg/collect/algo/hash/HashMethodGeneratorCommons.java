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

import net.openhft.jpsg.PrimitiveType;
import net.openhft.jpsg.collect.MethodContext;

import static java.lang.String.format;


public final class HashMethodGeneratorCommons {
    private HashMethodGeneratorCommons() {}

    static String curAssignment(MethodContext cxt, String keys, String key,
            boolean capacityAssigned) {
        return "(cur = " + firstKey(cxt, keys, key, capacityAssigned, true) + ")";
    }

    static String firstKey(MethodContext cxt, String keys, String key,
            boolean capacityAssigned, boolean distinctNullKey) {
        String indexAssignment;
        if (!cxt.isNullKey()) {
            String capacityAssignment = capacityAssigned ?
                    "capacity" :
                    ("(capacity = " + keys + ".length)");
            String hashAssignment = "(hash = " + positiveKeyHash(cxt, key, distinctNullKey) + ")";
            indexAssignment = "index = " + hashAssignment + " % " + capacityAssignment;
        } else {
            indexAssignment = "index = 0";
        }
        return keys + "[" + indexAssignment + "]";
    }

    static String step() {
        return "int step = (hash % (capacity - 2)) + 1;";
    }

    static String nextIndex() {
        return "if ((index -= step) < 0) index += capacity; // nextIndex";
    }

    static String keyHash(MethodContext cxt, String key, boolean distinctNullKey) {
        if (distinctNullKey && cxt.isNullKey()) {
            return "0";
        } if (cxt.isObjectKey() || cxt.isNullKey()) {
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
        if (!cxt.isPrimitiveKey()) {
            return "REMOVED";
        } else if (cxt.isIntegralKey()) {
            return "removed";
        } else {
            return "REMOVED_BITS";
        }
    }
}
