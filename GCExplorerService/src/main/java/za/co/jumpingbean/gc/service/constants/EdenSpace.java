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
public class EdenSpace {

    private final HashSet<String> names = new HashSet<>(4);

    public EdenSpace() {
        names.add("Eden Space");
        names.add("PS Eden Space");
        names.add("Par Eden Space");
        names.add("G1 Eden Space");
    }

    private static EdenSpace edenSpace;

    public static boolean isMember(String name) {
        if (edenSpace == null) {
            edenSpace = new EdenSpace();
        }
        return edenSpace.names.contains(name);
    }

}
