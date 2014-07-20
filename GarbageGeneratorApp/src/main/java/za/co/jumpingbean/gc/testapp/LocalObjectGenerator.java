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

import javax.inject.Inject;

/**
 *
 * @author mark
 */
public class LocalObjectGenerator {

    @Inject
    public LocalObjectGenerator() {
    }

    /**
     * Generate some short lived objects Generate @numInstances of {TestObject}
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
    public void generate(int numInstances, int instanceSize, long creationDelay)  {
        TestObject objs[] = new TestObject[numInstances];
        int i = 0;
        for (i = 1; i <= numInstances; i++) {
            objs[i - 1] = new TestObject(instanceSize * 1024 * 1024);
            try{
                Thread.sleep(creationDelay);
            }catch(InterruptedException ex){
                //continuecreating!
            }
        }
    }

}
