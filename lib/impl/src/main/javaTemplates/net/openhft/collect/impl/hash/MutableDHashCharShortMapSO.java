/* with
 DHash|QHash|LHash hash
 char|byte|short|int|long|float|double|obj key
 short|byte|char|int|long|float|double value
 Mutable|Updatable|Immutable mutability
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
import javax.annotation.Nonnull;


public abstract class MutableDHashCharShortMapSO/*<>*/
        extends MutableDHashCharKeyMap/* if obj key //<K>// endif */
        implements HashCharShortMap/*<>*/, InternalCharShortMapOps/*<>*/, CharShortDHash {

    /* bits */short[] values;


    void copy(CharShortDHash hash) {
        super.copy(hash);
        values = hash.valueArray().clone();
    }

    void move(CharShortDHash hash) {
        super.move(hash);
        values = hash.valueArray();
    }


    @Override
    @Nonnull
    public /* bits */short[] valueArray() {
        return values;
    }

    /* with internal|simple version */
    /* if simple version || float|double value */
    /* define valueBits */
    /* if internal version //// bits //short// elif simple version //short// endif */
    /* enddefine */
    int valueIndex(/* valueBits */short/**/ value) {
        /* template ValueIndex */ throw new NotGenerated(); /* endtemplate */
    }

    /* if simple version */@Override public/* endif */
    boolean containsValue(/* valueBits */short/**/ value) {
        return valueIndex(value) >= 0;
    }

    boolean removeValue(/* valueBits */short/**/ value) {
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
    /* endif */
    /* endwith */

    @Override
    public boolean containsValue(Object value) {
        return containsValue(((Short) value).shortValue());
    }

    /* if !(Immutable mutability) */
    @Override
    void rehash(int newCapacity) {
        /* template Rehash */
    }

    int insert(/* bits */char key, /* bits */short value) {
        /* template Insert with internal version */ throw new NotGenerated(); /* endtemplate */
    }

    /* if obj key */
    int insertNullKey(/* bits */short value) {
        /* template Insert with null key internal version */throw new NotGenerated();/*endtemplate*/
    }
    /* endif */

    @Override
    void allocateArrays(int capacity) {
        super.allocateArrays(capacity);
        values = new /* bits */short[capacity];
    }
    /* endif */

    /* if Mutable mutability */
    @Override
    void removeAt(int index) {
        // if !(LHash hash) */
        incrementModCount();
        super.removeAt(index);
        postRemoveHook();
        /* elif LHash hash //
        /* template RemoveAt */
        // endif */
    }
    /* endif */
}
