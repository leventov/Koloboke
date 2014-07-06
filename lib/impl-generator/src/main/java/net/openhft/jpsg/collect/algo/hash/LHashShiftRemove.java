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

import net.openhft.jpsg.collect.MethodContext;
import net.openhft.jpsg.collect.MethodGenerator;

import static net.openhft.jpsg.collect.algo.hash.HashMethodGeneratorCommons.*;
import static net.openhft.jpsg.collect.algo.hash.HashMethodGeneratorCommons.eraseSlot;
import static net.openhft.jpsg.collect.algo.hash.HashMethodGeneratorCommons.isFree;


class LHashShiftRemove {
    final MethodGenerator g;
    final MethodContext cxt;
    final String index;
    final String table;
    final String values;

    LHashShiftRemove(MethodGenerator g, MethodContext cxt, String index, String table,
            String values) {
        this.g = g;
        this.cxt = cxt;
        this.index = index;
        this.table = table;
        this.values = values;
    }

    void generate() {
        g.incrementModCount();
        closeDeletion();
        postRemoveHook();
    }

    final void closeDeletion() {
        g.lines("int indexToRemove = " + index + ";");
        g.lines("int indexToShift = indexToRemove;");
        g.lines("int shiftDistance = " + slots(1, cxt) +";");
        g.lines("while (true)").block(); {
            g.lines("indexToShift = (indexToShift - " + slots(1, cxt) + ") & capacityMask;");
            g.lines(cxt.keyUnwrappedType() + " keyToShift;");
            String key = readKeyOrEntry(cxt, "indexToShift");
            if (cxt.isObjectKey() && rawKeys()) {
                key = "(" + cxt.keyType() + ") " + key;
            }
            g.ifBlock(isFree(cxt, "(keyToShift = " + key + ")")); {
                g.lines("break;");
            } g.blockEnd();
            String keyDistance =  "((" + keyHash(cxt, "keyToShift", false) +
                    " - indexToShift) & capacityMask)";
            String shiftCondition = keyDistance + " >= shiftDistance";
            String shiftPrecondition = additionalShiftPrecondition();
            if (!shiftPrecondition.isEmpty()) {
                shiftCondition = "(" + shiftPrecondition + ") && (" + shiftCondition + ")";
            }
            g.ifBlock(shiftCondition); {
                beforeShift();
                writeKeyAndValue(g, cxt, table, "keys", values, "indexToRemove", "keyToShift",
                        () -> readValue(cxt, table, values, "indexToShift"), false,
                        cxt.hasValues());
                g.lines("indexToRemove = indexToShift;");
                g.lines("shiftDistance = " + slots(1, cxt) + ";");
            } g.elseBlock(); {
                String increment = doubleSizedParallel(cxt) ? " += 2" : "++";
                g.lines("shiftDistance" + increment + ";");
                if (cxt.isPrimitiveKey()) {
                    // if keys are primitives, free value could change during the close deletion
                    // loop, and if it then be removed immediately (i'm not even sure this
                    // is possible), then we hang on forever, because `keyToShift == free`
                    // is the only way to break the close deletion loop.
                    // TODO understand when this check could be avoided
                    g.ifBlock("indexToShift == " + slots(1, cxt) + " + " + index); {
                        g.concurrentMod();
                    } g.blockEnd();
                }
            } g.blockEnd();
        } g.blockEnd();
        eraseSlot(g, cxt, "indexToRemove", "indexToRemove", !rawKeys(), values);
    }

    final void postRemoveHook() {
        g.lines("postRemoveHook();");
    }

    boolean rawKeys() {
        return false;
    }

    String additionalShiftPrecondition() {
        return "";
    }

    void beforeShift() {
        // no-op by default
    }
}
