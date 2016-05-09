/* with
 DHash|QHash|LHash hash
 object elem
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

package com.koloboke.collect.impl.hash;

import com.koloboke.collect.impl.*;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.*;


public abstract class MutableSeparateKVObjDHashSO<E> extends MutableDHash
        implements SeparateKVObjDHash, DHash {

    /* if Separate kv */
    /* if Generic keyType */Object/* elif Specific keyType //E// endif */[] set;
    /* elif Parallel kv */
    Object[] table;
    /* endif */

    void copy(SeparateKVObjDHash hash) {
        super.copy(hash);
        /* if Separate kv */
        set = hash.keys().clone();
        /* elif Parallel kv */
        table = hash.table().clone();
        /* endif */
    }

    void move(SeparateKVObjDHash hash) {
        super.copy(hash);
        /* if Separate kv */
        set = hash.keys();
        /* elif Parallel kv */
        table = hash.table();
        /* endif */
    }

    boolean nullableKeyEquals(@Nullable E a, @Nullable E b) {
        return a == b || (a != null && keyEquals(a, b));
    }

    boolean keyEquals(@Nonnull E a, @Nullable E b) {
        return a.equals(b);
    }

    int nullableKeyHashCode(@Nullable E key) {
        return key != null ? keyHashCode(key) : 0;
    }

    int keyHashCode(@Nonnull E key) {
        return key.hashCode();
    }


    public boolean contains(/* if true nullKeyAllowed */@Nullable/* endif */ Object key) {
        return index(key) >= 0;
    }

    int index(/* if true nullKeyAllowed */@Nullable/* endif */ Object key) {
        /* template Index */ throw new NotGenerated(); /* endtemplate */
    }

    int indexNullKey() {
        /* template Index with null elem */ throw new NotGenerated(); /* endtemplate */
    }


    /* if !(Immutable mutability) */
    @Override
    void allocateArrays(int capacity) {
        /* if Separate kv */
        set = new /* if Generic keyType */Object/* elif Specific keyType //E// endif */[capacity];
        /* elif Parallel kv */
        table = new Object[capacity * 2];
        /* endif */
        /* if true nullKeyAllowed */fillFree();/* endif */
    }

    /* define objFree */
    // if true nullKeyAllowed //FREE// elif false nullKeyAllowed //null// endif //
    // enddefine */

    @Override
    public void clear() {
        super.clear();
        /* if Separate kv */
        fillFree();
        /* elif Parallel kv */
        Object[] tab = table;
        for (int i = 0; i < tab.length; i += 2) {
            tab[i] = /* objFree */FREE/**/;
            tab[i + 1] = null;
        }
        /* endif */
    }

    private void fillFree() {
        /* if Separate kv */
        Arrays.fill(set, /* objFree */FREE/**/);
        /* elif Parallel kv */
        Object[] tab = table;
        for (int i = 0; i < tab.length; i += 2) {
            tab[i] = /* objFree */FREE/**/;
        }
        /* endif */
    }
    /* endif */

    /* if Mutable mutability && !(LHash hash) */
    @Override
    void removeAt(int index) {
        /* if Separate kv */
        set[index] = REMOVED;
        /* elif Parallel kv */
        table[index] = REMOVED;
        /* endif */
    }
    /* endif */
}
