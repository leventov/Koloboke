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

package com.koloboke.jpsg;

import com.koloboke.jpsg.function.Predicate;
import com.koloboke.jpsg.function.UnaryOperator;
import org.gradle.api.internal.ConventionTask;
import org.gradle.api.tasks.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;


public class GeneratorTask extends ConventionTask {

    private final Generator g = new Generator();

    public GeneratorTask setObjectIdStyle(ObjectType.IdentifierStyle objectIdStyle) {
        g.setObjectIdStyle(objectIdStyle);
        return this;
    }

    public GeneratorTask setDefaultTypes(String defaultTypes) {
        g.setDefaultTypes(defaultTypes);
        return this;
    }

    public GeneratorTask with(Iterable<String> defaultContext) {
        g.with(defaultContext);
        return this;
    }

    public GeneratorTask with(String... defaultContext) {
        g.with(defaultContext);
        return this;
    }

    public GeneratorTask addToDefaultContext(String... defaultContext) {
        return with(defaultContext);
    }

    public GeneratorTask addProcessor(TemplateProcessor processor) {
        g.addProcessor(processor);
        return this;
    }

    public GeneratorTask addProcessor(Class<? extends TemplateProcessor> processorClass) {
        g.addProcessor(processorClass);
        return this;
    }

    public GeneratorTask addProcessor(String processorClassName) {
        g.addProcessor(processorClassName);
        return this;
    }

    public GeneratorTask addPrimitiveTypeModifierProcessors(String keyword,
            UnaryOperator<PrimitiveType> typeMapper, Predicate<String> dimFilter) {
        g.addPrimitiveTypeModifierProcessors(keyword, typeMapper, dimFilter);
        return this;
    }

    public GeneratorTask never(Iterable<String> options) {
        g.never(options);
        return this;
    }

    public GeneratorTask never(String... options) {
        g.never(options);
        return this;
    }

    public GeneratorTask include(Iterable<String> conditions) {
        g.include(conditions);
        return this;
    }

    public GeneratorTask include(String... conditions) {
        g.include(conditions);
        return this;
    }

    public GeneratorTask exclude(Iterable<String> conditions) {
        g.exclude(conditions);
        return this;
    }

    public GeneratorTask exclude(String... conditions) {
        g.exclude(conditions);
        return this;
    }


    public GeneratorTask setSource(File source) {
        g.setSource(source);
        return this;
    }

    public GeneratorTask setSource(Path source) {
        g.setSource(source.toFile());
        return this;
    }

    public GeneratorTask setSource(String source) {
        g.setSource(source);
        return this;
    }

    @InputDirectory
    public File getSource() {
        return g.getSource();
    }

    public GeneratorTask setTarget(File target) {
        g.setTarget(target);
        return this;
    }

    public GeneratorTask setTarget(Path target) {
        g.setTarget(target.toFile());
        return this;
    }

    public GeneratorTask setTarget(String target) {
        g.setTarget(target);
        return this;
    }

    @OutputDirectory
    public File getTarget() {
        return g.getTarget();
    }

    @TaskAction
    public void generate() throws IOException {
        g.generate();
    }
}
