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
package za.co.jumpingbean.gc.testapp.jmx;

import za.co.jumpingbean.gc.testapp.LocalObjectGenerator;
import za.co.jumpingbean.gc.testapp.LongLivedObjectGenerator;
import za.co.jumpingbean.gc.testapp.GarbageGeneratorApp;

/**
 *
 * @author mark
 */
public class GCGenerator implements GCGeneratorMBean {

    private final LocalObjectGenerator localGenerator;
    private final LongLivedObjectGenerator longLivedGenerator;
    private boolean isRunning = true;
    private final GarbageGeneratorApp gen;

    public GCGenerator(LocalObjectGenerator localGenerator,
            LongLivedObjectGenerator longLivedGenerator, GarbageGeneratorApp gen) {

        this.localGenerator = localGenerator;
        this.longLivedGenerator = longLivedGenerator;
        this.gen = gen;

    }

    @Override
    public String runLocalObjectCreator(int numInstances, int size, long creationDelay) {
        try {
            localGenerator.generate(numInstances, size, creationDelay);
            return "Ok";
        } catch (OutOfMemoryError ex){
            return "Out of memory!";
        }
    }

    @Override
    public String runLongLivedObjectCreator(int numInstances, int size, long creationDelay) {
        try {
            longLivedGenerator.generate(numInstances, size, creationDelay);
            return "Ok";
        } catch (OutOfMemoryError ex){
            return "Out of memory!";
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

    @Override
    public void releaseLongLivedObjects(int numInstances, boolean reverse) {
            longLivedGenerator.releaseLongLived(numInstances, reverse);
    }

    @Override
    public int getLongLivedObjectsCount() {
        return longLivedGenerator.getObjectCount();
    }

    @Override
    public int getLongLivedObjectsMemorySize() {
        return longLivedGenerator.getApproximateMemoryOccupied();
    }

}
