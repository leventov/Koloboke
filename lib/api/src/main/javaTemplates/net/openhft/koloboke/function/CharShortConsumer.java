/* with
 char|byte|short|int|long|float|double|obj t
 short|byte|char|int|long|float|double|obj u
*/
/* if !(obj t obj u) */
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

package net.openhft.koloboke.function;

/**
 * // if !(obj t int|long|double u JDK8 jdk) //
 * Represents an operation that accepts
 * // if !(char t char u) && !(byte t byte u) && !(short t short u) && !(int t int u) &&
 !(long t long u) && !(float t float u) && !(double t double u) //
 * // if !(obj t) ////a// {@code char}// elif obj t //an object// endif //-valued and
 * // if !(obj u) ////a// {@code short}// elif obj u //an object// endif //-valued argument,
 * // elif char t char u || byte t byte u || short t short u || int t int u || long t long u ||
 float t float u || double t double u //
 * two {@code char}-valued arguments
 * // endif //
 * and returns no result.  This is
 * the {@code (// if !(obj t) //char// elif obj t //reference// endif //,
 * // if !(obj u) //short// elif obj u//reference// endif //)} specialization of {@link BiConsumer}.
 * Unlike most other functional interfaces, {@code CharShortConsumer} is
 * expected to operate via side-effects.
 *
 * // if obj t //@param <T> the type of the first argument to the operation// endif //
 * // if obj u //@param <U> the type of the second argument the operation// endif //
 * @see BiConsumer
 * // elif obj t int|long|double u JDK8 jdk //
 * @deprecated this interface is present for backward compatibility with the version of this library
 *             for Java 6 or 7, use {@link java.util.function.ObjShortConsumer} instead.
 * // endif //
 */
/* if JDK8 jdk */@FunctionalInterface/* endif */
/* if obj t int|long|double u JDK8 jdk */@Deprecated/* endif */
public interface CharShortConsumer/*<>*//* if obj t int|long|double u JDK8 jdk //
        extends java.util.function.ObjShortConsumer<T>// endif */ {

    /* if !(obj t int|long|double u JDK8 jdk) */
    /**
     * Performs this operation on the given arguments.
     *
     * @param a the first input argument
     * @param b the second input argument
     */
    void accept(char a, short b);
    /* endif */
}
