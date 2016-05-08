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

package com.koloboke.collect.research.hash;

import com.koloboke.collect.impl.Primitives;
import com.koloboke.collect.impl.UnsafeConstants;
import com.koloboke.function.CharConsumer;

import java.util.Arrays;


public class NoStatesQHashCharSet implements UnsafeConstants {

    public int size = 0;
    public int freeSlots;
    public int removedSlots = 0;
    public char freeValue = CharOps.randomExcept();
    public char removedValue = CharOps.randomExcept(freeValue);
    public char[] set;
    public char[] reuseKeys;

    public NoStatesQHashCharSet(int capacity) {
        set = new char[capacity];
        Arrays.fill(set, freeValue);
        freeSlots = capacity;
    }

    public void clear() {
        if (size != 0) {
            size = 0;
            freeSlots = set.length;
            removedSlots = 0;
            freeValue = CharOps.randomExcept();
            removedValue = CharOps.randomExcept(freeValue);
            Arrays.fill(set, freeValue);
        }
    }

    public int indexTernaryStateSimpleIndexing(char key) {
        char free = freeValue;
        if (key != free && key != removedValue) {
            char[] keys = set;
            int capacity = keys.length;
            int hash = Primitives.hashCode(key) & Integer.MAX_VALUE;
            final int index = hash % capacity;
            char cur = keys[index];
            if (cur == key) {
                return index;
            } else {
                if (cur == free)
                    return -1;
                int step = 1;
                int bIndex = index;
                int fIndex = index;
                while (true) {
                    if ((bIndex -= step) < 0) bIndex += capacity;
                    if ((cur = keys[bIndex]) == key) {
                        return bIndex;
                    } else if (cur == free) {
                        return -1;
                    }
                    // This way of wrapping capacity is less clear and a bit slower than
                    // the method from indexTernaryStateUnsafeIndexing(), but it protects
                    // from possible int overflow issues
                    int t;
                    if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                    if ((cur = keys[fIndex]) == key) {
                        return fIndex;
                    } else if (cur == free) {
                        return -1;
                    }
                    step += 2;
                }
            }
        } else {
            return -1;
        }
    }

    public int indexTernaryStateUnsafeIndexing(char key) {
        char free = freeValue;
        if (key != free && key != removedValue) {
            char[] keys = set;
            int capacity = keys.length;
            int hash = Primitives.hashCode(key) & Integer.MAX_VALUE;
            final long index = (long) (hash % capacity);
            char cur = U.getChar(keys, CHAR_BASE + (index << CHAR_SCALE_SHIFT));
            if (cur == key) {
                return (int) index;
            } else {
                if (cur == free)
                    return -1;
                long step = 1L;
                long bIndex = index;
                long fIndex = index;
                long capacityAsLong = (long) capacity;
                while (true) {
                    if ((bIndex -= step) < 0L) bIndex += capacityAsLong;
                    if ((cur = U.getChar(keys, CHAR_BASE + (bIndex << CHAR_SCALE_SHIFT))) == key) {
                        return (int) bIndex;
                    } else if (cur == free) {
                        return -1;
                    }
                    if ((fIndex += step) >= capacityAsLong) fIndex -= capacityAsLong;
                    if ((cur = U.getChar(keys, CHAR_BASE + (fIndex << CHAR_SCALE_SHIFT))) == key) {
                        return (int) fIndex;
                    } else if (cur == free) {
                        return -1;
                    }
                    step += 2L;
                }
            }
        } else {
            return -1;
        }
    }

    public int indexBinaryStateSimpleIndexing(char key) {
        char free = freeValue;
        if (key != free) {
            char[] keys = set;
            int capacity = keys.length;
            int hash = Primitives.hashCode(key) & Integer.MAX_VALUE;
            final int index = hash % capacity;
            char cur = keys[index];
            if (cur == key) {
                return index;
            } else {
                if (cur == free)
                    return -1;
                int step = 1;
                int bIndex = index;
                int fIndex = index;
                while (true) {
                    if ((bIndex -= step) < 0) bIndex += capacity;
                    if ((cur = keys[bIndex]) == key) {
                        return bIndex;
                    } else if (cur == free) {
                        return -1;
                    }
                    // See comment in indexTernaryStateSimpleIndexing()
                    int t;
                    if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                    if ((cur = keys[fIndex]) == key) {
                        return fIndex;
                    } else if (cur == free) {
                        return -1;
                    }
                    step += 2;
                }
            }
        } else {
            return -1;
        }
    }

