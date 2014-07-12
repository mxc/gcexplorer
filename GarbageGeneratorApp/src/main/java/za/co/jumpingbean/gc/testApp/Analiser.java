/* 
 * Copyright (C) 2014 Mark Clarke
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
