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


/**
 * Low-level insertion methods of open-addressing hash tables
 * should return index along with slot type:
 * 1) the same element is already present in the hash
 * 2) free slot
 * 3) "removed" slot
 *
 * There are several ways to pass this information to higher-level methods:
 *
 *   - Member boolean field + "-index - 1" hack. Allows to encode even 4 states,
 *     we need 3. Overhead: index arithmetic, 1 field IO, + 0-8 bytes to every
 *     hash table (most likely 0).
 *
 *   - "Tuple" class (this one). Overhead before escape analysis and scalarization:
 *     object allocation, 2 fields IO.
 *     After scalarization: theoretically, almost no overhead.
 */
public final class InsertionIndex {
    private static final int FREE = 0;
    private static final int EXISTING = -1;
    private static final int REMOVED = 1;

    public static InsertionIndex free( int index ) {
        return new InsertionIndex( index, FREE );
    }

    public static InsertionIndex existing( int index ) {
        return new InsertionIndex( index, EXISTING );
    }

    public static InsertionIndex removed( int index ) {
        return new InsertionIndex( index, REMOVED );
    }

    private final int index;
    private final int type;

    InsertionIndex( int index, int type ) {
        this.index = index;
        this.type = type;
    }

    public int get() {
        return index;
    }

    public boolean existing() {
        return type < 0;
    }

    public boolean absent() {
        return type >= 0;
    }

    public boolean freeSlot() {
        return type == FREE;
    }
}
