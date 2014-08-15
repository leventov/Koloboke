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

public final class Maths {

    /**
     * Checks if the given number is a positive power of 2 (including zero power - one).
     *
     * @param n the number to check
     * @return {@code true} is the number is equal to 1, 2, 4, ... or 2^30
     */
    public static boolean isPowerOf2(int n) {
        return (n & (n - 1)) == 0;
    }

    /**
     * Checks if the given number is a positive power of 2 (including zero power - one).
     *
     * @param n the number to check
     * @return {@code true} is the number is equal to 1, 2, 4, ... or 2^62
     */
    public static boolean isPowerOf2(long n) {
        return (n & (n - 1L)) == 0;
    }

    private Maths() {}
}
