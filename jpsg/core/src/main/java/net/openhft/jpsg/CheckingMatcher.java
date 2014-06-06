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

import java.util.regex.*;


public final class CheckingMatcher implements MatchResult {

    static CheckingMatcher create(CharSequence input,
            Pattern checkingPattern, Pattern targetPattern) {
        return new CheckingMatcher(input,
                checkingPattern.matcher(input), targetPattern.matcher(input));
    }

    private final CharSequence input;
    private final Matcher checkingMatcher, targetMatcher;
    private int lastAppendPosition = 0;

    private CheckingMatcher(CharSequence input, Matcher checkingMatcher, Matcher targetMatcher) {
        this.input = input;
        this.checkingMatcher = checkingMatcher;
        this.targetMatcher = targetMatcher;
    }

    public boolean find() {
        if (!checkingMatcher.find())
            return false;
        int start = checkingMatcher.start();
        if (!targetMatcher.find(start) || targetMatcher.start() != start) {
            throw MalformedTemplateException.near(input, start);
        }
        return true;
    }

    @Override
    public int start() {
        return targetMatcher.start();
    }

    @Override
    public int start(int group) {
        return targetMatcher.start(group);
    }

    @Override
    public int end() {
        return targetMatcher.end();
    }

    @Override
    public int end(int group) {
        return targetMatcher.end(group);
    }

    @Override
    public String group() {
        return targetMatcher.group();
    }

    @Override
    public String group(int group) {
        return targetMatcher.group(group);
    }

    public String group(String name) {
        return targetMatcher.group(name);
    }

    @Override
    public int groupCount() {
        return targetMatcher.groupCount();
    }

    public CheckingMatcher appendSimpleReplacement(StringBuilder sb, String replacement) {
        sb.append(input, lastAppendPosition, targetMatcher.start());
        sb.append(replacement);
        lastAppendPosition = targetMatcher.end();
        return this;
    }

    public StringBuilder appendTail(StringBuilder sb) {
        return sb.append(input, lastAppendPosition, input.length());
    }

    public boolean matches() {
        return targetMatcher.matches();
    }

    public CheckingMatcher region(int start, int end) {
        lastAppendPosition = 0;
        checkingMatcher.region(start, end);
        targetMatcher.region(start, end);
        return this;
    }
}
