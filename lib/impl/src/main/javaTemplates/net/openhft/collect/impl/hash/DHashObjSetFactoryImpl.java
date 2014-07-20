/* with DHash|QHash|LHash hash */
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
import net.openhft.collect.hash.*;
import net.openhft.collect.set.hash.HashObjSetFactory;
import javax.annotation.Nullable;


public final class DHashObjSetFactoryImpl<E> extends DHashObjSetFactoryGO<E> {

    /** For ServiceLoader */
    public DHashObjSetFactoryImpl() {
        this(HashConfig.getDefault(), false);
    }

    public DHashObjSetFactoryImpl(HashConfig hashConf, boolean isNullAllowed) {
        super(hashConf, isNullAllowed);
    }

    @Override
    public <E2> HashObjSetFactory<E2> withEquivalence(@Nullable Equivalence<E2> equivalence) {
        if (equivalence == null) {
            // noinspection unchecked
            return (HashObjSetFactory<E2>) this;
        }
        return new WithCustomEquivalence<E2>(getHashConfig(), isNullKeyAllowed(), equivalence);
    }

    @Override
    public HashObjSetFactory<E> withHashConfig(HashConfig hashConf) {
        if (LHashCapacities.configIsSuitableForMutableLHash(hashConf))
            return new LHashObjSetFactoryImpl<E>(hashConf, isNullKeyAllowed());
        return /* with DHash|QHash hash */
                new DHashObjSetFactoryImpl<E>(hashConf, isNullKeyAllowed())/* endwith */;
    }

    @Override
    public HashObjSetFactory<E> withNullKeyAllowed(boolean nullAllowed) {
        if (nullAllowed == isNullKeyAllowed())
            return this;
        return new DHashObjSetFactoryImpl<E>(getHashConfig(), nullAllowed);
    }

    static final class WithCustomEquivalence<E> extends DHashObjSetFactoryGO<E> {
        final Equivalence<E> equivalence;

        public WithCustomEquivalence(HashConfig hashConf, boolean isNullAllowed,
                Equivalence<E> equivalence) {
            super(hashConf, isNullAllowed);
            this.equivalence = equivalence;
        }

        @Override
        public Equivalence<E> getEquivalence() {
            return equivalence;
        }

        /* with Mutable|Updatable|Immutable mutability */
        @Override
        <E2 extends E> MutableDHashObjSetGO<E2> uninitializedMutableSet() {
            MutableDHashObjSet.WithCustomEquivalence<E2> set =
                    new MutableDHashObjSet.WithCustomEquivalence<E2>();
            set.equivalence = equivalence;
            return set;
        }
        /* endwith */

        @Override
        public <E2> HashObjSetFactory<E2> withEquivalence(@Nullable Equivalence<E2> equivalence) {
            if (equivalence == null)
                return new DHashObjSetFactoryImpl<E2>(getHashConfig(), isNullKeyAllowed());
            if (this.equivalence.equals(equivalence)) {
                // noinspection unchecked
                return (HashObjSetFactory<E2>) this;
            }
            return new WithCustomEquivalence<E2>(getHashConfig(), isNullKeyAllowed(), equivalence);
        }

        @Override
        public HashObjSetFactory<E> withHashConfig(HashConfig hashConf) {
            if (LHashCapacities.configIsSuitableForMutableLHash(hashConf))
                return new LHashObjSetFactoryImpl.WithCustomEquivalence<E>(
                        hashConf, isNullKeyAllowed(), equivalence);
            /* with DHash|QHash hash */
            return new DHashObjSetFactoryImpl.WithCustomEquivalence<E>(
                    hashConf, isNullKeyAllowed(), equivalence);
            /* endwith */
        }

        @Override
        public HashObjSetFactory<E> withNullKeyAllowed(boolean nullAllowed) {
            if (nullAllowed == isNullKeyAllowed())
                return this;
            return new DHashObjSetFactoryImpl.WithCustomEquivalence<E>(
                    getHashConfig(), nullAllowed, equivalence);
        }
    }
}
