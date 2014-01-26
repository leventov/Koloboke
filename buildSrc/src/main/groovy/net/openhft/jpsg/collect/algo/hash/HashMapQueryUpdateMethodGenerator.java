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
import net.openhft.jpsg.collect.mapqu.*;

import static net.openhft.jpsg.collect.Permission.*;
import static net.openhft.jpsg.collect.algo.hash.HashMethodGeneratorCommons.*;
import static net.openhft.jpsg.collect.mapqu.BasicMapQueryUpdateOp.GET;
import static net.openhft.jpsg.collect.mapqu.BasicMapQueryUpdateOp.INSERT;
import static net.openhft.jpsg.collect.mapqu.BasicMapQueryUpdateOp.CUSTOM_INSERT;
import static net.openhft.jpsg.collect.mapqu.Branch.KEY_ABSENT;
import static net.openhft.jpsg.collect.mapqu.Branch.KEY_PRESENT;
import static java.lang.Math.min;
import static java.lang.StrictMath.max;


public class HashMapQueryUpdateMethodGenerator extends MapQueryUpdateMethodGenerator {

    private static final String KEY_SUB = "#key#";
    private static final String VAL_SUB = "#val#";
    private static final String VALUES_SUB = "#vals#";

    private MapQueryUpdateMethod method;
    private int beforeBranches;
    private int firstPresentLine;
    private int firstAbsentLine;

    // inline index/insert state

    int presentBranchSize, absentBranchSize;
    boolean separatePresent, separateAbsent;
    // for generating key absent branch
    boolean removedSlot;
    boolean earlyAbsentLabel;

