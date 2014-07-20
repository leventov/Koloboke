/* with
 char|byte|short|int|long|float|double|obj key
 short|byte|char|int|long|float|double|obj value
*/
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

package net.openhft.collect.testing.map;

import com.google.common.collect.testing.*;
import net.openhft.collect.testing.Mutability;
import net.openhft.collect.map.CharShortMapFactory;
import net.openhft.function./*f*/CharShortConsumer/**/;

import java.util.List;
import java.util.Map;


public class TestCharShortMapGenerator/*<>*/
        implements TestMapGenerator<Character, Short> {

    public static class Builder/*<>*/ {
        private CharShortMapFactory/*<>*/ factory;
        private SampleElements<? extends Character> keys;
        private SampleElements<? extends Short> values;

        public Builder/*<>*/ setFactory(CharShortMapFactory/*<>*/ factory) {
            this.factory = factory;
            return this;
        }

        public Builder/*<>*/ setKeys(SampleElements<? extends Character> keys) {
            this.keys = keys;
            return this;
        }

        public Builder/*<>*/ setValues(SampleElements<? extends Short> values) {
            this.values = values;
            return this;
        }

        public TestCharShortMapGenerator/*<>*/ withMutability(Mutability mutability) {
            return new TestCharShortMapGenerator(factory, keys, values, mutability);
        }
    }

    private CharShortMapFactory/*<super>*/ factory;
    private SampleElements<? extends Character> keys;
    private SampleElements<? extends Short> values;
    private Mutability mutability;

    private TestCharShortMapGenerator(CharShortMapFactory/*<>*/ factory,
            SampleElements<? extends Character> keys,
            SampleElements<? extends Short> values, Mutability mutability) {
        this.factory = factory;
        this.keys = keys;
        this.values = values;
        this.mutability = mutability;
    }

    @Override
    public SampleElements<Map.Entry<Character, Short>> samples() {
        // noinspection unchecked
        return (SampleElements) SampleElements.mapEntries(keys, values);
    }

    @Override
    public Character[] createKeyArray(int length) {
        return (Character[]) new /* raw */Character[length];
    }

    @Override
    public Short[] createValueArray(int length) {
        return (Short[]) new /* raw */Short[length];
    }

    @Override
    public Map<Character, Short> create(final Object... elements) {
        net.openhft.function.Consumer</*f*/CharShortConsumer/*<>*/> supplier =
                map -> {
                    for (Object e : elements) {
                        Map.Entry entry = (Map.Entry) e;
                        map.accept((Character) entry.getKey(), (Short) entry.getValue());
                    }
                };
        switch (mutability) {
            case IMMUTABLE: return factory.newImmutableMap(supplier);
            case UPDATABLE: return factory.newUpdatableMap(supplier);
            case MUTABLE: return factory.newMutableMap(supplier);
            default: throw new AssertionError();
        }
    }

    @Override
    public Map.Entry<Character, Short>[] createArray(int length) {
        return new Map.Entry[length];
    }

    @Override
    public Iterable<Map.Entry<Character, Short>> order(
            List<Map.Entry<Character, Short>> insertionOrder) {
        return insertionOrder;
    }
}
