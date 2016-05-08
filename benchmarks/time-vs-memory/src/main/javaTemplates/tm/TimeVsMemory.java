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

package tm;

import com.carrotsearch.hppc.*;
import com.carrotsearch.hppc.procedures.*;
import com.gs.collections.api.map.primitive.*;
import com.gs.collections.api.tuple.primitive.*;
import com.gs.collections.impl.map.mutable.primitive.*;
import gnu.trove.iterator.*;
import gnu.trove.map.*;
import gnu.trove.map.hash.*;
import gnu.trove.procedure.*;
import it.unimi.dsi.fastutil.objects.*;
import com.koloboke.bench.DimensionedJmh;
import com.koloboke.collect.hash.*;
import com.koloboke.collect.map.*;
import com.koloboke.collect.map.hash.*;
import org.apache.mahout.math.map.*;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.CommandLineOptionException;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;

import static java.lang.Integer.parseInt;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.IntStream.iterate;
import static java.util.stream.IntStream.range;
import static org.openjdk.jol.info.GraphLayout.parseInstance;


@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Threads(1)
@Fork(1)
@Warmup(iterations = 5, time = 1, timeUnit = SECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = SECONDS)
public class TimeVsMemory {
    static final int MIN_SIZE = 1000, MAX_SIZE = 10_000_000;
    static final int SIZE_STEPS = 10;

    static IntStream sizes;
    static {
        double sizeRatio = MAX_SIZE * 1.0 / MIN_SIZE;
        double step = Math.pow(sizeRatio, 1.0 / (SIZE_STEPS - 1));
        sizes = iterate(MIN_SIZE, s -> (int) (s * step)).limit(SIZE_STEPS);
    }

    static final IntStream loadLevels = range(1, 10);

    static final int SIZE = Integer.getInteger("size", MIN_SIZE);
    static final int LOAD_LEVEL = Integer.getInteger("loadLevel", 5);

    static HashConfig config(int loadLevel) {
        switch (loadLevel) {
            case 1: return config(0.066, 0.1, 0.134, 2.0);
            case 2: return config(0.133, 0.2, 0.267, 2.0);
            case 3: return config(0.2, 0.3, 0.4, 2.0);
            case 4: return config(0.266, 0.4, 0.534, 2.0);
            case 5: return config(0.33, 0.5, 0.67, 2.0);
            case 6: return config(0.4, 0.6, 0.8, 2.0);
            case 7: return config(0.466, 0.7, 0.934, 2.0);
            case 8: return config(0.64, 0.8, 0.96, 1.5);
            case 9: return config(0.79, 0.9, 0.99, 1.25);
            default: throw new AssertionError();
        }
    }

    private static HashConfig config(double minLoad, double targetLoad, double maxLoad,
            double growFactor) {
        HashConfig config = HashConfig.getDefault().withGrowFactor(growFactor);
        if (minLoad < config.getMinLoad()) {
            return config.withMinLoad(minLoad).withTargetLoad(targetLoad).withMaxLoad(maxLoad);
        } else {
            return config.withMaxLoad(maxLoad).withTargetLoad(targetLoad).withMinLoad(minLoad);
        }
    }

    /* with char|byte|int|long key */

    static class StdCharCharMap extends HashMap<Character, Character> {
        StdCharCharMap(int initialCapacity, float loadFactor) {
            super(initialCapacity, loadFactor);
        }
    }

    private static StdCharCharMap stdCharCharMap(int loadLevel, int size) {
        double loadFactor = 0.1 * loadLevel;
        int initialCapacity = (int) (size / loadFactor) + 1;
        return new StdCharCharMap(initialCapacity, (float) loadFactor);
    }

    private static char addValue(StdCharCharMap map, char key, char value) {
        return map.merge(key, value, (v1, v2) -> (char) (v1 + v2));
    }

    private static long iter(StdCharCharMap map) {
        long dummy = 0L;
        for (Map.Entry<Character, Character> entry : map.entrySet()) {
            dummy ^= entry.getKey() + entry.getValue();
        }
        return dummy;
    }

    private static com.koloboke.collect.map.CharCharMap kolobokeCharCharMap(
            int loadLevel, int size) {
        HashCharCharMapFactory factory = HashCharCharMaps.getDefaultFactory();
        factory = factory.withHashConfig(config(loadLevel));
        return factory.newUpdatableMap(size);
    }

    private static char addValue(com.koloboke.collect.map.CharCharMap map,
            char key, char value) {
        return map.addValue(key, value);
    }

    private static long iter(com.koloboke.collect.map.CharCharMap map) {
        long dummy = 0L;
        for (CharCharCursor cur = map.cursor(); cur.moveNext();) {
            dummy ^= cur.key() + cur.value();
        }
        return dummy;
    }

    private static TCharCharMap troveCharCharMap(int loadLevel, int size) {
        double loadFactor = 0.1 * loadLevel;
        return new TCharCharHashMap(size, (float) loadFactor);
    }

