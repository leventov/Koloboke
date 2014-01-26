This project is a yet another Java collections implementation. Java 6+. Apache 2.0 license.

Goals in priority order:

 - Excellent compatibility with Java Collections Framework (JCF)

 - Performance

 - API quality: consistency, hard to misuse. (I really paid much attention to this.)
   However, I can't say this pretty verbose API is very easy to use.

Bonus: java.uti.Map default methods from JDK8 in the API for Java 6 and 7.

---

#### [JavaDoc] (http://openhft.github.io/UntitledCollectionsProject/api/current/java7/index.html)

---

#### How to build and develop


    $ git clone git@github.com:OpenHFT/UntitledCollectionsProject.git collections
    $ cd collections
    $ gradle idea

Then you can open the project in IntelliJ IDEA. To build, run

    $ gradle build

---

Project started as [Trove fork](https://bitbucket.org/leventov/trove) in June 2013.

