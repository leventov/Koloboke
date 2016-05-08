/* with
 DHash|QHash|LHash hash
 byte|char|short|int|long|float|double|obj key
 obj value
 Mutable|Updatable|Immutable mutability
 Separate|Parallel kv
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

package com.koloboke.collect.impl.hash;

import com.koloboke.collect.impl.*;
import com.koloboke.collect.map.hash.HashByteObjMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.*;


public abstract class MutableDHashSeparateKVByteObjMapSO</* if obj key //K, // endif */V>
        extends MutableDHashSeparateKVByteKeyMap/* if obj key //<K>// endif */
        implements HashByteObjMap</* if obj key //K, // endif */V>,
        InternalByteObjMapOps</* if obj key //K, // endif */V>, SeparateKVByteObjDHash {

    /* if Separate kv */
    V[] values;

    void copy(SeparateKVByteObjDHash hash) {
        super.copy(hash);
        // noinspection unchecked
        values = (V[]) hash.valueArray().clone();
    }

    void move(SeparateKVByteObjDHash hash) {
        super.move(hash);
        // noinspection unchecked
        values = (V[]) hash.valueArray();
    }

    @Override
    @Nonnull
    public Object[] valueArray() {
        return values;
    }
    /* endif */

    boolean nullableValueEquals(@Nullable V a, @Nullable V b) {
        return a == b || (a != null && valueEquals(a, b));
    }

    boolean valueEquals(@Nonnull V a, @Nullable V b) {
        return a.equals(b);
    }

    int nullableValueHashCode(@Nullable V value) {
        return value != null ? valueHashCode(value) : 0;
    }

    int valueHashCode(@Nonnull V value) {
        return value.hashCode();
    }


    int valueIndex(@Nullable Object value) {
        if (value == null)
            return nullValueIndex();
        /* template ValueIndex */ throw new NotGenerated(); /* endtemplate */
    }

    private int nullValueIndex() {
        /* template ValueIndex with null value */ throw new NotGenerated(); /* endtemplate */
    }

    @Override
    public boolean containsValue(Object value) {
        return valueIndex(value) >= 0;
    }

    boolean removeValue(@Nullable Object value) {
        /* if Mutable mutability */
        int index = valueIndex(value);
        if (index >= 0) {
            removeAt(index);
            return true;
        } else {
            return false;
        }
        /* elif !(Mutable mutability) //
        throw new UnsupportedOperationException();
        // endif */
    }


    /* if !(Immutable mutability) */
    int insert(/* bits */byte key, V value) {
        /* template Insert with internal version */ throw new NotGenerated(); /* endtemplate */
    }

    /* if obj key */
    int insertNullKey(V value) {
        /* template Insert with null key */ throw new NotGenerated(); /* endtemplate */
    }
    /* endif */

    /* if Separate kv */
    @Override
    void allocateArrays(int capacity) {
        super.allocateArrays(capacity);
        // noinspection unchecked
        /* if Generic valueType */
        values = (V[]) new Object[capacity];
        /* elif Specific valueType //
        values = new V[capacity];
        // endif */
    }

    @Override
    public void clear() {
        super.clear();
        Arrays.fill(values, null);
    }
    /* endif */
    /* endif */
}
