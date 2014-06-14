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

package net.openhft.collect;

import com.google.common.collect.testing.features.*;

import java.util.*;

import static com.google.common.collect.testing.features.CollectionFeature.GENERAL_PURPOSE;
import static com.google.common.collect.testing.features.CollectionFeature.REMOVE_OPERATIONS;
import static com.google.common.collect.testing.features.CollectionFeature.SUPPORTS_ADD;
import static com.google.common.collect.testing.features.MapFeature.SUPPORTS_PUT;
import static java.util.Arrays.asList;


public enum Mutability {
    IMMUTABLE(Collections.<CollectionFeature>emptySet(), Collections.<MapFeature>emptySet(), Collections.<CollectionFeature>emptySet()),
    UPDATABLE(asList(SUPPORTS_ADD), asList(SUPPORTS_PUT), Collections.<CollectionFeature>emptySet()),
    MUTABLE(asList(GENERAL_PURPOSE), asList(MapFeature.GENERAL_PURPOSE), asList(REMOVE_OPERATIONS));

    public final Collection<CollectionFeature> collectionFeatures;
    public final Collection<MapFeature> mapFeatures;
    public final Collection<CollectionFeature> mapViewFeatures;

    Mutability(
            Collection<CollectionFeature> collectionFeatures,
            Collection<MapFeature> mapFeatures, Collection<CollectionFeature> mapViewFeatures) {
        this.collectionFeatures = collectionFeatures;
        this.mapFeatures = mapFeatures;
        this.mapViewFeatures = mapViewFeatures;
    }


}
