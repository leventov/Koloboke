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
import java.util.regex.Pattern;

import static java.lang.String.format;


public final class IntermediateOption {

    private static final Map<String, IntermediateOption> cache = new HashMap<>();

    public static IntermediateOption of(String dim) {
        IntermediateOption opt;
        if ((opt = cache.get(dim)) == null) {
            cache.put(dim, opt = new IntermediateOption(dim));
        }
        return opt;
    }

    /**
     * If some option value clashes with any of {className, standalone, lower, title, upper}
     * keywords or any dim name, errors occur. Replace with same-looking characters to preserve
     * readability for debug.
     */
    private static String cyrillicLetters(String s) {
        return s.replace('o', 'о').replace('e', 'е').replace('a', 'а').replace('i', 'і')
                .replace('y', 'у').replace('c', 'с').replace('p', 'р').replace('x', 'х')
                .replace('k', 'к').replace('r', 'г');
    }

    final String className;
    final Pattern classNameP;

    final String standalone;
    final Pattern standaloneP;

    final String lower;
    final Pattern lowerP;

    final String title;
    final Pattern titleP;

    final String upper;
    final Pattern upperP;

    private IntermediateOption(String dim) {
        className = cyrillicLetters(format("#%s.className#", dim));
        classNameP = Pattern.compile(Pattern.quote(className));

        standalone = cyrillicLetters(format("#%s.standalone#", dim));
        standaloneP = Pattern.compile(Pattern.quote(standalone));

        lower = cyrillicLetters(format("#%s.lower#", dim));
        lowerP = Pattern.compile(Pattern.quote(lower));

        title = cyrillicLetters(format("#%s.title#", dim));
        titleP = Pattern.compile(Pattern.quote(title));

        upper = cyrillicLetters(format("#%s.upper#", dim));
        upperP = Pattern.compile(Pattern.quote(upper));
    }
}
