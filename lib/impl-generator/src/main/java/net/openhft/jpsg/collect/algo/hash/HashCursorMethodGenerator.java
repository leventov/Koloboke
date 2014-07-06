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

import net.openhft.jpsg.collect.Permission;
import net.openhft.jpsg.collect.iter.CursorMethodGenerator;

import static net.openhft.jpsg.collect.algo.hash.HashIterMethodGeneratorCommons.*;
import static net.openhft.jpsg.collect.algo.hash.HashMethodGeneratorCommons.*;


public final class HashCursorMethodGenerator extends CursorMethodGenerator {

    @Override
    protected void generateFields() {
        commonFields(this, cxt);
        lines(
                "int index;",
                cxt.keyUnwrappedRawType() + " curKey;"
        );
        if (!cxt.isKeyView()) {
            lines(cxt.valueUnwrappedType() + " curValue;");
        }
    }

    @Override
    protected void generateConstructor() {
        commonConstructorOps(this, cxt);
        if (!parallelKV(cxt)) {
            if (cxt.isObjectKey()) {
                lines(
                        "// noinspection unchecked",
                        (needCapacityMask(cxt) ? cxt.keyType() + "[] keys = " : "") +
                                "this.keys = (" + cxt.keyUnwrappedType() + "[]) set;"
                );
            } else {
                lines((needCapacityMask(cxt) ? cxt.keyUnwrappedType() + "[] keys = " : "") +
                        "this.keys = set;");
            }
        } else {
            lines((needCapacityMask(cxt) ? tableType(cxt) + "[] tab = " : "") +
                    "this.tab = table;");
        }
        if (needCapacityMask(cxt))
            lines("capacityMask = " + capacityMask(cxt) + ";");
        if (!cxt.isKeyView() && !parallelKV(cxt))
            this.lines("vals = values;");
        if (cxt.isIntegralKey()) {
            this.lines(cxt.keyUnwrappedType() + " " + free(cxt) +
                    " = this." + free(cxt) + " = freeValue;");
            if (possibleRemovedSlots(cxt))
                this.lines("this." + removed(cxt) + " = removedValue;");
        }
        lines("curKey = " + free(cxt) + ";");
    }

    @Override
    protected void generateMoveNext() {
        checkModCount(this, cxt, false);
        copyKeys(this, cxt);
        copySpecials(this, cxt);
        declareEntry(this, cxt);
        forLoop(this, cxt, "index", "i", false); {
            ifKeyNotFreeOrRemoved(this, cxt, "i", true); {
                lines("index = i;");
                lines("curKey = key;");
                if (!cxt.isKeyView())
                    lines("curValue = " + readValue(cxt, "i") + ";");
                lines("return true;");
            } blockEnd();
        } blockEnd();
        lines(
                "curKey = " + free(cxt) + ";",
                "index = -1;",
                "return false;"
        );
        endOfModCountCheck(this, cxt);
    }

    @Override
    protected void generateKey() {
        lines(cxt.keyUnwrappedRawType() + " curKey;");
        ifBlock(isNotFree(cxt, "(curKey = this.curKey)"));
        if (cxt.isObjectKey())
            lines("// noinspection unchecked");
        ret(wrapKey(unwrappedKey()));
        endOfIllegalStateCheck(this, cxt);
    }

    @Override
    protected void generateValue() {
        ifBlock(isNotFree(cxt, "curKey"));
        ret(wrapValue("curValue"));
        endOfIllegalStateCheck(this, cxt);
    }

    @Override
    protected void generateSetValue() {
        ifBlock(isNotFree(cxt, "curKey"));
        checkModCount(this, cxt, false);
        writeValue(this, cxt, "index", unwrapValue("value"));
        if (possibleArrayCopyOnRemove(cxt)) {
            String tableHaveCopied = parallelKV(cxt) ? "tab != table" : "vals != values";
            ifBlock(tableHaveCopied); {
                writeValue(this, cxt, "table", "values", "index", unwrapValue("value"));
            } blockEnd();
        }
        endOfModCountCheck(this, cxt);
        endOfIllegalStateCheck(this, cxt);
    }

