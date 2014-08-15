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

package net.openhft.koloboke.collect.impl.hash;

import net.openhft.koloboke.collect.hash.HashConfig;

import static net.openhft.koloboke.collect.impl.Maths.isPowerOf2;
import static net.openhft.koloboke.collect.impl.hash.Capacities.chooseBetter;


public final class LHashCapacities {

    private static final int MIN_CAPACITY = 4;
    private static final int MAX_INT_CAPACITY = 1 << 30;
    private static final long MAX_LONG_CAPACITY = 1L << 62;

    public static boolean isMaxCapacity(int capacity) {
        return capacity == MAX_INT_CAPACITY;
    }

    public static boolean isMaxCapacity(int capacity, boolean doubleSizedArrays) {
        int maxCapacity = MAX_INT_CAPACITY;
        if (doubleSizedArrays)
            maxCapacity >>= 1;
        return capacity == maxCapacity;
    }

    public static boolean isMaxCapacity(long capacity) {
        return capacity == MAX_LONG_CAPACITY;
    }

    public static boolean isMaxCapacity(long capacity, boolean doubleSizedArrays) {
        long maxCapacity = MAX_LONG_CAPACITY;
        if (doubleSizedArrays)
            maxCapacity >>= 1L;
        return capacity == maxCapacity;
    }

    public static int capacity(HashConfigWrapper conf, int size) {
        return capacity(conf, size, false);
    }

    public static int capacity(HashConfigWrapper conf, int size, boolean doubleSizedArrays) {
        assert size >= 0 : "size must be non-negative";
        int desiredCapacity = conf.targetCapacity(size);
        if (desiredCapacity <= MIN_CAPACITY)
            return MIN_CAPACITY;
        int maxCapacity = MAX_INT_CAPACITY;
        if (doubleSizedArrays)
            maxCapacity >>= 1;
        if (desiredCapacity < maxCapacity) {
            if (isPowerOf2(desiredCapacity))
                return desiredCapacity;
            int lesserCapacity = highestOneBit(desiredCapacity);
            int greaterCapacity = lesserCapacity << 1;
            return chooseBetter(conf, size, desiredCapacity, lesserCapacity, greaterCapacity,
                    greaterCapacity);
        }
        return maxCapacity;
    }

    static boolean configIsSuitableForImmutableHash(HashConfigWrapper conf, int size) {
        assert size >= 0;
        int desiredCapacity = conf.targetCapacity(size);
        if (desiredCapacity <= MIN_CAPACITY)
            return MIN_CAPACITY <= conf.maxCapacity(size);
        if (desiredCapacity < MAX_INT_CAPACITY) {
            if (isPowerOf2(desiredCapacity))
                return true;
            int lesserCapacity = highestOneBit(desiredCapacity);
            int greaterCapacity = lesserCapacity << 1;
            int c = chooseBetter(conf, size, desiredCapacity, lesserCapacity, greaterCapacity, -1);
            return c > 0;
        }
        return false;
    }

    public static long capacity(HashConfigWrapper conf, long size) {
        return capacity(conf, size, false);
    }

    public static long capacity(HashConfigWrapper conf, long size, boolean doubleSizedArrays) {
        assert size >= 0L : "size must be non-negative";
        long desiredCapacity = conf.targetCapacity(size);
        if (desiredCapacity <= (long) MIN_CAPACITY)
            return (long) MIN_CAPACITY;
        long maxCapacity = MAX_LONG_CAPACITY;
        if (doubleSizedArrays)
            maxCapacity >>= 1L;
        if (desiredCapacity < maxCapacity) {
            if (isPowerOf2(desiredCapacity))
                return desiredCapacity;
            long lesserCapacity = highestOneBit(desiredCapacity);
            long greaterCapacity = lesserCapacity << 1;
            return chooseBetter(conf, size, desiredCapacity, lesserCapacity, greaterCapacity,
                    greaterCapacity);
        }
        return maxCapacity;
    }

    /** Standard highestOneBit is not an intrinsic */
    private static int highestOneBit(int n) {
        assert n > 0;
        return Integer.MIN_VALUE >>> Integer.numberOfLeadingZeros(n);
    }

    private static long highestOneBit(long n) {
        assert n > 0L;
        return Long.MIN_VALUE >>> Long.numberOfLeadingZeros(n);
    }

    static boolean configIsSuitableForMutableLHash(HashConfig conf) {
        return conf.getGrowFactor() == 2.0 && conf.getMinLoad() < 0.5;
    }

    private LHashCapacities() {}
}
