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
public class PermGen {

    private final HashSet<String> names = new HashSet<>(5);

    public PermGen() {
        names.add("Perm Gen");
        names.add("PS Perm Gen");
        names.add("CMS Perm Gen");
        names.add("G1 Perm Gen");
        names.add("Metaspace");
    }

    private static PermGen permGen;

    public static boolean isMember(String name) {
        if (permGen == null) {
            permGen = new PermGen();
        }
        return permGen.names.contains(name);
    }

}
