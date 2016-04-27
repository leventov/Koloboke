/* with
 byte|char|short|int|long|float|double|obj key
 short|byte|char|int|long|float|double value
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

package net.openhft.koloboke.collect.map.hash;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class ByteShortMapAddValueTest {

    /* define delta // /* if float|double value //, 0.001f// endif */ // enddefine */

    @Test
    public void byteShortMapAddValueTest() {
        HashByteShortMap map = HashByteShortMaps.getDefaultFactory()
                .withDefaultValue(/* const value 1 */(short) 1/* endconst */)
                .newMutableMap();

        /* raw */byte key = /* if !(obj key) *//* const key 0 */(byte) 0/* endconst */
                    /* elif obj key //null// endif */;
        assertEquals(/* if !(float|double value) */2L
                    /* elif float|double value //2.0// if float value //f// endif //// endif */,
                map.addValue(key, /* const value 1 */(short) 1/* endconst */)/* delta */);
        assertEquals(/* if !(float|double value) */4L
                    /* elif float|double value //4.0// if float value //f// endif //// endif */,
                map.addValue(key, /* const value 2 */(short) 2/* endconst */)/* delta */);
        assertEquals(/* if !(float|double value) */7L
                    /* elif float|double value //7.0// if float value //f// endif //// endif */,
                map.addValue(key, /* const value 3 */(short) 3/* endconst */,
                /* const value 4 */(short) 4/* endconst */)/* delta */);
        map.remove(key);
        assertEquals(/* if !(float|double value) */11L
                    /* elif float|double value //11.0// if float value //f// endif //// endif */,
                map.addValue(key, /* const value 5 */(short) 5/* endconst */,
                /* const value 6 */(short) 6/* endconst */)/* delta */);
        assertEquals(/* if !(float|double value) */18L
                    /* elif float|double value //18.0// if float value //f// endif //// endif */,
                map.addValue(key, /* const value 7 */(short) 7/* endconst */,
                /* const value 8 */(short) 8/* endconst */)/* delta */);
    }
}
