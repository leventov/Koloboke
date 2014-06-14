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
import net.openhft.jpsg.collect.MethodContext;
import net.openhft.jpsg.collect.MethodGenerator;

import static java.lang.String.format;
import static net.openhft.jpsg.collect.algo.hash.HashMethodGeneratorCommons.*;


final class KeySearch {
    static boolean tryPrecomputeStep(MethodGenerator g, MethodContext cxt) {
        if (isDHash(cxt) && !cxt.isNullKey()) {
            g.lines("int step = (hash % (capacity - 2)) + 1;");
            return true;
        } else {
            return false;
        }
    }

    static InnerLoop innerLoop(MethodGenerator g, MethodContext cxt, InnerLoop.Body body,
            boolean stepPrecomputed) {
        if (isLHash(cxt)) {
            assert !stepPrecomputed;
            return new LHashInnerLoop(g, cxt, body);
        } else if (isDHash(cxt)) {
            return new DHashInnerLoop(g, cxt, body, stepPrecomputed);
        } else {
            assert !stepPrecomputed;
            assertHash(cxt, isQHash(cxt));
            return new QHashInnerLoop(g, cxt, body);
        }
    }

    static String curAssignment(MethodContext cxt, String keys, String key,
            boolean capacityAssigned) {
        return "(cur = " + firstKey(cxt, keys, key, capacityAssigned, true) + ")";
    }

    static String firstKey(MethodContext cxt, String keys, String key,
            boolean capacityAssigned, boolean distinctNullKey) {
        String indexAssignment;
        if (!cxt.isNullKey()) {
            String modulo;
            if (isLHash(cxt)) {
                modulo = capacityAssigned ? " & capacityMask" :
                        (" & (capacityMask = " + keys + ".length - 1)");
            } else {
                modulo = capacityAssigned ? " % capacity" :
                        (" % (capacity = " + keys + ".length)");
            }
            String hashAssignment = isLHash(cxt) ? keyHash(cxt, key, distinctNullKey) :
                    positiveKeyHash(cxt, key, distinctNullKey);
            if (isDHash(cxt)) {
                hashAssignment = "(hash = " + hashAssignment + ")";
            } else {
                hashAssignment = "(" + hashAssignment + ")";
            }
            indexAssignment = "index = " + hashAssignment + modulo;
        } else {
            indexAssignment = "index = 0";
        }
        return keys + "[" + indexAssignment + "]";
    }

    private static String positiveKeyHash(MethodContext cxt, String key, boolean distinctNullKey) {
        if (distinctNullKey && cxt.isNullKey()) {
            return "0";
        } if (cxt.isObjectOrNullKey()) {
            return (distinctNullKey ? "keyHashCode" : "nullableKeyHashCode") +
                    "(" + key + ") & Integer.MAX_VALUE";
        } else {
            PrimitiveType keyOption = (PrimitiveType) cxt.keyOption();
            switch (keyOption) {
                case BYTE: return key + " & BYTE_MASK";
                case SHORT: return key + " & SHORT_MASK";
                case CHAR: return key;
                case INT:
                case FLOAT:
                    return key + " & Integer.MAX_VALUE";
                case LONG:
                case DOUBLE:
                    return format("((int) (%s ^ (%s >>> 32))) & Integer.MAX_VALUE", key, key);
                default:
                    throw new IllegalStateException();
            }
        }
    }

    static int innerLoopBodies(MethodContext cxt) {
        if (isDHash(cxt) || isLHash(cxt)) return 1;
        assertHash(cxt, isQHash(cxt));
        return 2;
    }

    abstract static class InnerLoop {

        static interface Body {
            void generate(String index);
        }

        final MethodGenerator g;
        final MethodContext cxt;
        final Body body;

        InnerLoop(MethodGenerator g, MethodContext cxt, Body body) {
            this.g = g;
            this.cxt = cxt;
            this.body = body;
        }

        abstract void generate();
    }

    private static class DHashInnerLoop extends InnerLoop {
        final boolean stepPrecomputed;

        DHashInnerLoop(MethodGenerator g, MethodContext cxt, Body body, boolean stepPrecomputed) {
            super(g, cxt, body);
            this.stepPrecomputed = stepPrecomputed;
        }

        @Override
        void generate() {
            if (!stepPrecomputed) {
                g.lines("int step = (hash % (capacity - 2)) + 1;");
            }
            g.lines("while (true)").block(); {
                if (!cxt.isNullKey()) {
                    g.lines("if ((index -= step) < 0) index += capacity; // nextIndex");
                } else {
                    g.lines("index++;");
                }
                body.generate("index");
            } g.blockEnd();
        }
    }

    private static class LHashInnerLoop extends InnerLoop {

        LHashInnerLoop(MethodGenerator g, MethodContext cxt, Body body) {
            super(g, cxt, body);
        }

        @Override
        void generate() {
            g.lines("while (true)").block(); {
                g.lines("index = (index - 1) & capacityMask;");
                body.generate("index");
            } g.blockEnd();
        }
    }

    private static class QHashInnerLoop extends InnerLoop {

        QHashInnerLoop(MethodGenerator g, MethodContext cxt, Body body) {
            super(g, cxt, body);
        }

        @Override
        void generate() {
            g.lines("int bIndex = index, fIndex = index, step = 1;");
            g.lines("while (true)").block(); {
                g.lines("if ((bIndex -= step) < 0) bIndex += capacity;");
                body.generate("bIndex");
                // This way of wrapping capacity is less clear and a bit slower than
                // the method from indexTernaryStateUnsafeIndexing(), but it protects
                // from possible int overflow issues
                g.lines("int t;");
                g.lines("if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;");
                body.generate("fIndex");
                g.lines("step += 2;");
            } g.blockEnd();
        }
    }

    private KeySearch() {}
}
