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


import java.util.Random;


public final class Primitives {

    public static int hashCode(double v) {
        long bits = Double.doubleToLongBits(v);
        return (int) (bits ^ (bits >>> 32));
    }


    public static int hashCode(float v) {
        return Float.floatToIntBits(v);
    }

    public static int hashCode(long v) {
        return (int) (v ^ (v >>> 32));
    }


    public static int hashCode(int v) {
        return v;
    }


    public static int hashCode(char v) {
        return v;
    }


    public static int hashCode(short v) {
        return v;
    }


    public static int hashCode(byte v) {
        return v;
    }


    // Hash codes for equality operator (==)

    public static int identityHashCode(double value) {
        long bits = Double.doubleToRawLongBits(value);
        // We should drop double sign, because -0.0 == 0.0
        return (int) (bits ^ (bits >>> 32)) & Integer.MAX_VALUE;
    }


    public static int identityHashCode(float value) {
        return Float.floatToRawIntBits(value) & Integer.MAX_VALUE;
    }


    // Nothing special for integral types

    public static int identityHashCode(long value) {
        return (int) (value ^ (value >>> 32));
    }


    public static int identityHashCode(int value) {
        return value;
    }


    public static int identityHashCode(char value) {
        return value;
    }


    public static int identityHashCode(short value) {
        return value;
    }


    public static int identityHashCode(byte value) {
        return value;
    }


    public static byte nextByte(Random random) {
        return (byte) random.nextInt();
    }


    public static char nextChar(Random random) {
        return (char) random.nextInt();
    }


    public static short nextShort(Random random) {
        return (short) random.nextInt();
    }


    public static int nextInt(Random random) {
        return random.nextInt();
    }


    public static long nextLong(Random random) {
        return random.nextLong();
    }


    public static float nextFloat(Random random) {
        return random.nextFloat();
    }


    public static double nextDouble(Random random) {
        return random.nextDouble();
    }

    private Primitives() {}
}
