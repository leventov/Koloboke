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

package net.openhft.function;


/**
 * // if !(int|long|double t JDK8 jdk) //
 * Represents an operation that accepts a single {@code char}-valued argument and
 * returns no result.  This is the primitive type specialization of
 * {@link Consumer} for {@code char}.  Unlike most other functional interfaces,
 * {@code CharConsumer} is expected to operate via side-effects.
 *
 * @see Consumer
 * // elif int|long|double t JDK8 jdk //
 * @deprecated this interface is present for backward compatibility with the version of this library
 *             for Java 6 or 7, use {@link java.util.function.CharConsumer} instead.
 * // endif //
 */
/* if JDK8 jdk */@FunctionalInterface/* endif */
/* if int|long|double t JDK8 jdk */@Deprecated/* endif */
public interface CharConsumer/* if int|long|double t JDK8 jdk //
        extends java.util.function.CharConsumer// endif */ {

    /* if !(int|long|double t JDK8 jdk) */
    /**
     * Performs this operation on the given argument.
     *
     * @param value the input argument
     */
    void accept(char value);
    /* endif */
}
