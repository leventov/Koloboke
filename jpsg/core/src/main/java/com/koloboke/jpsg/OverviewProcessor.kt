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

import com.koloboke.jpsg.MalformedTemplateException.Companion.lines
import java.io.IOException
import java.util.regex.Pattern


class OverviewProcessor : TemplateProcessor() {

    override fun priority(): Int {
        return PRIORITY
    }

    override fun process(sb: StringBuilder, source: Context, target: Context, template: String) {
        if (Generator.currentSourceFile().endsWith("package-info.java")) {
            val m: CheckingMatcher = OVERVIEW_PATTERN.matcher(template)
            if (m.find()) {
                val withoutOverview = template.substring(0, m.start()) + template.substring(m.end())
                postProcess(sb, source, target, withoutOverview)

                var overview = m.group(1)!!
                overview = lines(overview)
                        .map({ line -> JAVADOC_ASTERISKS.matcher(line).replaceFirst("") })
                        .joinToString("\n", "<html><body>", "</body></html>")
                val gen = Generator.currentGenerator()
                overview = gen.generate(source, target, overview)
                try {
                    gen.writeFile(gen.target!!.resolve("overview.html"), overview)
                } catch (e: IOException) {
                    throw RuntimeException(e)
                }
                return
            }
        }
        postProcess(sb, source, target, template)

    }

    companion object {
        val PRIORITY = TemplateProcessor.DEFAULT_PRIORITY + 10000

        private val OVERVIEW_PATTERN = CheckingPattern.compile(
                "//\\s+overview\\s+//", "//\\s+overview\\s+//(.*)//\\s+endOverview\\s+//")
        private val JAVADOC_ASTERISKS = Pattern.compile("^\\s*\\*")
    }
}
