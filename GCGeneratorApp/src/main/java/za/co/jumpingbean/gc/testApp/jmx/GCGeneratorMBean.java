/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.jumpingbean.gc.testApp.jmx;

/**
 *
 * @author mark
 */
public interface GCGeneratorMBean {

    public String runLocalObjectCreator(int numInstances, int bytes, long creationDelay, long methodDelay);

    public String runLongLivedObjectCreator(int numInstances, int bytes, long creationDelay, long methodDelay);

    public void shutDown();

    public boolean isRunning();
    
    public void runGC();
}
