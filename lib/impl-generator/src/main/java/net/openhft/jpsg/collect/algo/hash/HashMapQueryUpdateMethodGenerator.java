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
import net.openhft.jpsg.collect.*;
import net.openhft.jpsg.collect.mapqu.MapQueryUpdateMethod;
import net.openhft.jpsg.collect.mapqu.MapQueryUpdateMethodGenerator;

import static java.lang.Math.min;
import static java.lang.StrictMath.max;
import static net.openhft.jpsg.collect.Permission.REMOVE;
import static net.openhft.jpsg.collect.Permission.SET_VALUE;
import static net.openhft.jpsg.collect.algo.hash.HashMethodGeneratorCommons.*;
import static net.openhft.jpsg.collect.algo.hash.KeySearch.curAssignment;
import static net.openhft.jpsg.collect.algo.hash.KeySearch.innerLoopBodies;
import static net.openhft.jpsg.collect.algo.hash.KeySearch.tryPrecomputeStep;
import static net.openhft.jpsg.collect.mapqu.BasicMapQueryUpdateOp.*;
import static net.openhft.jpsg.collect.mapqu.Branch.KEY_ABSENT;
import static net.openhft.jpsg.collect.mapqu.Branch.KEY_PRESENT;


public final class HashMapQueryUpdateMethodGenerator extends MapQueryUpdateMethodGenerator {

    private static final String TABLE_SUB = "#tab#";
    private static final String KEY_SUB = "#key#";
    private static final String VAL_SUB = "#val#";
    private static final String VALUES_SUB = "#vals#";

    private MapQueryUpdateMethod method;
    private int beforeBranches;
    private int firstPresentLine;
    private int firstAbsentLine;

    // inline index/insert state

    private int presentBranchSize, absentBranchSize;
    private boolean separatePresent, separateAbsentFreeSlot, separateAbsentRemovedSlot;
    // for generating key absent branch
    private boolean removedSlot;
    private boolean earlyAbsentLabel;

    private boolean presentValueUsed = false;
    // if values copy should be lifted from branches
    private boolean commonValuesCopy = false;
    private boolean commonTableCopy = false;

    private boolean commonCapacityMaskCopy = false;

    private String index = "index";

    private boolean inline() {
        return method.inline() ||
                (parallelKV(cxt) && !doubleSizedParallel(cxt) && presentValueUsed);
    }

    @Override
    public String defaultValue() {
        if (cxt.isObjectValue() || cxt.genericVersion()) return "null";
        return "defaultValue()";
    }

    @Override
    public String valueEquals(String valueToCompare) {
        if (cxt.isObjectValue()) {
            return "nullableValueEquals(" + value() + ", (V) " + valueToCompare + ")";
        } else {
            return unwrappedValue() + " == " + unwrapValue(valueToCompare);
        }
    }

    @Override
    public void insert(String value) {
        permissions.add(Permission.INSERT);
        if (inline()) {
            incrementModCount();
            writeKeyAndValue(this, cxt, table(), "keys", values(), index(), unwrappedKey(),
                    () -> unwrapValue(value), true, cxt.isMapView());
            if (removedSlot) {
                lines("postRemovedSlotInsertHook();");
            } else {
                lines(possibleRemovedSlots(cxt) ? "postFreeSlotInsertHook();" :
                        "postInsertHook();");
            }
        } else {
            lines("insertAt(insertionIndex, " + unwrappedKey() + ", " + value + ");");
        }
    }

    @Override
    protected void generateLines(Method m) {
        method = (MapQueryUpdateMethod) m;
        determineBranchFeatures();
        if (inline()) {
            generateInline();
        } else {
            generateNotInlined();
        }
        replaceKey();
    }

