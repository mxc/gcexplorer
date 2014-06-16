/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.jumpingbean.gc.service.enums;

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
