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

import net.openhft.jpsg.collect.*;
import net.openhft.jpsg.collect.iter.CursorMethodGenerator;

import static net.openhft.jpsg.collect.algo.hash.HashIterMethodGeneratorCommons.*;
import static net.openhft.jpsg.collect.algo.hash.HashMethodGeneratorCommons.free;
import static net.openhft.jpsg.collect.algo.hash.HashMethodGeneratorCommons.removed;


public class HashCursorMethodGenerator extends CursorMethodGenerator {

    @Override
    public void generateFields() {
        commonFields(this, cxt);
        lines(
                "int index;",
                cxt.keyRawType() + " curKey;"
        );
        if (!cxt.isKeyView()) {
            lines(cxt.valueType() + " curValue;");
        }
    }

    @Override
    public void generateConstructor() {
        commonConstructorOps(this, cxt, false);
        if (cxt.isObjectKey()) {
            this.lines(
                    "// noinspection unchecked",
                    "this.keys = (" + cxt.keyType() + "[]) set;"
            );
        } else {
            this.lines("this.keys = set;");
        }
        if (!cxt.isKeyView()) {
            this.lines("vals = values;");
        }
        if (cxt.isPrimitiveKey()) {
            this.lines(cxt.keyType() + " " + free(cxt) + " = this." + free(cxt) + " = freeValue;");
            if (cxt.mutable()) {
                this.lines("this." + removed(cxt) + " = removedValue;");
            }
        }
        lines("curKey = " + free(cxt) + ";");
    }

    @Override
    public void generateMoveNext() {
        checkModCount(this, cxt, false);
        copyArrays(this, cxt);
        copySpecials(this, cxt);
        lines("for (int i = index - 1; i >= 0; i--)").block();
        lines(cxt.keyRawType() + " key;");
        ifBlock(keyNotFreeOrRemoved(cxt, "i", true));
        lines(
                "index = i;",
                "curKey = key;"
        );
        if (!cxt.isKeyView())
            lines("curValue = vals[i];");
        lines("return true;");
        blockEnd().blockEnd();
        lines(
                "index = -1;",
                "return false;"
        );
        endOfModCountCheck(this, cxt);
    }

    @Override
    public void generateKey() {
        lines(cxt.keyRawType() + " curKey;");
        ifBlock("((curKey = this.curKey) != " + free(cxt) + ")");
        if (cxt.isObjectKey())
            lines("// noinspection unchecked");
        ret((cxt.isObjectKey() ? "(" + cxt.keyType() + ") " : "") + "curKey");
        endOfIllegalStateCheck(this, cxt);
    }

    @Override
    public void generateValue() {
        ifBlock("curKey != " + free(cxt));
        ret("curValue");
        endOfIllegalStateCheck(this, cxt);
    }

    @Override
    public void generateSetValue() {
        ifBlock("curKey != " + free(cxt));
        checkModCount(this, cxt, false);
        lines("vals[index] = value;");
        endOfModCountCheck(this, cxt);
        endOfIllegalStateCheck(this, cxt);
    }

    @Override
    public void generateEntry() {
        lines(cxt.keyRawType() + " curKey;");
        ifBlock("((curKey = this.curKey) != " + free(cxt) + ")");
        if (cxt.isObjectKey())
            lines("// noinspection unchecked");
        String key = (cxt.isObjectKey() ? "(" + cxt.keyType() + ") " : "") + "curKey";
        ret(entry(cxt, "expectedModCount", "index", key, "curValue"));
        endOfIllegalStateCheck(this, cxt);
    }

    @Override
    public void generateRemove() {
        permissions.add(Permission.REMOVE);
        if (cxt.isPrimitiveKey()) {
            lines(cxt.keyType() + " " + free(cxt) + ";");
            ifBlock("curKey != (" + free(cxt) +" = this." + free(cxt) + ")");
        } else {
            ifBlock("curKey != " + free(cxt));
        }
        ifBlock("expectedModCount++ == " + modCount());
        if (cxt.isObjectValue())
            lines("int index;");
        String indexAssignment = cxt.isObjectValue() ? "index = this.index" : "index";
        String keys = cxt.isObjectKey() ? "((Object[]) keys)" : "keys";
        lines(keys + "[" + indexAssignment + "] = " + removed(cxt) + ";");
        if (cxt.isObjectValue()) {
            lines("vals[index] = null;");
        }
        lines("postRemoveHook();");
        lines("curKey = " + free(cxt) + ";");
        endOfModCountCheck(this, cxt);
        endOfIllegalStateCheck(this, cxt);
    }

    @Override
    public void generateForEachForward() {
        if (cxt.mutable()) {
            lines("int mc = expectedModCount;");
        }
        copyArrays(this, cxt);
        copySpecials(this, cxt);
        lines("int index = this.index;");
        lines("for (int i = index - 1; i >= 0; i--)").block();
        if (!noRemoved(cxt) || !cxt.isValueView())
            lines(cxt.keyRawType() + " key;");
        ifBlock(keyNotFreeOrRemoved(cxt, "i", false));
        if (cxt.isObjectKey())
            lines("// noinspection unchecked");
        lines("action.accept(" + makeNext(cxt, "i") + ");");
        blockEnd().blockEnd();
        String concurrentModCond = "index != this.index";
        if (cxt.mutable())
            concurrentModCond += " || mc != " + modCount();
        ifBlock(concurrentModCond);
        concurrentMod();
        blockEnd();
        lines("this.index = -1;");
        lines("curKey = " + free(cxt) + ";");
    }
}
