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

package net.openhft.collect.impl.hash;

final class Capacities {

    /**
     * Chooses lesser or greater capacity, which one is better for the given {@code size} and
     * hash config. (The {@code desiredCapacity} is just precomputed
     * {@code conf.targetCapacity(size)}).
     *
     * <p>Chooses the capacity which is closer to the {@code desiredCapacity} and conform min or
     * max capacity bounds for the given {@code size} and hash config.
     *
     * <p>If both {@code lesserCapacity} and {@code greaterCapacity} are out of these bounds,
     * {@code onFail} value is returned.
     *
     * @param conf the {@code HashConfigWrapper}
     * @param size should be non-negative
     * @param desiredCapacity precomputed {@code conf.targetCapacity(size)}
     * @param lesserCapacity should be greater than the {@code size} but lesser
     *        than the {@code desiredCapacity}
     * @param greaterCapacity should be greater than the {@code desiredCapacity}
     * @param onFail the value to return if both {@code lesserCapacity} and {@code greaterCapacity}
     *        are lesser than min size and greater than max size respectively
     *        for the given hash config and size
     * @return {@code lesserCapacity} or {@code greaterCapacity}
     * @see #chooseBetter(HashConfigWrapper, long, long, long, long, long)
     */
    static int chooseBetter(HashConfigWrapper conf, int size,
            int desiredCapacity, int lesserCapacity, int greaterCapacity, int onFail) {
        assert 0 <= size;
        assert size < lesserCapacity && lesserCapacity < desiredCapacity;
        assert desiredCapacity < greaterCapacity;
        if (greaterCapacity - desiredCapacity <= desiredCapacity - lesserCapacity &&
                greaterCapacity <= conf.maxCapacity(size)) {
            return greaterCapacity;
        }
        return lesserCapacity >= conf.minCapacity(size) ? lesserCapacity : onFail;
    }

    /**
     * Same as {@link #chooseBetter(HashConfigWrapper, int, int, int, int, int)}.
     *
     * @see #chooseBetter(HashConfigWrapper, int, int, int, int, int)
     */
    static long chooseBetter(HashConfigWrapper conf, long size,
            long desiredCapacity, long lesserCapacity, long greaterCapacity, long onFail) {
        assert 0L <= size;
        assert size < lesserCapacity && lesserCapacity < desiredCapacity;
        assert desiredCapacity < greaterCapacity;
        if (greaterCapacity - desiredCapacity <= desiredCapacity - lesserCapacity &&
                greaterCapacity <= conf.maxCapacity(size)) {
            return greaterCapacity;
        }
        return lesserCapacity >= conf.minCapacity(size) ? lesserCapacity : onFail;
    }

    private Capacities() {}
}
