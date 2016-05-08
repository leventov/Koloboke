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

import com.koloboke.jpsg.Option;
import com.koloboke.jpsg.SimpleOption;


/**
 * RemoveAll and RetainAll
 */
public abstract class BulkRemoveUsingAnotherCollection extends BulkMethod  {

    private static final Option GIVEN_THIS = new SimpleOption("given");

    private boolean givenThis() {
        return GIVEN_THIS.equals(cxt.getOption("this"));
    }

    @Override
    public final EntryType entryType() {
        return EntryType.REUSABLE;
    }

    @Override
    public final boolean withInternalVersion() {
        return true;
    }

    @Override
    public final String argsBeforeCollection() {
        return givenThis() ? "thisC" : "";
    }

    @Override
    public void beginning() {
        String thisC = givenThis() ? "thisC" : "this";
        gen.lines("if (" + thisC + " == (Object) c)");
        gen.lines("    throw new IllegalArgumentException();");
    }

    @Override
    public final void end() {
        gen.ret("changed");
    }
}
