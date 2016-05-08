/*
 * Copyright (C) 2008 The Guava Authors
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

package com.koloboke.collect.testing;

import com.google.common.collect.Lists;
import com.google.common.collect.testing.Helpers;
import junit.framework.AssertionFailedError;
import com.koloboke.collect.Cursor;
import com.koloboke.collect.ObjCursor;
import com.koloboke.function.Consumer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;


/**
 * @param <E> cursor element (Map.Entry if map cursor)
 * @param <C> cursor type
 * @see com.google.common.collect.testing.AbstractIteratorTester
 */
public abstract class AbstractCursorTester<E, C extends Cursor> {

    private Stimulus<E, ? super C>[] stimuli;
    private final Set<CursorFeature> features;
    private final List<E> expectedElements;
    private final int startIndex;
    private final CursorKnownOrder knownOrder;

    private boolean isPermitted(Set<Class<? extends RuntimeException>> possibleExceptionClasses,
            RuntimeException exception) {
        for (Class<? extends RuntimeException> clazz : possibleExceptionClasses) {
            if (clazz.isInstance(exception)) {
                return true;
            }
        }
        return false;
    }

    void assertPermitted(Set<Class<? extends RuntimeException>> possibleExceptionClasses,
            RuntimeException exception) {
        if (!isPermitted(possibleExceptionClasses, exception)) {
            // TODO: use simple class names
            String message = "Exception " + exception.getClass()
                    + " was thrown; expected one of " + possibleExceptionClasses;
            Util.fail(exception, message);
        }
    }

    private static final class UnknownElementException extends RuntimeException {
        private UnknownElementException(Collection<?> expected, Object actual) {
            super("Returned value '"
                    + actual + "' not found. Remaining elements: " + expected);
        }

        private static final long serialVersionUID = 0L;
    }

    protected final class MultiExceptionCursor implements ObjCursor<E> {

        // TODO: track seen elements when order isn't guaranteed
        // TODO: verify contents afterward
        // TODO: something shiny and new instead of Stack
        // TODO: test whether null is supported (create a Feature)
        final Stack<E> nextElements = new Stack<>();
        final Stack<E> previousElements = new Stack<>();

        private final Object NO_CURRENT = new Object();
        Object currentElement = NO_CURRENT;

        private final Set<Class<? extends RuntimeException>> ONLY_ILLEGAL_STATE =
                new HashSet<>(asList(IllegalStateException.class));

        private final Set<Class<? extends RuntimeException>> ONLY_UNSUPPORTED_OP =
                new HashSet<>(asList(UnsupportedOperationException.class));

        private final Set<Class<? extends RuntimeException>> UNSUPPORTED_OP_AND_ILLEGAL_STATE =
                new HashSet<Class<? extends RuntimeException>>(
                        asList(UnsupportedOperationException.class, IllegalStateException.class));

        public Set<Class<? extends RuntimeException>> exceptions = null;

        MultiExceptionCursor(List<E> expectedElements) {
            nextElements.addAll(Lists.reverse(expectedElements));
            for (int i = 0; i < startIndex; i++) {
                previousElements.push(nextElements.pop());
            }
        }

        @Override
        public boolean moveNext() {
            return transferElement(nextElements, previousElements);
        }

        @SuppressWarnings("unchecked")
        @Override
        public E elem() {
            exceptions = null;
            if (currentElement == NO_CURRENT) {
                exceptions = ONLY_ILLEGAL_STATE;
                return null;
            }
            return (E) currentElement;
        }

        @Override
        public void remove() {
            exceptions = null;
            if (!features.contains(CursorFeature.SUPPORTS_REMOVE))
                exceptions = ONLY_UNSUPPORTED_OP;
            if (currentElement == NO_CURRENT)
                exceptions = UNSUPPORTED_OP_AND_ILLEGAL_STATE;
            if (exceptions != null)
                return;

            currentElement = NO_CURRENT;
        }

        @Override
        public void forEachForward(Consumer<? super E> action) {
            exceptions = null;
            while (moveNext()) {
                E elem = elem();
                if (exceptions != null)
                    return;
                action.accept(elem);
            }
        }

        /**
         * Moves the given element from its current position in {@link #nextElements} to the top
         * of the stack. If the element is not in {@link #nextElements}, this method throws
         * an {@link UnknownElementException}.
         *
         * <p>This method is used when testing cursors without a known ordering.
         * We poll the target cursor's next element and pass it to the reference
         * iterator through this method so it can return the same element. This
         * enables the assertion to pass and the reference iterator to properly update its state.
         */
        void promoteToNext(E e) {
            if (nextElements.remove(e)) {
                nextElements.push(e);
            } else {
                throw new UnknownElementException(nextElements, e);
            }
        }

