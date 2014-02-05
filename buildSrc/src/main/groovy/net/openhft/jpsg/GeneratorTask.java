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

import org.gradle.api.internal.ConventionTask;
import org.gradle.api.tasks.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.openhft.jpsg.Condition.CONDITION;
import static net.openhft.jpsg.Dimensions.DIMENSION;
import static net.openhft.jpsg.Dimensions.Parser.parseOptions;
import static net.openhft.jpsg.ObjectType.IdentifierStyle.SHORT;
import static net.openhft.jpsg.RegexpUtils.compile;
import static net.openhft.jpsg.RegexpUtils.removeSubGroupNames;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;


public class GeneratorTask extends ConventionTask {
    private static Logger log = LoggerFactory.getLogger(GeneratorTask.class);

    private static Pattern compileBlock(String insideBlockRegex) {
        String block = "/[\\*/]\\s*" + removeSubGroupNames(insideBlockRegex) + "\\s*[\\*/]/";
        return compile(format("^\\s*%s[^\\S\\n]*?\\n|%s", block, block));
    }

    private static String getBlockGroup(String block, String insideBlockRegex, String group) {
        Matcher m =
                compile("(^\\s*)?/[\\*/]\\s*" + insideBlockRegex + "\\s*[\\*/]/([^\\S\\n]*?\\n)?")
                        .matcher(block);
        if (m.matches()) {
            return m.group(group);
        }  else {
            throw new IllegalArgumentException();
        }
    }

    public static final String DIMENSIONS = format("(?<dimensions>(\\s*%s\\s*)+)", DIMENSION);

    private static final String COND_START = format("if\\s*(?<condition>%s)", CONDITION);
    private static final Pattern COND_START_P = compileBlock(COND_START);

    private static final String COND_END = "endif";
    private static final Pattern COND_END_P = compileBlock(COND_END);

    private static final String COND_PART =
            format("((el)?if\\s*(?<condition>%s)|%s)", CONDITION, COND_END);
    private static final Pattern COND_PART_P = compileBlock(COND_PART);

    private static final String CONTEXT_START = format("with%s", DIMENSIONS);
    private static final Pattern CONTEXT_START_P = compileBlock(CONTEXT_START);

    private static final String CONTEXT_END = "endwith";
    private static final Pattern CONTEXT_END_P = compileBlock(CONTEXT_END);

    private static final String CONTEXT_PART = format("(%s|%s)", CONTEXT_START, CONTEXT_END);
    private static final Pattern CONTEXT_PART_P = compileBlock(CONTEXT_PART);

    private static final String BLOCK_START =
            format("(%s|%s)", removeSubGroupNames(COND_PART), CONTEXT_START);
    private static final Pattern BLOCK_START_P = compileBlock(BLOCK_START);


    private Path source;
    private Path target;

    private ObjectType.IdentifierStyle objectIdStyle = SHORT;
    private List<Option> defaultTypes =
            new ArrayList<>(Arrays.<Option>asList(PrimitiveType.values()));
    private Dimensions.Parser dimensionsParser;

    private final List<TemplateProcessor> processors = new ArrayList<TemplateProcessor>() {{
        // in chronological order
        add(new OptionProcessor());
        add(new ConstProcessor());
        add(new BlocksProcessor());
        add(new GenericsProcessor());
        add(new DefinitionProcessor());
        add(new FunctionProcessor());
        add(new FloatingWrappingProcessor());
    }};

    private List<String> with = new ArrayList<>();
    private Context defaultContext = null;

    private List<String> never = new ArrayList<>();
    private List<Option> excludedTypes;

    private List<String> included = new ArrayList<>();
    private List<Dimensions> permissiveConditions;

    private List<String> excluded = new ArrayList<>();
    private List<Dimensions> prohibitingConditions;

    private TemplateProcessor firstProcessor;

    public GeneratorTask setObjectIdStyle(ObjectType.IdentifierStyle objectIdStyle) {
        this.objectIdStyle = objectIdStyle;
        return this;
    }

    public GeneratorTask setDefaultTypes(String defaultTypes) {
        this.defaultTypes = parseOptions(defaultTypes, objectIdStyle);
        return this;
    }

    public GeneratorTask with(Iterable<String> defaultContext) {
        for (String cxt : defaultContext) {
            with.add(cxt);
        }
        return this;
    }

