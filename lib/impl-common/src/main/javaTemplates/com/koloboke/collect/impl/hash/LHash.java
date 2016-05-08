/* with LHash hash */
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

public interface LHash extends Hash {

    /** = round(2 ^ 32 * (sqrt(5) - 1)), Java form of unsigned 2654435769 */
    int INT_PHI_MAGIC = -1640531527;
    /** ~= round(2 ^ 64 * (sqrt(5) - 1)), Java form of 11400714819323198485 */
    long LONG_PHI_MAGIC = -7046029254386353131L;

    int BYTE_MIX_SHIFT = 6;
    int CHAR_MIX_SHIFT = 10, SHORT_MIX_SHIFT = CHAR_MIX_SHIFT;
    int INT_MIX_SHIFT = 16, FLOAT_MIX_SHIFT = INT_MIX_SHIFT;

    /* with Separate|Parallel kv */

    /* with byte|char|short|int|float key */
    class SeparateKVByteKeyMixing {
        public static int mix(/* bits */byte key) {
            int h = key * INT_PHI_MAGIC;
            return h ^ (h >> BYTE_MIX_SHIFT);
        }
    }
    /* endwith */

    /* with double|long key */
    class SeparateKVDoubleKeyMixing {
        public static int mix(long key) {
            long h = key * LONG_PHI_MAGIC;
            h ^= h >> 32;
            return (int) (h ^ (h >> INT_MIX_SHIFT));
        }
    }
    /* endwith */

    class SeparateKVObjKeyMixing {
        public static int mix(int hash) {
            return hash ^ (hash >> INT_MIX_SHIFT);
        }
    }

    /* endwith */
}

