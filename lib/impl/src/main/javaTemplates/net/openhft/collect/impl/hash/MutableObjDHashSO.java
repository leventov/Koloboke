/* with
 DHash|QHash|LHash hash
 object elem
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

import net.openhft.collect.impl.*;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.*;


//TODO doc
public abstract class MutableObjDHashSO<E> extends MutableDHash implements ObjDHash {

    Object[] set;

    void copy(ObjDHash hash) {
        super.copy(hash);
        set = hash.keys().clone();
    }

    void move(ObjDHash hash) {
        super.copy(hash);
        set = hash.keys();
    }

    @Nonnull
    @Override
    public Object[] keys() {
        return set;
    }

    @Override
    public int capacity() {
        return set.length;
    }

    boolean nullableKeyEquals(@Nullable E a, @Nullable E b) {
        return a == b || (a != null && a.equals(b));
    }

    boolean keyEquals(@Nonnull E a, @Nullable E b) {
        return a.equals(b);
    }

    int nullableKeyHashCode(@Nullable E key) {
        return key != null ? key.hashCode() : 0;
    }

    int keyHashCode(@Nonnull E key) {
        return key.hashCode();
    }


    public boolean contains(@Nullable Object key) {
        return index(key) >= 0;
    }

    int index(@Nullable Object key) {
        /* template Index */ throw new NotGenerated(); /* endtemplate */
    }

    int indexNullKey() {
        /* template Index with null elem */ throw new NotGenerated(); /* endtemplate */
    }


    /* if Mutable mutability */
    @Override
    void allocateArrays(int capacity) {
        set = new Object[ capacity ];
        Arrays.fill(set, FREE);
    }

    /* if !(LHash hash) */
    @Override
    void removeAt(int index) {
        set[index] = REMOVED;
    }
    /* endif */

    @Override
    public void clear() {
        super.clear();
        Arrays.fill(set, FREE);
    }
    /* endif */
}
