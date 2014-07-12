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

import com.codahale.metrics.MetricRegistry;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.IsCloseTo.closeTo;
import org.junit.Test;
import org.mockito.Mockito;
import za.co.jumpingbean.gc.testApp.Analiser;
import za.co.jumpingbean.gc.testApp.jmx.GCGenerator;
import za.co.jumpingbean.gc.testApp.jmx.GCGeneratorMBean;
import za.co.jumpingbean.gc.testApp.LocalObjectGenerator;
import za.co.jumpingbean.gc.testApp.LongLivedObjectGenerator;
import za.co.jumpingbean.gc.testApp.GarbageGeneratorApp;

/**
 *
 * @author mark
 */
public class GCGeneratorAppTest {
    @Test
    public void testLocalVariableGeneration() {
        try {
            Analiser analiser = Mockito.mock(Analiser.class);
            LocalObjectGenerator localGen = new LocalObjectGenerator(analiser);
            Long start = System.currentTimeMillis();
            localGen.generate(10, 10, 50);
            Long end = System.currentTimeMillis();
            assertThat(((double) end - (double) start), closeTo(500d, 200d));
        } catch (InterruptedException ex) {
            assertThat("Interrupted Exception", false);
        }
    }

    @Test
    public void testLongLivedObjectsTest() throws InterruptedException {
        try {
            Analiser analiser = Mockito.mock(Analiser.class);
            Long start = System.currentTimeMillis();
            LongLivedObjectGenerator gen = new LongLivedObjectGenerator(analiser);
            gen.generate(10, 10, 50);
            Long end = System.currentTimeMillis();
            assertThat(((double) end - (double) start), closeTo(500d, 200d));
        } catch(InterruptedException ex) {
            assertThat("Interrupted Exception", false);
        }
    }

    @Test
    public void testClassCreationMetric() {
        Analiser analiser = new Analiser(new MetricRegistry());
        assertThat(analiser.getLocalObjectCount(), is(equalTo(0L)));
        for (int i = 1; i <= 10; i++) {
            analiser.incLocalObjectCount();
        }
        assertThat(analiser.getLocalObjectCount(), is(equalTo(10L)));
        analiser.decLocalObjectCount(10);
        assertThat(analiser.getLocalObjectCount(), is(equalTo(0L)));
    }
    
    @Test
    public void testMBean(){
        LongLivedObjectGenerator ll = Mockito.mock(LongLivedObjectGenerator.class);
        LocalObjectGenerator sl = Mockito.mock(LocalObjectGenerator.class);
        GarbageGeneratorApp gen = Mockito.mock(GarbageGeneratorApp.class);
        GCGeneratorMBean bean = new GCGenerator(sl,ll,gen);
        String result = bean.runLocalObjectCreator(1,1,1);
        assertThat (result,is("Ok"));
        result= bean.runLongLivedObjectCreator(1,1,1);
        assertThat (result,is("Ok"));
    }    
}