    private void generateNotInlined() {
        if (method.baseOp() == INSERT)
            permissions.add(Permission.INSERT);
        method.beginning();
        getIndex();
        if (commonValuesCopy)
            copyValues();
        if (commonTableCopy)
            copyTable(this, cxt);
        beforeBranches = lines.size();
        if (absentBranchSize == 0 && presentBranchSize == 0)
            throw new IllegalStateException();
        if (absentBranchSize == 0) {
            ifBlock(indexPresent());
            generatePresent();
            blockEnd();
        } else if (presentBranchSize == 0) {
            ifBlock(indexAbsent());
            generateAbsent(false);
            blockEnd();
        } else {
            if (method.mostProbableBranch() == KEY_PRESENT) {
                ifBlock(indexPresent());
                generatePresent();
                elseBlock();
                generateAbsent(false);
            } else {
                ifBlock(indexAbsent());
                generateAbsent(false);
                elseBlock();
                generatePresent();
            }
            blockEnd();
        }
        liftValueCopies();
    }

    private void liftValueCopies() {
        if (lines.get(firstAbsentLine).equals(lines.get(firstPresentLine))) {
            // copying values to local array in both branches
            int first = min(firstAbsentLine, firstPresentLine);
            int last = max(firstPresentLine, firstAbsentLine);
            String valuesCopyLine = lines.remove(last).substring(4);
            lines.remove(first);
            lines.add(beforeBranches, valuesCopyLine);
            beforeBranches++;
            last--;
            if (lines.get(first).equals(lines.get(last))) {
                // copying current value to local
                String valCopyLine = lines.remove(last).substring(4);
                lines.remove(first);
                lines.add(beforeBranches, valCopyLine);
                beforeBranches++;
                last--;
                if (method.baseOp() == CUSTOM_INSERT &&
                        lines.get(first).equals(lines.get(last))) {
                    // int index = insertionIndex.get() was the first equal line
                    valCopyLine = lines.remove(last).substring(4);
                    lines.remove(first);
                    lines.add(beforeBranches, valCopyLine);
                }
            }
        }
    }

    private void replaceKey() {
        if (cxt.isFloatingKey() && !cxt.internalVersion()) {
            replaceAll(0, KEY_SUB, "k");
            String key = "key";
            if (cxt.genericVersion() &&
                    !permissions.contains(Permission.INSERT) &&
                    !permissions.contains(Permission.SET_VALUE)) {
                // key is Object, need to cast before unwrapping
                key = "(" + ((PrimitiveType) cxt.keyOption()).className + ") " + key;
            }
            String keyUnwrap = indent + cxt.keyUnwrappedType() + " k = " + unwrapKey(key) + ";";
            lines.add(0, keyUnwrap);
        }
        else if (cxt.isPrimitiveKey() && cxt.genericVersion()) {
            PrimitiveType keyType = (PrimitiveType) cxt.keyOption();
            int keyUsages = 0;
            for (String line : lines) {
                keyUsages += countOccurrences(line, KEY_SUB);
            }
            if (permissions.contains(Permission.INSERT) || permissions.contains(SET_VALUE)) {
                // insert or setValue => key couldn't be Object, key is Character/Integer/...
                // if more than 1 usage, unbox
                if (keyUsages > 1) {
                    replaceAll(0, KEY_SUB, "k");
                    String keyCopy = indent + cxt.keyType() + " k = key;";
                    lines.add(0, keyCopy);
                }
            } else if (!inline()) {
                // key is Object
                // the only usage - in index() call
                if (keyUsages != 1)
                    throw new IllegalStateException();
                // for this usage, cast the key to Character/Integer/...
                String key = "(" + keyType.className + ") key";
                replaceAll(0, KEY_SUB, key);
            } else {
                // inlined remove() op
                // cast & unbox
                replaceAll(0, KEY_SUB, "k");
                String keyCopy = indent + cxt.keyType() + " k = (" + keyType.className + ") key;";
                lines.add(0, keyCopy);
            }
        }
        String key;
        if (cxt.isNullKey()) {
            key = "null";
        } else if (cxt.isObjectKey() && inline() && method.baseOp() == GET) {
            key = "k";
        } else {
            key = "key";
        }
        replaceAll(0, KEY_SUB, key);
    }

