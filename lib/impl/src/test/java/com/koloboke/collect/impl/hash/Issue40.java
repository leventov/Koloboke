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

package com.koloboke.collect.impl.hash;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.BitSet;

import com.koloboke.collect.map.hash.HashLongObjMap;
import com.koloboke.collect.map.hash.HashLongObjMaps;
import com.koloboke.collect.set.hash.HashLongSet;
import com.koloboke.collect.set.hash.HashLongSets;
import org.junit.Test;


public class Issue40 {
    
    @Test
    public void test() {
        main();
    }

    // The primes 3, 7, 109, and 673, are quite remarkable. By taking any two
    // primes and concatenating them in any order the result will always be prime.
    // For example, taking 7 and 109, both 7109 and 1097 are prime. The sum of
    // these four primes, 792, represents the lowest sum for a set of four primes
    // with this property.
    //
    // Find the lowest sum for a set of five primes for which any two primes
    // concatenate to produce another prime.

    // Analysis:
    // 2 cannot be a part of the resulting set, since 2 is the only prime ending
    // in 2. Same for 5.
    // Use sets to intersect valid pairs until a set of five remains

    private static final long[] primes = generatePrimesWithESieve(100000);
    private static HashLongSet primeSet = HashLongSets.newUpdatableSet(primes);
    private static long maxSum = 100000;
    private static final int MAX_INDEX = Arrays.binarySearch(primes, BigInteger.valueOf(25000).nextProbablePrime().longValue());
    private static final long MAX_PRIME = primes[primes.length - 1];
    private static HashLongObjMap<HashLongSet> primePairMap = HashLongObjMaps.getDefaultFactory().<HashLongSet> newUpdatableMap(MAX_INDEX);

    public static void main() {
        long time = System.currentTimeMillis();
        for (int i = 1; i < MAX_INDEX; i++) {
            HashLongSet primarySet = getPairSet(primes[i], i);
            long[] secArr = primarySet.toLongArray();
            Arrays.sort(secArr);
            for (int j = 0; j < secArr.length; j++) {
                if (primes[i] + secArr[j] * 4 > maxSum) break;
                HashLongSet secondarySet = HashLongSets.getDefaultFactory().newMutableSet(primarySet);
                secondarySet.retainAll(getPairSet(secArr[j]));
                if (secondarySet.size() < 3) continue;
                long[] tertArr = secondarySet.toLongArray();
                Arrays.sort(tertArr);
                for (int k = 0; k < tertArr.length; k++) {
                    if (primes[i] + secArr[j] + tertArr[k] * 3 > maxSum) break;
                    HashLongSet tertiarySet = HashLongSets.getDefaultFactory().newMutableSet(secondarySet);
                    if (tertiarySet.size() < 2) continue;
                    tertiarySet.retainAll(getPairSet(tertArr[k]));
                    long[] quatArr = tertiarySet.toLongArray();
                    Arrays.sort(quatArr);
                    for (int l = 0; l < quatArr.length; l++) {
                        if (primes[i] + secArr[j] + tertArr[k] + quatArr[l] * 2 > maxSum) break;
                        HashLongSet quaternarySet = HashLongSets.getDefaultFactory().newMutableSet(tertiarySet);
                        if (quaternarySet.size() < 1) continue;
                        quaternarySet.retainAll(getPairSet(quatArr[l]));
                        long[] quinArr = quaternarySet.toLongArray();
                        Arrays.sort(quinArr);
                        for (int m = 0; m < quinArr.length; m++) {
                            if (primes[i] + secArr[j] + tertArr[k] + quatArr[l] + quinArr[m] > maxSum) break;
                            System.out.format("{ %1$d, %2$d, %3$d, %4$d, %5$d } Sum = %6$d", primes[i], secArr[j], tertArr[k], quatArr[l], quinArr[m], primes[i]
                                    + secArr[j] + tertArr[k] + quatArr[l] + quinArr[m]);
                            System.out.println();
                            long sum = primes[i] + secArr[j] + tertArr[k] + quatArr[l] + quinArr[m];
                            if (sum < maxSum) {
                                maxSum = sum;
                            }
                        }
                    }
                }
            }
        }

        System.out.println("Result = " + maxSum);
        System.out.println("Done.");
        System.out.println("Solution took " + (System.currentTimeMillis() - time) + " ms.");
    }

