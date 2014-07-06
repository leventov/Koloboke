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

import static net.openhft.jpsg.collect.algo.hash.HashMethodGeneratorCommons.*;


public final class LHashCloseDelayedRemoved implements Method {

    @Override
    public void init(net.openhft.jpsg.collect.MethodGenerator g, MethodContext cxt) {
        assert HashMethodGeneratorCommons.isLHash(cxt);
    }

    @Override
    public Class<? extends net.openhft.jpsg.collect.MethodGenerator> generatorBase() {
        return MethodGenerator.class;
    }

    public static class MethodGenerator extends net.openhft.jpsg.collect.MethodGenerator {

        @Override
        protected void generateLines(Method method) {
            assert method instanceof LHashCloseDelayedRemoved;

            if (cxt.isIntegralKey()) {
                lines(cxt.keyType() + " free = freeValue;");
                if (possibleRemovedSlots(cxt) && !noRemoved(cxt))
                    lines(cxt.keyType() + " removed = removedValue;");
            }
            copyArrays(this, cxt, cxt.hasValues());
            lines("int capacityMask = " + capacityMask(cxt) + ";");
            declareEntry(this, cxt);
            forLoop(this, cxt, "firstDelayedRemoved", "i", true); {
                String removed = cxt.isPrimitiveKey() ? "delayedRemoved" : "REMOVED";
                ifBlock(readKeyOrEntry(cxt, "i") + " == " + removed); {
                    new LHashShiftRemove(this, cxt, "i", "tab", "vals") {
                        @Override
                        String additionalShiftPrecondition() {
                            return "keyToShift != " + removed;
                        }
                    }.closeDeletion();
                } blockEnd();
            } blockEnd();
        }
    }
}
