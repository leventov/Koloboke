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

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.internal.artifacts.configurations.ConfigurationContainerInternal;
import org.gradle.api.internal.file.DefaultSourceDirectorySet;
import org.gradle.api.internal.file.FileResolver;
import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.api.internal.tasks.DefaultSourceSet;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;

import javax.inject.Inject;
import java.io.File;
import java.util.concurrent.Callable;


/**
 * Copied Gradle 1.10 Antlr plugin
 */
public class GradlePlugin  implements Plugin<ProjectInternal> {
    public static final String JPSG_CONFIGURATION_NAME = "jpsg";

    private enum Flavor {
        JAVA("java") {
            @Override
            String sourceSetNameEnd() {
                return " Java primitive specialization sources";
            }

            @Override
            SourceDirectorySet getSourceDirectorySet(SourceSet sourceSet) {
                return sourceSet.getJava();
            }

            @Override
            String dependentTaskName(SourceSet sourceSet) {
                return sourceSet.getCompileJavaTaskName();
            }
        },

        RESOURCES("resource") {
            @Override
            String sourceSetNameEnd() {
                return " primitive specializations of resources";
            }

            @Override
            String generatedPart() {
                return "resources";
            }

            @Override
            SourceDirectorySet getSourceDirectorySet(SourceSet sourceSet) {
                return sourceSet.getResources();
            }

            @Override
            String dependentTaskName(SourceSet sourceSet) {
                return sourceSet.getProcessResourcesTaskName();
            }
        };

        private final String word;

        Flavor(String word) {
            this.word = word;
        }

        abstract String sourceSetNameEnd();
        String templatesPart() {
            return word + "Templates";
        }
        String taskNameSuffix() {
            return word.substring(0, 1).toUpperCase() + word.substring(1) + "Specializations";
        }
        String taskDescriptionFormat() {
            return "Processes the %s " + word + " specialization sources.";
        }
        String generatedPart() {
            return word;
        }
        abstract SourceDirectorySet getSourceDirectorySet(SourceSet sourceSet);
        abstract String dependentTaskName(SourceSet sourceSet);
    }

    private static void setupTask(ProjectInternal project, SourceSet sourceSet, Flavor f) {
        String dirPath = String.format("src/%s/%s", sourceSet.getName(), f.templatesPart());
        FileResolver fileResolver = project.getFileResolver();
        File dir = fileResolver.resolve(dirPath);
        if (!dir.exists())
            return;
        // for each source set we will:
        // 1) Add a new 'javaTemplates' or 'resourceTemplates' source dirs
        String sourceSetName = ((DefaultSourceSet) sourceSet).getDisplayName();
        SourceDirectorySet templates = new DefaultSourceDirectorySet(
                sourceSetName + f.sourceSetNameEnd(), fileResolver);
        templates.srcDir(dirPath);
        sourceSet.getAllSource().source(templates);

        // 2) create an GeneratorTask for this sourceSet following the gradle
        //    naming conventions via call to sourceSet.getTaskName()
        final String taskName =
                sourceSet.getTaskName("generate", f.taskNameSuffix());
        GeneratorTask gen = project.getTasks().create(taskName, GeneratorTask.class);
        gen.setDescription(String.format(f.taskDescriptionFormat(), sourceSet.getName()));

        // 3) set up convention mapping for default sources
        // (allows user to not have to specify)
        gen.setSource(dir);

        // 4) Set up the Jpsg output directory (adding to javac inputs!)
        final String target = String.format("%s/generated-src/jpsg/%s/%s",
                project.getBuildDir(), sourceSet.getName(), f.generatedPart());
        gen.setTarget(target);
        f.getSourceDirectorySet(sourceSet).srcDir(new File(target));

        // 5) register fact that jpsg should be run before compiling
        project.getTasks().getByName(f.dependentTaskName(sourceSet)).dependsOn(taskName);
    }

    @Override
    public void apply(final ProjectInternal project) {
        project.getPlugins().apply(JavaPlugin.class);

        // set up a configuration named 'jpsg' for the user to specify the jpsg libs to use in case
        // they want a specific version etc.
        final ConfigurationContainerInternal projectConf = project.getConfigurations();
        Configuration jpsgConfiguration = projectConf.create(JPSG_CONFIGURATION_NAME)
                        .setVisible(false).setTransitive(false)
                        .setDescription("The Jpsg libraries to be used for this project.");
        projectConf.getByName(JavaPlugin.COMPILE_CONFIGURATION_NAME).extendsFrom(jpsgConfiguration);

        project.getConvention().getPlugin(JavaPluginConvention.class).getSourceSets().all(
                new Action<SourceSet>() {
                    @Override
                    public void execute(SourceSet sourceSet) {
                        setupTask(project, sourceSet, Flavor.JAVA);
                        setupTask(project, sourceSet, Flavor.RESOURCES);
                    }
                });
    }
}