    private static HashLongSet getPairSet(long prime) {
        return getPairSet(prime, Arrays.binarySearch(primes, prime));
    }

    private static HashLongSet getPairSet(long prime, int primeIndex) {
        HashLongSet ret = primePairMap.get(prime);
        if (ret == null) {
            primePairMap.put(prime, (ret = getPairs(prime, primeIndex)));
        }
        return ret;
    }

    private static HashLongSet getPairs(long prime, int primeIndex) {
        HashLongSet pairSet = HashLongSets.newUpdatableSet();
        long primeDigitSum = digitSum(prime);
        for (int j = primeIndex + 1; j < MAX_INDEX; j++) {
            if ((primeDigitSum + digitSum(primes[j])) % 3 == 0) continue;
            if (isPrime(concat(prime, primes[j])) && isPrime(concat(primes[j], prime))) pairSet.add(primes[j]);
        }
        return pairSet;
    }

    private static boolean isPrime(long num) {
        if (num > MAX_PRIME) { return BigInteger.valueOf(num).isProbablePrime(20); }
        return primeSet.contains(num);
    }

    public static long[] generatePrimesWithESieve(long upperLimit) {
        // No even numbers in sieve BitSet
        // BitSet starts from 0..sieveBound
        // p = 2i+3 => i = (p-3)/2
        // jStart = 3p
        // jStep = 2p

        // i = 0 => p = 3 => jStart = 9, jStep = 6
        // i = 0, j = 3, 6, 9
        // i = 1 => p = 5 => jStart = 15, jStep = 10
        // i = 1, j = 6, 11, 16
        // i = 2 => p = 7 => jStart = 21, jStep = 14
        // i = 2, j = 9, 16, 23

        int intUpperLimit = Math.toIntExact(upperLimit);
        int sieveBound = (intUpperLimit - 1) / 2;
        int upperSqrt = ((int) Math.sqrt(upperLimit) - 1) / 2;

        BitSet set = new BitSet(sieveBound + 1);
        set.set(0, sieveBound + 1);

        for (int i = 0; i <= upperSqrt; i++) {
            if (set.get(i)) {
                for (int j = 3 * i + 3; j <= sieveBound; j += 2 * i + 3) {
                    set.clear(j);
                }
            }
        }

        int i = 0;
        // Number of primes can be approximated with pi(x) = x/log(x - 1)
        LongArray list = new LongArray((int) (upperLimit / (Math.log(upperLimit) - 1.08366)));
        list.setSizeIncreaseFactor(1.2d);
        list.add(2); // sieve starts at 3
        do {
            list.add(2 * i + 3);
        } while ((i = set.nextSetBit(i + 1)) != -1);

        return list.getArray();
    }

    public static long concat(long a, long b) {
        for (long c = b; c > 0;) {
            a *= 10;
            c /= 10;
        }
        return a + b;
    }

    public static long digitSum(long num) {
        long sum = 0;
        while (num > 0) {
            sum += num - ((num / 10) * 10);
            num /= 10;
        }
        return sum;
    }

    public static class LongArray {
        private static final int DEFAULT_SIZE = 16;
        private double sizeIncreaseFactor = 1.5d;
        private long[] arr;
        private int size = 0;

        public LongArray() {
            arr = new long[DEFAULT_SIZE];
        }

        public LongArray(int initialSize) {
            arr = new long[initialSize];
        }

        public void add(long value) {
            if (size == arr.length) {
                increaseSize();
            }
            arr[size++] = value;
        }

        public long[] getArray() {
            trim();
            return arr;
        }

        public long get(int i) {
            return arr[i];
        }

        public void trim() {
            if (size < arr.length) {
                long[] newArr = new long[size];
                System.arraycopy(arr, 0, newArr, 0, size);
                arr = newArr;
            }
        }

        private void increaseSize() {
            long[] newArr = new long[(int) (arr.length * sizeIncreaseFactor)];
            System.arraycopy(arr, 0, newArr, 0, arr.length);
            arr = newArr;
        }

        public double getSizeIncreaseFactor() {
            return sizeIncreaseFactor;
        }

        public void setSizeIncreaseFactor(double sizeIncreaseFactor) {
            this.sizeIncreaseFactor = sizeIncreaseFactor;
        }
    }
}
