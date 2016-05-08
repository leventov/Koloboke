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

import java.util.*;


public final class Context implements Iterable<Map.Entry<String, Option>> {

    static Builder builder() {
        return new Builder();
    }

    static class Builder {
        private final LinkedHashMap<String, Option> options = new LinkedHashMap<String, Option>();

        Builder put(String dim, Option option) {
            options.put(dim, option);
            return this;
        }

        Context makeContext() {
            // noinspection unchecked
            return new Context(new LinkedHashMap<String, Option>(options));
        }
    }


    private final LinkedHashMap<String, Option> options;

    private Context(LinkedHashMap<String, Option> options) {
        this.options = options;
    }

    @Override
    public Iterator<Map.Entry<String, Option>> iterator() {
        return Collections.unmodifiableMap(options).entrySet().iterator();
    }

    public Context join(Context additionalContext) {
        // noinspection unchecked
        LinkedHashMap<String, Option> newOptions = new LinkedHashMap<String, Option>(options);
        for ( Map.Entry<String, Option> e : additionalContext.options.entrySet() ) {
            newOptions.put(e.getKey(), e.getValue());
        }
        return new Context(newOptions);
    }

    public Option getOption(String dim) {
        return options.get(dim);
    }


    @Override
    public String toString() {
        return options.toString();
    }
}
