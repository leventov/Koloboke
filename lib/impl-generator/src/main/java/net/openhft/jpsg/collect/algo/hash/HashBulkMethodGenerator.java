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
import net.openhft.jpsg.collect.Method;
import net.openhft.jpsg.collect.Permission;
import net.openhft.jpsg.collect.bulk.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.openhft.jpsg.collect.Permission.REMOVE;
import static net.openhft.jpsg.collect.algo.hash.HashMethodGeneratorCommons.*;


public class HashBulkMethodGenerator extends BulkMethodGenerator {

    private static final String KEY_SUB = "#key#";
    private static final String KEY_OBJ_SUB = "#key.obj#";
    private static final String VAL_SUB = "#val#";

    private boolean noRemoved = true;
    private BulkMethod method;
    private boolean valuesUsed = false;
    private boolean indexUsed = false;
    private boolean methodReturnsSomething = false;

    @Override
    public void generateLines(Method m) {
        this.method = (BulkMethod) m;
        determineFeatures();
        callInternalVersion();
        innerGenerate(true);
    }

    private void determineFeatures() {
        innerGenerate(false);
        noRemoved = true;
        valuesUsed |= countUsages(0, "entry") > 0;
        valuesUsed |= countUsages(0, VAL_SUB) > 0;
        lines.clear();
    }

    @Override
    public void ret(String ret) {
        if (!ret.isEmpty())
            methodReturnsSomething = true;
        super.ret(ret);
    }

    private boolean unsafeLoop() {
        return !valuesUsed && !indexUsed && parallelKV(cxt) && !doubleSizedParallel(cxt);
    }

    private void innerGenerate(boolean replace) {
        method.beginning();

        if (cxt.isEntryView() && method.entryType() == EntryType.REUSABLE)
            lines("ReusableEntry e = new ReusableEntry();");

        if (!cxt.immutable()) {
            lines("int mc = modCount();");
        }
        if (cxt.isIntegralKey()) {
            lines(cxt.keyType() + " free = freeValue;");
            if (possibleRemovedSlots(cxt)) {
                lines(cxt.keyType() + " removed = removedValue;");
            }
        }
        if (!parallelKV(cxt)) {
            lines(cxt.keyUnwrappedRawType() + "[] keys = set;");
        } else {
            copyTable(this, cxt);
        }
        if (isLHash(cxt) && permissions.contains(REMOVE)) {
            lines("int capacityMask = " + capacityMask(cxt) + ";");
            lines("int firstDelayedRemoved = -1;");
            if (cxt.isIntegralKey()) {
                String delayedValue = ((PrimitiveType) cxt.keyOption()).bitsType().formatValue("0");
                lines(cxt.keyUnwrappedRawType() + " delayedRemoved = " + delayedValue + ";");
            }
        }
        if (valuesUsed && !parallelKV(cxt))
            lines(cxt.valueUnwrappedType() + "[] vals = values;");

        if (unsafeLoop()) {
            PrimitiveType key = (PrimitiveType) cxt.keyOption();
            lines("long base = " + TableType.INSTANCE.apply(key).upper + "_BASE + " +
                            key.upper + "_KEY_OFFSET;");
        } else {
            declareEntry(this, cxt);
        }

        method.rightBeforeLoop();

        boolean splitLoops = possibleRemovedSlots(cxt) && !cxt.isFloatingKey();
        if (splitLoops) {
            lines("if (noRemoved()) {");
            indent();
        }
        bulkLoop(replace);
        if (splitLoops) {
            unIndent();
            lines("} else").block();
            noRemoved = false;
            bulkLoop(replace);
            blockEnd();
        }

        if (isLHash(cxt) && permissions.contains(REMOVE)) {
            ifBlock("firstDelayedRemoved >= 0"); {
                String addArg = cxt.isIntegralKey() ? ", delayedRemoved" : "";
                lines("closeDelayedRemoved(firstDelayedRemoved" + addArg + ");");
            } blockEnd();
        }

        if (!cxt.immutable()) {
            lines(
                    "if (mc != modCount())",
                    "    throw new java.util.ConcurrentModificationException();"
            );
        }

        method.end();
    }

    private void callInternalVersion() {
        if (method.withInternalVersion() && !cxt.internalVersion() && !cxt.genericVersion() &&
                (cxt.isFloatingView() ||
                        cxt.isMapView() && (cxt.isFloatingKey() || cxt.isFloatingValue()))) {
            String internalClass = "Internal";
            if (cxt.isFloatingView()) {
                internalClass += ((PrimitiveType) cxt.viewOption()).title + "CollectionOps";
            } else {
                internalClass += cxt.keyOption() instanceof PrimitiveType ?
                        ((PrimitiveType) cxt.keyOption()).title :
                        "Obj";
                internalClass += cxt.mapValueOption() instanceof PrimitiveType ?
                        ((PrimitiveType) cxt.mapValueOption()).title :
                        "Obj";
                internalClass += "MapOps";
            }
            String collectionArgName = method.collectionArgName();
            lines(
                    "if (" + collectionArgName + " instanceof " + internalClass + ")",
                    "    " + (methodReturnsSomething ? "return " : "") +  method.name() + "(" +
                            (method.argsBeforeCollection().isEmpty() ? "" :
                                    method.argsBeforeCollection() + ", ") +
                            "(" + internalClass + ") " + collectionArgName + ");"
            );
        }
    }

