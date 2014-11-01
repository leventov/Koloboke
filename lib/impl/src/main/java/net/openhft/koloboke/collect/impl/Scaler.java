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

package net.openhft.koloboke.collect.impl;

import java.math.BigDecimal;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.valueOf;
import static java.math.MathContext.DECIMAL128;


/**
 * Class for precise and fast scaling non-negative integers by positive doubles.
 *
 * <p>Used in {@link net.openhft.koloboke.collect.impl.hash.HashConfigWrapper}.
 *
 * <p>Latencies of operations on floating stack, required for simple approach scaling
 * <pre>
 *                       Haswell  Steamroller
 * FILD (long -> double) 6        11
 * FMUL                  5        5
 * FDIV                  10-24    9-37
 * FIST (double -> long) 7        7
 * </pre>
 *
 * <p>In major cases {@code Scaler} allows to replace this
 * with megamorphic call cost + a few clock cycles.
 */
public class Scaler {

    public static Scaler by(double scale) {
        if (Double.isNaN(scale) || scale <= 0.0 || Double.isInfinite(scale))
            throw new IllegalArgumentException(
                    "Scale should be a finite positive number, " + scale + " is given");
        if (scale == 0.25) return BY_0_25;
        // Special "precise" BigDecimal forms for scales which are inversions of custom
        // scales are needed to preserve inversion consistency
        if (scale == 1.0 / 3.0) return BY_3_0_INVERSE;
        if (scale == 0.5) return BY_0_5;
        if (scale == 1 / 1.5) return BY_1_5_INVERSE;
        if (scale == 0.75) return BY_0_75;
        if (scale == 1.0) return BY_1_0;
        if (scale == 1.0 / 0.75) return BY_0_75_INVERSE;
        if (scale == 1.5) return BY_1_5;
        if (scale == 2.0) return BY_2_0;
        if (scale == 3.0) return BY_3_0;
        if (scale == 4.0) return BY_4_0;
        return new Scaler(scale);
    }

    static final Scaler BY_0_25 = new Scaler(0.25) {
        @Override public int  scaleUpper(int  n) { check(n); return (n >> 2) + 1 ; }
        @Override public int  scaleLower(int  n) { check(n); return  n >> 2      ; }
        @Override public long scaleUpper(long n) { check(n); return (n >> 2) + 1L; }
        @Override public long scaleLower(long n) { check(n); return  n >> 2      ; }
    };

    static final Scaler BY_3_0_INVERSE = new Scaler(1.0 / 3.0) {
        @Override BigDecimal createBD() { return ONE.divide(valueOf(3L), DECIMAL128); }
    };

    static final Scaler BY_0_5 = new Scaler(0.5) {
        @Override public int  scaleUpper(int  n) { check(n); return (n >> 1) + 1 ; }
        @Override public int  scaleLower(int  n) { check(n); return  n >> 1      ; }
        @Override public long scaleUpper(long n) { check(n); return (n >> 1) + 1L; }
        @Override public long scaleLower(long n) { check(n); return  n >> 1      ; }
    };

    static final Scaler BY_1_5_INVERSE = new Scaler(1.0 / 1.5) {
        @Override BigDecimal createBD() { return valueOf(2L).divide(valueOf(3L), DECIMAL128); }
    };

    static final Scaler BY_0_75 = new Scaler(0.75) {
        @Override public int  scaleUpper(int  n) { check(n); int  r = n - (n >> 2); return (n & 3 ) != 0 ? r      : r + 1 ; }
        @Override public int  scaleLower(int  n) { check(n); int  r = n - (n >> 2); return (n & 3 ) != 0 ? r - 1  : r     ; }
        @Override public long scaleUpper(long n) { check(n); long r = n - (n >> 2); return (n & 3L) != 0 ? r      : r + 1L; }
        @Override public long scaleLower(long n) { check(n); long r = n - (n >> 2); return (n & 3L) != 0 ? r - 1L : r     ; }
    };

    static final Scaler BY_1_0 = new Scaler(1.0) {
        @Override public int  scaleUpper(int  n) { check(n); return n < Integer.MAX_VALUE ? n + 1  : Integer.MAX_VALUE; }
        @Override public int  scaleLower(int  n) { check(n); return n                                                 ; }
        @Override public long scaleUpper(long n) { check(n); return n < Long.MAX_VALUE    ? n + 1L : Long.MAX_VALUE   ; }
        @Override public long scaleLower(long n) { check(n); return n                                                 ; }
    };

    static final Scaler BY_0_75_INVERSE = new Scaler(1.0 / 0.75) {
        @Override BigDecimal createBD() { return valueOf(4L).divide(valueOf(3L), DECIMAL128); }
    };

