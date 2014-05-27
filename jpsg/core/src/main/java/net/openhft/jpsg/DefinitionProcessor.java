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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public final class DefinitionProcessor extends TemplateProcessor {
    public static final int PRIORITY = Generator.BlocksProcessor.PRIORITY + 10;

    private static final Pattern DEF_P = RegexpUtils.compile(
            "/[\\*/]\\s*define\\s+(?<name>[a-z_0-9]+)\\s*[\\*/]/" +
            "(?<body>.+?)" +
            "/[\\*/]\\s*enddefine\\s*[\\*/]/"
    );

    @Override
    protected int priority() {
        return PRIORITY;
    }

    @Override
    protected void process(StringBuilder builder, Context source, Context target, String template) {
        Matcher matcher = DEF_P.matcher(template);
        StringBuffer sb = new StringBuffer();
        Map<String, String> definitions = new HashMap<>();
        while(matcher.find()) {
            definitions.put(matcher.group("name"), matcher.group("body").trim());
            matcher.appendReplacement(sb, "");
        }
        matcher.appendTail(sb);
        postProcess(builder, source, target, replaceDefinitions(definitions, sb.toString()));
    }

    private static String replaceDefinitions(Map<String, String> definitions, String template) {
        for (Map.Entry<String, String> e : definitions.entrySet()) {
            String name = e.getKey();
            Pattern defPattern = Pattern.compile(
                    "/[\\*/]\\s*" + name + "\\s*[\\*/]/([^/]+?/[\\*/][\\*/]/)?+");
            String body = e.getValue();
            StringBuffer sb = new StringBuffer();
            Matcher m = defPattern.matcher(template);
            Map<String, String> withoutCurrentDef = new HashMap<>(definitions);
            withoutCurrentDef.remove(name);
            while (m.find()) {
                m.appendReplacement(sb,
                        Matcher.quoteReplacement(replaceDefinitions(withoutCurrentDef, body)));
            }
            m.appendTail(sb);
            template = sb.toString();
        }
        return template;
    }
}
