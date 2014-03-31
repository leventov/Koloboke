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

import net.openhft.collect.HashConfig;
import net.openhft.collect.HashOverflowException;
import net.openhft.collect.impl.AbstractContainer;

import static net.openhft.collect.impl.hash.DHashCapacities.bestCapacity;

import static java.lang.Math.max;


/**
 * <p><a name="rehash-logic"/><b>Rehash logic</b>
 *
 * <p></>We are targeting the following major hash use-patterns:
 * <ul>
 * <li>keys could be inserted, but never removed: caches, aggregating stats,
 * removing duplicates from list, ...</li>
 * <li>keys are inserted and removed so that the size remains nearly the same:
 * fixed-size caches, e. g. LRU.</li>
 * </ul>
 *
 * <hr/>
 *
 * <p>If the hash has {@code R} removed slots and {@code F} free slots,
 * probability of consuming free slot by inserting the new key is
 * {@code (F / (F + R)) ^ 2}.
 *
 * <p>Therefore, if we have the hash with {@code S} keys and capacity {@code C},
 * and {@code F = C - S} free slots, expected number of removed slots after
 * {@code IRP} insert-remove pairs is {@code R(IRP) = F * IRP / (F + IRP)}.
 *
 * <p>Suppose we have the hash with the size {@code S} and load factor
 * {@code LF}. We want to choose capacity C so that during endless insertions
 * and removals (2nd pattern) rehash will occur every {@code IRP}
 * insert-remove pairs, i. e. {@code R(IRP) + S = LF * C}.
 *
 * <p>Let {@code K = IRP / S}, then<br/>
 * {@code S = C * (K + LF + 1 - K * LF - sqrt((K * LF - K - LF - 1) ^ 2 - 4LF)) / 2}<br/>
 * if {@code K = 1} ( we want to rehash every {@code S} insertions and {@code S}
 * removals), {@code C = S / (1 - sqrt(1 - LF))}.
 *
 * <hr/>
 *
 * <p>Decided not to shrink automatically on removals, because
 * <ul>
 * <li>{@link java.util.HashMap}/{@code std::unordered_map} doesn't do this</li>
 * <li>{@link java.util.ArrayList}/{@code std::vector} doesn't do this</li>
 * </ul>
 * and for a good reason.
 *
 * <p>If someone want to leave in the hash only few
 * elements, he should create a new collection and pick selected elements
 * during iteration through the original one rather than remove the rest
 * elements from it.
 *
 * <p>However, adaptive rehash from the previous section causes compaction if
 * {@code S / (C - S) < (1 - sqrt(1 - LF)) / LF}.
 */
public abstract class MutableDHash extends AbstractContainer implements DHash {

    ////////////////////////////
    // Fields

    private HashConfig hashConfig;


    /** The current number of occupied slots in the hash. */
    private int size;


    /** The current number of free slots in the hash. */
    private int freeSlots;


    private int minFreeSlots;


    private int removedSlots;


    private int modCount = 0;


    /////////////////////////////
    // Getters

    @Override
    public final HashConfig hashConfig() {
        return hashConfig;
    }

    private float loadFactor() {
        return hashConfig().getLoadFactor();
    }

    @Override
    public final int size() {
        return size;
    }

    @Override
    public abstract int capacity();

    @Override
    public final boolean noRemoved() {
        return removedSlots == 0;
    }

    @Override
    public final int freeSlots() {
        return freeSlots;
    }

    @Override
    public final int removedSlots() {
        return removedSlots;
    }

    @Override
    public final int modCount() {
        return modCount;
    }

    final void incrementModCount() {
        modCount++;
    }

    @Override
    public final float currentLoad() {
        // Division in double to minimize precision loss
        return (float) (((double) (size + removedSlots)) / capacity());
    }


    ////////////////////////
    // Initialization and construction operations

    /**
     * Root operation for copy constructors
     *
     * @param hash Mutable or Immutable DHash instance
     */
    final void copy(DHash hash) {
        this.hashConfig = hash.hashConfig();
        this.size = hash.size();
        int freeSlots = this.freeSlots = hash.freeSlots();
        int minFreeSlots = this.minFreeSlots = max(1, (int) (hash.capacity() * (1 - loadFactor())));
        // see #initSlotCounts()
        if (freeSlots < minFreeSlots) this.minFreeSlots = (freeSlots + 1) >> 1;
        this.removedSlots = hash.removedSlots();
    }

    /**
     * Creates data structures with a prime capacity at or near the minimum
     * needed to hold {@code size} elements without triggering a rehash.
     *
     * <p>Should be called only in constructors and externalization code.
     * If {@code justExpected} is false, MutableDHash setups itself as if there are
     * already {@code size} elements in the hash (useful for externalization).
     */
    final void init(HashConfig hashConfig, int size) {
        this.hashConfig = hashConfig;
        this.size = 0;
        int capacity = bestCapacity(size, loadFactor(), 0);
        internalInit(capacity);
    }

    private void internalInit(int capacity) {
        initSlotCounts(capacity);
        allocateArrays(capacity);
    }

    /**
     * Allocates arrays of {@code capacity} size to hold states, elements, keys
     * or values in.
     *
     * <p>Subclasses should override this method, but SHOULD NOT call it. This
     * method is called in MutableDHash from initForRehash() and init() methods.
     *
     * @param capacity size of arrays, comprising the hash
     */
    abstract void allocateArrays(int capacity);

