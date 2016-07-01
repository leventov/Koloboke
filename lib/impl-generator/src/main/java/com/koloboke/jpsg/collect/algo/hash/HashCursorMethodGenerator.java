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

package com.koloboke.jpsg.collect.algo.hash;

import com.koloboke.jpsg.collect.Permission;
import com.koloboke.jpsg.collect.iter.CursorMethodGenerator;

import static com.koloboke.jpsg.collect.algo.hash.HashIterMethodGeneratorCommons.*;
import static com.koloboke.jpsg.collect.algo.hash.HashMethodGeneratorCommons.*;


public final class HashCursorMethodGenerator extends CursorMethodGenerator {

    @Override
    protected void generateFields() {
        commonFields(this, cxt);
        lines(
                "int index;",
                INSTANCE.keyArrayType(cxt) + " curKey;"
        );
        if (!cxt.isKeyView()) {
            lines(cxt.valueUnwrappedType() + " curValue;");
        }
    }

    @Override
    protected void generateConstructor() {
        commonConstructorOps(this, cxt);
        if (!INSTANCE.parallelKV(cxt)) {
            lines((needCapacityMask(cxt) ? INSTANCE.keyArrayType(cxt) + "[] keys = " : "") +
                    "this.keys = set;");
        } else {
            lines((needCapacityMask(cxt) ? INSTANCE.tableType(cxt) + "[] tab = " : "") +
                    "this.tab = table;");
        }
        if (needCapacityMask(cxt))
            lines("capacityMask = " + HashMethodGeneratorCommons.INSTANCE.capacityMask(cxt) + ";");
        lines("index = " + INSTANCE.localTableVar(cxt) + ".length;");
        if (!cxt.isKeyView() && !INSTANCE.parallelKV(cxt))
            this.lines("vals = values;");
        if (cxt.isIntegralKey()) {
            this.lines(cxt.keyUnwrappedType() + " " + INSTANCE.free(cxt) +
                    " = this." + INSTANCE.free(cxt) + " = freeValue;");
            if (INSTANCE.possibleRemovedSlots(cxt))
                this.lines("this." + INSTANCE.removed(cxt) + " = removedValue;");
        }
        lines("curKey = " + INSTANCE.free(cxt) + ";");
    }

    @Override
    protected void generateMoveNext() {
        checkModCount(this, cxt, false);
        copyKeys(this, cxt);
        copySpecials(this, cxt);
        INSTANCE.declareEntry(this, cxt);
        INSTANCE.forLoop(this, cxt, "index", "i", false); {
            ifKeyNotFreeOrRemoved(this, cxt, "i", true); {
                lines("index = i;");
                lines("curKey = key;");
                if (!cxt.isKeyView())
                    lines("curValue = " + INSTANCE.readValue(cxt, "i") + ";");
                lines("return true;");
            } blockEnd();
        } blockEnd();
        lines(
                "curKey = " + INSTANCE.free(cxt) + ";",
                "index = -1;",
                "return false;"
        );
        endOfModCountCheck(this, cxt);
    }

    @Override
    protected void generateKey() {
        lines(INSTANCE.keyArrayType(cxt) + " curKey;");
        ifBlock(INSTANCE.isNotFree(cxt, "(curKey = this.curKey)"));
        if (cxt.isObjectKey())
            lines("// noinspection unchecked");
        ret(wrapKey(unwrappedKey()));
        endOfIllegalStateCheck(this, cxt);
    }

    @Override
    protected void generateValue() {
        ifBlock(INSTANCE.isNotFree(cxt, "curKey"));
        ret(wrapValue("curValue"));
        endOfIllegalStateCheck(this, cxt);
    }

    @Override
    protected void generateSetValue() {
        ifBlock(INSTANCE.isNotFree(cxt, "curKey"));
        checkModCount(this, cxt, false);
        lines(cxt.valueUnwrappedType() + " unwrappedValue = curValue = " + unwrapValue("value") +
                ";");
        INSTANCE.writeValue(this, cxt, "index", "unwrappedValue");
        if (possibleArrayCopyOnRemove(cxt)) {
            String tableHaveCopied = INSTANCE.parallelKV(cxt) ? "tab != table" : "vals != values";
            ifBlock(tableHaveCopied); {
                INSTANCE.writeValue(this, cxt, "table", "values", "index", "unwrappedValue");
            } blockEnd();
        }
        endOfModCountCheck(this, cxt);
        endOfIllegalStateCheck(this, cxt);
    }

    @Override
    protected void generateEntry() {
        lines(INSTANCE.keyArrayType(cxt) + " curKey;");
        ifBlock(INSTANCE.isNotFree(cxt, "(curKey = this.curKey)"));
        if (cxt.isObjectKey())
            lines("// noinspection unchecked");
        ret(entry(cxt, "expectedModCount", "index", unwrappedKey(), "curValue"));
        endOfIllegalStateCheck(this, cxt);
    }

    private String unwrappedKey() {
        return (!INSTANCE.specializedKeysArray(cxt) ? "(" + cxt.keyType() + ") " : "") + "curKey";
    }

    @Override
    protected void generateRemove() {
        permissions.add(Permission.REMOVE);
        String curKeyAssignment;
        if (INSTANCE.isLHash(cxt)) {
            lines(INSTANCE.keyArrayType(cxt) + " curKey;");
            curKeyAssignment = "(curKey = this.curKey)";
        } else {
            curKeyAssignment = "curKey";
        }
        if (cxt.isIntegralKey()) {
            lines(cxt.keyType() + " " + INSTANCE.free(cxt) + ";");
            ifBlock(curKeyAssignment + " != (" + INSTANCE.free(cxt) +" = this." + INSTANCE.free(cxt) + ")");
        } else {
            ifBlock(INSTANCE.isNotFree(cxt, curKeyAssignment));
        }
        if (cxt.concurrentModificationChecked())
            ifBlock("expectedModCount++ == " + modCount());
        // Local copy still holds the current key (could be used in lHashShiftRemove())
        lines("this.curKey = " + INSTANCE.free(cxt) + ";");
        if (INSTANCE.isLHash(cxt)) {
            INSTANCE.declareEntry(this, cxt);
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
        HashMethodGeneratorCommons.INSTANCE.eraseSlot(this, cxt, indexAssignment, "index");
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
                String increment = INSTANCE.doubleSizedParallel(getCxt()) ? "(index += 2)" : "++index";
                lines("this.index = " + increment + ";");
            }

            @Override
            String keyToRemoveFromTheOriginalTable() {
                return "curKey";
            }
        }.generate();
    }

    @Override
    protected void generateForEachForward() {
        requireNonNull("action");
        if (cxt.concurrentModificationChecked())
            lines("int mc = expectedModCount;");
        copyArrays(this, cxt);
        copySpecials(this, cxt);
        INSTANCE.declareEntry(this, cxt);
        lines("int index = this.index;");
        INSTANCE.forLoop(this, cxt, "index", "i", false); {
            ifKeyNotFreeOrRemoved(this, cxt, "i", false); {
                if (cxt.isObjectKey())
                    lines("// noinspection unchecked");
                lines("action.accept(" + makeNext(cxt, "i") + ");");
            } blockEnd();
        } blockEnd();
        String concurrentModCond = "index != this.index";
        if (cxt.concurrentModificationChecked())
            concurrentModCond += " || mc != " + modCount();
        ifBlock(concurrentModCond); {
            concurrentMod();
        } blockEnd();
        lines("this.index = -1;");
        lines("curKey = " + INSTANCE.free(cxt) + ";");
    }
}
