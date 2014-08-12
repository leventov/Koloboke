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
 * Represents a predicate (boolean-valued function) of two arguments.  This is
 * the two-arity specialization of {@link Predicate}.
 *
 * @param <T> the type of the first argument to the predicate
 * @param <U> the type of the second argument the predicate
 * @see Predicate
 * // elif JDK8 jdk //
 * @deprecated this interface is present for backward compatibility with the version of this library
 *             for Java 6 or 7, use {@link java.util.function.BiPredicate} instead.
 * // endif //
 */
/* if JDK8 jdk */@FunctionalInterface @Deprecated/* endif */
public interface BiPredicate<T, U>
        /* if JDK8 jdk */extends java.util.function.BiPredicate<T, U> /* endif */{

    /* if !(JDK8 jdk) */
    /**
     * Evaluates this predicate on the given arguments.
     *
     * @param t the first input argument
     * @param u the second input argument
     * @return {@code true} if the input arguments match the predicate,
     * otherwise {@code false}
     */
    public boolean test(T t, U u);
    /* endif */
}
