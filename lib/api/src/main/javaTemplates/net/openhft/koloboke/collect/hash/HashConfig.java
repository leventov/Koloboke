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


package net.openhft.koloboke.collect.hash;

import com.google.auto.value.AutoValue;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


/**
 * A config object that holds configurations of hash container's loads and dynamic behaviour.
 *
 * <p>Instead of a single <em>load factor</em>, available for configuration in most other hash
 * collections APIs, {@code HashConfig} allows to configure three loads:
 *
 * <ul>
 *     <li><em>Target load</em> denotes the desirable hash table load. On hash container
 *     construction capacity is chosen in order to table's load to be as close to the target load,
 *     as possible.</li>
 *
 *     <li><em>Min load</em> denotes the minimum load a hash table will try to never be sparser
 *     than.</li>
 *
 *     <li><em>Max load</em> denotes the maximum load a hash table will try to never be denser
 *     than.</li>
 * </ul>
 *
 * <p><a name="invariants"></a>There is an obvious invariant over the loads:
 * <pre>{@code
 * 0 <= min load <= target load <= max load <= 1}</pre>
 * But the target load shouldn't touch the bounds:
 * <pre>{@code
 * 0 < target load < 1}</pre>
 * The loads are bounded within the {@code [0, 1]} range,
 * since all hashes in the library
 * use <a href="http://en.wikipedia.org/wiki/Open_addressing">open addressing</a>
 * method of collision resolution.
 *
 * <p>Also {@code HashConfig} allows to configure the <em>grow factor</em>. When elements
 * are inserted into the hash container and it grows, when
 * {@linkplain HashContainer#currentLoad() the hash container load} reaches the <em>max load</em>,
 * hash table's capacity is multiplied by the grow factor, immediately after that the current load
 * is supposed to be at or a bit higher than the <em>min load</em>. That is why {@code HashConfig}
 * keeps one more invariant:
 * <pre>{@code
 * 1.0 < grow factor <= max load / min load}</pre>
 *
 * <p>The schema explained above allows much more precise control over
 * memory footprint -- performance tradeoff of hash containers, than a single load factor
 * configuration.
 *
 * <p>Hash config is immutable, all "setters" return a new independent config object with
 * the corresponding field changed.
 *
 * @see HashContainer
 * @see HashContainerFactory#withHashConfig(HashConfig)
 */
@AutoValue
public abstract class HashConfig {

    private static final double DEFAULT_MIN_LOAD = 1.0 / 3.0;
    private static final double DEFAULT_MAX_LOAD = 2.0 / 3.0;
    private static final double DEFAULT_TARGET_LOAD = 0.5;
    private static final double DEFAULT_GROW_FACTOR = 2.0;
    private static final HashConfig DEFAULT = create(
            DEFAULT_MIN_LOAD, DEFAULT_TARGET_LOAD, DEFAULT_MAX_LOAD, DEFAULT_GROW_FACTOR, null);

    /**
     * Returns a hash config with 0.(3) min load, 0.5 target load, 0.(6) max load, 2.0 grow factor
     * and {@code null} shrink condition.
     *
     * @return the default hash config
     */
    @Nonnull
    public static HashConfig getDefault() {
        return DEFAULT;
    }

    /**
     * Returns a new hash config with the given loads and the grow factor set to
     * {@code maxLoad / minLoad}.
     *
     * <p>The shrink condition in the returned hash config is left default, i. e. {@code null}.
     *
     * @param minLoad the min load, should be in the {@code [0.0, targetLoad]} range
     * @param targetLoad the target load, should be in the {@code [minLoad, maxLoad]} range
     * @param maxLoad the max load, should be in the {@code [targetLoad, 1.0]} range
     * @return a hash config with the given loads and the grow factor of {@code maxLoad / minLoad}
     * @throws IllegalArgumentException if the given loads violate
     *         the hash config <a href="#invariants">invariants</a>
     */
    @Nonnull
    public static HashConfig fromLoads(double minLoad, double targetLoad, double maxLoad) {
        return create(minLoad, targetLoad, maxLoad, maxLoad / minLoad, null);
    }

