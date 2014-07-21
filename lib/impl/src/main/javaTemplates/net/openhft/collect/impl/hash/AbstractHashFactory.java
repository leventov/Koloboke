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

import net.openhft.collect.hash.HashConfig;
import net.openhft.collect.hash.HashContainerFactory;

import javax.annotation.Nonnull;


abstract class AbstractHashFactory {
    final HashConfig hashConf;
    final HashConfigWrapper configWrapper;
    final int defaultExpectedSize;

    AbstractHashFactory(HashConfig hashConf, int defaultExpectedSize) {
        this.hashConf = hashConf;
        configWrapper = new HashConfigWrapper(hashConf);
        this.defaultExpectedSize = defaultExpectedSize;
    }

    @Nonnull
    public final HashConfig getHashConfig() {
        return hashConf;
    }

    public final int getDefaultExpectedSize() {
        return defaultExpectedSize;
    }

    String commonString() {
        return "hashConfig=" + getHashConfig() + ",defaultExpectedSize=" + getDefaultExpectedSize();
    }

    boolean commonEquals(HashContainerFactory<?> other) {
        return getHashConfig().equals(other.getHashConfig()) &&
                getDefaultExpectedSize() == other.getDefaultExpectedSize();
    }

    int commonHashCode() {
        int hashCode = 17;
        hashCode = hashCode * 31 + getHashConfig().hashCode();
        return hashCode * 31 + getDefaultExpectedSize();
    }
}
