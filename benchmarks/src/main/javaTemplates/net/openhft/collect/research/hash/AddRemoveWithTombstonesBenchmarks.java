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


@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Threads(1)
@Fork(1)
@Warmup(iterations = 5)
@Measurement(iterations = 10)
public class AddRemoveWithTombstonesBenchmarks {

    static final int SMALL_SIZE = 2000, LARGE_SIZE = 20000;
    static final int SIZE = Integer.getInteger("size", SMALL_SIZE);
    static final int FULL_RENEWALS_PER_INVOCATION =
            Integer.getInteger("fullRenewalsPerInvocation", 100);
    static final int LOOKUPS_PER_INSERTION = Integer.getInteger("lookupsPerInsertion", 4);

    static final double LOAD_FACTOR = parseDouble(System.getProperty("loadFactor", "0.5"));
    static final double REHASH_LOAD = parseDouble(System.getProperty("rehashLoad", "0.75"));

    static final int DHASH_CAPACITY = DHashCapacities.bestCapacity(SIZE, LOAD_FACTOR, 0);
    static final int QHASH_CAPACITY =
            QHashCapacities.getIntCapacity(((int)(SIZE / LOAD_FACTOR)) + 1, 0);

    /* with char|byte|short|int|long key QHash|DHash hash */

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
            set = new NoStatesQHashCharSet(QHASH_CAPACITY);
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

    @GenerateMicroBenchmark
    public int addRemove_qHash_charKey_withoutLookups(QHashCharSetState state) {
        int freeSlotsRehashThreshold = (int) (QHASH_CAPACITY * (1.0 - REHASH_LOAD));
        int i = 0, j = SIZE;
        NoStatesQHashCharSet set = state.set;
        char[] keys = state.keys;
        int keysLen = keys.length;
        while (j < keysLen) {
            set.removeSimpleIndexing(keys[i++]);
            set.addTernaryStateSimpleIndexing(keys[j++]);
            if (set.freeSlots <= freeSlotsRehashThreshold)
                set.rehashSimpleIndexing(QHASH_CAPACITY);
        }
        return set.size;
    }

    @GenerateMicroBenchmark
    public int addRemove_qHash_charKey_withLookups(QHashCharSetState state) {
        int freeSlotsRehashThreshold = (int) (QHASH_CAPACITY * (1.0 - REHASH_LOAD));
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
            if (set.freeSlots <= freeSlotsRehashThreshold)
                set.rehashSimpleIndexing(QHASH_CAPACITY);
        }
        return set.size ^ lookupDummy;
    }

    /* endwith */

    public static void main(String[] args) throws RunnerException, CommandLineOptionException {
        new DimensionedJmh(AddRemoveWithTombstonesBenchmarks.class)
                .addArgDim("size", SMALL_SIZE, LARGE_SIZE)
                .addArgDim("fullRenewalsPerInvocation", 100)
                .addArgDim("lookupsPerInsertion", 4)
                .addArgDim("loadFactor")
                .addArgDim("rehashLoad")
                .withGetOperationsPerInvocation(options ->
                        parseInt(options.get("size")) *
                                parseInt(options.get("fullRenewalsPerInvocation")))
                .run(args);
    }
}
