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

package net.openhft.collect.impl;

public interface PrimitiveConstants {
    public static final int BYTE_CARDINALITY = 1 << 8;
    public static final int BYTE_MASK = 0xFF;
    /**
     * A prime slightly greater than <tt>{@link #BYTE_CARDINALITY} / 3</tt>.
     */
    public static final int BYTE_PERMUTATION_STEP = 97;

    public static final int SHORT_CARDINALITY = 1 << 16;
    public static final int SHORT_MASK = 0xFFFF;
    public static final int SHORT_PERMUTATION_STEP = 21859;

    public static final int CHAR_CARDINALITY = 1 << 16;
    public static final int CHAR_PERMUTATION_STEP = 21859;

    public static final long INT_MASK = 0xFFFFFFFFL;
    public static final long FLOAT_MASK = INT_MASK;
}
