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

package net.openhft.jpsg.collect.mapqu;

public final class Merge extends MapQueryUpdateMethod {

    @Override
    public BasicMapQueryUpdateOp baseOp() {
        return BasicMapQueryUpdateOp.CUSTOM_INSERT;
    }

    @Override
    public void ifPresent() {
        gen.lines(cxt.valueGenericType() + " newValue = remappingFunction." + cxt.applyValueName() +
                "(" + gen.value() + ", value);");
        if (cxt.isObjectValue() || cxt.genericVersion()) {
            gen.ifBlock("newValue != null");
        }
        gen.setValue("newValue");
        gen.ret("newValue");
        if (cxt.isObjectValue() || cxt.genericVersion()) {
            gen.elseBlock();
            gen.remove();
            gen.ret("null");
            gen.blockEnd();
        }
    }

    @Override
    public void ifAbsent() {
        if (cxt.isObjectValue() || cxt.genericVersion()) {
            gen.ifBlock("value != null");
        }
        gen.insert("value");
        gen.ret("value");
        if (cxt.isObjectValue() || cxt.genericVersion()) {
            gen.elseBlock();
            gen.ret("null");
            gen.blockEnd();
        }
    }

    @Override
    public String nullArgs() {
        return "value, remappingFunction";
    }
}
