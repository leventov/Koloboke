/* with
 DHash|QHash|LHash hash
 byte|char|short|int|long|float|double|obj elem
 Mutable|Updatable|Immutable mutability
 Separate|Parallel kv
 true|false concurrentModificationChecked
*/
/* if (Separate kv) || (Enabled parallelKV) */
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

package com.koloboke.collect.impl.hash;

import com.koloboke.collect.*;
import com.koloboke.collect.impl.InternalByteCollectionOps;
import com.koloboke.collect.set.hash.HashByteSet;
import com.koloboke.function./*f*/ByteConsumer/**/;
import com.koloboke.function./*f*/BytePredicate/**/;
import com.koloboke.function.Consumer;
import com.koloboke.function.Predicate;
import com.koloboke.collect.impl.NotGenerated;

import com.koloboke.collect.set.ByteSet;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import javax.annotation.Nonnull;

import java.util.*;


public abstract class MutableSeparateKVByteDHashGO/*<>*/
        extends MutableSeparateKVByteDHashSO/*<>*/ {

    /* if Separate kv */
    @Nonnull
    @Override
    public /* bits *//* raw */byte[] keys() {
        return set;
    }
    /* elif Parallel kv */
    @Nonnull
    @Override
    public /* if !(obj elem) */char/* elif obj elem //Object// endif */[] table() {
        return table;
    }
    /* endif */

    /* if Parallel kv long|double|obj elem && !(Immutable mutability) */
    @Override
    boolean doubleSizedArrays() {
        return true;
    }
    /* endif */

    @Override
    public int capacity() {
        /* if Separate kv */
        return set.length;
        /* elif Parallel kv */
        return table.length/* if long|double|obj elem */ >> 1/* endif */;
        /* endif */
    }

    public void forEach(Consumer<? super Byte> action) {
        /* template ForEach */
    }

    /* if !(object elem) */
    public void forEach(ByteConsumer action) {
        /* template ForEach */
    }
    /* endif */

    public boolean forEachWhile(
            /* if !(obj elem) */BytePredicate/*elif obj elem //Predicate// endif *//*<super>*/
            predicate) {
        /* template ForEachWhile */ throw new NotGenerated(); /* endtemplate */
    }

    public boolean allContainingIn(ByteCollection/*<?>*/ c) {
        /* template AllContainingIn */ throw new NotGenerated(); /* endtemplate */
    }

    /* if float|double elem */
    boolean allContainingIn(InternalByteCollectionOps c) {
        /* template AllContainingIn with internal version */throw new NotGenerated();/*endtemplate*/
    }
    /* endif */

    public boolean reverseAddAllTo(ByteCollection/*<super>*/ c) {
        /* template ReverseAddAllTo */ throw new NotGenerated(); /* endtemplate */
    }

    /* if float|double elem */
    boolean reverseAddAllTo(InternalByteCollectionOps c) {
        /* template ReverseAddAllTo with internal version */throw new NotGenerated();/*endtemplate*/
    }
    /* endif */

    public boolean reverseRemoveAllFrom(ByteSet/*<?>*/ s) {
        /* template ReverseRemoveAllFrom */ throw new NotGenerated(); /* endtemplate */
    }

    /* if float|double elem */
    boolean reverseRemoveAllFrom(InternalByteCollectionOps s) {
        /* template ReverseRemoveAllFrom with internal version */ throw new NotGenerated();
        /* endtemplate */
    }
    /* endif */


    public ByteIterator/*<>*/ iterator() {
        /* if true concurrentModificationChecked */int mc = modCount();/* endif */
        /* if Mutable mutability && !(LHash hash) //
        if (!noRemoved())
            return new SomeRemovedIterator(
                // if true concurrentModificationChecked //mc// endif //);
        // endif */
        return new NoRemovedIterator(/* if true concurrentModificationChecked */mc/* endif */);
    }

    public ByteCursor/*<>*/ setCursor() {
        /* if true concurrentModificationChecked */int mc = modCount();/* endif */
        /* if Mutable mutability && !(LHash hash) //
        if (!noRemoved())
            return new SomeRemovedCursor(// if true concurrentModificationChecked //mc// endif //);
        // endif */
        return new NoRemovedCursor(/* if true concurrentModificationChecked */mc/* endif */);
    }

    @Nonnull
    public Object[] toArray() {
        /* template ToArray with generic version */ throw new NotGenerated(); /* endtemplate */
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public <T> T[] toArray(@Nonnull T[] a) {
        /* template ToTypedArray */ throw new NotGenerated(); /* endtemplate */
    }

    /* if !(obj elem) */
    @Nonnull
    public byte[] toByteArray() {
        /* template ToArray */ throw new NotGenerated(); /* endtemplate */
    }

    @Nonnull
    public byte[] toArray(byte[] a) {
        /* template ToPrimitiveArray */ throw new NotGenerated(); /* endtemplate */
    }
    /* endif */


    public int setHashCode() {
        /* template SetHashCode */ throw new NotGenerated(); /* endtemplate */
    }

    /* if compile project Specific|BoundedGeneric keyType */
    @SuppressFBWarnings("EC_UNRELATED_TYPES_USING_POINTER_EQUALITY")/* endif */
    public String setToString() {
        /* template ToString */ throw new NotGenerated(); /* endtemplate */
    }


    abstract boolean justRemove(/* bits */byte key);

    public boolean removeIf(Predicate<? super Byte> filter) {
        /* template RemoveIf */ throw new NotGenerated(); /* endtemplate */
    }

    /* if !(obj elem) */
    public boolean removeIf(BytePredicate filter) {
        /* template RemoveIf */ throw new NotGenerated(); /* endtemplate */
    }
    /* endif */

    boolean removeAll(@Nonnull HashByteSet/*<>*/ thisC, @Nonnull Collection<?> c) {
        /* template RemoveAll with generic version given this */ throw new NotGenerated();
        /* endtemplate */
    }

    /* if !(obj elem) */
    boolean removeAll(@Nonnull HashByteSet/*<>*/ thisC, @Nonnull ByteCollection c) {
        /* template RemoveAll with given this */ throw new NotGenerated(); /* endtemplate */
    }

    /* if float|double elem */
    boolean removeAll(@Nonnull HashByteSet/*<>*/ thisC, @Nonnull InternalByteCollectionOps c) {
        /* template RemoveAll with internal version given this */ throw new NotGenerated();
        /* endtemplate */
    }
    /* endif */
    /* endif */

    boolean retainAll(@Nonnull HashByteSet/*<>*/ thisC, @Nonnull Collection<?> c) {
        /* if !(obj elem) */
        if (c instanceof ByteCollection)
            return retainAll(thisC, (ByteCollection) c);
        /* endif */
        /* template RetainAll with generic version given this */ throw new NotGenerated();
        /* endtemplate */
    }

    /* if !(obj elem) */
    private boolean retainAll(@Nonnull HashByteSet/*<>*/ thisC, @Nonnull ByteCollection c) {
        /* template RetainAll with given this */ throw new NotGenerated(); /* endtemplate */
    }

    /* if float|double elem */
    private boolean retainAll(@Nonnull HashByteSet/*<>*/ thisC,
            @Nonnull InternalByteCollectionOps c) {
        /* template RetainAll with internal version given this */ throw new NotGenerated();
        /* endtemplate */
    }
    /* endif */
    /* endif */

    /* if Mutable mutability LHash hash */
    void closeDelayedRemoved(int firstDelayedRemoved
            /* if !(obj|float|double elem) */, /* bits */byte delayedRemoved/* endif */) {
        /* template LHashCloseDelayedRemoved */ throw new NotGenerated(); /* endtemplate */
    }
    /* endif */


    /* with No|Some removed */
    /* if !(Updatable|Immutable mutability Some removed) && !(LHash hash Some removed) */

    class NoRemovedIterator implements ByteIterator/*<>*/ {
        /* template Iterator.fields */

        NoRemovedIterator(/* if true concurrentModificationChecked */int mc/* endif */) {
            /* template Iterator.constructor */
        }

        /* if !(obj elem) */
        @Override
        public byte nextByte() {
            /* template Iterator.next */ throw new NotGenerated(); /* endtemplate */
        }
        /* endif */

        /* if obj elem || JDK8 jdk //@Override// endif */
        public void forEachRemaining(Consumer<? super Byte> action) {
            /* template Iterator.forEachRemaining */ throw new NotGenerated(); /* endtemplate */
        }

        /* if !(obj elem) */
        @Override
        public void forEachRemaining(ByteConsumer action) {
            /* template Iterator.forEachRemaining */ throw new NotGenerated(); /* endtemplate */
        }
        /* endif */

        @Override
        public boolean hasNext() {
            /* template Iterator.hasNext */ throw new NotGenerated(); /* endtemplate */
        }

        @Override
        public Byte next() {
            /* if !(obj elem) */
            return nextByte();
            /* elif obj elem */
            /* template Iterator.next */
            /* endif */
        }

        @Override
        public void remove() {
            /* template Iterator.remove */ throw new NotGenerated(); /* endtemplate */
        }
    }


    class NoRemovedCursor implements ByteCursor/*<>*/ {
        /* template Cursor.fields */

        NoRemovedCursor(/* if true concurrentModificationChecked */int mc/* endif */) {
            /* template Cursor.constructor */
        }

        @Override
        public void forEachForward(/*f*/ByteConsumer action) {
            /* template Cursor.forEachForward */ throw new NotGenerated(); /* endtemplate */
        }

        @Override
        public byte elem() {
            /* template Cursor.elem */ throw new NotGenerated(); /* endtemplate */
        }

        @Override
        public boolean moveNext() {
            /* template Cursor.moveNext */ throw new NotGenerated(); /* endtemplate */
        }

        @Override
        public void remove() {
            /* template Cursor.remove */ throw new NotGenerated(); /* endtemplate */
        }
    }

    /* endif */
    /* endwith */
}
