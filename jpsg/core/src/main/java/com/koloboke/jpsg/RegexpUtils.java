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

import java.util.regex.Pattern;

import static java.util.regex.Pattern.*;


public final class RegexpUtils {

    public static final String JAVA_ID_OR_CONST = "[a-zA-Z\\d_$]++(\\.[a-zA-Z\\d_$]++)?";

    public static final int STANDARD_TEMPLATE_FLAGS = CASE_INSENSITIVE | DOTALL | MULTILINE;

    public static Pattern compile(String regex) {
        return Pattern.compile(regex, STANDARD_TEMPLATE_FLAGS);
    }

    public static String removeSubGroupNames(String regex) {
        return regex.replaceAll("\\?<[a-z]+?>", "");
    }


    private RegexpUtils() {}
}
