/* with char|byte|short|int|long elem */
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

package net.openhft.collect.hash;


/**
 * Common configuration for factories of hash containers with {@code char} keys.
 *
 * <p>Currently {@code CharHashFactory} allows to specify consecutive range of keys which could be
 * inserted into hash container - <em>keys domain</em>. This is a performance hint:
 * hash containers might, but aren't required to throw {@link IllegalArgumentException}
 * on inserting a key out of the keys domain.
 *
 * <p>By default, all keys are allowed (keys domain is a whole range of {@code char}s, from
 * {@link Character#MIN_VALUE} to {@link Character#MAX_VALUE}.
 *
 * <p>For example, map keys or elements of the set could be unique IDs, counting from 1, thus it's
 * guaranteed that these keys are positive. Or one specific key has a special meaning in the logic
 * of your application. When you are sure that some keys range could never be put into
 * hash container, it is recommended to configure corresponding factory, which extends this
 * interface, with complement of that range as keys domain (or, alternatively, with that range
 * as keys domain complement).
 *
 * <p>It's OK to specify keys domain which include some actually impossible keys, but you shouldn't
 * leave a single valid key out of the domain. If the set of possible (impossible) keys consist of
 * several ranges, and/or standalone keys, it is still recommended to specify the domain to "forbid"
 * some impossible keys. For example, if possible keys are // if char elem //printable characters,
 * you should exclude first eight non-printable characters prior to {@code \t} (tab character):
 * <pre>{@code
 * factory = factory.withKeysDomain('\t', Character.MAX_VALUE);}</pre>
 * // elif !(char elem) //odd numbers, you should exclude one even number, zero:
 * <pre>{@code
 * factory = factory.withKeysDomainComplement(// const elem 0 //0, // const elem 0 //0);}</pre>
 * // endif //
 *
 * @param <F> the concrete factory type which extends this interface
 */
public interface CharHashFactory<F extends CharHashFactory<F>> extends HashContainerFactory<F> {

    /**
     * Returns lower (inclusive) bound of keys domain.
     *
     * <p>Default: {@link Character#MIN_VALUE}.
     *
     * @return lower (inclusive) bound of keys domain
     */
    char getLowerKeyDomainBound();

    /**
     * Returns upper (inclusive) bound of keys domain.
     *
     * <p>Default: {@link Character#MAX_VALUE}.
     *
     * @return upper (inclusive) bound of keys domain
     */
    char getUpperKeyDomainBound();

    /**
     * Returns a copy of this factory with keys domain set to the specified range.
     *
     * <p>This is a performance hint: hash containers might, but aren't required to throw
     * {@link IllegalArgumentException} on putting key out of the keys domain.
     *
     * <p>Example:
     * <pre> {@code
     * // only positive keys
     * factory = factory.withKeysDomain(// const elem 1 //1, Character.MAX_VALUE);}</pre>
     *
     * @param minPossibleKey lower (inclusive) bound of the target keys domain
     * @param maxPossibleKey upper (inclusive) bound of the target keys domain
     * @return a copy of this factory with keys domain set to the specified range
     * @throws IllegalArgumentException if {@code minPossibleKey} is greater
     *         than {@code maxPossibleKey}
     */
    F withKeysDomain(char minPossibleKey, char maxPossibleKey);

    /* define hole *//* if !(char elem) *//* const elem 0 //0/* elif char elem //'\u001a'// endif *//* enddefine */

    /**
     * Returns a copy of this factory with keys domain set to the complement of the specified range.
     *
     * <p>This is a performance hint: hash containers might, but aren't required to throw
     * {@link IllegalArgumentException} on putting key out of the keys domain.
     *
     * <p>This method is needed to specify keys domain that include both {@link Character#MIN_VALUE}
     * and {@link Character#MAX_VALUE}, but with a "hole" somewhere in between. Providing a single
     * {@link #withKeysDomain(char, char)} method for this and "ordinary" keys domain application
     * is error-prone, because there is no way to distinguish intention (domain with a "hole")
     * and mistakenly reordered arguments while attempting to specify "ordinary" domain.
     *
     * <p>Example:
     * <pre> {@code
     * // any keys except // if !(char elem) //0// elif char elem //EOF// endif //
     * factory = factory.withKeysDomainComplement(//hole//, //hole//);}</pre>
     *
     * @param minImpossibleKey upper (exclusive) bound of the target keys domain
     * @param maxImpossibleKey lower (exclusive) bound of the target keys domain
     * @return a copy of this factory with keys domain set to the complement of the specified range
     * @throws IllegalArgumentException if {@code minImpossibleKey} is greater
     *         than {@code maxImpossibleKey}
     */
    F withKeysDomainComplement(char minImpossibleKey, char maxImpossibleKey);
}
