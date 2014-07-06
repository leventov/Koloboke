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


final class HashIterMethodGeneratorCommons {

    private HashIterMethodGeneratorCommons() {}

    static void commonFields(MethodGenerator g, MethodContext cxt) {
        String arrayCopiesMod = possibleArrayCopyOnRemove(cxt) ? "" : "final ";
        if (!parallelKV(cxt)) {
            g.lines(arrayCopiesMod + cxt.keyUnwrappedType() + "[] keys;");
            if (!cxt.isKeyView())
                g.lines(arrayCopiesMod + cxt.valueUnwrappedType() + "[] vals;");
        } else {
            g.lines(arrayCopiesMod + tableType(cxt) + "[] tab;");
        }
        if (cxt.isIntegralKey()) {
            g.lines("final " + cxt.keyType() + " " + free(cxt) + ";");
            if (possibleRemovedSlots(cxt)) {
                g.lines("final " + cxt.keyType() + " " + removed(cxt) + ";");
            }
        }
        if (needCapacityMask(cxt)) {
            g.lines("final int capacityMask;");
        }
        if (!cxt.immutable()) {
            g.lines("int expectedModCount;");
        }
        generateMutableEntryClassAwareOfPossibleCopyOnRemove(g, cxt);
    }

    static boolean possibleArrayCopyOnRemove(MethodContext cxt) {
        return cxt.mutable() && isLHash(cxt);
    }

    static boolean needCapacityMask(MethodContext cxt) {
        return possibleArrayCopyOnRemove(cxt);
    }

    static void commonConstructorOps(MethodGenerator g, MethodContext cxt) {
        if (!cxt.immutable()) {
            g.lines("expectedModCount = mc;");
        }
    }

    static void checkModCount(MethodGenerator g, MethodContext cxt, boolean copyModCount) {
        if (!cxt.immutable()) {
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
        if (!cxt.immutable()) {
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
        String keyAssignment = readKeyOrEntry(cxt, indexVariableName);
        if (forceCopyKey || (!noRemoved(cxt) && !cxt.isFloatingKey()) || !cxt.isValueView()) {
            gen.lines(cxt.keyUnwrappedRawType() + " key;");
            keyAssignment = "(key = " + keyAssignment + ")";
        }
        String cond = cxt.isFloatingKey() ?
                keyAssignment + " < FREE_BITS" :
                isNotFree(cxt, keyAssignment) +
                        (noRemoved(cxt) ? "" : (" && " + isNotRemoved(cxt, "key")));
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
                    readValue(cxt, index));
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
        return MethodGenerator.wrap(cxt, cxt.mapValueOption(), readValue(cxt, index));
    }

    static String modCount() {
        return "modCount()";
    }

    static String entry(MethodContext cxt, String mc, String index, String key, String value) {
        if (!cxt.immutable()) {
            String mutableEntryClass = "MutableEntry";
            if (possibleArrayCopyOnRemove(cxt)) mutableEntryClass += "2";
            return "new " + mutableEntryClass + "(" +
                    mc + ", " + index + ", " + key + ", " + value + ")";
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
                    "MutableEntry2(int modCount, int index, " +
                            cxt.keyUnwrappedType() + " key, " + cxt.valueUnwrappedType() +
                            " value) {",
                    "    super(modCount, index, key, value);",
                    "}",
                    "",
                    "@Override"
            );
            g.lines("void updateValueInTable(" + cxt.valueUnwrappedType() + " newValue)").block(); {
                String haveNotCopiedTableYet =
                        parallelKV(cxt) ? "tab == table" : "vals == values";
                g.ifBlock(haveNotCopiedTableYet); {
                    writeValue(g, cxt, "index", "newValue");
                } g.elseBlock(); {
                    g.lines("justPut(key, newValue);");
                    g.ifBlock("this.modCount != " + modCount()); {
                        g.illegalState();
                    } g.blockEnd();
                } g.blockEnd();
            } g.blockEnd();
        } g.blockEnd();
        g.lines("");
    }

    static void copySpecials(MethodGenerator g, MethodContext cxt) {
        if (cxt.isIntegralKey()) {
            g.lines(cxt.keyType() + " " + free(cxt) + " = this." + free(cxt) + ";");
        }
        copyRemoved(g, cxt);
    }

