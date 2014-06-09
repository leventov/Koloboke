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

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.SKIP_SUBTREE;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static net.openhft.jpsg.CheckingPattern.compile;
import static net.openhft.jpsg.Condition.CONDITION;
import static net.openhft.jpsg.Dimensions.DIMENSION;
import static net.openhft.jpsg.Dimensions.Parser.parseOptions;
import static net.openhft.jpsg.MalformedTemplateException.near;
import static net.openhft.jpsg.ObjectType.IdentifierStyle.SHORT;
import static net.openhft.jpsg.RegexpUtils.removeSubGroupNames;


public class Generator {
    private static final Logger log = LoggerFactory.getLogger(Generator.class);

    private static ThreadLocal<Path> currentSource = new ThreadLocal<>();
    private static void setCurrentSourceFile(Path source) {
        currentSource.set(source);
    }

    public static Path currentSourceFile() {
        return currentSource.get();
    }

    private static CheckingPattern compileBlock(String insideBlockRegex, String keyword) {
        String checkingBlock = "/[\\*/]\\s*" + keyword + "[^/\\*]*+[\\*/]/";
        String block = "/[\\*/]\\s*" + removeSubGroupNames(insideBlockRegex) + "\\s*[\\*/]/";
        return compile(justBlockOrWholeLine(checkingBlock), justBlockOrWholeLine(block));
    }

    private static String justBlockOrWholeLine(String block) {
        return format("^[^\\S\\r\\n]*%s[^\\S\\n]*?\\n|%s", block, block);
    }

    private static Pattern wrapBlock(String insideBlockRegex) {
        return RegexpUtils.compile("\\s*/[\\*/]\\s*" + insideBlockRegex + "\\s*[\\*/]/\\s*");
    }

    private static String getBlockGroup(String block, Pattern pattern, String group) {
        Matcher m = pattern.matcher(block);
        if (m.matches()) {
            return m.group(group);
        }  else {
            throw new AssertionError();
        }
    }

    public static final String DIMENSIONS = format("(?<dimensions>(\\s*%s\\s*)+)", DIMENSION);

    private static final String COND_START = format("if\\s*(?<condition>%s)", CONDITION);
    private static final Pattern COND_START_BLOCK_P = wrapBlock(COND_START);
    private static final CheckingPattern COND_START_P = compileBlock(COND_START, "if");

    private static final String COND_END = "endif";
    private static final CheckingPattern COND_END_P = compileBlock(COND_END, COND_END);

    private static final String COND_PART =
            format("((el)?if\\s*(?<condition>%s)|%s)", CONDITION, COND_END);
    private static final Pattern COND_PART_BLOCK_P = wrapBlock(COND_PART);
    private static final CheckingPattern COND_PART_P = compileBlock(COND_PART, "(el|end)?if");

    private static final String CONTEXT_START = format("with%s", DIMENSIONS);
    private static final Pattern CONTEXT_START_BLOCK_P = wrapBlock(CONTEXT_START);
    private static final CheckingPattern CONTEXT_START_P = compileBlock(CONTEXT_START, "with");

    private static final String CONTEXT_END = "endwith";
    private static final CheckingPattern CONTEXT_END_P = compileBlock(CONTEXT_END, CONTEXT_END);

    private static final String CONTEXT_PART = format("(%s|%s)", CONTEXT_START, CONTEXT_END);
    private static final CheckingPattern CONTEXT_PART_P = compileBlock(CONTEXT_PART, "(end)?with");

    private static final String ANY_BLOCK_PART = format("(%s|%s)", COND_PART, CONTEXT_PART);
    private static final CheckingPattern ANY_BLOCK_PART_P =
            compileBlock(ANY_BLOCK_PART, "((el|end)?if|(end)?with)");


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

    private final List<String> with = new ArrayList<>();
    private Context defaultContext = null;

    private final List<String> never = new ArrayList<>();
    private List<Option> excludedTypes;

    private final List<String> included = new ArrayList<>();
    private List<Dimensions> permissiveConditions;

    private final List<String> excluded = new ArrayList<>();
    private List<Dimensions> prohibitingConditions;

    private TemplateProcessor firstProcessor;

    public Generator setObjectIdStyle(ObjectType.IdentifierStyle objectIdStyle) {
        this.objectIdStyle = objectIdStyle;
        return this;
    }

    public Generator setDefaultTypes(String defaultTypes) {
        this.defaultTypes = parseOptions(defaultTypes, objectIdStyle);
        return this;
    }

    public Generator with(Iterable<String> defaultContext) {
        for (String cxt : defaultContext) {
            with.add(cxt);
        }
        return this;
    }

    public Generator with(String... defaultContext) {
        return with(Arrays.asList(defaultContext));
    }

    public Generator addProcessor(TemplateProcessor processor) {
        processors.add(processor);
        return this;
    }

    public Generator addProcessor(Class<? extends TemplateProcessor> processorClass) {
        try {
            return addProcessor(processorClass.newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalArgumentException(processorClass +
                    " template processor class should have public no-arg constructor");
        }
    }

    public Generator addProcessor(String processorClassName) {
        try {
            // noinspection unchecked
            return addProcessor(
                    (Class<? extends TemplateProcessor>) Class.forName(processorClassName));
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Template processor class with " +
                    processorClassName + " name is not found");
        }
    }

