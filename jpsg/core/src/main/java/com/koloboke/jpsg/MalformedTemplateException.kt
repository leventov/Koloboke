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

package com.koloboke.jpsg

import java.lang.Math.max
import java.lang.Math.min


class MalformedTemplateException private constructor(message: String) : RuntimeException(message) {
    companion object {

        @JvmStatic
        @JvmOverloads fun near(input: CharSequence, pos: Int,
                               message: String = "Malformed template near")
                : MalformedTemplateException {
            return MalformedTemplateException(makeMessageNear(input, pos, message))
        }

        internal fun lines(s: String): List<String> {
            val ls = s.split("\\n".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
            return ls.map({ l -> l + '\n' }).toList()
        }

        private fun makeMessageNear(input: CharSequence, pos: Int, message: String): String {
            val messageNear = StringBuilder()

            messageNear.append("Source file: " + Generator.currentSourceFile() + "\n")
            messageNear.append(message + ":\n")

            val s = input.toString()
            val lines = lines(s)
            var charCount = 0
            var targetLine = -1
            for (i in lines.indices) {
                val line = lines[i]
                if (pos < charCount + line.length) {
                    targetLine = i
                    break
                }
                charCount += line.length
            }
            val firstLine = max(targetLine - 2, 0)
            lines.subList(firstLine, targetLine + 1).forEach({ messageNear.append(it) })
            val pointer = String(CharArray(pos - charCount)).replace('\u0000', ' ') + "^\n"
            messageNear.append(pointer)
            val lastLine = min(targetLine + 3, lines.size)
            lines.subList(targetLine + 1, lastLine).forEach({ messageNear.append(it) })
            return messageNear.toString()
        }
    }
}