    private static void copyRemoved(MethodGenerator g, MethodContext cxt) {
        if (cxt.isIntegralKey() && possibleRemovedSlots(cxt) && !noRemoved(cxt)) {
            g.lines(cxt.keyType() + " " + removed(cxt) + " = this." + removed(cxt) + ";");
        }
    }

    static void copyArrays(MethodGenerator g, MethodContext cxt) {
        copyKeys(g, cxt);
        if (cxt.hasValues() && !parallelKV(cxt))
            g.lines(cxt.valueUnwrappedType() + "[] vals = this.vals;");
    }

    static void copyKeys(MethodGenerator g, MethodContext cxt) {
        if (!parallelKV(cxt)) {
            g.lines(cxt.keyUnwrappedType() + "[] keys = this.keys;");
        } else {
            g.lines(tableType(cxt) + "[] tab = this.tab;");
        }
    }

    abstract static class LHashIterShiftRemove extends LHashShiftRemove {

        LHashIterShiftRemove(MethodGenerator g, MethodContext cxt) {
            super(g, cxt, "index", "tab", "vals");
        }

        @Override
        void generate() {
            copyArrays(g, cxt);
            // If we haven't copied the table yet, i. e. are going to search for
            // the next keys/values in the original table
            String haveNotCopiedTableYet = parallelKV(cxt) ? "tab == table" : "keys == set";
            g.ifBlock(haveNotCopiedTableYet); {
                copyRemoved(g, cxt);
                g.lines("int capacityMask = this.capacityMask;");
                super.generate();
            }
            // If keys != set, i. e. the arrays already copied
            g.elseBlock(); {
                // Remove from the original table. Local copy of curKey is used
                // (see the comment in generateRemove()).
                g.lines("justRemove(" + keyToRemoveFromTheOriginalTable() + ");");
                // These removals in the table copy only for GC.
                // keys[index] won't be accessed anymore (moveNext() will start from index - 1),
                // that is why we can set it to null instead of REMOVED special object.
                if (cxt.isObjectKey())
                    writeKey(g, cxt, "index", "null");
                if (cxt.isObjectValue())
                    writeValue(g, cxt, "index", "null");
            } g.blockEnd();
        }

        @Override
        void beforeShift() {
            // If we haven't copied the table yet, i. e. are going to search for
            // the next keys/values in the original table
            String haveNotCopiedTableYet =
                    parallelKV(cxt) ? "this.tab == tab" : "this.keys == keys";
            g.ifBlock(haveNotCopiedTableYet); {
                // This condition means indexToShift wrapped around zero and keyToShift
                // was already passed by this cursor. Making a copy of the original
                // table for future moveNext() calls which wouldn't contain this
                // entry.
                // Note that local copies of keys and vals arrays are not changed, shift
                // deletion continues in the original table.
                g.ifBlock("indexToShift > indexToRemove"); {
                    g.lines("int slotsToCopy;");
                    g.ifBlock("(slotsToCopy = " + slotsToCopy() + ") > 0"); {
                        g.ifBlock("indexToRemove < slotsToCopy"); {
                            // In normal path slots substitute one another and only the last one
                            // is erased. But before copying the table we should erase the slot too.
                            eraseSlot(g, cxt, "indexToRemove", "indexToRemove");
                        } g.blockEnd();
                        if (!parallelKV(cxt)) {
                            g.lines("this.keys = Arrays.copyOf(keys, slotsToCopy);");
                            if (cxt.hasValues()) {
                                g.lines("this.vals = Arrays.copyOf(vals, slotsToCopy);");
                            }
                        } else {
                            g.lines("this.tab = Arrays.copyOf(tab, slotsToCopy);");
                        }
                    } g.blockEnd();
                }
                g.elseIf("indexToRemove == index"); {
                    onInitialSlotSubstitution();
                } g.blockEnd();
            } g.blockEnd();
        }

        abstract String slotsToCopy();

        abstract void onInitialSlotSubstitution();

        abstract String keyToRemoveFromTheOriginalTable();
    }
}