    public int indexBinaryStateUnsafeIndexing(char key) {
        char free = freeValue;
        if (key != free) {
            char[] keys = set;
            int capacity = keys.length;
            int hash = Primitives.hashCode(key) & Integer.MAX_VALUE;
            final long index = (long) (hash % capacity);
            char cur = U.getChar(keys, CHAR_BASE + (index << CHAR_SCALE_SHIFT));
            if (cur == key) {
                return (int) index;
            } else {
                if (cur == free)
                    return -1;
                long step = 1L;
                long bIndex = index;
                long fIndex = index;
                long capacityAsLong = (long) capacity;
                while (true) {
                    if ((bIndex -= step) < 0L) bIndex += capacityAsLong;
                    if ((cur = U.getChar(keys, CHAR_BASE + (bIndex << CHAR_SCALE_SHIFT))) == key) {
                        return (int) bIndex;
                    } else if (cur == free) {
                        return -1;
                    }
                    if ((fIndex += step) >= capacityAsLong) fIndex -= capacityAsLong;
                    if ((cur = U.getChar(keys, CHAR_BASE + (fIndex << CHAR_SCALE_SHIFT))) == key) {
                        return (int) fIndex;
                    } else if (cur == free) {
                        return -1;
                    }
                    step += 2L;
                }
            }
        } else {
            return -1;
        }
    }

    public boolean addTernaryStateSimpleIndexing(char key) {
        char free = freeValue;
        char removed = removedValue;
        if (key == free || key == removed) {
            return false;
        }
        char[] keys = set;
        int capacity = keys.length;
        int hash = Primitives.hashCode(key) & Integer.MAX_VALUE;
        int index = hash % capacity;
        char cur = keys[index];
        keyAbsentFreeSlot:
        if (cur != free) {
            if (cur == key)
                return false;
            int firstRemoved = cur != removed ? -1 : index;
            int step = 1;
            int bIndex = index;
            int fIndex = index;
            while (true) {
                if ((bIndex -= step) < 0) bIndex += capacity;
                if ((cur = keys[bIndex]) == free) {
                    if (firstRemoved < 0) {
                        index = bIndex;
                        break keyAbsentFreeSlot;
                    } else {
                        // key is absent, removed slot
                        keys[firstRemoved] = key;
                        size++;
                        removedSlots--;
                        return true;
                    }
                } else if (cur == key) {
                    return false;
                } else if (cur == removed && firstRemoved < 0) {
                    firstRemoved = bIndex;
                }

                int t;
                if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                if ((cur = keys[fIndex]) == free) {
                    if (firstRemoved < 0) {
                        index = fIndex;
                        break keyAbsentFreeSlot;
                    } else {
                        // key is absent, removed slot
                        keys[firstRemoved] = key;
                        size++;
                        removedSlots--;
                        return true;
                    }
                } else if (cur == key) {
                    return false;
                } else if (cur == removed && firstRemoved < 0) {
                    firstRemoved = fIndex;
                }
                step += 2;
            }
        }
        // key is absent, free slot
        keys[index] = key;
        size++;
        freeSlots--;
        return true;
    }

    public boolean addTernaryStateUnsafeIndexing(char key) {
        char free = freeValue;
        char removed = removedValue;
        if (key == free || key == removed) {
            return false;
        }
        char[] keys = set;
        int capacity = keys.length;
        int hash = Primitives.hashCode(key) & Integer.MAX_VALUE;
        long index = (long) (hash % capacity);
        long offset = (index << CHAR_SCALE_SHIFT);
        char cur = U.getChar(keys, CHAR_BASE + offset);
        keyAbsentFreeSlot:
        if (cur != free) {
            if (cur == key)
                return false;
            long firstRemovedOffset = cur != removed ? -1L : offset;
            long step = CHAR_SCALE;
            long bOffset = offset;
            long fOffset = offset;
            long capacityBytes = ((long) capacity) << CHAR_SCALE_SHIFT;
            while (true) {
                if ((bOffset -= step) < 0L) bOffset += capacityBytes;
                if ((cur = U.getChar(keys, CHAR_BASE + bOffset)) == free) {
                    if (firstRemovedOffset < 0L) {
                        offset = bOffset;
                        break keyAbsentFreeSlot;
                    } else {
                        // key is absent, removed slot
                        U.putChar(keys, CHAR_BASE + firstRemovedOffset, key);
                        size++;
                        removedSlots--;
                        return true;
                    }
                } else if (cur == key) {
                    return false;
                } else if (cur == removed && firstRemovedOffset < 0L) {
                    firstRemovedOffset = bOffset;
                }

                if ((fOffset += step) >= capacityBytes) fOffset -= capacityBytes;
                if ((cur = U.getChar(keys, CHAR_BASE + fOffset)) == free) {
                    if (firstRemovedOffset < 0L) {
                        offset = fOffset;
                        break keyAbsentFreeSlot;
                    } else {
                        // key is absent, removed slot
                        U.putChar(keys, CHAR_BASE + firstRemovedOffset, key);
                        size++;
                        removedSlots--;
                        return true;
                    }
                } else if (cur == key) {
                    return false;
                } else if (cur == removed && firstRemovedOffset < 0L) {
                    firstRemovedOffset = fOffset;
                }
                step += CHAR_SCALE + CHAR_SCALE;
            }
        }
        // key is absent, free slot
        U.putChar(keys, CHAR_BASE + offset, key);
        size++;
        freeSlots--;
        return true;
    }

