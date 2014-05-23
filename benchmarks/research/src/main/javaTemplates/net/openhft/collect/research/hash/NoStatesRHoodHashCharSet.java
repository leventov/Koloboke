/* with char|byte|short|int|long key */
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

package net.openhft.collect.research.hash;

import net.openhft.collect.impl.Primitives;
import net.openhft.collect.impl.UnsafeConstants;

import java.util.*;

import static net.openhft.collect.impl.Primitives.hashCode;


public class NoStatesRHoodHashCharSet implements UnsafeConstants {

    public int size = 0;
    public char freeValue = Character.MIN_VALUE;
    public char[] set;

    public NoStatesRHoodHashCharSet(int capacity) {
        if ((capacity & (capacity - 1)) != 0) {
            throw new IllegalArgumentException();
        }
        set = new char[capacity];
        Arrays.fill(set, freeValue);
    }

    public void clear() {
        if (size != 0) {
            size = 0;
            Arrays.fill(set, freeValue);
        }
    }

    public boolean addBinaryStateSimpleIndexing(char key) {
        char free = freeValue;
        if (key == free) {
            return false;
        }
        char[] keys = set;
        int capacity = keys.length;
        int capacityMask = capacity - 1;
        int index = Primitives.hashCode(key) & capacityMask;
        char cur = keys[index];
        keyAbsent:
        if (cur != free) {
            if (cur == key) {
                return false;
            } else {
                int distance = 0;
                firstLoop:
                while (true) {
                    index = (index + 1) & capacityMask;
                    if ((cur = keys[index]) == free) {
                        break keyAbsent;
                    } else if (cur == key) {
                        return false;
                    } else {
                        int curDistance = (index - Primitives.hashCode(cur)) & capacityMask;
                        if (++distance > curDistance) {
                            keys[index] = key;
                            key = cur;
                            distance = curDistance;
                            break firstLoop;
                        }
                    }
                }
                while (true) {
                    index = (index + 1) & capacityMask;
                    if ((cur = keys[index]) == free) {
                        break keyAbsent;
                    } else {
                        int curDistance = (index - Primitives.hashCode(cur)) & capacityMask;
                        if (++distance > curDistance) {
                            keys[index] = key;
                            key = cur;
                            distance = curDistance;
                        }
                    }
                }
            }
        }
        // key is absent
        keys[index] = key;
        postAdd();
        return true;
    }

    public boolean addBinaryStateUnsafeIndexing(char key) {
        char free = freeValue;
        if (key == free) {
            return false;
        }
        char[] keys = set;
        int capacity = keys.length;
        int capacityMask = capacity - 1;
        long capacityMaskAsLong = (long) capacityMask;
        long index = ((long) Primitives.hashCode(key)) & capacityMaskAsLong;
        long offset = index << CHAR_SCALE_SHIFT;
        char cur = U.getChar(keys, CHAR_BASE + offset);
        keyAbsent:
        if (cur != free) {
            if (cur == key) {
                return false;
            } else {
                long capacityOffsetMask = capacityMaskAsLong << CHAR_SCALE_SHIFT;
                int distance = 0;
                firstLoop:
                while (true) {
                    offset = (offset + CHAR_SCALE) & capacityOffsetMask;
                    if ((cur = U.getChar(keys, CHAR_BASE + offset)) == free) {
                        break keyAbsent;
                    } else if (cur == key) {
                        return false;
                    } else {
                        int curDistance =
                                (((int) (offset >> CHAR_SCALE_SHIFT)) +
                                        capacity - Primitives.hashCode(cur)) & capacityMask;
                        if (++distance > curDistance) {
                            U.putChar(keys, CHAR_BASE + offset, key);
                            key = cur;
                            distance = curDistance;
                            break firstLoop;
                        }
                    }
                }
                while (true) {
                    offset = (offset + CHAR_SCALE) & capacityOffsetMask;
                    if ((cur = U.getChar(keys, CHAR_BASE + offset)) == free) {
                        break keyAbsent;
                    } else {
                        int curDistance =
                                (((int) (offset >> CHAR_SCALE_SHIFT)) +
                                        capacity - Primitives.hashCode(cur)) & capacityMask;
                        if (++distance > curDistance) {
                            U.putChar(keys, CHAR_BASE + offset, key);
                            key = cur;
                            distance = curDistance;
                        }
                    }
                }
            }
        }
        // key is absent
        U.putChar(keys, CHAR_BASE + offset, key);
        postAdd();
        return true;
    }

    private int distance(char key, int index) {
        return (index + set.length - Primitives.hashCode(key)) & (set.length - 1);
    }

    void postAdd() {
        size++;
    }

    static class DistanceStats {
        List<Long> counts;

        DistanceStats() {
            counts = new ArrayList<Long>();
        }

        DistanceStats(List<Long> counts) {
            this.counts = counts;
        }

        void add(int distance) {
            while (counts.size() < distance + 1) {
                counts.add(0L);
            }
            counts.set(distance, counts.get(distance) + 1);
        }

        double mean() {
            long size = 0, sum = 0;
            for (int i = 0; i < counts.size(); i++) {
                sum += i * counts.get(i);
                size += counts.get(i);
            }
            return ((double) sum) / size;
        }

        DistanceStats plus(DistanceStats stats) {
            List<Long> shorter, longer;
            if (counts.size() > stats.counts.size()) {
                shorter = stats.counts;
                longer = counts;
            } else {
                shorter = counts;
                longer = stats.counts;
            }
            List<Long> newCounts = new ArrayList<Long>(longer);
            for (int i = 0; i < shorter.size(); i++) {
                newCounts.set(i, newCounts.get(i) + shorter.get(i));
            }
            return new DistanceStats(newCounts);
        }
    }

    DistanceStats countDistanceStats() {
        DistanceStats stats = new DistanceStats();
        char free = freeValue;
        char[] keys = set;
        for (int i = 0; i < keys.length; i++) {
            char key = keys[i];
            if (key != free)
                stats.add(distance(key, i));
        }
        return stats;
    }

    public static void main(String[] args) {
        int power = Integer.parseInt(args[0]);
        if (power < 2 || power > 31)
            throw new IllegalArgumentException();
        int repeats = Integer.parseInt(args[1]);
        if (repeats <= 0)
            throw new IllegalArgumentException();


        int capacity = 1 << power;
        DistanceStats[] stats = new DistanceStats[capacity - 1];
        NoStatesRHoodHashCharSet set = new NoStatesRHoodHashCharSet(capacity);
        Random r = new Random();
        for (int i = 0; i < repeats; i++) {
            for (int j = 0; j < capacity - 1; j++) {
                set.addBinaryStateSimpleIndexing((char) r.nextLong());
                DistanceStats setStats = set.countDistanceStats();
                stats[j] = stats[j] == null ? setStats : stats[j].plus(setStats);
            }
            set.clear();
        }
        for (int i = 0; i < stats.length; i++) {
            DistanceStats distanceStats = stats[i];
            System.out.printf("%d: %.3f %s\n", i + 1, distanceStats.mean(),
                    distanceStats.counts.toString());
        }
    }
}
