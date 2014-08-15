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

package net.openhft.koloboke.collect;

import net.openhft.koloboke.function.Consumer;

import javax.annotation.Nonnull;
import java.util.Iterator;


/**
 * Extends {@link Iterator} for the symmetry with primitive specializations.
 *
 * <p>See the <a href="{@docRoot}/overview-summary.html#iteration">comparison of iteration ways</a>
 * in the library.
 *
 * <p>Iterators of updatable and immutable collections don't support {@link #remove()}
 * operation. <a href="{@docRoot}/overview-summary.html#mutability">More about mutability profiles.
 * </a>
 *
 * @param <E> the type of elements returned by this iterator
 * @see ObjCollection#iterator()
 */
public interface ObjIterator<E> extends Iterator<E> {

    /* if !(JDK8 jdk) */
    /**
     * Performs the given action for each remaining element until all elements
     * have been processed or the action throws an exception.  Actions are
     * performed in the order of iteration, if that order is specified.
     * Exceptions thrown by the action are relayed to the caller.
     *
     * @param action the action to be performed for each element
     */
    void forEachRemaining(@Nonnull Consumer<? super E> action);
    /* endif */
}
