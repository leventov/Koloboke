/* with byte|char|short|int|long elem */
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


public final class ByteArrays implements UnsafeConstants {

    public static void replaceAll(byte[] a, byte oldValue, byte newValue) {
        for (int i = 0; i < a.length; i++) {
            if (a[i] == oldValue) {
                a[i] = newValue;
            }
        }
    }

    public static void replaceAllKeys(char[] table, byte oldKey, byte newKey) {
        /* if !(long elem) */
        long base = CHAR_BASE + BYTE_KEY_OFFSET;
        for (long off = CHAR_SCALE * (long) table.length; (off -= CHAR_SCALE) >= 0L;) {
            if (U.getByte(table, base + off) == oldKey) {
                U.putByte(table, base + off, newKey);
            }
        }
        /* elif long elem */
        for (int i = 0; i < table.length; i += 2) {
            if (table[i] == oldKey) {
                table[i] = newKey;
            }
        }
        /* endif */
    }

    public static void fillKeys(char[] table, byte key) {
        /* if !(long elem) */
        long base = CHAR_BASE + BYTE_KEY_OFFSET;
        for (long off = CHAR_SCALE * (long) table.length; (off -= CHAR_SCALE) >= 0L;) {
            U.putByte(table, base + off, key);
        }
        /* elif long elem */
        for (int i = 0; i < table.length; i += 2) {
            table[i] = key;
        }
        /* endif */
    }

    private ByteArrays() {}
}
