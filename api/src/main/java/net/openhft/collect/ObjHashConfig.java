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


import com.google.auto.value.AutoValue;


/**
 * Immutable config for hash containers with Object keys.
 *
 * @see #getDefault()
 */
@AutoValue
public abstract class ObjHashConfig {

    private static final ObjHashConfig DEFAULT = create(HashConfig.getDefault(), true);

    public static ObjHashConfig getDefault() {
        return DEFAULT;
    }

    private static ObjHashConfig create(HashConfig hashConfig, boolean nullKeyAllowed) {
        return new AutoValue_ObjHashConfig(hashConfig, nullKeyAllowed);
    }

    /**
     * Package-private constructor to prevent subclassing from outside of the package
     */
    ObjHashConfig() {}

    public abstract HashConfig getHashConfig();

    public ObjHashConfig withHashConfig(HashConfig config) {
        return create(config, isNullKeyAllowed());
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
    public abstract boolean isNullKeyAllowed();

    /**
     * @see #isNullKeyAllowed()
     */
    public ObjHashConfig withNullKeyAllowed(boolean nullKeyAllowed) {
        return create(getHashConfig(), nullKeyAllowed);
    }
}
