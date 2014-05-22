/* with
 DHash|LHash hash
 char|byte|short|int|long elem
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

import net.openhft.collect.CharHashConfig;
import net.openhft.collect.HashConfig;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

abstract class CharDHashFactory/* if !(LHash hash) */<MT>/* endif */ {

    final CharHashConfig conf;
    final HashConfig hashConf;
    final HashConfigWrapper configWrapper;
    private final boolean randomFree, randomRemoved;
    private final char freeValue, removedValue;

    CharDHashFactory(CharHashConfig conf) {
        this.conf = conf;
        hashConf = conf.getHashConfig();
        configWrapper = new HashConfigWrapper(hashConf);
        char lower = conf.getLowerKeyDomainBound();
        char upper = conf.getUpperKeyDomainBound();
        if ((char) (lower - 1) == upper) {
            randomFree = randomRemoved = true;
            freeValue = removedValue = /* const elem 0 */0;
        } else {
            randomFree = false;
            if ((lower < upper && (lower > 0 || upper < 0)) ||
                    (upper < lower && (lower > 0 && upper < 0))) {
                freeValue = /* const elem 0 */0;
            } else {
                freeValue = (char) (lower - 1);
            }
            if ((char) (lower - 2) == upper) {
                randomRemoved = true;
                removedValue = /* const elem 0 */0;
            } else {
                randomRemoved = false;
                if (upper + 1 != 0) {
                    removedValue = (char) (upper + 1);
                } else {
                    removedValue = (char) (upper + 2);
                }
            }
        }
    }

    public CharHashConfig getConfig() {
        return conf;
    }

    /* if !(LHash hash) */
    abstract MT createNew(int expectedSize, char free, char removed);

    /* define nextIntOrLong */
    /* if !(long elem) //nextInt// elif long elem //nextLong// endif */
    /* enddefine */

    MT newHash(int expectedSize) {
        char free, removed;
        if (randomRemoved) {
            Random random = ThreadLocalRandom.current();
            removed = (char) random./* nextIntOrLong */nextInt/**/();
            if (randomFree) {
                free = (char) random./* nextIntOrLong */nextInt/**/();
            } else {
                free = freeValue;
            }
            while (free == removed) {
                removed = (char) random./* nextIntOrLong */nextInt/**/();
            }
        } else {
            removed = removedValue;
            free = freeValue;
        }
        return createNew(expectedSize, free, removed);
    }

    /* elif LHash hash */

    char getFree() {
        if (randomFree) {
            Random random = ThreadLocalRandom.current();
            return (char) random./* nextIntOrLong */nextInt/**/();
        } else {
            return freeValue;
        }
    }
    /* endif */
}
