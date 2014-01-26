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

package net.openhft.collect;


/**
 * Immutable config for hash containers with primitive char keys.
 */
public final class CharHashConfig {

    public static final CharHashConfig DEFAULT =
            new CharHashConfig(HashConfig.DEFAULT, Character.MIN_VALUE, Character.MAX_VALUE);


    private final HashConfig hashConfig;
    private final char lowerKeyDomainBound;
    private final char upperKeyDomainBound;


    private CharHashConfig(HashConfig hashConfig,
            char lowerKeyDomainBound, char upperKeyDomainBound) {
        this.hashConfig = hashConfig;
        this.lowerKeyDomainBound = lowerKeyDomainBound;
        this.upperKeyDomainBound = upperKeyDomainBound;
    }


    public HashConfig getHashConfig() {
        return hashConfig;
    }

    public CharHashConfig withHashConfig(HashConfig config) {
        if (hashConfig.equals(config))
            return this;
        return new CharHashConfig(config, lowerKeyDomainBound, upperKeyDomainBound);
    }

    /**
     * Returns lower (inclusive) bound of keys domain.
     *
     * <p>Default: {@link Character#MIN_VALUE}.
     *
     * @return lower (inclusive) bound of keys domain
     */
    public char getLowerKeyDomainBound() {
        return lowerKeyDomainBound;
    }

    /**
     * Returns upper (inclusive) bound of keys domain.
     *
     * <p>Default: {@link Character#MAX_VALUE}.
     *
     * @return upper (inclusive) bound of keys domain
     */
    public char getUpperKeyDomainBound() {
        return upperKeyDomainBound;
    }

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
     * @throws java.lang.IllegalArgumentException if minPossibleKey is greater than maxPossibleKey
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
        if (this.lowerKeyDomainBound == lowerKeyDomainBound &&
                this.upperKeyDomainBound == upperKeyDomainBound)
            return this;
        return new CharHashConfig(hashConfig, lowerKeyDomainBound, upperKeyDomainBound);
    }


    @Override
    public int hashCode() {
        int hashCode = 17;
        hashCode = hashCode * 31 + hashConfig.hashCode();
        char l = lowerKeyDomainBound;
        hashCode = hashCode * 31 +
                /* if !(long elem) */l/* elif long elem //((int) ((l >>> 32) ^ l))// endif */;
        char u = upperKeyDomainBound;
        hashCode = hashCode * 31 +
                /* if !(long elem) */u/* elif long elem //((int) ((u >>> 32) ^ u))// endif */;
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null)
            return false;
        if (obj instanceof CharHashConfig) {
            CharHashConfig conf = (CharHashConfig) obj;
            return lowerKeyDomainBound == conf.lowerKeyDomainBound &&
                    upperKeyDomainBound == conf.upperKeyDomainBound &&
                    hashConfig.equals(conf.hashConfig);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "CharHashConfig[hashConfig=" + hashConfig +
                ",lowerKeyDomainBound=" + boundAsString(lowerKeyDomainBound) +
                ",upperKeyDomainBound=" + boundAsString(upperKeyDomainBound) + "]";
    }

    private String boundAsString(char bound) {
        /* if char elem */
        return String.format("%04x", (int) bound);
        /* elif !(char elem) //
        return "" + bound;
        // endif */
    }
}