    private static char addValue(TCharCharMap map, char key, char value) {
        return map.adjustOrPutValue(key, value, value);
    }

    private static long iter(TCharCharMap map) {
        long dummy = 0L;
        for (TCharCharIterator it = map.iterator(); it.hasNext(); ) {
            it.advance();
            dummy ^= it.key() + it.value();
        }
        return dummy;
    }

    private static com.carrotsearch.hppc.CharCharMap hppcCharCharMap(int loadLevel, int size) {
        double loadFactor = 0.1 * loadLevel;
        int initialCapacity = (int) (size / loadFactor) + 1;
        return new CharCharOpenHashMap(initialCapacity, (float) loadFactor);
    }

    private static char addValue(com.carrotsearch.hppc.CharCharMap map,
            char key, char value) {
        return map.addTo(key, value);
    }

    private static long iter(com.carrotsearch.hppc.CharCharMap map) {
        long dummy = 0L;
        for (com.carrotsearch.hppc.cursors.CharCharCursor cur : map) {
            dummy ^= cur.key + cur.value;
        }
        return dummy;
    }

    private static MutableCharCharMap gsCharCharMap(int loadLevel, int size) {
        double loadFactor = 0.1 * (loadLevel + 1);
        int initialCapacity = (int) (size / loadFactor);
        return new CharCharHashMap(initialCapacity);
    }

    private static char addValue(MutableCharCharMap map, char key, char value) {
        return map.addToValue(key, value);
    }

    private static long iter(MutableCharCharMap map) {
        long dummy = 0L;
        for (CharCharPair pair : map.keyValuesView()) {
            dummy ^= pair.getOne() + pair.getTwo();
        }
        return dummy;
    }

    private static it.unimi.dsi.fastutil.chars.Char2CharOpenHashMap fastutilCharCharMap(
            int loadLevel, int size) {
        return new it.unimi.dsi.fastutil.chars.Char2CharOpenHashMap(
                size, (float) (0.1 * loadLevel));
    }

    private static long iter(it.unimi.dsi.fastutil.chars.Char2CharOpenHashMap map) {
        long dummy = 0L;
        ObjectIterator<it.unimi.dsi.fastutil.chars.Char2CharMap.Entry> it = map.char2CharEntrySet()
                .fastIterator();
        while (it.hasNext()) {
            it.unimi.dsi.fastutil.chars.Char2CharMap.Entry entry = it.next();
            dummy ^= entry.getCharKey() + entry.getCharValue();
        }
        return dummy;
    }

    private static AbstractCharCharMap mahoutCharCharMap(int loadLevel, int size) {
        HashConfig config = config(loadLevel);
        int initialCapacity = (int) (size / config.getTargetLoad());
        return new OpenCharCharHashMap(initialCapacity, config.getMinLoad(), config.getMaxLoad());
    }

    private static char addValue(AbstractCharCharMap map, char key, char value) {
        return map.adjustOrPutValue(key, value, value);
    }

    /* define mapType */
    /* if Koloboke collections //com.koloboke.collect.map.CharCharMap
    // elif Trove collections //TCharCharMap
    // elif Hppc collections //com.carrotsearch.hppc.CharCharMap
    // elif Gs collections //MutableCharCharMap
    // elif Fastutil collections //it.unimi.dsi.fastutil.chars.Char2CharOpenHashMap
    // elif Mahout collections //AbstractCharCharMap
    // elif Std collections //StdCharCharMap
    // endif */
    /* enddefine */

    static class StdCharCharConsumer implements BiConsumer<Character, Character> {
        long dummy = 0L;
        @Override
        public void accept(Character a, Character b) {
            dummy ^= a + b;
        }

        public void forEach(StdCharCharMap map) {
            map.forEach(this);
        }
    }

    static class CharCharConsumer implements
            com.koloboke.function.CharCharConsumer, TCharCharProcedure, CharCharProcedure,
            com.gs.collections.api.block.procedure.primitive.CharCharProcedure {
        long dummy = 0L;

        @Override
        public void accept(char a, char b) {
            dummy ^= a + b;
        }

        /* with Koloboke|Hppc collections */
        public void forEach(/*mapType*/com.koloboke.collect.map.CharCharMap/**/ map) {
            map.forEach(this);
        }
        /* endwith */

        @Override
        public boolean execute(char a, char b) {
            dummy ^= a + b;
            return true;
        }

        public void forEach(TCharCharMap map) {
            map.forEachEntry(this);
        }

        @Override
        public void apply(char a, char b) {
            dummy ^= a + b;
        }

        @Override
        public void value(char a, char b) {
            dummy ^= a + b;
        }

        public void forEach(MutableCharCharMap map) {
            map.forEachKeyValue(this);
        }
    }

    static class MahoutCharCharConsumer
            implements org.apache.mahout.math.function.CharCharProcedure {
        long dummy = 0L;

        @Override
        public boolean apply(char a, char b) {
            dummy ^= a + b;
            return true;
        }

        public void forEach(AbstractCharCharMap map) {
            map.forEachPair(this);
        }
    }