    private void initSlotCounts(int capacity) {
        int freeSlots = this.freeSlots = capacity - size;
        // Need at least one free slot for open addressing
        int minFreeSlots = this.minFreeSlots = max(1, (int) (capacity * (1 - loadFactor())));
        // free could be less than minFreeSlots only in case when capacity
        // is not sufficient to comply load factor (due to saturation with
        // Java array size limit). Set minFreeSlots to a half of free to avoid
        // too often (instant) rehashing in this case.
        if (freeSlots < minFreeSlots) this.minFreeSlots = (freeSlots + 1) / 2;
        removedSlots = 0;
    }

    /**
     * Moves elements to the new arrays of {@code newCapacity} size.
     *
     * <p>This method should be implemented as follows:
     *
     *  1. Copy references to the old arrays comprising the hash from fields
     *     to local variables
     *
     *  2. Call {@link #initForRehash(int)}
     *
     *  3. Move elements, entries, etc. from the old arrays to the new ones.
     *
     * <p>Subclasses should implement, but MUST NOT call this method. This method is called
     * in DHash from postInsertHooks, {@link #ensureCapacity(long)} and {@link #shrink()} methods.
     */
    abstract void rehash(int newCapacity);

    /**
     * This method just increments modification count (see {@link #modCount()})
     * and calls {@link #internalInit(int)}. Should be called by subclasses in
     * {@link #rehash(int)} implementation.
     */
    final void initForRehash(int newCapacity) {
        modCount++;
        internalInit(newCapacity);
    }

    
    //////////////////////////////
    // Roots of chain operations

    /**
     * Empties the hash.
     */
    public void clear() {
        modCount++;
        size = 0;
        freeSlots = capacity();
        removedSlots = 0;
    }

    
    abstract void removeAt( int index );
    

    /////////////////////////////
    // Modification hooks and rehash logic

    @Override
    public boolean shrink() {
        int newCapacity = bestCapacity(size, loadFactor(), size);
        if (removedSlots > 0 || newCapacity < capacity()) {
            rehash(newCapacity);
            return true;
        } else {
            return false;
        }
    }

    private boolean tryRehashForExpansion(long desiredSize) {
        int newCapacity = bestCapacity(desiredSize, loadFactor(), size);
        // No sense in rehashing for expansion if we already reached Java array
        // size limit.
        if (newCapacity > capacity() || removedSlots > 0) {
            rehash(newCapacity);
            return true;
        } else {
            if (freeSlots < minFreeSlots)
                minFreeSlots = (freeSlots + 1) / 2;
            return false;
        }
    }

    @Override
    public final boolean ensureCapacity(long minSize) {
        int intMinSize = (int) Math.min(minSize, Integer.MAX_VALUE);
        if (minSize < 0L)
            throw new IllegalArgumentException(
                    "Min size should be positive, " + minSize + " given.");
        int additionalSize = intMinSize - size;
        if (additionalSize <= 0)
            return false;
        int lowFreeEstimate;
        if (removedSlots == 0) {
            lowFreeEstimate = freeSlots - additionalSize;
        } else {
            int nonFull = freeSlots + removedSlots;
            float freeFraction =  ((float) freeSlots) / nonFull;
            // Precise free estimate = free * (nonFull - addSize) /
            //                        (nonFull - (1 - freeFraction) * addSize)
            // Because freeEstimate'(addSize) =
            //            - (freeEstimate(addSize) / (nonFull - addSize)) ^ 2
            //
            // This estimate is even lower:
            lowFreeEstimate = (int) ((nonFull - additionalSize) * freeFraction);
        }
        if (lowFreeEstimate < minFreeSlots) {
            tryRehashForExpansion(minSize);
            return true;
        } else {
            return false;
        }
    }

    final void postRemoveHook() {
        modCount++;
        size--;
        removedSlots++;
    }

    /**
     * After insertion, this hook is called to adjust the size/free
     * values of the hash and to perform rehashing if necessary.
     *
     * @param usedFreeSlot the slot
     */
    final void postInsertHook( boolean usedFreeSlot ) {
        modCount++;
        size++;
        if ( usedFreeSlot ) {
            if ( --freeSlots < minFreeSlots) {
                if (!tryRehashIfTooFewFreeSlots() && freeSlots == 0) {
                    throw new HashOverflowException();
                }
            }
        } else {
            removedSlots--;
        }
    }

    final void postFreeSlotInsertHook() {
        modCount++;
        size++;
        if ( --freeSlots < minFreeSlots) {
            if (!tryRehashIfTooFewFreeSlots() && freeSlots == 0) {
                throw new HashOverflowException();
            }
        }
    }

    final void postRemovedSlotInsertHook() {
        modCount++;
        size++;
        removedSlots--;
    }

    /**
     * See <a href="#rehash-logic">Rehash logic</a>.
     */
    private boolean tryRehashIfTooFewFreeSlots() {
        if ( removedSlots > 0 ) {
            double k = 1 - Math.sqrt(1 - loadFactor());
            rehash(bestCapacity(size, k, size));
            return true;
        } else {
            return tryRehashForExpansion(size * 2L); // 2L to prevent overflow
        }
    }
}