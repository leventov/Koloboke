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

import net.openhft.collect.impl.hash.*;
import net.openhft.collect.set.*;
import net.openhft.collect.set.hash.*;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.logic.results.RunResult;
import org.openjdk.jmh.runner.*;
import org.openjdk.jmh.runner.options.*;
import org.openjdk.jmh.runner.parameters.TimeValue;
import org.openjdk.jmh.util.internal.Statistics;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;


@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Threads(1)
@Fork(1)
@Warmup(iterations = 5)
@Measurement(iterations = 10)
public class HashBenchmarks {

    public static class Config {
        public final int powerOf2Capacity, primeCapacity, n;
        Config(int powerOf2Capacity, double loadFactor) {
            this.powerOf2Capacity = powerOf2Capacity;
            this.n = (int) (powerOf2Capacity * loadFactor);
            this.primeCapacity = DHashCapacities.bestCapacity(n, loadFactor, 0);
        }
    }

    public static final int SMALL_CAPACITY = 1024, LARGE_CAPACITY = (1 << 20);
    public static final int CAPACITY = Integer.getInteger("capacity", LARGE_CAPACITY);
    public static final double LOAD_FACTOR =
            Double.parseDouble(System.getProperty("loadFactor", "0.6"));
    public static final Config CONF = new Config(CAPACITY, LOAD_FACTOR);
    public static final int N = (int) (CAPACITY * LOAD_FACTOR);


    /* with char|byte|short|int|long key */


    /* with Bit|Byte|ByteAlong|No states LHash|DHash|RHoodSimpleHash hash */
    /* if !(Bit states DHash hash) && !(Bit|Byte|ByteAlong states RHoodSimpleHash hash) */

    @State(Scope.Thread)
    public static class BitStatesLHashChars {
        Random r = ThreadLocalRandom.current();
        CharSet keySet = HashCharSets.newMutableSet(N);
        CharSet notKeySet = HashCharSets.newMutableSet(N);
        public char[] keys;
        public char[] notKeys;
        public BitStatesLHashCharSet set;

        @Setup(Level.Trial)
        public void allocate() {
            keys = new char[N];
            notKeys = new char[N];
            set = new BitStatesLHashCharSet(CONF./* if LHash|RHoodSimpleHash hash */powerOf2Capacity
                    /* elif DHash hash //primeCapacity// endif */);
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

    /* with Binary|Ternary state Simple|Unsafe indexing */
    /* if !(Bit states Ternary state) && !(LHash|RHoodSimpleHash hash Ternary state) &&
          !(ByteAlong states Simple indexing) */

    @GenerateMicroBenchmark
    public int lookup_binary_lHash_bitStates_present_charKey_simpleIndexing(BitStatesLHashChars s) {
        int x = 0;
        BitStatesLHashCharSet set = s.set;
        for (char key : s.keys) {
            x ^= set.indexBinaryStateSimpleIndexing(key);
        }
        return x;
    }

    @GenerateMicroBenchmark
    public int lookup_binary_lHash_bitStates_absent_charKey_simpleIndexing(BitStatesLHashChars s) {
        int x = 0;
        BitStatesLHashCharSet set = s.set;
        for (char key : s.notKeys) {
            x ^= set.indexBinaryStateSimpleIndexing(key);
        }
        return x;
    }

    /* endif */
    /* endwith */

    /* endif */
    /* endwith */


    /* endwith */

    public static void main(String[] args) throws RunnerException, CommandLineOptionException {
        boolean headerPrinted = false;
        CommandLineOptions commandLineOptions = new CommandLineOptions(args);
        Locale.setDefault(Locale.ENGLISH);
        for (int capacity : new int[] {SMALL_CAPACITY, LARGE_CAPACITY}) {
            for (String loadFactor : new String[] {"0.3", "0.6", "0.9"}) {
                int n = (int) (capacity * Double.parseDouble(loadFactor));
                Options opt = new OptionsBuilder()
                        .parent(commandLineOptions)
                        .mode(Mode.AverageTime)
                        .timeUnit(TimeUnit.NANOSECONDS)
                        .measurementIterations(10)
                        .measurementTime(TimeValue.seconds(1L))
                        .warmupIterations(5)
                        .warmupTime(TimeValue.seconds(1L))
                        .forks(1)
                        .threads(1)
                        .jvmArgs("-Dcapacity=" + capacity, "-DloadFactor=" + loadFactor)
                        .build();

                SortedMap<BenchmarkRecord, RunResult> results = new Runner(opt).run();

                if (!headerPrinted) {
                    String[] dims = "capacity loadFactor arity algo states queryResult key indexing"
                            .split(" ");
                    for (String dim : dims) {
                        System.out.print(alignDimString(dim));
                    }
                    System.out.printf(":%8s%8s\n", "mean", "std");
                    headerPrinted = true;
                }

                for (Map.Entry<BenchmarkRecord, RunResult> e : results.entrySet()) {
                    String[] parts = e.getKey().getUsername().split("\\.");
                    String methodName = parts[parts.length - 1];
                    parts = methodName.split("_");
                    StringBuilder dims = new StringBuilder();
                    dims.append(alignDimNumber(capacity));
                    dims.append(alignDimString(loadFactor));
                    for (int i = 1; i < parts.length; i++) {
                        String[] camelCaseParts = parts[i].split("(?<!^)(?=[A-Z])");
                        int lastPart = camelCaseParts.length == 1 ? 1 : camelCaseParts.length - 1;
                        String value = "";
                        for (int j = 0; j < lastPart; j++) {
                            value += camelCaseParts[j];
                        }
                        dims.append(alignDimString(value));
                    }
                    System.out.print(dims.toString());
                    Statistics stats = e.getValue().getPrimaryResult().getStatistics();
                    double mean = stats.getMean() / n;
                    double std = stats.getStandardDeviation() / n;
                    System.out.printf(":%8.3f%8.3f\n", mean, std);
                }
            }
        }
    }

    private static String alignDimNumber(int n) {
        return String.format("%-12d", n);
    }

    private static String alignDimString(String name) {
        return String.format("%-12s", name);
    }
}
