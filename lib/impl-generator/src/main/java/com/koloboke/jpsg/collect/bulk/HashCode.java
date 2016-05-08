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

package com.koloboke.jpsg.collect.bulk;

import com.koloboke.jpsg.PrimitiveType;

import static com.koloboke.jpsg.collect.MethodGenerator.primitiveHash;


public abstract class HashCode extends BulkMethod {

    abstract int initialValue();

    @Override
    public final void beginning() {
        gen.lines("int hashCode = " + initialValue() + ";");
    }

    abstract String aggregate(String elemHash);

    @Override
    public final void loopBody() {
        String key = gen.unwrappedKey(), keyHash;
        if (cxt.isPrimitiveKey()) {
            keyHash = primitiveHash((PrimitiveType) cxt.keyOption(), key);
        } else {
            String keyHashCodeMethod = cxt.nullKeyAllowed() ?
                    "nullableKeyHashCode" : "keyHashCode";
            keyHash = keyHashCodeMethod + "(" + key + ")";
        }
        String value = gen.unwrappedValue(), valueHash;
        if (cxt.isPrimitiveValue()) {
            valueHash = primitiveHash((PrimitiveType) cxt.mapValueOption(), value);
        } else {
            valueHash = "nullableValueHashCode(" + value + ")";
        }

        String hash;
        if (cxt.isMapView()) {
            hash = keyHash + " ^ " + valueHash;
        } else {
            hash = cxt.isKeyView() ? keyHash : valueHash;
        }
        gen.lines(aggregate(hash) + ";");
    }

    @Override
    public final void end() {
        gen.ret("hashCode");
    }
}
