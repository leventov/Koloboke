## HFT Collections

This is a yet another Java collections library of primitive specializations.
Java 6+. Apache 2.0 license.

Currently only *hash sets* and *hash maps* are implemented.

### Features

 - Excellent compatibility with Java Collections Framework (JCF):
    - All primitive specialization collections *extend basic interfaces*
      (`Collection`, `Set`, `Map`),<br/> hence could be used as drop-in replacements
      of slow collections of boxed values
    - API for Java 6 and 7 is *forward-compatible* with all methods new in Java 8
    - *Fail-fast* semantics everywhere
    - `null` keys are (optionally) supported, just like in `java.util.HashMap`
    - `Float.NaN` and `Double.NaN` keys are treated consistently with boxed version
       (all `NaN`s are considered equal)
 - Performance:
    - It is fast, *honestly, very* fast
    - Every method is implemented just as fast as it even possible
    - Hash table configurations allow to control memory-time tradeoff very precisely
 - API quality:
    - API consists exclusively of *interfaces and static factory methods*
    - Every interface is provided with dozens of factory methods
    - As already mentioned, major part of Java 8 Collections API additions (actually, everything
      except streams and spliterators) is backported to the API for Java 6 and 7
    - Some useful extension methods beyond Java 8 Collections API
 - More than *half a million* of automatically generated tests

#### Disadvantage
All this goodness for the cost of... the library is insane fat. Currently it takes about 20 MB
(and that's only hash sets and maps).

### Ultra quick start

Gradle build script (similarly for Maven):

    dependencies {
        // `jdk8` instead of `jdk6-7` if you use Java 8
        compile 'net.openhft:hftc-api-jdk6-7:0.5'
        runtime 'net.openhft:hftc-impl-jdk6-7:0.5'
    }

Code:

    Map<Integer, Integer> map = HashIntIntMaps.newUpdatableMap(expectedSize);
    
Next, see [the table of equivalents of JDK collection patterns]
(http://openhft.github.io/hftc/api/0.5/java8/net/openhft/collect/package-summary.html#jdk-equivalents).

### JavaDoc: [Java 6] (http://openhft.github.io/hftc/api/0.5/java6/index.html) | [Java 7] (http://openhft.github.io/hftc/api/0.5/java7/index.html) | [Java 8] (http://openhft.github.io/hftc/api/0.5/java8/index.html)

---

### Satellite projects

 - [JPSG](jpsg) -- Java Primitive Specializations Generator
 - [Benchmarks](benchmarks) -- Many different JMH benchmarks,
   either related to the collection library and not
    - [Dimensioned JMH](benchmarks/dimensioned-jmh) - a convenient JMH wrapper
    
### Contributing

I would like to accept a feedback from you.

 - What method names/signatures are inconvenient?
 - Missing features
 - Performance experience

### How to build and develop
Gradle build requires Java 8 compiler, set `JAVA_HOME` environment variable to the JDK8 location,
if your default `java` is still Java 7.

Then

    $ git clone git@github.com:OpenHFT/hftc.git
    $ cd hftc
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
 
---

Project started as [Trove fork](https://bitbucket.org/leventov/trove) in July 2013.

