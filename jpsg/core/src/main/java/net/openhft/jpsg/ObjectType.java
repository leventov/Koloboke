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

package net.openhft.jpsg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class ObjectType implements Option {
    private static final Logger log = LoggerFactory.getLogger(ObjectType.class);

    public static enum IdentifierStyle {
        LONG("object", "Object", "OBJECT"),
        SHORT("obj", "Obj", "OBJ");

        final String lower, title, upper;
        private final ObjectType typeForThisStyle;

        IdentifierStyle(String lower, String title, String upper) {
            this.lower = lower;
            this.title = title;
            this.upper = upper;
            typeForThisStyle = new ObjectType(this);

        }
    }

    static ObjectType get(IdentifierStyle idStyle) {
        return idStyle.typeForThisStyle;
    }

    final IdentifierStyle idStyle;

    private ObjectType(IdentifierStyle idStyle) {
        this.idStyle = idStyle;
    }

    @Override
    public String intermediateReplace(String content, String dim) {
        log.info("Object type can't be template source");
        return content;
    }

    public static String genericParamName(String dim) {
        return dim.substring(0, 1).toUpperCase();
    }


    @Override
    public String finalReplace(String content, String dim) {
        IntermediateOption intermediate = IntermediateOption.of(dim);
        String genericParamName = genericParamName(dim);
        content = intermediate.classNameP.matcher(content).replaceAll(genericParamName);
        content = intermediate.standaloneP.matcher(content).replaceAll(genericParamName);
        content = intermediate.lowerP.matcher(content).replaceAll(idStyle.lower);
        content = intermediate.titleP.matcher(content).replaceAll(idStyle.title);
        content = intermediate.upperP.matcher(content).replaceAll(idStyle.upper);
        return content;
    }


    @Override
    public String toString() {
        return "Object";
    }
}
