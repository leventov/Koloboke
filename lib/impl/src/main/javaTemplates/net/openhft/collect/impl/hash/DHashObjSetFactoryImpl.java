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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public final class DHashObjSetFactoryImpl<E> extends DHashObjSetFactoryGO<E> {

    /** For ServiceLoader */
    public DHashObjSetFactoryImpl() {
        this(HashConfig.getDefault(), 10, false);
    }

    /* define commonArgDef //HashConfig hashConf, int defaultExpectedSize, boolean isNullKeyAllowed
    // enddefine */

    /* define commonArgApply //hashConf, defaultExpectedSize, isNullKeyAllowed// enddefine */

    /* define commonArgGet //getHashConfig(), getDefaultExpectedSize(), isNullKeyAllowed()
    // enddefine */

    public DHashObjSetFactoryImpl(/* commonArgDef */) {
        super(/* commonArgApply */);
    }

    @Override
    public <E2> HashObjSetFactory<E2> withEquivalence(@Nonnull Equivalence<E2> equivalence) {
        if (equivalence.equals(Equivalence.defaultEquality())) {
            // noinspection unchecked
            return (HashObjSetFactory<E2>) this;
        }
        return new WithCustomEquivalence<E2>(/* commonArgGet */, equivalence);
    }


    public HashObjSetFactory<E> withNullKeyAllowed(boolean nullAllowed) {
        if (nullAllowed == isNullKeyAllowed())
            return this;
        return thisWith(getHashConfig(), getDefaultExpectedSize(), nullAllowed);
    }

    @Override
    HashObjSetFactory<E> thisWith(/* commonArgDef */) {
        return new DHashObjSetFactoryImpl<E>(/* commonArgApply */);
    }

    /* with DHash|QHash|LHash hash */
    @Override
    HashObjSetFactory<E> dHashLikeThisWith(/* commonArgDef */) {
        return new DHashObjSetFactoryImpl<E>(/* commonArgApply */);
    }
    /* endwith */

    static final class WithCustomEquivalence<E> extends DHashObjSetFactoryGO<E> {
        final Equivalence<E> equivalence;

        public WithCustomEquivalence(/* commonArgDef */, Equivalence<E> equivalence) {
            super(/* commonArgApply */);
            this.equivalence = equivalence;
        }

        @Override
        @Nonnull
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
        public <E2> HashObjSetFactory<E2> withEquivalence(@Nonnull Equivalence<E2> equivalence) {
            if (equivalence.equals(Equivalence.defaultEquality()))
                return new DHashObjSetFactoryImpl<E2>(/* commonArgGet */);
            if (this.equivalence.equals(equivalence)) {
                // noinspection unchecked
                return (HashObjSetFactory<E2>) this;
            }
            return new WithCustomEquivalence<E2>(/* commonArgGet */, equivalence);
        }

        @Override
        public HashObjSetFactory<E> withNullKeyAllowed(boolean nullAllowed) {
            if (nullAllowed == isNullKeyAllowed())
                return this;
            return thisWith(getHashConfig(), getDefaultExpectedSize(), nullAllowed);
        }

        @Override
        HashObjSetFactory<E> thisWith(/* commonArgDef */) {
            return new WithCustomEquivalence<E>(/* commonArgApply */, equivalence);
        }

        /* with DHash|QHash|LHash hash */
        @Override
        HashObjSetFactory<E> dHashLikeThisWith(/* commonArgDef */) {
            return new DHashObjSetFactoryImpl.WithCustomEquivalence<E>(
                /* commonArgApply */, equivalence);
        }
        /* endwith */
    }
}
