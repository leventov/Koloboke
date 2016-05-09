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

import java.util.*
import java.util.regex.Pattern


class FunctionProcessor : TemplateProcessor() {

    public override fun priority(): Int {
        return PRIORITY
    }

    override fun process(sb: StringBuilder, source: Context, target: Context, template: String) {
        val titleToDim = HashMap<String, String>()
        for (e in source) {
            val opt = e.value
            if (opt is PrimitiveType) {
                titleToDim.put(opt.title, e.key)
            } else if (opt is ObjectType) {
                titleToDim.put(opt.idStyle.title, e.key)
            }
        }
        var prevEnd = 0
        val m = FUNCTION_P.matcher(template)
        while (m.find()) {
            var functionClass: String? = m.group(1)
            if (functionClass == null)
                functionClass = m.group(3)
            val parts = CAMEL_CASE.split(functionClass)
            val argDims = ArrayList<String>()
            var i = 0
            while (titleToDim[parts[i]] != null) {
                argDims.add(titleToDim[parts[i]]!!)
                i++
            }
            var outDim: String?
            if ("To" == parts[i]) {
                i++
                if (titleToDim[parts[i]] != null) {
                    outDim = titleToDim[parts[i]]
                    i++
                } else {
                    throw MalformedTemplateException.near(template, m.start(),
                            "Function \"out\" type (after `To` infix) is not present" +
                                    "in the source context: " + source.toString())
                }
            } else {
                outDim = null
            }
            var baseName = ""
            for (j in i..parts.size - 1) {
                baseName += parts[j]
            }
            if (argDims.isEmpty()) {
                throw MalformedTemplateException.near(template, m.start(),
                        "Function should have at 1 or 2 \"input\" type params and " + "0 or 1 \"out\" type param (after `To` infix)")
            }
            val allowOperatorCollapse: Boolean
            if ("BinaryOperator" == baseName || "UnaryOperator" == baseName) {
                if (argDims.size != 1)
                    throw MalformedTemplateException.near(template, m.start(),
                            baseName + " can have only one only 1 \"input\" type param, " +
                                    argDims.size + " given: " + argDims)
                val dim = argDims[0]
                if (baseName.startsWith("Binary"))
                    argDims.add(dim)
                outDim = dim
                baseName = "Function"
                allowOperatorCollapse = true
            } else {
                allowOperatorCollapse = false
            }

            val templateStartM = TEMPLATE_START.matcher(template.substring(m.end()))

            val noTemplateAhead = m.group(2) != null || templateStartM.find() && templateStartM.start() == 0

            postProcess(sb, source, target, template.substring(prevEnd, m.start()))
            val generatedName = generateName(
                    argDims, outDim, baseName, allowOperatorCollapse, !noTemplateAhead, target)
            sb.append(generatedName)
            prevEnd = m.end()
        }
        postProcess(sb, source, target, template.substring(prevEnd))
    }

    companion object {
        // after blocks processor, before options processor
        val PRIORITY = (OptionProcessor.PRIORITY + Generator.BLOCKS_PROCESSOR_PRIORITY) / 2

        private val FUNCTION_P = RegexpUtils.compile(
                "/[\\*/]f[\\*/]/([a-z]+)(/[\\*/][\\*/]/)?+|([a-z]+)/[\\*/]ef[\\*/]/")

        /**
         * template start or end of input, because template file is split by // if //s
         * in blocks preprocessor
         */
        private val TEMPLATE_START = Pattern.compile("/[\\*/]|$")
        private val CAMEL_CASE = Pattern.compile("(?<!^)(?=[A-Z])")

        private fun generateName(argDims: List<String>,
                                 outDim: String?,
                                 baseName: String,
                                 allowOperatorCollapse: Boolean,
                                 withParams: Boolean,
                                 target: Context): String {
            val args = argDims.map({ target.getOption(it) }).toList()
            val out = if (outDim != null) target.getOption(outDim) else null
            if (out is PrimitiveType &&
                    (argDims.size == 1 &&
                            args.get(0) === out || allowOperatorCollapse && argDims.size == 2 &&
                    argDims[0] == argDims[1] &&
                    argDims[0] == outDim)) {
                val infix = if (argDims.size == 1) "Unary" else "Binary"
                return out.title + infix + "Operator"
            }
            var prefix = ""
            var infix = ""
            val params = ArrayList<String>()
            for (i in args.indices) {
                val arg = args.get(i)
                if (arg is PrimitiveType) {
                    prefix += arg.title
                } else {
                    if (args.size > 1)
                        prefix += (arg as ObjectType).idStyle.title
                    params.add("? super " + argDims[i].substring(0, 1).toUpperCase())
                }
            }
            if (args.size == 2 &&
                    args.get(0) is ObjectType && args.get(1) is ObjectType) {
                prefix = ""
                infix = "Bi"
            }
            if (outDim != null) {
                if (out is PrimitiveType) {
                    prefix += "To" + out.title
                } else {
                    params.add("? extends " + outDim.substring(0, 1).toUpperCase())
                }
            }
            return prefix + infix + baseName + if (withParams) joinParams(params) else ""
        }

        private fun joinParams(params: List<String>): String {
            if (params.isEmpty())
                return ""
            var res = ""
            for (param in params) {
                res += ", " + param
            }
            return "<" + res.substring(2) + ">"
        }
    }
}
