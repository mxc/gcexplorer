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
    public void generate(int numInstances, int instanceSize, long creationDelay) throws InterruptedException {
        int i = 0;
        try {
            for (i = 1; i <= numInstances; i++) {
                analiser.incLocalObjectCount();
                list.add(new TestObject(instanceSize*1024*1024));
                Thread.sleep(creationDelay);
            }
        } finally {
            analiser.decLocalObjectCount(i);
        }
    }
    

    public void releaseLongLived(int numInstances,boolean reverse){
        if (list.size()<=numInstances){
            list.clear();
        }
        if (reverse){
            int end = list.size()-1;
            int start = end=numInstances;
            list.removeAll(list.subList(start,end));
        }else{
            list.removeAll(list.subList(0,(numInstances-1)));
        }
    }
}
