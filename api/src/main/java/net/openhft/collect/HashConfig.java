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
import net.openhft.function.Predicate;
import javax.annotation.Nullable;


/**
 * Immutable hash config.
 */
@AutoValue
public abstract class HashConfig {

    /**
     * Config with {@literal 0.5f} load factor, {@code null} shrink condition,
     * default expected size is 10.
     */
    public static final HashConfig DEFAULT = create(0.5f, null, 10);


    private static HashConfig create(float loadFactor,
            @Nullable Predicate<HashContainer> shrinkCondition, int defaultExpectedSize) {
        if (Float.isNaN(loadFactor) || loadFactor <= 0.0f || loadFactor >= 1.0f)
            throw new IllegalArgumentException("Load factor must be in (0.0, 1.0) range, " +
                    loadFactor + " given.");
        if (defaultExpectedSize < 0)
            throw new IllegalArgumentException("Default expected hash size must be non-negative, " +
                    defaultExpectedSize + " given");
        return new AutoValue_HashConfig(loadFactor, shrinkCondition, defaultExpectedSize);
    }

    /**
     * Package-private constructor to prevent subclassing from outside of the package
     */
    HashConfig() {}

    /**
     * @return load factor, a value in (0.0, 1.0) range
     * @see #withLoadFactor(float)
     */
    public abstract float getLoadFactor();

    /**
     * Load factor should be in (0.0, 1.0) range.
     * @see #getLoadFactor()
     */
    public final HashConfig withLoadFactor(float loadFactor) {
        if (getLoadFactor() == loadFactor)
            return this;
        return create(loadFactor, getShrinkCondition(), getDefaultExpectedSize());
    }

    /**
     * Immediately after hash set or map construction from non-distinct sources (e. g. arrays)
     * it's load could be significantly less than the specified load factor due to expansion.
     * This predicate is used to shrink too sparse hash containers automatically.
     *
     * {@code null} condition is considered as constant {@code false} predicate: never shrink.
     *
     * <p>Particularly useful for construction immutable containers, because they couldn't be
     * shrunk manually after creation.
     *
     * @return shrink condition
     * @see #withShrinkCondition(net.openhft.function.Predicate)
     * @see net.openhft.collect.HashContainer#shrink()
     */
    @Nullable
    public abstract Predicate<HashContainer> getShrinkCondition();

    /**
     * Returns hash config with the specified shrink condition.
     *
     * <p>Example:
     * <pre> {@code
     * conf.withShrinkCondition(h -> h.currentLoad() + 0.1f < h.hashConfig().getLoadFactor());
     * }</pre>
     *
     * @param condition shrink condition
     * @return hash config with the specified shrink condition
     * @see #getShrinkCondition()
     */
    public final HashConfig withShrinkCondition(@Nullable Predicate<HashContainer> condition) {
        if (NullableObjects.equals(getShrinkCondition(), condition))
            return this;
        return create(getLoadFactor(), condition, getDefaultExpectedSize());
    }

    /**
     * This size is used in no-arg hash container factory methods.
     *
     * @return default expected size
     */
    public abstract int getDefaultExpectedSize();

    public final HashConfig withDefaultExpectedSize(int defaultExpectedSize) {
        if (getDefaultExpectedSize() == defaultExpectedSize)
            return this;
        return create(getLoadFactor(), getShrinkCondition(), defaultExpectedSize);
    }
}
