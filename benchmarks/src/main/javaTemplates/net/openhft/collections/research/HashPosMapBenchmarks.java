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

package net.openhft.collections.research;

import net.openhft.collect.set.CharSet;
import net.openhft.collect.set.hash.HashCharSets;
import net.openhft.function.IntConsumer;
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
@Warmup(iterations=5, time=3, timeUnit=TimeUnit.SECONDS)
@Measurement(iterations = 10)
public class HashPosMapBenchmarks {

    public static final int SMALL_CAPACITY = 2048, LARGE_CAPACITY = 1 << 16;
    public static final int CAPACITY = Integer.getInteger("capacity", LARGE_CAPACITY);
    public static final double LOAD_FACTOR =
            Double.parseDouble(System.getProperty("loadFactor", "0.6"));
    public static final int N = (int) (CAPACITY * LOAD_FACTOR);

    /* with Simple|Tiered1|Tiered2 flavor */

    @State(Scope.Thread)
    public static class SimpleState {

        Random r = ThreadLocalRandom.current();
        CharSet keySet = HashCharSets.newMutableSet(N);
        public char[] keys;
        public SimpleVanillaShortShortMultiMap map;

        @Setup(Level.Trial)
        public void allocate() {
            keys = new char[N];
            map = new SimpleVanillaShortShortMultiMap(CAPACITY);
        }

        @Setup(Level.Iteration)
        public void fill() {
            map.clear();
            keySet.clear();
            while (keySet.size() < N) {
                char key = (char) r.nextLong();
                if (keySet.add(key)) {
                    map.put(key, 0);
                }
            }
            keySet.toArray(keys);
        }

        @TearDown(Level.Trial)
        public void recycle() {
            keySet = null;
            keys = null;
            map = null;
        }
    }

    @GenerateMicroBenchmark
    public int forEachValueCommonSink_simple_present(SimpleState st) {
        SinkConsumer sink = new SinkConsumer();
        SimpleVanillaShortShortMultiMap map = st.map;
        for (char key : st.keys) {
            map.forEachValue(key, sink);
        }
        return sink.x;
    }

    @GenerateMicroBenchmark
    public int forEachValueLocalSink_simple_present(SimpleState st) {

        int x = 0;
        SimpleVanillaShortShortMultiMap map = st.map;
        for (char key : st.keys) {
            SinkConsumer sink = new SinkConsumer();
            map.forEachValue(key, sink);
            x ^= sink.x;
        }
        return x;
    }

    @GenerateMicroBenchmark
    public int forEachValueStatelessSink_simple_present(SimpleState st) {
        final int[] x = new int[1];
        SimpleVanillaShortShortMultiMap map = st.map;
        for (char key : st.keys) {
            SinkConsumer sink = new SinkConsumer();
            map.forEachValue(key, new IntConsumer() {
                @Override
                public void accept(int value) {
                    x[0] ^= value;
                }
            });
        }
        return x[0];
    }

    @GenerateMicroBenchmark
    public int searchPos_simple_present(SimpleState st) {
        int x = 0;
        SimpleVanillaShortShortMultiMap map = st.map;
        for (char key : st.keys) {
            map.startSearch(x);
            int pos;
            while ((pos = map.nextPos()) > 0) {
                x ^= pos;
            }
        }
        return x;
    }

    /* endwith */

    static class SinkConsumer implements IntConsumer {
        int x = 0;
        @Override
        public void accept(int v) {
            x ^= v;
        }
    }



    public static void main(String[] args) throws RunnerException, CommandLineOptionException {
        boolean headerPrinted = false;
        CommandLineOptions commandLineOptions = new CommandLineOptions(args);
        Locale.setDefault(Locale.ENGLISH);
        for (int capacity : new int[] {SMALL_CAPACITY, LARGE_CAPACITY}) {
            for (String loadFactor : new String[] {"0.25", "0.5", "0.75"}) {
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
                    String[] dims = "capacity loadFactor op algo queryResult"
                            .split(" ");
                    for (String dim : dims) {
                        System.out.printf("%-25s", dim);
                    }
                    System.out.printf(":%8s%8s\n", "mean", "std");
                    headerPrinted = true;
                }

                for (Map.Entry<BenchmarkRecord, RunResult> e : results.entrySet()) {
                    String[] parts = e.getKey().getUsername().split("\\.");
                    String methodName = parts[parts.length - 1];
                    parts = methodName.split("_");
                    StringBuilder dims = new StringBuilder();
                    dims.append(String.format("%-25d", capacity));
                    dims.append(String.format("%-25s", loadFactor));
                    for (int i = 0; i < parts.length; i++) {
                        String[] camelCaseParts = parts[i].split("(?<!^)(?=[A-Z])");
                        int lastPart = camelCaseParts.length == 1 ? 1 : camelCaseParts.length - 1;
                        String value = "";
                        for (int j = 0; j < lastPart; j++) {
                            value += camelCaseParts[j];
                        }
                        dims.append(String.format("%-25s", value));
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
}
