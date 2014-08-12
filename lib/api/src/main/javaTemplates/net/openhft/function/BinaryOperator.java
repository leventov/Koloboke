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
 * // if !(JDK8 jdk) //
 * Represents an operation upon two operands of the same type, producing a result
 * of the same type as the operands.  This is a specialization of
 * {@link BiFunction} for the case where the operands and the result are all of
 * the same type.
 *
 * @param <T> the type of the operands and result of the operator
 * @see BiFunction
 * @see UnaryOperator
 * // elif JDK8 jdk //
 * @deprecated this interface is present for backward compatibility with the version of this library
 *             for Java 6 or 7, use {@link java.util.function.BinaryOperator} instead.
 * // endif //
 */
/* if JDK8 jdk */@FunctionalInterface @Deprecated/* endif */
public interface BinaryOperator<T> extends BiFunction<T, T, T>
        /* if JDK8 jdk */, java.util.function.BinaryOperator<T>/* endif */ {
}
