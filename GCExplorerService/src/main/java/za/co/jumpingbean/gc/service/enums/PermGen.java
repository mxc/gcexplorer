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
public enum PermGen {

    SERIALGC("Perm Gen",JVMCollector.SERIALGC),
    PARALLELGC("PS Perm Gen", JVMCollector.PARALLELGC),
    CONCMARKSWEEP("CMS Perm Gen", JVMCollector.CONCMARKSWEEP),
    G1GC("G1 Perm Gen",JVMCollector.G1GC);

    private final String jmxName;
    private final JVMCollector collector;

    private PermGen(String jmxName, JVMCollector collector) {
        this.jmxName = jmxName;
        this.collector = collector;
    }

    public JVMCollector getCollector() {
        return this.collector;
    }

    public String getJMXName() {
        return this.jmxName;
    }
    
    public static PermGen fromJVMCollector(JVMCollector collector) {
        if (collector != null) {
            for (PermGen permGen : PermGen.values()) {
                if (permGen.getCollector().equals(collector)) {
                    return permGen;
                }
            }
        }
        throw new IllegalArgumentException("Collector not found");
    }

    public static PermGen fromJMXName(String jmxName) {
        if (jmxName != null) {
            for (PermGen permGen : PermGen.values()) {
                if (permGen.getJMXName().equals(jmxName)) {
                    return permGen;
                }
            }
        }
        throw new IllegalArgumentException("Collector not found");
    }
    
    public static PermGen getEnum(String value) {
        for (PermGen tmpEnum : values()) {
            if (tmpEnum.getJMXName().equalsIgnoreCase(value)) {
                return tmpEnum;
            }
        }
        throw new IllegalArgumentException();
    }    
}
