/* with char|byte|short|int|long|float|double|obj elem */
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

package net.openhft.collect.testing;

import com.google.common.collect.testing.*;
import net.openhft.collect.testing.testers.*;

import java.util.ArrayList;
import java.util.List;


public class GuavaCharCollectionTestSuiteBuilder/*<>*/
        extends CollectionTestSuiteBuilder<Character> {

    @Override
    protected List<Class<? extends AbstractTester>> getTesters() {
        List<Class<? extends AbstractTester>> testers = new ArrayList<>(super.getTesters());
        testers.add(CharCollectionCursorTester.class);
        testers.add(CharCollectionForEachTester.class);
        testers.add(CharCollectionRemoveIfTester.class);
        testers.add(CharCollectionRetainAllTester.class);
        testers.add(CharCollectionRemoveAllTester.class);
        testers.add(CharCollectionContainsAllTester.class);
        return testers;
    }

    @Override
    public CollectionTestSuiteBuilder<Character> usingGenerator(
            TestCollectionGenerator<Character> subjectGenerator) {
        return super.usingGenerator(subjectGenerator);
    }
}
