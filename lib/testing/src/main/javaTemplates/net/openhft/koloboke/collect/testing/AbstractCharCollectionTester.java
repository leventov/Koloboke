/* with char|byte|short|int|long|float|double|obj elem */
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

package net.openhft.koloboke.collect.testing;

import com.google.common.collect.testing.AbstractCollectionTester;
import net.openhft.koloboke.collect.CharCollection;
import net.openhft.koloboke.collect.set.hash.HashCharSets;

import java.util.ArrayList;
import java.util.Collection;


public abstract class AbstractCharCollectionTester/*<>*/
        extends AbstractCollectionTester<Character> {
    protected CharCollection/*<>*/ c() {
        return (CharCollection) collection;
    }

    /* if obj|float|double elem */
    protected char special() {
        return Specials.getChar();
    }

    /**
     * @return an array of the proper size with {@code null} inserted into the
     * middle element.
     */
    protected Character[] createArrayWithSpecialElement() {
        Character[] array = createSamplesArray();
        array[getNullLocation()] = special();
        return array;
    }

    protected void initCollectionWithSpecialElement() {
        Object[] array = createArrayWithSpecialElement();
        resetContainer(getSubjectGenerator().create(array));
    }

    protected int getSpecialLocation() {
        return getNullLocation();
    }
    /* endif */

    protected void remove() {
        c()./* if !(obj elem) */removeChar/* elif obj elem //remove// endif */(samples.e0);
    }

    protected Collection<Character> noRemoved(Collection<Character> elements) {
        return elements;
    }

    protected Collection<Character> someRemoved(Collection<Character> elements) {
        elements = new ArrayList<>(elements);
        elements.remove(samples.e0);
        return elements;
    }

    protected Collection<Character> simple(Collection<Character> c) {
        return c;
    }

    protected Collection<Character> specialized(Collection<Character> c) {
        return HashCharSets.newImmutableSet(c);
    }
}
