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

import kotlin.io.FilesKt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.List;


public final class Jdk8FunctionReplacer extends TemplateProcessor {
    private static final Logger log = LoggerFactory.getLogger(Jdk8FunctionReplacer.class);
    public static final int PRIORITY = DEFAULT_PRIORITY - 10000;

    @Override
    public int priority() {
        return PRIORITY;
    }

    private static final SimpleOption JDK8 = new SimpleOption("JDK8");

    @Override
    protected void process(StringBuilder sb, Context source, Context target, String template) {
        File currentSourceFile = Generator.Companion.currentSourceFile();
        boolean packageInfo = FilesKt.endsWith(currentSourceFile, new File("package-info.java")) ||
                currentSourceFile.getName().endsWith("html");
        if (JDK8.equals(target.getOption("jdk")) &&
                // Heuristic that we are at the end of the template class
                (template.trim().endsWith("}") || packageInfo)) {
            template = sb.toString() + template;
            // in ordinary Java source files, we replace only imports, to let fully-qualified refs
            // in the code to remain unchanged. In package descriptions, there are no imports,
            // so we can only replace all fully-qualified names.
            String prefix = packageInfo ? "" : "import ";
            for (String jdk8Interface : JDK8_INTERFACES) {
                template = template.replace(
                        prefix + "net.openhft.koloboke.function." + jdk8Interface,
                        prefix + "java.util.function." + jdk8Interface
                );
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
    );
}
