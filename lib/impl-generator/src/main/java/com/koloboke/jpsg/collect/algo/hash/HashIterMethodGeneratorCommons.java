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

import com.koloboke.jpsg.collect.MethodContext;
import com.koloboke.jpsg.collect.MethodGenerator;

import static com.koloboke.jpsg.collect.algo.hash.HashMethodGeneratorCommons.*;


final class HashIterMethodGeneratorCommons {

    private HashIterMethodGeneratorCommons() {}

    static void commonFields(MethodGenerator g, MethodContext cxt) {
        String arrayCopiesMod = possibleArrayCopyOnRemove(cxt) ? "" : "final ";
        if (!INSTANCE.parallelKV(cxt)) {
            g.lines(arrayCopiesMod + INSTANCE.keyArrayType(cxt) + "[] keys;");
            if (!cxt.isKeyView())
                g.lines(arrayCopiesMod + cxt.valueUnwrappedType() + "[] vals;");
        } else {
            g.lines(arrayCopiesMod + INSTANCE.tableType(cxt) + "[] tab;");
        }
        if (cxt.isIntegralKey()) {
            g.lines("final " + cxt.keyType() + " " + INSTANCE.free(cxt) + ";");
            if (INSTANCE.possibleRemovedSlots(cxt)) {
                g.lines("final " + cxt.keyType() + " " + INSTANCE.removed(cxt) + ";");
            }
        }
        if (needCapacityMask(cxt)) {
            g.lines("final int capacityMask;");
        }
        if (cxt.concurrentModificationChecked()) {
            g.lines("int expectedModCount;");
        }
        generateMutableEntryClassAwareOfPossibleCopyOnRemove(g, cxt);
    }

    static boolean possibleArrayCopyOnRemove(MethodContext cxt) {
        return cxt.mutable() && INSTANCE.isLHash(cxt);
    }

    static boolean needCapacityMask(MethodContext cxt) {
        return possibleArrayCopyOnRemove(cxt);
    }

    static void commonConstructorOps(MethodGenerator g, MethodContext cxt) {
        if (cxt.concurrentModificationChecked())
            g.lines("expectedModCount = mc;");
    }

