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

package com.koloboke.jpsg.collect.mapqu;

import com.koloboke.jpsg.Option;

import static com.koloboke.jpsg.PrimitiveType.*;


public class AddValue extends MapQueryUpdateMethod {

    @Override
    public final BasicMapQueryUpdateOp baseOp() {
        return BasicMapQueryUpdateOp.CUSTOM_INSERT;
    }

    String toAdd() {
        return "value";
    }

    String initialValue() {
        return gen.defaultValue();
    }

    @Override
    public final void ifPresent() {
        declareNewValue(gen.value());
        gen.setValue("newValue");
        gen.ret("newValue");
    }

    @Override
    public final void ifAbsent() {
        declareNewValue(initialValue());
        gen.insert("newValue");
        gen.ret("newValue");
    }

    @Override
    public String nullArgs() {
        return "value";
    }

    private void declareNewValue(String baseValue) {
        String newValue = baseValue + " + " + toAdd();
        Option mvo = cxt.mapValueOption();
        if (mvo == BYTE || mvo == CHAR || mvo == SHORT) {
            newValue = "(" + cxt.valueType() + ") (" + newValue + ")";
        }
        gen.lines(cxt.valueType() + " newValue = " + newValue + ";");
    }
}
