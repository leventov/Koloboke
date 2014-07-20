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

import net.openhft.collect.hash.*;
import net.openhft.collect.set.hash.HashByteSetFactory;


public class DHashByteSetFactoryImpl extends DHashByteSetFactoryGO {

    /** For ServiceLoader */
    public DHashByteSetFactoryImpl() {
        this(HashConfig.getDefault()
                /* if !(float|double elem) */, Byte.MIN_VALUE, Byte.MAX_VALUE/* endif */);
    }

    public DHashByteSetFactoryImpl(HashConfig hashConf
            /* if !(float|double elem) */, byte lower, byte upper/* endif */) {
        super(hashConf/* if !(float|double elem) */, lower, upper/* endif */);
    }

    @Override
    public HashByteSetFactory withHashConfig(HashConfig hashConf) {
        if (LHashCapacities.configIsSuitableForMutableLHash(hashConf))
            return new LHashByteSetFactoryImpl(hashConf/* if !(float|double elem) */
                    , getLowerKeyDomainBound(), getUpperKeyDomainBound()/* endif */);
        /* with DHash|QHash hash */
        return new DHashByteSetFactoryImpl(hashConf/* if !(float|double elem) */
                , getLowerKeyDomainBound(), getUpperKeyDomainBound()/* endif */);
        /* endwith */
    }

    /* if !(float|double elem) */
    HashByteSetFactory withDomain(byte lower, byte upper) {
        if (lower == getLowerKeyDomainBound() && upper == getUpperKeyDomainBound())
            return this;
        return new DHashByteSetFactoryImpl(getHashConfig(), lower, upper);
    }

    @Override
    public HashByteSetFactory withKeysDomain(byte lower, byte upper) {
        if (lower > upper)
            throw new IllegalArgumentException("minPossibleKey shouldn't be greater " +
                    "than maxPossibleKey");
        return withDomain(lower, upper);
    }

    @Override
    public HashByteSetFactory withKeysDomainComplement(byte lower, byte upper) {
        if (lower > upper)
            throw new IllegalArgumentException("minImpossibleKey shouldn't be greater " +
                    "than maxImpossibleKey");
        return withDomain((byte) (upper + 1), (byte) (lower - 1));
    }
    /* endif */
}
