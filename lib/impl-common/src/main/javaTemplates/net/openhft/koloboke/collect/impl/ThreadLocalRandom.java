/* if JDK6 jdk */
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

package net.openhft.koloboke.collect.impl;

import java.util.Random;


/**
 * Placeholder of {@link java.util.concurrent.ThreadLocalRandom} for Java 6.
 */
public final class ThreadLocalRandom {

    private static final ThreadLocal<Random> threadLocal = new ThreadLocal<Random>() {
        @Override
        protected Random initialValue() {
            return new Random();
        }
    };

    public static Random current() {
        return threadLocal.get();
    }

    private ThreadLocalRandom() {}
}
