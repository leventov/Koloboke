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

import net.openhft.function.Predicate;
import org.jetbrains.annotations.Nullable;


/**
 * Immutable hash config.
 */
public final class HashConfig {

    /**
     * Config with {@literal 0.5f} load factor, {@code null} shrink condition,
     * default expected size is 10.
     */
    public static final HashConfig DEFAULT = new HashConfig(0.5f, null, 10);


    private final float loadFactor;
    @Nullable
    private final Predicate<HashContainer> shrinkCondition;
    private final int defaultExpectedSize;

    private HashConfig(float loadFactor, @Nullable Predicate<HashContainer> shrinkCondition,
            int defaultExpectedSize) {
        this.loadFactor = loadFactor;
        this.shrinkCondition = shrinkCondition;
        this.defaultExpectedSize = defaultExpectedSize;
    }

    /**
     * @return load factor, a value in (0.0, 1.0) range
     * @see #withLoadFactor(float)
     * @see HashContainer#loadFactor()
     */
    public float getLoadFactor() {
        return loadFactor;
    }

    /**
     * Load factor should be in (0.0, 1.0) range.
     * @see #getLoadFactor()
     */
    public HashConfig withLoadFactor(float loadFactor) {
        if (this.loadFactor == loadFactor)
            return this;
        if (Float.isNaN(loadFactor) || loadFactor <= 0.0f || loadFactor >= 1.0f)
            throw new IllegalArgumentException("Load factor must be in (0.0, 1.0) range, " +
                    loadFactor + " given.");
        return new HashConfig(loadFactor, shrinkCondition, defaultExpectedSize);
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
     * @see HashContainer#shrink()
     */
    public @Nullable Predicate<HashContainer> getShrinkCondition() {
        return shrinkCondition;
    }

    /**
     * Returns hash config with the specified shrink condition.
     *
     * <p>Example:
     * <pre> {@code
     * conf.withShrinkCondition(h -> h.currentLoad() + 0.1f < h.loadFactor());
     * }</pre>
     *
     * @param condition shrink condition
     * @return hash config with the specified shrink condition
     * @see #getShrinkCondition()
     */
    public HashConfig withShrinkCondition(@Nullable Predicate<HashContainer> condition) {
        if (NullableObjects.equals(this.shrinkCondition, condition))
            return this;
        return new HashConfig(loadFactor, condition, defaultExpectedSize);
    }

    /**
     * This size is used in no-arg hash container factory methods.
     *
     * @return default expected size
     */
    public int getDefaultExpectedSize() {
        return defaultExpectedSize;
    }

    public HashConfig withDefaultExpectedSize(int defaultExpectedSize) {
        if (this.defaultExpectedSize == defaultExpectedSize)
            return this;
        if (defaultExpectedSize < 0)
            throw new IllegalArgumentException("Default expected hash size must be positive, " +
                    defaultExpectedSize + " given");
        return new HashConfig(loadFactor, shrinkCondition, defaultExpectedSize);
    }


    @Override
    public int hashCode() {
        int hashCode = 17;
        hashCode = hashCode * 31 + Float.floatToIntBits(loadFactor);
        hashCode = hashCode * 31 + NullableObjects.hashCode(shrinkCondition);
        return hashCode * 31 + defaultExpectedSize;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null)
            return false;
        if (obj instanceof HashConfig) {
            HashConfig conf = (HashConfig) obj;
            return loadFactor == conf.loadFactor &&
                    defaultExpectedSize == conf.defaultExpectedSize &&
                    NullableObjects.equals(shrinkCondition, conf.shrinkCondition);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "HashConfig[loadFactor[loadFactor=" + loadFactor +
                ",shrinkCondition=" + shrinkCondition +
                ",defaultExpectedSize=" + defaultExpectedSize + "]";
    }
}