    private void bulkLoop(boolean replace) {
        if (unsafeLoop()) {
            String tableTypeUpper = TableType.INSTANCE.apply((PrimitiveType) cxt.keyOption()).upper;
            lines("for (long off = ((long) tab.length) << " + tableTypeUpper + "_SCALE_SHIFT; " +
                    "(off -= " + tableTypeUpper + "_SCALE) >= 0L;)").block();
        } else {
            forLoop(this, cxt, localTableVar(cxt) + ".length", "i", false);
        } {
            int bodyStart = lines.size();
            lines("if (" + isFull() + ")").block(); {
                method.loopBody();
            } blockEnd();
            if (replace) {
                replaceValue(bodyStart + 1); // after if (isFull) check
                replaceKey(bodyStart);
            }
            noInspectionKeyCast(bodyStart);
        } blockEnd();
    }

    private void replaceKey(int bodyStart) {
        int keyUsages = countUsages(bodyStart, KEY_SUB);
        int keyObjUsages = countUsages(bodyStart, KEY_OBJ_SUB);
        String key;
        if (!unsafeLoop()) {
            key = readKeyOrEntry(cxt, "i");
        } else {
            key = cxt.unsafeGetKeyBits("tab", "base + off");
        }
        String castedKey = key;
        if (cxt.isObjectKey()) {
            castedKey = "(" + cxt.keyType() + ") " + castedKey;
        }
        if (keyUsages + keyObjUsages <= 1) {
            replaceAll(bodyStart, KEY_SUB, castedKey);
            replaceAll(bodyStart, KEY_OBJ_SUB, key);
        } else if (keyUsages == 0) {
            replaceFirstDifferent(bodyStart, KEY_OBJ_SUB, "(key = " + key + ")", "key");
            lines.add(bodyStart, indent + cxt.keyUnwrappedRawType() + " key;");
        } else {
            boolean replacedFirst = false;
            for (int i = bodyStart; i < lines.size(); i++) {
                String line = lines.get(i);
                if (!replacedFirst) {
                    String newLine = replaceFirst(line, KEY_SUB, "(key = " + castedKey + ")");
                    if (!line.equals(newLine)) {
                        replacedFirst = true;
                        line = newLine;
                    } else {
                        newLine = replaceFirst(line, KEY_OBJ_SUB, "(key = " + castedKey + ")");
                        if (!line.equals(newLine)) {
                            replacedFirst = true;
                            line = newLine;
                        }
                    }
                }
                line = replaceAll(line, KEY_SUB, "key");
                lines.set(i, replaceAll(line, KEY_OBJ_SUB, "key"));
            }
            lines.add(bodyStart, indent + cxt.keyUnwrappedType() + " key;");
        }
    }

    private void noInspectionKeyCast(int bodyStart) {
        if (cxt.isObjectKey()) {
            Pattern cast = Pattern.compile(Pattern.quote("(" + cxt.keyType() + ")"));
            for (int i = lines.size(); i-- > bodyStart;) {
                String line = lines.get(i);
                Matcher m = cast.matcher(line);
                if (m.find()) {
                    Matcher indentM = Pattern.compile("\\s+").matcher(line);
                    boolean foundIndent = indentM.find();
                    assert foundIndent;
                    String indent = indentM.group();
                    lines.add(i, indent + "// noinspection unchecked");
                }
            }
        }
    }

    private void replaceValue(int bodyStart) {
        int valueUsages = countUsages(bodyStart, VAL_SUB);

        if (valueUsages > 1) {
            replaceFirstDifferent(bodyStart, VAL_SUB, "(val = " + readValue(cxt, "i") + ")", "val");
            lines.add(bodyStart, indent + cxt.valueUnwrappedType() + " val;");
        } else if (valueUsages == 1) {
            replaceAll(bodyStart, VAL_SUB, readValue(cxt, "i"));
        }
    }

    private String isFull() {
        if (cxt.isFloatingKey())
            return KEY_OBJ_SUB + " < FREE_BITS";
        if (!noRemoved) {
            return KEY_OBJ_SUB + " != " + free(cxt) + " && " + KEY_OBJ_SUB + " != " + removed(cxt);
        } else {
            return KEY_OBJ_SUB + " != " + free(cxt);
        }
    }


