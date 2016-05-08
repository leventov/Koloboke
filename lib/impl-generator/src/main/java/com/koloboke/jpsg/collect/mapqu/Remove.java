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

public final class Remove extends RemoveLike {

    @Override
    public void ifPresent() {
        if (cxt.isMapView()) {
            gen.lines(cxt.valueType() + " val = " + gen.value() + ";");
            gen.remove();
            gen.ret("val");
        } else {
            gen.remove();
            gen.ret(true);
        }
    }

    @Override
    public void ifAbsent() {
        gen.ret(cxt.isMapView() ? gen.defaultValue() : "false");
    }

    @Override
    public String nullArgs() {
        return "";
    }
}
