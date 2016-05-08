/* with char|byte|short|int|long key */
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

package com.koloboke.collect.research.hash;

import java.util.concurrent.ThreadLocalRandom;


public final class CharOps {

    static char randomExcept(char... specials) {
        search:
        while (true) {
            char c = (char) ThreadLocalRandom.current().nextLong();
            for (char special : specials) {
                if (special == c)
                    continue search;
            }
            return c;
        }
    }

    private CharOps() {}
}