        private boolean transferElement(Stack<E> source, Stack<E> destination) {
            if (currentElement != NO_CURRENT)
                destination.push(elem());
            if (source.isEmpty()) {
                currentElement = NO_CURRENT;
                return false;
            } else {
                currentElement = source.pop();
                return true;
            }
        }

        private List<E> getElements() {
            List<E> elements = new ArrayList<>();
            elements.addAll(previousElements);
            if (currentElement != NO_CURRENT)
                elements.add(elem());
            elements.addAll(Lists.reverse(nextElements));
            return elements;
        }
    }

    @SuppressWarnings("unchecked") // creating array of generic class Stimulus
    protected AbstractCursorTester(int steps, Iterable<CursorFeature> features,
            Iterable<E> expectedElements, CursorKnownOrder knownOrder, int startIndex,
            boolean mapCursor) {
        stimuli = new Stimulus[steps];
        this.features = Helpers.copyToSet(features);
        this.expectedElements = Helpers.copyToList(expectedElements);
        this.knownOrder = knownOrder;
        this.startIndex = startIndex;
        current = new Stimulus<E, Cursor>(mapCursor ? "key, value" : "elem") {
            @Override
            void executeAndCompare(ObjCursor<E> reference, Cursor target) {
                internalExecuteAndCompare(reference, (C) target, CURRENT_METHOD);
            }
        };
    }

    /**
     * I'd like to make this a parameter to the constructor, but I can't because
     * the stimulus instances refer to {@code this}.
     */
    Iterable<? extends Stimulus<E, ? super C>> getStimulusValues() {
        return asList(moveNext, remove, forEachForward, current);
    }

    /**
     * Returns a new target cursor each time it's called. This is the cursor you are trying to test.
     * This must return an Cursor that returns the expected elements passed to the constructor
     * in the given order. Warning: it is not enough to simply pull multiple iterators
     * from the same source Collection, unless that Cursor is unmodifiable.
     */
    protected abstract C newTargetCursor();

    /**
     * Get current element from the cursor. If the tested cursor belongs to Collection,
     * the implementation should simply be {@code cursor.elem()} call, otherwise if it belongs
     * to Map, it should create an entry from {@code cursor.key()} and {@code cursor.value()}.
     *
     * @param cursor the tested cursor
     * @return the iteration element
     */
    protected abstract E current(C cursor);

    /**
     * Perform forEachForward operation over the given cursor,
     * creating an entry from {@code cursor.key()} and {@code cursor.value()} if neccessary.
     * @param cursor the tested cursor
     * @param action the action to perform over the cursor
     */
    protected abstract void forEachForward(C cursor, Consumer<? super E> action);

    /**
     * Override this to verify anything after running a list of Stimuli.
     *
     * <p>For example, verify that calls to remove() actually removed
     * the correct elements.
     *
     * @param elements the expected elements passed to the constructor,
     *     as mutated by {@code remove()} calls
     */
    @SuppressWarnings("unused")
    protected void verify(List<E> elements) {}

    /**
     * Executes the test.
     */
    public final void test() {
        try {
            recurse(0);
        } catch (RuntimeException e) {
            throw new RuntimeException(Arrays.toString(stimuli), e);
        }
    }

    private void recurse(int level) {
        // We're going to reuse the stimuli array n^steps times by overwriting it
        // in a recursive loop.  Sneaky.
        if (level == stimuli.length) {
            // We've filled the array.
            compareResultsForThisListOfStimuli();
        } else {
            // Keep recursing to fill the array.
            for (Stimulus<E, ? super C> stimulus : getStimulusValues()) {
                stimuli[level] = stimulus;
                recurse(level + 1);
            }
        }
    }

    private void compareResultsForThisListOfStimuli() {
        MultiExceptionCursor reference =
                new MultiExceptionCursor(expectedElements);
        C target = newTargetCursor();
        for (int i = 0; i < stimuli.length; i++) {
            Stimulus<E, ? super C> stimulus = stimuli[i];
            try {
                stimulus.executeAndCompare(reference, target);
                List<E> elements = reference.getElements();
                verify(elements);
            } catch (AssertionFailedError cause) {
                Util.fail(cause, "failed with stimuli " + subListCopy(stimuli, i + 1));
            }
        }
    }

    private static List<Object> subListCopy(Object[] source, int size) {
        final Object[] copy = new Object[size];
        System.arraycopy(source, 0, copy, 0, size);
        return asList(copy);
    }

    private interface CursorOperation {
        Object execute(Cursor cursor);
    }

