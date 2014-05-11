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

    private static final double DEFAULT_MIN_LOAD = 1.0 / 3.0;
    private static final double DEFAULT_MAX_LOAD = 2.0 / 3.0;
    private static final double DEFAULT_TARGET_LOAD = 0.5;
    private static final double DEFAULT_GROW_FACTOR = 2.0;
    private static final int DEFAULT_DEFAULT_EXPECTED_SIZE = 10;
    @Nullable
    private static final Predicate<HashContainer> DEFAULT_SHRINK_CONDITION = null;
    private static final HashConfig DEFAULT =
            create(DEFAULT_MIN_LOAD, DEFAULT_TARGET_LOAD, DEFAULT_MAX_LOAD, DEFAULT_GROW_FACTOR,
                    DEFAULT_SHRINK_CONDITION, DEFAULT_DEFAULT_EXPECTED_SIZE);

    /**
     * Returns a config with {@literal 0.(3)} min load, {@literal 0.5} target load,
     * {@literal 0.(6)} max load, {@literal 2.0} grow factor, {@code null} shrink condition
     * and {@literal 10} default expected size.
     */
    public static HashConfig getDefault() {
        return DEFAULT;
    }

    private static HashConfig create(
            double minLoad, double targetLoad, double maxLoad, double growFactor,
            @Nullable Predicate<HashContainer> shrinkCondition, int defaultExpectedSize) {
        if (Double.isNaN(targetLoad) || targetLoad <= 0.0 || targetLoad >= 1.0) {
            throw new IllegalArgumentException("Target load must be in (0.0, 1.0) range, " +
                    targetLoad + " given.");
        }
        if (Double.isNaN(minLoad) || minLoad < 0.0 || minLoad > targetLoad) {
            throw new IllegalArgumentException(String.format(
                    "Min load must be in [0.0, target load = %f]  range, %f given.",
                    targetLoad, minLoad));
        }
        if (Double.isNaN(maxLoad) || maxLoad > 1.0 || maxLoad < targetLoad) {
            throw new IllegalArgumentException(String.format(
                    "Min load must be in [%f (target load), 1.0]  range, %f given.",
                    targetLoad, maxLoad));
        }
        if (Double.isNaN(growFactor) || growFactor <= 1.0 || growFactor > maxLoad / minLoad) {
            throw new IllegalArgumentException(String.format(
                    "Grow factor must be in [1.0, max load / min load = %f]  range, %f given.",
                    maxLoad / minLoad, growFactor));
        }
        if (defaultExpectedSize < 0) {
            throw new IllegalArgumentException("Default expected hash size must be non-negative, " +
                    defaultExpectedSize + " given");
        }
        HashConfig config = new AutoValue_HashConfig(minLoad, targetLoad,
                maxLoad, growFactor, shrinkCondition, defaultExpectedSize);
        return config;
    }


    /**
     * Package-private constructor to prevent subclassing from outside of the package
     */
    HashConfig() {}

    /**
     * Denotes the minimum load a hash table will try to never be sparser than specified by.
     *
     * @return the minimum load, a value in [{@literal 0.0}, {@link #getTargetLoad()}]
     * @see #withMinLoad(double)
     */
    public abstract double getMinLoad();

    /**
     * Returns a config with the specified min load. Min load allows to limit memory usage
     * of hash tables.
     *
     * <p>Updatable linear hash tables can't have min load greater than {@literal 0.5}.
     *
     * @param minLoad a value in [{@literal 0.0}, {@link #getTargetLoad()}] range
     * @return hash config with the specified min load
     * @see #getMinLoad()
     */
    public final HashConfig withMinLoad(double minLoad) {
        return create(minLoad, getTargetLoad(), getMaxLoad(), getGrowFactor(),
                getShrinkCondition(), getDefaultExpectedSize());
    }

    /**
     * Denotes the default desirable hash table load. On hash table construction capacity is chosen
     * in order to table's load to be as close to the target load, as possible.
     * {@link HashContainer#shrink()} rehashes a table to the target load.
     *
     * @return the target load, a value in [{@link #getMinLoad()}, {@link #getMaxLoad()}] range
     * @see #withTargetLoad(double)
     */
    public abstract double getTargetLoad();

    /**
     * Returns a config with the specified target load. Target load allows to control basic
     * memory usage -- performance tradeoff for hash tables.
     *
     * @param targetLoad a value in [{@link #getMinLoad()}, {@link #getMaxLoad()}] range
     * @return hash config with the specified target load
     * @see #getTargetLoad()
     */
    public final HashConfig withTargetLoad(double targetLoad) {
        return create(getMinLoad(), targetLoad, getMaxLoad(), getGrowFactor(),
                getShrinkCondition(), getDefaultExpectedSize());
    }

    /**
     * Denotes a maximum load a hash table will try to never be denser than specified by.
     *
     * @return the maximum load, a value in [{@link #getTargetLoad()}, 1.0] range
     * @see #withMaxLoad(double)
     */
    public abstract double getMaxLoad();

    /**
     * Returns a config with the specified max load. Max load allows to limit minimum performance
     * of hash tables, because too dense hash tables operate slowly.
     *
     * @param maxLoad a value in [{@link #getTargetLoad()}, 1.0] range
     * @return hash config with the specified max load
     * @see #getMaxLoad()
     */
    public final HashConfig withMaxLoad(double maxLoad) {
        return create(getMinLoad(), getTargetLoad(), maxLoad, getGrowFactor(),
                getShrinkCondition(), getDefaultExpectedSize());
    }

    /**
     * Denotes how much a hash table's capacity is increased on periodical rehashes
     * on adding (putting) new elements (entries).
     *
     * @return a value in [1.0, {@link #getMaxLoad()} / {@link #getMinLoad()}] range
     * @see #withGrowFactor(double)
     */
    public abstract double getGrowFactor();

    /**
     * Returns a config with the specified grow factor. Grow factor allows to control memory
     * usage -- performance tradeoff for steadily growing hash tables.
     *
     * <p>Updatable linear hash tables can't have any grow factor other than {@literal 2.0}.
     *
     * @param growFactor a value in [1.0, {@link #getMaxLoad()} / {@link #getMinLoad()}] range
     * @return hash config with the specified grow factor
     * @see #getGrowFactor()
     */
    public final HashConfig withGrowFactor(double growFactor) {
        return create(getMinLoad(), getTargetLoad(), getMaxLoad(), growFactor,
                getShrinkCondition(), getDefaultExpectedSize());
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
     * conf.withShrinkCondition(h -> h.currentLoad() + 0.1f < h.hashConfig().getTargetLoad());
     * }</pre>
     *
     * @param condition shrink condition
     * @return hash config with the specified shrink condition
     * @see #getShrinkCondition()
     */
    public final HashConfig withShrinkCondition(@Nullable Predicate<HashContainer> condition) {
        return create(getMinLoad(), getTargetLoad(), getMaxLoad(), getGrowFactor(),
                condition, getDefaultExpectedSize());
    }

    /**
     * This size is used in no-arg hash container factory methods.
     *
     * @return default expected size
     * @see #withDefaultExpectedSize(int)
     */
    public abstract int getDefaultExpectedSize();

    /**
     * Returns a config with the specified default expected size.
     *
     * @param defaultExpectedSize a positive value
     * @return hash config with the specified default expected size
     * @see #getDefaultExpectedSize()
     */
    public final HashConfig withDefaultExpectedSize(int defaultExpectedSize) {
        return create(getMinLoad(), getTargetLoad(), getMaxLoad(), getGrowFactor(),
                getShrinkCondition(), defaultExpectedSize);
    }
}