    // if values copy should be lifted from branches
    boolean commonValuesCopy;

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
            return value() + " == " + valueToCompare;
        }
    }

    @Override
    public void insert(String value) {
        permissions.add(Permission.INSERT);
        if (method.inline()) {
            lines("keys[" + index() + "] = " + key() + ";");
            if (cxt.isMapView())
                lines(values() + "[" + index() + "] = " + value + ";");
            if (removedSlot) {
                lines("postRemovedSlotInsertHook();");
            } else {
                lines("postFreeSlotInsertHook();");
            }
        } else {
            lines("insertAt(insertionIndex, " + key() + ", " + value + ");");
        }
    }

    @Override
    protected void generateLines(Method m) {
        method = (MapQueryUpdateMethod) m;
        determineBranchFeatures();
        if (method.inline()) {
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
        if (cxt.isPrimitiveKey() && cxt.genericVersion()) {
//            throw new IllegalStateException();
            PrimitiveType keyType = (PrimitiveType) cxt.keyOption();
            int keyUsages = 0;
            for (String line : lines) {
                keyUsages += countOccurrences(line, KEY_SUB);
            }
            if (permissions.contains(Permission.INSERT) || permissions.contains(SET_VALUE)) {
                // insert or setValue => key couldn't be Object, key is Character/Integer/...
                // if more than 1 usage, unbox
                if (keyUsages > 1) {
                    for (int i = 0; i < lines.size(); i++) {
                        lines.set(i, replaceAll(lines.get(i), KEY_SUB, "k"));
                    }
                    String keyCopy = indent + cxt.keyType() + " k = key;";
                    lines.add(0, keyCopy);
                }
            } else if (!method.inline()) {
                // key is Object
                // the only usage - in index() call
                if (keyUsages != 1)
                    throw new IllegalStateException();
                // for this usage, cast the key to Character/Integer/...
                String key = "(" + keyType.className + ") key";
                for (int i = 0; i < lines.size(); i++) {
                    lines.set(i, replaceAll(lines.get(i), KEY_SUB, key));
                }
            } else {
                // inlined remove() op
                // cast & unbox
                for (int i = 0; i < lines.size(); i++) {
                    lines.set(i, replaceAll(lines.get(i), KEY_SUB, "k"));
                }
                String keyCopy = indent + cxt.keyType() + " k = (" + keyType.className + ") key;";
                lines.add(0, keyCopy);
            }
        }
        String key;
        if (cxt.isNullKey()) {
            key = "null";
        } else if (cxt.isObjectKey() && method.inline() && method.baseOp() == GET) {
            key = "k";
        } else {
            key = "key";
        }
        for (int i = 0; i < lines.size(); i++) {
            lines.set(i, replaceAll(lines.get(i), KEY_SUB, key));
        }
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
        generateAbsent(removedSlot, true);
    }

    private void generateAbsent(boolean removedSlot, boolean replaceValues) {
        String time = method.baseOp() == INSERT && !method.inline() ? "was" : "is";
        String comment = "// key " + time + " absent";
        if (cxt.mutable() && method.inline()) {
            comment += removedSlot ? ", removed slot" : ", free slot";
        }
        lines(comment);
        int branchStart = firstAbsentLine = lines.size();
        this.removedSlot = removedSlot;
        method.ifAbsent();
        if (replaceValues)
            replaceValues(branchStart);
        this.removedSlot = false;
    }

    private void getIndex() {
        if (method.baseOp() == GET) {
            lines("int index = index(" + key() + ");");
        } else if (method.baseOp() == INSERT) {
            String insertArgs = cxt.isMapView() ? key() + ", value" : key();
            lines("int index = insert(" + insertArgs + ");");
        } else if (method.baseOp() == CUSTOM_INSERT) {
            lines("InsertionIndex insertionIndex = insertionIndex(" + key() + ");");
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
        int valUsages = countValUsages(branchStart);
        if (valUsages >= 2) {
            for (int i = branchStart; i < lines.size(); i++) {
                lines.set(i, replaceAll(lines.get(i), VAL_SUB, "val"));
            }
            lines.add(branchStart,
                    indent + cxt.valueType() + " val = " + values() + "[" + index() + "];");
        } else {
            for (int i = branchStart; i < lines.size(); i++) {
                lines.set(i, replaceAll(lines.get(i), VAL_SUB, values() + "[" + index() + "]"));
            }
        }
        if (commonValuesCopy) {
            for (int i = branchStart; i < lines.size(); i++) {
                lines.set(i, replaceAll(lines.get(i), VALUES_SUB, "vals"));
            }
        } else {
            int valArrayUsages = countValuesUsages(branchStart);
            if (valArrayUsages >= 2) {
                for (int i = branchStart; i < lines.size(); i++) {
                    lines.set(i, replaceAll(lines.get(i), VALUES_SUB, "vals"));
                }
                lines.add(branchStart, indent + cxt.valueType() + "[] vals = values;");
            } else {
                for (int i = branchStart; i < lines.size(); i++) {
                    lines.set(i, replaceAll(lines.get(i), VALUES_SUB, "values"));
                }
            }
        }
        if (method.baseOp() == CUSTOM_INSERT && !method.inline()) {
            for (int i = branchStart; i < lines.size(); i++) {
                String line = lines.get(i);
                if (countOccurrences(line, "index") > 0) {
                    lines.add(branchStart, indent + "int index = insertionIndex.get();");
                    break;
                }
            }
        }
    }

    private int countValUsages(int branchStart) {
        int valUsages = 0;
        for (int i = branchStart; i < lines.size(); i++) {
            String line = lines.get(i);
            valUsages += countOccurrences(line, VAL_SUB);
        }
        return valUsages;
    }

    private int countValuesUsages(int branchStart) {
        int valArrayUsages = 0;
        for (int i = branchStart; i < lines.size(); i++) {
            String line = lines.get(i);
            valArrayUsages += countOccurrences(line, VALUES_SUB);
        }
        return valArrayUsages;
    }

    String removedValue() {
        if (method.inline()) {
            return removed(cxt);
        } else {
            return cxt.isPrimitiveKey() ? "removedValue" : "REMOVED";
        }
    }

    String index() {
        return removedSlot ? "firstRemoved" : "index";
    }

    @Override
    public String key() {
        return KEY_SUB;
    }

    @Override
    public String value() {
        return VAL_SUB;
    }

    private String values() {
        return VALUES_SUB;
    }

    @Override
    public MethodGenerator remove() {
        String keys;
        if (method.inline()) {
            keys = "keys";
            if (cxt.isObjectKey()) {
                keys = "((Object[]) " + keys + ")";
            }
        } else {
            keys = "set";
        }
        lines(keys + "[index] = " + removedValue() + ";");
        if (cxt.isObjectValue()) {
            lines(values() + "[index] = null;");
        }
        lines("postRemoveHook();");
        permissions.add(REMOVE);
        return this;
    }

    @Override
    public MethodGenerator setValue(String newValue) {
        lines(values() + "[index] = " + newValue + ";");
        permissions.add(SET_VALUE);
        return this;
    }


    private void generateInline() {
        inlineBeginning();
        inlineLocals();
        String curAssignment = curAssignment(cxt, "keys", key(), false);
        if (method.mostProbableBranch() == KEY_ABSENT) {
            firstIndexFreeCheck(curAssignment, earlyAbsentLabel);
            if (separatePresent) {
                lines("keyPresent:");
                String keyNotEqualsCond = "cur != " + key();
                if (cxt.isObjectKey() && cxt.immutable()) {
                    keyNotEqualsCond += " && !keyEquals(" + key() +", cur)";
                }
                ifBlock(keyNotEqualsCond);
            } else {
                String keyEqualsCond = "cur == " + key();
                if (cxt.isObjectKey() && cxt.immutable()) {
                    keyEqualsCond += " || keyEquals(" + key() + ", cur)";
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
            if (separateAbsent && !earlyAbsentLabel)
                generateAbsent(false);
        } else {
            // most probable branch - key is present
            if (separatePresent) {
                lines("keyPresent:");
                ifBlock(curAssignment + " != " + key());
            } else {
                ifBlock(curAssignment + " == " + key());
                generatePresent();
                elseBlock();
            }
            firstIndexFreeCheck("cur", earlyAbsentLabel);
            if (cxt.isObjectKey() && cxt.immutable()) {
                ifBlock("keyEquals(" + key() + ", cur)");
                generateOrGoToPresent();
                elseBlock();
            }
            innerInline();
            if (cxt.isObjectKey() && cxt.immutable())
                blockEnd();
            blockEnd();
            if (separateAbsent && !earlyAbsentLabel)
                generateAbsent(false);
            blockEnd();
            if (separatePresent)
                generatePresent();
        }
        inlineEnd();
    }

    private void innerInline() {
        if (cxt.mutable() && (cxt.isObjectKey() || method.baseOp() != GET)) {
            if (method.baseOp() != GET)
                lines("int firstRemoved;");
            if (!cxt.isObjectKey()) {
                countStep();
            }
            ifBlock("cur != " + removed(cxt));
            if (cxt.isObjectKey()) {
                if (method.mostProbableBranch() == KEY_PRESENT) {
                    ifBlock("keyEquals(" + key() + ", cur)");
                    generateOrGoToPresent();
                    elseBlock();
                } else {
                    ifBlock("!keyEquals(" + key() + ", cur)");
                }
            }
            ifBlock("noRemoved()");
            if (cxt.isObjectKey())
                countStep();
            keySearchLoop(true);
            if (method.baseOp() != GET) {
                elseBlock();
                lines("firstRemoved = -1;");
            }
            blockEnd();
            if (cxt.isObjectKey()) {
                if (method.mostProbableBranch() != KEY_PRESENT) {
                    elseBlock();
                    generateOrGoToPresent();
                }
                blockEnd();
            }
            if (method.baseOp() != GET) {
                elseBlock();
                lines("firstRemoved = index;");
            }
            blockEnd();
            if (cxt.isObjectKey())
                countStep();
            if (method.baseOp() == GET) {
                keySearchLoop(false);
            } else {
                keySearchLoopDifferentRemovedHandling();
            }
        } else {
            countStep();
            keySearchLoop(true);
        }
    }

    private void inlineLocals() {
        if (cxt.isObjectKey())
            lines("// noinspection unchecked");
        lines(cxt.keyType() + "[] keys = " +
                        (cxt.isObjectKey() ? "(" + cxt.keyType() + "[]) " : "") + "set;");
        if (commonValuesCopy)
            copyValues();
        lines(
                "int " + (cxt.isNullKey() ? "" : "capacity, hash, ") + "index;",
                cxt.keyType() + " cur;"
        );
    }

    private void copyValues() {
        lines(cxt.valueType() + "[] vals = values;");
    }

    private void inlineBeginning() {
        if (cxt.isObjectKey()) {
            ifBlock("key != null");
            if (method.baseOp() == GET) {
                lines(
                        "// noinspection unchecked",
                        cxt.keyType() + " " + key() + " = (" + cxt.keyType() + ") key;"
                );
            }
        }
        method.beginning();
        earlyAbsentLabel = false;
        if (method.baseOp() == GET) {
            if (cxt.isIntegralKey()) {
                boolean isRemoveOp = permissions.contains(Permission.REMOVE);
                lines(cxt.keyType() + " free" + (isRemoveOp ? ", removed" : "") + ";");
                if (separateAbsent) {
                    lines("keyAbsent:");
                    earlyAbsentLabel = true;
                }

                String removed = isRemoveOp ? "(removed = removedValue)" : "removedValue";
                ifBlock(key() + " != (free = freeValue)" +
                        (cxt.mutable() ? " && " + key() + " != " + removed : ""));
            }
        } else {
            if (cxt.isIntegralKey()) {
                lines(cxt.keyType() + " free;");
                if (cxt.mutable())
                    lines(cxt.keyType() + " removed = removedValue;");
                ifBlock(key() + " == (free = freeValue)");
                lines("free = changeFree();");
                if (cxt.mutable()) {
                    elseIf(key() + " == removed");
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
                generateAbsent(false);
                blockEnd();
            }
        }
        if (cxt.isObjectKey()) {
            elseBlock();
            lines("return " + method.name() + "NullKey(" + method.nullArgs() + ");");
            blockEnd();
        }
    }

    private void firstIndexFreeCheck(String cur, boolean earlyAbsentLabel) {
        if (separateAbsent) {
            if (!earlyAbsentLabel) {
                lines(absentLabel() + ":");
            }
            ifBlock(cur + " != " + free(cxt));
        } else {
            ifBlock(cur + " == " + free(cxt));
            generateAbsent(false);
            elseBlock();
        }
    }

    private void countStep() {
        if (!cxt.isNullKey()) {
            lines(step());
        }
    }

    private void keySearchLoop(boolean noRemoved) {
        lines("while (true)").block();
        nextIndex();
        if (method.mostProbableBranch() == KEY_PRESENT) {
            ifBlock("(cur = keys[index]) == " + key());
            generateOrGoToPresent();
            elseIf("cur == " + free(cxt));
            generateOrGoToAbsent(false);
            blockEnd();
            if (cxt.isObjectKey()) {
                lines("else if (" + objectKeyEqualsCond(noRemoved) + ")").block();
                generateOrGoToPresent();
                blockEnd();
            }
        } else {
            ifBlock("(cur = keys[index]) == " + free(cxt));
            generateOrGoToAbsent(false);
            String presentCond = "cur == " + key();
            if (cxt.isObjectKey()) {
                presentCond += " || (" + objectKeyEqualsCond(noRemoved) + ")";
            }
            elseIf(presentCond);
            generateOrGoToPresent();
            blockEnd();
        }
        blockEnd();
    }

    private void nextIndex() {
        if (!cxt.isNullKey()) {
            lines(HashMethodGeneratorCommons.nextIndex());
        } else {
            lines("index++;");
        }
    }

    private void keySearchLoopDifferentRemovedHandling() {
        lines("while (true)").block();
        nextIndex();
        if (method.mostProbableBranch() == KEY_PRESENT) {
            ifBlock("(cur = keys[index]) == " + key());
            generateOrGoToPresent();
            elseIf("cur == " + free(cxt));
            generateAbsentDependingOnFirstRemoved();
        } else {
            ifBlock("(cur = keys[index]) == " + free(cxt));
            generateAbsentDependingOnFirstRemoved();
            elseIf("cur == " + key());
            generateOrGoToPresent();
        }
        if (cxt.isObjectKey()) {
            elseIf("cur != " + removed(cxt));
            ifBlock("keyEquals(" + key() + ", cur)");
            generateOrGoToPresent();
            blockEnd();
            elseIf("firstRemoved < 0");
        } else {
            elseIf("cur == " + removed(cxt) + " && firstRemoved < 0");
        }
        lines("firstRemoved = index;");
        blockEnd();
        blockEnd();
    }

    private void generateAbsentDependingOnFirstRemoved() {
        ifBlock("firstRemoved < 0");
        generateOrGoToAbsent(false);
        elseBlock();
        generateOrGoToAbsent(true);
        blockEnd();
    }

    private String objectKeyEqualsCond(boolean noRemoved) {
        return (cxt.mutable() && !noRemoved ? "cur != REMOVED && " : "") +
                "keyEquals(" + key() + ", cur)";
    }

    private void determineBranchFeatures() {
        generateAbsent(false, false);
        // first line is comment
        absentBranchSize = lines.size() - 1;
        separateAbsent = absentBranchSize > 1;
        int absentBranchValuesUsages = countValuesUsages(0) + countValUsages(0);
        lines.clear();

        generatePresent(false);
        // first line is comment
        presentBranchSize = lines.size() - 1;
        separatePresent = presentBranchSize > 1;
        int presentBranchValueUsages = countValuesUsages(0) + countValUsages(0);
        lines.clear();

        commonValuesCopy = absentBranchValuesUsages > 0 && presentBranchValueUsages > 0;
    }

    private void generateOrGoToPresent() {
        if (separatePresent) {
            lines("break keyPresent;");
        } else {
            generatePresent();
        }
    }

    private void generateOrGoToAbsent(boolean removedSlot) {
        if (separateAbsent && !removedSlot) {
            lines("break " + absentLabel() + ";");
        } else {
            generateAbsent(removedSlot);
        }
    }

    private String absentLabel() {
        if (method.baseOp() != GET && cxt.mutable()) {
            return "keyAbsentFreeSlot";
        } else {
            return "keyAbsent";
        }
    }
}
