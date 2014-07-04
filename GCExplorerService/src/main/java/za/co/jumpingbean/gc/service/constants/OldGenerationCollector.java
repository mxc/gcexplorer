/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.jumpingbean.gc.service.constants;

import java.util.HashSet;

/**
 *
 * @author mark
 */
public class OldGenerationCollector {

    private final HashSet<String> names = new HashSet<>(4);

    public OldGenerationCollector() {
        names.add("MarkSweepCompact");
        names.add("PS MarkSweep");
        names.add("ConcurrentMarkSweep");
        names.add("G1 Old Generation");
    }

    private static OldGenerationCollector oldGenCollector;

    public static boolean isMember(String name) {
        if (oldGenCollector == null) {
            oldGenCollector = new OldGenerationCollector();
        }
        return oldGenCollector.names.contains(name);
    }
}