    static final Scaler BY_1_5 = new Scaler(1.5) {
        @Override public int  scaleUpper(int  n) { check(n); return n <= 1431655764           ? n + (n >> 1) + 1  : Integer.MAX_VALUE; }
        @Override public int  scaleLower(int  n) { check(n); return n <= 1431655764           ? n + (n >> 1)      : Integer.MAX_VALUE; }
        @Override public long scaleUpper(long n) { check(n); return n <= 6148914691236517204L ? n + (n >> 1) + 1L : Long.MAX_VALUE   ; }
        @Override public long scaleLower(long n) { check(n); return n <= 6148914691236517204L ? n + (n >> 1)      : Long.MAX_VALUE   ; }
    };

    static final Scaler BY_2_0 = new Scaler(2.0) {
        @Override public int  scaleUpper(int  n) { check(n); return n < (1  << 30) ? (n << 1) + 1  : Integer.MAX_VALUE; }
        @Override public int  scaleLower(int  n) { check(n); return n < (1  << 30) ? (n << 1)      : Integer.MAX_VALUE; }
        @Override public long scaleUpper(long n) { check(n); return n < (1L << 62) ? (n << 1) + 1L : Long.MAX_VALUE   ; }
        @Override public long scaleLower(long n) { check(n); return n < (1L << 62) ? (n << 1)      : Long.MAX_VALUE   ; }
    };

    static final Scaler BY_3_0 = new Scaler(3.0) {
        @Override public int  scaleUpper(int  n) { check(n); return n <= 715827882            ? n + (n << 1) + 1  : Integer.MAX_VALUE; }
        @Override public int  scaleLower(int  n) { check(n); return n <= 715827882            ? n + (n << 1)      : Integer.MAX_VALUE; }
        @Override public long scaleUpper(long n) { check(n); return n <= 3074457345618258602L ? n + (n << 1) + 1L : Long.MAX_VALUE   ; }
        @Override public long scaleLower(long n) { check(n); return n <= 3074457345618258602L ? n + (n << 1)      : Long.MAX_VALUE   ; }
    };

    static final Scaler BY_4_0 = new Scaler(4.0) {
        @Override public int  scaleUpper(int  n) { check(n); return n < (1  << 29) ? (n << 2) + 1  : Integer.MAX_VALUE; }
        @Override public int  scaleLower(int  n) { check(n); return n < (1  << 29) ? (n << 2)      : Integer.MAX_VALUE; }
        @Override public long scaleUpper(long n) { check(n); return n < (1L << 61) ? (n << 2) + 1L : Long.MAX_VALUE   ; }
        @Override public long scaleLower(long n) { check(n); return n < (1L << 61) ? (n << 2)      : Long.MAX_VALUE   ; }
    };

    private static void check(int n) {
        assert n >= 0 : "n should be non-negative, otherwise result is undefined";
    }

    private static void check(long n) {
        assert n >= 0L : "n should be non-negative, otherwise result is undefined";
    }

    private static final BigDecimal LONG_MAX_VALUE = valueOf(Long.MAX_VALUE);

    final double scale;
    private BigDecimal scaleAsBD;

    private Scaler(double scale) {
        this.scale = scale;
    }

    private BigDecimal scaleAsBD() {
        return scaleAsBD != null ? scaleAsBD : (scaleAsBD = createBD());
    }

    /** Shouldn't be called from outside of the class. */
    BigDecimal createBD() {
        return valueOf(scale);
    }

    public int scaleUpper(int n) {
        check(n);
        int lower = (int) ((double) n * scale);
        return lower < Integer.MAX_VALUE ? lower + 1 : Integer.MAX_VALUE;
    }

    public int scaleLower(int n) {
        check(n);
        return (int) ((double) n * scale);
    }

    public long scaleUpper(long n) {
        check(n);
        if (n < Integer.MAX_VALUE && scale < 1.0)
            return (long) scaleUpper((int) n);
        BigDecimal lower = valueOf(n).multiply(scaleAsBD());
        return lower.compareTo(LONG_MAX_VALUE) < 0 ? lower.longValue() + 1L : Long.MAX_VALUE;
    }

    public long scaleLower(long n) {
        check(n);
        if (n < Integer.MAX_VALUE && scale < 1.0)
            return (long) scaleLower((int) n);
        BigDecimal lower = valueOf(n).multiply(scaleAsBD());
        return lower.compareTo(LONG_MAX_VALUE) < 0 ? lower.longValue() : Long.MAX_VALUE;
    }
}
