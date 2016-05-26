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

import com.koloboke.collect.hash.HashConfig;
import com.koloboke.collect.map.ObjIntMap;
import com.koloboke.collect.map.ObjIntMapFactory;
import com.koloboke.collect.map.hash.*;
import com.koloboke.collect.set.hash.HashLongSet;
import com.koloboke.collect.set.hash.HashObjSet;
import com.koloboke.function.BiConsumer;

import java.io.Serializable;
import java.lang.annotation.*;
import java.util.*;


/**
 * Specifies that Koloboke Compile should generate an implementation of the annotated {@linkplain
 * Map}-like class or interface.
 *
 * <h3>Requirements and permissions for {@code @KolobokeMap}-annotated types</h3>
 * <h4><i>{@code Map} model defining</i> method forms</h4>
 * <p>The annotated type must have at least one abstract method (either declared in the body of the
 * type or inherited) <i>matching</i> (see below) some form from the following list:
 * <table BORDER CELLPADDING=3 CELLSPACING=1 summary="Map input method forms">
 *     <tr><th>Return type</th><th>Signature</th><th>Notes</th></tr>
 *     <tr><td ALIGN=RIGHT>{@code ValueType}</td>
 *         <td>{@code put(KeyType key, ValueType value)}</td><td></td></tr>
 *     <tr><td ALIGN=RIGHT>{@code ValueType}</td>
 *         <td>{@code putIfAbsent(KeyType key, ValueType value)}</td><td></td></tr>
 *     <tr><td ALIGN=RIGHT>{@code ValueType}</td>
 *         <td>{@code replace(KeyType key, ValueType value)}</td><td></td></tr>
 *     <tr><td ALIGN=RIGHT>{@code boolean}</td>
 *         <td>{@code replace(KeyType key, ValueType oldValue, ValueType newValue)}</td><td></td>
 *         </tr>
 *     <tr><td ALIGN=RIGHT>{@code void}</td>
 *         <td>{@code justPut(KeyType key, ValueType value)}</td>
 *         <td>Semantically equivalent to {@code put()}, except it doesn't return the previous
 *         mapped value</td></tr>
 *     <tr><td ALIGN=RIGHT>{@code ValueType}</td>
 *         <td>{@code addValue(KeyType key, ValueType addition)}</td>
 *         <td>Only if the {@code ValueType} is a numeric primitive type</td></tr>
 *     <tr><td ALIGN=RIGHT>{@code ValueType}</td>
 *         <td>{@code addValue(KeyType key, ValueType addition, ValueType initialValue)}</td>
 *         <td>Only if the {@code ValueType} is a numeric primitive type</td></tr>
 * </table>
 * Where {@code KeyType} and {@code ValueType} are either some <i>reference types</i> (reference
 * type is a declared type, or an array type, or a type variable) or <i>numeric primitive types</i>
 * ({@code byte}, {@code char}, {@code short}, {@code int}, {@code long}, {@code float} or
 * {@code double}). {@code KeyType} and {@code ValueType} are independent in this regard: i. e. the
 * {@code KeyType} could be {@code int} with the {@code ValueType} of {@link String} or {@code V}
 * (a type variable) or {@code long}, and so on, in any combination.
 *
 * <h4><a name="method-form-matching">Method form matching</a></h4>
 * <p>"A method matches a form" means that the form and the method have the same <i>signature</i>
 * (as defined in the Java Language Specification, §8.4.2), and return type of the form is identical
 * to the return type of the method, or, if the return type of the form is a reference type, it is a
 * <i>subtype</i> of the return type of the method. If the method is annotated with a {@link
 * MethodForm @MethodForm} annotation, for signature comparison, instead of the method's own name,
 * the name specified by the {@code @MethodForm} is taken. The method might declare any exceptions
 * in the {@code throws} clause. Method parameter <i>names</i> are <i>not</i> included in method
 * signatures in the Java language, hence they shouldn't necessarily be the same in the from and the
 * matching method.
 *
 * <h4><a name="type-consistency">{@code KeyType} and {@code ValueType} consistency</a></h4>
 * <p>{@code KeyType} and {@code ValueType} should be the same in all methods (either abstract or
 * not) in the {@code @KolobokeMap}-annotated type, which match the above forms (and the forms
 * defined below). For example, the annotated type couldn't simultaneously have methods {@code
 * int put(K, long)} and {@code String replace(K, String)}, because according to the first method
 * the {@code ValueType} is {@code int}, and according to the second method the {@code ValueType} is
 * {@code String}.
 *
 * <h4><a name="model">Key and value types ({@code Map} model definition)</a></h4>
 * <p>Hereafter in this specification and specifications of other Koloboke Compile annotations, for
 * some {@code @KolobokeMap}-annotated type, the {@code KeyType} and {@code ValueType} appearing in
 * the abstract methods of this type, which match the above forms, are called the <i>key type</i>
 * and the <i>value type</i> of the annotated type.
 *
 * <h4><a name="boxing">"Boxed" and "primitive" versions of method forms for numeric primitive key
 * or value types</a></h4>
 * <p>If either the key type or the value type or both are some numeric primitive types, methods
 * (either abstract or not) could match additional versions of the {@code put()}, {@code
 * putIfAbsent()} or both {@code replace()} forms from the above list: a version with primitive
 * wrapper classes in the positions of the corresponding primitive numeric {@code KeyType} and
 * {@code ValueType}. For example, if the key type of the annotated type is {@code int} and the
 * value type is {@code long}, it could have method {@code long put(int, long)}, or {@code
 * Long put(Integer, Long)}, or both simultaneously. If the key type of the annotated type is {@code
 * String} and the value type is {@code double}, it could have methods {@code
 * double addValue(String, double)} and {@code boolean replace(String, Double, Double)}. Among the
 * methods of some {@code @KolobokeMap}-annotated type some method forms could appear in the
 * "primitive" version, some other forms - only in the "boxed" version, and some forms could appear
 * in both versions. "Mixed" versions and "boxed" versions of {@code justPut()} and both {@code
 * addValue()} forms are disallowed: <span style="text-decoration:line-through;">
 * {@code long put(Integer, long)}</span>, <span style="text-decoration:line-through;">
 * {@code void justPut(String, Double)}</span>.
 *
 * <p>A numeric primitive key or value type could even never appear as itself in the signatures and
 * return types of the methods of the annotated type (but only as a wrapper class in methods
 * matching the "boxed" versions of some forms): for example, a {@code @KolobokeMap}-annotated
 * interface extending {@code Map<Long, Double>} and not defining own methods (hence inheriting
 * abstract methods like {@code Double put(Long, Double)}, which match the "boxed" versions of the
 * defined forms) still have primitive {@code long} key type and primitive {@code double} value
 * type. This is important, because in the generated implementations for such types keys and/or
 * values are stored as primitives, hence insertion of the {@code null} key or value into such map
 * is not possible and always leads to {@code NullPointerException}. The only way to make Koloboke
 * Compile to generate a map implementation that actually stores primitive wrapper objects (though
 * this is a highly questionable goal) is to annotate with {@code @KolobokeMap} a generic abstract
 * class or an interface with key or value type of a type variable, and parameterize it with
 * primitive wrapper class (e. g. {@code Integer}) at the moment of instantiation.
 *
 * <p>Currently Koloboke Compile is able to generate only hash table-based implementations of
 * annotated Map-like classes or interfaces. In the future, ability to generate other kinds of
 * implementations (e. g. tree-based) might be added.
 *
 * <h4>Other method forms</h4>
 * <p>In addition to the method forms specified above, a {@code KolobokeMap}-annotated class or
 * interface could have methods (either abstract or not) matching the following forms:
 * <ul>
 *     <li><b>Forms, prototyped by methods in interfaces from the Koloboke Collections API</b>
 *     <table BORDER CELLPADDING=3 CELLSPACING=1
 *     summary="Forms, prototyped by methods in interfaces from the Koloboke Collections API">
 *         <tr><th>{@code KeyType}</th><th>{@code ValueType}</th>
 *         <th><i>Prototyping interface</i></th></tr>
 *
 *         <tr><td>A reference type</td><td>A reference type</td>
 *         <td>{@link HashObjObjMap HashObjObjMap&lt;KeyType, ValueType&gt;}</td></tr>
 *
 *         <tr><td>A reference type</td><td>A numeric primitive type</td>
 *         <td>{@code HashObjYyyMap<KeyType>}, where {@code Yyy} is a capitalized name of the value
 *         type, e. g. {@link HashObjIntMap HashObjIntMap&lt;KeyType&gt;} if the value type is
 *         {@code int}</td></tr>
 *
 *         <tr><td>A numeric primitive type</td><td>A reference type</td>
 *         <td>{@code HashXxxObjMap<ValueType>}, where {@code Xxx} is a capitalized name of the key
 *         type, e. g. {@link HashLongObjMap HashLongObjMap&lt;ValueType&gt;} if the key type is
 *         {@code long}</td></tr>
 *
 *         <tr><td>A numeric primitive type</td><td>A numeric primitive type</td>
 *         <td>{@code HashXxxYyyMap}, where {@code Xxx} is a capitalized name of the key type and
 *         {@code Yyy} is a capitalized name of the value type, e. g. {@link HashLongIntMap} if the
 *         key type is {@code long} and the value type is {@code int}</td></tr>
 *     </table>
 *
 *     Method forms have signatures and return types of the methods in the prototyping
 *     interface from the third column in the above table, corresponding to the kinds of key and
 *     value types of the {@code @KolobokeMap}-annotated type in the first two columns of in the
 *     same row.
 *
 *     <p>The Koloboke Collections API comes in two different distributions: for Java 6 or 7, and
 *     for Java 8+, and signatures of some methods are different in the same interfaces, provided by
 *     different distributions. For example, the method {@link HashObjObjMap#forEach(BiConsumer)}
 *     has the parameter type of {@link java.util.function.BiConsumer java.util.function.BiConsumer}
 *     in the distribution for Java 8+, and <a
 *     href="http://leventov.github.io/Koloboke/api/1.0/java6/com/koloboke/function/BiConsumer.html"
 *     >{@code com.koloboke.function.BiConsumer}</a> in the distribution for
 *     Java 6 and 7. Possible method forms have signatures of the methods in that version of the
 *     prototyping interface, <i>which is present in the compilation classpath at the moment of
 *     Koloboke Compile generation</i>.
 *
 *     <p>Forms, defined by methods, which are not abstract in the prototyping interface, couldn't
 *     be matched by abstract methods in the {@code KolobokeMap}-annotated class or interface, but
 *     if there are some non-abstract matching methods, they should be consistent with the key and
 *     value types of the annotated type, <a href="#type-consistency">as specified above</a>. This
 *     means, for example, that if the project, containing the {@code @KolobokeMap}-annotated type,
 *     depends on JDK 8 and the Koloboke Collections API distribution for Java 6 and 7 (although
 *     Koloboke Compile emits a warning in this case, suggesting to change the dependency to the
 *     Koloboke Collections API distribution for Java 8+), method {@link
 *     Map#forEach(java.util.function.BiConsumer)} will have a default implementation (i. e. it will
 *     be <i>non-abstract</i>) in the {@link HashObjObjMap} interface, so the
 *     {@code @KolobokeMap}-annotated type couldn't declare or inherit such a method as abstract.
 *
 *     <p>Since <a href="#method-form-matching">as specified above</a> a form allows a matching
 *     method to have reference return type to be a supertype of the return type of the form, it is
 *     not required from e. g. the {@code keySet()} method to have the return type of {@link
 *     HashObjSet HashObjSet&lt;KeyType&gt;} or {@link HashLongSet} (the {@code keySet()} return
 *     types in the {@code HashObjObjMap<KeyType, ValueType>} and {@code HashLongObjMap} interfaces,
 *     respectively). The {@code keySet()} return type could be {@link Set Set&lt;KeyType&gt;} or
 *     {@code Set<Long>}, that are supertypes of {@code HashObjSet<KeyType>} and {@code HashLongSet}
 *     respectively. This is useful, if the {@code @KolobokeMap}-annotated type shouldn't depend on
 *     the Koloboke Collections API.
 *     </li>
 *
 *     <li><b>Method forms, missing in the {@code Map} interface</b>
 *     <table BORDER CELLPADDING=3 CELLSPACING=1
 *     summary="Method forms, missing in the Map interface">
 *         <tr><th>Return type</th><th>Signature and Description</th></tr>
 *         <tr><td ALIGN=RIGHT VALIGN=TOP>{@code boolean}</td><td>{@code justRemove(Object key)}<br>
 *             Semantically equivalent to {@code remove(Object)}, except instead of the previously
 *             mapped value it returns {@code true}, if the removal was successful (actually changed
 *             the map).
 *             </td></tr>
 *         <tr><td ALIGN=RIGHT VALIGN=TOP>{@code boolean}</td>
 *         <td>{@code containsEntry(Object key, Object value)}<br>
 *             Returns {@code true} if {@code remove(Object, Object)} invoked with the same
 *             arguments would find the entry and return {@code true}.</td></tr>
 *     </table>
 *     <br>
 *     </li>
 *
 *     <li><b>Specializations of methods with "raw" parameters in the {@code Map} interface</b><br>
 *     Methods like {@code get()} and {@code remove()} in the {@code Map} interface, and
 *     consequently in subinterfaces (including primitive specializations) from the Koloboke
 *     Collections API, have the key and value parameters of {@code Object} type, that is a
 *     double-edged design decision. Along with the forms, prototyped by those methods, similar
 *     forms with <i>generified</i> parameter types are also allowed in
 *     {@code @KolobokeMap}-annotated types:
 *     <table BORDER CELLPADDING=3 CELLSPACING=1
 *     summary="Specializations of methods with raw parameters in the Map interface">
 *         <tr><th>Return type</th><th>Signature</th><th>Notes</th></tr>
 *         <tr><td ALIGN=RIGHT>{@code ValueType}</td><td>{@code get(KeyType key)}</td><td></td></tr>
 *         <tr><td ALIGN=RIGHT>{@code ValueType}</td>
 *             <td>{@code getOrDefault(KeyType key, ValueType defaultValue)}</td><td></td></tr>
 *         <tr><td ALIGN=RIGHT>{@code boolean}</td>
 *             <td>{@code containsKey(KeyType key)}</td><td></td></tr>
 *         <tr><td ALIGN=RIGHT>{@code boolean}</td>
 *             <td>{@code containsValue(ValueType value)}</td><td></td></tr>
 *         <tr><td ALIGN=RIGHT>{@code boolean}</td>
 *             <td>{@code containsEntry(KeyType key, ValueType value)}</td>
 *             <td>Unless the key and value types are both {@code Object} or unbound type
 *             variables</td></tr>
 *         <tr><td ALIGN=RIGHT>{@code ValueType}</td>
 *             <td>{@code remove(KeyType key)}</td><td></td></tr>
 *         <tr><td ALIGN=RIGHT>{@code ValueType}</td>
 *             <td>{@code removeAsYyy(KeyType key)}</td><td>Only if the key type is a reference type
 *             and the value type is a numeric primitive type. {@code Yyy} is a capitalized name of
 *             the value type.</td></tr>
 *         <tr><td ALIGN=RIGHT>{@code boolean}</td>
 *             <td>{@code justRemove(KeyType key)}</td>
 *             <td>Only if the key type is not an unbound type variable</td></tr>
 *         <tr><td ALIGN=RIGHT>{@code boolean}</td>
 *             <td>{@code remove(KeyType key, ValueType value)}</td><td></td></tr>
 *     </table>
 *
 *     <a name="raw-methods"></a>
 *     <p>Koloboke Compile not only permits generified versions of the
 *     "{@code Object}-parameterized" methods, but <i>effectively opts out the feature of making
 *     queries with arguments of unrelated types for Maps with concrete (not a type variable) key or
 *     value type</i>. For example, if a {@code @KolobokeMap}-annotated class has {@code ArrayList}
 *     key type, it may have a method {@code get(Object)}, but an attempt to call this method with
 *     {@code LinkedList} argument will result in {@code ClassCastException}, with a message like
 *     "LinkedList couldn't be cast to ArrayList". However, if the key type of a
 *     {@code @KolobokeMap}-annotated class or interface is a type variable, and it is {@code
 *     ArrayList} at the instantiation point, {@code map.get(linkedList)} will proceed without
 *     exception and may return some value, if there is a mapping with an equivalent {@code
 *     ArrayList} as the key in this map.
 *     </li>
 * </ul>
 *
 * <h4>Miscellaneous requirements and permissions</h4>
 * <p>The {@code @KolobokeMap}-annotated type could have an abstract method of the form of the
 * {@link Object#equals(Object)} method, only if it is a subtype of {@link Map}.
 *
 * <p>The {@code @KolobokeMap}-annotated type could have any static methods and non-abstract
 * methods with any signatures and return types, as long as they don't match any of the forms
 * defined above (but if they <i>do</i> match some forms, they should have
 * <a href="#type-consistency">consistent</a> signatures and return types).
 *
 * <p>The annotated type could be a nested type, but in this case it should be static and
 * non-private. The annotated type couldn't be an inner or an anonymous class.
 *
 * <p>If the annotated type is an abstract class, it must have exactly one non-private constructor
 * (the implicit no-argument constructor counts).
 *
 * <p>The annotated type couldn't have a {@link KolobokeSet @KolobokeSet} annotation (any type could
 * either have a {@code @KolobokeMap} or a {@code @KolobokeSet} annotation, but not both).
 *
 * <h3>Generated implementation</h3>
 * If the annotated type meets all requirements specified above, Koloboke Compile generates a class
 * that implements the annotated type (a subclass, if the annotated type is an abstract class). If
 * some requirements are not met, Koloboke Compile emits a compile-time error and an implementation
 * class is not generated.
 *
 * <h4>Name</h4>
 * The simple name of the implementation class is the simple name of the implemented type with
 * "Koloboke" prefix. For example, if the name of a {@code @KolobokeMap}-annotated type is {@code
 * MyMap}, the name of the implementation class is {@code KolobokeMyMap}. If the implemented type is
 * a nested type, instead of the simple name of the implemented type, it's canonical name (excluding
 * package) is used with dots replaced with underscores: {@code _}. For example, if a
 * {@code @KolobokeMap}-annotated type named {@code InnerMap} is a nested type in a type named
 * {@code Outer}, the name of the Koloboke Compile-generated implementation class is {@code
 * KolobokeOuter_InnerMap}.
 *
 * <h4>Package</h4>
 * <p>The implementation class is located in the same package, as the implemented type, and has
 * package-private visibility in this package.
 *
 * <h4>Type parameters</h4>
 * <p>If the {@code @KolobokeMap}-annotated type has type parameters, the implementation class has
 * the same number of type parameters with equivalent types in the same order ("equivalence of types
 * of type parameters" essentially means equivalence of their bounds).
 *
 * <h4>Semantics and behaviour</h4>
 * <p>The implementation class is non-abstract. It overrides all abstract methods in the implemented
 * type, and doesn't override non-abstract methods. Methods in the implementation class, overriding
 * some methods in the implemented type, have contracts of the methods in interfaces from the
 * Koloboke Collections API, prototyping the forms, matched by the overridden methods in the
 * implemented type. If a form of an overridden method is not prototyped by methods in interfaces
 * from the Koloboke Collections API (e. g. {@code justPut()} and {@code justRemove()} forms, which
 * are not present in any interfaces from the Koloboke Collections API), contracts of overriding
 * methods in the implementation class are specified in a "Notes" column in one of the tables above,
 * where the corresponding form is defined.
 *
 * <h4><a name="underriding-warning">Method underriding requires great care</a></h4>
 * <p>The implementation may call <i>any</i> (even not documented as methods forms in this document)
 * non-abstract methods of the implemented type, which it would otherwise generate itself. To ensure
 * that user implementations of some methods don't change semantics of Koloboke Compile-generated
 * methods in some unintended way (because they call underridden methods), it is recommended to
 * manually check all usages of non-abstract methods of the implemented type in the Koloboke
 * Compile-generated implementation class. If it appears that behaviour of some methods indeed
 * become wrong because of cross-dependencies between methods in the implemented and implementation
 * classes, a possible workaround is to declare new abstract method(s) having the same parameter and
 * return types as the underridden method(s) which caused undesirable semantics change of other
 * methods, but with different names, and annotate them with {@link
 * MethodForm @MethodForm("nameOfTheUnderriddenMethod"}. This makes Koloboke Compile to "remap"
 * usages of the underridden methods in the generated implementation (usages, which statically
 * resolve to the underridden methods, i. e. where the type of the method call
 * receiver is the implemented type or the implementation type, but not a supertype of the
 * implemented type) to the newly declared abstract methods as well as generate implementations for
 * these methods.
 *
 * <p>No guarantee is provided on whether in the generated class such bulk methods as {@link
 * Map#putAll}, map view's {@link Collection#containsAll}, {@link Collection#removeAll}, {@link
 * Set#retainAll}, etc. are implemented via single-key query methods such as {@link
 * Map#put(Object, Object)}, {@link Map#containsKey(Object)}, {@link Map#remove(Object)} etc. or
 * not. More generally, is it not specified how and if any methods of the implemented class, it's
 * cursors, views, iterators and cursors of the views may call each other.
 *
 * <p><b>{@code equals()} and {@code hashCode()}</b><br>
 * The implementation class overrides {@link Object#equals(Object)}, {@link Object#hashCode()} if
 * the implemented type is a subtype of {@link Map} and the methods are abstract in the implemented
 * type. If any of them is non-abstract, but inherited directly from {@link Object} (i. e. inherited
 * the default implementation), it is still overridden.
 *
 * <p><b>{@code toString()}</b><br>
 * The implementation class overrides {@link Object#toString()} if it is abstract in the implemented
 * type. If it is non-abstract, but inherited directly from {@link Object}, it is still overridden.
 *
 * <p>Contracts of the methods in the generated implementation class might be affected by additional
 * annotations applied to the implemented type, see the <a href="#implementation-customizations">
 * Implementation Customizations</a> section below.
 *
 * <p>The implementation class is declared {@code final} (not extensible).
 *
 * <p>Koloboke Compile "doesn't know" about {@link Serializable} and {@link Cloneable} interfaces:
 * it doesn't check if {@code @KolobokeMap}-annotated classes or interfaces implement or extend
 * these interfaces, nor overrides {@link Object#clone()} or implements {@code readObject()} and
 * {@code writeObject()} methods, specified by the {@code Serializable} interface. The
 * implementation classes don't declare any fields {@code transient}. However,
 * {@code @KolobokeMap}-annotated types could intercept by providing implementations for
 * {@code clone()} or serialization methods itself.
 *
 * <p>The implementation class is not synchronized in any way. It doesn't internally use the
 * intrinsic object lock of the receiver instance nor any input objects. (So it doesn't declare any
 * methods {@code synchronized}.) Abstract {@code @KolobokeMap}-annotated types are free to use the
 * intrinsic lock (e. g. declare some non-abstract methods {@code synchronized}).
 *
 * <h4><a name="constructors">Constructors</a></h4>
 * <p>The implementation class has two constructors. One constructor accepts a parameter of
 * the {@code int} type, the <i>expected size of the map being constructed</i>. Another constructor
 * accepts two parameters: the first of the {@link HashConfig} type -- a hash configuration for the
 * constructed map, the second of the {@code int} type, the expected size of the map. Calling the
 * first constructor is equivalent to calling the second, with the {@linkplain
 * HashConfig#getDefault() default hash config} passed as the first argument.
 *
 * <p>If the implemented type is an abstract class and it's non-private constructor has some
 * parameters, parameters lists of both constructors of the implementation type are <i>preceded</i>
 * with parameters of the same types as the parameters of the implemented type's constructor,
 * and they are passed over to the implemented class constructor in a {@code super()} call. For
 * example, if a {@code @KolobokeMap}-annotated abstract class has a non-private constructor with
 * parameter types {@code int}, {@code String} and {@code Object}, parameter types of the
 * constructors of the generated class have the following types: <ol>
 *     <li>{@code int, String, Object, int (expectedSize)}</li>
 *     <li>{@code int, String, Object, HashConfig, int (expectedSize)}</li>
 * </ol>
 *
 * <p>If the non-private constructor of the implemented class has type parameters, equal type
 * parameters are present in both constructors in the implementation class as well.
 *
 * <h3><a name="implementation-customizations">Implementation Customizations</a></h3>
 * <h4><a name="mutability">Mutability</a></h4>
 * <p>If the {@code @KolobokeMap}-annotated type is annotated with one of the annotations from the
 * <a href="{@docRoot}/com/koloboke/compile/mutability/package-summary.html"><code>
 * com.koloboke.compile.mutability</code></a> package, Koloboke Compile generates an implementation
 * with the specified <i>mutability profile</i>. If none of the annotations from this package is
 * applied to a {@code @KolobokeMap}-annotated type, Koloboke Compile generates
 * an implementation with <i>the least mutability, which supports all the abstract methods in
 * an implemented type</i>. See <a
 * href="{@docRoot}/com/koloboke/compile/mutability/package-summary.html">the package documentation
 * </a> for more information.
 *
 * <h4><a name="key-nullability">Key nullability</a></h4>
 * <p>By default, if a {@code @KolobokeMap}-annotated type has a reference key type, Koloboke
 * Compile generates an implementation that disallows insertion and querying the {@code null} key,
 * i. e. calls like {@code map.put(null, value)} and {@code map.get(null)} on instances of such
 * implementation result in {@link NullPointerException}. Koloboke Compile generates an
 * implementation that allows the {@code null} key, only if the implemented type is annotated with
 * {@link NullKeyAllowed @NullKeyAllowed}.
 *
 * <h4><a name="custom-key-equivalence">Custom key equivalence</a></h4>
 * <p>By default Koloboke Compile generates a Map implementation (for a model with a <i>reference
 * </i> key type) which relies on the Java built-in object equality ({@link Object#equals(Object)}
 * and {@link Object#hashCode()}) for comparing keys, just as {@link Map} interface specifies. But
 * if the implemented type has two non-abstract methods which match {@code
 * boolean keyEquals(KeyType, KeyType)} {@code int keyHashCode(KeyType)} forms, the generated
 * implementation class relies on these methods for determining hash codes and comparing the queried
 * keys. This may be used for "redefining" inefficiently implemented {@code equals()} and {@code
 * hashCode()} for some key type, or <a
 * href="{@docRoot}/com/koloboke/compile/CustomKeyEquivalence.html#configurable-key-equivalence-map"
 * >making key equivalence configurable</a>, or using a non-standard equivalence relationship in
 * the keys domain, e. g. defining a {@link IdentityHashMap}-like type with primitive {@code int}
 * values. See the {@link CustomKeyEquivalence @CustomKeyEquivalence} specification for more
 * information.
 *
 * <h4>Choosing a hash table algorithm</h4>
 * <p>See the documentation for the
 * <a href="{@docRoot}/com/koloboke/compile/hash/algo/openaddressing/package-summary.html">
 * <code>com.koloboke.compile.hash.algo.openaddressing</code></a> package.
 *
 * <h4>Checks for concurrent modifications</h4>
 * <p>By default Koloboke Compile generates a Map implementation that returns so-called <i>fail-fast
 * </i> iterators (of the collection views) and cursors, and tries to identify concurrent structural
 * modifications during bulk operations like {@code forEach()}, {@code removeIf()}, {@code
 * putAll()}, and during bulk operations on the collections views. This is consistent with {@link
 * HashMap}'s behaviour. If instances of the Koloboke Compile-generated implementation of the
 * {@code @KolobokeMap}-annotated type are going to be accessed only via single-key queries (e. g.
 * only via {@code get()} and {@code put()}), disabling concurrent modification checks by applying
 * {@link ConcurrentModificationUnchecked @ConcurrentModificationUnchecked} might improve the
 * implementation performance a bit.
 *
 * <h4><a name="default-value">Custom <i>default value</i> for numeric primitive value type</a></h4>
 * <p>The {@link Map} interface returns {@code null} from {@code get()} method if no value mapped
 * for the key is found, from {@code put()}, {@code putIfAbsent()}, {@code replace()} and {@code
 * remove()}, if the mapping for the key didn't exist before the operation, and passes {@code null}
 * as the argument for the lambda in {@code compute()}, if the mapping for the key didn't exist
 * before the compute operation. In primitive specializations of these methods using {@code null}
 * for such "no-value" purposes is not possible, because {@code null} is not a valid primitive
 * value. In the Koloboke Collections API, the concept of <i>default value</i> is introduced to
 * address this problem. In the Koloboke Collections API, the default value of the map is
 * <ul>
 *     <li>Constant through the map instance lifetime</li>
 *     <li>Returned from {@code defaultValue()} method, e. g. {@link ObjIntMap#defaultValue()}</li>
 *     <li>Configured per-factory via {@code withDefaultValue()} method, e. g. {@link
 *     ObjIntMapFactory#withDefaultValue(int)}</li>
 * </ul>
 * The default value is also used as the initial value in the {@code
 * ValueType addValue(KeyType key, ValueType addition)} method, the version of {@code addValue()}
 * without {@code initialValue} provided.
 *
 * <p>By default in the Koloboke Collections implementation library and Koloboke Compile-generated
 * implementations the default value is zero for all numeric primitive value types, i. e. {@code 0}
 * for {@code int}, {@code 0.0f} for {@code float}, etc.
 *
 * <p>If the {@code @KolobokeMap}-annotated type has a non-abstract, non-private method of the
 * {@code ValueType defaultValue()} signature, the Koloboke Compile-generated implementation calls
 * this method whenever it needs to return "no value" or pass "no value" to the lambda argument of
 * the {@code compute()} method. Example: <pre><code>
 * &#064;KolobokeMap
 * abstract class MinusOneDefaultValueObjIntMap&lt;K&gt; implements ObjIntMap&lt;K&gt; {
 *
 *     static &lt;K&gt; ObjIntMap&lt;K&gt; withExpectedSize(int expectedSize) {
 *         return new KolobokeMinusOneDefaultValueObjIntMap&lt;K&gt;(expectedSize);
 *     }
 *
 *     &#064;Override
 *     public final int defaultValue() {
 *         return -1;
 *     }
 * }</code></pre>
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface KolobokeMap {
}
