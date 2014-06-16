/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.jumpingbean.gc.testApp.jmx;

import java.util.logging.Level;
import java.util.logging.Logger;
import za.co.jumpingbean.gc.testApp.LocalObjectGenerator;
import za.co.jumpingbean.gc.testApp.LongLivedObjectGenerator;
import za.co.jumpingbean.gc.testApp.GCGeneratorApp;

/**
 *
 * @author mark
 */
public class GCGenerator implements GCGeneratorMBean {

    private final LocalObjectGenerator localGenerator;
    private final LongLivedObjectGenerator longLivedGenerator;
    private boolean isRunning = true;
    private final GCGeneratorApp gen;

    public GCGenerator(LocalObjectGenerator localGenerator,
            LongLivedObjectGenerator longLivedGenerator, GCGeneratorApp gen) {

        this.localGenerator = localGenerator;
        this.longLivedGenerator = longLivedGenerator;
        this.gen = gen;

    }

    @Override
    public String runLocalObjectCreator(int numInstances, int size, long creationDelay, long methodDelay) {
        try {
            localGenerator.generate(numInstances, size, creationDelay, methodDelay);
            return "Ok";
        } catch (InterruptedException ex) {
            Logger.getLogger(GCGenerator.class.getName()).log(Level.SEVERE, null, ex);
            return "An error occurred";
        }
    }

    @Override
    public String runLongLivedObjectCreator(int numInstances, int size, long creationDelay, long methodDelay) {
        try {
            longLivedGenerator.generate(numInstances, size, creationDelay, methodDelay);
            return "Ok";
        } catch (InterruptedException ex) {
            Logger.getLogger(GCGenerator.class.getName()).log(Level.SEVERE, null, ex);
            return "An error occurred";
        }
    }

    @Override
    public void shutDown() {
        synchronized (gen) {
            isRunning = false;
            gen.notifyAll();
        }
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public void runGC() {
            System.gc();
            
    }

}
