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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Subclasses must have public no-arg constructor.
 */
public abstract class TemplateProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(TemplateProcessor.class);
    public static final int DEFAULT_PRIORITY = 0;

    private Dimensions.Parser dimensionsParser;
    private TemplateProcessor next = null;

    /**
     * @param source template context
     * @param target context to generate code for
     * @param template source template
     */
    protected abstract void process(StringBuilder sb,
            Context source, Context target, String template);

    protected int priority() {
        return DEFAULT_PRIORITY;
    }

    protected final Dimensions.Parser getDimensionsParser() {
        return dimensionsParser;
    }

    protected final void postProcess(StringBuilder sb,
            Context source, Context target, String template) {
        if (next != null) {
            next.process(sb, source, target, template);
        } else {
            sb.append(template);
        }
        // sb.append(next != null ? next.generate(source, target, template) : template);
    }

    final String generate(Context source, Context target, String template) {
        StringBuilder sb = new StringBuilder();
        process(sb, source, target, template);
        return sb.toString();
    }


    final void setDimensionsParser(Dimensions.Parser parser) {
        dimensionsParser = parser;
    }

    final void setNext(TemplateProcessor next) {
        this.next = next;
    }
}
