/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.jumpingbean.gc.service;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import java.util.LinkedList;
import java.util.List;
import za.co.jumpingbean.gc.testApp.GarbageGeneratorApp;

/**
 *
 * @author Mark Clarke
 */
public class LocalJavaProcessFinder {

    public static List<String> getLocalJavaProcesses() {
        List<String> localProcesses = new LinkedList<>();
            List<VirtualMachineDescriptor> vms = VirtualMachine.list();
            for(VirtualMachineDescriptor vmd: vms){
                if (!vmd.displayName().contains(GarbageGeneratorApp.class.getSimpleName())) {
                    localProcesses.add(vmd.id()+" "+vmd.displayName());
                }
            }
        return localProcesses;
    }

}
