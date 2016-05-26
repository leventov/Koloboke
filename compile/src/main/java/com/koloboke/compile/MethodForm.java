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

package com.koloboke.compile;

import java.lang.annotation.*;


/**
 * Indicates that the annotated method should be overridden in the Koloboke Compile-generated
 * implementation of the containing type, based on the method form having parameter and return
 * types of the annotated method and name, specified by this annotation. {@code @MethodForm}
 * effectively just alters the method form matching algorithm (see description in the specification
 * of <a href="{@docRoot}/com/koloboke/compile/KolobokeMap.html#method-form-matching">
 * {@code @KolobokeMap}</a> or
 * <a href="{@docRoot}/com/koloboke/compile/KolobokeSet.html#set-method-form-matching">
 * {@code @KolobokeSet}</a>).
 *
 * <p>Most commonly this annotation could be used to decorate Koloboke Compile-generated methods,
 * e. g. synchronize them, make extra argument checks, etc. In subclasses, this is as simple as
 * overriding a method and calling {@code super.methodName()} in the body, but since the implemented
 * type is a supertype of the Koloboke Compile-generated class, and non-abstract methods in the
 * implemented type <i>underride</i> methods in the generated class, decoration is a little more
 * complicated:<pre><code>
 * &#064;KolobokeMap
 * public abstract class SynchronizedMap&lt;K, V&gt; {
 *     public static &lt;K, V&gt; SynchronizedMap&lt;K, V&gt; withExpectedSize(int expectedSize) {
 *         return new KolobokeSynchronizedMap&lt;K, V&gt;(expectedSize);
 *     }
 *
 *     public final synchronized V get(K key) {
 *         return subGet(key);
 *     }
 *
 *     public final synchronized V put(K key, V value) {
 *         return subPut(key, value);
 *     }
 *
 *     public final synchronized int size() {
 *         return subSize();
 *     }
 *
 *     &#064;MethodForm("get")
 *     abstract V subGet(K key);
 *
 *     &#064;MethodForm("put")
 *     abstract V subPut(K key, V value);
 *
 *     &#064;MethodForm("size")
 *     abstract int subSize();
 * }</code></pre>
 *
 * <p>Only abstract methods could be annotated with {@code @MethodForm} (i. e. they must be
 * implemented by Koloboke Compile, not the user). If several abstract methods in a
 * {@code @KolobokeMap}- or {@code @KolobokeSet}-annotated type have equivalent parameter types and
 * have same method form name (either specified by {@code @MethodForm} or in the absence of this
 * annotation, equal to the method name), Koloboke Compile emits a compilation error message and
 * doesn't generate an implementation for such type.
 *
 * <p>By default, if there are usages (calls or method references) in the Koloboke Compile-generated
 * implementation class of the method with the name, specified by this {@code @MethodForm}
 * annotation, and parameter types of the annotated method, Koloboke Compile replaces usages of this
 * method with the annotated method. If {@link #replaceUsages()} is {@code false}, Koloboke Compile
 * doesn't perform this forwarding, and the implementation methods may call the decorated method
 * implementation.
 *
 * <p>The annotated method could have any name, but it's own signature (without the
 * {@code @MethodForm} annotation) shouldn't <a
 * href="{@docRoot}/com/koloboke/compile/KolobokeMap.html#method-form-matching">match</a> another
 * method form.
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface MethodForm {

    /**
     * The name of the method form, that Koloboke Compile should implement in the overriding method,
     * different from the name of the annotated method.
     *
     * @return name of the method form, that Koloboke Compile should implement in the overriding
     * method
     */
    String value();

    /**
     * Tells whether usages (calls or method references) in the Koloboke Compile-generated
     * implementation class of the method with the name, specified by this {@code @MethodForm}
     * annotation, and parameter types of the annotated method, should be replaced with usages of
     * the annotated method or not. Only usages, which statically resolve to the prototype method,
     * i. e. where the type of the method call receiver is the implemented type or the generated
     * implementation type, but not a supertype of the implemented type, are replaced.
     *
     * <p>If {@code replaceUsages} is configured to {@code false} and usages are not replaced, the
     * same risks as <a href="{@docRoot}/com/koloboke/compile/KolobokeMap.html#underriding-warning">
     * with usual method underriding</a> appear.
     *
     * @return {@code true} if Koloboke Compile should replace usages of the method form prototype
     * methods in the generated implementation with the {@code @MethodForm}-annotated method
     */
    boolean replaceUsages() default true;
}
