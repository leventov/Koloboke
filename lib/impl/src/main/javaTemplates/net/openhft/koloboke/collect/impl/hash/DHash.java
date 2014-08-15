/* with DHash hash */
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


interface DHash extends Hash {

    /* with Separate|Parallel kv */

    /* with byte|char|short|int|float key */
    static class SeparateKVByteKeyMixing {
        static int mix(/* bits */byte key) {
            return key/* if !(char key) */ & Integer.MAX_VALUE/* endif */;
        }
    }
    /* endwith */

    /* with double|long key */
    static class SeparateKVDoubleKeyMixing {
        static int mix(long key) {
            // not to loose information about 31-32 and 63-64-th bits
            long h = key ^ (key >> 40) ^ (key >> 24);
            /* if Separate kv */
            return ((int) h) & Integer.MAX_VALUE;
            /* elif Parallel kv */
            return (((int) h) << 2) >>> 1;
            /* endif */
        }
    }
    /* endwith */

    static class SeparateKVObjKeyMixing {
        static int mix(int hash) {
            /* if Separate kv */
            return hash & Integer.MAX_VALUE;
            /* elif Parallel kv */
            return (hash << 2) >>> 1;
            /* endif */
        }
    }

    /* endwith */
}
