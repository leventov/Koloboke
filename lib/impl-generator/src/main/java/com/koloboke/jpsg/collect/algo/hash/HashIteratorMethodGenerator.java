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
import com.koloboke.jpsg.collect.iter.IteratorMethodGenerator;

import static com.koloboke.jpsg.collect.algo.hash.HashIterMethodGeneratorCommons.*;
import static com.koloboke.jpsg.collect.algo.hash.HashMethodGeneratorCommons.*;


public final class HashIteratorMethodGenerator extends IteratorMethodGenerator {

    @Override
    protected void generateFields() {
        commonFields(this, cxt);
        if (cxt.mutable()) {
            lines("int index = -1;");
        }
        lines(
                "int nextIndex;",
                elemType() + " next;"
        );
    }

    private void loop() {
        INSTANCE.declareEntry(this, cxt);
        String decrement = INSTANCE.doubleSizedParallel(cxt) ? "(nextI -= 2)" : "--nextI";
        lines("while (" + decrement + " >= 0)").block();
        ifKeyNotFreeOrRemoved(this, cxt, "nextI", false);
        if (cxt.isObjectKey())
            lines("// noinspection unchecked");
        lines("next = " + makeNext(cxt, "nextI") + ";");
        lines("break;");
        blockEnd().blockEnd();
        lines("nextIndex = nextI;");
    }

    @Override
    protected void generateConstructor() {
        commonConstructorOps(this, cxt);
        if (!INSTANCE.parallelKV(cxt)) {
            if (cxt.isObjectKey()) {
                lines(
                        "// noinspection unchecked",
                        cxt.keyType() + "[] keys = this.keys = (" + cxt.keyType() + "[]) set;"
                );
            } else {
                lines(cxt.keyUnwrappedType() + "[] keys = this.keys = set;");
            }
        } else {
            lines(INSTANCE.tableType(cxt) + "[] tab = this.tab = table;");
        }
        if (needCapacityMask(cxt)) {
            lines("capacityMask = " + HashMethodGeneratorCommons.INSTANCE.capacityMask(cxt) + ";");
        }
        if (!INSTANCE.parallelKV(cxt) && (cxt.isValueView() || cxt.isEntryView()))
            this.lines(cxt.valueUnwrappedType() + "[] vals = this.vals = values;");
        if (cxt.isIntegralKey()) {
            this.lines(cxt.keyType() + " " + INSTANCE.free(cxt) + " = this." + INSTANCE.free(cxt) + " = freeValue;");
            if (INSTANCE.possibleRemovedSlots(cxt)) {
                this.lines(
                        (INSTANCE.noRemoved(cxt) ? "" : cxt.keyType() + " " + INSTANCE.removed(cxt) + " = ") +
                        "this." + INSTANCE.removed(cxt) + " = removedValue;");
            }
        }
        lines("int nextI = " + INSTANCE.localTableVar(cxt) + ".length;");
        loop();
    }

    @Override
    protected void generateHasNext() {
        ret("nextIndex >= 0");
    }

    @Override
    protected void generateNext() {
        lines("int nextI;");
        ifBlock("(nextI = nextIndex) >= 0");
        checkModCount(this, cxt, true);
        if (cxt.mutable())
            lines("index = nextI;");
        copyKeys(this, cxt);
        copySpecials(this, cxt);
        lines(elemType() + " prev = next;");
        loop();
        ret("prev");
        endOfModCountCheck(this, cxt);
        elseBlock();
        lines("throw new java.util.NoSuchElementException();");
        blockEnd();
    }

    @Override
    protected void generateRemove() {
        permissions.add(Permission.REMOVE);
        lines("int index;");
        ifBlock("(index = this.index) >= 0");
        ifBlock("expectedModCount++ == " + modCount());
        lines("this.index = -1;");
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
        HashMethodGeneratorCommons.INSTANCE.eraseSlot(this, cxt, "index", "index");
        lines("postRemoveHook();");
    }

    private void lHashShiftRemove() {
        new LHashIterShiftRemove(this, cxt) {
            @Override
            String slotsToCopy() {
                // `nextIndex + 1`, because if remove() is called
                // on the next iteration, the key is removed from the original table
                // by `justRemove(keys[index])` call, i. e. if we will copy just `nextIndex`
                // entries, `keys[index]` indexing will be out of `keys` array bounds.
                return "nextIndex + " + INSTANCE.slots(1, getCxt());
            }

            @Override
            void onInitialSlotSubstitution() {
                lines("this.nextIndex = index;");
                ifBlock("indexToShift < index - " + INSTANCE.slots(1, getCxt())); {
                    lines("this.next = " +
                            makeNext(getCxt(), modCount(), "keyToShift", "indexToShift", false) + ";");
                } blockEnd();
            }

            @Override
            String keyToRemoveFromTheOriginalTable() {
                return INSTANCE.readKeyOnly(getCxt(), "index");
            }
        }.generate();
    }

    @Override
    protected void generateForEachRemaining() {
        requireNonNull("action");
        if (!cxt.immutable())
            lines("int mc = expectedModCount;");
        copyArrays(this, cxt);
        copySpecials(this, cxt);
        INSTANCE.declareEntry(this, cxt);
        lines("int nextI = nextIndex;");
        INSTANCE.forLoop(this, cxt, "nextI", "i", true); {
            ifKeyNotFreeOrRemoved(this, cxt, "i", false); {
                if (cxt.isObjectKey())
                    lines("// noinspection unchecked");
                lines("action.accept(" + makeNext(cxt, "i") + ");");
            } blockEnd();
        } blockEnd();
        String concurrentModCond = "nextI != nextIndex";
        if (!cxt.immutable())
            concurrentModCond += " || mc != " + modCount();
        ifBlock(concurrentModCond); {
            concurrentMod();
        } blockEnd();
        lines((cxt.mutable() ? "index = " : "") + "nextIndex = -1;");
    }

    private String elemType() {
        if (cxt.isKeyView()) {
            return cxt.keyType();
        } else if (cxt.isValueView()) {
            return cxt.valueType();
        } else if (cxt.isEntryView()) {
            return entryType();
        } else {
            throw new IllegalStateException();
        }
    }

    private String entryType() {
        return (cxt.immutable() ? "Immutable" : "Mutable") + "Entry";
    }
}
