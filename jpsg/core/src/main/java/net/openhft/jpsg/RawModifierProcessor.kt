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
//package net.openhft.jpsg
//
//
//class RawModifierProcessor : TemplateProcessor() {
//
//    override fun priority(): Int {
//        return PRIORITY
//    }
//
//    override fun process(sb: StringBuilder, source: Context, target: Context, template: String) {
//        var template = template
//        for (e in source) {
//            val dim = e.key
//            if (e.value is PrimitiveType && target.getOption(dim) is ObjectType) {
//                val sourceT = e.value as PrimitiveType
//                val rawP = OptionProcessor.prefixPattern(RAW,
//                        "(" + sourceT.className + "|" + sourceT.standalone + ")")
//                template = template.replace(rawP.toRegex(), "Object")
//            }
//        }
//        // remove left modifier templates when for example target is primitive type
//        template = template.replace(RAW.toRegex(), "")
//        postProcess(sb, source, target, template)
//    }
//
//    companion object {
//        /**
//         * `RawModifierProcessor` should run before any [PrimitiveTypeModifierPreProcessor]
//         */
//        val PRIORITY = PrimitiveTypeModifierPreProcessor.PRIORITY + 10
//
//        private val RAW = OptionProcessor.modifier("raw")
//    }
//}
