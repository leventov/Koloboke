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

package net.openhft.jpsg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static java.util.stream.Collectors.toList;


public final class Jdk8FunctionReplacer extends TemplateProcessor {
    private static final Logger log = LoggerFactory.getLogger(Jdk8FunctionReplacer.class);
    public static final int PRIORITY = DEFAULT_PRIORITY - 10000;

    @Override
    protected int priority() {
        return PRIORITY;
    }

    private static final SimpleOption JDK8 = new SimpleOption("JDK8");

    @Override
    protected void process(StringBuilder sb, Context source, Context target, String template) {
        if (JDK8.equals(target.getOption("jdk")) &&
                // Heuristic that we are at the end of the template class
                template.trim().endsWith("}")) {
            template = sb.toString() + template;
            for (String jdk8Interface : JDK8_INTERFACES) {
                template = template.replace(jdk8Interface, "import java.util.function." +
                        jdk8Interface.substring("import net.openhft.function.".length()));
            }
            sb.setLength(0);
            sb.append(template);
        } else {
            postProcess(sb, source, target, template);
        }
    }

    private static final List<String> JDK8_INTERFACES = Arrays.asList(
            "BiConsumer",
            "BiFunction",
            "BinaryOperator",
            "BiPredicate",
            "BooleanSupplier",
            "Consumer",
            "DoubleBinaryOperator",
            "DoubleConsumer",
            "DoubleFunction",
            "DoublePredicate",
            "DoubleSupplier",
            "DoubleToIntFunction",
            "DoubleToLongFunction",
            "DoubleUnaryOperator",
            "Function",
            "IntBinaryOperator",
            "IntConsumer",
            "IntFunction",
            "IntPredicate",
            "IntSupplier",
            "IntToDoubleFunction",
            "IntToLongFunction",
            "IntUnaryOperator",
            "LongBinaryOperator",
            "LongConsumer",
            "LongFunction",
            "LongPredicate",
            "LongSupplier",
            "LongToDoubleFunction",
            "LongToIntFunction",
            "LongUnaryOperator",
            "ObjDoubleConsumer",
            "ObjIntConsumer",
            "ObjLongConsumer",
            "Predicate",
            "Supplier",
            "ToDoubleBiFunction",
            "ToDoubleFunction",
            "ToIntBiFunction",
            "ToIntFunction",
            "ToLongBiFunction",
            "ToLongFunction",
            "UnaryOperator"
    ).stream().map("import net.openhft.function."::concat).collect(toList());
}
