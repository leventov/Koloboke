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
import com.koloboke.collect.set.hash.HashIntSet;
import com.koloboke.collect.set.hash.HashObjSet;
import com.koloboke.function.Consumer;

import java.io.Serializable;
import java.lang.annotation.*;
import java.util.*;


/**
 * Specifies that Koloboke Compile should generate an implementation of the annotated {@linkplain
 * Set}-like class or interface.
 *
 * <h3>Requirements and permissions for {@code @KolobokeSet}-annotated types</h3>
 * <h4><i>{@code Set} model defining</i> method form</h4>
 * <p>The annotated type must have an abstract method (either declared in the body of the
 * type or inherited) <i>matching</i> (see below) the <b>{@code boolean add(KeyType key)}</b> form,
 * where {@code KeyType} is either some <i>reference type</i> (a declared type, or an array type, or
 * a type variable) or a <i>numeric primitive type</i> ({@code byte}, {@code char}, {@code short},
 * {@code int}, {@code long}, {@code float} or {@code double}).
 *
 * <h4><a name="set-method-form-matching">Method form matching</a></h4>
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
 * <h4><a name="set-type-consistency">{@code KeyType} consistency</a></h4>
 * <p>{@code KeyType} should be the same in all methods (either abstract or not) in the
 * {@code @KolobokeSet}-annotated type, which match the above form and the forms defined below. For
 * example, the annotated type couldn't simultaneously have methods {@code boolean add(K)} and
 * {@code boolean contains(String)}, because according to the first method the {@code KeyType} is
 * {@code K} (a type variable), and according to the second method the {@code KeyType} is
 * {@code String}.
 *
 * <h4>Key type({@code Set} model definition)</h4>
 * <p>Hereafter in this specification and specifications of other Koloboke Compile annotations, for
 * some {@code @KolobokeSet}-annotated type, the {@code KeyType} appearing in the abstract method of
 * this type, which match the above form, is called the <i>key type</i> of the annotated type.
 *
 * <h4>"Boxed" and "primitive" versions of the {@code add()} method form for numeric primitive key
 * type</h4>
 * <p>If the key type is a numeric primitive type, a method (either abstract or not) could match
 * additional version of the {@code add()} form: a version with primitive wrapper classes as the
 * {@code key} parameter. For example, if the key type of the annotated type is {@code int}, it
 * could have method {@code boolean add(int)}, or {@code boolean add(Integer)}, or both
 * simultaneously.
 *
 * <p>A numeric primitive key type could even never appear as itself in the signatures and
 * return types of the methods of the annotated type (but only as a wrapper class in methods
 * matching the "boxed" versions of some forms): for example, a {@code @KolobokeSet}-annotated
 * interface extending {@code Set<Long>} and not defining own methods (hence inheriting
 * abstract methods like {@code boolean put(Long)}, which match the "boxed" version of the
 * {@code add()} form) still have primitive {@code long} key type. This is important, because in the
 * generated implementations for such types keys are stored as primitives, hence insertion of the
 * {@code null} key into such set is not possible and always leads to {@code NullPointerException}.
 * The only way to make Koloboke Compile to generate a set implementation that actually stores
 * primitive wrapper objects (though this is a highly questionable goal) is to annotate with
 * {@code @KolobokeSet} a generic abstract class or an interface with key type of a type variable,
 * and parameterize it with primitive wrapper class (e. g. {@code Integer}) at the moment of
 * instantiation.
 *
 * <p>Currently Koloboke Compile is able to generate only hash table-based implementations of
 * annotated Set-like classes or interfaces. In the future, ability to generate other kinds of
 * implementations (e. g. tree-based) might be added.
 *
 * <h4>Other method forms</h4>
 * <p>In addition to the {@code add()} method form, a {@code KolobokeSet}-annotated class or
 * interface could have methods (either abstract or not) matching the following forms:
 * <ul>
 *     <li><b>Forms, prototyped by methods in interfaces from the Koloboke Collections API</b><br>
 *     If the key type is a reference type, the prototyping interface is {@link HashObjSet
 *     HashObjSet&lt;KeyType&gt;}. If the key type is a numeric primitive type, the prototyping
 *     interface is {@code HashXxxSet}, where is a capitalized name of the key type, e. g. {@link
 *     HashIntSet} if the key type is {@code int}. Method forms have signatures and return types of
 *     the methods in the prototyping interface.
 *
 *     <p>The Koloboke Collections API comes in two different distributions: for Java 6 or 7, and
 *     for Java 8+, and signatures of some methods are different in the same interfaces, provided by
 *     different distributions. For example, the method {@link HashObjSet#forEach(Consumer)} has the
 *     parameter type of {@link java.util.function.Consumer java.util.function.Consumer} in the
 *     distribution for Java 8+, and <a
 *     href="http://leventov.github.io/Koloboke/api/1.0/java6/com/koloboke/function/Consumer.html">
 *     {@code com.koloboke.function.Consumer}</a> in the distribution for Java 6 and 7. Possible
 *     method forms have signatures of the methods in that version of the prototyping interface,
 *     <i>which is present in the compilation classpath at the moment of Koloboke Compile generation
 *     </i>.
 *
 *     <p>Forms, defined by methods, which are not abstract in the prototyping interface, couldn't
 *     be matched by abstract methods in the {@code KolobokeSet}-annotated class or interface, but
 *     if there are some non-abstract matching methods, they should be consistent with the key and
 *     value types of the annotated type, <a href="#set-type-consistency">as specified above</a>.
 *     This means, for example, that if the project, containing the {@code @KolobokeSet}-annotated
 *     type, depends on JDK 8 and the Koloboke Collections API distribution for Java 6 and 7
 *     (although Koloboke Compile emits a warning in this case, suggesting to change the dependency
 *     to the Koloboke Collections API distribution for Java 8+), method {@link
 *     Set#forEach(java.util.function.Consumer)} will have a default implementation (i. e. it will
 *     be <i>non-abstract</i>) in the {@link HashObjSet} interface, so the
 *     {@code @KolobokeSet}-annotated type couldn't declare or inherit such a method as abstract.
 *
 *     <p>The Koloboke Collections API distribution for Java 8+ doesn't currently declare
 *     {@code stream()} and {@code spliterator()} methods as abstract, so they couldn't be abstract
 *     in {@code @KolobokeSet}-annotated types as well.
 *     </li>
 *
 *     <li><b>Specializations of methods with "raw" parameters in the {@code Set} interface</b><br>
 *     Methods {@code contains()} and {@code remove()} in the {@code Set} interface, and
 *     consequently in subinterfaces (including primitive specializations) from the Koloboke
 *     Collections API, have the key parameter of {@code Object} type, that is a double-edged design
 *     decision. Along with the forms, prototyped by those methods, similar
 *     forms with <i>generified</i> parameter type are also allowed in
 *     {@code @KolobokeSet}-annotated types:
 *     <table BORDER CELLPADDING=3 CELLSPACING=1
 *     summary="Specializations of methods with raw parameters in the Set interface">
 *         <tr><th>Return type</th><th>Signature</th></tr>
 *         <tr><td ALIGN=RIGHT>{@code boolean}</td><td>{@code contains(KeyType key)}</td></tr>
 *         <tr><td ALIGN=RIGHT>{@code boolean}</td><td>{@code remove(KeyType key)}</td></tr>
 *     </table>
 *
 *     <p>Koloboke Compile not only permits generified versions of the
 *     "{@code Object}-parameterized" methods, but <i>effectively opts out the feature of making
 *     queries with arguments of unrelated types for Sets with concrete (not a type variable) key
 *     type</i>. For example, if a {@code @KolobokeSet}-annotated class has {@code ArrayList}
 *     key type, it may have a method {@code contains(Object)}, but an attempt to call this method
 *     with {@code LinkedList} argument will result in {@code ClassCastException}, with a message
 *     like "LinkedList couldn't be cast to ArrayList". However, if the key type of a
 *     {@code @KolobokeSet}-annotated class or interface is a type variable, and it is {@code
 *     ArrayList} at the instantiation point, {@code set.contains(linkedList)} will proceed without
 *     exception and may return {@code true}, if there is an equivalent {@code ArrayList} key in
 *     this set.
 *     </li>
 * </ul>
 *
 * <h4>Miscellaneous requirements and permissions</h4>
 * <p>The {@code @KolobokeSet}-annotated type could have an abstract method of the form of the
 * {@link Object#equals(Object)} method, only if it is a subtype of {@link Set}.
 *
 * <p>The {@code @KolobokeSet}-annotated type could have any static methods and non-abstract
 * methods with any signatures and return types, as long as they don't match any of the forms
 * defined above (but if they <i>do</i> match some forms, they should have
 * <a href="#set-type-consistency">consistent</a> signatures and return types).
 *
 * <p>The annotated type could be a nested type, but in this case it should be static and
 * non-private. The annotated type couldn't be an inner or an anonymous class.
 *
 * <p>If the annotated type is an abstract class, it must have exactly one non-private constructor
 * (the implicit no-argument constructor counts).
 *
 * <p>The annotated type couldn't have a {@link KolobokeMap @KolobokeMap} annotation (any type could
 * either have a {@code @KolobokeSet} or a {@code @KolobokeMap} annotation, but not both).
 *
 * <h3>Generated implementation</h3>
 * If the annotated type meets all requirements specified above, Koloboke Compile generates a class
 * that implements the annotated type (a subclass, if the annotated type is an abstract class). If
 * some requirements are not met, Koloboke Compile emits a compile-time error and an implementation
 * class is not generated.
 *
 * <h4>Name</h4>
 * The simple name of the implementation class is the simple name of the implemented type with
 * "Koloboke" prefix. For example, if the name of a {@code @KolobokeSet}-annotated type is {@code
 * MySet}, the name of the implementation class is {@code KolobokeMySet}. If the implemented type is
 * a nested type, instead of the simple name of the implemented type, it's canonical name (excluding
 * package) is used with dots replaced with underscores: {@code _}. For example, if a
 * {@code @KolobokeSet}-annotated type named {@code InnerSet} is a nested type in a type named
 * {@code Outer}, the name of the Koloboke Compile-generated implementation class is {@code
 * KolobokeOuter_InnerSet}.
 *
 * <h4>Package</h4>
 * <p>The implementation class is located in the same package, as the implemented type, and has
 * package-private visibility in this package.
 *
 * <h4>Type parameters</h4>
 * <p>If the {@code @KolobokeSet}-annotated type has type parameters, the implementation class has
 * the same number of type parameters with equivalent types in the same order ("equivalence of types
 * of type parameters" essentially means equivalence of their bounds).
 *
 * <h4>Semantics and behaviour</h4>
 * <p>The implementation class is non-abstract. It overrides all abstract methods in the implemented
 * type, and doesn't override non-abstract methods. Methods in the implementation class, overriding
 * some methods in the implemented type, have contracts of the methods in interfaces from the
 * Koloboke Collections API, prototyping the forms, matched by the overridden methods in the
 * implemented type.
 *
 * <h4>Method underriding requires great care</h4>
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
 * MethodForm @MethodForm("nameOfTheUnderriddenMethod")}. This makes Koloboke Compile to replace
 * usages of the underridden methods in the generated implementation (usages, which statically
 * resolve to the underridden methods, i. e. where the type of the method call
 * receiver is the implemented type or the implementation type, but not a supertype of the
 * implemented type) with the newly declared abstract methods as well as generate implementations
 * for these methods.
 *
 * <p>No guarantee is provided on whether in the generated class such bulk methods as {@link
 * Set#containsAll}, {@link Set#addAll}, {@link Set#removeAll}, {@link Set#retainAll}, etc. are
 * implemented via single-key query methods such as {@link Set#contains(Object)}, {@link
 * Set#add(Object)}, {@link Set#remove(Object)} etc. or not. More generally, is it not specified how
 * and if any methods of the implemented class, it's iterators and cursors may call each other.
 *
 * <p><b>{@code equals()} and {@code hashCode()}</b><br>
 * The implementation class overrides {@link Object#equals(Object)}, {@link Object#hashCode()} if
 * the implemented type is a subtype of {@link Set} and the methods are abstract in the implemented
 * type. If any of them is non-abstract, but inherited directly from {@link Object} (i. e. inherited
 * the default implementation), it is still overridden.
 *
 * <p><b>{@code toString()}</b><br>
 * The implementation class overrides {@link Object#toString()} if it is abstract in the implemented
 * type. If it is non-abstract, but inherited directly from {@link Object}, it is still overridden.
 *
 * <p>Contracts of the methods in the generated implementation class might be affected by additional
 * annotations applied to the implemented type, see the
 * <a href="#set-implementation-customizations">Implementation Customizations</a> section below.
 *
 * <p>The implementation class is declared {@code final} (not extensible).
 *
 * <p>Koloboke Compile "doesn't know" about {@link Serializable} and {@link Cloneable} interfaces:
 * it doesn't check if {@code @KolobokeSet}-annotated classes or interfaces implement or extend
 * these interfaces, nor overrides {@link Object#clone()} or implements {@code readObject()} and
 * {@code writeObject()} methods, specified by the {@code Serializable} interface. The
 * implementation classes don't declare any fields {@code transient}. However,
 * {@code @KolobokeSet}-annotated types could intercept by providing implementations for
 * {@code clone()} or serialization methods itself.
 *
 * <p>The implementation class is not synchronized in any way. It doesn't internally use the
 * intrinsic object lock of the receiver instance nor any input objects. (So it doesn't declare any
 * methods {@code synchronized}.) Abstract {@code @KolobokeSet}-annotated types are free to use the
 * intrinsic lock (e. g. declare some non-abstract methods {@code synchronized}).
 *
 * <h4>Constructors</h4>
 * <p>The implementation class has two constructors. One constructor accepts a parameter of
 * the {@code int} type, the <i>expected size of the set being constructed</i>. Another constructor
 * accepts two parameters: the first of the {@link HashConfig} type -- a hash configuration for the
 * constructed set, the second of the {@code int} type, the expected size of the set. Calling the
 * first constructor is equivalent to calling the second, with the {@linkplain
 * HashConfig#getDefault() default hash config} passed as the first argument.
 *
 * <p>If the implemented type is an abstract class and it's non-private constructor has some
 * parameters, parameters lists of both constructors of the implementation type are <i>preceded</i>
 * with parameters of the same types as the parameters of the implemented type's constructor,
 * and they are passed over to the implemented class constructor in a {@code super()} call. For
 * example, if a {@code @KolobokeSet}-annotated abstract class has a non-private constructor with
 * parameter types {@code int}, {@code String} and {@code Object}, parameter types of the
 * constructors of the generated class have the following types: <ol>
 *     <li>{@code int, String, Object, int (expectedSize)}</li>
 *     <li>{@code int, String, Object, HashConfig, int (expectedSize)}</li>
 * </ol>
 *
 * <p>If the non-private constructor of the implemented class has type parameters, equal type
 * parameters are present in both constructors in the implementation class as well.
 *
 * <h3><a name="set-implementation-customizations">Implementation Customizations</a></h3>
 * <h4><a name="set-mutability">Mutability</a></h4>
 * <p>If the {@code @KolobokeSet}-annotated type is annotated with one of the annotations from the
 * <a href="{@docRoot}/com/koloboke/compile/mutability/package-summary.html"><code>
 * com.koloboke.compile.mutability</code></a> package, Koloboke Compile generates an implementation
 * with the specified <i>mutability profile</i>. If none of the annotations from this package is
 * applied to a {@code @KolobokeSet}-annotated type, Koloboke Compile generates
 * an implementation with <i>the least mutability, which supports all the abstract methods in
 * an implemented type</i>. See <a
 * href="{@docRoot}/com/koloboke/compile/mutability/package-summary.html">the package documentation
 * </a> for more information.
 *
 * <h4><a name="set-key-nullability">Key nullability</a></h4>
 * <p>By default, if a {@code @KolobokeSet}-annotated type has a reference key type, Koloboke
 * Compile generates an implementation that disallows insertion and querying the {@code null} key,
 * i. e. calls like {@code set.add(null)}, {@code set.remove(null)} and {@code set.contains(null)}
 * on instances of such implementation result in {@link NullPointerException}. Koloboke Compile
 * generates an implementation that allows the {@code null} key, only if the implemented type is
 * annotated with {@link NullKeyAllowed @NullKeyAllowed}.
 *
 * <h4><a name="set-custom-key-equivalence">Custom key equivalence</a></h4>
 * <p>By default Koloboke Compile generates a Set implementation (for a model with a <i>reference
 * </i> key type) which relies on the Java built-in object equality ({@link Object#equals(Object)}
 * and {@link Object#hashCode()}) for comparing keys, just as {@link Set} interface specifies. But
 * if the implemented type has two non-abstract methods which match {@code
 * boolean keyEquals(KeyType, KeyType)} {@code int keyHashCode(KeyType)} forms, the generated
 * implementation class relies on these methods for determining hash codes and comparing the queried
 * keys. This may be used for "redefining" inefficiently implemented {@code equals()} and {@code
 * hashCode()} for some key type, or <a
 * href="{@docRoot}/com/koloboke/compile/CustomKeyEquivalence.html#configurable-key-equivalence-map"
 * >making key equivalence configurable</a>, or using a non-standard equivalence relationship in
 * the keys domain, e. g. defining a {@link IdentityHashMap}-like set type. See the {@link
 * CustomKeyEquivalence @CustomKeyEquivalence} specification for more information.
 *
 * <h4>Choosing a hash table algorithm</h4>
 * <p>See the documentation for the
 * <a href="{@docRoot}/com/koloboke/compile/hash/algo/openaddressing/package-summary.html">
 * <code>com.koloboke.compile.hash.algo.openaddressing</code></a> package.
 *
 * <h4>Checks for concurrent modifications</h4>
 * <p>By default Koloboke Compile generates a Set implementation that returns so-called <i>fail-fast
 * </i> iterators and cursors, and tries to identify concurrent structural modifications during bulk
 * operations like {@code forEach()}, {@code removeIf()}, {@code containsAll()}, {@code addAll()},
 * etc. This is consistent with {@link HashSet}'s behaviour. If instances of the Koloboke
 * Compile-generated implementation of the {@code @KolobokeSet}-annotated type are going to be
 * accessed only via single-key queries (e. g. only via {@code add()} and {@code contains()}),
 * disabling concurrent modification checks by applying {@link
 * ConcurrentModificationUnchecked @ConcurrentModificationUnchecked} might improve the
 * implementation performance a bit.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface KolobokeSet {
}
