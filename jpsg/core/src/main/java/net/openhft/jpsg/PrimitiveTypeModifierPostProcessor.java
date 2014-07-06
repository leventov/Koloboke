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
import java.util.function.Predicate;
import java.util.function.UnaryOperator;


public class PrimitiveTypeModifierPostProcessor extends TemplateProcessor {
    public static final int PRIORITY = OptionProcessor.PRIORITY - 10;

    private final String keyword;
    private final UnaryOperator<PrimitiveType> typeMapper;
    private final Predicate<String> dimFilter;

    public PrimitiveTypeModifierPostProcessor(String keyword,
            UnaryOperator<PrimitiveType> typeMapper, Predicate<String> dimFilter) {
        this.keyword = keyword;
        this.typeMapper = typeMapper;
        this.dimFilter = dimFilter;
    }

    @Override
    protected int priority() {
        return PRIORITY;
    }

    @Override
    protected void process(StringBuilder sb, Context source, Context target, String template) {
        for (Map.Entry<String, Option> e : target) {
            String dim = e.getKey();
            if (!dimFilter.test(dim))
                continue;
            Option targetT = e.getValue();
            if (targetT instanceof PrimitiveType || targetT instanceof ObjectType) {
                String kwDim = dim + "." + keyword;
                Option mapped = targetT instanceof PrimitiveType ?
                        typeMapper.apply((PrimitiveType) targetT) :
                        targetT; // ObjectType maps to itself
                template = mapped.finalReplace(template, kwDim);
            }
        }
        postProcess(sb, source, target, template);
    }
}
