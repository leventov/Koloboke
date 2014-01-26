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

import net.openhft.jpsg.Option;
import net.openhft.jpsg.collect.MethodContext;
import net.openhft.jpsg.collect.MethodGenerator;

import static net.openhft.jpsg.collect.algo.hash.HashMethodGeneratorCommons.free;
import static net.openhft.jpsg.collect.algo.hash.HashMethodGeneratorCommons.removed;


public final class HashIterMethodGeneratorCommons {

    private HashIterMethodGeneratorCommons() {}

    static void commonFields(MethodGenerator g, MethodContext cxt) {
        g.lines("final " + cxt.keyType() + "[] keys;");
        if (!cxt.isKeyView()) {
            g.lines("final " + cxt.valueType() + "[] vals;");
        }
        if (cxt.isPrimitiveKey()) {
            g.lines("final " + cxt.keyType() + " " + free(cxt) + ";");
            if (cxt.mutable()) {
                g.lines("final " + cxt.keyType() + " " +
                        removed(cxt) + ";");
            }
        }
        if (cxt.mutable()) {
            g.lines("int expectedModCount;");
        }
    }

    static void commonConstructorOps(MethodGenerator g, MethodContext cxt, boolean copyModCount) {
        if (cxt.mutable()) {
            String mc;
            if (cxt.isEntryView() && copyModCount) {
                mc = "int mc = expectedModCount";
            } else {
                mc = "expectedModCount";
            }
            g.lines(mc + " = " + modCount() + ";");
        }
    }

    static void checkModCount(MethodGenerator g, MethodContext cxt, boolean copyModCount) {
        if (cxt.mutable()) {
            String mc;
            if (cxt.isEntryView() && copyModCount) {
                g.lines("int mc;");
                mc = "(mc = expectedModCount)";
            } else {
                mc = "expectedModCount";
            }
            g.ifBlock(mc + " == " + modCount());
        }
    }

    static void endOfModCountCheck(MethodGenerator g, MethodContext cxt) {
        if (cxt.mutable()) {
            g.elseBlock();
            g.concurrentMod();
            g.blockEnd();
        }
    }

    static void endOfIllegalStateCheck(MethodGenerator g, MethodContext cxt) {
        g.elseBlock();
        g.illegalState();
        g.blockEnd();
    }

    static String keyNotFreeOrRemoved(MethodContext cxt, String indexVariableName,
            boolean copyKey) {
        if (noRemoved(cxt) && cxt.isValueView() && !copyKey) {
            return "keys[" + indexVariableName + "] != " + free(cxt);
        }
        return "(key = keys[" + indexVariableName + "]) != " + free(cxt) +
                (noRemoved(cxt) ? "" : (" && key != " + removed(cxt)));
    }

    static String makeNext(MethodContext cxt, String index) {
        String key = "key";
        if (cxt.isObjectKey()) {
            key = "(" + cxt.keyType() + ") " + key;
        }
        String value = "vals[" + index + "]";
        if (cxt.isKeyView()) {
            return key;
        } else if (cxt.isValueView()) {
            return value;
        } else if (cxt.isEntryView()) {
            return entry(cxt, key, index);
        } else if (cxt.isMapView()) {
            return key + ", " + value;
        } else {
            throw new IllegalStateException();
        }
    }

    static boolean noRemoved(MethodContext cxt) {
        Option removed = cxt.getOption("removed");
        return removed != null && removed.toString().equalsIgnoreCase("no");
    }

    static String modCount() {
        return "modCount()";
    }

    static String elemType(MethodContext cxt) {
        if (cxt.isKeyView()) {
            return cxt.keyType();
        } else if (cxt.isValueView()) {
            return cxt.valueType();
        } else if (cxt.isEntryView()) {
            return entryType(cxt);
        } else {
            throw new IllegalStateException();
        }
    }

    static String entry(MethodContext cxt, String key, String index) {
        return entry(cxt, "mc", index, key, "vals[" + index + "]");
    }

    static String entry(MethodContext cxt, String mc, String index, String key, String value) {
        if (cxt.mutable()) {
            return "new MutableEntry(" + mc + ", " + index + ", " + key + ", " + value + ")";
        } else {
            return "new ImmutableEntry(" + key + ", " + value + ")";
        }
    }

    static String entryType(MethodContext cxt) {
        return (cxt.mutable() ? "Mutable" : "Immutable") + "Entry";
    }

    static void copySpecials(MethodGenerator g, MethodContext cxt) {
        if (cxt.isPrimitiveKey()) {
            g.lines(cxt.keyType() + " " + free(cxt) + " = this." + free(cxt) + ";");
            if (cxt.mutable() && !noRemoved(cxt)) {
                g.lines(cxt.keyType() + " " + removed(cxt) + " = this." + removed(cxt) + ";");
            }
        }
    }

    static void copyArrays(MethodGenerator g, MethodContext cxt) {
        g.lines(cxt.keyRawType() + "[] keys = this.keys;");
        if (!cxt.isKeyView())
            g.lines(cxt.valueType() + "[] vals = this.vals;");
    }
}
