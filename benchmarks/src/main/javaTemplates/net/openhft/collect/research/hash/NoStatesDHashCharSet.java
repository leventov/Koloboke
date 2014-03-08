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
import net.openhft.collect.research.UnsafeConstants;

import java.util.*;


public class NoStatesDHashCharSet extends UnsafeConstants {

    public int size = 0;
    public int freeSlots;
    public int removedSlots = 0;
    public char freeValue = Character.MIN_VALUE;
    public char removedValue = Character.MIN_VALUE + 1;
    public char[] set;

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

    public boolean addTernaryState(char key) {
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

    public boolean addBinaryState(char key) {
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
}