    private void generatePresent() {
        generatePresent(true);
    }

    private void generatePresent(boolean replaceValues) {
        lines("// key is present");
        int branchStart = firstPresentLine = lines.size();
        method.ifPresent();
        if (replaceValues)
            replaceValues(branchStart);
    }

    private void generateAbsent(boolean removedSlot) {
        generateAbsent(removedSlot, true, true);
    }

    private void generateAbsent(boolean removedSlot, boolean replaceValues,
            boolean slotTypeComment) {
        String time = method.baseOp() == INSERT && !inline() ? "was" : "is";
        String comment = "// key " + time + " absent";
        if (slotTypeComment && cxt.mutable() && inline() && !isLHash(cxt)) {
            comment += removedSlot ? ", removed slot" : ", free slot";
        }
        lines(comment);
        int branchStart = firstAbsentLine = lines.size();
        this.removedSlot = removedSlot;
        if (inline() && !method.inline() && method.baseOp() == INSERT) {
            assert cxt.isMapView();
            insert("value");
        }
        method.ifAbsent();
        if (replaceValues)
            replaceValues(branchStart);
        this.removedSlot = false;
    }

    private void getIndex() {
        if (method.baseOp() == GET) {
            lines("int index = index(" + unwrappedKey() + ");");
        } else if (method.baseOp() == INSERT) {
            String insertArgs = cxt.isMapView() ?
                    unwrappedKey() + ", " + unwrapValue("value") :
                    unwrappedKey();
            lines("int index = insert(" + insertArgs + ");");
        } else if (method.baseOp() == CUSTOM_INSERT) {
            lines("InsertionIndex insertionIndex = insertionIndex(" + unwrappedKey() + ");");
        } else {
            throw new IllegalStateException();
        }
    }

    private String indexPresent() {
        if (method.baseOp() == GET || method.baseOp() == INSERT) {
            return "index >= 0";
        } else if (method.baseOp() == CUSTOM_INSERT) {
            return "insertionIndex.existing()";
        } else {
            throw new IllegalStateException();
        }
    }

    private String indexAbsent() {
        if (method.baseOp() == GET || method.baseOp() == INSERT) {
            return "index < 0";
        } else if (method.baseOp() == CUSTOM_INSERT) {
            return "insertionIndex.absent()";
        } else {
            throw new IllegalStateException();
        }
    }

    private void replaceValues(int branchStart) {
        int valUsages = countUsages(branchStart, VAL_SUB);
        if (valUsages > 0) {
            String val = readValue(cxt, table(), values(), index());
            if (valUsages >= 2) {
                replaceFirstDifferent(branchStart, VAL_SUB, "(val = " + val + ")", "val");
                lines.add(branchStart, indent + cxt.valueUnwrappedType() + " val;");
            } else {
                replaceAll(branchStart, VAL_SUB, val);
            }
        }
        if (commonValuesCopy) {
            replaceAll(branchStart, VALUES_SUB, "vals");
        } else {
            int valArrayUsages = countUsages(branchStart, VALUES_SUB);
            if (valArrayUsages >= 2) {
                replaceAll(branchStart, VALUES_SUB, "vals");
                lines.add(branchStart, indent + cxt.valueUnwrappedType() + "[] vals = values;");
            } else {
                replaceAll(branchStart, VALUES_SUB, "values");
            }
        }
        if (inline() || commonTableCopy) {
            replaceAll(branchStart, TABLE_SUB, "tab");
        } else {
            int tableUsages = countUsages(branchStart, TABLE_SUB);
            if (tableUsages >= 2) {
                replaceAll(branchStart, TABLE_SUB, "tab");
                lines.add(branchStart, indent + tableType(cxt) + "[] tab = table;");
            } else {
                replaceAll(branchStart, TABLE_SUB, "table");
            }
        }
        if (method.baseOp() == CUSTOM_INSERT && !inline()) {
            for (int i = branchStart; i < lines.size(); i++) {
                String line = lines.get(i);
                if (countOccurrences(line, "index") > 0) {
                    lines.add(branchStart, indent + "int index = insertionIndex.get();");
                    break;
                }
            }
        }
    }

