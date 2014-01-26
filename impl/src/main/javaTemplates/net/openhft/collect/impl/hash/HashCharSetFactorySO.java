/* with char|byte|short|int|long|float|double elem */
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

package net.openhft.collect.impl.hash;

import net.openhft.collect.CharCollection;
import net.openhft.collect.CharHashConfig;
import net.openhft.function.CharConsumer;
import net.openhft.collect.set.hash.HashCharSetFactory;

import java.util.Set;


public abstract class HashCharSetFactorySO extends CharHashFactory<MutableDHashCharSetGO>
        implements HashCharSetFactory {

    HashCharSetFactorySO(CharHashConfig conf) {
        super(conf);
    }

    @Override
    MutableDHashCharSetGO createNew(float loadFactor, int expectedSize, char free, char removed) {
        MutableDHashCharSet set = new MutableDHashCharSet();
        set.init(loadFactor, expectedSize, free, removed);
        return set;
    }

    @Override
    public MutableDHashCharSetGO newMutableSet(int expectedSize) {
        return newHash(expectedSize);
    }

    ImmutableDHashCharSetGO uninitializedImmutableSet() {
        return new ImmutableDHashCharSet();
    }

    @Override
    public MutableDHashCharSetGO newMutableSet(Iterable<Character> elements, int expectedSize) {
        if (elements instanceof CharCollection) {
            if (elements instanceof CharDHash) {
                CharDHash hash = (CharDHash) elements;
                if (hash.loadFactor() == hashConf.getLoadFactor()) {
                    MutableDHashCharSet set = new MutableDHashCharSet();
                    set.copy(hash);
                    return set;
                }
            }
            int size = elements instanceof Set ? ((Set) elements).size() : expectedSize;
            final MutableDHashCharSetGO set = newMutableSet(size);
            ((CharCollection) elements).forEach(new CharConsumer() {
                @Override
                public void accept(char e) {
                    set.add(e);
                }
            });
            return set;
        } else {
            int size = elements instanceof Set ? ((Set) elements).size() : expectedSize;
            MutableDHashCharSetGO set = newMutableSet(size);
            for (char e : elements) {
                set.add(e);
            }
            return set;
        }
    }
}
