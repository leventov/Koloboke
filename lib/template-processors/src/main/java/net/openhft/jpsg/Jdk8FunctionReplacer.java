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
                template = template.replace(jdk8Interface, "java.util.function." +
                        jdk8Interface.substring("net.openhft.function.".length()));
            }
            sb.setLength(0);
            sb.append(template);
        } else {
            postProcess(sb, source, target, template);
        }
    }

    private static final List<String> JDK8_INTERFACES = Arrays.asList(
            "net.openhft.function.BiConsumer",
            "net.openhft.function.BiFunction",
            "net.openhft.function.BinaryOperator",
            "net.openhft.function.BiPredicate",
            "net.openhft.function.BooleanSupplier",
            "net.openhft.function.Consumer",
            "net.openhft.function.DoubleBinaryOperator",
            "net.openhft.function.DoubleConsumer",
            "net.openhft.function.DoubleFunction",
            "net.openhft.function.DoublePredicate",
            "net.openhft.function.DoubleSupplier",
            "net.openhft.function.DoubleToIntFunction",
            "net.openhft.function.DoubleToLongFunction",
            "net.openhft.function.DoubleUnaryOperator",
            "net.openhft.function.Function",
            "net.openhft.function.IntBinaryOperator",
            "net.openhft.function.IntConsumer",
            "net.openhft.function.IntFunction",
            "net.openhft.function.IntPredicate",
            "net.openhft.function.IntSupplier",
            "net.openhft.function.IntToDoubleFunction",
            "net.openhft.function.IntToLongFunction",
            "net.openhft.function.IntUnaryOperator",
            "net.openhft.function.LongBinaryOperator",
            "net.openhft.function.LongConsumer",
            "net.openhft.function.LongFunction",
            "net.openhft.function.LongPredicate",
            "net.openhft.function.LongSupplier",
            "net.openhft.function.LongToDoubleFunction",
            "net.openhft.function.LongToIntFunction",
            "net.openhft.function.LongUnaryOperator",
            "net.openhft.function.ObjDoubleConsumer",
            "net.openhft.function.ObjIntConsumer",
            "net.openhft.function.ObjLongConsumer",
            "net.openhft.function.Predicate",
            "net.openhft.function.Supplier",
            "net.openhft.function.ToDoubleBiFunction",
            "net.openhft.function.ToDoubleFunction",
            "net.openhft.function.ToIntBiFunction",
            "net.openhft.function.ToIntFunction",
            "net.openhft.function.ToLongBiFunction",
            "net.openhft.function.ToLongFunction",
            "net.openhft.function.UnaryOperator"
    );

    private static final int MAX_LEN =
            JDK8_INTERFACES.stream().mapToInt(String::length).max().getAsInt();
}
