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

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public final class SimpleOption implements Option {

    private static boolean isContextOption(String title) {
        boolean hasLow = false, hasUp = false;
        for (int i = 0; i < title.length(); i++) {
        	if (Character.isLowerCase(title.charAt(i))) {
                hasLow = true;
            }
            if (Character.isUpperCase(title.charAt(i))) {
                hasUp = true;
            }
        }
        return !hasLow || !hasUp;
    }
    // for options used only to enrich context, not for replacing
    final boolean contextOption;
    final String title;
    Pattern titleP;

    String lower;
    Pattern lowerP;

    String upper;
    Pattern upperP;

    public SimpleOption(String title) {
        this.title = title;
        if (contextOption = isContextOption(title)) {
            return;
        }
        titleP = Pattern.compile(title + "(?![a-rt-z].|s[a-z])");

        lower = title.substring(0, 1).toLowerCase() + title.substring(1);
        lowerP = Pattern.compile("(?<![A-Za-z])" + lower + "(?![a-rt-z].|s[a-z])");

        Matcher m = Pattern.compile("[A-Z]([a-z0-9]*+)").matcher(title);
        String upper = "";
        int parts = 0;
        while (m.find()) {
            upper += (parts == 0 ? "" : "_") + m.group().toUpperCase();
            parts++;
        }
        if (upper.length() != title.length() + parts - 1) {
            throw new IllegalArgumentException();
        }
        this.upper = upper;
        upperP = Pattern.compile("(?<![A-Z])" + upper + "(?![A-RT-Z].|S[A-Z])");
    }


    @Override
    public String intermediateReplace(String content, String dim) {
        if (contextOption)
            return content;
        IntermediateOption intermediate = IntermediateOption.of(dim);
        content = lowerP.matcher(content).replaceAll(intermediate.lower);
        content = titleP.matcher(content).replaceAll(intermediate.title);
        content = upperP.matcher(content).replaceAll(intermediate.upper);
        return content;
    }


    @Override
    public String finalReplace(String content, String dim) {
        if (contextOption)
            return content;
        IntermediateOption intermediate = IntermediateOption.of(dim);
        content = intermediate.lowerP.matcher(content).replaceAll(lower);
        content = intermediate.titleP.matcher(content).replaceAll(title);
        content = intermediate.upperP.matcher(content).replaceAll(upper);
        return content;
    }


    @Override
    public String toString() {
        return title;
    }


    @Override
    public int hashCode() {
        return title.hashCode();
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        return obj instanceof SimpleOption && title.equals(((SimpleOption) obj).title);
    }
}
