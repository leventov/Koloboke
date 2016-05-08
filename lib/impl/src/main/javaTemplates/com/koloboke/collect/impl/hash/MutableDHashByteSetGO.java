/* with
 DHash|QHash|LHash hash
 byte|char|short|int|long|float|double|obj elem
 Mutable|Updatable|Immutable mutability
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

package com.koloboke.collect.impl.hash;

import com.koloboke.collect.*;
import com.koloboke.collect.impl.*;
import com.koloboke.collect.set.ByteSet;
import com.koloboke.collect.set.hash.HashByteSet;
import com.koloboke.function./*f*/ByteConsumer/**/;
import com.koloboke.function./*f*/BytePredicate/**/;

import javax.annotation.Nonnull;

import java.util.*;


public class MutableDHashByteSetGO/*<>*/ extends MutableByteDHashSetSO/*<>*/
        implements HashByteSet/*<>*/, InternalByteCollectionOps/*<>*/ {

    @Override
    final void copy(SeparateKVByteDHash hash) {
        int myMC = modCount(), hashMC = hash.modCount();
        super.copy(hash);
        if (myMC != modCount() || hashMC != hash.modCount())
            throw new ConcurrentModificationException();
    }

    @Override
    final void move(SeparateKVByteDHash hash) {
        int myMC = modCount(), hashMC = hash.modCount();
        super.move(hash);
        if (myMC != modCount() || hashMC != hash.modCount())
            throw new ConcurrentModificationException();
    }

    public int hashCode() {
        return setHashCode();
    }

    @Override
    public String toString() {
        return setToString();
    }

    @Override
    public boolean equals(Object obj) {
        /* if impl project */
        return CommonSetOps.equals(this, obj);
        /* elif compile project */
        if (set == obj)
            return true;
        if (!(obj instanceof Set))
            return false;
        Set<?> another = (Set<?>) obj;
        if (another.size() != this.size())
            return false;
        try {
            return containsAll(another);
        } catch (ClassCastException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
        /* endif */
    }

    /* if compile project */
    /**
     * This method is needed because of problems with comparing this with other collection, when
     * they have incompatible types (if set interface/class annotated with @KolobokeSet doesn't
     * extend java.util.Set). If just if ((Object) this == c), redundant (Object) cast is omitted
     * by JDT compiler or Spoon(?)
     */
    private static boolean identical(Object a, Object b) {
        return a == b;
    }
    /* endif */

    @Override
    public boolean containsAll(@Nonnull Collection<?> c) {
        /* if impl project */
        return CommonByteCollectionOps.containsAll(this, c);
        /* elif compile project */
        if (identical(this, c))
            return true;
        if (c instanceof ByteCollection) {
            ByteCollection c2 = (ByteCollection) c;
            /* if obj elem */
            if (this.equivalence().equals(c2.equivalence())) {
            /* endif */
                if (ByteSet.class.isAssignableFrom(getClass()) && c2 instanceof ByteSet &&
                        this.size() < c.size()) {
                    return false;
                }
                if (ByteCollection.class.isAssignableFrom(getClass()) &&
                        c2 instanceof InternalByteCollectionOps) {
                    // noinspection unchecked
                    return ((InternalByteCollectionOps) c2).allContainingIn(
                            (ByteCollection/*<?>*/) ByteCollection.class.cast(this));
                }
            /* if obj elem */
            }
            // noinspection unchecked
            /* endif */
            return c2.forEachWhile(new
                    /*f*/BytePredicate/**/() {
                @Override
                public boolean test(/* raw */byte value) {
                    return contains(value);
                }
            });
        } else {
            for (Object o : c) {
                if (!this.contains(/* if !(obj elem) */((Byte) o).byteValue()
                        /* elif obj elem //o// endif */))
                    return false;
            }
            return true;
        }
        /* endif */
    }

    @Nonnull
    @Override
    public ByteCursor/*<>*/ cursor() {
        return setCursor();
    }


    /* if !(obj elem) */
    @Override
    public boolean add(Byte e) {
        return add(e.byteValue());
    }
    /* endif */

    @Override
    public boolean add(/* bits */byte key) {
        /* template Add with internal version */ throw new NotGenerated(); /* endtemplate */
    }

    /* if float|double elem */
    @Override
    public boolean add(byte key) {
        return add(/* unwrap elem */key/**/);
    }
    /* elif obj elem */
    private boolean addNullKey() {
        /* template Add with null elem */ throw new NotGenerated(); /* endtemplate */
    }
    /* endif */

    @Override
    public boolean addAll(@Nonnull Collection<? extends Byte> c) {
        /* if impl project */
        return CommonByteCollectionOps.addAll(this, c);
        /* elif compile project */
        if (identical(this, c))
            throw new IllegalArgumentException();
        long maxPossibleSize = this.sizeAsLong() + Containers.sizeAsLong(c);
        this.ensureCapacity(maxPossibleSize);
        if (c instanceof ByteCollection) {
            if (ByteCollection.class.isAssignableFrom(getClass()) &&
                    c instanceof InternalByteCollectionOps) {
                return ((InternalByteCollectionOps) c).reverseAddAllTo(
                        (ByteCollection/*<super>*/) ByteCollection.class.cast(this));
            } else {
                class AddAll implements /*f*/ByteConsumer/*<>*/ {
                    boolean collectionChanged = false;
                    @Override
                    public void accept(byte value) {
                        collectionChanged |= add(value);
                    }
                }
                AddAll addAll = new AddAll();
                ((ByteCollection) c).forEach(addAll);
                return addAll.collectionChanged;
            }
        } else {
            boolean collectionChanged = false;
            for (Byte v : c) {
                collectionChanged |= this.add(v/* if !(obj elem) */.byteValue()/* endif */);
            }
            return collectionChanged;
        }
        /* endif */
    }

    @Override
    public boolean remove(Object key) {
        /* if !(obj elem) */
        return removeByte(((Byte) key).byteValue());
        /* elif obj elem //
        /* template Remove */
        // endif */
    }

    /* if obj elem */
    private boolean removeNullKey() {
        /* template Remove with null elem */ throw new NotGenerated(); /* endtemplate */
    }
    /* endif */

    @Override
    boolean justRemove(/* bits */byte key) {
        return /* if !(obj elem) */removeByte(key)/* elif obj elem //remove(key)// endif */;
    }

    /* if !(obj elem) */
    @Override
    public boolean removeByte(/* bits */byte key) {
        /* template Remove with internal version */ throw new NotGenerated(); /* endtemplate */
    }

    /* if float|double elem */
    @Override
    public boolean removeByte(byte key) {
        return removeByte(/* unwrap elem */key/**/);
    }
    /* endif */
    /* endif */


    @Override
    public boolean removeAll(@Nonnull Collection<?> c) {
        /* if Mutable mutability */
        /* if !(obj elem) */
        if (c instanceof ByteCollection) {
        /* endif */
            if (/* if compile project */
                    ByteSet.class.isAssignableFrom(getClass()) &&/* endif */
                    c instanceof InternalByteCollectionOps) {
                InternalByteCollectionOps c2 = (InternalByteCollectionOps) c;
                if (c2.size() < this.size()/* if obj elem //
                            && equivalence().equals(c2.equivalence())
                            // endif */) {
                    /* if obj elem */// noinspection unchecked/* endif */
                    return c2.reverseRemoveAllFrom(
                            /* if impl project //this
                            /* elif compile project */(ByteSet/*<?>*/)
                                    ByteSet.class.cast(this)
                                    /* endif */);
                }
            }
        /* if !(obj elem) */
            /* if impl project */
            return removeAll(this, (ByteCollection) c);
            /* elif compile project */
            /* template RemoveAll */ throw new NotGenerated(); /* endtemplate */
            /* endif */
        }
        /* endif */
        /* if impl project */
        return removeAll(this, c);
        /* elif compile project */
        /* template RemoveAll with generic version */ throw new NotGenerated();
        /* endtemplate */
        /* endif */
        /* elif !(Mutable mutability) //
        throw new UnsupportedOperationException();
        // endif */
    }

    /* if float|double elem compile project */
    boolean removeAll(@Nonnull InternalByteCollectionOps c) {
        /* template RemoveAll with internal version */ throw new NotGenerated();
        /* endtemplate */
    }
    /* endif */

    @Override
    public boolean retainAll(@Nonnull Collection<?> c) {
        /* if impl project */
        return retainAll(this, c);
        /* elif compile project */
        /* if !(obj elem) */
        if (c instanceof ByteCollection)
            return retainAll((ByteCollection) c);
        /* endif */
        /* template RetainAll with generic version */ throw new NotGenerated();
        /* endtemplate */
        /* endif */
    }

    /* if compile project */
    /* if !(obj elem) */
    private boolean retainAll(@Nonnull ByteCollection c) {
        /* template RetainAll */ throw new NotGenerated(); /* endtemplate */
    }

    /* if float|double elem */
    private boolean retainAll(@Nonnull InternalByteCollectionOps c) {
        /* template RetainAll with internal version */ throw new NotGenerated();
        /* endtemplate */
    }
    /* endif */
    /* endif */
    /* endif */
}