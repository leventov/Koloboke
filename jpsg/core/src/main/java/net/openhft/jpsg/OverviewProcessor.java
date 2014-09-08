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

import java.io.IOException;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.joining;
import static net.openhft.jpsg.MalformedTemplateException.lines;


public final class OverviewProcessor extends TemplateProcessor {
    public static final int PRIORITY = DEFAULT_PRIORITY + 10000;

    private static final CheckingPattern OVERVIEW_PATTERN =CheckingPattern.compile(
            "//\\s+overview\\s+//", "//\\s+overview\\s+//(.*)//\\s+endOverview\\s+//");
    private static final Pattern JAVADOC_ASTERISKS = Pattern.compile("^\\s*\\*");

    @Override
    protected int priority() {
        return PRIORITY;
    }

    @Override
    protected void process(StringBuilder sb, Context source, Context target, String template) {
        CheckingMatcher m;
        if (!Generator.currentSourceFile().endsWith("package-info.java") ||
                !(m = OVERVIEW_PATTERN.matcher(template)).find()) {
            postProcess(sb, source, target, template);
        } else {
            String withoutOverview = template.substring(0, m.start()) + template.substring(m.end());
            postProcess(sb, source, target, withoutOverview);

            String overview = m.group(1);
            overview = lines(overview).stream()
                    .map(line -> JAVADOC_ASTERISKS.matcher(line).replaceFirst(""))
                    .collect(joining("\n", "<html><body>", "</body></html>"));
            Generator gen = Generator.currentGenerator();
            overview = gen.generate(source, target, overview);
            try {
                gen.writeFile(gen.getTarget().toPath().resolve("overview.html"), overview);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