    String removedValue() {
        return inline() || !cxt.isIntegralKey() ? removed(cxt) : "removedValue";
    }

    String index() {
        return removedSlot ? "firstRemoved" : index;
    }

    @Override
    public String key() {
        return wrapKey(unwrappedKey());
    }

    private String unwrappedKey() {
        return KEY_SUB;
    }

    @Override
    public String value() {
        return wrapValue(unwrappedValue());
    }

    private String unwrappedValue() {
        return VAL_SUB;
    }

    private String table() {
        return TABLE_SUB;
    }

    private String values() {
        return VALUES_SUB;
    }

    @Override
    public MethodGenerator remove() {
        permissions.add(REMOVE);
        if (isLHash(cxt) && !method.removeIsHighlyProbable()) {
            lines("removeAt(" + index() + ");");
        } else {
            if (isLHash(cxt)) {
                if (!inline() || !commonCapacityMaskCopy) {
                    lines("int capacityMask = " + capacityMask(cxt) + ";");
                }
                if (inline()) {
                    commonCapacityMaskCopy = true;
                }
                new LHashShiftRemove(this, cxt, index(), TABLE_SUB, VALUES_SUB).generate();
            } else {
                incrementModCount();
                String keys, table;
                if (inline()) {
                    keys = "keys";
                    if (cxt.isObjectOrNullKey())
                        keys = "((Object[]) " + keys + ")";
                    table = "tab";
                } else {
                    keys = "set";
                    table = "table";
                }
                writeKey(this, cxt, table, keys, index(), removedValue());
                if (cxt.isObjectValue())
                    writeValue(this, cxt, table(), values(), index(), "null");
                lines("postRemoveHook();");
            }
        }
        return this;
    }

    @Override
    public MethodGenerator setValue(String newValue) {
        writeValue(this, cxt, table(), values(), index(), unwrapValue(newValue));
        permissions.add(SET_VALUE);
        return this;
    }


    private void generateInline() {
        inlineBeginning();
        inlineLocals();
        String curAssignment = curAssignment(cxt, unwrappedKey(), commonCapacityMaskCopy);
        if (method.mostProbableBranch() == KEY_ABSENT) {
            firstIndexFreeCheck(curAssignment);
            if (separatePresent) {
                lines("keyPresent:");
                String keyNotEqualsCond = "cur != " + unwrappedKey();
                if (cxt.isObjectKey() && !cxt.mutable()) {
                    keyNotEqualsCond += " && !keyEquals(" + unwrappedKey() +", cur)";
                }
                ifBlock(keyNotEqualsCond);
            } else {
                String keyEqualsCond = "cur == " + unwrappedKey();
                if (cxt.isObjectKey() && !cxt.mutable()) {
                    keyEqualsCond += " || keyEquals(" + unwrappedKey() + ", cur)";
                }
                ifBlock(keyEqualsCond);
                generatePresent();
                elseBlock();
            }
            innerInline();
            blockEnd();
            if (separatePresent)
                generatePresent();
            blockEnd();
            if (separateAbsentFreeSlot && !earlyAbsentLabel)
                generateAbsent(false);
        } else {
            // most probable branch - key is present
            if (separatePresent) {
                lines("keyPresent:");
                ifBlock(curAssignment + " != " + unwrappedKey());
            } else {
                ifBlock(curAssignment + " == " + unwrappedKey());
                generatePresent();
                elseBlock();
            }
            firstIndexFreeCheck("cur");
            if (cxt.isObjectKey() && !cxt.mutable()) {
                ifBlock("keyEquals(" + unwrappedKey() + ", cur)");
                generateOrGoToPresent(() -> {});
                elseBlock();
            }
            innerInline();
            if (cxt.isObjectKey() && !cxt.mutable())
                blockEnd();
            blockEnd();
            if (separateAbsentFreeSlot && !earlyAbsentLabel)
                generateAbsent(false);
            blockEnd();
            if (separatePresent)
                generatePresent();
        }
        inlineEnd();
    }

