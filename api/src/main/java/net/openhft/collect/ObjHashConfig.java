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
 * Immutable config for hash containers with Object keys.
 *
 * @see #getDefault()
 */
public final class ObjHashConfig {

    private static final ObjHashConfig DEFAULT = new ObjHashConfig(HashConfig.getDefault(), true);

    public static ObjHashConfig getDefault() {
        return DEFAULT;
    }

    private final HashConfig hashConfig;
    private final boolean nullKeyAllowed;

    private ObjHashConfig(HashConfig hashConfig, boolean nullKeyAllowed) {
        this.hashConfig = hashConfig;
        this.nullKeyAllowed = nullKeyAllowed;
    }

    public HashConfig getHashConfig() {
        return hashConfig;
    }

    public ObjHashConfig withHashConfig(HashConfig config) {
        if (hashConfig.equals(config))
            return this;
        return new ObjHashConfig(config, nullKeyAllowed);
    }

    /**
     * This is a performance hint, hash containers aren't required to throw
     * {@code NullPointerException} on putting {@code null} as a key, although they could,
     * if {@code null} key is disallowed.
     *
     * <p>Default: {@code true}.
     *
     * @return {@code true} if null key is allowed, {@code false} otherwise
     * @see #withNullKeyAllowed(boolean)
     */
    public boolean isNullKeyAllowed() {
        return nullKeyAllowed;
    }

    /**
     * @see #isNullKeyAllowed()
     */
    public ObjHashConfig withNullKeyAllowed(boolean nullKeyAllowed) {
        if (this.nullKeyAllowed == nullKeyAllowed)
            return this;
        return new ObjHashConfig(hashConfig, nullKeyAllowed);
    }

    @Override
    public int hashCode() {
        int hashCode = 17;
        hashCode = hashCode * 31 + hashConfig.hashCode();
        return hashCode * 31 + (nullKeyAllowed ? 1 : 0);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null)
            return false;
        if (obj instanceof ObjHashConfig) {
            ObjHashConfig conf = (ObjHashConfig) obj;
            return nullKeyAllowed == conf.nullKeyAllowed && hashConfig.equals(conf.hashConfig);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "ObjHashConfig[hashConfig=" + hashConfig + ",nullKeyAllowed=" + nullKeyAllowed + "]";
    }
}
