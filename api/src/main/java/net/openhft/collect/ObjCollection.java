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

import net.openhft.function.Consumer;
import net.openhft.function.Predicate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;


//TODO doc
public interface ObjCollection<E> extends Collection<E>, Container {

    @Nullable
    Equivalence<E> equivalence();

    /* if JDK8 jdk //@Override// endif */
    void forEach(Consumer<? super E> action);

    boolean forEachWhile(Predicate<? super E> predicate);

    @NotNull
    ObjCursor<E> cursor();

    /* if JDK8 jdk //@Override// endif */
    boolean removeIf(Predicate<? super E> filter);
}
