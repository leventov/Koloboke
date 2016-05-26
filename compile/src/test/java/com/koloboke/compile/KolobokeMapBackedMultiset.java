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

package com.koloboke.compile;

import com.google.common.base.Predicate;
import com.google.common.collect.*;
import com.google.common.primitives.Ints;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Predicates.in;
import static com.google.common.base.Predicates.not;


@ParametersAreNonnullByDefault
@KolobokeMap
@NullKeyAllowed
public abstract class KolobokeMapBackedMultiset<E> extends AbstractCollection<E>
        implements Multiset<E> {

    /**
     * Parts of this class are copied from Guava.
     *
     * <p>Implementation preserves and relies on the invariant -- keys never map to 0 (such
     * entries must be removed immediately).
     */

    public static <E> Multiset<E> withExpectedDistinctElements(int expectedDistinctElements) {
        return new KolobokeKolobokeMapBackedMultiset<E>(expectedDistinctElements);
    }

    public static <E> Multiset<E> withElements(E... elements) {
        Multiset<E> multiset = withExpectedDistinctElements(elements.length);
        for (E e : elements) {
            multiset.add(e);
        }
        return multiset;
    }

    private long size = 0L;

    abstract int modCount();

    @MethodForm("getInt")
    @Override
    public abstract int count(@Nullable Object element);

    abstract int put(E element, int count);

    abstract boolean replace(E element, int oldCount, int newCount);

    abstract int insert(@Nullable E element, int count);

    abstract int index(@Nullable Object element);

    abstract int[] valueArray();

    abstract void removeAt(int index);

    @MethodForm("size")
    abstract int mapSize();

    @MethodForm("keySet")
    abstract Set<E> mapKeySet();

    @MethodForm("entrySet")
    abstract Set<Map.Entry<E, Integer>> mapEntrySet();

    @MethodForm("removeAsInt")
    abstract int mapRemove(Object element);

    @MethodForm("remove")
    abstract boolean mapRemove(Object element, int count);

    @MethodForm("clear")
    abstract void mapClear();

    abstract boolean containsKey(@Nullable Object element);

    @MethodForm("contains")
    abstract boolean setContains(@Nullable Object element);

    @MethodForm("iterator")
    abstract Iterator<E> keyIterator();

    @MethodForm("toArray")
    abstract <T> T[] setToArray(T[] a);

    @MethodForm("toArray")
    abstract Object[] setToArray();

    abstract boolean containsEntry(Object element, int count);

    private static void checkOccurrences(int occurrences) {
        if (occurrences < 0)
            throw new IllegalArgumentException("occurrences < 0: " + occurrences);
    }

    @Override
    public final int add(@Nullable E element, int occurrences) {
        checkOccurrences(occurrences);
        if (occurrences == 0)
            return count(element);
        int index = insert(element, occurrences);
        if (index >= 0) {
            int[] values = valueArray();
            int prevOccurrences = values[index];
            long newOccurrences = (long) prevOccurrences + (long) occurrences;
            if (newOccurrences > (long) Integer.MAX_VALUE) {
                throw new IllegalArgumentException(
                        element + " occurrences overflow: " + newOccurrences);
            }
            values[index] = (int) newOccurrences;
            size += (long) occurrences;
            return prevOccurrences;
        } else {
            size += (long) occurrences;
            return 0;
        }
    }

    @Override
    public final int remove(@Nullable Object element, int occurrences) {
        checkOccurrences(occurrences);
        if (occurrences == 0)
            return count(element);
        int index = index(element);
        if (index >= 0) {
            int[] values = valueArray();
            int prevOccurrences = values[index];
            int newOccurrences = prevOccurrences - occurrences;
            if (newOccurrences > 0) {
                values[index] = newOccurrences;
                size -= (long) occurrences;
            } else {
                removeAt(index);
                size -= (long) prevOccurrences;
            }
            return prevOccurrences;
        } else {
            return 0;
        }
    }

    /**
     * Override justRemove() that is used by keySet().remove(), keySet().removeAll(),
     * keySet().retainAll(), to update multimap's size
     */
    final boolean justRemove(Object element) {
        return remove(element, Integer.MAX_VALUE) > 0;
    }

    @Override
    public final int setCount(E element, int count) {
        checkOccurrences(count);
        int prevCount = count > 0 ? put(element, count) : mapRemove(element);
        size += (long) (count - prevCount);
        return prevCount;
    }

    @Override
    public final boolean setCount(E element, int oldCount, int newCount) {
        checkOccurrences(oldCount);
        checkOccurrences(newCount);
        if (oldCount == newCount) {
            if (oldCount > 0) {
                return containsEntry(element, oldCount);
            } else {
                return !containsKey(element);
            }
        }
        boolean countChanged;
        if (oldCount > 0) {
            countChanged = newCount > 0 ? replace(element, oldCount, newCount) :
                    mapRemove(element, oldCount);
        } else {
            countChanged = insert(element, newCount) < 0;
        }
        if (countChanged)
            size += (long) (newCount - oldCount);
        return countChanged;
    }

    @Override
    public final Set<E> elementSet() {
        final Set<E> mapKeySet = mapKeySet();
        return new ForwardingSet<E>() {
            @Override
            protected Set<E> delegate() {
                return mapKeySet;
            }

            @Override
            public Iterator<E> iterator() {
                final Iterator<Map.Entry<E, Integer>> mapEntrySetIterator =
                        mapEntrySet().iterator();
                return new Iterator<E>() {
                    Map.Entry<E, Integer> toRemove;

                    @Override
                    public boolean hasNext() {
                        return mapEntrySetIterator.hasNext();
                    }

                    @Override
                    public E next() {
                        toRemove = mapEntrySetIterator.next();
                        return toRemove.getKey();
                    }

                    @Override
                    public void remove() {
                        checkRemove(toRemove != null);
                        size -= toRemove.getValue();
                        mapEntrySetIterator.remove();
                        toRemove = null;
                    }
                };
            }

            @SuppressFBWarnings("IA_AMBIGUOUS_INVOCATION_OF_INHERITED_OR_OUTER_METHOD")
            @Override
            public boolean removeAll(Collection<?> c) {
                int mc = modCount();
                Collection<?> collection = checkNotNull(c);
                checkNotNull(collection); // for GWT
                if (collection instanceof Multiset) {
                    collection = ((Multiset<?>) collection).elementSet();
                }
                boolean modified = false;
                /*
                 * AbstractSet.removeAll(List) has quadratic behavior if the list size
                 * is just less than the set's size.  We augment the test by
                 * assuming that sets have fast contains() performance, and other
                 * collections don't.  See
                 * http://code.google.com/p/guava-libraries/issues/detail?id=1013
                 */
                if (collection instanceof Set && collection.size() > size()) {
                    Iterator<E> removeFrom = iterator();
                    Predicate<? super E> predicate = in((Collection<? extends E>) collection);
                    checkNotNull(predicate);
                    while (removeFrom.hasNext()) {
                        if (predicate.apply(removeFrom.next())) {
                            removeFrom.remove();
                            mc++;
                            modified = true;
                        }
                    }
                } else {
                    for (Object e : collection) {
                        if (remove(e)) {
                            mc++;
                            modified = true;
                        }
                    }
                }
                if (mc != modCount())
                    throw new ConcurrentModificationException();
                return modified;
            }

            @Override
            public boolean retainAll(Collection<?> collection) {
                int mc = modCount();
                Iterator<E> removeFrom = this.iterator();
                Predicate<? super E> predicate = not(in(collection));
                checkNotNull(predicate);
                boolean modified = false;
                while (removeFrom.hasNext()) {
                    if (predicate.apply(removeFrom.next())) {
                        removeFrom.remove();
                        mc++;
                        modified = true;
                    }
                }
                if (mc != modCount())
                    throw new ConcurrentModificationException();
                return modified;
            }

            @Override
            public void clear() {
                KolobokeMapBackedMultiset.this.clear();
            }
        };
    }

    @Override
    public final Set<Entry<E>> entrySet() {
        return new EntrySet();
    }

    private class EntrySet extends AbstractSet<Multiset.Entry<E>> {
        @Override
        public boolean removeAll(Collection<?> c) {
            checkNotNull(c); // for GWT
            if (c instanceof Multiset) {
                c = ((Multiset<?>) c).elementSet();
            }
            /*
             * AbstractSet.removeAll(List) has quadratic behavior if the list size
             * is just less than the set's size.  We augment the test by
             * assuming that sets have fast contains() performance, and other
             * collections don't.  See
             * http://code.google.com/p/guava-libraries/issues/detail?id=1013
             */
            if (c instanceof Set && c.size() > this.size()) {
                return Iterators.removeAll(this.iterator(), c);
            } else {
                return removeAllImpl(this, c.iterator());
            }
        }

        /**
         * Remove each element in an iterable from a set.
         */
        boolean removeAllImpl(Set<?> set, Iterator<?> iterator) {
            boolean changed = false;
            while (iterator.hasNext()) {
                changed |= set.remove(iterator.next());
            }
            return changed;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            return super.retainAll(checkNotNull(c)); // GWT compatibility
        }

        @Override
        public boolean contains(@Nullable Object o) {
            if (o instanceof Entry) {
                /*
                 * The GWT compiler wrongly issues a warning here.
                 */
                @SuppressWarnings("cast")
                Entry<?> entry = (Entry<?>) o;
                if (entry.getCount() <= 0) {
                    return false;
                }
                int count = KolobokeMapBackedMultiset.this.count(entry.getElement());
                return count == entry.getCount();
            }
            return false;
        }

        // GWT compiler warning; see contains().
        @SuppressWarnings("cast")
        @Override
        public boolean remove(Object object) {
            if (object instanceof Multiset.Entry) {
                Entry<?> entry = (Entry<?>) object;
                Object element = entry.getElement();
                int entryCount = entry.getCount();
                if (entryCount != 0) {
                    // Safe as long as we never add a new entry, which we won't.
                    @SuppressWarnings("unchecked")
                    Multiset<Object> multiset = (Multiset) KolobokeMapBackedMultiset.this;
                    return multiset.setCount(element, entryCount, 0);
                }
            }
            return false;
        }

        @Override
        public void clear() {
            KolobokeMapBackedMultiset.this.clear();
        }

        @Override
        public int size() {
            return mapSize();
        }

        @Override
        public Iterator<Entry<E>> iterator() {
            return new EntryIterator();
        }
    }

    private class EntryIterator implements Iterator<Multiset.Entry<E>> {
        private final Iterator<Map.Entry<E, Integer>> backingEntries = mapEntrySet().iterator();
        private Map.Entry<E, Integer> toRemove;

        @Override
        public boolean hasNext() {
            return backingEntries.hasNext();
        }

        @Override
        public Multiset.Entry<E> next() {
            final Map.Entry<E, Integer> mapEntry = backingEntries.next();
            toRemove = mapEntry;
            return Multisets.immutableEntry(mapEntry.getKey(), mapEntry.getValue());
        }

        @Override
        public void remove() {
            checkRemove(toRemove != null);
            size -= toRemove.getValue();
            backingEntries.remove();
            toRemove = null;
        }
    }

    /**
     * Precondition tester for {@code Iterator.remove()} that throws an exception with a consistent
     * error message.
     */
    private static void checkRemove(boolean canRemove) {
        checkState(canRemove, "no calls to next() since the last call to remove()");
    }

    @Override
    public final Iterator<E> iterator() {
        return new MapBasedMultisetIterator();
    }

    private class MapBasedMultisetIterator implements Iterator<E> {
        final Iterator<Map.Entry<E, Integer>> entryIterator = mapEntrySet().iterator();
        private Map.Entry<E, Integer> currentEntry;
        private int occurrencesLeft;
        private boolean canRemove;

        @Override
        public boolean hasNext() {
            return occurrencesLeft > 0 || entryIterator.hasNext();
        }

        @Override
        public E next() {
            if (occurrencesLeft == 0) {
                currentEntry = entryIterator.next();
                occurrencesLeft = currentEntry.getValue();
            }
            occurrencesLeft--;
            canRemove = true;
            return currentEntry.getKey();
        }

        @Override
        public void remove() {
            checkRemove(canRemove);
            int index = index(currentEntry.getKey());
            if (index < 0)
                throw new ConcurrentModificationException();
            int[] values = valueArray();
            int currentCount = values[index];
            if (currentCount == 1) {
                entryIterator.remove();
            } else {
                values[index] = currentCount - 1;
            }
            size--;
            canRemove = false;
        }
    }

    @Override
    public final boolean contains(@Nullable Object element) {
        return containsKey(element);
    }

    @Override
    public final boolean add(E element) {
        add(element, 1);
        return true;
    }

    @Override
    public final boolean remove(@Nullable Object element) {
        return remove(element, 1) > 0;
    }

    @Override
    public final boolean removeAll(Collection<?> c) {
        Collection<?> collection =
                (c instanceof Multiset)
                        ? ((Multiset<?>) c).elementSet()
                        : c;
        return this.elementSet().removeAll(collection);
    }

    @Override
    public final boolean retainAll(Collection<?> c) {
        checkNotNull(c);
        Collection<?> collection =
                (c instanceof Multiset)
                        ? ((Multiset<?>) c).elementSet()
                        : c;

        return this.elementSet().retainAll(collection);
    }

    @Override
    public final int size() {
        return Ints.saturatedCast(size);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        if (c.isEmpty()) {
            return false;
        }
        if (c instanceof Multiset) {
            Multiset<? extends E> that = cast(c);
            for (Entry<? extends E> entry : that.entrySet()) {
                this.add(entry.getElement(), entry.getCount());
            }
        } else {
            Iterators.addAll(this, c.iterator());
        }
        return true;
    }

    /**
     * Used to avoid http://bugs.sun.com/view_bug.do?bug_id=6558557
     */
    static <T> Multiset<T> cast(Iterable<T> iterable) {
        return (Multiset<T>) iterable;
    }

    @Override
    public void clear() {
        mapClear();
        size = 0L;
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Multiset) {
            Multiset<?> that = (Multiset<?>) obj;
            /*
             * We can't simply check whether the entry sets are equal, since that
             * approach fails when a TreeMultiset has a comparator that returns 0
             * when passed unequal elements.
             */

            if (this.size() != that.size() || this.entrySet().size() != that.entrySet().size()) {
                return false;
            }
            for (Entry<?> entry : that.entrySet()) {
                if (this.count(entry.getElement()) != entry.getCount()) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return entrySet().hashCode();
    }

    @Override
    public String toString() {
        return entrySet().toString();
    }
}
