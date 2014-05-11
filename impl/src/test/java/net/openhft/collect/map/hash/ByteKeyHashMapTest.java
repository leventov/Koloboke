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

package net.openhft.collect.map.hash;

import net.openhft.collect.ByteHashConfig;
import net.openhft.collect.impl.hash.ByteIntDHash;
import net.openhft.collect.map.ByteIntMap;
import net.openhft.collect.map.ByteIntMapFactory;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static net.openhft.collect.map.hash.HashByteIntMaps.getDefaultFactory;


public class ByteKeyHashMapTest {

    @Test
    public void test() {
        ByteHashConfig config = ByteHashConfig.getDefault()
                .withKeysDomainComplement((byte) 0, (byte) 0);
        ByteIntMapFactory factory = getDefaultFactory().withConfig(config);
        for (int i = Byte.MIN_VALUE; i <= Byte.MAX_VALUE; i++) {
            for (int j = Byte.MIN_VALUE; j <= Byte.MAX_VALUE; j++) {
                for (int k = Byte.MIN_VALUE; k <= Byte.MAX_VALUE; k++) {
                    ByteIntMap map = factory.newMutableMapOf((byte) i, i, (byte) j, j, (byte) k, k);
                    if (map.size() != 3)
                        break;
                    ByteIntDHash h = (ByteIntDHash) map;
                    String p = toString("Initially: ", h) + " ";
                    assertEquals(3, sizeByValueIterator(map));
                    map.remove((byte) i);
                    assertEquals(2, map.size());
                    assertEquals(2, sizeByValueIterator(map));
                    map.put((byte) i, i);
                    assertEquals(3, map.size());
                    assertEquals(3, sizeByValueIterator(map));

                    map.remove((byte) j);
                    assertEquals(2, map.size());
                    assertEquals(2, sizeByValueIterator(map));
                    map.put((byte) j, j);
                    assertEquals(3, map.size());
                    assertEquals(3, sizeByValueIterator(map));

                    map.remove((byte) k);
                    assertEquals(2, map.size());
                    assertEquals(2, sizeByValueIterator(map));
                    map.put((byte) k, k);
                    assertEquals(3, map.size());
                    assertEquals(3, sizeByValueIterator(map));
                }
            }
        }
    }

    private static String toString(String prefix, ByteIntDHash hash) {
        return prefix +
                "Hash: " + hash.toString() + "; Keys: " + Arrays.toString(hash.keys()) +
                "; Free value: " + hash.freeValue() +
                "; Removed value: " + hash.removedValue() +
                "; Free slots: " + hash.freeSlots() + "; Removed slots: " + hash.removedSlots();
    }

    private static int sizeByValueIterator(Map<?, ?> m) {
        int size = 0;
        for (Object o : m.values()) {
            size++;
        }
        return size;
    }
}
