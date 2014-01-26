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


package net.openhft.collect.impl;

import net.openhft.collect.Container;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;


public abstract class AbstractView<E> implements Collection<E>, Container {

    @Override
    public boolean ensureCapacity(int minSize) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final boolean isEmpty() {
        return size() == 0;
    }

    public abstract String toString();

    @Override
    public final boolean add(E e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final boolean addAll(@NotNull Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }
}
