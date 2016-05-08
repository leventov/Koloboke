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

import com.koloboke.collect.Equivalence;
import com.koloboke.collect.hash.HashConfig;

import javax.annotation.Nonnull;


abstract class ObjHashFactorySO<E> extends AbstractHashFactory {

    private final boolean isNullAllowed;

    ObjHashFactorySO(HashConfig hashConf, int defaultExpectedSize, boolean isNullAllowed) {
        super(hashConf, defaultExpectedSize);
        this.isNullAllowed = isNullAllowed;
    }

    public boolean isNullKeyAllowed() {
        return isNullAllowed;
    }

    @Nonnull
    abstract Equivalence<E> getEquivalence();

    int keySpecialHashCode(int hashCode) {
        hashCode = hashCode * 31 + getEquivalence().hashCode();
        return hashCode * 31 + (isNullKeyAllowed() ? 1 : 0);
    }
}
