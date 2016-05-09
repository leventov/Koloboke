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

package com.koloboke.jpsg;

import static com.koloboke.jpsg.RegexpUtils.JAVA_ID_OR_CONST;


public final class ConstProcessor extends TemplateProcessor {
    // after option processor because constant "(char) 0" is generated
    public static final int PRIORITY = OptionProcessor.PRIORITY - 10;

    private static String PREFIX = "/[\\*/]\\s*const\\s+(?<dim>[a-zA-Z]+)";
    private static CheckingPattern CONST_PATTERN = CheckingPattern.compile(PREFIX,
            PREFIX + "\\s+(?<value>-?\\d+|min|max)\\s*[\\*/]/" +
            "([^/]*?/[\\*/]\\s*endconst\\s*[\\*/]/|" +
            "\\s*+" + JAVA_ID_OR_CONST + ")");

    private static String replaceValue(String value, Option option) {
        PrimitiveType type = ( PrimitiveType ) option;
        if (value.equalsIgnoreCase("min")) return type.minValue();
        if (value.equalsIgnoreCase("max")) return type.maxValue();
        return type.formatValue(value);
    }

    @Override
    protected void process(StringBuilder builder, Context source, Context target, String template) {
        CheckingMatcher valueM = CONST_PATTERN.matcher(template);
        StringBuilder sb = new StringBuilder();
        while (valueM.find()) {
            String dim = valueM.group("dim");
            Option option = target.getOption(dim);
            if (option != null) {
                valueM.appendSimpleReplacement(sb, replaceValue(valueM.group("value"), option));
            } else {
                throw MalformedTemplateException.near(template, valueM.start(),
                        "Nonexistent dimension: " + dim + ", available dims: " + target);
            }
        }
        valueM.appendTail(sb);
        template = sb.toString();
        postProcess(builder, source, target, template);
    }

    @Override
    protected int priority() {
        return PRIORITY;
    }
}
