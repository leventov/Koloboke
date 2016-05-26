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

package com.koloboke.compile.fromdocs;

import com.google.common.base.Preconditions;
import com.koloboke.collect.set.IntSet;
import com.koloboke.compile.KolobokeSet;
import com.koloboke.compile.MethodForm;

import javax.annotation.Nonnull;


@KolobokeSet
public abstract class PositiveNumbersSet implements IntSet {

    public static IntSet withExpectedSize(int expectedSize) {
        return new KolobokePositiveNumbersSet(expectedSize);
    }

    /**
     * {@code replaceUsages=false} to make {@code addAll()} to delegate to checking {@code add()}
     * instead of {@code subAdd()}. See {@link MethodForm} javadocs for details.
     */
    @MethodForm(value = "add", replaceUsages = false)
    abstract boolean subAdd(int e);

    @Override
    public final boolean add(@Nonnull Integer e) {
        return add((int) e);
    }

    @Override
    public final boolean add(int e) {
        Preconditions.checkArgument(e > 0, "elements of this set must be positive, {} given", e);
        return subAdd(e);
    }
}
