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

import com.beust.jcommander.*;
import com.beust.jcommander.converters.IParameterSplitter;

import java.io.IOException;
import java.util.*;


public final class CLI {

    private static class Args {
        @Parameter(description = "TEMPLATES_ROOT_DIR or FILE, TARGET_ROOT_DIR",
                arity = 2, required = true)
        List<String> roots;

        @Parameter(
                names = {"-p", "--processor"},
                description = "Adds the processor " +
                        "(fully-qualified class name of net.openhft.jpsg.TemplateProcessor " +
                        "subclass) to generation chain")
        List<String> processors = new ArrayList<>();

        @Parameter(
                names = "--defaultTypes",
                description = "Set of options used to populate target contexts in templates " +
                        "which doesn't have /* with */ statement at the first line " +
                        "and template file name contains primitive title")
        String defaultTypes = "byte|char|short|int|long|float|double";

        @Parameter(
                names = "--never",
                description = "Set of options which couldn't appear at any dimension " +
                        "at any nesting-level in target contexts. " +
                        "Useful for generating less code. Example: --never byte|char|short")
        List<String> never = new ArrayList<>();

        @Parameter(
                names = "--include",
                description = "Code is generated for target context if it is permitted by this " +
                        "condition or any other similar condition. Context is prohibited " +
                        "by condition if it contains all dimensions of the condition and " +
                        "condition options for any dimension doesn't contain " +
                        "context option of that dimension. If no conditions are specified, code " +
                        "is generated for all target contexts. " +
                        "Example: --if key=int|long,value=int|long",
                splitter = NoSplitter.class)
        List<String> included = new ArrayList<>();

        @Parameter(
                names = "--exclude",
                description = "Code isn't generated for target context if it is prohibited " +
                        "by this condition or any other similar condition. Context is prohibited " +
                        "by condition if it contains all dimensions of the condition and " +
                        "condition options for any dimension doesn't contain " +
                        "context option of that dimension. If no conditions are specified, code " +
                        "is generated for all target contexts. " +
                        "Example: --except key=long,value=int",
                splitter = NoSplitter.class)
        List<String> excluded = new ArrayList<>();

        @Parameter(
                names = "--with",
                description = "Adds some options to default context. Example: --with jdk=JDK8",
                splitter = NoSplitter.class)
        List<String> defaultContext = new ArrayList<>();

        @Parameter(
                names = "--objectIdStyle",
                description = "Style of generating object identifiers from primitive templates. " +
                        "CharFoo.CHAR_BAR.charBaz -> LONG: ObjectFoo.OBJECT_BAR.objectBaz, " +
                        "SHORT: ObjFoo.OBJ_BAR.objBaz.")
        ObjectType.IdentifierStyle objectIdStyle = ObjectType.IdentifierStyle.SHORT;

        @Parameter(names = {"-h", "--help"}, description = "Show this help", help = true)
        private boolean help;
    }

    public static void main(String[] args)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException,
            IOException {
        Args parsedArgs = new Args();
        JCommander jc = new JCommander(parsedArgs, args);
        if (parsedArgs.help) {
            jc.usage();
            return;
        }
        Generator generator = new Generator();
        for (String processor : parsedArgs.processors) {
            generator.addProcessor(processor);
        }
        generator.setDefaultTypes(parsedArgs.defaultTypes)
                .setObjectIdStyle(parsedArgs.objectIdStyle)
                .never(parsedArgs.never)
                .include(parsedArgs.included)
                .exclude(parsedArgs.excluded)
                .with(parsedArgs.defaultContext)
                .setSource(parsedArgs.roots.get(0))
                .setTarget(parsedArgs.roots.get(1))
                .generate();
    }

    public static class NoSplitter implements IParameterSplitter {
        @Override
        public List<String> split(String value) {
            return Arrays.asList(value);
        }
    }
}