    public GeneratorTask with(String... defaultContext) {
        return with(Arrays.asList(defaultContext));
    }

    public GeneratorTask addProcessor(TemplateProcessor processor) {
        processors.add(processor);
        return this;
    }

    public GeneratorTask addProcessor(Class<? extends TemplateProcessor> processorClass) {
        try {
            return addProcessor(processorClass.newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public GeneratorTask addProcessor(String processorClassName) {
        try {
            // noinspection unchecked
            return addProcessor(
                    (Class<? extends TemplateProcessor>) Class.forName(processorClassName));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public GeneratorTask never(Iterable<String> options) {
        for (String opts : options) {
            never.add(opts);
        }
        return this;
    }

    public GeneratorTask never(String... options) {
        return never(Arrays.asList(options));
    }

    public GeneratorTask include(Iterable<String> conditions) {
        for (String condition : conditions) {
            included.add(condition);
        }
        return this;
    }

    public GeneratorTask include(String... conditions) {
        return include(Arrays.asList(conditions));
    }

    public GeneratorTask exclude(Iterable<String> conditions) {
        for (String condition : conditions) {
            excluded.add(condition);
        }
        return this;
    }

    public GeneratorTask exclude(String... conditions) {
        return exclude(Arrays.asList(conditions));
    }


    public GeneratorTask setSource(File source) {
        this.source = source.toPath();
        return this;
    }

    public GeneratorTask setSource(Path source) {
        this.source = source;
        return this;
    }

    public GeneratorTask setSource(String source) {
        this.source = Paths.get(source);
        return this;
    }

    @InputDirectory
    public File getSource() {
        return source.toFile();
    }

    public GeneratorTask setTarget(File target) {
        this.target = target.toPath();
        return this;
    }


    public GeneratorTask setTarget(Path target) {
        this.target = target;
        return this;
    }

    public GeneratorTask setTarget(String target) {
        this.target = Paths.get(target);
        return this;
    }

    @OutputDirectory
    public File getTarget() {
        return target.toFile();
    }

    @TaskAction
    public void generate() throws IOException {
        log.debug("Generator source: {}", source);
        log.debug("Generator target: {}", target);
        if (!Files.exists(source)) {
            return;
        }
        if (!Files.exists(target)) {
            Files.createDirectories(target);
        } else if (Files.isRegularFile(target)) {
            log.error("Target {} should be a dir", target);
            throw new IllegalArgumentException();
        }
        init();
        if (Files.isDirectory(source)) {
            Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                        throws IOException {
                    Path targetDir = target.resolve(source.relativize(dir));
                    try {
                        Files.copy(dir, targetDir);
                    } catch (FileAlreadyExistsException e) {
                        if (!Files.isDirectory(targetDir))
                            throw e;
                    }
                    return CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path sourceFile, BasicFileAttributes attrs)
                        throws IOException {
                    Path targetFile = target.resolve(source.relativize(sourceFile));
                    if (sourceFile.toFile().lastModified() < targetFile.toFile().lastModified()) {
                        log.info("File {} is up to date, not processing source", targetFile);
                        return CONTINUE;
                    }
                    doGenerate(sourceFile, targetFile.getParent());
                    return CONTINUE;
                }
            });
        } else {
            doGenerate(source, target);
        }
    }


    private void init() {
        for (int i = 0; i < defaultTypes.size(); i++) {
            Option defaultType = defaultTypes.get(i);
            if (defaultType instanceof ObjectType) {
                defaultTypes.set(i, ObjectType.get(objectIdStyle));
            }
        }
        dimensionsParser = new Dimensions.Parser(defaultTypes, objectIdStyle);

        defaultContext = new Context.Builder().makeContext();
        for (String context : with) {
            List<Context> contexts = dimensionsParser.parseCLI(context).generateContexts();
            if (contexts.size() > 1) {
                throw new IllegalArgumentException(
                        "Default context should have only trivial dimensions");
            }
            defaultContext = defaultContext.join(contexts.get(0));
        }

        excludedTypes = new ArrayList<>();
        for (String options : never) {
            excludedTypes.addAll(dimensionsParser.parseOptions(options));
        }

        permissiveConditions = new ArrayList<>();
        for (String cond : included) {
            permissiveConditions.add(dimensionsParser.parseCLI(cond));
        }

        prohibitingConditions = new ArrayList<>();
        for (String cond : excluded) {
            prohibitingConditions.add(dimensionsParser.parseCLI(cond));
        }

        initProcessors();
    }

    private void initProcessors() {
        Collections.sort(processors, new Comparator<TemplateProcessor>() {
            @Override
            public int compare(TemplateProcessor p1, TemplateProcessor p2) {
                return p1.priority() - p2.priority();
            }
        });
        TemplateProcessor prev = null;
        for (TemplateProcessor processor : processors) {
            processor.setDimensionsParser(dimensionsParser);
            processor.setNext(prev);
            prev = processor;
        }
        firstProcessor = processors.get(processors.size() - 1);
    }

    private void doGenerate(Path sourceFile, Path targetDir) throws IOException {
        log.info("Processing file: {}", sourceFile);
        String sourceFileName = sourceFile.toFile().getName();
        Dimensions targetDims = dimensionsParser.parseClassName(sourceFileName);
        String content = new String(Files.readAllBytes(sourceFile));
        Matcher fileDimsM = CONTEXT_START_P.matcher(content);
        if (fileDimsM.find() && fileDimsM.start() == 0) {
            targetDims = dimensionsParser.parse(
                    getBlockGroup(fileDimsM.group(), CONTEXT_START, "dimensions"));
            content = content.substring(fileDimsM.end()).trim() + "\n";
        }
        Matcher fileCondM = COND_START_P.matcher(content);
        Condition fileCond = null;
        if (fileCondM.find() && fileCondM.start() == 0) {
            fileCond = Condition.parse(
                    getBlockGroup(fileCondM.group(), COND_START, "condition"),
                    dimensionsParser);
            content = content.substring(fileCondM.end()).trim() + "\n";
        }
        log.info("Target dimensions: {}", targetDims);
        List<Context> targetContexts = targetDims.generateContexts();
        Context mainContext = targetContexts.get(0);
        for (Context target : targetContexts) {
            if (!checkContext(target)) {
                log.info("Context filtered by generator: {}", target);
                continue;
            }
            target = defaultContext.join(target);
            if (fileCond != null && !fileCond.check(target)) {
                log.info("Context filtered by file condition: {}", target);
                continue;
            }
            String generatedContent = generate(mainContext, target, content);
            String generatedFileName = generate(mainContext, target, sourceFileName);
            Path generatedFile = targetDir.resolve(generatedFileName);
            if (Files.exists(generatedFile)) {
                String targetContent = new String(Files.readAllBytes(generatedFile));
                if (generatedContent.equals(targetContent)) {
                    log.warn("Already generated: {}", generatedFileName);
                    continue;
                }
            }
            Files.write(generatedFile, Arrays.asList(generatedContent), UTF_8,
                    TRUNCATE_EXISTING, CREATE);
            log.info("Wrote: {}", generatedFileName);
        }
    }

    private String generate(Context source, Context target, String template) {
        return firstProcessor.generate(source, target, template);
    }

    private boolean checkContext(Context target) {
        for (Map.Entry<String, Option> e : target) {
            if (excludedTypes.contains(e.getValue()))
                return false;
        }
        checkPermissive:
        if (!permissiveConditions.isEmpty()) {
            for (Dimensions permissiveCondition : permissiveConditions) {
                if (permissiveCondition.checkAsCondition(target))
                    break checkPermissive;
            }
            // context doesn't permitted by any condition
            return false;
        }
        if (!prohibitingConditions.isEmpty()) {
            for (Dimensions prohibitingCondition : prohibitingConditions) {
                if (prohibitingCondition.checkAsCondition(target))
                    return false;
            }
        }
        return true;
    }

    public class BlocksProcessor extends TemplateProcessor {
        public static final int PRIORITY = DEFAULT_PRIORITY + 10;

        @Override
        protected int priority() {
            return PRIORITY;
        }

        @Override
        protected void process(Context source, Context target, String template) {
            Matcher blockStartMatcher = BLOCK_START_P.matcher(template);
            int prevBlockEndPos = 0;
            blockSearch:
            while (blockStartMatcher.find()) {
                int blockDefPos = blockStartMatcher.start();
                String linearBlock = template.substring(prevBlockEndPos, blockDefPos);
                postProcess(source, target, linearBlock);
                String blockStart = blockStartMatcher.group();
                Matcher condM = COND_START_P.matcher(blockStart);
                if (condM.matches()) {
                    Condition branchCond = Condition.parse(
                            getBlockGroup(condM.group(), COND_PART, "condition"),
                            getDimensionsParser());
                    int branchStartPos = blockStartMatcher.end();
                    int nest = 0;
                    condM = COND_PART_P.matcher(template);
                    condM.region(branchStartPos, template.length());
                    while (condM.find()) {
                        String condPart = condM.group();
                        if (COND_START_P.matcher(condPart).matches()) {
                            nest++;
                        }
                        else if (nest != 0) {
                            if (COND_END_P.matcher(condPart).matches()) {
                                nest--;
                            }
                        } else {
                            if (branchCond.check(target)) {
                                int branchEndPos = condM.start();
                                String block = template.substring(branchStartPos, branchEndPos);
                                process(source, target, block);
                                if (COND_END_P.matcher(condPart).matches()) {
                                    prevBlockEndPos = condM.end();
                                    blockStartMatcher.region(prevBlockEndPos, template.length());
                                    continue blockSearch;
                                } else {
                                    while (condM.find()) {
                                        condPart = condM.group();
                                        if (COND_END_P.matcher(condPart).matches()) {
                                            if (nest == 0) {
                                                prevBlockEndPos = condM.end();
                                                blockStartMatcher.region(
                                                        prevBlockEndPos, template.length());
                                                continue blockSearch;
                                            } else {
                                                nest--;
                                            }
                                        } else if (COND_START_P.matcher(condPart).matches()) {
                                            nest++;
                                        }
                                    }
                                    throw new IllegalStateException();
                                }
                            } else {
                                if (COND_END_P.matcher(condPart).matches()) {
                                    prevBlockEndPos = condM.end();
                                    blockStartMatcher.region(prevBlockEndPos, template.length());
                                    continue blockSearch;
                                } else {
                                    branchCond = Condition.parse(
                                            getBlockGroup(condM.group(), COND_PART, "condition"),
                                            getDimensionsParser());
                                    branchStartPos = condM.end();
                                }
                            }
                        }
                    }
                    String contentAround = template.substring(Math.max(0, blockDefPos - 100),
                            Math.min(blockDefPos + 100, template.length()));
                    throw new IllegalStateException("Near: " + contentAround);
                }
                else {
                    Matcher contextM = CONTEXT_START_P.matcher(blockStart);
                    if (contextM.matches()) {
                        Dimensions additionalDims = getDimensionsParser().parse(
                                getBlockGroup(contextM.group(), CONTEXT_START, "dimensions"));
                        int blockStartPos = blockStartMatcher.end();
                        int nest = 0;
                        contextM = CONTEXT_PART_P.matcher(template);
                        contextM.region(blockStartPos, template.length());
                        while (contextM.find()) {
                            String contextPart = contextM.group();
                            if (CONTEXT_END_P.matcher(contextPart).matches()) {
                                if (nest == 0) {
                                    int blockEndPos = contextM.start();
                                    String block = template.substring(blockStartPos, blockEndPos);
                                    List<Context> addContexts = additionalDims.generateContexts();
                                    Context newSource = source.join(addContexts.get(0));
                                    for (Context addCxt : addContexts) {
                                        Context newTarget = target.join(addCxt);
                                        // if addContext size is 1, this context is not
                                        // for generation. For example to prevent
                                        // unwanted generation:
                                        // /*with int|long dim*/ generated int 1 /*with int elem*/
                                        // always int /*endwith*/ generated int 2 /*endwith*/
                                        // -- for example. We don't filter such contexts.
                                        if (addContexts.size() == 1 || checkContext(newTarget)) {
                                            process(newSource, newTarget, block);
                                        }
                                    }
                                    prevBlockEndPos = contextM.end();
                                    blockStartMatcher.region(prevBlockEndPos, template.length());
                                    continue blockSearch;
                                } else {
                                    nest--;
                                }
                            } else {
                                // context start
                                nest++;
                            }
                        }
                    }
                    else {
                        String contentAround = template.substring(Math.max(0, blockDefPos - 100),
                                Math.min(blockDefPos + 100, template.length()));
                        throw new IllegalStateException("Near: " + contentAround);
                    }
                }
            }
            String tail = template.substring(prevBlockEndPos);
            postProcess(source, target, tail);
        }
    }
}