    public boolean addBinaryStateSimpleIndexing(char key) {
        char free = freeValue;
        if (key == free) {
            return false;
        }
        char[] keys = set;
        int capacity = keys.length;
        int hash = Primitives.hashCode(key) & Integer.MAX_VALUE;
        int index = hash % capacity;
        char cur = keys[index];
        keyAbsent:
        if (cur != free) {
            if (cur == key)
                return false;
            int step = 1;
            int bIndex = index;
            int fIndex = index;
            while (true) {
                if ((bIndex -= step) < 0) bIndex += capacity;
                if ((cur = keys[bIndex]) == free) {
                    index = bIndex;
                    break keyAbsent;
                } else if (cur == key) {
                    return false;
                }
                fIndex += step;
                int t;
                if ((t = fIndex - capacity) >= 0) fIndex = t;
                if ((cur = keys[fIndex]) == free) {
                    index = fIndex;
                    break keyAbsent;
                } else if (cur == key) {
                    return false;
                }
                step += 2;
            }
        }
        // key is absent
        keys[index] = key;
        size++;
        freeSlots--;
        return true;
    }

    public int addBinaryStateCountingProbes(char key) {
        char free = freeValue;
        if (key == free) {
            return -1;
        }
        int probes = 1;
        char[] keys = set;
        int capacity = keys.length;
        int hash = Primitives.hashCode(key) & Integer.MAX_VALUE;
        int index = hash % capacity;
        char cur = keys[index];
        keyAbsent:
        if (cur != free) {
            if (cur == key) {
                return -1;
            } else {
                int step = 1;
                int bIndex = index;
                int fIndex = index;
                while (true) {
                    probes++;
                    if ((bIndex -= step) < 0) bIndex += capacity;
                    if ((cur = keys[bIndex]) == free) {
                        index = bIndex;
                        break keyAbsent;
                    } else if (cur == key) {
                        return -1;
                    }
                    probes++;
                    fIndex += step;
                    int t;
                    if ((t = fIndex - capacity) >= 0) fIndex = t;
                    if ((cur = keys[fIndex]) == free) {
                        index = fIndex;
                        break keyAbsent;
                    } else if (cur == key) {
                        return -1;
                    }
                    step += 2;
                }
            }
        }
        // key is absent
        keys[index] = key;
        size++;
        freeSlots--;
        return probes;
    }

    public boolean removeSimpleIndexing(char key) {
        char free = freeValue;
        char removed = removedValue;
        if (key == free || key == removed) {
            return false;
        }
        char[] keys = set;
        int capacity = keys.length;
        int hash = Primitives.hashCode(key) & Integer.MAX_VALUE;
        int index = hash % capacity;
        char cur = keys[index];
        keyPresent:
        if (cur != key) {
            if (cur == free)
                return false;
            int step = 1;
            int bIndex = index;
            int fIndex = index;
            while (true) {
                if ((bIndex -= step) < 0) bIndex += capacity;
                if ((cur = keys[bIndex]) == key) {
                    index = bIndex;
                    break keyPresent;
                } else if (cur == free) {
                    return false;
                }

                int t;
                if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                if ((cur = keys[fIndex]) == key) {
                    index = fIndex;
                    break keyPresent;
                } else if (cur == free) {
                    return false;
                }
                step += 2;
            }
        }
        // key is present
        keys[index] = removed;
        size--;
        removedSlots++;
        return true;
    }

    public boolean removeUnsafeIndexing(char key) {
        char free = freeValue;
        char removed = removedValue;
        if (key == free || key == removed) {
            return false;
        }
        char[] keys = set;
        int capacity = keys.length;
        int hash = Primitives.hashCode(key) & Integer.MAX_VALUE;
        long index = (long) (hash % capacity);
        long offset = (index << CHAR_SCALE_SHIFT);
        char cur = U.getChar(keys, CHAR_BASE + offset);
        keyPresent:
        if (cur != key) {
            if (cur == free)
                return false;
            long step = CHAR_SCALE;
            long bOffset = offset;
            long fOffset = offset;
            long capacityBytes = ((long) capacity) << CHAR_SCALE_SHIFT;
            while (true) {
                if ((bOffset -= step) < 0L) bOffset += capacityBytes;
                if ((cur = U.getChar(keys, CHAR_BASE + bOffset)) == key) {
                    offset = bOffset;
                    break keyPresent;
                } else if (cur == free) {
                    return false;
                }

                if ((fOffset += step) >= capacityBytes) fOffset -= capacityBytes;
                if ((cur = U.getChar(keys, CHAR_BASE + fOffset)) == key) {
                    offset = fOffset;
                    break keyPresent;
                } else if (cur == free) {
                    return false;
                }
                step += CHAR_SCALE + CHAR_SCALE;
            }
        }
        // key is present
        U.putChar(keys, CHAR_BASE + offset, removed);
        size--;
        removedSlots++;
        return true;
    }

