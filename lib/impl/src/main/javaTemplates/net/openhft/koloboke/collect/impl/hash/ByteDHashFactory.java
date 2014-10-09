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

package net.openhft.koloboke.collect.impl.hash;

import net.openhft.koloboke.collect.hash.*;

import java.util.Random;
import /* if JDK8 jdk //java.util.concurrent
     /* elif JDK6 jdk */net.openhft.koloboke.collect.impl/* endif */.ThreadLocalRandom;


abstract class ByteDHashFactory
        /* if !(float|double elem) && !(LHash hash) */<MT>/* endif */ extends ByteHashFactorySO {

    ByteDHashFactory(HashConfig hashConf, int defaultExpectedSize
            /* if !(float|double elem) */, byte lower, byte upper/* endif */) {
        super(hashConf, defaultExpectedSize/* if !(float|double elem) */, lower, upper/* endif */);
    }

    /* if !(float|double elem) && !(LHash hash) */
    abstract MT createNewMutable(int expectedSize, byte free, byte removed);

    /* define nextIntOrLong */
    /* if !(long elem) //nextInt// elif long elem //nextLong// endif */
    /* enddefine */

    MT newMutableHash(int expectedSize) {
        byte free, removed;
        if (randomRemoved) {
            Random random = ThreadLocalRandom.current();
            removed = (byte) random./* nextIntOrLong */nextInt/**/();
            if (randomFree) {
                free = (byte) random./* nextIntOrLong */nextInt/**/();
            } else {
                free = freeValue;
            }
            while (free == removed) {
                removed = (byte) random./* nextIntOrLong */nextInt/**/();
            }
        } else {
            removed = removedValue;
            free = freeValue;
        }
        return createNewMutable(expectedSize, free, removed);
    }

    /* endif */
}
