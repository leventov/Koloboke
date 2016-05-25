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

package com.koloboke.collect.impl.hash;

import com.koloboke.collect.*;
import com.koloboke.collect.hash.*;
import com.koloboke.collect.set.ObjSet;
import com.koloboke.collect.set.hash.HashObjSetFactory;

import javax.annotation.Nonnull;

import java.util.Collection;
import java.util.Set;


public abstract class DHashObjSetFactorySO<E> extends ObjHashFactorySO<E>
        implements HashObjSetFactory<E> {

    DHashObjSetFactorySO(HashConfig hashConf, int defaultExpectedSize, boolean isNullAllowed) {
        super(hashConf, defaultExpectedSize, isNullAllowed);
    }

    String keySpecialString() {
        return ",equivalence=" + getEquivalence() +
                ",nullKeyAllowed=" + isNullKeyAllowed();
    }

    boolean keySpecialEquals(HashObjSetFactory<?> other) {
        return getEquivalence().equals(other.getEquivalence()) &&
                isNullKeyAllowed() == other.isNullKeyAllowed();
    }

    @Nonnull
    @Override
    public Equivalence<E> getEquivalence() {
        return Equivalence.defaultEquality();
    }

    /* with Mutable|Updatable|Immutable mutability */
    <E2 extends E> MutableDHashObjSetGO<E2> uninitializedMutableSet() {
        return new MutableDHashObjSet<E2>();
    }
    /* endwith */

    /* with Mutable|Updatable mutability */
    @Override
    @Nonnull
    public <E2 extends E> MutableDHashObjSetGO<E2> newMutableSet(int expectedSize) {
        MutableDHashObjSetGO<E2> set = uninitializedMutableSet();
        set.init(configWrapper, expectedSize);
        return set;
    }

    /* if Updatable mutability */
    @SuppressWarnings("unchecked")
    @Override
    @Nonnull
    public <E2 extends E> MutableDHashObjSetGO<E2> newMutableSet(Iterable<? extends E2> elements,
            int expectedSize) {
        if (elements instanceof ObjCollection) {
            int size;
            if (elements instanceof ObjSet) {
                ObjSet elemSet = (ObjSet) elements;
                if (elements instanceof SeparateKVObjDHash) {
                    SeparateKVObjDHash hash = (SeparateKVObjDHash) elements;
                    if (hash.hashConfig().equals(hashConf) &&
                            elemSet.equivalence().equals(getEquivalence())) {
                        MutableDHashObjSetGO<E2> set = uninitializedMutableSet();
                        set.copy(hash);
                        return set;
                    }
                }
                if (elemSet.equivalence().equals(getEquivalence())) {
                    size = elemSet.size();
                } else {
                    size = expectedSize;
                }
            } else {
                size = expectedSize;
            }
            MutableDHashObjSetGO<E2> set = newMutableSet(size);
            set.addAll((Collection<? extends E2>) elements);
            return set;
        } else {
            int size = getEquivalence() == null && elements instanceof Set ?
                    ((Set) elements).size() :
                    expectedSize;
            MutableDHashObjSetGO<E2> set = newMutableSet(size);
            for (E2 e : elements) {
                set.add(e);
            }
            return set;
        }
    }
    /* endif */
    /* endwith */
}
