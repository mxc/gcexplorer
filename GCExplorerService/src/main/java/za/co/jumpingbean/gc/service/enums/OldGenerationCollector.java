/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.jumpingbean.gc.service.enums;

import static za.co.jumpingbean.gc.service.enums.YoungGenerationCollector.values;

/**
 *
 * @author mark
 */
public enum OldGenerationCollector {

    SERIALGC("MarkSweepCompact", JVMCollector.SERIALGC),
    PARALLELGC("PS MarkSweep", JVMCollector.PARALLELGC),
    CONCMARKSWEEP("ConcurrentMarkSweep",JVMCollector.CONCMARKSWEEP),
    G1GC("G1 Old Generation",JVMCollector.G1GC);   
    
    private final String jmxName;
    private final JVMCollector collector;

    private OldGenerationCollector(String jmxName, JVMCollector collector) {
        this.jmxName = jmxName;
        this.collector = collector;
    }

    public JVMCollector getCollector() {
        return this.collector;
    }

    public String getJMXName() {
        return this.jmxName;
    }
    
    public static OldGenerationCollector fromJVMCollector(JVMCollector collector) {
        if (collector != null) {
            for (OldGenerationCollector oldGen : OldGenerationCollector.values()) {
                if (oldGen.getCollector().equals(collector)) {
                    return oldGen;
                }
            }
        }
        throw new IllegalArgumentException("Collector not found");
    }

    public static OldGenerationCollector fromJMXName(String jmxName) {
        if (jmxName != null) {
            for (OldGenerationCollector oldGen : OldGenerationCollector.values()) {
                if (oldGen.getJMXName().equals(jmxName)) {
                    return oldGen;
                }
            }
        }
        throw new IllegalArgumentException("Collector not found");
    }
    
    public static OldGenerationCollector getEnum(String value) {
        for (OldGenerationCollector tmpEnum : values()) {
            if (tmpEnum.getJMXName().equalsIgnoreCase(value)) {
                return tmpEnum;
            }
        }
        throw new IllegalArgumentException();
    }    
    
}
