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

package net.openhft.koloboke.collect.research.hash;

import net.openhft.bench.DimensionedJmh;
import net.openhft.koloboke.collect.impl.hash.DHashCapacities;
import net.openhft.koloboke.collect.impl.hash.QHashCapacities;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.CommandLineOptionException;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static java.lang.Double.parseDouble;


@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Threads(1)
@Fork(1)
@Warmup(iterations = 5)
@Measurement(iterations = 10)
public class AddRemoveWithTombstonesBenchmarks {

    static final int SMALL_SIZE = 2000, LARGE_SIZE = 1000000;
    static final int SIZE = Integer.getInteger("size", SMALL_SIZE);
    static final int LOOKUPS_PER_INSERTION = Integer.getInteger("lookupsPerInsertion", 4);

    static final double LOAD_FACTOR = parseDouble(System.getProperty("loadFactor", "0.5"));
    static final double REHASH_LOAD = parseDouble(System.getProperty("rehashLoad", "0.75"));

    static int addRemovesToRehashOnce(double targetLoad, double rehashLoad, int capacity) {
        // Expected theoretical number of add-remove pairs to reach rehash load
        // is (1 - lf) / (1 - rl), x2 for reliability :)
        return (int) ((1.0 - targetLoad) / (1.0 - rehashLoad) * 2.0 * (double) capacity);
    }

    /* with QHash|DHash hash */
    static final int Q_HASH_CAPACITY =
            QHashCapacities.nearestGreaterCapacity(((int) ((double) SIZE / LOAD_FACTOR)) + 1, 0);
    static final int Q_HASH_ADD_REMOVES =
            addRemovesToRehashOnce(LOAD_FACTOR, REHASH_LOAD, Q_HASH_CAPACITY);
    /* endwith */

    static int freeSlotsRehashThreshold(double rehashLoad, int capacity) {
        return (int) ((1.0 - rehashLoad) * (double) capacity);
    }

    /* with char|byte|short|int|long key QHash|DHash hash */

    @AuxCounters
    @State(Scope.Thread)
    public static class QHashCharSetState {
        Random r;
        char[] keys;
        char[] lookupKeys;
        NoStatesQHashCharSet set;
        public long operationsPerIteration = 0L;

        @Setup(Level.Trial)
        public void allocate() {
            r = ThreadLocalRandom.current();
            keys = new char[SIZE + Q_HASH_ADD_REMOVES];
            lookupKeys = new char[Q_HASH_ADD_REMOVES * LOOKUPS_PER_INSERTION];
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

        @Setup(Level.Iteration)
        public void resetOperationsPerInvocation() {
            operationsPerIteration = 0L;
        }

        @TearDown(Level.Trial)
        public void recycle() {
            keys = null;
            set = null;
        }
    }

    @Benchmark
    public int addRemove_qHash_charKey_withoutLookups(QHashCharSetState state) {
        int freeSlotsRehashThreshold = freeSlotsRehashThreshold(REHASH_LOAD, Q_HASH_CAPACITY);
        int i = 0, j = SIZE;
        NoStatesQHashCharSet set = state.set;
        char[] keys = state.keys;
        int keysLen = keys.length;
        while (j < keysLen) {
            set.removeSimpleIndexing(keys[i++]);
            set.addTernaryStateSimpleIndexing(keys[j++]);
            if (set.freeSlots <= freeSlotsRehashThreshold) {
                set.rehashSimpleIndexing(Q_HASH_CAPACITY);
                state.operationsPerIteration += i;
                return set.size;
            }
        }
        throw new IllegalStateException();
    }

    @Benchmark
    public int addRemove_qHash_charKey_withLookups(QHashCharSetState state) {
        int freeSlotsRehashThreshold = freeSlotsRehashThreshold(REHASH_LOAD, Q_HASH_CAPACITY);
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
                lookupDummy ^= set.indexTernaryStateUnsafeIndexing(lookupKeys[lookupI++]);
            }
            if (set.freeSlots <= freeSlotsRehashThreshold) {
                set.rehashSimpleIndexing(Q_HASH_CAPACITY);
                state.operationsPerIteration += removeI;
                return set.size ^ lookupDummy;
            }
        }
        throw new IllegalStateException();
    }

    /* endwith */

    public static void main(String[] args) throws RunnerException, CommandLineOptionException {
        new DimensionedJmh(AddRemoveWithTombstonesBenchmarks.class)
                .addArgDim("size", SMALL_SIZE, LARGE_SIZE)
                .addArgDim("lookupsPerInsertion", 4)
                .addArgDim("loadFactor")
                .addArgDim("rehashLoad")
                .dynamicOperationsPerIteration()
                .run(args);
    }
}