    @Override
    protected void generateEntry() {
        lines(cxt.keyUnwrappedRawType() + " curKey;");
        ifBlock(isNotFree(cxt, "(curKey = this.curKey)"));
        if (cxt.isObjectKey())
            lines("// noinspection unchecked");
        ret(entry(cxt, "expectedModCount", "index", unwrappedKey(), "curValue"));
        endOfIllegalStateCheck(this, cxt);
    }

    private String unwrappedKey() {
        return (cxt.isObjectKey() ? "(" + cxt.keyType() + ") " : "") + "curKey";
    }

    @Override
    protected void generateRemove() {
        permissions.add(Permission.REMOVE);
        String curKeyAssignment;
        if (isLHash(cxt)) {
            lines(cxt.keyUnwrappedRawType() + " curKey;");
            curKeyAssignment = "(curKey = this.curKey)";
        } else {
            curKeyAssignment = "curKey";
        }
        if (cxt.isIntegralKey()) {
            lines(cxt.keyType() + " " + free(cxt) + ";");
            ifBlock(curKeyAssignment + " != (" + free(cxt) +" = this." + free(cxt) + ")");
        } else {
            ifBlock(isNotFree(cxt, curKeyAssignment));
        }
        ifBlock("expectedModCount++ == " + modCount());
        // Local copy still holds the current key (could be used in lHashShiftRemove())
        lines("this.curKey = " + free(cxt) + ";");
        if (isLHash(cxt)) {
            declareEntry(this, cxt);
            lHashShiftRemove();
        } else {
            tombstoneRemove();
        }
        endOfModCountCheck(this, cxt);
        endOfIllegalStateCheck(this, cxt);
    }

    private void tombstoneRemove() {
        incrementModCount();
        if (cxt.isObjectValue())
            lines("int index;");
        String indexAssignment = cxt.isObjectValue() ? "index = this.index" : "index";
        eraseSlot(this, cxt, indexAssignment, "index");
        lines("postRemoveHook();");
    }

    private void lHashShiftRemove() {
        lines("int index = this.index;");
        new LHashIterShiftRemove(this, cxt) {
            @Override
            String slotsToCopy() {
                return "index";
            }

            @Override
            void onInitialSlotSubstitution() {
                // Step `index` back, because moveNext() starts from `index` - 1 and won't see
                // the entry shifted to the slot at `index`.
                // Update the local copy too, because this variable could be used
                // later in the table copying (see IterShiftRemove.beforeShift()).
                String increment = doubleSizedParallel(cxt) ? "(index += 2)" : "++index";
                lines("this.index = " + increment + ";");
            }

            @Override
            String keyToRemoveFromTheOriginalTable() {
                String key = "curKey";
                if (cxt.isObjectKey())
                    key = "(" + cxt.keyType() + ") " + key;
                return key;
            }
        }.generate();
    }

    @Override
    protected void generateForEachForward() {
        if (!cxt.immutable())
            lines("int mc = expectedModCount;");
        copyArrays(this, cxt);
        copySpecials(this, cxt);
        declareEntry(this, cxt);
        lines("int index = this.index;");
        forLoop(this, cxt, "index", "i", false); {
            ifKeyNotFreeOrRemoved(this, cxt, "i", false); {
                if (cxt.isObjectKey())
                    lines("// noinspection unchecked");
                lines("action.accept(" + makeNext(cxt, "i") + ");");
            } blockEnd();
        } blockEnd();
        String concurrentModCond = "index != this.index";
        if (!cxt.immutable())
            concurrentModCond += " || mc != " + modCount();
        ifBlock(concurrentModCond); {
            concurrentMod();
        } blockEnd();
        lines("this.index = -1;");
        lines("curKey = " + free(cxt) + ";");
    }
}
