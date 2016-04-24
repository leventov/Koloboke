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
import java.util.regex.Pattern;


public final class GenericsProcessor extends TemplateProcessor {

    private static List<Map.Entry<String, Option>> getViewOptions(Context context) {
        List<Map.Entry<String, Option>> options = new ArrayList<Map.Entry<String, Option>>();
        for (Map.Entry<String, Option> e : context) {
            options.add(e);
        }
        Option view = context.getOption("view");
        if (view != null && view instanceof SimpleOption) {
            String viewDim = ((SimpleOption) view).title.toLowerCase();
            Option viewOption = context.getOption(viewDim);
            if (viewOption != null) {
                options = Arrays.<Map.Entry<String, Option>>asList(
                        new AbstractMap.SimpleImmutableEntry<String, Option>(viewDim, viewOption));
            }
        }
        return options;
    }

    private static final Pattern SIMPLE_P = RegexpUtils.compile("/[\\*/]<>[\\*/]/");
    private static final Pattern SUPER_P = RegexpUtils.compile("/[\\*/]<super>[\\*/]/");
    private static final Pattern EXTENDS_P =
            RegexpUtils.compile("/[\\*/]<extends>[\\*/]/");

    private static final Pattern UNBOUND_P = RegexpUtils.compile("/[\\*/]<\\?>[\\*/]/");

    @Override
    protected void process(StringBuilder sb, Context source, Context target, String template) {
        List<Map.Entry<String, Option>> targetOptions = getViewOptions(target);

        String simpleG = "", superG = "", extendsG = "", unboundG = "";
        for (Map.Entry<String, Option> e : targetOptions) {
            if (e.getValue() instanceof ObjectType) {
                String genericParam = e.getKey().substring(0, 1).toUpperCase();
                simpleG += ", " + genericParam;
                superG += ", ? super " + genericParam;
                extendsG += ", ? extends " + genericParam;
                unboundG += ", ?";
            }
        }
        if (!simpleG.isEmpty()) {
            simpleG = "<" + simpleG.substring(2) + ">";
            superG = "<" + superG.substring(2) + ">";
            extendsG = "<" + extendsG.substring(2) + ">";
            unboundG = "<" + unboundG.substring(2) + ">";
        }
        // replace with generics or remove templates
        template = SIMPLE_P.matcher(template).replaceAll(simpleG);
        template = SUPER_P.matcher(template).replaceAll(superG);
        template = EXTENDS_P.matcher(template).replaceAll(extendsG);
        template = UNBOUND_P.matcher(template).replaceAll(unboundG);
        postProcess(sb, source, target, template);
    }
}