    private void innerInline() {
        if (possibleRemovedSlots(cxt) && (cxt.isObjectKey() || method.baseOp() != GET)) {
            if (method.baseOp() != GET)
                lines("int firstRemoved;");
            boolean stepPrecomputed = !cxt.isObjectKey() && tryPrecomputeStep(this, cxt);
            ifBlock(isNotRemoved(cxt, "cur"));
            if (cxt.isObjectKey()) {
                if (method.mostProbableBranch() == KEY_PRESENT) {
                    ifBlock("keyEquals(" + unwrappedKey() + ", cur)");
                    generateOrGoToPresent(() -> {});
                    elseBlock();
                } else {
                    ifBlock("!keyEquals(" + unwrappedKey() + ", cur)");
                }
            }
            ifBlock("noRemoved()");
            keySearchLoop(true, stepPrecomputed);
            if (method.baseOp() != GET) {
                elseBlock();
                lines("firstRemoved = -1;");
            }
            blockEnd();
            if (cxt.isObjectKey()) {
                if (method.mostProbableBranch() != KEY_PRESENT) {
                    elseBlock();
                    generateOrGoToPresent(() -> {});
                }
                blockEnd();
            }
            if (method.baseOp() != GET) {
                elseBlock();
                lines("firstRemoved = index;");
            }
            blockEnd();
            if (method.baseOp() == GET) {
                keySearchLoop(false, stepPrecomputed);
            } else {
                keySearchLoopDifferentRemovedHandling(stepPrecomputed);
            }
        } else {
            if (isLHash(cxt) && cxt.isNullKey() && !commonCapacityMaskCopy)
                lines("capacityMask = " + capacityMask(cxt) + ";");
            keySearchLoop(true, false);
        }
    }

    private void inlineLocals() {
        copyArrays(this, cxt, commonValuesCopy);
        String locals;
        if (isLHash(cxt)) {
            if (commonCapacityMaskCopy) {
                lines("int capacityMask = " + capacityMask(cxt) + ";");
                locals = "";
            } else {
                locals = "capacityMask, ";
            }
        } else if (isQHash(cxt)) {
            if (cxt.isNullKey()) {
                lines("int capacity = " + localTableVar(cxt) + ".length;");
                locals = "";
            } else {
                locals = "capacity, ";
            }
        } else {
            assertHash(cxt, isDHash(cxt));
            locals = (!cxt.isNullKey() ? "capacity, hash, " : "");
        }
        lines("int " + locals + "index;");
        lines(cxt.keyUnwrappedType() + " cur;");
        if (parallelKV(cxt) && !doubleSizedParallel(cxt))
            lines(tableEntryType(cxt) + " entry;");
    }

    private void copyValues() {
        lines(cxt.valueUnwrappedType() + "[] vals = values;");
    }

