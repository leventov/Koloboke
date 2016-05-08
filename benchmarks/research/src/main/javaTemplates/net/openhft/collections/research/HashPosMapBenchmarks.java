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

import com.koloboke.bench.DimensionedJmh;
import com.koloboke.collect.set.CharSet;
import com.koloboke.collect.set.hash.HashCharSets;
import com.koloboke.function.IntConsumer;
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
@Warmup(iterations=5, time=3, timeUnit=TimeUnit.SECONDS)
@Measurement(iterations = 10)
public class HashPosMapBenchmarks {

    public static final int SMALL_CAPACITY = 2048, LARGE_CAPACITY = 1 << 16;
    public static final int CAPACITY = Integer.getInteger("capacity", LARGE_CAPACITY);
    public static final double LOAD_FACTOR =
            Double.parseDouble(System.getProperty("loadFactor", "0.6"));
    public static final int N = (int) ((double) CAPACITY * LOAD_FACTOR);

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
            map = new SimpleVanillaShortShortMultiMap((long) CAPACITY);
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

    @Benchmark
    public int forEachValueCommonSinkIter_simpleFlavor_present(SimpleState st) {
        SinkConsumer sink = new SinkConsumer();
        SimpleVanillaShortShortMultiMap map = st.map;
        for (char key : st.keys) {
            map.forEachValue(key, sink);
        }
        return sink.x;
    }

    @Benchmark
    public int forEachValueLocalSinkIter_simpleFlavor_present(SimpleState st) {

        int x = 0;
        SimpleVanillaShortShortMultiMap map = st.map;
        for (char key : st.keys) {
            SinkConsumer sink = new SinkConsumer();
            map.forEachValue(key, sink);
            x ^= sink.x;
        }
        return x;
    }

    @Benchmark
    public int forEachValueStatelessSinkIter_simpleFlavor_present(SimpleState st) {
        final int[] x = new int[1];
        SimpleVanillaShortShortMultiMap map = st.map;
        for (char key : st.keys)
            map.forEachValue(key, value -> x[0] ^= value);
        return x[0];
    }

    @Benchmark
    public int searchPosIter_simpleFlavor_present(SimpleState st) {
        int x = 0;
        SimpleVanillaShortShortMultiMap map = st.map;
        for (char key : st.keys) {
            map.startSearch(key);
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
        new DimensionedJmh(HashPosMapBenchmarks.class)
                .addArgDim("loadFactor", "0.25", "0.5", "0.75")
                .addArgDim("capacity", SMALL_CAPACITY, LARGE_CAPACITY)
                .withGetOperationsPerInvocation(options -> {
                    int capacity = parseInt(options.get("capacity"));
                    double loadFactor = parseDouble(options.get("loadFactor"));
                    return (long) ((double) capacity * loadFactor);
                })
                .run(args);
    }
}
