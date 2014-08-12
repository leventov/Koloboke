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
 * Represents an operation on a single {@code char}-valued operand that produces
 * //a// {@code char}-valued result.  This is the primitive type specialization of
 * {@link UnaryOperator} for {@code char}.
 *
 * @see UnaryOperator
 * // elif int|long|double t JDK8 jdk //
 * @deprecated this interface is present for backward compatibility with the version of this library
 *             for Java 6 or 7, use {@link java.util.function.CharUnaryOperator} instead.
 * // endif //
 */
/* if JDK8 jdk */@FunctionalInterface/* endif */
/* if int|long|double t JDK8 jdk */@Deprecated/* endif */
public interface CharUnaryOperator/* if int|long|double t JDK8 jdk //
        extends java.util.function.CharUnaryOperator// endif */ {

    /* if !(int|long|double t JDK8 jdk) */
    /**
     * Applies this operator to the given operand.
     *
     * @param operand the operand
     * @return the operator result
     */
    char applyAsChar(char operand);
    /* endif */
}