    public Generator never(Iterable<String> options) {
        for (String opts : options) {
            never.add(opts);
        }
        return this;
    }

    public Generator never(String... options) {
        return never(Arrays.asList(options));
    }

    public Generator include(Iterable<String> conditions) {
        for (String condition : conditions) {
            included.add(condition);
        }
        return this;
    }

    public Generator include(String... conditions) {
        return include(Arrays.asList(conditions));
    }

    public Generator exclude(Iterable<String> conditions) {
        for (String condition : conditions) {
            excluded.add(condition);
        }
        return this;
    }

    public Generator exclude(String... conditions) {
        return exclude(Arrays.asList(conditions));
    }


    public Generator setSource(File source) {
        this.source = source.toPath();
        return this;
    }

    public Generator setSource(Path source) {
        this.source = source;
        return this;
    }

    public Generator setSource(String source) {
        this.source = Paths.get(source);
        return this;
    }

    public File getSource() {
        return source.toFile();
    }

    public Generator setTarget(File target) {
        this.target = target.toPath();
        return this;
    }


    public Generator setTarget(Path target) {
        this.target = target;
        return this;
    }

    public Generator setTarget(String target) {
        this.target = Paths.get(target);
        return this;
    }

    public File getTarget() {
        return target.toFile();
    }

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
            throw new IllegalArgumentException(
                    target + " generation destination should be a dir");
        }
        init();
        if (Files.isDirectory(source)) {
            class DirGeneration extends RecursiveAction {
                final Path dir;

                public DirGeneration(Path dir) {
                    this.dir = dir;
                }

                @Override
                protected void compute() {
                    try {
                        final Collection<ForkJoinTask<?>> subTasks = new ArrayList<>();
                        Path targetDir = target.resolve(source.relativize(dir));
                        try {
                            Files.copy(dir, targetDir);
                        } catch (FileAlreadyExistsException e) {
                            if (!Files.isDirectory(targetDir))
                                throw e;
                        }
                        Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
                            @Override
                            public FileVisitResult preVisitDirectory(Path dir,
                                    BasicFileAttributes attrs) throws IOException {
                                if (!DirGeneration.this.dir.equals(dir)) {
                                    subTasks.add(new DirGeneration(dir));
                                    return SKIP_SUBTREE;
                                } else {
                                    return CONTINUE;
                                }
                            }

                            @Override
                            public FileVisitResult visitFile(final Path sourceFile,
                                    BasicFileAttributes attrs) throws IOException {
                                final Path targetFile =
                                        target.resolve(source.relativize(sourceFile));
                                if (sourceFile.toFile().lastModified() <
                                        targetFile.toFile().lastModified()) {
                                    log.info("File {} is up to date, not processing source",
                                            targetFile);
                                    return CONTINUE;
                                }
                                subTasks.add(ForkJoinTask.adapt(() -> {
                                    doGenerate(sourceFile, targetFile.getParent());
                                    return null;
                                }));
                                return CONTINUE;
                            }
                        });
                        ForkJoinTask.invokeAll(subTasks);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            new DirGeneration(source).compute();
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

        excludedTypes = never.stream()
                .flatMap(options -> dimensionsParser.parseOptions(options).stream())
                .collect(Collectors.toList());

        permissiveConditions = included.stream().map(dimensionsParser::parseCLI)
                .collect(Collectors.toList());

        prohibitingConditions = excluded.stream().map(dimensionsParser::parseCLI)
                .collect(Collectors.toList());

        initProcessors();
    }

    private void initProcessors() {
        Collections.sort(processors, (p1, p2) -> p1.priority() - p2.priority());
        TemplateProcessor prev = null;
        for (TemplateProcessor processor : processors) {
            processor.setDimensionsParser(dimensionsParser);
            processor.setNext(prev);
            prev = processor;
        }
        firstProcessor = processors.get(processors.size() - 1);
    }

    private void doGenerate(Path sourceFile, Path targetDir) throws IOException {
        setCurrentSourceFile(sourceFile);
        log.info("Processing file: {}", sourceFile);
        String sourceFileName = sourceFile.toFile().getName();
        Dimensions targetDims = dimensionsParser.parseClassName(sourceFileName);
        String rawContent = new String(Files.readAllBytes(sourceFile));
        CheckingMatcher fileDimsM = CONTEXT_START_P.matcher(rawContent);
        if (fileDimsM.find() && fileDimsM.start() == 0) {
            targetDims = dimensionsParser.parseForContext(
                    getBlockGroup(fileDimsM.group(), CONTEXT_START_BLOCK_P, "dimensions"));
            rawContent = rawContent.substring(fileDimsM.end()).trim() + "\n";
        }
        log.info("Target dimensions: {}", targetDims);
        List<Context> targetContexts = targetDims.generateContexts();
        final Context mainContext = defaultContext.join(targetContexts.get(0));
        CheckingMatcher fileCondM = COND_START_P.matcher(rawContent);
        Condition fileCond = null;
        if (fileCondM.find() && fileCondM.start() == 0) {
            fileCond = Condition.parseCheckedCondition(
                    getBlockGroup(fileCondM.group(), COND_START_BLOCK_P, "condition"),
                    dimensionsParser, mainContext,
                    rawContent, fileCondM.start()
            );
            rawContent = rawContent.substring(fileCondM.end()).trim() + "\n";
        }
        final String content = rawContent;

        List<ForkJoinTask<?>> contextGenerationTasks = new ArrayList<>();
        for (Context tc : targetContexts) {
            if (!checkContext(tc)) {
                log.info("Context filtered by generator: {}", tc);
                continue;
            }
            final Context target = defaultContext.join(tc);
            if (fileCond != null && !fileCond.check(target)) {
                log.info("Context filtered by file condition: {}", target);
                continue;
            }
            final String generatedFileName = generate(mainContext, target, sourceFileName);
            final Path generatedFile = targetDir.resolve(generatedFileName);
            contextGenerationTasks.add(ForkJoinTask.adapt(() -> {
                setCurrentSourceFile(sourceFile);
                String generatedContent = generate(mainContext, target, content);
                if (Files.exists(generatedFile)) {
                    String targetContent = new String(Files.readAllBytes(generatedFile));
                    if (generatedContent.equals(targetContent)) {
                        log.warn("Already generated: {}", generatedFileName);
                        return null;
                    }
                }
                Files.write(generatedFile, Arrays.asList(generatedContent), UTF_8,
                        TRUNCATE_EXISTING, CREATE);
                log.info("Wrote: {}", generatedFileName);
                return null;
            }));
        }
        ForkJoinTask.invokeAll(contextGenerationTasks);
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
            // context isn't permitted by any condition
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

    public final class BlocksProcessor extends TemplateProcessor {
        public static final int PRIORITY = DEFAULT_PRIORITY + 10;

        @Override
        protected int priority() {
            return PRIORITY;
        }

        @Override
        protected void process(StringBuilder sb, Context source, Context target, String template) {
            CheckingMatcher blockStartMatcher = ANY_BLOCK_PART_P.matcher(template);
            int prevBlockEndPos = 0;
            blockSearch:
            while (blockStartMatcher.find()) {
                int blockDefPos = blockStartMatcher.start();
                String linearBlock = template.substring(prevBlockEndPos, blockDefPos);
                postProcess(sb, source, target, linearBlock);
                String blockStart = blockStartMatcher.group();
                CheckingMatcher condM = COND_START_P.matcher(blockStart);
                if (condM.matches()) {
                    Condition prevBranchCond = Condition.parseCheckedCondition(
                            getBlockGroup(condM.group(), COND_PART_BLOCK_P, "condition"),
                            getDimensionsParser(), source,
                            template, blockStartMatcher.start()
                    );
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
                            // no special processing of nested `elif` branches
                        }
                        else {
                            if (prevBranchCond.check(target)) {
                                // condition of the previous `if` or `elif` branch
                                // triggers on the context
                                int branchEndPos = condM.start();
                                String block = template.substring(branchStartPos, branchEndPos);
                                process(sb, source, target, block);
                                if (COND_END_P.matcher(condPart).matches()) {
                                    prevBlockEndPos = condM.end();
                                    blockStartMatcher.region(prevBlockEndPos, template.length());
                                    continue blockSearch;
                                } else {
                                    // skip the rest `elif` branches
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
                                    throw near(template, blockDefPos, "`if` block is not closed");
                                }
                            } else {
                                if (COND_END_P.matcher(condPart).matches()) {
                                    prevBlockEndPos = condM.end();
                                    blockStartMatcher.region(prevBlockEndPos, template.length());
                                    continue blockSearch;
                                } else {
                                    // `elif` branch
                                    prevBranchCond = Condition.parseCheckedCondition(
                                            getBlockGroup(condM.group(), COND_PART_BLOCK_P,
                                                    "condition"),
                                            getDimensionsParser(), source,
                                            template, condM.start()
                                    );
                                    branchStartPos = condM.end();
                                }
                            }
                        }
                    }
                    throw near(template, blockDefPos, "`if` block is not closed");
                }
                else {
                    // `with` block
                    CheckingMatcher contextM = CONTEXT_START_P.matcher(blockStart);
                    if (contextM.matches()) {
                        Dimensions additionalDims = getDimensionsParser().parseForContext(
                                getBlockGroup(contextM.group(), CONTEXT_START_BLOCK_P,
                                        "dimensions"));
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
                                            process(sb, newSource, newTarget, block);
                                        }
                                    }
                                    prevBlockEndPos = contextM.end();
                                    blockStartMatcher.region(prevBlockEndPos, template.length());
                                    continue blockSearch;
                                } else {
                                    // nesting `with` end
                                    nest--;
                                }
                            } else {
                                // nesting `with` start
                                nest++;
                            }
                        }
                    } else {
                        throw near(template, blockDefPos,
                                "Block end or `elif` branch without start");
                    }
                }
            }
            String tail = template.substring(prevBlockEndPos);
            postProcess(sb, source, target, tail);
        }
    }
}
