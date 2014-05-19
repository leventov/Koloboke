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

import static net.openhft.jpsg.collect.algo.hash.HashMethodGeneratorCommons.*;


public final class HashIterMethodGeneratorCommons {

    private HashIterMethodGeneratorCommons() {}

    static void commonFields(MethodGenerator g, MethodContext cxt) {
        g.lines("final " + cxt.keyUnwrappedType() + "[] keys;");
        if (!cxt.isKeyView()) {
            g.lines("final " + cxt.valueUnwrappedType() + "[] vals;");
        }
        if (cxt.isIntegralKey()) {
            g.lines("final " + cxt.keyType() + " " + free(cxt) + ";");
            if (cxt.mutable()) {
                g.lines("final " + cxt.keyType() + " " + removed(cxt) + ";");
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

    static void ifKeyNotFreeOrRemoved(MethodGenerator gen, MethodContext cxt,
            String indexVariableName, boolean forceCopyKey) {
        String keyAssignment;
        if (forceCopyKey || (!noRemoved(cxt) && !cxt.isFloatingKey()) || !cxt.isValueView()) {
            gen.lines(cxt.keyUnwrappedRawType() + " key;");
            keyAssignment = "(key = keys[" + indexVariableName + "])";
        } else {
            keyAssignment = "keys[" + indexVariableName + "]";
        }
        String cond = cxt.isFloatingKey() ?
                keyAssignment + " < FREE_BITS" :
                isNotFree(cxt, keyAssignment) +
                        (noRemoved(cxt) ? "" : (" && " + isNotRemoved(cxt, "key")));
        gen.ifBlock(cond);
    }

    static String makeNext(MethodContext cxt, String index) {
        if (cxt.isKeyView()) {
            return makeKey(cxt, true);
        } else if (cxt.isValueView()) {
            return makeValue(cxt, index);
        } else if (cxt.isEntryView()) {
            return entry(cxt, makeKey(cxt, false), index);
        } else if (cxt.isMapView()) {
            return makeKey(cxt, true) + ", " + makeValue(cxt, index);
        } else {
            throw new IllegalStateException();
        }
    }

    private static String makeKey(MethodContext cxt, boolean wrap) {
        String key = "key";
        if (cxt.isObjectKey()) {
            key = "(" + cxt.keyType() + ") " + key;
        }
        if (wrap) key = MethodGenerator.wrap(cxt, cxt.keyOption(), key);
        return key;
    }

    private static String makeValue(MethodContext cxt, String index) {
        return MethodGenerator.wrap(cxt, cxt.mapValueOption(), "vals[" + index + "]");
    }

    static boolean noRemoved(MethodContext cxt) {
        Option removed = cxt.getOption("removed");
        return removed != null && removed.toString().equalsIgnoreCase("no");
    }

    static String modCount() {
        return "modCount()";
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

    static void copySpecials(MethodGenerator g, MethodContext cxt) {
        if (cxt.isIntegralKey()) {
            g.lines(cxt.keyType() + " " + free(cxt) + " = this." + free(cxt) + ";");
            if (cxt.mutable() && !noRemoved(cxt)) {
                g.lines(cxt.keyType() + " " + removed(cxt) + " = this." + removed(cxt) + ";");
            }
        }
    }

    static void copyArrays(MethodGenerator g, MethodContext cxt) {
        copyKeys(g, cxt);
        if (!cxt.isKeyView())
            g.lines(cxt.valueUnwrappedType() + "[] vals = this.vals;");
    }

    static void copyKeys(MethodGenerator g, MethodContext cxt) {
        g.lines(cxt.keyUnwrappedRawType() + "[] keys = this.keys;");
    }
}