    /**
     * Apply this method to both cursors and return normally only if both
     * produce the same response.
     *
     * @see Stimulus#executeAndCompare(ObjCursor, Cursor)
     */
    private void internalExecuteAndCompare(
            Cursor reference, C target, CursorOperation method) throws AssertionFailedError {

        Object referenceReturnValue = null;
        Set<Class<? extends RuntimeException>> possibleExceptionClasses = null;
        Object targetReturnValue = null;
        RuntimeException targetException = null;

        try {
            targetReturnValue = method.execute(target);
        } catch (RuntimeException e) {
            targetException = e;
        }

        try {
            if (method == MOVE_NEXT_METHOD && targetException == null &&
                    knownOrder == CursorKnownOrder.UNKNOWN_ORDER) {
                try {
                    ((MultiExceptionCursor) reference).promoteToNext(current(target));
                } catch (IllegalStateException e) {
                    // do nothing, reference should also throw IllegalStateException,
                    // this will be checked in the subsequent
                    // internalExecuteAndCompare(reference, target, CURRENT_METHOD) call
                }
            }

            if (method == FOR_EACH_FORWARD_METHOD && targetException == null &&
                    knownOrder == CursorKnownOrder.UNKNOWN_ORDER) {
                List<E> elements = (List<E>) targetReturnValue;
                Lists.reverse(elements).forEach(((MultiExceptionCursor) reference)::promoteToNext);
            }

            referenceReturnValue = method.execute(reference);
            if (referenceReturnValue instanceof Set)
                possibleExceptionClasses =
                        (Set<Class<? extends RuntimeException>>) referenceReturnValue;
        } catch (UnknownElementException e) {
            Util.fail(e, e.getMessage());
        }

        if (possibleExceptionClasses == null) {
            if (targetException != null) {
                Util.fail(targetException, "Target threw exception when reference did not");
            }

            assertEquals(referenceReturnValue, targetReturnValue);
            return;
        }

        if (targetException == null) {
            fail("Target failed to throw one of " + possibleExceptionClasses);
        }

        assertPermitted(possibleExceptionClasses, targetException);
    }

    @SuppressWarnings("unchecked")
    private final CursorOperation CURRENT_METHOD = cursor -> {
        if (MultiExceptionCursor.class.isInstance(cursor)) {
            MultiExceptionCursor referenceCursor = (MultiExceptionCursor) cursor;
            E elem = referenceCursor.elem();
            if (referenceCursor.exceptions != null)
                return referenceCursor.exceptions;
            return elem;
        } else {
            return current((C) cursor);
        }
    };

    @SuppressWarnings("unchecked")
    private final CursorOperation FOR_EACH_FORWARD_METHOD = cursor -> {
        List<E> elements = new ArrayList<>();
        if (MultiExceptionCursor.class.isInstance(cursor)) {
            MultiExceptionCursor referenceCursor = (MultiExceptionCursor) cursor;
            referenceCursor.forEachForward(elements::add);
            if (referenceCursor.exceptions != null)
                return referenceCursor.exceptions;
        } else {
            forEachForward((C) cursor, elements::add);
        }
        return elements;
    };

    @SuppressWarnings("unchecked")
    private final CursorOperation REMOVE_METHOD = cursor -> {
        cursor.remove();
        if (MultiExceptionCursor.class.isInstance(cursor)) {
            MultiExceptionCursor referenceCursor = (MultiExceptionCursor) cursor;
            if (referenceCursor.exceptions != null)
                return referenceCursor.exceptions;
        }
        return null;
    };

    private static final CursorOperation MOVE_NEXT_METHOD = Cursor::moveNext;

    abstract static class Stimulus<E, C extends Cursor> {
        private final String toString;

        protected Stimulus(String toString) {
            this.toString = toString;
        }

        /**
         * Send this stimulus to both cursors and return normally only if both
         * produce the same response.
         */
        abstract void executeAndCompare(ObjCursor<E> reference, C target);

        @Override public String toString() {
            return toString;
        }
    }

    Stimulus<E, Cursor> moveNext = new Stimulus<E, Cursor>("moveNext") {
        @Override
        void executeAndCompare(ObjCursor<E> reference, Cursor target) {
            internalExecuteAndCompare(reference, (C) target, MOVE_NEXT_METHOD);
        }
    };

    Stimulus<E, Cursor> current;

    Stimulus<E, Cursor> remove = new Stimulus<E, Cursor>("remove") {
        @Override
        void executeAndCompare(ObjCursor<E> reference, Cursor target) {
            internalExecuteAndCompare(reference, (C) target, REMOVE_METHOD);
        }
    };

    Stimulus<E, Cursor> forEachForward = new Stimulus<E, Cursor>("forEachForward") {
        @Override
        void executeAndCompare(ObjCursor<E> reference, Cursor target) {
            internalExecuteAndCompare(reference, (C) target, FOR_EACH_FORWARD_METHOD);
        }
    };
}

