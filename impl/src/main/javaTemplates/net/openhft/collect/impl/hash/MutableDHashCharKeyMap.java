/* with
 char|byte|short|int|long|float|double|obj key
 Mutable|Immutable mutability
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

package net.openhft.collect.impl.hash;

import net.openhft.collect.*;
import net.openhft.function.*;
import net.openhft.collect.impl.*;
import net.openhft.collect.set.CharSet;
import net.openhft.collect.set.hash.HashCharSet;
import javax.annotation.Nonnull;

import java.util.*;


public abstract class MutableDHashCharKeyMap/*<>*/ extends MutableCharDHashGO/*<>*/ {

    /* if obj key */
    public Equivalence<Character> keyEquivalence() {
        return null;
    }
    /* endif */


    public final boolean containsKey(Object key) {
        return contains(key);
    }

    /* if !(obj key) */
    public boolean containsKey(char key) {
        return contains(key);
    }
    /* endif */


    @Nonnull
    public HashCharSet keySet() {
        return new KeyView();
    }


    abstract boolean justRemove(/* raw */char key);

    /* if float|double key */
    abstract boolean justRemove(/* bits */char key);
    /* endif */

    class KeyView extends AbstractCharKeyView/*<>*/
            implements HashCharSet/*<>*/, InternalCharCollectionOps/*<>*/, CharDHash {

        /* if obj key */
        /* if obj key //@Override// endif */
        public Equivalence<Character> equivalence() {
            return MutableDHashCharKeyMap.this.keyEquivalence();
        }
        /* endif */

        @Override
        public float loadFactor() {
            return MutableDHashCharKeyMap.this.loadFactor();
        }

        @Override
        public int size() {
            return MutableDHashCharKeyMap.this.size();
        }

        @Override
        public float currentLoad() {
            return MutableDHashCharKeyMap.this.currentLoad();
        }

        /* if !(obj|float|double key) */
        @Override
        public char freeValue() {
            return MutableDHashCharKeyMap.this.freeValue();
        }

        @Override
        public boolean supportRemoved() {
            return MutableDHashCharKeyMap.this.supportRemoved();
        }

        @Override
        public char removedValue() {
            return MutableDHashCharKeyMap.this.removedValue();
        }
        /* endif */

        @Nonnull
        @Override
        public /* bits *//* raw */char[] keys() {
            return MutableDHashCharKeyMap.this.keys();
        }

        @Override
        public int capacity() {
            return MutableDHashCharKeyMap.this.capacity();
        }

        @Override
        public int freeSlots() {
            return MutableDHashCharKeyMap.this.freeSlots();
        }

        @Override
        public boolean noRemoved() {
            return MutableDHashCharKeyMap.this.noRemoved();
        }

        @Override
        public int removedSlots() {
            return MutableDHashCharKeyMap.this.removedSlots();
        }

        @Override
        public int modCount() {
            return MutableDHashCharKeyMap.this.modCount();
        }

        @Override
        public final boolean contains(Object o) {
            return MutableDHashCharKeyMap.this.contains(o);
        }

        /* if !(obj key) */
        @Override
        public boolean contains(char key) {
            return MutableDHashCharKeyMap.this.contains(key);
        }

        /* if float|double key */
        @Override
        public boolean contains(/* bits */char bits) {
            return MutableDHashCharKeyMap.this.contains(bits);
        }
        /* endif */
        /* endif */


        /* if obj key || JDK8 jdk //@Override// endif */
        public void forEach( Consumer<? super Character> action ) {
            MutableDHashCharKeyMap.this.forEach(action);
        }

        /* if !(obj key) */
        @Override
        public void forEach( CharConsumer action ) {
            MutableDHashCharKeyMap.this.forEach(action);
        }
        /* endif */

        @Override
        public boolean forEachWhile(
                /* if !(obj key) */CharPredicate/* elif obj key //Predicate// endif *//*<super>*/
                predicate) {
            return MutableDHashCharKeyMap.this.forEachWhile(predicate);
        }

        @Override
        public boolean allContainingIn(CharCollection/*<?>*/ c) {
            return MutableDHashCharKeyMap.this.allContainingIn(c);
        }

        @Override
        public boolean reverseAddAllTo(CharCollection/*<super>*/ c) {
            return MutableDHashCharKeyMap.this.reverseAddAllTo(c);
        }

        @Override
        public boolean reverseRemoveAllFrom(CharSet/*<?>*/ s) {
            return MutableDHashCharKeyMap.this.reverseRemoveAllFrom(s);
        }

        @Override
        @Nonnull
        public CharIterator/*<>*/ iterator() {
            return MutableDHashCharKeyMap.this.iterator();
        }

        @Override
        @Nonnull
        public CharCursor/*<>*/ cursor() {
            return setCursor();
        }

        @Override
        @Nonnull
        public Object[] toArray() {
            return MutableDHashCharKeyMap.this.toArray();
        }

        @Override
        @Nonnull
        public <T> T[] toArray(@Nonnull T[] a) {
            return MutableDHashCharKeyMap.this.toArray(a);
        }

        /* if !(obj key) */
        @Override
        public char[] toCharArray() {
            return MutableDHashCharKeyMap.this.toCharArray();
        }

        @Override
        public char[] toArray(char[] a) {
            return MutableDHashCharKeyMap.this.toArray(a);
        }
        /* endif */


        @Override
        public int hashCode() {
            return setHashCode();
        }

        @Override
        public String toString() {
            return setToString();
        }


        @Override
        public boolean shrink() {
            return MutableDHashCharKeyMap.this.shrink();
        }


        @Override
        public final boolean remove(Object o) {
            return justRemove(/* if !(obj key) */( Character ) /* endif */o);
        }

        /* if !(obj key) */
        @Override
        public boolean removeChar(char v) {
            return justRemove(v);
        }

        /* if float|double key */
        @Override
        public boolean removeChar(/* bits */char bits) {
            return justRemove(bits);
        }
        /* endif */
        /* endif */


        /* if obj key || JDK8 jdk //@Override// endif */
        public boolean removeIf(Predicate<? super Character> filter) {
            return MutableDHashCharKeyMap.this.removeIf(filter);
        }

        /* if !(obj key) */
        @Override
        public boolean removeIf(CharPredicate filter) {
            return MutableDHashCharKeyMap.this.removeIf(filter);
        }
        /* endif */

        @Override
        public boolean removeAll(@Nonnull Collection<?> c) {
            /* if !(obj key) */
            if (c instanceof CharCollection) {
            /* endif */
                if (c instanceof InternalCharCollectionOps) {
                    InternalCharCollectionOps c2 = (InternalCharCollectionOps) c;
                    if (c2.size() < this.size()/* if obj key //
                            && NullableObjects.equals(equivalence(), c2.equivalence())
                            // endif */) {
                        /* if obj key */// noinspection unchecked/* endif */
                        return c2.reverseRemoveAllFrom(this);
                    }
                }
            /* if !(obj key) */
                return MutableDHashCharKeyMap.this.removeAll((CharCollection) c);
            }
            /* endif */
            return MutableDHashCharKeyMap.this.removeAll(c);
        }

        @Override
        public boolean retainAll(@Nonnull Collection<?> c) {
            return MutableDHashCharKeyMap.this.retainAll(c);
        }

        @Override
        public void clear() {
            MutableDHashCharKeyMap.this.clear();
        }
    }
}
