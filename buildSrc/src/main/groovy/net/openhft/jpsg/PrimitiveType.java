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

import java.util.regex.Pattern;

import static java.lang.String.format;


public enum PrimitiveType implements Option {

    BYTE ("byte", "Byte"),
    CHAR ("char", "Character"),
    SHORT ("short", "Short"),

    INT ("int", "Integer") {
        @Override
        public String formatValue(String value) {
            return value;
        }
    },

    LONG ("long", "Long") {
        @Override
        public String formatValue(String value) {
            return value + "L";
        }
    },

    FLOAT ("float", "Float") {

        @Override
        public String intermediateReplace(String content, String dim) {
            content = super.intermediateReplace(content, dim);
            content = INT.intermediateReplace(content, dim + ".bits");
            return content;
        }

        @Override
        public String finalReplace(String content, String dim) {
            content = INT.finalReplace(content, dim + ".bits");
            content = super.finalReplace(content, dim);
            return content;
        }

        @Override
        public String formatValue(String value) {
            return value + ".0f";
        }

        @Override
        String minValue() {
            return "Float.NEGATIVE_INFINITY";
        }

        @Override
        String maxValue() {
            return "Float.POSITIVE_INFINITY";
        }
    },

    DOUBLE ("double", "Double") {

        @Override
        public String intermediateReplace(String content, String dim) {
            content = super.intermediateReplace(content, dim);
            content = LONG.intermediateReplace(content, dim + ".bits");
            return content;
        }


        @Override
        public String finalReplace(String content, String dim) {
            content = LONG.finalReplace(content, dim + ".bits");
            content = super.finalReplace(content, dim);
            return content;
        }

        @Override
        public String formatValue(String value) {
            return value + ".0";
        }

        @Override
        String minValue() {
            return "Double.NEGATIVE_INFINITY";
        }

        @Override
        String maxValue() {
            return "Double.POSITIVE_INFINITY";
        }
    };

    public final String className;
    final Pattern classNameP;

    public final String standalone;
    final Pattern standaloneP;

    public final String lower;
    final Pattern lowerP;

    public final String title;
    final Pattern titleP;

    public final String upper;
    final Pattern upperP;

    private PrimitiveType(String prim, String className) {
        this.className = className;
        classNameP = Pattern.compile("(?<![A-Za-z0-9_$#])" + className + "(?![A-Za-z0-9_$#])");

        standalone = prim;
        standaloneP = Pattern.compile("(?<![A-Za-z0-9_$#])" + prim + "(?![A-Za-z0-9_$#])");

        lower = prim;
        // look around not to replace keyword substring inside words
        // Uppercase ahead is ok -- consider Integer.intValue
        // Special case - plural form (ints, chars)
        lowerP = Pattern.compile(
                "(?<![A-Za-z])" + prim + "(?![a-rt-z].|s[a-z])");

        title = prim.substring(0, 1).toUpperCase() + prim.substring(1);
        // lookahead not to replace Int in ex. doInterrupt
        // special case - plural form: addAllInts
        titleP = Pattern.compile("\\$?" + title + "(?![a-rt-z].|s[a-z])");

        upper = prim.toUpperCase();
        // lookahead and lookbehind not to replace INT in ex. INTERLEAVE_CONSTANT
        upperP = Pattern.compile("(?<![A-Z])" + upper + "(?![A-RT-Z].|S[A-Z])");
    }

    String minValue() {
        return className + ".MIN_VALUE";
    }

    String maxValue() {
        return className + ".MAX_VALUE";
    }

    public String formatValue(String value) {
        return format("(%s) %s", standalone, value);
    }


    @Override
    public String intermediateReplace(String content, String dim) {
        IntermediateOption intermediate = new IntermediateOption(dim);
        content = classNameP.matcher(content).replaceAll(intermediate.className);
        content = standaloneP.matcher(content).replaceAll(intermediate.standalone);
        content = lowerP.matcher(content).replaceAll(intermediate.lower);
        content = titleP.matcher(content).replaceAll(intermediate.title);
        content = upperP.matcher(content).replaceAll(intermediate.upper);
        return content;
    }


    @Override
    public String finalReplace(String content, String dim) {
        IntermediateOption intermediate = new IntermediateOption(dim);
        content = intermediate.classNameP.matcher(content).replaceAll(className);
        content = intermediate.standaloneP.matcher(content).replaceAll(standalone);
        content = intermediate.lowerP.matcher(content).replaceAll(lower);
        content = intermediate.titleP.matcher(content).replaceAll(title);
        content = intermediate.upperP.matcher(content).replaceAll(upper);
        return content;
    }


    @Override
    public String toString() {
        return title;
    }
}