    @Override
    public String viewValues() {
        if (cxt.isKeyView()) return key();
        if (cxt.isValueView()) return value();
        if (cxt.isEntryView()) return entry();
        if (cxt.isMapView()) return keyAndValue();
        throw new IllegalStateException();
    }

    private String entry() {
        if (method.entryType() == EntryType.SIMPLE) {
            if (!cxt.immutable()) {
                return "new MutableEntry(mc, i, " + unwrappedKeyAndValue() + ")";
            } else {
                return "new ImmutableEntry(" + unwrappedKeyAndValue() + ")";
            }
        } else {
            return "e.with(" + unwrappedKeyAndValue() + ")";
        }
    }

    private String unwrappedKeyAndValue() {
        return unwrappedKey() + ", " + unwrappedValue();
    }

    @Override
    public String viewElem() {
        if (cxt.isMapView()) throw new IllegalStateException();
        return viewValues();
    }

    @Override
    public String key() {
        return wrapKey(unwrappedKey());
    }

    @Override
    public String unwrappedKey() {
        return KEY_SUB;
    }

    @Override
    public String value() {
        return wrapValue(unwrappedValue());
    }

    @Override
    public String unwrappedValue() {
        return VAL_SUB;
    }

    String index() {
        indexUsed = true;
        return "i";
    }

    @Override
    public BulkMethodGenerator remove() {
        incrementModCount();
        lines("mc++;");
        if (isLHash(cxt)) {
            lHashShiftRemove();
        } else {
            tombstoneRemove();
            lines("postRemoveHook();");
        }
        permissions.add(REMOVE);
        return this;
    }

    private void tombstoneRemove() {
        if (!unsafeLoop()) {
            writeKey(this, cxt, "i", removed(cxt));
            if (cxt.isObjectValue()) {
                valuesUsed = true;
                writeValue(this, cxt, "i", "null");
            }
        } else {
            cxt.unsafePutKeyBits(this, "tab", "base + off", removed(cxt));
            assert !cxt.isObjectValue();
        }
    }

    private void lHashShiftRemove() {
        valuesUsed |= cxt.hasValues();
        new LHashShiftRemove(this, cxt, "i", "tab", "vals") {

            @Override
            void generate() {
                lines("closeDeletion:");
                ifBlock("firstDelayedRemoved < 0"); {
                    // "simple" mode
                    closeDeletion();
                    postRemoveHook();
                } elseBlock(); {
                    // "delayed removed" mode
                    writeKey(g, cxt, "i", (cxt.isIntegralKey() ? "delayedRemoved" :
                            (cxt.isFloatingKey() ? "REMOVED_BITS" : "REMOVED")));
                } blockEnd();
            }

            @Override
            boolean rawKeys() {
                return true;
            }

            @Override
            void beforeShift() {
                // This condition means indexToShift wrapped around zero and keyToShift
                // was already passed by this bulk operation. To prevent processing it twice
                // we enter "delayed removed" mode, in which we place tombstones each time we
                // want to shift delete a key.
                ifBlock("indexToShift > indexToRemove"); {
                    // set `firstDelayedRemoved` to `i`, not `indexToRemove` because after turning
                    // into "delayed removed" mode we could remove the slot just before i-th.
                    // more strictly `firstDelayedRemoved` should be `indexToRemove`,
                    // if `indexToRemove` is equal to `i`, i. e. it is the first iteration
                    // of close deletion loop, and `i - 1` otherwise, but we just use `i`
                    // to simplify the code, because anyway it is a very rare branch
                    lines("firstDelayedRemoved = i;");
                    String delayedRemoved;
                    if (cxt.isIntegralKey()) {
                        lines("delayedRemoved = " + key() + ";");
                        delayedRemoved = key();
                    } else if (cxt.isFloatingKey()) {
                        delayedRemoved = "REMOVED_BITS";
                    } else {
                        delayedRemoved = "REMOVED";
                    }
                    writeKey(g, cxt, "indexToRemove", delayedRemoved);
                    lines("break closeDeletion;");
                } blockEnd();
                ifBlock("indexToRemove == i"); {
                    String increment = doubleSizedParallel(cxt) ? " += 2" : "++";
                    lines("i" + increment + ";");
                } blockEnd();
            }
        }.generate();
    }

    @Override
    public BulkMethodGenerator setValue(String newValue) {
        valuesUsed = true;
        assert cxt.isMapView();
        writeValue(this, cxt, "i", unwrapValue(newValue));
        permissions.add(Permission.SET_VALUE);
        return this;
    }

    @Override
    public BulkMethodGenerator clear() {
        lines("clear();");
        permissions.add(Permission.CLEAR);
        return this;
    }
}
