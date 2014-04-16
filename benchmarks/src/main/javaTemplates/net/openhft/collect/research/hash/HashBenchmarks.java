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
import net.openhft.collect.impl.hash.*;
import net.openhft.collect.set.*;
import net.openhft.collect.set.hash.*;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.*;
import org.openjdk.jmh.runner.options.*;

import java.util.*;
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
public class HashBenchmarks {

    static class Config {
        public final int powerOf2Capacity, primeCapacity, n;
        Config(int powerOf2Capacity, double loadFactor) {
            this.powerOf2Capacity = powerOf2Capacity;
            this.n = (int) (powerOf2Capacity * loadFactor);
            this.primeCapacity = DHashCapacities.bestCapacity(n, loadFactor, 0);
        }
    }

    static final int SMALL_CAPACITY = 1024, LARGE_CAPACITY = (1 << 20);
    static final int CAPACITY = Integer.getInteger("capacity", LARGE_CAPACITY);
    static final double LOAD_FACTOR = parseDouble(System.getProperty("loadFactor", "0.6"));
    static final Config CONF = new Config(CAPACITY, LOAD_FACTOR);

    static int n(int capacity, double loadFactor) {
        return (int) (capacity * loadFactor);
    }
    public static final int N = n(CAPACITY, LOAD_FACTOR);


    static double harmonic(int n, double power) {
        double h = 0.0;
        for (int i = 1; i <= n; i++) {
            h += 1.0 / Math.pow(i, power);
        }
        return h;
    }

    /* with char|byte|short|int|long key */

    static void shuffle(char[] a, Random r) {
        for (int i = a.length - 1; i > 0; i--) {
            int index = r.nextInt(i + 1);
            char t = a[index];
            a[index] = a[i];
            a[i] = t;
        }
    }

    /* with Bit|Byte|ByteAlong|No states LHash|LSelfAdjHash|DHash|RHoodSimpleHash|QHash hash */
    /* if !(Bit states DHash hash) &&
          !(Bit|Byte|ByteAlong states RHoodSimpleHash|QHash|LSelfAdjHash hash) */

    @State(Scope.Thread)
    public static class BitStatesLHashCharsUniformQueries {
        Random r;
        CharSet keySet;
        CharSet notKeySet;
        public char[] keys;
        public char[] notKeys;
        public BitStatesLHashCharSet set;

        @Setup(Level.Trial)
        public void allocate() {
            r = ThreadLocalRandom.current();
            keySet = HashCharSets.newMutableSet(N);
            notKeySet = HashCharSets.newMutableSet(N);
            keys = new char[N];
            notKeys = new char[N];
            set = new BitStatesLHashCharSet(
                    CONF./* if LHash|LSelfAdjHash|RHoodSimpleHash hash */powerOf2Capacity
                         /* elif DHash|QHash hash //primeCapacity// endif */);
        }

        @Setup(Level.Iteration)
        public void fill() {
            set.clear();
            keySet.clear();
            notKeySet.clear();
            while (set.size < N) {
                char key = (char) r.nextLong();
                if (set.addBinaryState(key)) {
                    keySet.add(key);
                }
            }
            keySet.toArray(keys);
            while (notKeySet.size() < N) {
                char key = (char) r.nextLong();
                if (!keySet.contains(key))
                    notKeySet.add(key);
            }
            notKeySet.toArray(notKeys);
        }

        @TearDown(Level.Trial)
        public void recycle() {
            keySet = notKeySet = null;
            keys = notKeys = null;
            set = null;
        }
    }

    public static abstract class BitStatesLHashCharsDistributedQueries {
        Random r;
        CharSet keySet;
        public char[] keys;
        public BitStatesLHashCharSet set;

        @Setup(Level.Trial)
        public void allocate() {
            r = ThreadLocalRandom.current();
            keySet = HashCharSets.newMutableSet(N);
            keys = new char[N];
            set = new BitStatesLHashCharSet(
                    CONF./* if LHash|LSelfAdjHash|RHoodSimpleHash hash */powerOf2Capacity
                         /* elif DHash|QHash hash //primeCapacity// endif */);
        }

        @Setup(Level.Iteration)
        public void fill() {
            set.clear();
            keySet.clear();
            int order = 1;
            int i = 0;
            while (i < N) {
                char key;
                while (!set.addBinaryState(key = (char) r.nextLong()));
                keySet.add(key);
                int count = Math.max((int) (count(order) + 0.55), 1);
                int limit = Math.min(N, i + count);
                for (; i < limit; i++) {
                    keys[i] = key;
                }
                order++;
            }
            shuffle(keys, r);
            while (set.size < N) {
                char key = (char) r.nextLong();
                if (set.addBinaryState(key))
                    keySet.add(key);
            }
            // Don't give advantage to early generated keys
            set.clear();
            char[] keys = keySet.toCharArray();
            shuffle(keys, r);
            for (char key : keys) {
                set.addBinaryState(key);
            }
        }

        abstract double count(int order);

        @TearDown(Level.Trial)
        public void recycle() {
            keySet = null;
            keys = null;
            set = null;
        }
    }

    // All the following formulas taken from TAoCP Vol. 3 chapter 6.1

    @State(Scope.Thread)
    public static class BitStatesLHashCharsZipfQueries
            extends BitStatesLHashCharsDistributedQueries {
        static final double C = N / harmonic(N, 1.0);
        @Override
        double count(int order) {
            return C / order;
        }
    }

    @State(Scope.Thread)
    public static class BitStatesLHashCharsParetoQueries
            extends BitStatesLHashCharsDistributedQueries {
        static final double PARETO_THETA = Math.log(0.80) / Math.log(0.20);
        static final double C = N / harmonic(N, 1.0 - PARETO_THETA);

        @Override
        double count(int order) {
            return C / Math.pow(order, 1.0 - PARETO_THETA);
        }
    }
    // End of TAoCP formulas

    /* with Binary|Ternary state Simple|Unsafe indexing Uniform|Zipf|Pareto queries */
    /* if !(Bit states Ternary state) && !(LHash|LSelfAdjHash|RHoodSimpleHash hash Ternary state) &&
          !(ByteAlong states Simple indexing) */

    @GenerateMicroBenchmark
    public
    int lookup_binaryArity_lHash_bitStates_presentQueryResult_uniformQueries_charKey_simpleIndexing(
            BitStatesLHashCharsUniformQueries s) {
        int x = 0;
        BitStatesLHashCharSet set = s.set;
        for (char key : s.keys) {
            x ^= set.indexBinaryStateSimpleIndexing(key);
        }
        return x;
    }

    /* if Uniform queries */
    @GenerateMicroBenchmark
    public
    int lookup_binaryArity_lHash_bitStates_absentQueryResult_uniformQueries_charKey_simpleIndexing(
            BitStatesLHashCharsUniformQueries s) {
        int x = 0;
        BitStatesLHashCharSet set = s.set;
        for (char key : s.notKeys) {
            x ^= set.indexBinaryStateSimpleIndexing(key);
        }
        return x;
    }
    /* endif */

    /* endif */
    /* endwith */

    /* endif */
    /* endwith */


    /* endwith */

    public static void main(String[] args) throws RunnerException, CommandLineOptionException {
        new DimensionedJmh(HashBenchmarks.class)
                .addArgDim("loadFactor", "0.3", "0.6", "0.9")
                .addArgDim("capacity", SMALL_CAPACITY, LARGE_CAPACITY)
                .withGetOperationCount(options -> (long) n(parseInt(options.get("capacity")),
                        parseDouble(options.get("loadFactor"))))
                .run(args);
    }
}
