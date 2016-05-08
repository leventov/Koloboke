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

import java.util.*;


public class NoStatesDHashCharSet implements UnsafeConstants {

    public int size = 0;
    public int freeSlots;
    public int removedSlots = 0;
    public char freeValue = CharOps.randomExcept();
    public char removedValue = CharOps.randomExcept(freeValue);
    public char[] set;
    public char[] reuseKeys;

    public NoStatesDHashCharSet(int capacity) {
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
            int index = hash % capacity;
            char cur = keys[index];
            if (cur == key) {
                return index;
            } else {
                if (cur == free) {
                    return -1;
                } else {
                    int step = (hash % (capacity - 2)) + 1;
                    while (true) {
                        if ((index -= step) < 0) index += capacity; // nextIndex
                        if ((cur = keys[index]) == key) {
                            return index;
                        } else if (cur == free) {
                            return -1;
                        }
                    }
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
            long index = (long) (hash % capacity);
            char cur = U.getChar(keys, CHAR_BASE + (index << CHAR_SCALE_SHIFT));
            if (cur == key) {
                return (int) index;
            } else {
                if (cur == free) {
                    return -1;
                } else {
                    long step = (long) ((hash % (capacity - 2)) + 1);
                    while (true) {
                        if ((index -= step) < 0L) index += capacity; // nextIndex
                        if ((cur = U.getChar(keys, CHAR_BASE + (index << CHAR_SCALE_SHIFT))) == key) {
                            return (int) index;
                        } else if (cur == free) {
                            return -1;
                        }
                    }
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
            int index = hash % capacity;
            char cur = keys[index];
            if (cur == key) {
                return index;
            } else {
                if (cur == free) {
                    return -1;
                } else {
                    int step = (hash % (capacity - 2)) + 1;
                    while (true) {
                        if ((index -= step) < 0) index += capacity; // nextIndex
                        if ((cur = keys[index]) == key) {
                            return index;
                        } else if (cur == free) {
                            return -1;
                        }
                    }
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
            long index = (long) (hash % capacity);
            char cur = U.getChar(keys, CHAR_BASE + (index << CHAR_SCALE_SHIFT));
            if (cur == key) {
                return (int) index;
            } else {
                if (cur == free) {
                    return -1;
                } else {
                    long step = (long) ((hash % (capacity - 2)) + 1);
                    while (true) {
                        if ((index -= step) < 0L) index += capacity; // nextIndex
                        if ((cur = U.getChar(keys, CHAR_BASE + (index << CHAR_SCALE_SHIFT))) == key) {
                            return (int) index;
                        } else if (cur == free) {
                            return -1;
                        }
                    }
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
            if (cur == key) {
                return false;
            } else {
                int firstRemoved = cur != removed ? -1 : index;
                int step = (hash % (capacity - 2)) + 1;
                while (true) {
                    if ((index -= step) < 0) index += capacity; // nextIndex
                    if ((cur = keys[index]) == free) {
                        if (firstRemoved < 0) {
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
                        firstRemoved = index;
                    }
                }
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
        long offset = CHAR_BASE + (index << CHAR_SCALE_SHIFT);
        char cur = U.getChar(keys, offset);
        keyAbsentFreeSlot:
        if (cur != free) {
            if (cur == key) {
                return false;
            } else {
                long firstRemovedOffset = cur != removed ? -1L : offset;
                long step = ((long) ((hash % (capacity - 2)) + 1)) << CHAR_SCALE_SHIFT;
                while (true) {
                    if ((offset -= step) < CHAR_BASE)
                        offset += ((long) capacity) << CHAR_SCALE_SHIFT; // nextIndex
                    if ((cur = U.getChar(keys, offset)) == free) {
                        if (firstRemovedOffset < 0L) {
                            break keyAbsentFreeSlot;
                        } else {
                            // key is absent, removed slot
                            U.putChar(keys, firstRemovedOffset, key);
                            size++;
                            removedSlots--;
                            return true;
                        }
                    } else if (cur == key) {
                        return false;
                    } else if (cur == removed && firstRemovedOffset < 0L) {
                        firstRemovedOffset = offset;
                    }
                }
            }
        }
        // key is absent, free slot
        U.putChar(keys, offset, key);
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
            if (cur == key) {
                return false;
            } else {
                int step = (hash % (capacity - 2)) + 1;
                while (true) {
                    if ((index -= step) < 0) index += capacity; // nextIndex
                    if ((cur = keys[index]) == free) {
                        break keyAbsent;
                    } else if (cur == key) {
                        return false;
                    }
                }
            }
        }
        // key is absent
        keys[index] = key;
        size++;
        freeSlots--;
        return true;
    }

    public boolean removeSimpleIndexing(char key) {
        char free = freeValue;
        char removed = removedValue;
        if (key != free && key != removed) {
            char[] keys = set;
            int capacity = keys.length;
            int hash = Primitives.hashCode(key) & Integer.MAX_VALUE;
            int index = hash % capacity;
            char cur = keys[index];
            keyPresent:
            if (cur != key) {
                if (cur == free) {
                    return false;
                } else {
                    int step = (hash % (capacity - 2)) + 1;
                    while (true) {
                        if ((index -= step) < 0) index += capacity; // nextIndex
                        if ((cur = keys[index]) == key) {
                            break keyPresent;
                        } else if (cur == free) {
                            return false;
                        }
                    }
                }
            }
            // key is present
            keys[index] = removed;
            size--;
            removedSlots++;
            return true;
        } else {
            return false;
        }
    }

    public boolean removeUnsafeIndexing(char key) {
        char free = freeValue;
        char removed = removedValue;
        if (key != free && key != removed) {
            char[] keys = set;
            int capacity = keys.length;
            int hash = Primitives.hashCode(key) & Integer.MAX_VALUE;
            long index = (long) (hash % capacity);
            long offset = CHAR_BASE + (index << CHAR_SCALE_SHIFT);
            char cur = U.getChar(keys, offset);
            keyPresent:
            if (cur != key) {
                if (cur == free) {
                    return false;
                } else {
                    long step = (long) ((hash % (capacity - 2)) + 1) << CHAR_SCALE_SHIFT;
                    while (true) {
                        if ((offset -= step) < CHAR_BASE)
                            offset += ((long) capacity) << CHAR_SCALE_SHIFT; // nextIndex
                        if ((cur = U.getChar(keys, offset)) == key) {
                            break keyPresent;
                        } else if (cur == free) {
                            return false;
                        }
                    }
                }
            }
            // key is present
            U.putChar(keys, offset, removed);
            size--;
            removedSlots++;
            return true;
        } else {
            return false;
        }
    }

    public void rehashSimpleIndexing(int capacity) {
        char free = freeValue;
        char removed = removedValue;
        char[] keys = set;
        char[] newKeys = getNewKeys(capacity);
        Arrays.fill(newKeys, free);
        for (int i = keys.length - 1; i >= 0; i--) {
            char key;
            if ((key = keys[i]) != free && key != removed) {
                int hash = Primitives.hashCode(key) & Integer.MAX_VALUE;
                int index = hash % capacity;
                if (newKeys[index] != free) {
                    int step = (hash % (capacity - 2)) + 1;
                    do {
                        if ((index -= step) < 0) index += capacity; // nextIndex
                    } while (newKeys[index] != free);
                }
                newKeys[index] = key;
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
        int CHAR_SCALE_SHIFT = UnsafeConstants.CHAR_SCALE_SHIFT;
        long capacityBytes = ((long) capacity) << CHAR_SCALE_SHIFT;

        for (int i = keys.length - 1; i >= 0; i--) {
            char key;
            if ((key = keys[i]) != free && key != removed) {
                int hash = Primitives.hashCode(key) & Integer.MAX_VALUE;
                long index = (long) (hash % capacity);
                long offset = CHAR_BASE + (index << CHAR_SCALE_SHIFT);
                if (U.getChar(newKeys, offset) != free) {
                    long step = ((long) ((hash % (capacity - 2)) + 1)) << CHAR_SCALE_SHIFT;
                    do {
                        if ((offset -= step) < CHAR_BASE) offset += capacityBytes;
                    } while (U.getChar(newKeys, offset) != free);
                }
                U.putChar(newKeys, offset, key);
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
