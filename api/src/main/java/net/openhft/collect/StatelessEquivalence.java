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

package net.openhft.collect;

/**
 * Base class for final (or anonymous) stateless {@code Equivalence} implementations.
 *
 * <p>Override {@link #toString()} if implementation is anonymous and you want to log
 * or pretty print it, because otherwise {@code String} representation
 * would be {@code ""} (empty {@code String}).
 */
public abstract class StatelessEquivalence<T> extends Equivalence<T> {

    /**
     * Returns {@code true} if the given object is also an instance of this equivalence class.
     *
     * @return {@code o != null && o.getClass() == getClass()}
     */
    @Override
    public boolean equals(Object o) {
        return o != null && o.getClass() == getClass();
    }

    /**
     * Returns hash code of this equivalence class.
     */
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    /**
     * Returns simple name of this equivalence class.
     *
     * @return {@code getClass().getSimpleName()}
     */
    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