    /* define consumerType */
    // if Std|Mahout collections //Koloboke// endif //CharCharConsumer
    /* enddefine */

    /* with Koloboke|Trove|Hppc|Gs|Fastutil|Mahout|Std collections */

    @State(Scope.Thread)
    public static class KolobokeCharCharMapState {
        Random r;
        char[] keys;
        /*mapType*/com.koloboke.collect.map.CharCharMap/**/ map;

        @Setup(Level.Trial)
        public void allocate() {
            r = ThreadLocalRandom.current();
            keys = new char[SIZE];
            map = kolobokeCharCharMap(LOAD_LEVEL, SIZE);
        }

        public void generateKeys() {
            for (int i = 0; i < keys.length; i++) {
                keys[i] = (char) r.nextLong();
            }
        }

        @TearDown(Level.Trial)
        public void recycle() {
            keys = null;
            map = null;
        }
    }

    public static class PutOpKolobokeCharCharMapState extends KolobokeCharCharMapState {
        @Setup(Level.Invocation)
        public void clearMap() {
            generateKeys();
            /* if !(Mahout collections) */map.clear();
            /* elif Mahout collections */map = kolobokeCharCharMap(LOAD_LEVEL, SIZE);
            /* endif */
        }
    }

    public static class QueryUpdateOpKolobokeCharCharMapState extends KolobokeCharCharMapState {
        @Setup(Level.Invocation)
        public void fillMap() {
            generateKeys();
            map.clear();
            for (char key : keys) {
                map.put(key, /* const key 1 */(char) 1/* endconst */);
            }
        }
    }

    @Benchmark
    public long putOp_kolobokeCollections_charKey(PutOpKolobokeCharCharMapState state) {
        char[] keys = state.keys;
        /*mapType*/com.koloboke.collect.map.CharCharMap/**/ map = state.map;
        for (char key : keys) {
            map.put(key, /* const key 1 */(char) 1/* endconst */);
        }
        return (long) map.size();
    }

    @Benchmark
    public long getOp_kolobokeCollections_charKey(QueryUpdateOpKolobokeCharCharMapState state) {
        char[] keys = state.keys;
        /*mapType*/com.koloboke.collect.map.CharCharMap/**/ map = state.map;
        long dummy = 0L;
        for (char key : keys) {
            dummy ^= (long) map.get(key);
        }
        return dummy;
    }

    /* if !(Fastutil collections) */
    @Benchmark
    public long addValueOp_kolobokeCollections_charKey(QueryUpdateOpKolobokeCharCharMapState state) {
        char[] keys = state.keys;
        /*mapType*/com.koloboke.collect.map.CharCharMap/**/ map = state.map;
        long dummy = 0L;
        for (char key : keys) {
            dummy ^= (long) addValue(map, key, /* const key 1 */(char) 1/* endconst */);
        }
        return dummy;
    }
    /* endif */

    /* if !(Fastutil collections) */
    @Benchmark
    public long forEachOp_kolobokeCollections_charKey(QueryUpdateOpKolobokeCharCharMapState state) {
        /*consumerType*/CharCharConsumer/**/ c = new /*consumerType*/CharCharConsumer/**/();
        c.forEach(state.map);
        return c.dummy;
    }
    /* endif */

    /* if !(Mahout collections) */
    @Benchmark
    public long iterOp_kolobokeCollections_charKey(QueryUpdateOpKolobokeCharCharMapState state) {
        return iter(state.map);
    }
    /* endif */

    /* endwith */
    /* endwith */

    public static void main(String[] args) throws RunnerException, CommandLineOptionException {
        if (Arrays.asList(args).contains("footprint")) {
            footPrintMain();
            return;
        }
        new DimensionedJmh(TimeVsMemory.class)
                .addArgDim("size", sizes.mapToObj(Integer::valueOf).toArray())
                .addArgDim("loadLevel", loadLevels.mapToObj(Integer::valueOf).toArray())
                .withGetOperationsPerInvocation(options -> parseInt(options.get("size")))
                .run(args);
    }

    static void footPrintMain() {
        int[] ss = sizes.toArray();
        loadLevels.forEach(loadLevel -> {
            /* with Koloboke|Trove|Hppc|Gs|Fastutil|Mahout|Std collections char|int key */
            /* if int key */
            Arrays.stream(ss).forEach(size -> {
                try {
                    /*mapType*/com.koloboke.collect.map.CharCharMap/**/ map =
                            kolobokeCharCharMap(loadLevel, size);
                    Random r = ThreadLocalRandom.current();
                    for (int i = 0; i < size; i++) {
                        map.put((char) r.nextLong(), /* const key 1 */
                                (char) 1/* endconst */);
                    }
                    double footPrint =
                            (parseInstance(map).totalSize() * 1.0 / size) / 8.0 - 1.0;
                    System.out.printf(Locale.US, "%d koloboke %.3f\n", loadLevel, footPrint);
                } catch (Throwable e) {
                }
            });
            /* endif */
            /* endwith */
        });
    }
}
