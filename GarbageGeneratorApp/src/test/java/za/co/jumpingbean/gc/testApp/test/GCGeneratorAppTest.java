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
package za.co.jumpingbean.gc.testApp.test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.IsCloseTo.closeTo;
import org.junit.Test;
import org.mockito.Mockito;
import za.co.jumpingbean.gc.testapp.jmx.GCGenerator;
import za.co.jumpingbean.gc.testapp.jmx.GCGeneratorMBean;
import za.co.jumpingbean.gc.testapp.LocalObjectGenerator;
import za.co.jumpingbean.gc.testapp.LongLivedObjectGenerator;
import za.co.jumpingbean.gc.testapp.GarbageGeneratorApp;

/**
 *
 * @author mark
 */
public class GCGeneratorAppTest {

    @Test
    public void localVariableGenerationTest() throws InterruptedException {
            //Analiser analiser = Mockito.mock(Analiser.class);
            LocalObjectGenerator localGen = new LocalObjectGenerator();
            Long start = System.currentTimeMillis();
            localGen.generate(10, 10, 50);
            Long end = System.currentTimeMillis();
            assertThat(((double) end - (double) start), closeTo(500d, 200d));
    }

    @Test
    public void longLivedObjectsCreationTest() throws InterruptedException {
        //Analiser analiser = Mockito.mock(Analiser.class);
        Long start = System.currentTimeMillis();
        LongLivedObjectGenerator gen = new LongLivedObjectGenerator();
        gen.generate(10, 10, 50);
        Long end = System.currentTimeMillis();
        assertThat(((double) end - (double) start), closeTo(500d, 300d));
        assertThat(gen.getObjectCount(), equalTo(10));
        assertThat(gen.getApproximateMemoryOccupied(), equalTo(100));
    }

    @Test
    public void longLivedObjectsReleaseTest() throws InterruptedException {
        //Analiser analiser = Mockito.mock(Analiser.class);
        LongLivedObjectGenerator gen = new LongLivedObjectGenerator();
        gen.generate(10, 10, 50);
        assertThat(gen.getObjectCount(), equalTo(10));
        assertThat(gen.getApproximateMemoryOccupied(), equalTo(100));
        gen.releaseLongLived(4, true);
        assertThat(gen.getObjectCount(), equalTo(6));
        assertThat(gen.getApproximateMemoryOccupied(), equalTo(60));
    }

    @Test
    public void longLivedObjectsDiffSizeReleaseEndTest() throws InterruptedException {
        //Analiser analiser = Mockito.mock(Analiser.class);
        LongLivedObjectGenerator gen = new LongLivedObjectGenerator();
        gen.generate(10, 10, 50);
        gen.generate(10, 5, 50);
        assertThat(gen.getObjectCount(), equalTo(20));
        assertThat(gen.getApproximateMemoryOccupied(), equalTo(150));
        gen.releaseLongLived(11, true);
        assertThat(gen.getObjectCount(), equalTo(9));
        assertThat(gen.getApproximateMemoryOccupied(), equalTo(90));
    }

    @Test
    public void longLivedObjectsDiffSizeReleaseStartTest() throws InterruptedException {
        //Analiser analiser = Mockito.mock(Analiser.class);
        LongLivedObjectGenerator gen = new LongLivedObjectGenerator();
        gen.generate(10, 10, 50);
        gen.generate(10, 5, 50);
        assertThat(gen.getObjectCount(), equalTo(20));
        assertThat(gen.getApproximateMemoryOccupied(), equalTo(150));
        gen.releaseLongLived(11, false);
        assertThat(gen.getObjectCount(), equalTo(9));
        assertThat(gen.getApproximateMemoryOccupied(), equalTo(45));
    }

//    @Test
//    public void testClassCreationMetric() {
//        //Analiser analiser = new Analiser(new MetricRegistry());
//        assertThat(analiser.getLocalObjectCount(), is(equalTo(0L)));
//        for (int i = 1; i <= 10; i++) {
//            analiser.incLocalObjectCount();
//        }
//        assertThat(analiser.getLocalObjectCount(), is(equalTo(10L)));
//        analiser.decLocalObjectCount(10);
//        assertThat(analiser.getLocalObjectCount(), is(equalTo(0L)));
//    }
    @Test
    public void testMBean() {
        LongLivedObjectGenerator ll = Mockito.mock(LongLivedObjectGenerator.class);
        LocalObjectGenerator sl = Mockito.mock(LocalObjectGenerator.class);
        GarbageGeneratorApp gen = Mockito.mock(GarbageGeneratorApp.class);
        GCGeneratorMBean bean = new GCGenerator(sl, ll, gen);
        String result = bean.runLocalObjectCreator(1, 1, 1);
        assertThat(result, is("Ok"));
        result = bean.runLongLivedObjectCreator(1, 1, 1);
        assertThat(result, is("Ok"));
    }
}
