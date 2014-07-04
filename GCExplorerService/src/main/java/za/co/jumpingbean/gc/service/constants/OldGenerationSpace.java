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
public class OldGenerationSpace {

    private final HashSet<String> names = new HashSet<>(4);

    public OldGenerationSpace() {
        names.add("Tenured Gen");
        names.add("PS Old Gen");
        names.add("CMS Old Gen");
        names.add("G1 Old Gen");
    }

    private static OldGenerationSpace oldGen;

    public static boolean isMember(String name) {
        if (oldGen == null) {
            oldGen = new OldGenerationSpace();
        }
        return oldGen.names.contains(name);
    }

}
