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

import static net.openhft.jpsg.collect.algo.hash.HashMethodGeneratorCommons.*;


public class HashBulkMethodGenerator extends BulkMethodGenerator {

    private static final String KEY_SUB = "#key#";
    private static final String KEY_OBJ_SUB = "#key.obj#";
    private static final String VAL_SUB = "#val#";

    private boolean noRemoved = true;
    private BulkMethod method;

    @Override
    public void generateLines(Method m) {
        this.method = (BulkMethod) m;

        callInternalVersion();

        method.beginning();

        if (cxt.isEntryView() && method.entryType() == EntryType.REUSABLE)
            lines("ReusableEntry entry = new ReusableEntry();");

        if (cxt.mutable()) {
            lines("int mc = modCount();");
        }
        if (cxt.isIntegralKey()) {
            lines(cxt.keyType() + " free = freeValue;");
            if (possibleRemovedSlots(cxt)) {
                lines(cxt.keyType() + " removed = removedValue;");
            }
        }
        lines(cxt.keyUnwrappedRawType() + "[] keys = set;");
        int beforeLoops = lines.size();

        method.rightBeforeLoop();

        boolean splitLoops = possibleRemovedSlots(cxt) && !cxt.isFloatingKey();
        if (splitLoops) {
            lines("if (noRemoved()) {");
            indent();
        }
        bulkLoop();
        if (splitLoops) {
            unIndent();
            lines("} else").block();
            noRemoved = false;
            bulkLoop();
            blockEnd();
        }

        boolean valuesUsed = false;
        for (int i = lines.size(); i-- > beforeLoops;) {
            if (lines.get(i).contains("vals"))
                valuesUsed = true;
        }
        if (valuesUsed)
            lines.add(beforeLoops, indent + copyValueArray());

        if (cxt.mutable()) {
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
                    "    " + method.name() + "(" +
                            (method.argsBeforeCollection().isEmpty() ? "" :
                                    method.argsBeforeCollection() + ", ") +
                            "(" + internalClass + ") " + collectionArgName + ");"
            );
        }
    }

    private void bulkLoop() {
        lines("for (int i = keys.length - 1; i >= 0; i--)").block(); {
            int bodyStart = lines.size();
            lines("if (" + isFull() + ")").block(); {
                method.loopBody();
            } blockEnd();
            replaceValue(bodyStart + 1); // after if (isFull) check
            replaceKey(bodyStart);
            noInspectionKeyCast(bodyStart);
        } blockEnd();
    }

    private void replaceKey(int bodyStart) {
        int keyUsages = countUsages(bodyStart, KEY_SUB);
        int keyObjUsages = countUsages(bodyStart, KEY_OBJ_SUB);
        String castedKey = "keys[i]";
        if (cxt.isObjectKey()) {
            castedKey = "(" + cxt.keyType() + ") " + castedKey;
        }
        if (keyUsages + keyObjUsages <= 1) {
            replaceAll(bodyStart, KEY_SUB, castedKey);
            replaceAll(bodyStart, KEY_OBJ_SUB, "keys[i]");
        } else if (keyUsages == 0) {
            replaceFirstDifferent(bodyStart, KEY_OBJ_SUB, "(key = keys[i])", "key");
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
            replaceFirstDifferent(bodyStart, VAL_SUB, "(val = vals[i])", "val");
            lines.add(bodyStart, indent + cxt.valueUnwrappedType() + " val;");
        } else if (valueUsages == 1) {
            replaceAll(bodyStart, VAL_SUB, "vals[i]");
        }
    }

    private void replaceFirstDifferent(int bodyStart,
            String placeholder, String firstReplacement, String restReplacement) {
        boolean replacedFirst = false;
        for (int i = bodyStart; i < lines.size(); i++) {
            String line = lines.get(i);
            if (!replacedFirst) {
                String newLine = replaceFirst(line, placeholder, firstReplacement);
                if (!line.equals(newLine)) {
                    replacedFirst = true;
                    line = newLine;
                }
            }
            lines.set(i, replaceAll(line, placeholder, restReplacement));
        }
    }

    private String copyValueArray() {
        return cxt.valueUnwrappedType() + "[] vals = values;";
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
            if (cxt.mutable()) {
                return "new MutableEntry(mc, i, " + unwrappedKeyAndValue() + ")";
            } else {
                return "new ImmutableEntry(" + unwrappedKeyAndValue() + ")";
            }
        } else {
            return "entry.with(" + unwrappedKeyAndValue() + ")";
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
        return "i";
    }

    @Override
    public BulkMethodGenerator remove() {
        incrementModCount();
        lines("mc++;");
        lines("keys[i] = " + removed(cxt) + ";");
        if (cxt.isObjectValue()) {
            lines("vals[i] = null;");
        }
        lines("postRemoveHook();");
        permissions.add(Permission.REMOVE);
        return this;
    }

    @Override
    public BulkMethodGenerator setValue(String newValue) {
        if (!cxt.isMapView()) throw new IllegalStateException();
        lines("vals[i] = " + unwrapValue(newValue) + ";");
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
