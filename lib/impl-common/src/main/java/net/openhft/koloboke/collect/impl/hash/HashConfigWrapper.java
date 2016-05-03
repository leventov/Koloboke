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

package net.openhft.koloboke.collect.impl.hash;

import net.openhft.koloboke.collect.hash.HashConfig;
import net.openhft.koloboke.collect.impl.Scaler;


public final class HashConfigWrapper {

    private final HashConfig config;
    private final Scaler minLoadInverse;
    private final Scaler targetLoadInverse;
    private final Scaler maxLoad, maxLoadInverse;
    private final Scaler growFactor;

    public HashConfigWrapper(HashConfig config) {
        this.config = config;
        minLoadInverse = Scaler.by(
                // minLoad can be 0.0
                config.getMinLoad() > 0.0 ? 1.0 / config.getMinLoad() : Double.MAX_VALUE);
        targetLoadInverse = Scaler.by(1.0 / config.getTargetLoad());
        maxLoad = Scaler.by(config.getMaxLoad());
        maxLoadInverse = Scaler.by(1.0 / config.getMaxLoad());
        growFactor = Scaler.by(config.getGrowFactor());
    }

    public HashConfig config() {
        return config;
    }

    /**
     * Computes hash table capacity for the given size and min load of this config.
     *
     * @param size size of the hash table to compute capacity for
     * @return if the given size is non-negative, returns the least int capacity such that
     *         size / capacity < {@code config().getMinLoad()}, or {@code Integer.MAX_VALUE}
     *         if there is no such capacity. If size is negative, result is undefined.
     */
    public int maxCapacity(int size) {
        return minLoadInverse.scaleUpper(size);
    }

    /**
     * Computes hash table capacity for the given size and min load of this config.
     *
     * @param size size of the hash table to compute capacity for
     * @return if the given size is non-negative, returns the least long capacity
     *         such that size / capacity < {@code config().getMinLoad()}.
     *         If size is negative or there is no such long capacity, result is undefined.
     */
    public long maxCapacity(long size) {
        return minLoadInverse.scaleUpper(size);
    }

    /**
     * Computes hash table capacity for the given size and target load of this config.
     *
     * @param size size of the hash table to compute capacity for
     * @return if the given size is non-negative, returns the least int capacity such that
     *         size / capacity < {@code config().getTargetLoad()}, or {@code Integer.MAX_VALUE}
     *         if there is no such capacity. If size is negative, result is undefined.
     */
    public int targetCapacity(int size) {
        return targetLoadInverse.scaleUpper(size);
    }

    /**
     * Computes hash table capacity for the given size and target load of this config.
     *
     * @param size size of the hash table to compute capacity for
     * @return if the given size is non-negative, returns the least long capacity
     *         such that size / capacity < {@code config().getTargetLoad()}.
     *         If size is negative or there is no such long capacity, result is undefined.
     */
    public long targetCapacity(long size) {
        return targetLoadInverse.scaleUpper(size);
    }


    /**
     * Computes hash table capacity for the given size and max load of this config.
     *
     * @param size size of the hash table to compute capacity for
     * @return if the given size is non-negative, returns the least int capacity such that
     *         size / capacity < {@code config().getMaxLoad()}, or {@code Integer.MAX_VALUE}
     *         if there is no such capacity. If size is negative, result is undefined.
     */
    public int minCapacity(int size) {
        return maxLoadInverse.scaleUpper(size);
    }

    /**
     * Computes hash table capacity for the given size and max load of this config.
     *
     * @param size size of the hash table to compute capacity for
     * @return if the given size is non-negative, returns the least long capacity
     *         such that size / capacity < {@code config().getMaxLoad()}.
     *         If size is negative or there is no such long capacity, result is undefined.
     */
    public long minCapacity(long size) {
        return maxLoadInverse.scaleUpper(size);
    }

    public int maxSize(int capacity) {
        return maxLoad.scaleLower(capacity);
    }

    public long maxSize(long capacity) {
        return maxLoad.scaleLower(capacity);
    }

    /**
     * Computes grown hash table capacity for the given capacity
     * and grow factor of this config.
     *
     * @param capacity capacity of the hash table to grow
     * @return if the given capacity is non-negative, returns the least int capacity
     *         such that |new capacity - the given capacity * {@code config().getGrowFactor()}| < 1,
     *         or {@code Integer.MAX_VALUE} if there is no such capacity.
     *         If the given capacity is negative, result is undefined.
     */
    public int grow(int capacity) {
        return growFactor.scaleLower(capacity);
    }

    /**
     * Computes grown hash table capacity for the given capacity
     * and grow factor of this config.
     *
     * @param capacity capacity of the hash table to grow
     * @return if the given capacity is non-negative, returns the least long capacity
     *         such that |new capacity - the given capacity * {@code config().getGrowFactor()}| < 1.
     *         If there is no such capacity or the given capacity is negative, result is undefined.
     */
    public long grow(long capacity) {
        return growFactor.scaleLower(capacity);
    }

    @Override
    public int hashCode() {
        return config.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        assert obj.getClass() != HashConfig.class; // dangerous confusion
        return obj instanceof HashConfigWrapper &&
                config.equals(((HashConfigWrapper) obj).config());
    }

    @Override
    public String toString() {
        return "HashConfigWrapper{config=" + config + "}";
    }
}
