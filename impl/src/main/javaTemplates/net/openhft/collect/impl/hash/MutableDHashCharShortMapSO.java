/* with
 char|byte|short|int|long|float|double|obj key
 short|byte|char|int|long|float|double value
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
import net.openhft.collect.map.hash.HashCharShortMap;
import org.jetbrains.annotations.NotNull;


public abstract class MutableDHashCharShortMapSO/*<>*/
        extends MutableDHashCharKeyMap/* if obj key //<K>// endif */
        implements HashCharShortMap/*<>*/, InternalCharShortMapOps/*<>*/, CharShortDHash {

    short[] values;


    final void copy(CharShortDHash hash) {
        super.copy(hash);
        values = hash.valueArray().clone();
    }

    final void move(CharShortDHash hash) {
        super.move(hash);
        values = hash.valueArray();
    }


    @Override
    @NotNull
    public short[] valueArray() {
        return values;
    }

    int valueIndex(short value) {
        /* template ValueIndex */ throw new NotGenerated(); /* endtemplate */
    }

    @Override
    public boolean containsValue(short value) {
        return valueIndex(value) >= 0;
    }

    @Override
    public boolean containsValue(Object value) {
        return containsValue(((Short) value).shortValue());
    }

    boolean removeValue(short value) {
        /* if Mutable mutability */
        int index = valueIndex(value);
        if (index >= 0) {
            removeAt(index);
            return true;
        } else {
            return false;
        }
        /* elif Immutable mutability //
        throw new UnsupportedOperationException();
        // endif */
    }

    /* if Mutable mutability */
    @Override
    void rehash(int newCapacity) {
        /* template Rehash */
    }

    int insert(char key, short value) {
        /* template Insert */ throw new NotGenerated(); /* endtemplate */
    }

    /* if obj key */
    int insertNullKey(short value) {
        /* template Insert with null key */ throw new NotGenerated(); /* endtemplate */
    }
    /* endif */


    @Override
    void allocateArrays(int capacity) {
        super.allocateArrays(capacity);
        values = new short[capacity];
    }
    /* endif */
}
