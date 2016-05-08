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

import org.hamcrest.Matcher;
import org.junit.Test;

import java.math.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Math.*;
import static java.lang.String.format;
import static java.math.BigDecimal.valueOf;
import static java.math.MathContext.DECIMAL128;
import static com.koloboke.collect.impl.Scaler.*;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;


public class ScalerTest {

    private static final Scaler[] SCALERS = {
            BY_0_25,
            BY_0_5,
            BY_0_75,
            BY_1_0,
            BY_1_5,
            BY_2_0,
            BY_3_0,
            BY_4_0,
            Scaler.by(PI),
            Scaler.by(E),
            Scaler.by(nextUp(1.0)),
            Scaler.by(nextAfter(1.0, 0.0)),
            Scaler.by(nextUp(0.0)),
            Scaler.by(Double.MAX_VALUE),
    };

    public static final Collection<Integer> ints = new ArrayList<Integer>();
    public static final Collection<Long> longs = new ArrayList<Long>();
    static {
        for (int i = 1; i < 10000; i++) {
            ints.add(i);
            longs.add((long) i);
        }
        for (int i = Integer.MAX_VALUE - 10; i > 0; i++) {
            ints.add(i);
        }
        for (long l = Long.MAX_VALUE - 10L; l > 0L; l++) {
            longs.add(l);
        }
        int[] specialInts = {1 << 29, 1 << 30, 1431655764, 715827882};
        for (int special : specialInts) {
            for (int i = special - 5; i < special + 5; i++) {
                ints.add(i);
            }
        }
        long[] specialLongs = {1L << 61, 1L << 62, 6148914691236517204L, 3074457345618258602L};
        for (long special : specialLongs) {
            for (long l = special - 5L; l < special + 5L; l++) {
                longs.add(l);
            }
        }
        Random r = ThreadLocalRandom.current();
        for (int i = 0; i < 10000; i++) {
            int v = r.nextInt(Integer.MAX_VALUE) + 1;
            ints.add(v);
            longs.add((long) v);
        }
    }

    static BigDecimal asBD(int i) {
        return new BigDecimal(i, DECIMAL128);
    }

    static BigDecimal asBD(long l) {
        return new BigDecimal(l, DECIMAL128);
    }

    @Test
    public void test() {
        for (Scaler s : SCALERS) {
            BigDecimal scale = valueOf(s.scale);
            Matcher<BigDecimal> greaterThanScale = greaterThan(scale);
            Matcher<BigDecimal> lessOrEqualToScale = lessThanOrEqualTo(scale);
            BigDecimal intMaxValue = asBD(Integer.MAX_VALUE);
            for (int i : ints) {
                if (asBD(i).multiply(scale).compareTo(intMaxValue) < 0) {
                    assertEquals(format("diff, scale: %f, int: %d", s.scale, i),
                            1L, (long) (s.scaleUpper(i) - s.scaleLower(i)));
                    assertThat(format("lower, scale: %f, int: %d", s.scale, i),
                            asBD(s.scaleLower(i)).divide(asBD(i), DECIMAL128), lessOrEqualToScale);
                    assertThat(format("upper, scale: %f, int: %d", s.scale, i),
                            asBD(s.scaleUpper(i)).divide(asBD(i), DECIMAL128), greaterThanScale);
                } else {
                    assertEquals(format("lower, scale: %f, int: %d", s.scale, i),
                            (long) Integer.MAX_VALUE, (long) s.scaleLower(i));
                    assertEquals(format("upper, scale: %f, int: %d", s.scale, i),
                            (long) Integer.MAX_VALUE, (long) s.scaleUpper(i));
                }
            }
            BigDecimal longMaxValue = asBD(Long.MAX_VALUE);
            for (long l : longs) {
                if (asBD(l).multiply(scale).compareTo(longMaxValue) < 0) {
                    assertEquals(format("diff, scale: %f, long: %d", s.scale, l),
                            1L, s.scaleUpper(l) - s.scaleLower(l));
                    assertThat(format("lower, scale: %f, long: %d", s.scale, l),
                            asBD(s.scaleLower(l)).divide(asBD(l), DECIMAL128), lessOrEqualToScale);
                    assertThat(format("upper, scale: %f, long: %d", s.scale, l),
                            asBD(s.scaleUpper(l)).divide(asBD(l), DECIMAL128), greaterThanScale);
                } else {
                    assertEquals(format("lower, scale: %f, long: %d", s.scale, l),
                            Long.MAX_VALUE, s.scaleLower(l));
                    assertEquals(format("upper, scale: %f, long: %d", s.scale, l),
                            Long.MAX_VALUE, s.scaleUpper(l));
                }
            }
        }
    }

    @Test
    public void testZeros() {
        for (Scaler s : SCALERS) {
            assertEquals(0L, (long) s.scaleLower(0));
            assertEquals(1L, (long) s.scaleUpper(0));
            assertEquals(0L, s.scaleLower(0L));
            assertEquals(1L, s.scaleUpper(0L));
        }
    }
}
