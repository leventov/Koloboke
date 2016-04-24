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

package net.openhft.jpsg.concurrent

import org.slf4j.LoggerFactory
import java.util.concurrent.Callable

interface ForkJoinTasks {

    companion object {
        private val LOG = LoggerFactory.getLogger(ForkJoinTasks::class.java)

        private val forkJoinTasks: ForkJoinTasks = try {
            RealForkJoinTasks()
        } catch (t: Throwable) {
            LOG.warn("Not found ForkJoinTask class, recommended to use JDK 8")
            NoForkJoinTasks()
        }

        fun <V> adapt(callable: Callable<out V>): ForkJoinTaskShim<V> {
            return forkJoinTasks.adapt(callable)
        }

        fun <T> invokeAll(tasks: Iterable<ForkJoinTaskShim<T>>) {
            forkJoinTasks.invokeAll(tasks)
        }
    }

    fun <V> adapt(callable: Callable<out V>): ForkJoinTaskShim<V>

    fun <T> invokeAll(tasks: Iterable<ForkJoinTaskShim<T>>)
}
