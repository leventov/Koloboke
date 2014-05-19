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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;


public final class Dimensions {

    private static final String OPTIONS = "([a-z0-9]+)((\\|[a-z0-9]+)+)?";

    static final String DIMENSION = format("(?<options>%s)\\s+(?<dim>[a-z]+)", OPTIONS);

    private static final Pattern DIMENSION_P = RegexpUtils.compile(DIMENSION);

    private static final Pattern PRIM_TITLE_P =
            Pattern.compile("Double|Float|Int|Long|Byte|Short|Char");

    public static class Parser {
        private final List<Option> defaultTypes;
        private final ObjectType.IdentifierStyle objectIdStyle;

        Parser(List<Option> defaultTypes, ObjectType.IdentifierStyle objectIdStyle) {
            this.defaultTypes = defaultTypes;
            this.objectIdStyle = objectIdStyle;
        }

        Dimensions parseClassName(String className) {
            Matcher typeMatcher = PRIM_TITLE_P.matcher(className);
            List<String> types = new ArrayList<>();
            while (typeMatcher.find()) {
                String part = typeMatcher.group();
                if (!types.contains(part))
                    types.add( part );
            }
            // dimension names are conventional generic variable names in java
            LinkedHashMap<String, List<Option>> dimensions = new LinkedHashMap<>();
            if (types.size() >= 1)
                addClassNameDim(dimensions, "t", parseOption(types.get(0)));
            if (types.size() >= 2)
                addClassNameDim(dimensions, "u", parseOption(types.get(1)));
            if (types.size() >= 3)
                addClassNameDim(dimensions, "v", parseOption(types.get(2)));
            return new Dimensions(dimensions);
        }

        /**
         * @param descriptor in format "opt1|opt2 dim1 opt3|opt4 dim2"
         */
        public Dimensions parse(String descriptor) {
            Matcher m = DIMENSION_P.matcher(descriptor);
            LinkedHashMap<String, List<Option>> dimensions = new LinkedHashMap<>();
            while (m.find()) {
                String dim = m.group("dim");
                List<Option> opts = parseOptions(m.group("options"));
                dimensions.put(dim, opts);
            }
            return new Dimensions(dimensions);
        }

        /**
         * @param descriptor in format "dim1=opt1|opt2,dim2=opt3|opt4"
         */
        Dimensions parseCLI(String descriptor) {
            String[] dimDescriptors = descriptor.split(",");
            LinkedHashMap<String, List<Option>> dimensions = new LinkedHashMap<>();
            for (String dimDescriptor : dimDescriptors) {
                String[] parts = dimDescriptor.split("=");
                dimensions.put(parts[0], parseOptions(parts[1]));
            }
            return new Dimensions(dimensions);
        }

        private void addClassNameDim(LinkedHashMap<String,
                List<Option>> dimensions, String dim, Option main) {
            List<Option> keyOptions = new ArrayList<>();
            keyOptions.add(main);
            for (Option type : defaultTypes) {
                if (!keyOptions.contains(type))
                    keyOptions.add(type);
            }
            dimensions.put(dim, keyOptions);
        }

        List<Option> parseOptions(String options) {
            return parseOptions(options, objectIdStyle);
        }

        static List<Option> parseOptions(String options, ObjectType.IdentifierStyle objectIdStyle) {
            String[] opts = options.split("\\|");
            List<Option> result = new ArrayList<>();
            for (String option : opts) {
                result.add(parseOption(option, objectIdStyle));
            }
            return result;
        }

        private Option parseOption(String opt) {
            return parseOption(opt, objectIdStyle);
        }

        private static Option parseOption(String opt, ObjectType.IdentifierStyle objectIdStyle) {
            switch (opt.toUpperCase()) {
                case "INT": return PrimitiveType.INT;
                case "LONG": return PrimitiveType.LONG;
                case "FLOAT": return PrimitiveType.FLOAT;
                case "DOUBLE": return PrimitiveType.DOUBLE;
                case "OBJ": case "OBJECT": return ObjectType.get(objectIdStyle);
                case "BYTE": return PrimitiveType.BYTE;
                case "CHAR": return PrimitiveType.CHAR;
                case "SHORT": return PrimitiveType.SHORT;
                default: return new SimpleOption(opt);
            }
        }
    }


    private LinkedHashMap<String, List<Option>> dimensions;

    private Dimensions(LinkedHashMap<String, List<Option>> dimensions) {
        this.dimensions = dimensions;
    }

    public List<Context> generateContexts() {
        int totalCombinations = 1;
        for ( List<?> options : dimensions.values() ) {
            totalCombinations *= options.size();
        }
        List<Context> contexts = new ArrayList<>(totalCombinations);
        for (int comb = 0; comb < totalCombinations; comb++) {
            Context.Builder cb = Context.builder();
            int combRem = comb;
            for (Map.Entry<String, List<Option>> e : dimensions.entrySet()) {
                List<Option> options = e.getValue();
                int index = combRem % options.size();
                combRem /= options.size();
                Option option = options.get(index);
                cb.put(e.getKey(), option);
            }
            contexts.add(cb.makeContext());
        }
        return contexts;
    }

    boolean checkAsCondition(Context context) {
        for (Map.Entry<String, List<Option>> e : dimensions.entrySet()) {
            String dim = e.getKey();
            Option contextOption = context.getOption(dim);
            List<Option> conditionOptions = e.getValue();
            if (!conditionOptions.contains(contextOption))
                return false;
        }
        return true;
    }


    @Override
    public String toString() {
        return dimensions.toString();
    }
}
