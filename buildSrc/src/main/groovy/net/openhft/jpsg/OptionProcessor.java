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

import java.util.*;


public final class OptionProcessor extends TemplateProcessor {
    public static final int PRIORITY = DEFAULT_PRIORITY;

    private static final String RAW = "/[\\*/]\\s*raw\\s*[\\*/]/";
    private static final String BITS = "/[\\*/]\\s*bits\\s*[\\*/]/";

    private static String prefixPattern(String prefix, String primitive) {
        return prefix + primitive + "(?![A-Za-z0-9_$#])";
    }

    private static String processRaw(Context source, Context target, String template) {
        for (Map.Entry<String, Option> e : source) {
            String dim = e.getKey();
            if (e.getValue() instanceof PrimitiveType &&
                    target.getOption(dim) instanceof ObjectType) {
                PrimitiveType sourceT = (PrimitiveType) e.getValue();
                String rawP = prefixPattern(RAW,
                        "(" + sourceT.className + "|" + sourceT.standalone + ")");
                template = template.replaceAll(rawP, "Object");
            }
        }
        return template.replaceAll(RAW, "");
    }

    private static String processBits(Context source, Context target, String template) {
        for (Map.Entry<String, Option> e : source) {
            if (e.getValue() instanceof PrimitiveType) {
                String dim = e.getKey();
                Option targetT = target.getOption(dim);
                if (targetT == PrimitiveType.FLOAT || targetT == PrimitiveType.DOUBLE) {
                    PrimitiveType sourceT = (PrimitiveType) e.getValue();
                    String bitsP = prefixPattern(BITS, sourceT.standalone);
                    // will be replaced with bits type during regular final replace
                    // can't replace immediately, because if source option is long,
                    // generated double -- long bits will be replaced with double back during
                    // regular replace
                    template = template.replaceAll(bitsP,
                            new IntermediateOption(dim + ".bits").standalone);
                }
            }
        }
        return template.replaceAll(BITS, "");
    }

    @Override
    protected int priority() {
        return PRIORITY;
    }

    @Override
    protected void process(Context source, Context target, String template) {
        template = processRaw(source, target, template);
        template = processBits(source, target, template);

        for (Map.Entry<String, Option> e : source) {
            String dim = e.getKey();
            Option option = e.getValue();
            template = option.intermediateReplace(template, dim);
        }
        List<String> dims = new ArrayList<>();
        for (Map.Entry<String, Option> e : target) {
            dims.add(e.getKey());
        }
        for (int i = dims.size(); i-- > 0;) {
            String dim = dims.get(i);
            Option option = target.getOption(dim);
            template = option.finalReplace(template, dim);
        }
        postProcess(source, target, template);
    }
}
