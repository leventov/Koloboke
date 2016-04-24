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


open class PrimitiveTypeModifierPreProcessor(private val keyword: String,
                                             private val typeMapper: UnaryOperator<PrimitiveType>, private val dimFilter: Predicate<String>) : TemplateProcessor() {

    override fun priority(): Int {
        return PRIORITY
    }

    override fun process(sb: StringBuilder, source: Context, target: Context, template: String) {
        var template = template
        val modifier = OptionProcessor.modifier(keyword)
        for (e in source) {
            val dim = e.key
            if (!dimFilter.test(dim))
                continue
            if (e.value is PrimitiveType) {
                val targetT = target.getOption(dim)
                val sourceT = e.value as PrimitiveType
                val kwDim = dim + "." + keyword
                if (targetT is PrimitiveType && typeMapper.apply(targetT) !== targetT) {
                    val modP = OptionProcessor.prefixPattern(modifier, sourceT.standalone)
                    template = template.replace(modP.toRegex(), IntermediateOption.of(kwDim).standalone)
                }
                if (typeMapper.apply(sourceT) !== sourceT) {
                    template = typeMapper.apply(sourceT).intermediateReplace(template, kwDim)
                }
            }
        }
        // remove left modifier templates when for example target is object
        template = template.replace(modifier.toRegex(), "")
        postProcess(sb, source, target, template)
    }

    companion object {
        const val PRIORITY = OptionProcessor.PRIORITY + 10
    }
}
