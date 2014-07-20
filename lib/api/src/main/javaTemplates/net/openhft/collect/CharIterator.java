/* with char|byte|short|int|long|float|double elem */
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

import net.openhft.function.CharConsumer;
import net.openhft.function.Consumer;

import java.util.Iterator;


//TODO doc
public interface CharIterator extends Iterator<Character>
        /* if int|long|double elem JDK8 jdk //, java.util.PrimitiveIterator.OfChar// endif */ {

    /* if int|long|double elem JDK8 jdk //@Override// endif */
    char nextChar();

    /* if JDK8 jdk */
    /**
     * @deprecated Use specialization {@link #forEachRemaining(CharConsumer)} instead
     */
    @Override
    @Deprecated
    void forEachRemaining(Consumer<? super Character> action);
    /* endif */

    /* if int|long|double elem JDK8 jdk //@Override// endif */
    void forEachRemaining(CharConsumer action);

}
