/* with
 DHash|QHash|LHash hash
 double|float elem
 Mutable|Updatable|Immutable mutability
 Separate|Parallel kv
*/
/* if (Separate kv) || (Enabled parallelKV) */
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

package net.openhft.collect.impl.hash;

import net.openhft.collect.impl.*;

import java.util.Arrays;


public abstract class MutableSeparateKVDoubleDHashSO extends MutableDHash
        implements SeparateKVDoubleDHash, PrimitiveConstants, UnsafeConstants {

    /* if Separate kv */
    long[] set;
    /* elif Parallel kv */
    /*tt*/double[] table;
    /* endif */

    void copy(SeparateKVDoubleDHash hash) {
        super.copy(hash);
        /* if Separate kv */
        set = hash.keys().clone();
        /* elif Parallel kv */
        table = hash.table().clone();
        /* endif */
    }

    void move(SeparateKVDoubleDHash hash) {
        super.copy(hash);
        /* if Separate kv */
        set = hash.keys();
        /* elif Parallel kv */
        table = hash.table();
        /* endif */
    }


    public boolean contains(Object key) {
        return contains(((Double) key).doubleValue());
    }

    public boolean contains(double key) {
        return index(Double.doubleToLongBits(key)) >= 0;
    }

    public boolean contains(long key) {
        return index(key) >= 0;
    }

    int index(long key) {
        /* template Index with internal version */ throw new NotGenerated(); /* endtemplate */
    }


    /* if !(Immutable mutability) */
    @Override
    void allocateArrays(int capacity) {
        /* if Separate kv */
        set = new long[capacity];
        Arrays.fill(set, FREE_BITS);
        /* elif Parallel kv */
        table = new /*tt*/double[capacity/* if double elem */ * 2/* endif */];
        LongArrays.fillKeys(table, FREE_BITS);
        /* endif */
    }

    @Override
    public void clear() {
        super.clear();
        /* if Separate kv */
        Arrays.fill(set, FREE_BITS);
        /* elif Parallel kv */
        LongArrays.fillKeys(table, FREE_BITS);
        /* endif */
    }
    /* endif */

    /* if Mutable mutability && !(LHash hash) */
    @Override
    void removeAt(int index) {
        /* if Separate kv */
        set[index] = REMOVED_BITS;
        /* elif Parallel kv */
        /* if float elem */
        /* with float elem */
        U.putInt(table, LONG_BASE + FLOAT_KEY_OFFSET + (((long) index) << LONG_SCALE_SHIFT),
                REMOVED_BITS);
        /* endwith */
        /* elif double elem */
        table[index] = REMOVED_BITS;
        /* endif */
        /* endif */
    }
    /* endif */
}
