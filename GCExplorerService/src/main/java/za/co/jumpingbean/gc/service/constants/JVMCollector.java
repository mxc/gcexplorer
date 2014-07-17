/* 
 * Copyright (C) 2014 Mark Clarke
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package za.co.jumpingbean.gc.service.constants;

/**
 *
 * @author mark
 */
public enum JVMCollector {

    SERIALGC("+UseSerialGC","Serial Collector"),
    PARALLELGC("+UseParallelGC","Throughput Collector"),
    CONCMARKSWEEP("+ConcMarkSweep","Concurrent Low Pause Collector"),
    G1GC("+UseG1GC","G1 Collector");

    private final String jvmOption;
    private final String commonName;

    private JVMCollector(String jmxName, String collectorName) {
        this.jvmOption = jmxName;
        this.commonName = collectorName;
    }

    String getCommonName() {
        return this.commonName;
    }

    String getJvmOption() {
        return this.jvmOption;
    }

}