    private void inlineBeginning() {
        if (cxt.isObjectKey()) {
            ifBlock("key != null");
            if (method.baseOp() == GET) {
                lines(
                        "// noinspection unchecked",
                        cxt.keyType() + " " + unwrappedKey() + " = (" + cxt.keyType() + ") key;"
                );
            }
        }
        method.beginning();
        earlyAbsentLabel = false;
        if (cxt.isIntegralKey()) {
            if (method.baseOp() == GET) {
                boolean isRemoveOp = permissions.contains(Permission.REMOVE);
                lines(cxt.keyType() + " free" +
                        (isRemoveOp && possibleRemovedSlots(cxt) ? ", removed" : "") + ";");
                if (separateAbsentFreeSlot) {
                    lines("keyAbsent:");
                    earlyAbsentLabel = true;
                }

                String removed = isRemoveOp ? "(removed = removedValue)" : "removedValue";
                ifBlock(unwrappedKey() + " != (free = freeValue)" +
                        (possibleRemovedSlots(cxt) ? " && " + unwrappedKey() + " != " + removed :
                                ""));
            } else {
                lines(cxt.keyType() + " free;");
                if (possibleRemovedSlots(cxt))
                    lines(cxt.keyType() + " removed = removedValue;");
                ifBlock(unwrappedKey() + " == (free = freeValue)");
                lines("free = changeFree();");
                if (possibleRemovedSlots(cxt)) {
                    elseIf(unwrappedKey() + " == removed");
                    lines("removed = changeRemoved();");
                }
                blockEnd();
            }
        }
    }

    private void inlineEnd() {
        if (method.baseOp() == GET) {
            if (earlyAbsentLabel) {
                blockEnd();
                generateAbsent(false);
            } else if (cxt.isIntegralKey()) {
                elseBlock();
                generateAbsent(false, true, false);
                blockEnd();
            }
        }
        if (cxt.isObjectKey()) {
            elseBlock();
            lines("return " + method.name() + "NullKey(" + method.nullArgs() + ");");
            blockEnd();
        }
    }

    private void firstIndexFreeCheck(String cur) {
        if (separateAbsentFreeSlot) {
            if (!earlyAbsentLabel) {
                lines(absentLabel(false) + ":");
            }
            ifBlock(isNotFree(cxt, cur));
        } else {
            ifBlock(isFree(cxt, cur));
            generateAbsent(false);
            elseBlock();
        }
    }

    private void keySearchLoop(boolean noRemoved, boolean stepPrecomputed) {
        KeySearch.innerLoop(this, cxt, index -> {
            String prevIndex = this.index;
            this.index = index;
            Runnable beforeBreak = () -> {
                if (!index.equals(prevIndex))
                    lines(prevIndex + " = " + index + ";");
            };
            String key = readKeyOrEntry(cxt, index);
            if (method.mostProbableBranch() == KEY_PRESENT) {
                ifBlock("(cur = " + key + ") == " + unwrappedKey());
                generateOrGoToPresent(beforeBreak);
                elseIf(isFree(cxt, "cur"));
                generateOrGoToAbsent(false, beforeBreak);
                blockEnd();
                if (cxt.isObjectKey()) {
                    lines("else if (" + objectKeyEqualsCond(noRemoved) + ")").block();
                    generateOrGoToPresent(beforeBreak);
                    blockEnd();
                }
            } else {
                ifBlock(isFree(cxt, "(cur = " + key + ")"));
                generateOrGoToAbsent(false, beforeBreak);
                String presentCond = "cur == " + unwrappedKey();
                if (cxt.isObjectKey()) {
                    presentCond += " || (" + objectKeyEqualsCond(noRemoved) + ")";
                }
                elseIf(presentCond);
                generateOrGoToPresent(beforeBreak);
                blockEnd();
            }
            this.index = prevIndex;
        }, stepPrecomputed).generate();
    }


