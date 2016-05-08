## Research benchmarks ##

# `com.koloboke.collect.research` #

    $ ./../../gradlew clean build
    $ java -cp build/libs/benchmarks.jar com.koloboke.collect.research.hash.LookupBenchmarks arity=binary hash=l states=bit queryResult=present key=int indexing=simple queries=uniform,zipf capacity=1048576 loadFactor=0.9 -v SILENT

# `net.openhft.collections.research` #

    $ java -cp build/libs/benchmarks.jar net.openhft.collections.research.HashPosMapBenchmarks -v SILENT

`-h`, `--help` supported.

