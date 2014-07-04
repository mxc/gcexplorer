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
public class SurvivorSpace {

    private final HashSet<String> names = new HashSet<>(4);

    public SurvivorSpace(){
        names.add("Survivor Space");
        names.add("PS Survivor Space");
        names.add("Par Survivor Space");
        names.add("G1 Survivor Space");
    }

    private static SurvivorSpace survivorSpace;
    
    public static boolean isMember(String name) {
        if(survivorSpace==null){
            survivorSpace = new SurvivorSpace();
        }
        return survivorSpace.names.contains(name);
    }

}
