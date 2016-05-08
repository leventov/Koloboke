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

import java.lang.String.format
import java.util.*
import java.util.regex.Pattern


class Dimensions private constructor(private val dimensions: LinkedHashMap<String, List<Option>>) {

    class Parser internal constructor(private val defaultTypes: List<Option>, private val objectIdStyle: ObjectType.IdentifierStyle) {

        internal fun parseClassName(className: String): Dimensions {
            val typeMatcher = PRIM_TITLE_P.matcher(className)
            val types = ArrayList<String>()
            while (typeMatcher.find()) {
                val part = typeMatcher.group()
                if (!types.contains(part))
                    types.add(part)
            }
            // dimension names are conventional generic variable names in java
            val dimensions = LinkedHashMap<String, List<Option>>()
            if (types.size >= 1)
                addClassNameDim(dimensions, "t", parseOption(types[0]))
            if (types.size >= 2)
                addClassNameDim(dimensions, "u", parseOption(types[1]))
            if (types.size >= 3)
                addClassNameDim(dimensions, "v", parseOption(types[2]))
            return Dimensions(dimensions)
        }

        /**
         * @param descriptor in format "opt1|opt2 dim1 opt3|opt4 dim2"
         */
        fun parseForContext(descriptor: String): Dimensions {
            try {
                return parse(descriptor, null, false)
            } catch (e: NonexistentDimensionException) {
                throw AssertionError(e)
            }

        }

        @Throws(NonexistentDimensionException::class)
        fun parseForCondition(descriptor: String, context: Context): Dimensions {
            return parse(descriptor, context, true)
        }

        @Throws(NonexistentDimensionException::class)
        private fun parse(descriptor: String, context: Context?, checkContext: Boolean): Dimensions {
            val m = DIMENSION_P.matcher(descriptor)
            val dimensions = LinkedHashMap<String, List<Option>>()
            while (m.find()) {
                val dim = m.group("dim")
                if (checkContext && context!!.getOption(dim) == null)
                    throw NonexistentDimensionException()
                val opts = parseOptions(m.group("options"))
                dimensions.put(dim, opts)
            }
            return Dimensions(dimensions)
        }

        /**
         * @param descriptor in format "dim1=opt1|opt2,dim2=opt3|opt4"
         */
        internal fun parseCLI(descriptor: String): Dimensions {
            val dimDescriptors = descriptor.split(",".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
            val dimensions = LinkedHashMap<String, List<Option>>()
            for (dimDescriptor in dimDescriptors) {
                val parts = dimDescriptor.split("=".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                dimensions.put(parts[0], parseOptions(parts[1]))
            }
            return Dimensions(dimensions)
        }

        private fun addClassNameDim(dimensions: LinkedHashMap<String, List<Option>>, dim: String, main: Option) {
            val keyOptions = ArrayList<Option>()
            keyOptions.add(main)
            defaultTypes.filter({ type -> !keyOptions.contains(type) }).forEach({ keyOptions.add(it) })
            dimensions.put(dim, keyOptions)
        }

        internal fun parseOptions(options: String): List<Option> {
            return parseOptions(options, objectIdStyle)
        }

        private fun parseOption(opt: String): Option {
            return parseOption(opt, objectIdStyle)
        }

        companion object {

            internal fun parseOptions(options: String, objectIdStyle: ObjectType.IdentifierStyle): List<Option> {
                val opts = options.split("\\|".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                val result = ArrayList<Option>()
                for (option in opts) {
                    result.add(parseOption(option, objectIdStyle))
                }
                return result
            }

            private fun parseOption(opt: String, objectIdStyle: ObjectType.IdentifierStyle): Option {
                when (opt.toUpperCase()) {
                    "INT" -> return PrimitiveType.INT
                    "LONG" -> return PrimitiveType.LONG
                    "FLOAT" -> return PrimitiveType.FLOAT
                    "DOUBLE" -> return PrimitiveType.DOUBLE
                    "OBJ", "OBJECT" -> return ObjectType.get(objectIdStyle)
                    "BYTE" -> return PrimitiveType.BYTE
                    "CHAR" -> return PrimitiveType.CHAR
                    "SHORT" -> return PrimitiveType.SHORT
                    else -> return SimpleOption(opt)
                }
            }
        }
    }

    fun generateContexts(): List<Context> {
        var totalCombinations = 1
        for (options in dimensions.values) {
            totalCombinations *= options.size
        }
        val contexts = ArrayList<Context>(totalCombinations)
        for (comb in 0..totalCombinations - 1) {
            val cb = Context.builder()
            var combRem = comb
            for (e in dimensions.entries) {
                val options = e.value
                val index = combRem % options.size
                combRem /= options.size
                val option = options[index]
                cb.put(e.key, option)
            }
            contexts.add(cb.makeContext())
        }
        return contexts
    }

    fun checkAsCondition(context: Context): Boolean {
        for ((dim, conditionOptions) in dimensions) {
            val contextOption = context.getOption(dim)
            if (!conditionOptions.contains(contextOption))
                return false
        }
        return true
    }


    override fun toString(): String {
        return dimensions.toString()
    }

    companion object {

        private val OPTIONS = "([a-z0-9]+)((\\|[a-z0-9]+)+)?"

        internal val DIMENSION = format("(?<options>%s)\\s+(?<dim>[a-z]+)", OPTIONS)

        private val DIMENSION_P = RegexpUtils.compile(DIMENSION)

        private val PRIM_TITLE_P = Pattern.compile("Double|Float|Int|Long|Byte|Short|Char")
    }
}
