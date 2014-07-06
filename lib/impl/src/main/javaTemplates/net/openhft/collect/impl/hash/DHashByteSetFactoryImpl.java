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


public class DHashByteSetFactoryImpl extends DHashByteSetFactoryGO {

    /* define configClass */
    /* if !(float|double elem) //ByteHashConfig// elif float|double elem //HashConfig// endif */
    /* enddefine */

    /** For ServiceLoader */
    public DHashByteSetFactoryImpl() {
        this(/* configClass */ByteHashConfig/**/.getDefault());
    }

    public DHashByteSetFactoryImpl(/* configClass */ByteHashConfig/**/ conf) {
        super(conf);
    }

    @Override
    public HashByteSetFactory withConfig(/* configClass */ByteHashConfig/**/ config) {
        if (LHashCapacities.configIsSuitableForMutableLHash(
                /* if !(float|double elem) */config.getHashConfig()
                /* elif float|double elem //config// endif */))
            return new LHashByteSetFactoryImpl(config);
        return /* with DHash|QHash hash */new DHashByteSetFactoryImpl(config);/* endwith */
    }
}
