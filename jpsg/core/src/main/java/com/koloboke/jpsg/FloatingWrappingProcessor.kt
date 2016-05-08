///*
// * Copyright 2014 the original author or authors.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.koloboke.jpsg
//
//
//class FloatingWrappingProcessor : TemplateProcessor() {
//
//    override fun process(builder: StringBuilder, source: Context, target: Context, template: String) {
//        val sb = StringBuilder()
//        val m = WRAPPING_P.matcher(template)
//        while (m.find()) {
//            val body = m.group(if (m.group("closed") != null) "closedBody" else "openBody")
//            val targetType = target.getOption(m.group("dim"))
//            val wrap = m.group("op") == "wrap"
//            var repl = body
//            if (targetType === PrimitiveType.FLOAT) {
//                repl = "Float." + (if (wrap) "intBitsToFloat" else "floatToIntBits") + "(" + repl + ")"
//            } else if (targetType === PrimitiveType.DOUBLE) {
//                repl = "Double." + (if (wrap) "longBitsToDouble" else "doubleToLongBits") + "(" +
//                        repl + ")"
//            }
//            m.appendSimpleReplacement(sb, repl)
//        }
//        m.appendTail(sb)
//        postProcess(builder, source, target, sb.toString())
//    }
//
//    companion object {
//
//        private val WRAPPING_PREFIX = "/[\\*/]\\s*(?<op>wrap|unwrap)"
//        private val WRAPPING_P = CheckingPattern.compile(WRAPPING_PREFIX,
//                "$WRAPPING_PREFIX\\s+(?<dim>[a-z0-9]+)\\s*[\\*/]/((?<closed>(?<closedBody>[^/]+)/[\\*/][\\*/]/)|(?<openBody>[^\\s\\{\\};/\\*]+))")
//    }
//}
