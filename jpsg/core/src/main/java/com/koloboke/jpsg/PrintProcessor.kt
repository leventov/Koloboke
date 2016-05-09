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


class PrintProcessor : TemplateProcessor() {
    companion object {
        /**
         * After BlocksProcessor, not to process unintended //if//
         * branches and account //with// conditions
         */
        @JvmStatic val PRIORITY = Generator.BLOCKS_PROCESSOR_PRIORITY - 100;
    }
    override fun priority(): Int {
        return PRIORITY;
    }

    private val PREFIX = "/[\\*/]\\s*print\\s+(?<dim>[a-zA-Z]+)"
    private val PRINT_PATTERN = CheckingPattern.compile(PREFIX,
            "$PREFIX\\s*[\\*/]/[^/]*?/[\\*/]\\s*endprint\\s*[\\*/]/")

    override fun process(
            builder: StringBuilder, source: Context, target: Context, template: String) {
        val valueM = PRINT_PATTERN.matcher(template)
        val sb = StringBuilder()
        while (valueM.find()) {
            val dim = valueM.group("dim")
            val option = target.getOption(dim)
            if (option != null) {
                valueM.appendSimpleReplacement(sb, option.toString())
            } else {
                throw MalformedTemplateException.near(template, valueM.start(),
                        "Nonexistent dimension: $dim, available dims: $target")
            }
        }
        valueM.appendTail(sb)
        postProcess(builder, source, target, sb.toString())
    }
}