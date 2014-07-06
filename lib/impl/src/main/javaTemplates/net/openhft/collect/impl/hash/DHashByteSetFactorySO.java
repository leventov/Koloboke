/* with
 DHash|QHash|LHash hash
 byte|char|short|int|long|float|double elem
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

package net.openhft.collect.impl.hash;

import net.openhft.collect.*;
import net.openhft.collect.set.hash.HashByteSetFactory;

import java.util.Collection;
import java.util.Set;


public abstract class DHashByteSetFactorySO
        /* if !(float|double elem) */
        extends ByteDHashFactory/* if !(LHash hash) */<MutableDHashByteSetGO>/* endif */
        /* endif */
        implements HashByteSetFactory {

    /* if float|double elem */
    final HashConfig hashConf;
    final HashConfigWrapper configWrapper;
    /* endif */

    DHashByteSetFactorySO(/* if !(float|double elem) */ByteHashConfig
            /* elif float|double elem //HashConfig// endif */ conf) {
        /* if !(float|double elem) */
        super(conf);
        /* elif float|double elem */
        hashConf = conf;
        configWrapper = new HashConfigWrapper(conf);
        /* endif */
    }

    /* if !(float|double elem) && !(LHash hash) */
    @Override
    MutableDHashByteSetGO createNewMutable(int expectedSize, byte free, byte removed) {
        MutableDHashByteSet set = new MutableDHashByteSet();
        set.init(configWrapper, expectedSize, free, removed);
        return set;
    }
    /* elif float|double elem */
    @Override
    public HashConfig getConfig() {
        return hashConf;
    }
    /* endif */

    /* with Mutable|Updatable|Immutable mutability */
    MutableDHashByteSetGO uninitializedMutableSet() {
        return new MutableDHashByteSet();
    }
    /* endwith */

    /* with Mutable|Updatable mutability */
    @Override
    public MutableDHashByteSetGO newMutableSet(int expectedSize) {
        /* if float|double elem */
        MutableDHashByteSetGO set = new MutableDHashByteSet();
        set.init(configWrapper, expectedSize);
        return set;
        /* elif !(float|double elem) && !(LHash hash) && Mutable mutability */
        return newMutableHash(expectedSize);
        /* elif LHash hash || Updatable mutability */
        MutableDHashByteSetGO set = new MutableDHashByteSet();
        set.init(configWrapper, expectedSize, getFree());
        return set;
        /* endif */
    }

    /* if Updatable mutability */
    @Override
    public MutableDHashByteSetGO newMutableSet(Iterable<Byte> elements, int expectedSize) {
        if (elements instanceof ByteCollection) {
            if (elements instanceof SeparateKVByteDHash) {
                SeparateKVByteDHash hash = (SeparateKVByteDHash) elements;
                if (hash.hashConfig().equals(hashConf)) {
                    MutableDHashByteSet set = new MutableDHashByteSet();
                    set.copy(hash);
                    return set;
                }
            }
            int size = elements instanceof Set ? ((Set) elements).size() : expectedSize;
            MutableDHashByteSetGO set = newMutableSet(size);
            set.addAll((Collection<Byte>) elements);
            return set;
        } else {
            int size = elements instanceof Set ? ((Set) elements).size() : expectedSize;
            MutableDHashByteSetGO set = newMutableSet(size);
            for (byte e : elements) {
                set.add(e);
            }
            return set;
        }
    }
    /* endif */
    /* endwith */
}
