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
public enum YoungGenerationCollector {

    SERIALGC("Copy", JVMCollector.SERIALGC),
    PARALLELGC("PS Scavenge", JVMCollector.PARALLELGC),
    CONCMARKSWEEP("ParNew", JVMCollector.CONCMARKSWEEP),
    G1GC("G1 Young Generation", JVMCollector.G1GC);

    private final String jmxName;
    private final JVMCollector collector;

    private YoungGenerationCollector(String jmxName, JVMCollector collector) {
        this.jmxName = jmxName;
        this.collector = collector;
    }

    public static YoungGenerationCollector getEnum(String value) {
        for (YoungGenerationCollector tmpEnum : values()) {
            if (tmpEnum.getJMXName().equalsIgnoreCase(value)) {
                return tmpEnum;
            }
        }
        throw new IllegalArgumentException();
    }

    public JVMCollector getCollector() {
        return this.collector;
    }

    public String getJMXName() {
        return this.jmxName;
    }

    public static YoungGenerationCollector fromJVMCollector(JVMCollector collector) {
        if (collector != null) {
            for (YoungGenerationCollector youngGenCollector : YoungGenerationCollector.values()) {
                if (youngGenCollector.getCollector().equals(collector)) {
                    return youngGenCollector;
                }
            }
        }
        throw new IllegalArgumentException("Collector not found");
    }

    public static YoungGenerationCollector fromJMXName(String jmxName) {
        if (jmxName != null) {
            for (YoungGenerationCollector youngGenCollector : YoungGenerationCollector.values()) {
                if (youngGenCollector.getJMXName().equals(jmxName)) {
                    return youngGenCollector;
                }
            }
        }
        throw new IllegalArgumentException("Collector not found");
    }

}
