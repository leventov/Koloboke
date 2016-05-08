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

package com.koloboke.collect.impl;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

import static com.koloboke.collect.impl.Scaler.*;


@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Threads(1)
@Fork(1)
@Warmup(iterations = 5)
@Measurement(iterations = 10)
public class ScalerBenchmarks {

    private static final int intV = 1000;
    private static final long longV = (1L << 40);

    static final Scaler GOLDEN = Scaler.by(2.0 / (1.0 + Math.sqrt(5.0)));
    static final Scaler PI = Scaler.by(Math.PI);
    
    /* with int|long n Lower|Upper bound */

    @Benchmark
    public int by_0_25_lower_int() {
        return BY_0_25.scaleLower(intV);
    }

    
    @Benchmark
    public int by_0_5_lower_int() {
        return BY_0_5.scaleLower(intV);
    }

    
    @Benchmark
    public int by_golden_lower_int() {
        return GOLDEN.scaleLower(intV);
    }

    
    @Benchmark
    public int by_0_75_lower_int() {
        return BY_0_75.scaleLower(intV);
    }

    
    @Benchmark
    public int by_1_0_lower_int() {
        return BY_1_0.scaleLower(intV);
    }


    @Benchmark
    public int by_1_5_lower_int() {
        return BY_1_5.scaleLower(intV);
    }


    @Benchmark
    public int by_2_0_lower_int() {
        return BY_2_0.scaleLower(intV);
    }


    @Benchmark
    public int by_3_0_lower_int() {
        return BY_3_0.scaleLower(intV);
    }


    @Benchmark
    public int by_4_0_lower_int() {
        return BY_4_0.scaleLower(intV);
    }


    @Benchmark
    public int by_pi_lower_int() {
        return PI.scaleLower(intV);
    }

    /* endwith */

    @Benchmark
    public int simple_div_by_golden_int() {
        return (int) (intV / GOLDEN.scale);
    }

    @Benchmark
    public long simple_div_by_golden_long() {
        return (long) (longV / GOLDEN.scale);
    }
}
