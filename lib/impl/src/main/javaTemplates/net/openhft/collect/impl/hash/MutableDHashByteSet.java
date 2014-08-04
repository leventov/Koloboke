/* with
 DHash|QHash|LHash hash
 byte|char|short|int|long|float|double|obj elem
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

import net.openhft.collect.Equivalence;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public final class MutableDHashByteSet/*<>*/ extends MutableDHashByteSetGO/*<>*/ {

    /* if obj elem */
    static final class WithCustomEquivalence<E> extends MutableDHashObjSetGO<E> {
        Equivalence<? super E> equivalence;

        @Override
        @Nonnull
        public Equivalence<E> equivalence() {
            // noinspection unchecked
            return (Equivalence<E>) equivalence;
        }

        @Override
        boolean nullableKeyEquals(@Nullable E a, @Nullable E b) {
            return equivalence.nullableEquivalent(a, b);
        }

        @Override
        boolean keyEquals(@Nonnull E a, @Nullable E b) {
            return b != null && equivalence.equivalent(a, b);
        }

        @Override
        int nullableKeyHashCode(@Nullable E key) {
            return equivalence.nullableHash(key);
        }

        @Override
        int keyHashCode(@Nonnull E key) {
            return equivalence.hash(key);
        }
    }
    /* endif */
}
