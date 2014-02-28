/* with
 double|float elem
 Mutable|Immutable mutability
*/
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

import net.openhft.collect.impl.NotGenerated;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;


public abstract class MutableDoubleDHashSO extends MutableDHash implements DoubleDHash {

    long[] set;

    final void copy(DoubleDHash hash) {
        super.copy(hash);
        set = hash.keys().clone();
    }

    final void move(DoubleDHash hash) {
        super.copy(hash);
        set = hash.keys();
    }

    @NotNull
    @Override
    public long[] keys() {
        return set;
    }

    @Override
    public int capacity() {
        return set.length;
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


    /* if Mutable mutability */
    @Override
    void allocateArrays(int capacity) {
        set = new long[capacity];
        Arrays.fill(set, FREE_BITS);
    }

    @Override
    void removeAt(int index) {
        set[index] = REMOVED_BITS;
        postRemoveHook();
    }

    @Override
    public void clear() {
        super.clear();
        Arrays.fill(set, FREE_BITS);
    }
    /* endif */
}
