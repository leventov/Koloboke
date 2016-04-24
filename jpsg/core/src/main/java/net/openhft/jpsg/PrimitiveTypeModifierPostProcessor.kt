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

import java.util.function.Predicate
import java.util.function.UnaryOperator


open class PrimitiveTypeModifierPostProcessor(
        private val keyword: String,
        private val typeMapper: UnaryOperator<PrimitiveType>,
        private val dimFilter: Predicate<String>) : TemplateProcessor() {

    override fun priority(): Int {
        return PRIORITY
    }

    override fun process(sb: StringBuilder, source: Context, target: Context, template: String) {
        var template = template
        for ((dim, targetT) in target) {
            if (!dimFilter.test(dim))
                continue
            if (targetT is PrimitiveType || targetT is ObjectType) {
                val kwDim = dim + "." + keyword
                val mapped = if (targetT is PrimitiveType)
                    typeMapper.apply(targetT)
                else
                    targetT // ObjectType maps to itself
                template = mapped.finalReplace(template, kwDim)
            }
        }
        postProcess(sb, source, target, template)
    }

    companion object {
        const val PRIORITY = OptionProcessor.PRIORITY - 10
    }
}
