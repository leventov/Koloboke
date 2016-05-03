/* with QHash hash */
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

import static net.openhft.koloboke.collect.impl.hash.LHash.*;


public interface QHash extends Hash {
    /* with Separate|Parallel kv */

    /* with byte|char|short|int|float key */
    class SeparateKVByteKeyMixing {
        public static int mix(/* bits */byte key) {
            return (key * INT_PHI_MAGIC) & Integer.MAX_VALUE;
        }
    }
    /* endwith */

    /* with double|long key */
    class SeparateKVDoubleKeyMixing {
        public static int mix(long key) {
            long h = key * LONG_PHI_MAGIC;
            /* if Separate kv */
            h ^= h >> 32;
            return ((int) h) & Integer.MAX_VALUE;
            /* elif Parallel kv */
            // not to loose information about 63-64-th bits
            h ^= (h >> 40) ^ (h >> 24);
            return ((((int) h) << 2) >>> 1);
            /* endif */
        }
    }
    /* endwith */

    class SeparateKVObjKeyMixing {
        public static int mix(int hash) {
            /* if Separate kv */
            return hash & Integer.MAX_VALUE;
            /* elif Parallel kv */
            return (hash << 2) >>> 1;
            /* endif */
        }
    }

    /* endwith */
}
