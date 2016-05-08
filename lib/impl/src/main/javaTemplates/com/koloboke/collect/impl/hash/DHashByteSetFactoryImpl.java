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

package com.koloboke.collect.impl.hash;

import com.koloboke.collect.hash.*;
import com.koloboke.collect.set.hash.HashByteSetFactory;


public final class DHashByteSetFactoryImpl extends DHashByteSetFactoryGO {

    /** For ServiceLoader */
    public DHashByteSetFactoryImpl() {
        this(HashConfig.getDefault(), 10
                /* if !(float|double elem) */, Byte.MIN_VALUE, Byte.MAX_VALUE/* endif */);
    }

    /* define commonArgDef //HashConfig hashConf, int defaultExpectedSize
        // if !(float|double elem) //, byte lower, byte upper// endif //
    // enddefine */

    /* define commonArgApply //hashConf, defaultExpectedSize
        // if !(float|double elem) //, lower, upper// endif //// enddefine */

    public DHashByteSetFactoryImpl(/* commonArgDef */) {
        super(/* commonArgApply */);
    }

    @Override
    HashByteSetFactory thisWith(/* commonArgDef */) {
        return new DHashByteSetFactoryImpl(/* commonArgApply */);
    }

    /* with DHash|QHash|LHash hash */
    @Override
    HashByteSetFactory dHashLikeThisWith(/* commonArgDef */) {
        return new DHashByteSetFactoryImpl(/* commonArgApply */);
    }
    /* endwith */

    /* if !(float|double elem) */
    HashByteSetFactory withDomain(byte lower, byte upper) {
        if (lower == getLowerKeyDomainBound() && upper == getUpperKeyDomainBound())
            return this;
        return new DHashByteSetFactoryImpl(getHashConfig(), getDefaultExpectedSize(), lower, upper);
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
