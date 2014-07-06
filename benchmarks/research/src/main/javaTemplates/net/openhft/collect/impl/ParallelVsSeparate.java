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

package net.openhft.collect.impl;

import net.openhft.bench.DimensionedJmh;
import net.openhft.collect.*;
import net.openhft.collect.map.*;
import net.openhft.collect.map.hash.*;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.CommandLineOptionException;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static java.lang.Integer.parseInt;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Threads(1)
@Fork(1)
@Warmup(iterations = 5)
@Measurement(iterations = 10)
public class ParallelVsSeparate {

    static final int BYTE_SIZE = 250;
    static final int SMALL_CHAR_SIZE = 1000, LARGE_CHAR_SIZE = 60000;
    static final int SMALL_SIZE = SMALL_CHAR_SIZE, LARGE_SIZE = 1000 * 1000;

    static final HashConfig L_HASH_CONFIG = HashConfig.getDefault();
    static final HashConfig Q_HASH_CONFIG = L_HASH_CONFIG.withGrowFactor(1.999);

    private static int getSize(String sizeHint, String keyType) {
        switch (keyType) {
            case "byte":
                return BYTE_SIZE;
            case "char":
                return "small".equals(sizeHint) ? SMALL_CHAR_SIZE : LARGE_CHAR_SIZE;
            default:
                return "small".equals(sizeHint) ? SMALL_SIZE : LARGE_SIZE;
        }
    }

    /* with char|byte|int|long key QHash|LHash hash */

    @State(Scope.Thread)
    public static class QHashCharCharMapState {
        Random r;
        char[] keys;
        CharCharMap map;

        @Setup(Level.Trial)
        public void allocate() {
            r = ThreadLocalRandom.current();
            int size = getSize(System.getProperty("size", "small"), char.class.getName());
            keys = new char[size];
            HashCharCharMapFactory factory = HashCharCharMaps.getDefaultFactory();
            factory = factory.withConfig(factory.getConfig().withHashConfig(Q_HASH_CONFIG));
            map = factory.newUpdatableMap(size);
        }

        @Setup(Level.Invocation)
        public void generateKeys() {
            for (int i = 0; i < keys.length; i++) {
                keys[i] = (char) r.nextLong();
            }
        }

        @TearDown(Level.Trial)
        public void recycle() {
            keys = null;
            map = null;
        }
    }

    public static class PutOpQHashCharCharMapState extends QHashCharCharMapState {
        @Setup(Level.Invocation)
        public void clearMap() {
            map.clear();
        }
    }

    public static class QueryUpdateOpQHashCharCharMapState extends QHashCharCharMapState {
        @Setup(Level.Invocation)
        public void fillMap() {
            map.clear();
            for (char key : keys) {
                map.put(key, /* const key 1 */(char) 1/* endconst */);
            }
        }
    }

    @GenerateMicroBenchmark
    public long putOp_qHash_charKey(PutOpQHashCharCharMapState state) {
        char[] keys = state.keys;
        CharCharMap map = state.map;
        long dummy = 0L;
        for (char key : keys) {
            dummy ^= (long) map.put(key, /* const key 1 */(char) 1/* endconst */);
        }
        return dummy;
    }

    @GenerateMicroBenchmark
    public long getOp_qHash_charKey(QueryUpdateOpQHashCharCharMapState state) {
        char[] keys = state.keys;
        CharCharMap map = state.map;
        long dummy = 0L;
        for (char key : keys) {
            dummy ^= (long) map.get(key);
        }
        return dummy;
    }

    @GenerateMicroBenchmark
    public long incrementValueOp_qHash_charKey(QueryUpdateOpQHashCharCharMapState state) {
        char[] keys = state.keys;
        CharCharMap map = state.map;
        long dummy = 0L;
        for (char key : keys) {
            dummy ^= (long) map.incrementValue(key, /* const key 1 */(char) 1/* endconst */);
        }
        return dummy;
    }

    @GenerateMicroBenchmark
    public long computeOp_qHash_charKey(QueryUpdateOpQHashCharCharMapState state) {
        char[] keys = state.keys;
        CharCharMap map = state.map;
        long dummy = 0L;
        for (char key : keys) {
            dummy ^= (long) map.compute(key, (k, v) -> (char) (-v));
        }
        return dummy;
    }

    /* endwith */

    public static void main(String[] args) throws RunnerException, CommandLineOptionException {
        new DimensionedJmh(ParallelVsSeparate.class)
                .addArgDim("size", "small", "large")
                .withGetOperationsPerInvocation(options ->
                        getSize(options.get("size"), options.get("key")))
                .run(args);
    }
}
