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
public enum OldGenerationSpace {

    SERIALGC("Tenured Gen",JVMCollector.SERIALGC),
    PARALLELGC("PS Old Gen",JVMCollector.PARALLELGC),
    CONCMARKSWEEP("CMS Old Gen",JVMCollector.CONCMARKSWEEP),
    G1GC("G1 Old Gen",JVMCollector.G1GC);

    private final String jmxName;
    private final JVMCollector collector;

    private OldGenerationSpace(String jmxName, JVMCollector collector) {
        this.jmxName = jmxName;
        this.collector = collector;
    }

    public JVMCollector getCollector() {
        return this.collector;
    }

    public String getJMXName() {
        return this.jmxName;
    }  
    
    public static OldGenerationSpace fromJVMCollector(JVMCollector collector) {
        if (collector != null) {
            for (OldGenerationSpace oldGenSpace : OldGenerationSpace.values()) {
                if (oldGenSpace.getCollector().equals(collector)) {
                    return oldGenSpace;
                }
            }
        }
        throw new IllegalArgumentException("Collector not found");
    }

    public static OldGenerationSpace fromJMXName(String jmxName) {
        if (jmxName != null) {
            for (OldGenerationSpace oldGenSpace : OldGenerationSpace.values()) {
                if (oldGenSpace.getJMXName().equals(jmxName)) {
                    return oldGenSpace;
                }
            }
        }
        throw new IllegalArgumentException("Collector not found");
    }
        
     public static OldGenerationSpace getEnum(String value) {
        for (OldGenerationSpace tmpEnum : values()) {
            if (tmpEnum.getJMXName().equalsIgnoreCase(value)) {
                return tmpEnum;
            }
        }
        throw new IllegalArgumentException();
    }   
    
}
