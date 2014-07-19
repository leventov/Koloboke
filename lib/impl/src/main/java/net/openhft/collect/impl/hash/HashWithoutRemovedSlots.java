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

import net.openhft.collect.hash.HashConfig;
import net.openhft.collect.impl.AbstractContainer;


public abstract class HashWithoutRemovedSlots extends AbstractContainer implements Hash {

    @Override
    public final HashConfig hashConfig() {
        return configWrapper().config();
    }

    @Override
    public final boolean noRemoved() {
        return true;
    }

    @Override
    public final int freeSlots() {
        return capacity() - size();
    }

    @Override
    public final int removedSlots() {
        return 0;
    }

    @Override
    public final double currentLoad() {
        return ((double) size()) / (double) capacity();
    }
}
