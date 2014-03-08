## ```net.openhft.collect.research``` ##

Try

    $ gradle clean build
    $ java -cp build/libs/microbenchmarks.jar net.openhft.collect.research.hash.HashBenchmarks '.*lookup_binary_lHash_bitStates_present_intKey_simpleIndexing.*' -v SILENT

