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
        if (!INSTANCE.parallelKV(cxt)) {
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
        lines(cxt.keyUnwrappedRawType() + " curKey;");
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
        INSTANCE.writeValue(this, cxt, "index", unwrapValue("value"));
        if (possibleArrayCopyOnRemove(cxt)) {
            String tableHaveCopied = INSTANCE.parallelKV(cxt) ? "tab != table" : "vals != values";
            ifBlock(tableHaveCopied); {
                INSTANCE.writeValue(this, cxt, "table", "values", "index", unwrapValue("value"));
            } blockEnd();
        }
        endOfModCountCheck(this, cxt);
        endOfIllegalStateCheck(this, cxt);
    }

    @Override
    protected void generateEntry() {
        lines(cxt.keyUnwrappedRawType() + " curKey;");
        ifBlock(INSTANCE.isNotFree(cxt, "(curKey = this.curKey)"));
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
        if (INSTANCE.isLHash(cxt)) {
            lines(cxt.keyUnwrappedRawType() + " curKey;");
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
                String key = "curKey";
                if (getCxt().isObjectKey())
                    key = "(" + getCxt().keyType() + ") " + key;
                return key;
            }
        }.generate();
    }

    @Override
    protected void generateForEachForward() {
        requireNonNull("action");
        if (!cxt.immutable())
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
        if (!cxt.immutable())
            concurrentModCond += " || mc != " + modCount();
        ifBlock(concurrentModCond); {
            concurrentMod();
        } blockEnd();
        lines("this.index = -1;");
        lines("curKey = " + INSTANCE.free(cxt) + ";");
    }
}
