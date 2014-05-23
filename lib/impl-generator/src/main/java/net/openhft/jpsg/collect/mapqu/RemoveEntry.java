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

public class RemoveEntry extends RemoveLike {

    @Override
    public void ifPresent() {
        gen.ifBlock(gen.valueEquals("value"));
        gen.remove();
        gen.ret(true);
        gen.elseBlock();
        gen.ret(false);
        gen.blockEnd();
    }

    @Override
    public void ifAbsent() {
        gen.ret(false);
    }

    @Override
    public String nullArgs() {
        return "value";
    }
}
