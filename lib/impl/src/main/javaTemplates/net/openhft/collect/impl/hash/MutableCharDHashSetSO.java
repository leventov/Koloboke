/* with
 DHash|QHash|LHash hash
 char|byte|short|int|long|float|double|obj elem
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

import net.openhft.collect.Equivalence;
import net.openhft.collect.impl.NotGenerated;

import java.util.ConcurrentModificationException;


public abstract class MutableCharDHashSetSO/*<>*/
        extends MutableCharDHashGO/*<>*/ {

    /* if obj elem */
    public Equivalence/*<>*/ equivalence() {
        return null;
    }
    /* endif */


    /* if Mutable mutability */
    @Override
    void rehash(int newCapacity) {
        /* template Rehash */
    }

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

    @Override
    public void clear() {
        int mc = modCount() + 1;
        super.clear();
        if (mc != modCount())
            throw new ConcurrentModificationException();
    }
    /* endif */
}
