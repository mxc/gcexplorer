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
public enum EdenSpace {

    SERIALGC("Eden Space", JVMCollector.SERIALGC),
    PARALLELGC("PS Eden Space", JVMCollector.PARALLELGC),
    CONCMARKSWEEP("Par Eden Space", JVMCollector.CONCMARKSWEEP),
    G1GC("G1 Eden Space", JVMCollector.G1GC);

    private final String jmxName;
    private final JVMCollector collector;

    private EdenSpace(String jmxName, JVMCollector collector) {
        this.jmxName = jmxName;
        this.collector = collector;
    }

    public JVMCollector getCollector() {
        return this.collector;
    }

    public String getJMXName() {
        return this.jmxName;
    }

    public static EdenSpace fromJVMCollector(JVMCollector collector) {
        if (collector != null) {
            for (EdenSpace eden : EdenSpace.values()) {
                if (eden.getCollector().equals(collector)) {
                    return eden;
                }
            }
        }
        throw new IllegalArgumentException("Collector not found");
    }

    public static EdenSpace fromJMXName(String jmxName) {
        if (jmxName != null) {
            for (EdenSpace eden : EdenSpace.values()) {
                if (eden.getJMXName().equals(jmxName)) {
                    return eden;
                }
            }
        }
        throw new IllegalArgumentException("Collector not found");
    }
    
    public static EdenSpace getEnum(String value) {
        for (EdenSpace tmpEnum : values()) {
            if (tmpEnum.getJMXName().equalsIgnoreCase(value)) {
                return tmpEnum;
            }
        }
        throw new IllegalArgumentException();
    }    
}
