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

import net.openhft.collect.HashConfig;
import net.openhft.collect.impl.AbstractContainer;


public abstract class ImmutableDHash extends AbstractContainer implements DHash {

    ////////////////////////////
    // Fields

    private HashConfig hashConfig;

    /** The current number of occupied slots in the hash. */
    private int size;


    final void copy(DHash hash) {
        this.hashConfig = hash.hashConfig();
        this.size = hash.size();
    }

    final void init(HashConfig hashConfig, int size) {
        this.hashConfig = hashConfig;
        this.size = size;
    }


    ////////////////////////
    // Getters

    @Override
    public final int size() {
        return size;
    }


    @Override
    public HashConfig hashConfig() {
        return hashConfig;
    }


    @Override
    public final float currentLoad() {
        // Division in double to minimize precision loss
        return ( float ) (((double) size) / capacity());
    }


    @Override
    public final boolean noRemoved() {
        return true;
    }

    @Override
    public final int modCount() {
        return 0;
    }

    @Override
    public final int freeSlots() {
        return capacity() - size;
    }

    @Override
    public final int removedSlots() {
        return 0;
    }


    ///////////////////////////////////
    // Mutation operations aren't supported


    public final void clear() {
        throw new UnsupportedOperationException();
    }


    @Override
    public boolean ensureCapacity(long minSize) {
        throw new UnsupportedOperationException();
    }


    @Override
    public boolean shrink() {
        throw new UnsupportedOperationException();
    }
}
