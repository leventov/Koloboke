/* with
 char|byte|short|int|long|float|double|object t
 short|byte|char|int|long|float|double u
*/
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
 * Represents a function that accepts
 * // if !(char t char u) && !(byte t byte u) && !(short t short u) && !(int t int u) &&
 !(long t long u) && !(float t float u) && !(double t double u) //
 * // if !(obj t) ////a// {@code char}// elif obj t //an object// endif //-valued and
 * //a// {@code short}-valued argument
 * // elif char t char u || byte t byte u || short t short u || int t int u || long t long u ||
 float t float u || double t double u //
 * two {@code char}-valued arguments
 * // endif //
 * and produces //a// {@code short}-valued result.
 * This is the {@code (// if !(obj t) //char// elif obj t //reference// endif //, short, short)}
 * specialization of {@link BiFunction}.
 *
 * // if char t char u || byte t byte u || short t short u || int t int u || long t long u ||
 float t float u || double t double u //
 * <p>Unlike {@link CharBinaryOperator}, this function is supposed to accept heterogeneous
 * arguments, e. g. key and value in {@link net.openhft.collect.map.CharCharMap#compute(char,
 * CharCharToCharFunction)} method.
 * // endif //
 *
 * // if obj t // @param //<>// the type of the first argument to the function// endif //
 * @see BiFunction
 */
/* if JDK8 jdk */@FunctionalInterface/* endif */
public interface CharShortToShortFunction/*<>*/ {

    /**
     * Applies this function to the given arguments.
     *
     * @param a the first function argument
     * @param b the second function argument
     * @return the function result
     */
    short applyAsShort(char a, short b);
}
