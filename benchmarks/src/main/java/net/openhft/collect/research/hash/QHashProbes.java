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

package net.openhft.collect.research.hash;

import net.openhft.collect.impl.hash.QHashCapacities;
import net.openhft.collect.map.DoubleObjMap;
import net.openhft.collect.map.hash.HashDoubleObjMaps;

import java.util.*;


public class QHashProbes {

    private static class Probes {
        int count = 0;
        long probes = 0L;
        void add(int probes) {
            count++;
            this.probes += probes;
        }
    }

    public static void main(String[] args) {
        int capacity = QHashCapacities.getIntCapacity(Integer.parseInt(args[0]), 0);
        NoStatesQHashIntSet hash = new NoStatesQHashIntSet(capacity);
        int repeats = Integer.parseInt(args[1]);
        DoubleObjMap<Probes> stats = HashDoubleObjMaps.newMutableMap(capacity);
        Random r = new Random();
        for (int i = 0; i < repeats; i++) {
            hash.clear();
            while (hash.size < capacity - 1) {
                int probes;
                double load = ((double) hash.size) / capacity;
                while ((probes = hash.addBinaryStateCountingProbes(r.nextInt())) <= 0);
                stats.computeIfAbsent(load, l -> new Probes()).add(probes);
            }
        }
        double step = Double.parseDouble(args[2]);
        SortedMap<Double, Probes> sortedStats = new TreeMap<>(stats);
        for (double from = 0.0, to = step; ; to += step) {
            long count = 0L;
            long probes = 0L;
            for (Probes ps : sortedStats.subMap(from, to).values()) {
                count += ps.count;
                probes += ps.probes;
            }
            System.out.println(((double) probes) / count);
            if (to > 1.0)
                break;
            from = to;
        }
    }
}
