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

package com.koloboke.function;

/**
 * Represents a function that accepts //a// {@code char}-valued and an object-valued argument
 * and returns a result. This is the {@code (char, reference, reference)} specialization
 * of {@link BiFunction}.
 *
 * @param <T> the type of the first argument to the function
 * @param <R> the type of the result of the function
 * @see BiFunction
 */
/* if JDK8 jdk */@FunctionalInterface/* endif */
public interface CharObjFunction<T, R> {

    /**
     * Applies this function to the given arguments.
     *
     * @param v the first function argument
     * @param t the second function argument
     * @return the function result
     */
    R apply(char v, T t);
}