    private void keySearchLoopDifferentRemovedHandling(boolean stepPrecomputed) {
        if (separateAbsentRemovedSlot)
            lines(absentLabel(true) + ":").block();
        KeySearch.innerLoop(this, cxt, index -> {
            String prevIndex = this.index;
            this.index = index;
            Runnable beforeBreak = () -> {
                if (!index.equals(prevIndex))
                    lines(prevIndex + " = " + index + ";");
            };
            String key = readKeyOrEntry(cxt, index);
            if (method.mostProbableBranch() == KEY_PRESENT) {
                ifBlock("(cur = " + key + ") == " + unwrappedKey());
                generateOrGoToPresent(beforeBreak);
                elseIf(isFree(cxt, "cur"));
                generateAbsentDependingOnFirstRemoved(beforeBreak);
            } else {
                ifBlock(isFree(cxt, "(cur = " + key + ")"));
                generateAbsentDependingOnFirstRemoved(beforeBreak);
                elseIf("cur == " + unwrappedKey());
                generateOrGoToPresent(beforeBreak);
            }
            if (cxt.isObjectKey()) {
                elseIf(isNotRemoved(cxt, "cur"));
                ifBlock("keyEquals(" + unwrappedKey() + ", cur)");
                generateOrGoToPresent(beforeBreak);
                blockEnd();
                elseIf("firstRemoved < 0");
            } else {
                elseIf(isRemoved(cxt, "cur") + " && firstRemoved < 0");
            }
            lines("firstRemoved = " + index + ";");
            blockEnd();
            this.index = prevIndex;
        }, stepPrecomputed).generate();
        if (separateAbsentRemovedSlot) {
            blockEnd();
            generateAbsent(true);
        }
    }

    private void generateAbsentDependingOnFirstRemoved(Runnable beforeBreak) {
        ifBlock("firstRemoved < 0"); {
            generateOrGoToAbsent(false, beforeBreak);
        } elseBlock(); {
            generateOrGoToAbsent(true, beforeBreak);
        } blockEnd();
    }

    private String objectKeyEqualsCond(boolean noRemoved) {
        return (possibleRemovedSlots(cxt) && !noRemoved ? "cur != REMOVED && " : "") +
                "keyEquals(" + unwrappedKey() + ", cur)";
    }

    private void determineBranchFeatures() {
        generateAbsent(false, false, false);
        // first line is comment
        absentBranchSize = lines.size() - 1;
        separateAbsentFreeSlot = absentBranchSize > 1;
        int absentBranchValuesUsages = countUsages(0, VALUES_SUB) + countUsages(0, VAL_SUB);
        int absentBranchTableUsages = countUsages(0, TABLE_SUB);
        lines.clear();

        separateAbsentRemovedSlot = separateAbsentFreeSlot && innerLoopBodies(cxt) > 1;

        generatePresent(false);
        // first line is comment
        presentBranchSize = lines.size() - 1;
        separatePresent = presentBranchSize > 1;
        int presentValUsages = countUsages(0, VAL_SUB);
        int presentBranchValuesUsages = countUsages(0, VALUES_SUB) + presentValUsages;
        int presentBranchTableUsages = countUsages(0, TABLE_SUB);
        lines.clear();

        presentValueUsed = presentValUsages > 0;
        commonValuesCopy = absentBranchValuesUsages > 0 && presentBranchValuesUsages > 0;
        commonTableCopy = absentBranchTableUsages > 0 && presentBranchTableUsages > 0;
    }

    private void generateOrGoToPresent(Runnable beforeBreak) {
        if (separatePresent) {
            beforeBreak.run();
            lines("break keyPresent;");
        } else {
            generatePresent();
        }
    }

    private void generateOrGoToAbsent(boolean removedSlot, Runnable beforeBreak) {
        if (separateAbsentRemovedSlot && removedSlot)  {
            lines("break " + absentLabel(true) + ";");
        } else if (separateAbsentFreeSlot && !removedSlot) {
            beforeBreak.run();
            lines("break " + absentLabel(false) + ";");
        } else {
            generateAbsent(removedSlot);
        }
    }

    private String absentLabel(boolean removedSlot) {
        if (method.baseOp() != GET && possibleRemovedSlots(cxt)) {
            return "keyAbsent" + (removedSlot ? "RemovedSlot" : "FreeSlot");
        } else {
            return "keyAbsent";
        }
    }
}
