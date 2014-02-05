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

import net.openhft.jpsg.*;
import net.openhft.jpsg.collect.MethodContext;
import net.openhft.jpsg.collect.Method;
import net.openhft.jpsg.collect.MethodGenerator;

import static net.openhft.jpsg.collect.mapqu.BasicMapQueryUpdateOp.INSERT;
import static net.openhft.jpsg.collect.mapqu.BasicMapQueryUpdateOp.CUSTOM_INSERT;
import static net.openhft.jpsg.collect.mapqu.Branch.KEY_ABSENT;
import static net.openhft.jpsg.collect.mapqu.Branch.KEY_PRESENT;


public abstract class MapQueryUpdateMethod implements Method {

    protected MapQueryUpdateMethodGenerator gen;
    protected MethodContext cxt;

    @Override
    public final void init(MethodGenerator g, MethodContext c) {
        this.gen = (MapQueryUpdateMethodGenerator) g;
        this.cxt = c;
    }

    public void beginning() {
    }

    public abstract BasicMapQueryUpdateOp baseOp();

    public Branch mostProbableBranch() {
        return baseOp() == INSERT ? KEY_ABSENT : KEY_PRESENT;
    }

    public boolean inline() {
        return baseOp() == CUSTOM_INSERT;
    }

    public String name() {
        return MethodGenerator.defaultMethodName(cxt, this);
    }

    public String nullArgs() {
        return "";
    }

    public abstract void ifPresent();

    public abstract void ifAbsent();
}
