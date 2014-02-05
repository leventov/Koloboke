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

package net.openhft.collect;

import com.google.common.collect.testing.SampleElements;

import java.util.Arrays;
import java.util.List;


public final class CharSamples {

    public static List<SampleElements<? extends Character>> allKeys() {
        return all();
    }

    public static List<SampleElements<? extends Character>> allValues() {
        return all();
    }

    private static List<SampleElements<? extends Character>> all() {
        // noinspection unchecked
        return (List) Arrays.asList(
                /* if char|int t */
                new SampleElements.Chars(),
                /* endif */
                /* if char t //
                new SampleElements<Character>('a', 'b', 'c', 'd', (char) 0)
                // elif !(char elem) */
                new SampleElements<Character>(
                        /* const t 0 */(char) 0/* endconst */,
                        /* const t min */Character.MIN_VALUE/* endconst*/,
                        /* const t max */Character.MAX_VALUE/* endconst*/,
                        /* const t 1 */(char) 1/* endconst */,
                        /* const t -2 */(char) -2/* endconst */
                )
                /* endif */
                /* if float|double t */,
                new SampleElements<Character>(
                                /* const t 0 */(char) 0/* endconst */,
                                /* if float t */Float/* elif double t //Double// endif */.NaN,
                                Character.MAX_VALUE,
                                /* const t 1 */(char) 1/* endconst */,
                                /* const t -2 */(char) -2/* endconst */
                )
                /* endif */
        );
    }

    private CharSamples() {}
}
