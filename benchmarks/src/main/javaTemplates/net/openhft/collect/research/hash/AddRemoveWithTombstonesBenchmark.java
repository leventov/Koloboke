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
public class AddRemoveWithTombstonesBenchmark {

    static final int SMALL_SIZE = 2000, LARGE_SIZE = 20000;
    static final int SIZE = Integer.getInteger("size", SMALL_SIZE);
    static final int FULL_RENEWALS_PER_INVOCATION =
            Integer.getInteger("fullRenewalsPerInvocation", 100);

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
        NoStatesQHashCharSet set;

        @Setup(Level.Trial)
        public void allocate() {
            r = ThreadLocalRandom.current();
            keys = new char[SIZE * (FULL_RENEWALS_PER_INVOCATION + 1)];
            set = new NoStatesQHashCharSet(QHASH_CAPACITY);
        }

        @Setup(Level.Invocation)
        public void fill() {
            set.clear();
            int i = 0;
            char[] keys = new char[SIZE];
            while (set.size < SIZE) {
                char key;
                while (!set.addBinaryState(key = (char) r.nextLong()));
                keys[i++] = key;
            }
            HashBenchmarks.shuffle(keys, r);
            System.arraycopy(keys, 0, this.keys, 0, SIZE);
            while (i < this.keys.length) {
                this.keys[i++] = (char) r.nextLong();
            }
        }

        @TearDown(Level.Trial)
        public void recycle() {
            keys = null;
            set = null;
        }
    }

    @GenerateMicroBenchmark
    public int addRemove_qHash_charKey(QHashCharSetState state) {
        int freeSlotsRehashThreshold = (int) (QHASH_CAPACITY * (1.0 - REHASH_LOAD));
        int i = 0, j = SIZE;
        NoStatesQHashCharSet set = state.set;
        char[] keys = state.keys;
        int keysLen = keys.length;
        while (j < keysLen) {
            set.remove(keys[i++]);
            set.addTernaryState(keys[j++]);
            if (set.freeSlots <= freeSlotsRehashThreshold)
                set.rehash(QHASH_CAPACITY);
        }
        return set.size;
    }

    /* endwith */

    public static void main(String[] args) throws RunnerException, CommandLineOptionException {
        new DimensionedJmh(AddRemoveWithTombstonesBenchmark.class)
                .addArgDim("size", SMALL_SIZE, LARGE_SIZE)
                .addArgDim("fullRenewalsPerInvocation", 100)
                .addArgDim("loadFactor")
                .addArgDim("rehashLoad")
                .withGetOperationCount(options ->
                        parseInt(options.get("size")) *
                                parseInt(options.get("fullRenewalsPerInvocation")))
                .run(args);
    }
}
