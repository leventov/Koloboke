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


public final class DefinitionProcessor extends TemplateProcessor {
    public static final int PRIORITY = Generator.BLOCKS_PROCESSOR_PRIORITY + 100;

    private static final String DEF_PREFIX = "/[\\*/]\\s*define";
    private static final CheckingPattern DEF_P = CheckingPattern.compile(DEF_PREFIX,
            DEF_PREFIX + "\\s+(?<name>[a-z_0-9]+)\\s*[\\*/]/" +
            "(?<body>.+?)" +
            "/[\\*/]\\s*enddefine\\s*[\\*/]/"
    );

    @Override
    protected int priority() {
        return PRIORITY;
    }

    @Override
    protected void process(StringBuilder builder, Context source, Context target, String template) {
        CheckingMatcher matcher = DEF_P.matcher(template);
        StringBuilder sb = new StringBuilder();
        Map<String, String> definitions = new HashMap<String, String>();
        while(matcher.find()) {
            definitions.put(matcher.group("name"), matcher.group("body").trim());
            matcher.appendSimpleReplacement(sb, "");
        }
        matcher.appendTail(sb);
        postProcess(builder, source, target, replaceDefinitions(definitions, sb.toString()));
    }

    private static String replaceDefinitions(Map<String, String> definitions, String template) {
        for (Map.Entry<String, String> e : definitions.entrySet()) {
            String name = e.getKey();
            String defPrefix = "/[\\*/]\\s*" + name + "\\s*[\\*/]";
            CheckingPattern defPattern = CheckingPattern.compile(defPrefix,
                    defPrefix + "/([^/]+?/[\\*/][\\*/]/)?+", 0);
            String body = e.getValue();
            StringBuilder sb = new StringBuilder();
            CheckingMatcher m = defPattern.matcher(template);
            Map<String, String> withoutCurrentDef = new HashMap<String, String>(definitions);
            withoutCurrentDef.remove(name);
            while (m.find()) {
                m.appendSimpleReplacement(sb, replaceDefinitions(withoutCurrentDef, body));
            }
            m.appendTail(sb);
            template = sb.toString();
        }
        return template;
    }
}
