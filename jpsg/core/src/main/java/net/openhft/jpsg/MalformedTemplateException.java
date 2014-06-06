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

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.stream.Collectors.toList;


public final class MalformedTemplateException extends RuntimeException {

    public static MalformedTemplateException near(CharSequence input, int pos) {
        return near(input, pos, "Malformed template near");
    }

    public static MalformedTemplateException near(CharSequence input, int pos, String message) {
        return new MalformedTemplateException(makeMessageNear(input, pos, message));
    }

    private static String makeMessageNear(CharSequence input, int pos, String message) {
        StringJoiner joiner = new StringJoiner("");
        joiner.add(message + ":\n");

        String s = input.toString();
        String[] ls = s.split("\\n");
        List<String> lines = Arrays.stream(ls).map(l -> l + '\n').collect(toList());
        int charCount = 0;
        int targetLine = -1;
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (pos < charCount + line.length()) {
                targetLine = i;
                break;
            }
            charCount += line.length();
        }
        int firstLine = max(targetLine - 2, 0);
        lines.subList(firstLine, targetLine + 1).forEach(joiner::add);
        String pointer = new String(new char[pos - charCount]).replace('\0', ' ') + "^\n";
        joiner.add(pointer);
        int lastLine = min(targetLine + 3, lines.size());
        lines.subList(targetLine + 1, lastLine).forEach(joiner::add);
        return joiner.toString();
    }

    private MalformedTemplateException(String message) {
        super(message);
    }
}
