# [Koloboke](https://koloboke.com)

A family of projects around collections in Java (so far).

## The Koloboke Collections API
[![koloboke-api maven central](
https://maven-badges.herokuapp.com/maven-central/com.koloboke/koloboke-api-jdk8/badge.svg)](
https://maven-badges.herokuapp.com/maven-central/com.koloboke/koloboke-api-jdk8)

A carefully designed extension of the Java Collections Framework with primitive specializations and
more. Java 6+. Apache 2.0 license.

**Compatibility with the Java Collections Framework**

 - All primitive specialization collections *extend basic interfaces* (`Collection`, `Set`, `Map`),
 hence could be used as drop-in replacements of slow collections of boxed values
 - API for Java 6 and 7 is *forward-compatible* with all methods new in Java 8

**JavaDoc: [Java 6](http://leventov.github.io/Koloboke/api/1.0/java6/index.html)
| [Java 7](http://leventov.github.io/Koloboke/api/1.0/java7/index.html)
| [Java 8](http://leventov.github.io/Koloboke/api/1.0/java8/index.html)**

## [Koloboke Compile](https://koloboke.com/compile)
[![koloboke-compile maven central](
https://maven-badges.herokuapp.com/maven-central/com.koloboke/koloboke-compile/badge.svg)](
https://maven-badges.herokuapp.com/maven-central/com.koloboke/koloboke-compile)

An annotation processor, generates implementations for collection-like abstract classes or
interfaces. API agnostic, may be used to generate implementation for classes or interfaces,
extending interfaces from the Koloboke Collections API, or, for example, [interfaces from the Trove
collections library](https://github.com/leventov/trove-over-koloboke-compile). "Embeddable version"
of the Koloboke implementation library.

### Quick start
Add the following dependencies in your Maven `pom.xml`:
```xml
  <dependency>
    <groupId>com.koloboke</groupId>
    <artifactId>koloboke-compile</artifactId>
    <version>0.5</version>
    <scope>provided</scope>
  </dependency>
  <dependency>
    <groupId>com.koloboke</groupId>
    <!-- `jdk6-7` instead of `jdk8` if you use Java 6 or 7 -->
    <artifactId>koloboke-impl-common-jdk8</artifactId>
    <version>1.0.0</version>
  </dependency>
```

Or in your Gradle build script, you should first apply the [`propdeps` Gradle plugin](
https://github.com/spring-projects/gradle-plugins/tree/master/propdeps-plugin#overview) to enable
`provided` dependencies, and then configure the `dependencies` block:
```groovy
dependencies {
    provided 'com.koloboke:koloboke-compile:0.5'
    // `jdk6-7` instead of `jdk8` if you use Java 6 or 7
    compile 'com.koloboke:koloboke-impl-common-jdk8:1.0.0'
}
```

Next step: read the [Koloboke Compile tutorial](compile/tutorial.md).

[**Javadocs**](http://leventov.github.io/Koloboke/compile/0.5/index.html)

## [The Koloboke implementation library](https://koloboke.com/implementation-library)
[![koloboke-impl maven central](
https://maven-badges.herokuapp.com/maven-central/com.koloboke/koloboke-impl-jdk8/badge.svg)](
https://maven-badges.herokuapp.com/maven-central/com.koloboke/koloboke-impl-jdk8)

An efficient implementation of the Koloboke Collections API.

**Compatibility with the Java Collections Framework**
 - *Fail-fast* semantics everywhere
 - `null` keys are (optionally) supported, just like in `java.util.HashMap`
 - `Float.NaN` and `Double.NaN` keys are treated consistently with boxed version
 (all `NaN`s are considered equal)

### Quick start
Add the following dependencies in your Maven `pom.xml`:
```xml
  <dependencies>
    <dependency>
      <groupId>com.koloboke</groupId>
      <artifactId>koloboke-api-jdk8</artifactId>
      <version>1.0.0</version>
    </dependency>
    <dependency>
      <groupId>com.koloboke</groupId>
      <artifactId>koloboke-impl-jdk8</artifactId>
      <version>1.0.0</version>
      <scope>runtime</scope>
    </dependency>
  <dependencies>
```

Or to your Gradle build script:
```groovy
dependencies {
    // `jdk6-7` instead of `jdk8` if you use Java 7 or older
    compile 'com.koloboke:koloboke-api-jdk8:1.0.0'
    runtime 'com.koloboke:koloboke-impl-jdk8:1.0.0'
}
```

Or similarly for your favourite build system.

Then you can start using collections. Replace all lines like

    Map<Integer, Integer> map = new HashMap<>();

with

    Map<Integer, Integer> map = HashIntIntMaps.newMutableMap();

Next step: see [the table of equivalents of JDK collection patterns]
(http://leventov.github.io/Koloboke/api/1.0/java8/index.html#jdk-equivalents).

---

### [Releases (with changelog)](https://github.com/leventov/Koloboke/releases)

---

### Contributing, Feedback & Support
Use [issues](https://github.com/leventov/Koloboke/issues) or ask a question on
[StackOverflow](stackoverflow.com/questions/tagged/koloboke).

---

### How to build and develop
Gradle build requires Java 8 compiler, set `JAVA_HOME` environment variable to the JDK 8 location.
Next to your JDK 8 location (i. e. a `jdk1.8` directory), *JDK 9 installation has to be present in
a `jdk-9` directory*. For meta projects development, JDK 6 and JDK 7 also have to be present in
`jdk1.6` and `jdk1.7` directories sibling to the `jdk1.8` directory.

Then

    $ git clone git@github.com:leventov/Koloboke.git
    $ cd Koloboke
    $ ./gradlew :buildMeta
    $ ./gradlew buildMain -x test -x findbugsMain -x findbugsTest
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

