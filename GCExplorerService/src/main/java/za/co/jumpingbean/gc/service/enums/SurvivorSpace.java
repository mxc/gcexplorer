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
public enum SurvivorSpace {

    SERIALGC("Survivor Space",JVMCollector.SERIALGC),
    PARALLELGC("PS Survivor Space", JVMCollector.PARALLELGC),
    CONCMARKSWEEP("Par Survivor Space", JVMCollector.CONCMARKSWEEP),
    G1GC("G1 Survivor Space",JVMCollector.G1GC);

    private final String jmxName;
    private final JVMCollector collector;

    private SurvivorSpace(String jmxName, JVMCollector collector) {
        this.jmxName = jmxName;
        this.collector = collector;
    }

    public JVMCollector getCollector() {
        return this.collector;
    }

    public String getJMXName() {
        return this.jmxName;
    }
    
    public static SurvivorSpace fromJVMCollector(JVMCollector collector) {
        if (collector != null) {
            for (SurvivorSpace permGen : SurvivorSpace.values()) {
                if (permGen.getCollector().equals(collector)) {
                    return permGen;
                }
            }
        }
        throw new IllegalArgumentException("Collector not found");
    }

    public static SurvivorSpace fromJMXName(String jmxName) {
        if (jmxName != null) {
            for (SurvivorSpace permGen : SurvivorSpace.values()) {
                if (permGen.getJMXName().equals(jmxName)) {
                    return permGen;
                }
            }
        }
        throw new IllegalArgumentException("Collector not found");
    }   
    
    public static SurvivorSpace getEnum(String value) {
        for (SurvivorSpace tmpEnum : values()) {
            if (tmpEnum.getJMXName().equalsIgnoreCase(value)) {
                return tmpEnum;
            }
        }
        throw new IllegalArgumentException();
    }    
}
