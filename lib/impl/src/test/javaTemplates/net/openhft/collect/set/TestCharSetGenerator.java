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

package net.openhft.collect.set;

import com.google.common.collect.testing.SampleElements;
import com.google.common.collect.testing.TestSetGenerator;
import net.openhft.function.*;

import java.util.List;
import java.util.Set;


public class TestCharSetGenerator/*<>*/ implements TestSetGenerator<Character> {

    public static /*<>*/ TestCharSetGenerator/*<>*/ mutable(CharSetFactory/*<>*/ factory,
            SampleElements<? extends Character> elems) {
        return new TestCharSetGenerator(true, factory, elems);
    }

    public static /*<>*/ TestCharSetGenerator/*<>*/ immutable(CharSetFactory/*<>*/ factory,
            SampleElements<? extends Character> elems) {
        return new TestCharSetGenerator(false, factory, elems);
    }

    private final boolean mutable;
    private final CharSetFactory/*<>*/ factory;
    private final SampleElements<? extends Character> elems;

    private TestCharSetGenerator(boolean mutable, CharSetFactory/*<>*/ factory,
            SampleElements<? extends Character> elems) {
        this.mutable = mutable;
        this.factory = factory;
        this.elems = elems;
    }

    @Override
    public Set<Character> create(final Object... elements) {
        Consumer</*f*/CharConsumer/*<>*/> supplier = new Consumer</*f*/CharConsumer/*<>*/>() {
            @Override
            public void accept(/*f*/CharConsumer/*<>*/ set) {
                for (Object e : elements) {
                    set.accept((Character) e);
                }
            }
        };
        return mutable ? factory.newMutableSet(supplier) : factory.newImmutableSet(supplier);
    }

    @Override
    public SampleElements<Character> samples() {
        // noinspection unchecked
        return (SampleElements) elems;
    }

    @Override
    public Character[] createArray(int length) {
        return (Character[]) new /* raw */Character[length];
    }

    @Override
    public Iterable<Character> order(List<Character> insertionOrder) {
        return insertionOrder;
    }
}
