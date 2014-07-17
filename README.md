This project is a yet another Java collections implementation. Java 6+. Apache 2.0 license.

Goals in priority order:

 - Excellent compatibility with Java Collections Framework (JCF)

 - Performance

 - API quality: consistency, hard to misuse. (I really paid much attention to this.)
   However, I can't say this pretty verbose API is very easy to use.

Bonus: `java.util.Map` default methods from JDK8 in the API for Java 6 and 7.

---

#### [JavaDoc] (http://openhft.github.io/UntitledCollectionsProject/api/current/java7/index.html)

---

#### How to build and develop
Gradle build requires Java 8 compiler, set `JAVA_HOME` environment variable to the JDK8 location, if
your default `java` is still Java 7.

Then

    $ git clone git@github.com:OpenHFT/UntitledCollectionsProject.git collections
    $ cd collections
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

