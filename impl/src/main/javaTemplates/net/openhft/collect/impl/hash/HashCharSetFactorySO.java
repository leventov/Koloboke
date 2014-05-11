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

import net.openhft.collect.*;
import net.openhft.collect.set.hash.HashCharSetFactory;

import java.util.Collection;
import java.util.Set;


public abstract class HashCharSetFactorySO
        /* if !(float|double elem) */extends CharHashFactory<MutableDHashCharSetGO>/* endif */
        implements HashCharSetFactory {

    /* if float|double elem */
    final HashConfig hashConf;
    final HashConfigWrapper configWrapper;
    /* endif */

    HashCharSetFactorySO(/* if !(float|double elem) */CharHashConfig
            /* elif float|double elem //HashConfig// endif */ conf) {
        /* if !(float|double elem) */
        super(conf);
        /* elif float|double elem */
        hashConf = conf;
        configWrapper = new HashConfigWrapper(conf);
        /* endif */
    }

    /* if !(float|double elem) */
    @Override
    MutableDHashCharSetGO createNew(
            HashConfigWrapper configWrapper, int expectedSize, char free, char removed) {
        MutableDHashCharSet set = new MutableDHashCharSet();
        set.init(configWrapper, expectedSize, free, removed);
        return set;
    }
    /* elif float|double elem */
    @Override
    public HashConfig getConfig() {
        return hashConf;
    }
    /* endif */

    @Override
    public MutableDHashCharSetGO newMutableSet(int expectedSize) {
        /* if !(float|double elem) */
        return newHash(expectedSize);
        /* elif float|double elem */
        MutableDHashCharSetGO set = new MutableDHashCharSet();
        set.init(configWrapper, expectedSize);
        return set;
        /* endif */
    }

    ImmutableDHashCharSetGO uninitializedImmutableSet() {
        return new ImmutableDHashCharSet();
    }

    @Override
    public MutableDHashCharSetGO newMutableSet(Iterable<Character> elements, int expectedSize) {
        if (elements instanceof CharCollection) {
            if (elements instanceof CharDHash) {
                CharDHash hash = (CharDHash) elements;
                if (hash.hashConfig().equals(hashConf)) {
                    MutableDHashCharSet set = new MutableDHashCharSet();
                    set.copy(hash);
                    return set;
                }
            }
            int size = elements instanceof Set ? ((Set) elements).size() : expectedSize;
            MutableDHashCharSetGO set = newMutableSet(size);
            set.addAll((Collection<Character>) elements);
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
