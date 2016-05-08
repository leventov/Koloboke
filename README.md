## Koloboke Collections

This library is a carefully designed and efficient extension of the Java Collections Framework
with primitive specializations and more. Java 6+. Apache 2.0 license.

Currently only *hash sets* and *hash maps* are implemented.

### Features

 - **Excellent compatibility with Java Collections Framework (JCF)**
    - All primitive specialization collections *extend basic interfaces*
      (`Collection`, `Set`, `Map`),<br/> hence could be used as drop-in replacements
      of slow collections of boxed values
    - API for Java 6 and 7 is *forward-compatible* with all methods new in Java 8
    - *Fail-fast* semantics everywhere
    - `null` keys are (optionally) supported, just like in `java.util.HashMap`
    - `Float.NaN` and `Double.NaN` keys are treated consistently with boxed version
       (all `NaN`s are considered equal)
 - **Performance**
    - Here are several performance/memory footprint comparisons (covering different use cases)
      of collections frameworks, evidencing that in most cases Koloboke is the fastest
      and the most memory efficient library implementing hash maps and sets,
      typically beating the closest competitor by a large margin:
       - [Time - memory tradeoff with the example of
          Java Maps](http://java.dzone.com/articles/time-memory-tradeoff-example)
       - [Large HashMap overview: JDK, FastUtil, Goldman Sachs, HPPC,
          Koloboke, Trove](http://java-performance.info/large-hashmap-overview-jdk-fastutil-goldman-sachs-hppc-koloboke-trove)
       - [Most efficient way to increment a Map value
          in Java](http://stackoverflow.com/a/25354509/648955)
       - [Hash `Set<E>` implementations comparison](http://stackoverflow.com/a/26369483/648955)
    - Every method is implemented just as fast as it even possible
    - Hash table configurations allow to control memory-time tradeoff very precisely
 - **API quality**
    - API consists exclusively of *interfaces and static factory methods*
    - Every interface is provided with dozens of factory methods
    - As already mentioned, major part of Java 8 Collections API additions (actually, everything
      except streams and spliterators) is backported to the API for Java 6 and 7
    - Some useful extension methods beyond Java 8 Collections API
    - *Every* public entity in the API is documented
 - More than *half a million* of automatically generated tests

#### Disadvantage
All this goodness for the cost of... the library is insanely fat. Currently it takes about 20 MB
(and that's only hash sets and maps).

##### Possible solution: [roll the collection implementation only with features you need](https://github.com/leventov/Koloboke/wiki/Koloboke:-roll-the-collection-implementation-with-features-you-need).

### Ultra quick start

Add to your Gradle build script:

    dependencies {
        // `jdk8` instead of `jdk6-7` if you use Java 8
        compile 'net.openhft:koloboke-api-jdk6-7:0.6.8'
        runtime 'net.openhft:koloboke-impl-jdk6-7:0.6.8'
    }

Or Maven config (don't forget about jdk6-7/jdk8 suffix):

    <dependencies>
        <dependency>
            <groupId>net.openhft</groupId>
            <artifactId>koloboke-api-jdk6-7</artifactId>
            <version>0.6.8</version>
        </dependency>
        <dependency>
            <groupId>net.openhft</groupId>
            <artifactId>koloboke-impl-jdk6-7</artifactId>
            <version>0.6.8</version>
            <scope>runtime</scope>
        </dependency>
    <dependencies>

Or similarly for your favourite build system.
Or download jars of [the latest release](https://github.com/leventov/Koloboke/releases/latest).

Then you can start using collections. Replace all lines like

    Map<Integer, Integer> map = new HashMap<>();

with   

    Map<Integer, Integer> map = HashIntIntMaps.newMutableMap();

Next step: see [the table of equivalents of JDK collection patterns]
(http://leventov.github.io/Koloboke/api/0.6/java8/index.html#jdk-equivalents).

### JavaDoc: [Java 6] (http://leventov.github.io/Koloboke/api/0.6/java6/index.html) | [Java 7] (http://leventov.github.io/Koloboke/api/0.6/java7/index.html) | [Java 8] (http://leventov.github.io/Koloboke/api/0.6/java8/index.html)

### [Releases (with changelog)](https://github.com/leventov/Koloboke/releases)

### [Roadmap](https://github.com/leventov/Koloboke/issues?q=is%3Aopen+label%3A"new+functionality"+is%3Aissue)

---

### Satellite projects

 - [JPSG](jpsg) -- Java Primitive Specializations Generator
 - [Benchmarks](benchmarks) -- Many different JMH benchmarks,
   either related to the collection library and not
    - [Dimensioned JMH](benchmarks/dimensioned-jmh) - a convenient JMH wrapper
    
### Contributing, Feedback & Support

I would like to accept feedback from you.

 - What method names/signatures are inconvenient?
 - Missing features
 - Performance experience
 - Bugs or problems

Use [issues](https://github.com/leventov/Koloboke/issues) or ask a question on
[StackOverflow](stackoverflow.com/questions/tagged/koloboke).

### How to build and develop
Gradle build requires Java 8 compiler, set `JAVA_HOME` environment variable to the JDK8 location,
if your default `java` is still Java 7.

Then

    $ git clone git@github.com:leventov/Koloboke.git
    $ cd Koloboke
    $ ./gradlew :buildMeta
    $ ./gradlew buildMain -x test -x findbugsMain
    $ ./gradlew idea

Then you can open the project in IntelliJ IDEA.

To rebuild meta projects (code generators), run from the project root dir:

    $ ./gradlew :cleanMeta :buildMeta

To rebuild either the lib, benchmarks or both, run

    $ ./gradlew cleanMain buildMain

from the `lib`, `benchmarks` subdir or the root project dir respectively.

To build the lib for Java 8, run

    $ ../gradlew cleanMain buildMain -PlibTargetJava=8
    
from the `lib` subdir.

If you want to generate proper Javadocs, especially for Java 6 or 7, you should specify
`javadocExecutable` and `jdkSrc` build properties (see
[Gradle docs](http://www.gradle.org/docs/2.0/userguide/tutorial_this_and_that.html#sec:gradle_properties_and_system_properties)
for how to do that). Typical `javadocExecutable` value is `JAVA_HOME/bin/javadoc[.exe]`, `jdkSrc`
should point to a directory which contain uncompressed JDK sources, i. e. package structure starting
from `java`, `javax`, `sun`, etc. subdirs.

---

#### Project name history

 - ~~Trove~~ (This project was started as a [Trove fork](https://bitbucket.org/leventov/trove),
   but has nothing in common with Trove for already very long time.)
 - ~~UntitledCollectionsProject, UCP~~
 - ~~Higher Frequency Trading Collections, OpenHFT Collections, HFT Collections, HFTC~~
 - Koloboke (Collections) -- current name!

