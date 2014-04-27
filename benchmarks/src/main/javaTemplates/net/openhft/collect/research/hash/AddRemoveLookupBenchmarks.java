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

package net.openhft.collect.research.hash;

import net.openhft.benchmarks.DimensionedJmh;
import net.openhft.collect.impl.hash.DHashCapacities;
import net.openhft.collect.impl.hash.QHashCapacities;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.CommandLineOptionException;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import static net.openhft.collect.research.hash.LookupBenchmarks.n;


@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Threads(1)
@Fork(1)
@Warmup(iterations = 5)
@Measurement(iterations = 10)
public class AddRemoveLookupBenchmarks {

    static final int SMALL_CAPACITY = 1024, LARGE_CAPACITY = SMALL_CAPACITY * 16;
    static final int CAPACITY = Integer.getInteger("capacity", SMALL_CAPACITY);
    static final int FULL_RENEWALS_PER_INVOCATION =
            Integer.getInteger("fullRenewalsPerInvocation", 100);
    static final int LOOKUPS_PER_INSERTION = Integer.getInteger("lookupsPerInsertion", 4);

    static final double LOAD_FACTOR = parseDouble(System.getProperty("loadFactor", "0.5"));
    static final double QHASH_REHASH_LOAD =
            0.54 + 0.816 * LOAD_FACTOR - 0.363 * (LOAD_FACTOR * LOAD_FACTOR);
    static final double DHASH_REHASH_LOAD =
            0.6 + 0.671 * LOAD_FACTOR - 0.274 * (LOAD_FACTOR * LOAD_FACTOR);

    static final int SIZE = n(CAPACITY, LOAD_FACTOR);

    static final int L_HASH_CAPACITY = CAPACITY;
    static final int R_HOOD_SIMPLE_HASH_CAPACITY = CAPACITY;
    static final int D_HASH_CAPACITY = DHashCapacities.bestCapacity(SIZE, LOAD_FACTOR, 0);
    static final int Q_HASH_CAPACITY =
            QHashCapacities.getIntCapacity(((int) (SIZE / LOAD_FACTOR)) + 1, 0);

    /* with char|byte|short|int|long key QHash|DHash|LHash|RHoodSimpleHash hash */

    @State(Scope.Thread)
    public static class QHashCharSetState {
        Random r;
        char[] keys;
        char[] lookupKeys;
        NoStatesQHashCharSet set;

        @Setup(Level.Trial)
        public void allocate() {
            r = ThreadLocalRandom.current();
            keys = new char[SIZE * (FULL_RENEWALS_PER_INVOCATION + 1)];
            lookupKeys = new char[SIZE * FULL_RENEWALS_PER_INVOCATION * LOOKUPS_PER_INSERTION];
            set = new NoStatesQHashCharSet(Q_HASH_CAPACITY);
        }

        @Setup(Level.Invocation)
        public void fill() {
            set.clear();
            int i = 0;
            char[] keys = new char[SIZE];
            while (set.size < SIZE) {
                char key;
                while (!set.addBinaryStateSimpleIndexing(key = (char) r.nextLong()));
                keys[i++] = key;
            }
            LookupBenchmarks.shuffle(keys, r);
            System.arraycopy(keys, 0, this.keys, 0, SIZE);
            int j = 0;
            while (i < this.keys.length) {
                this.keys[i++] = (char) r.nextLong();
                for (int k = 0; k < LOOKUPS_PER_INSERTION; k++) {
                    lookupKeys[j++] = this.keys[i - 1 - r.nextInt(SIZE)];
                }
            }
        }

        @TearDown(Level.Trial)
        public void recycle() {
            keys = null;
            set = null;
        }
    }

    /* with Simple|Unsafe indexing */
    /* if QHash|DHash hash */

    @GenerateMicroBenchmark
    public int addRemoveLookup_qHash_charKey_simpleIndexing(QHashCharSetState state) {
        int freeSlotsRehashThreshold = (int) (Q_HASH_CAPACITY * (1.0 - QHASH_REHASH_LOAD));
        int removeI = 0, insertI = SIZE;
        NoStatesQHashCharSet set = state.set;
        char[] keys = state.keys;
        char[] lookupKeys = state.lookupKeys;
        int lookupI = 0;
        int lookupDummy = 0;
        int keysLen = keys.length;
        while (insertI < keysLen) {
            set.removeSimpleIndexing(keys[removeI++]);
            set.addTernaryStateSimpleIndexing(keys[insertI++]);
            for (int i = 0; i < LOOKUPS_PER_INSERTION; i++) {
                lookupDummy ^= set.indexTernaryStateSimpleIndexing(lookupKeys[lookupI++]);
            }
            if (set.freeSlots <= freeSlotsRehashThreshold)
                set.rehashSimpleIndexing(Q_HASH_CAPACITY);
        }
        return set.size ^ lookupDummy;
    }

    /* elif LHash|RHoodSimpleHash hash */

    @GenerateMicroBenchmark
    public int addRemoveLookup_qHash_charKey_simpleIndexing(QHashCharSetState state) {
        int removeI = 0, insertI = SIZE;
        NoStatesQHashCharSet set = state.set;
        char[] keys = state.keys;
        char[] lookupKeys = state.lookupKeys;
        int lookupI = 0;
        int lookupDummy = 0;
        int keysLen = keys.length;
        while (insertI < keysLen) {
            set.removeSimpleIndexing(keys[removeI++]);
            set.addBinaryStateSimpleIndexing(keys[insertI++]);
            for (int i = 0; i < LOOKUPS_PER_INSERTION; i++) {
                lookupDummy ^= set.indexBinaryStateSimpleIndexing(lookupKeys[lookupI++]);
            }
        }
        return set.size ^ lookupDummy;
    }

    /* endif */
    /* endwith */
    /* endwith */

    public static void main(String[] args) throws RunnerException, CommandLineOptionException {
        new DimensionedJmh(AddRemoveLookupBenchmarks.class)
                .addArgDim("capacity", SMALL_CAPACITY, LARGE_CAPACITY)
                .addArgDim("fullRenewalsPerInvocation", 100)
                .addArgDim("lookupsPerInsertion", 4)
                .addArgDim("loadFactor", "0.3", "0.6", "0.9")
                .withGetOperationCount(options -> {
                    int size = n(parseInt(options.get("capacity")),
                            parseDouble(options.get("loadFactor")));
                    return size * parseInt(options.get("fullRenewalsPerInvocation"));
                })
                .run(args);
    }
}
