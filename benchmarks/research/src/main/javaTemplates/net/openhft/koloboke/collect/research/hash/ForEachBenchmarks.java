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
import net.openhft.koloboke.collect.impl.hash.*;
import net.openhft.koloboke.function.*;
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
public class ForEachBenchmarks {

    static class Config {
        public final int powerOf2Capacity, dHashCapacity, qHashCapacity, n;
        Config(int powerOf2Capacity, double loadFactor) {
            this.powerOf2Capacity = powerOf2Capacity;
            this.n = (int) ((double) powerOf2Capacity * loadFactor);
            /* with QHash|DHash hash */
            this.qHashCapacity =
                    QHashCapacities.nearestGreaterCapacity((int) ((double) n / loadFactor) + 1, 0);
            /* endwith */
        }
    }

    static final int SMALL_CAPACITY = 1024, LARGE_CAPACITY = (1 << 20);
    static final int CAPACITY = Integer.getInteger("capacity", LARGE_CAPACITY);
    static final double LOAD_FACTOR = parseDouble(System.getProperty("loadFactor", "0.6"));
    static final Config CONF = new Config(CAPACITY, LOAD_FACTOR);

    static int n(int capacity, double loadFactor) {
        return (int) ((double) capacity * loadFactor);
    }
    public static final int N = n(CAPACITY, LOAD_FACTOR);

    /* with char|byte|short|int|long key */

    static class CharXor implements CharConsumer {
        char x = (char) 0;
        @Override
        public void accept(char value) {
            x ^= value;
        }
    }
    /* with Bit|Byte|ByteAlong|No|ZeroMasking states
            LHash|LSelfAdjHash|DHash|RHoodSimpleHash|QHash hash */
    /* if !(Bit states DHash hash) &&
          !(Bit|Byte|ByteAlong states RHoodSimpleHash|QHash|LSelfAdjHash hash) &&
          !(ZeroMasking states RHoodSimpleHash|DHash|LSelfAdjHash hash) */

    @State(Scope.Thread)
    public static class BitStatesLHashChars {
        Random r;
        public BitStatesLHashCharSet set;
        CharXor xor;

        @Setup(Level.Trial)
        public void allocate() {
            r = ThreadLocalRandom.current();
            set = new BitStatesLHashCharSet(
                    CONF./* if LHash|LSelfAdjHash|RHoodSimpleHash hash */powerOf2Capacity
                         /* elif DHash hash //dHashCapacity
                         // elif QHash hash //qHashCapacity// endif */);
            xor = new CharXor();
        }

        /* define add */
        /* if !(ByteAlong states) //addBinaryStateSimpleIndexing
        // elif ByteAlong states //addBinaryStateUnsafeIndexing// endif *//* enddefine */

        @Setup(Level.Iteration)
        public void fill() {
            set.clear();
            while (set.size < N) {
                set./*add*/addBinaryStateSimpleIndexing/**/((char) r.nextLong());
            }
        }

        @TearDown(Level.Trial)
        public void recycle() {
            set = null;
        }
    }

    /* with Binary|Ternary state */
    /* if !(ByteAlong|Byte states Ternary state) &&
          !(LSelfAdjHash|RHoodSimpleHash hash Ternary state)  &&
          !(No|ZeroMasking states LHash hash Ternary state) */
    @Benchmark
    public char forEach_lHash_bitStates_binaryArity_charKey(BitStatesLHashChars s) {
        s.set.forEachBinaryState(s.xor);
        return s.xor.x;
    }
    /* endif */
    /* endwith */

    /* endif */
    /* endwith */

    /* endwith */

    public static void main(String[] args) throws RunnerException, CommandLineOptionException {
        new DimensionedJmh(ForEachBenchmarks.class)
                .addArgDim("loadFactor", "0.3", "0.6", "0.9")
                .addArgDim("capacity", SMALL_CAPACITY, LARGE_CAPACITY)
                .withGetOperationsPerInvocation(options ->
                        (long) n(parseInt(options.get("capacity")),
                                parseDouble(options.get("loadFactor"))))
                .run(args);
    }
}
