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
                    public void execute(SourceSet sourceSet) {
                        FileResolver fileResolver = project.getFileResolver();
                        // for each source set we will:
                        // 1) Add a new 'javaTemplates' and 'resourceTemplates' source dirs
                        String sourceSetName = ((DefaultSourceSet) sourceSet).getDisplayName();
                        SourceDirectorySet javaTemplates = new DefaultSourceDirectorySet(
                                sourceSetName + " Java primitive specialization sources",
                                fileResolver);
                        String javaDir = String.format("src/%s/javaTemplates", sourceSet.getName());
                        javaTemplates.srcDir(javaDir);
                        sourceSet.getAllSource().source(javaTemplates);

                        SourceDirectorySet resourceTemplates = new DefaultSourceDirectorySet(
                                sourceSetName + " primitive specializations of resources",
                                fileResolver);
                        String resourcesDir =
                                String.format("src/%s/resourceTemplates", sourceSet.getName());
                        resourceTemplates.srcDir(resourcesDir);
                        sourceSet.getAllSource().source(resourceTemplates);

                        // 2) create an GeneratorTask for this sourceSet following the gradle
                        //    naming conventions via call to sourceSet.getTaskName()
                        final String javaTaskName =
                                sourceSet.getTaskName("generate", "JavaSpecializations");
                        final String resourcesTaskName =
                                sourceSet.getTaskName("generate", "ResourceSpecializations");
                        GeneratorTask javaGen = project.getTasks()
                                .create(javaTaskName, GeneratorTask.class);
                        GeneratorTask resourcesGen = project.getTasks()
                                .create(resourcesTaskName, GeneratorTask.class);
                        javaGen.setDescription(String.format(
                                "Processes the %s Java specialization sources.",
                                sourceSet.getName()));
                        resourcesGen.setDescription(String.format(
                                "Processes the %s resource specialization sources.",
                                sourceSet.getName()));

                        // 3) set up convention mapping for default sources
                        // (allows user to not have to specify)
                        javaGen.setSource(fileResolver.resolve(javaDir));
                        resourcesGen.setSource(fileResolver.resolve(resourcesDir));

                        // 4) Set up the Jpsg output directory (adding to javac inputs!)
                        final String javaTarget = String.format("%s/generated-src/jpsg/%s/java",
                                project.getBuildDir(), sourceSet.getName());
                        javaGen.setTarget(javaTarget);
                        final String resourcesTarget =
                                String.format("%s/generated-src/jpsg/%s/resources",
                                        project.getBuildDir(), sourceSet.getName());
                        resourcesGen.setTarget(resourcesTarget);

                        sourceSet.getJava().srcDir(new File(javaTarget));
                        sourceSet.getResources().srcDir(new File(resourcesTarget));

                        // 5) register fact that jpsg should be run before compiling
                        project.getTasks().getByName(sourceSet.getCompileJavaTaskName())
                                .dependsOn(javaTaskName);
                        project.getTasks().getByName(sourceSet.getProcessResourcesTaskName())
                                .dependsOn(resourcesTaskName);
                    }
                });
    }
}
