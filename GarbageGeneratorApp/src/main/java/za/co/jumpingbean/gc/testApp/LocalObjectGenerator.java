/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.jumpingbean.gc.testApp;

import javax.inject.Inject;

/**
 *
 * @author mark
 */
public class LocalObjectGenerator {

    private final Analiser analiser;

    @Inject
    public LocalObjectGenerator(Analiser analiser) {
        this.analiser = analiser;
    }

    /**
     * Generate some short lived objects
     * Generate @numInstances of {TestObject} with a memory size in mega bytes of
     * @instanceSize. Wait @creationDelay between each object creation and then
     * wait @methodReturnDelay before returning from the method.
     * 
     * Instance size is in megabytes.
     * 
     * @param numInstances
     * @param instanceSize
     * @param creationDelay
     * @param methodReturnDelay
     * @throws InterruptedException 
     */
    public void generate(int numInstances, int instanceSize, long creationDelay,
            long methodReturnDelay) throws InterruptedException {
        TestObject objs[] = new TestObject[numInstances];
        int i = 0;
        try {
            for (i = 1; i <= numInstances; i++) {
                analiser.incLocalObjectCount();
                objs[i-1] = new TestObject(instanceSize*1024*1024);
                Thread.sleep(creationDelay);
            }
           synchronized(this){
                this.wait(methodReturnDelay);
           }
        } finally {
            analiser.decLocalObjectCount(i);
        }
    }

}
