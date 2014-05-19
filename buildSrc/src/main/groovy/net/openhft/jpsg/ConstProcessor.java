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

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public final class ConstProcessor extends TemplateProcessor {
    // after option processor because constant "(char) 0" is generated
    public static final int PRIORITY = OptionProcessor.PRIORITY - 1;

    private static Pattern valuePattern(String dim) {
        String pattern =
                "/[\\*/]\\s*const\\s+" + dim + "\\s+(?<value>-?\\d+|min|max)\\s*[\\*/]/" +
                        "([^/]*?/[\\*/] endconst [\\*/]/|" +
                        "\\s*+" + RegexpUtils.JAVA_ID_OR_CONST + ")";
        return RegexpUtils.compile(pattern);
    }

    private static String replaceValue(String value, Option option) {
        PrimitiveType type = ( PrimitiveType ) option;
        if (value.equalsIgnoreCase("min")) return type.minValue();
        if (value.equalsIgnoreCase("max")) return type.maxValue();
        return type.formatValue(value);
    }

    @Override
    protected void process(StringBuilder builder, Context source, Context target, String template) {
        for (Map.Entry<String, Option> e : target) {
            String dim = e.getKey();
            Option option = e.getValue();

            Matcher valueM = valuePattern(dim).matcher(template);
            StringBuffer sb = new StringBuffer();
            while (valueM.find()) {
                valueM.appendReplacement(sb,
                        Matcher.quoteReplacement(replaceValue(valueM.group("value"), option)));
            }
            valueM.appendTail(sb);
            template = sb.toString();
        }
        postProcess(builder, source, target, template);
    }

    @Override
    protected int priority() {
        return PRIORITY;
    }
}
