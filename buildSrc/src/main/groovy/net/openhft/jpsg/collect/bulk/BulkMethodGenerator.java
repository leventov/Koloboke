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

package net.openhft.jpsg.collect.bulk;

import net.openhft.jpsg.collect.MethodGenerator;


public abstract class BulkMethodGenerator extends MethodGenerator {

    public abstract String keyAndValue();

    public abstract String key();

    public abstract String value();

    public abstract MethodGenerator remove();

    public abstract MethodGenerator setValue(String newValue);

    public abstract String viewValues();

    public abstract String viewElem();

    public abstract BulkMethodGenerator clear();
}
