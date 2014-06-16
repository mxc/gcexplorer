/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package za.co.jumpingbean.gc.testApp;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;


/**
 *
 * @author mark
 */
public class Analiser {
    
    private final Counter  localObjectCount;
    private final Counter  longLivedObjectCount;
    
    public Analiser(MetricRegistry registry){
        this.localObjectCount = registry.counter("localClassCounter");
        this.longLivedObjectCount=registry.counter("longLivedObjectCount");
    }
    
    public long getLocalObjectCount(){
        return localObjectCount.getCount();
    }

    public void incLocalObjectCount() {
             localObjectCount.inc();
    }

    public void decLocalObjectCount(int i) {
            localObjectCount.dec(i);
    }
    
    public void incLongLivedObjectCount(){
        longLivedObjectCount.inc();
    }
    
    public void decLongLivedObjectCount(){
        longLivedObjectCount.dec();
    }
    
    public void devLongLovedObjectCount(long n){
        longLivedObjectCount.dec(n);
    }
}