    public void rehashSimpleIndexing(int capacity) {
        char free = freeValue;
        char removed = removedValue;
        char[] keys = set;
        char[] newKeys = getNewKeys(capacity);
        Arrays.fill(newKeys, free);
        iterKeys:
        for (int i = keys.length - 1; i >= 0; i--) {
            char key;
            if ((key = keys[i]) != free && key != removed) {
                int hash = Primitives.hashCode(key) & Integer.MAX_VALUE;
                int index = hash % capacity;
                if (newKeys[index] == free) {
                    newKeys[index] = key;
                } else {
                    int step = 1;
                    int bIndex = index;
                    int fIndex = index;
                    while (true) {
                        if ((bIndex -= step) < 0) bIndex += capacity;
                        if (newKeys[bIndex] == free) {
                            newKeys[bIndex] = key;
                            continue iterKeys;
                        }
                        int t;
                        if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                        if (newKeys[fIndex] == free) {
                            newKeys[fIndex] = key;
                            continue iterKeys;
                        }
                        step += 2;
                    }
                }
            }
        }
        set = newKeys;
        reuseKeys = keys;
        freeSlots = capacity - size;
        removedSlots = 0;
    }

    public void rehashUnsafeIndexing(int capacity) {
        char free = freeValue;
        char removed = removedValue;
        char[] keys = set;
        char[] newKeys = getNewKeys(capacity);
        Arrays.fill(newKeys, free);

        long CHAR_BASE = UnsafeConstants.CHAR_BASE;
        long CHAR_SCALE = UnsafeConstants.CHAR_SCALE;
        int CHAR_SCALE_SHIFT = UnsafeConstants.CHAR_SCALE_SHIFT;
        long capacityBytes = ((long) capacity) << CHAR_SCALE_SHIFT;
        long stepStep = CHAR_SCALE + CHAR_SCALE;

        iterKeys:
        for (int i = keys.length - 1; i >= 0; i--) {
            char key;
            if ((key = keys[i]) != free && key != removed) {
                int hash = Primitives.hashCode(key) & Integer.MAX_VALUE;
                long index = (long) (hash % capacity);
                long offset = (index << CHAR_SCALE_SHIFT);
                if (U.getChar(newKeys, CHAR_BASE + offset) == free) {
                    U.putChar(newKeys, CHAR_BASE + offset, key);
                } else {
                    long step = CHAR_SCALE;
                    long bOffset = offset;
                    long fOffset = offset;
                    while (true) {
                        if ((bOffset -= step) < 0L) bOffset += capacityBytes;
                        if (U.getChar(newKeys, CHAR_BASE + bOffset) == free) {
                            U.putChar(newKeys, CHAR_BASE + bOffset, key);
                            continue iterKeys;
                        }

                        if ((fOffset += step) >= capacityBytes) fOffset -= capacityBytes;
                        if (U.getChar(newKeys, CHAR_BASE + fOffset) == free) {
                            U.putChar(newKeys, CHAR_BASE + fOffset, key);
                            continue iterKeys;
                        }
                        step += stepStep;
                    }
                }
            }
        }
        set = newKeys;
        reuseKeys = keys;
        freeSlots = capacity - size;
        removedSlots = 0;
    }

    public char[] getNewKeys(int capacity) {
        if (reuseKeys != null && reuseKeys.length == capacity) {
            return reuseKeys;
        } else {
            return new char[capacity];
        }
    }

    public void forEachBinaryState(CharConsumer action) {
        char free = freeValue;
        char[] keys = set;
        for (int i = keys.length - 1; i >= 0; i--) {
            char k;
            if ((k = keys[i]) != free)
                action.accept(k);
        }
    }

    public void forEachTernaryState(CharConsumer action) {
        char free = freeValue;
        char removed = removedValue;
        char[] keys = set;
        for (int i = keys.length - 1; i >= 0; i--) {
            char k;
            if ((k = keys[i]) != free && k != removed)
                action.accept(k);
        }
    }
}
