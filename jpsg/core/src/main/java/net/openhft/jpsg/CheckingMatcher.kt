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

package net.openhft.jpsg

import java.util.regex.*


class CheckingMatcher private constructor(
        private val input: CharSequence,
        private val checkingMatcher: Matcher,
        private val targetMatcher: Matcher) : MatchResult {
    private var lastAppendPosition = 0

    fun find(): Boolean {
        if (!checkingMatcher.find())
            return false
        val start = checkingMatcher.start()
        if (!targetMatcher.find(start) || targetMatcher.start() != start) {
            throw MalformedTemplateException.near(input, start)
        }
        return true
    }

    override fun start(): Int {
        return targetMatcher.start()
    }

    override fun start(group: Int): Int {
        return targetMatcher.start(group)
    }

    fun start(name: String): Int {
        return targetMatcher.start(name)
    }

    override fun end(): Int {
        return targetMatcher.end()
    }

    override fun end(group: Int): Int {
        return targetMatcher.end(group)
    }

    fun end(name: String): Int {
        return targetMatcher.end(name)
    }

    override fun group(): String {
        return targetMatcher.group()
    }

    override fun group(group: Int): String? {
        return targetMatcher.group(group)
    }

    fun group(name: String): String? {
        return targetMatcher.group(name)
    }

    override fun groupCount(): Int {
        return targetMatcher.groupCount()
    }

    fun appendSimpleReplacement(sb: StringBuilder, replacement: String): CheckingMatcher {
        sb.append(input, lastAppendPosition, targetMatcher.start())
        sb.append(replacement)
        lastAppendPosition = targetMatcher.end()
        return this
    }

    fun appendTail(sb: StringBuilder): StringBuilder {
        return sb.append(input, lastAppendPosition, input.length)
    }

    fun matches(): Boolean {
        return targetMatcher.matches()
    }

    fun region(start: Int, end: Int): CheckingMatcher {
        lastAppendPosition = 0
        checkingMatcher.region(start, end)
        targetMatcher.region(start, end)
        return this
    }

    companion object {

        @JvmStatic
        fun create(input: CharSequence,
                            checkingPattern: Pattern, targetPattern: Pattern): CheckingMatcher {
            return CheckingMatcher(input,
                    checkingPattern.matcher(input), targetPattern.matcher(input))
        }
    }
}
