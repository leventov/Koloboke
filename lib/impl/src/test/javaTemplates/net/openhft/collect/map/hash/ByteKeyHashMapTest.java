/* with DHash|QHash hash */
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
import net.openhft.collect.HashConfig;
import net.openhft.collect.impl.hash.ByteIntDHash;
import net.openhft.collect.impl.hash.MutableDHash;
import net.openhft.collect.map.ByteIntMap;
import net.openhft.collect.map.ByteIntMapFactory;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;

import static net.openhft.collect.map.hash.HashByteIntMaps.getDefaultFactory;
import static org.junit.Assert.*;


public class ByteKeyHashMapTest {

    @Test
    public void testCorrectFreeAndRemovedValuesReplacement() {
        ByteHashConfig config = ByteHashConfig.getDefault()
                // to ensure DHash will be created
                .withHashConfig(HashConfig.getDefault().withGrowFactor(1.999))
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

    @Test
    public void testAbilityToReplaceFreeOnAlmostFullHash() {
        ByteHashConfig config = ByteHashConfig.getDefault()
                .withHashConfig(HashConfig.getDefault().withMaxLoad(0.999)
                        // to ensure DHash will be created
                        .withGrowFactor(1.999))
                .withKeysDomainComplement((byte) 0, (byte) 1);
        ByteIntMapFactory factory = getDefaultFactory().withConfig(config);

        ByteIntMap map;
        ByteIntDHash asDHash;
        for (int i = 0; ; i++) {
            map = factory.newMutableMap(i);
            asDHash = (ByteIntDHash) map;
            if (asDHash.capacity() > 128) {
                break;
            }
        }

        int capacity = asDHash.capacity();
        assertEquals(capacity - 1, ((MutableDHash) map).maxSize());
        assertTrue(asDHash.freeValue() == 0 && asDHash.removedValue() == 1 ||
                asDHash.freeValue() == 1 && asDHash.removedValue() == 0);

        for (int i = 2; i < capacity; i++) {
            map.put((byte) i, 0);
        }

        assertTrue(asDHash.freeValue() == 0 && asDHash.removedValue() == 1 ||
                asDHash.freeValue() == 1 && asDHash.removedValue() == 0);
        assertEquals(capacity, asDHash.capacity());

        map.put((byte) 0, 0);

        assertEquals(capacity, asDHash.capacity());
        assertEquals(capacity - 1, sizeByValueIterator(map));
        assertTrue(asDHash.freeValue() != 0 && asDHash.removedValue() == 1 ||
                asDHash.freeValue() == 1 && asDHash.removedValue() != 0);
        assertTrue(map.containsKey((byte) 0));
        assertFalse(map.containsKey((byte) 1));
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
