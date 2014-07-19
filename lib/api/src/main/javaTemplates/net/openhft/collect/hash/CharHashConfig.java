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

import com.google.auto.value.AutoValue;


/**
 * Immutable config for hash containers with primitive char keys.
 */
@AutoValue
public abstract class CharHashConfig {

    private static final CharHashConfig DEFAULT =
            create(HashConfig.getDefault(), Character.MIN_VALUE, Character.MAX_VALUE);

    public static CharHashConfig getDefault() {
        return DEFAULT;
    }

    private static CharHashConfig create(HashConfig hashConfig,
            char lowerKeyDomainBound, char upperKeyDomainBound) {
        return new AutoValue_CharHashConfig(hashConfig, lowerKeyDomainBound, upperKeyDomainBound);
    }

    /**
     * Package-private constructor to prevent subclassing from outside of the package
     */
    CharHashConfig() {}


    public abstract HashConfig getHashConfig();

    public CharHashConfig withHashConfig(HashConfig config) {
        if (getHashConfig().equals(config))
            return this;
        return create(config, getLowerKeyDomainBound(), getUpperKeyDomainBound());
    }

    /**
     * Returns lower (inclusive) bound of keys domain.
     *
     * <p>Default: {@link Character#MIN_VALUE}.
     *
     * @return lower (inclusive) bound of keys domain
     */
    public abstract char getLowerKeyDomainBound();

    /**
     * Returns upper (inclusive) bound of keys domain.
     *
     * <p>Default: {@link Character#MAX_VALUE}.
     *
     * @return upper (inclusive) bound of keys domain
     */
    public abstract char getUpperKeyDomainBound();

    /**
     * Returns char hash config with the specified keys domain.
     *
     * <p>This is a performance hint, hash containers could, but aren't required to throw
     * {@code IllegalArgumentException} on putting key out of the specified domain.
     *
     * <p>Example:
     * <pre> {@code
     * // only positive keys
     * conf = conf.withKeysDomain(1, Character.MAX_VALUE);
     * }</pre>
     *
     * @return char hash config with the specified keys domain
     * @throws IllegalArgumentException if minPossibleKey is greater than maxPossibleKey
     */
    public CharHashConfig withKeysDomain(char minPossibleKey, char maxPossibleKey) {
        if (minPossibleKey > maxPossibleKey)
            throw new IllegalArgumentException();
        return internalWithKeysDomain(minPossibleKey, maxPossibleKey);
    }

    /**
     * Returns char hash config with the specified keys domain complement.
     *
     * <p>Example:
     * <pre> {@code
     * // any keys except 0
     * conf = conf.withKeysDomainComplement(0, 0);
     * }</pre>
     * @return char hash config with the specified keys domain complement.
     * @see #withKeysDomain(char, char)
     */
    public CharHashConfig withKeysDomainComplement(char minImpossibleKey, char maxImpossibleKey) {
        if (minImpossibleKey > maxImpossibleKey)
            throw new IllegalArgumentException();
        return internalWithKeysDomain((char) (maxImpossibleKey + 1), (char) (minImpossibleKey - 1));
    }

    private CharHashConfig internalWithKeysDomain(
            char lowerKeyDomainBound, char upperKeyDomainBound) {
        if (getLowerKeyDomainBound() == lowerKeyDomainBound &&
                getUpperKeyDomainBound() == upperKeyDomainBound)
            return this;
        return create(getHashConfig(), lowerKeyDomainBound, upperKeyDomainBound);
    }

    @Override
    public String toString() {
        return "CharHashConfig[hashConfig=" + getHashConfig() +
                ",lowerKeyDomainBound=" + boundAsString(getLowerKeyDomainBound()) +
                ",upperKeyDomainBound=" + boundAsString(getUpperKeyDomainBound()) + "]";
    }

    /**
     * To distinguish non-printable characters in debug output
     */
    private String boundAsString(char bound) {
        /* if char elem */
        return String.format("%04x", (int) bound);
        /* elif !(char elem) //
        return "" + bound;
        // endif */
    }
}
