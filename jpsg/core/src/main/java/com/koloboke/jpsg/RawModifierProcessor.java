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

import java.util.Map;


public final class RawModifierProcessor extends TemplateProcessor {
    /**
     * {@code RawModifierProcessor} should run before any {@link PrimitiveTypeModifierPreProcessor}
     */
    public static final int PRIORITY = PrimitiveTypeModifierPreProcessor.getPRIORITY() + 10;

    private static final String RAW = OptionProcessor.modifier("raw");

    @Override
    protected int priority() {
        return PRIORITY;
    }

    @Override
    protected void process(StringBuilder sb, Context source, Context target, String template) {
        for (Map.Entry<String, Option> e : source) {
            String dim = e.getKey();
            if (e.getValue() instanceof PrimitiveType &&
                    target.getOption(dim) instanceof ObjectType) {
                PrimitiveType sourceT = (PrimitiveType) e.getValue();
                String rawP = OptionProcessor.prefixPattern(RAW,
                        "(" + sourceT.className + "|" + sourceT.standalone + ")");
                template = template.replaceAll(rawP, "Object");
            }
        }
        // remove left modifier templates when for example target is primitive type
        template = template.replaceAll(RAW, "");
        postProcess(sb, source, target, template);
    }
}