    static void checkModCount(MethodGenerator g, MethodContext cxt, boolean copyModCount) {
        if (cxt.concurrentModificationChecked()) {
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
        if (cxt.concurrentModificationChecked()) {
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
        String keyAssignment = INSTANCE.readKeyOrEntry(cxt, indexVariableName);
        if (forceCopyKey || (!INSTANCE.noRemoved(cxt) && !cxt.isFloatingKey()) || !cxt.isValueView()) {
            gen.lines(INSTANCE.keyArrayType(cxt) + " key;");
            keyAssignment = "(key = " + keyAssignment + ")";
        }
        String cond = cxt.isFloatingKey() ?
                keyAssignment + " < FREE_BITS" :
                INSTANCE.isNotFree(cxt, keyAssignment) +
                        (INSTANCE.noRemoved(cxt) ? "" : (" && " + INSTANCE.isNotRemoved(cxt, "key")));
        gen.ifBlock(cond);
    }

    static String makeNext(MethodContext cxt, String index) {
        return makeNext(cxt, "mc", "key", index, true);
    }

    static String makeNext(MethodContext cxt, String modCount, String key, String index,
            boolean raw) {
        if (cxt.isKeyView()) {
            return makeKey(cxt, key, true, raw);
        } else if (cxt.isValueView()) {
            return makeValue(cxt, index);
        } else if (cxt.isEntryView()) {
            return entry(cxt, modCount, index, makeKey(cxt, key, false, raw),
                    INSTANCE.readValue(cxt, index));
        } else if (cxt.isMapView()) {
            return makeKey(cxt, key, true, raw) + ", " + makeValue(cxt, index);
        } else {
            throw new IllegalStateException();
        }
    }

    private static String makeKey(MethodContext cxt, String key, boolean wrap, boolean raw) {
        if (cxt.isObjectKey() && raw) {
            key = "(" + cxt.keyType() + ") " + key;
        }
        if (wrap) key = MethodGenerator.wrap(cxt, cxt.keyOption(), key);
        return key;
    }

    private static String makeValue(MethodContext cxt, String index) {
        return MethodGenerator.wrap(cxt, cxt.mapValueOption(), INSTANCE.readValue(cxt, index));
    }

    static String modCount() {
        return "modCount()";
    }

    static String entry(MethodContext cxt, String mc, String index, String key, String value) {
        if (!cxt.immutable()) {
            String mutableEntryClass = "MutableEntry";
            if (possibleArrayCopyOnRemove(cxt)) mutableEntryClass += "2";
            String entry = "new " + mutableEntryClass + "(";
            if (cxt.concurrentModificationChecked())
                entry += mc + ", ";
            return entry + index + ", " + key + ", " + value + ")";
        } else {
            return "new ImmutableEntry(" + key + ", " + value + ")";
        }
    }

    private static void generateMutableEntryClassAwareOfPossibleCopyOnRemove(
            MethodGenerator g, MethodContext cxt) {
        if (!possibleArrayCopyOnRemove(cxt) || !cxt.isEntryView())
            return;
        g.lines("");
        g.lines("class MutableEntry2 extends MutableEntry").block(); {
            g.lines(
                    "MutableEntry2(" +
                            (cxt.concurrentModificationChecked() ? "int modCount, " : "") +
                            "int index, " +
                            cxt.keyUnwrappedType() + " key, " + cxt.valueUnwrappedType() +
                            " value) {",
                    "    super(" + (cxt.concurrentModificationChecked() ? "modCount, " : "") +
                            "index, key, value);",
                    "}",
                    "",
                    "@Override"
            );
            g.lines("void updateValueInTable(" + cxt.valueUnwrappedType() + " newValue)").block(); {
                String haveNotCopiedTableYet =
                        INSTANCE.parallelKV(cxt) ? "tab == table" : "vals == values";
                g.ifBlock(haveNotCopiedTableYet); {
                    INSTANCE.writeValue(g, cxt, "index", "newValue");
                } g.elseBlock(); {
                    g.lines("justPut(key, newValue);");
                    if (cxt.concurrentModificationChecked()) {
                        // This check ensures that justPut (one line above) was an update, as
                        // expected, rather than insertion put. As far as this hash table
                        // implementation isn't fully thread-safe, this check is excessive, because
                        // there is one in setValue() prologue.
                        //
                        // (The explanation above is the result of investigation,
                        // because I didn't remember the purpose of the check. I would remove
                        // the check, if I was 100% sure that had correctly recalled my own intents
                        // when added this check.)
                        g.ifBlock("this.modCount != " + modCount()); {
                            g.illegalState();
                        } g.blockEnd();
                    }
                } g.blockEnd();
            } g.blockEnd();
        } g.blockEnd();
        g.lines("");
    }

    static void copySpecials(MethodGenerator g, MethodContext cxt) {
        if (cxt.isIntegralKey()) {
            g.lines(cxt.keyType() + " " + INSTANCE.free(cxt) + " = this." + INSTANCE.free(cxt) + ";");
        }
        copyRemoved(g, cxt);
    }

    private static void copyRemoved(MethodGenerator g, MethodContext cxt) {
        if (cxt.isIntegralKey() && INSTANCE.possibleRemovedSlots(cxt) && !INSTANCE.noRemoved(cxt)) {
            g.lines(cxt.keyType() + " " + INSTANCE.removed(cxt) + " = this." + INSTANCE.removed(cxt) + ";");
        }
    }

    static void copyArrays(MethodGenerator g, MethodContext cxt) {
        copyKeys(g, cxt);
        if (cxt.hasValues() && !INSTANCE.parallelKV(cxt))
            g.lines(cxt.valueUnwrappedType() + "[] vals = this.vals;");
    }

    static void copyKeys(MethodGenerator g, MethodContext cxt) {
        if (!INSTANCE.parallelKV(cxt)) {
            g.lines(INSTANCE.keyArrayType(cxt) + "[] keys = this.keys;");
        } else {
            g.lines(INSTANCE.tableType(cxt) + "[] tab = this.tab;");
        }
    }

    abstract static class LHashIterShiftRemove extends LHashShiftRemove {

        LHashIterShiftRemove(MethodGenerator g, MethodContext cxt) {
            super(g, cxt, "index", "tab", "vals");
        }

        @Override
        public void generate() {
            copyArrays(getG(), getCxt());
            // If we haven't copied the table yet, i. e. are going to search for
            // the next keys/values in the original table
            String haveNotCopiedTableYet = INSTANCE.parallelKV(getCxt()) ? "tab == table" : "keys == set";
            getG().ifBlock(haveNotCopiedTableYet); {
                copyRemoved(getG(), getCxt());
                getG().lines("int capacityMask = this.capacityMask;");
                super.generate();
            }
            // If keys != set, i. e. the arrays already copied
            getG().elseBlock(); {
                // Remove from the original table. Local copy of curKey is used
                // (see the comment in generateRemove()).
                String keyToRemove = keyToRemoveFromTheOriginalTable();
                if (!INSTANCE.specializedKeysArray(getCxt()))
                    keyToRemove = "(" + getCxt().keyType() + ") " + keyToRemove;
                getG().lines("justRemove(" + keyToRemove + ");");
                // These removals in the table copy only for GC.
                // keys[index] won't be accessed anymore (moveNext() will start from index - 1),
                // that is why we can set it to null instead of REMOVED special object.
                if (getCxt().isObjectKey())
                    INSTANCE.writeKey(getG(), getCxt(), "index", "null");
                if (getCxt().isObjectValue())
                    INSTANCE.writeValue(getG(), getCxt(), "index", "null");
            } getG().blockEnd();
        }

        @Override
        public void beforeShift() {
            // If we haven't copied the table yet, i. e. are going to search for
            // the next keys/values in the original table
            String haveNotCopiedTableYet =
                    INSTANCE.parallelKV(getCxt()) ? "this.tab == tab" : "this.keys == keys";
            getG().ifBlock(haveNotCopiedTableYet); {
                // This condition means indexToShift wrapped around zero and keyToShift
                // was already passed by this cursor. Making a copy of the original
                // table for future moveNext() calls which wouldn't contain this
                // entry.
                // Note that local copies of keys and vals arrays are not changed, shift
                // deletion continues in the original table.
                getG().ifBlock("indexToShift > indexToRemove"); {
                    getG().lines("int slotsToCopy;");
                    getG().ifBlock("(slotsToCopy = " + slotsToCopy() + ") > 0"); {
                        if (!INSTANCE.parallelKV(getCxt())) {
                            getG().lines("this.keys = Arrays.copyOf(keys, slotsToCopy);");
                            if (getCxt().hasValues()) {
                                getG().lines("this.vals = Arrays.copyOf(vals, slotsToCopy);");
                            }
                        } else {
                            getG().lines("this.tab = Arrays.copyOf(tab, slotsToCopy);");
                        }
                        getG().ifBlock("indexToRemove < slotsToCopy"); {
                            // In normal path slots substitute one another and only the last one
                            // is erased. But we should erase in the table copy the current slot,
                            // that we are going to substitute with a shifted slot (or erase),
                            // because shift deletion continues in the original table.
                            INSTANCE.writeKey(getG(), getCxt(), "this.tab", "this.keys", "indexToRemove", INSTANCE
                                    .removed(getCxt()));
                            if (getCxt().isObjectValue()) {
                                INSTANCE.writeValue(getG(), getCxt(), "this.tab", "this.vals", "indexToRemove",
                                        "null");
                            }
                        } getG().blockEnd();
                    } getG().blockEnd();
                }
                getG().elseIf("indexToRemove == index"); {
                    onInitialSlotSubstitution();
                } getG().blockEnd();
            } getG().blockEnd();
        }

        abstract String slotsToCopy();

        abstract void onInitialSlotSubstitution();

        abstract String keyToRemoveFromTheOriginalTable();
    }
}
