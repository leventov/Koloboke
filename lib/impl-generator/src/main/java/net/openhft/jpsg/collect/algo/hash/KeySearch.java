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
            dHashStep(g, cxt);
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

    static String curAssignment(MethodContext cxt, String key, boolean capacityAssigned) {
        return "(cur = " + firstKey(cxt, "tab", "keys", key, capacityAssigned, true, false) + ")";
    }

    static String firstKey(MethodContext cxt, String table, String keys, String key,
            boolean capacityAssigned, boolean distinctNullKey, boolean readKeyOnly) {
        String indexAssignment;
        if (!cxt.isNullKey()) {
            String modulo;
            if (isLHash(cxt)) {
                modulo = capacityAssigned ? " & capacityMask" :
                        (" & (capacityMask = " + capacityMask(cxt, parallelKV(cxt) ? table : keys) +
                                ")");
            } else {
                modulo = capacityAssigned ? " % capacity" :
                        (" % (capacity = " + (parallelKV(cxt) ? table : keys) + ".length)");
            }
            String hashAssignment = keyHash(cxt, key, distinctNullKey);
            if (isDHash(cxt))
                hashAssignment = "(hash = " + hashAssignment + ")";
            indexAssignment = "index = " + hashAssignment + modulo;
        } else {
            indexAssignment = "index = 0";
        }
        if (readKeyOnly) {
            return readKeyOnly(cxt, table, keys, indexAssignment);
        } else {
            return readKeyOrEntry(cxt, table, keys, indexAssignment);
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
            if (!stepPrecomputed)
                dHashStep(g, cxt);
            g.lines("while (true)").block(); {
                if (!cxt.isNullKey()) {
                    g.lines("if ((index -= step) < 0) index += capacity; // nextIndex");
                } else {
                    String increment = doubleSizedParallel(cxt) ? " += 2" : "++";
                    g.lines("index" + increment + ";");
                }
                body.generate("index");
            } g.blockEnd();
        }
    }

    private static void dHashStep(MethodGenerator g, MethodContext cxt) {
        String step = "(hash % (capacity - " + slots(2, cxt) + ")) + " + slots(1, cxt);
        g.lines("int step = " + step + ";");
    }

    private static class LHashInnerLoop extends InnerLoop {

        LHashInnerLoop(MethodGenerator g, MethodContext cxt, Body body) {
            super(g, cxt, body);
        }

        @Override
        void generate() {
            g.lines("while (true)").block(); {
                g.lines("index = (index - " + slots(1, cxt) + ") & capacityMask;");
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
            g.lines("int bIndex = index, fIndex = index, step = " + slots(1, cxt) + ";");
            g.lines("while (true)").block(); {
                g.lines("if ((bIndex -= step) < 0) bIndex += capacity;");
                body.generate("bIndex");
                // This way of wrapping capacity is less clear and a bit slower than
                // the method from indexTernaryStateUnsafeIndexing(), but it protects
                // from possible int overflow issues
                g.lines("int t;");
                g.lines("if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;");
                body.generate("fIndex");
                g.lines("step += " + slots(2, cxt) + ";");
            } g.blockEnd();
        }
    }

    private KeySearch() {}
}
