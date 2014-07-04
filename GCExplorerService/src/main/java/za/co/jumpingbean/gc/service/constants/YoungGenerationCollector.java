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
public class YoungGenerationCollector {

    private final HashSet<String> names = new HashSet<>(4);

    public YoungGenerationCollector() {
        names.add("Copy");
        names.add("PS Scavenge");
        names.add("ParNew");
        names.add("G1 Young Generation");
    }

    private static YoungGenerationCollector youngCollector;

    public static boolean isMember(String name) {
        if (youngCollector == null) {
            youngCollector = new YoungGenerationCollector();
        }
        return youngCollector.names.contains(name);
    }
}