    private static HashConfig create(
            double minLoad, double targetLoad, double maxLoad, double growFactor,
            @Nullable net.openhft.koloboke.function.Predicate<HashContainer> shrinkCondition) {
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
                    "Max load must be in [%f (target load), 1.0]  range, %f given.",
                    targetLoad, maxLoad));
        }
        if (Double.isNaN(growFactor) || growFactor <= 1.0 || growFactor > maxLoad / minLoad) {
            throw new IllegalArgumentException(String.format(
                    "Grow factor must be in [1.0, max load / min load = %f]  range, %f given.",
                    maxLoad / minLoad, growFactor));
        }
        return new AutoValue_HashConfig(minLoad, targetLoad,
                maxLoad, growFactor, shrinkCondition);
    }


    /**
     * Package-private constructor to prevent subclassing from outside of the package
     */
    HashConfig() {}

    /**
     * Returns the min load of this hash config. It denotes the minimum load a hash table will
     * try to never be sparser than.
     *
     * <p>The default is 0.(3) (one-third).
     *
     * @return the minimum load, a value
     *         in the [{@code 0.0}, {@link #getTargetLoad() target load}] range
     * @see #withMinLoad(double)
     */
    public abstract double getMinLoad();

    /**
     * Returns a copy of this hash config with the min load set to the given value.
     *
     * <p>Min load allows to limit memory usage of hash containers.
     *
     * <p>Updatable and mutable linear hash tables can't have min load greater than 0.5.
     *
     * @param minLoad the new min load, a value
     *                in the [{@code 0.0}, {@link #getTargetLoad() target load}] range
     * @return a copy of this hash config with the min load set to the given value
     * @throws IllegalArgumentException if the resulting hash config violates
     *         the <a href="#invariants">invariants</a>
     * @see #getMinLoad()
     */
    public final HashConfig withMinLoad(double minLoad) {
        return create(minLoad, getTargetLoad(), getMaxLoad(), getGrowFactor(),
                getShrinkCondition());
    }

    /**
     * Returns the target load of this hash config. It denotes the desirable hash table load.
     * On hash container construction capacity is chosen in order to table's load to be as close
     * to the target load, as possible.
     *
     * <p>{@link HashContainer#shrink()} rehashes a table to the target load.
     *
     * @return the target load, a value
     *         in the [{@link #getMinLoad() min load}, {@link #getMaxLoad() max load}] range
     * @see #withTargetLoad(double)
     */
    public abstract double getTargetLoad();

    /**
     * Returns a copy of this hash config with the target load set to the given value.
     *
     * <p>Target load allows to control basic memory usage -- performance tradeoff for hash tables.
     *
     * @param targetLoad the new target load, a value in the
     *                   [{@link #getMinLoad() min load}, {@link #getMaxLoad() max load}] range
     * @return a copy of this hash config with the target load set to the given value
     * @throws IllegalArgumentException if the resulting hash config violates
     *         the <a href="#invariants">invariants</a>
     * @see #getTargetLoad()
     */
    public final HashConfig withTargetLoad(double targetLoad) {
        return create(getMinLoad(), targetLoad, getMaxLoad(), getGrowFactor(),
                getShrinkCondition());
    }

    /**
     * Returns the max load of this hash config. It denotes the maximum load a hash table will
     * try to never be denser than.
     *
     * @return the maximum load, a value
     *         in the [{@link #getTargetLoad() target load}, {@code 1.0}] range
     * @see #withMaxLoad(double)
     */
    public abstract double getMaxLoad();

    /**
     * Returns a copy of this hash config with the max load set to the given value.
     *
     * <p>Max load allows to limit the <em>minimum</em> performance of hash tables,
     * because too dense hash tables operate slowly.
     *
     * @param maxLoad the new max load, a value
     *                in the [{@link #getTargetLoad() target load}, {@code 1.0}] range
     * @return a copy of this hash config with the max load set to the given value
     * @throws IllegalArgumentException if the resulting hash config violates
     *         the <a href="#invariants">invariants</a>
     * @see #getMaxLoad()
     */
    public final HashConfig withMaxLoad(double maxLoad) {
        return create(getMinLoad(), getTargetLoad(), maxLoad, getGrowFactor(),
                getShrinkCondition());
    }

    /**
     * Returns the grow factor of this hash config. It denotes how much a hash container's capacity
     * is increased on periodical rehashes on adding (putting) new elements (entries).
     *
     * @return the grow factor, a value in the [{@code 1.0},
     *         {@link #getMaxLoad() max load} / {@link #getMinLoad() min load}] range
     * @see #withGrowFactor(double)
     */
    public abstract double getGrowFactor();

    /**
     * Returns a copy of this hash config with the grow factor set to the given value.
     *
     * <p>Grow factor allows to control memory usage -- performance tradeoff for steadily growing
     * hash tables.
     *
     * <p>Linear hash tables can't have any grow factor other than 2.0.
     *
     * @param growFactor the new grow factor, a value in the [{@code 1.0},
     *                   {@link #getMaxLoad() max load} / {@link #getMinLoad() min load}] range
     * @return a copy of this hash config with the grow factor set to the given value
     * @throws IllegalArgumentException if the resulting hash config violates
     *         the <a href="#invariants">invariants</a>
     * @see #getGrowFactor()
     */
    public final HashConfig withGrowFactor(double growFactor) {
        return create(getMinLoad(), getTargetLoad(), getMaxLoad(), growFactor,
                getShrinkCondition());
    }

    /**
     * Returns the <em>shrink condition</em> of this hash config.
     *
     * <p>Immediately after hash set or map construction from non-distinct sources (e. g. arrays)
     * it's {@linkplain HashContainer#currentLoad() load} could be significantly less than
     * the target factor due to expansion. The shrink condition is a predicate which is used
     * to shrink too sparse hash containers automatically.
     *
     * <p>{@code null} condition is considered as constant {@code false} predicate: never shrink.
     * It is a default value.
     *
     * <p>Particularly useful for immutable containers construction,
     * because they couldn't be shrunk manually after being returned from the factory method.
     *
     * @return the shrink condition of this hash config
     * @see #withShrinkCondition(net.openhft.koloboke.function.Predicate)
     * @see HashContainer#shrink()
     */
    @Nullable
    public abstract net.openhft.koloboke.function.Predicate<HashContainer> getShrinkCondition();

    /**
     * Returns a copy of this hash config with the shrink condition set to the given predicate.
     *
     * <p>An example of sensible shrink condition:
     * <pre>
     * <code>
     * // if JDK8 jdk //conf.withShrinkCondition(h -&gt; h.currentLoad() + 0.1 &lt; h.hashConfig().getTargetLoad());
     * // elif !(JDK8 jdk) //conf.withShrinkCondition(new Predicate&lt;HashContainer&gt;() {
     *     {@literal @}Override
     *     public boolean test(HashContainer h) {
     *         return h.currentLoad() + 0.1 &lt; h.hashConfig().getTargetLoad();
     *     }
     * });// endif //
     * </code>
     * </pre>
     *
     * @param condition the new shrink condition
     * @return a copy of this hash config with the shrink condition set to the given predicate
     * @see #getShrinkCondition()
     */
    public final HashConfig withShrinkCondition(
            @Nullable net.openhft.koloboke.function.Predicate<HashContainer> condition) {
        return create(getMinLoad(), getTargetLoad(), getMaxLoad(), getGrowFactor(), condition);
    }
}
