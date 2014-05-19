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
import net.openhft.jpsg.collect.iter.IteratorMethodGenerator;

import static net.openhft.jpsg.collect.algo.hash.HashIterMethodGeneratorCommons.*;
import static net.openhft.jpsg.collect.algo.hash.HashMethodGeneratorCommons.free;
import static net.openhft.jpsg.collect.algo.hash.HashMethodGeneratorCommons.removed;


public class HashIteratorMethodGenerator extends IteratorMethodGenerator {

    @Override
    public void generateFields() {
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
        lines("while (--nextI >= 0)").block();
        ifKeyNotFreeOrRemoved(this, cxt, "nextI", false);
        if (cxt.isObjectKey())
            lines("// noinspection unchecked");
        lines("next = " + makeNext(cxt, "nextI") + ";");
        lines("break;");
        blockEnd().blockEnd();
        lines("nextIndex = nextI;");
    }

    @Override
    public void generateConstructor() {
        commonConstructorOps(this, cxt, true);
        if (cxt.isObjectKey()) {
            this.lines(
                    "// noinspection unchecked",
                    cxt.keyType() + "[] keys = this.keys = (" + cxt.keyType() + "[]) set;"
            );
        } else {
            this.lines(cxt.keyUnwrappedType() + "[] keys = this.keys = set;");
        }
        if (cxt.isValueView() || cxt.isEntryView()) {
            this.lines(cxt.valueUnwrappedType() + "[] vals = this.vals = values;");
        }
        if (cxt.isIntegralKey()) {
            this.lines(cxt.keyType() + " " + free(cxt) + " = this." + free(cxt) + " = freeValue;");
            if (cxt.mutable()) {
                this.lines(
                        (noRemoved(cxt) ? "" : cxt.keyType() + " " + removed(cxt) + " = ") +
                        "this." + removed(cxt) + " = removedValue;");
            }
        }
        lines("int nextI = keys.length;");
        loop();
    }

    @Override
    public void generateHasNext() {
        ret("nextIndex >= 0");
    }

    @Override
    public void generateNext() {
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
    public void generateRemove() {
        permissions.add(Permission.REMOVE);
        lines("int i;");
        ifBlock("(i = index) >= 0");
        ifBlock("expectedModCount++ == " + modCount());
        incrementModCount();
        String keys = cxt.isObjectKey() ? "((Object[]) keys)" : "keys";
        lines(keys + "[i] = " + removed(cxt) + ";");
        if (cxt.isObjectValue()) {
            lines("vals[i] = null;");
        }
        lines("postRemoveHook();");
        lines("index = -1;");
        endOfModCountCheck(this, cxt);
        endOfIllegalStateCheck(this, cxt);
    }

    @Override
    public void generateForEachRemaining() {
        if (cxt.mutable()) {
            lines("int mc = expectedModCount;");
        }
        copyArrays(this, cxt);
        copySpecials(this, cxt);
        lines("int nextI = nextIndex;");
        lines("for (int i = nextI; i >= 0; i--)").block();
        ifKeyNotFreeOrRemoved(this, cxt, "i", false);
        if (cxt.isObjectKey())
            lines("// noinspection unchecked");
        lines("action.accept(" + makeNext(cxt, "i") + ");");
        blockEnd().blockEnd();
        String concurrentModCond = "nextI != nextIndex";
        if (!cxt.immutable())
            concurrentModCond += " || mc != " + modCount();
        ifBlock(concurrentModCond);
        concurrentMod();
        blockEnd();
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
        return (cxt.mutable() ? "Mutable" : "Immutable") + "Entry";
    }
}
