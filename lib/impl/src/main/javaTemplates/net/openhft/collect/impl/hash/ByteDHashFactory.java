/* with
 DHash|QHash|LHash hash
 byte|char|short|int|long elem
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

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;


abstract class ByteDHashFactory/* if !(LHash hash) */<MT>/* endif */ {

    final ByteHashConfig conf;
    final HashConfig hashConf;
    final HashConfigWrapper configWrapper;
    private final boolean randomFree, randomRemoved;
    private final byte freeValue, removedValue;

    ByteDHashFactory(ByteHashConfig conf) {
        this.conf = conf;
        hashConf = conf.getHashConfig();
        configWrapper = new HashConfigWrapper(hashConf);
        byte lower = conf.getLowerKeyDomainBound();
        byte upper = conf.getUpperKeyDomainBound();
        if ((byte) (lower - 1) == upper) {
            randomFree = randomRemoved = true;
            freeValue = removedValue = /* const elem 0 */0;
        } else {
            randomFree = false;
            if ((lower < upper && (lower > 0 || upper < 0)) ||
                    (upper < lower && (lower > 0 && upper < 0))) {
                freeValue = /* const elem 0 */0;
            } else {
                freeValue = (byte) (lower - 1);
            }
            if ((byte) (lower - 2) == upper) {
                randomRemoved = true;
                removedValue = /* const elem 0 */0;
            } else {
                randomRemoved = false;
                if (upper + 1 != 0) {
                    removedValue = (byte) (upper + 1);
                } else {
                    removedValue = (byte) (upper + 2);
                }
            }
        }
    }

    public ByteHashConfig getConfig() {
        return conf;
    }

    /* if !(LHash hash) */
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

    byte getFree() {
        if (randomFree) {
            Random random = ThreadLocalRandom.current();
            return (byte) random./* nextIntOrLong */nextInt/**/();
        } else {
            return freeValue;
        }
    }
}
