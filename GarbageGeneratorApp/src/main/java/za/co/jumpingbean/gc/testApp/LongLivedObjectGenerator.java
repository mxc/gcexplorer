/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.jumpingbean.gc.testApp;

import java.util.ArrayList;
import java.util.List;
import za.co.jumpingbean.gc.testApp.Analiser;
/**
 *
 * @author mark
 */
public class LongLivedObjectGenerator {

    private List<TestObject> list;
    private Analiser analiser;

    public LongLivedObjectGenerator(Analiser analiser) {
        list = new ArrayList<>();
        this.analiser = analiser;
    }

    /**
     * Generate some long lived objects Generate @numInstances of {TestObject}
     * with a memory size in mega bytes of
     *
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
        int i = 0;
        try {
            for (i = 1; i <= numInstances; i++) {
                analiser.incLocalObjectCount();
                list.add(new TestObject(instanceSize*1024*1024));
                Thread.sleep(creationDelay);
            }
            Thread.sleep(methodReturnDelay);
        } finally {
            analiser.decLocalObjectCount(i);
        }
    }
}
