/* with DHash|LHash hash */
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
import net.openhft.collect.ObjHashConfig;
import net.openhft.collect.set.hash.HashObjSetFactory;
import javax.annotation.Nullable;


public final class DHashObjSetFactoryImpl<E> extends DHashObjSetFactoryGO<E> {

    /** For ServiceLoader */
    public DHashObjSetFactoryImpl() {
        this(ObjHashConfig.getDefault());
    }

    public DHashObjSetFactoryImpl(ObjHashConfig conf) {
        super(conf);
    }

    @Override
    public <E2> HashObjSetFactory<E2> withEquivalence(@Nullable Equivalence<E2> equivalence) {
        if (equivalence == null) {
            // noinspection unchecked
            return (HashObjSetFactory<E2>) this;
        }
        return new WithCustomEquivalence<E2>(conf, equivalence);
    }

    @Override
    public HashObjSetFactory<E> withConfig(ObjHashConfig config) {
        if (LHashCapacities.configIsSuitableForMutableLHash(config.getHashConfig()))
            return new LHashObjSetFactoryImpl<E>(config);
        return /* with DHash hash */new DHashObjSetFactoryImpl<E>(config)/* endwith */;
    }

    static final class WithCustomEquivalence<E> extends DHashObjSetFactoryGO<E> {
        final Equivalence<E> equivalence;

        public WithCustomEquivalence(ObjHashConfig conf, Equivalence<E> equivalence) {
            super(conf);
            this.equivalence = equivalence;
        }

        @Override
        public Equivalence<E> getEquivalence() {
            return equivalence;
        }

        @Override
        <E2 extends E> MutableDHashObjSetGO<E2> uninitializedMutableSet() {
            MutableDHashObjSet.WithCustomEquivalence<E2> set =
                    new MutableDHashObjSet.WithCustomEquivalence<E2>();
            set.equivalence = equivalence;
            return set;
        }

        @Override
        <E2 extends E> ImmutableDHashObjSetGO<E2> uninitializedImmutableSet() {
            ImmutableDHashObjSet.WithCustomEquivalence<E2> set =
                    new ImmutableDHashObjSet.WithCustomEquivalence<E2>();
            set.equivalence = equivalence;
            return set;
        }

        @Override
        public <E2> HashObjSetFactory<E2> withEquivalence(@Nullable Equivalence<E2> equivalence) {
            if (equivalence == null)
                return new DHashObjSetFactoryImpl<E2>(conf);
            if (this.equivalence.equals(equivalence)) {
                // noinspection unchecked
                return (HashObjSetFactory<E2>) this;
            }
            return new WithCustomEquivalence<E2>(conf, equivalence);
        }

        @Override
        public HashObjSetFactory<E> withConfig(ObjHashConfig config) {
            if (LHashCapacities.configIsSuitableForMutableLHash(config.getHashConfig()))
                return new LHashObjSetFactoryImpl.WithCustomEquivalence<E>(config, equivalence);
            /* with DHash hash */
            return new DHashObjSetFactoryImpl.WithCustomEquivalence<E>(config, equivalence);
            /* endwith */
        }
    }
}
