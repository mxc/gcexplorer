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
package za.co.jumpingbean.gc.testapp;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mark
 */
public class LongLivedObjectGenerator {

    private final List<TestObject> list;
    private int size = 0;

    public LongLivedObjectGenerator() {
        list = new ArrayList<TestObject>();
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
     */
    public void generate(int numInstances, int instanceSize, long creationDelay) {
        int i = 0;
        synchronized (list) {
            for (i = 1; i <= numInstances; i++) {
                list.add(new TestObject(instanceSize * 1024 * 1024));
                size += instanceSize;
                try {
                    Thread.sleep(creationDelay);
                } catch (InterruptedException ex) {
                    //continue creating!
                }
            }
        }
    }

    public void releaseLongLived(int numInstances, boolean reverse) {
        synchronized (list) {
            if (list.size() <= numInstances) {
                list.clear();
                size = 0;
            }
            List<TestObject> subList;
            if (reverse) {
                int end = list.size() - 1;
                int start = end + 1 - numInstances;
                subList = new ArrayList<TestObject>();
                for (int j = start; j <= end; j++) {
                    subList.add(list.get(j));
                }
            } else {
                subList = list.subList(0, (numInstances));
            }
            int tmpSize = 0;
            for (TestObject obj : subList) {
                tmpSize += obj.getSize();
            }
            size -= tmpSize;
            list.removeAll(subList);
        }
    }

    public int getObjectCount() {
        synchronized (list) {
            return this.list.size();
        }
    }

    public int getApproximateMemoryOccupied() {
        return this.size;
    }
}
