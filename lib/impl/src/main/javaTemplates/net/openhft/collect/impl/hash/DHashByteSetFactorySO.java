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
import net.openhft.collect.hash.*;
import net.openhft.collect.set.hash.HashByteSetFactory;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Set;


public abstract class DHashByteSetFactorySO
        extends ByteDHashFactory
            /* if !(float|double elem) && !(LHash hash) */<MutableDHashByteSetGO>/* endif */
        implements HashByteSetFactory {

    DHashByteSetFactorySO(HashConfig hashConf, int defaultExpectedSize
            /* if !(float|double elem) */, byte lower, byte upper/* endif */) {
        super(hashConf, defaultExpectedSize/* if !(float|double elem) */, lower, upper/* endif */);
    }

    /* if !(float|double elem) && !(LHash hash) */
    @Override
    MutableDHashByteSetGO createNewMutable(int expectedSize, byte free, byte removed) {
        MutableDHashByteSet set = new MutableDHashByteSet();
        set.init(configWrapper, expectedSize, free, removed);
        return set;
    }
    /* endif */

    /* with Mutable|Updatable|Immutable mutability */
    MutableDHashByteSetGO uninitializedMutableSet() {
        return new MutableDHashByteSet();
    }
    /* endwith */

    /* with Mutable|Updatable mutability */
    @Override
    @Nonnull
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
    @Nonnull
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
