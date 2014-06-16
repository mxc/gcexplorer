/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.jumpingbean.gc.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.util.LinkedList;
import java.util.List;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import javax.management.MalformedObjectNameException;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;
import za.co.jumpingbean.gc.service.Generator;
import za.co.jumpingbean.gc.service.JMXQueryRunner;
import za.co.jumpingbean.gc.service.enums.EdenSpace;
import za.co.jumpingbean.gc.service.enums.JVMCollector;
import za.co.jumpingbean.gc.service.enums.OldGenerationCollector;
import za.co.jumpingbean.gc.service.enums.OldGenerationSpace;
import za.co.jumpingbean.gc.service.enums.PermGen;
import za.co.jumpingbean.gc.service.enums.SurvivorSpace;
import za.co.jumpingbean.gc.service.enums.YoungGenerationCollector;



/**
 *
 * @author mark
 */
public class GCGeneratorTest {

    @Test
    public void testJMXQueryBrowser() {

    }

    @Test
    public void testSerialGCEnums() {
        JVMCollector serial = JVMCollector.SERIALGC;
        assertThat(EdenSpace.fromJVMCollector(serial).getJMXName(), is("Eden Space"));
        assertThat(SurvivorSpace.fromJVMCollector(serial).getJMXName(), is("Survivor Space"));
        assertThat(OldGenerationSpace.fromJVMCollector(serial).getJMXName(), is("Tenured Gen"));
        assertThat(PermGen.fromJVMCollector(serial).getJMXName(), is("Perm Gen"));

        assertThat(YoungGenerationCollector.fromJVMCollector(serial).getJMXName(), is("Copy"));
        assertThat(OldGenerationCollector.fromJVMCollector(serial).getJMXName(), is("MarkSweepCompact"));
    }

    @Test
    public void testParallelGCEnums() {
        JVMCollector parallelGC = JVMCollector.PARALLELGC;
        assertThat(EdenSpace.fromJVMCollector(parallelGC).getJMXName(), is("PS Eden Space"));
        assertThat(SurvivorSpace.fromJVMCollector(parallelGC).getJMXName(), is("PS Survivor Space"));
        assertThat(OldGenerationSpace.fromJVMCollector(parallelGC).getJMXName(), is("PS Old Gen"));
        assertThat(PermGen.fromJVMCollector(parallelGC).getJMXName(), is("PS Perm Gen"));

        assertThat(YoungGenerationCollector.fromJVMCollector(parallelGC).getJMXName(), is("PS Scavenge"));
        assertThat(OldGenerationCollector.fromJVMCollector(parallelGC).getJMXName(), is("PS MarkSweep"));
    }

    @Test
    public void testConcMarkSweepGCEnums() {
        JVMCollector concMarkSweepGC = JVMCollector.CONCMARKSWEEP;
        assertThat(EdenSpace.fromJVMCollector(concMarkSweepGC).getJMXName(), is("Par Eden Space"));
        assertThat(SurvivorSpace.fromJVMCollector(concMarkSweepGC).getJMXName(), is("Par Survivor Space"));
        assertThat(OldGenerationSpace.fromJVMCollector(concMarkSweepGC).getJMXName(), is("CMS Old Gen"));
        assertThat(PermGen.fromJVMCollector(concMarkSweepGC).getJMXName(), is("CMS Perm Gen"));

        assertThat(YoungGenerationCollector.fromJVMCollector(concMarkSweepGC).getJMXName(), is("ParNew"));
        assertThat(OldGenerationCollector.fromJVMCollector(concMarkSweepGC).getJMXName(), is("ConcurrentMarkSweep"));
    }

    @Test
    public void testG1GCEnums() {
        JVMCollector concMarkSweepGC = JVMCollector.G1GC;
        assertThat(EdenSpace.fromJVMCollector(concMarkSweepGC).getJMXName(), is("G1 Eden Space"));
        assertThat(SurvivorSpace.fromJVMCollector(concMarkSweepGC).getJMXName(), is("G1 Survivor Space"));
        assertThat(OldGenerationSpace.fromJVMCollector(concMarkSweepGC).getJMXName(), is("G1 Old Gen"));
        assertThat(PermGen.fromJVMCollector(concMarkSweepGC).getJMXName(), is("G1 Perm Gen"));

        assertThat(YoungGenerationCollector.fromJVMCollector(concMarkSweepGC).getJMXName(), is("G1 Young Generation"));
        assertThat(OldGenerationCollector.fromJVMCollector(concMarkSweepGC).getJMXName(), is("G1 Old Generation"));
    }

    @Test
    public void testStartStopTestApp() throws CannotCompileException, IOException, NotFoundException, InterruptedException, MalformedObjectNameException {
        createTmpClassMainClass();
        Generator gen = new Generator();
        List<String> cmdOption = new LinkedList<>();
        cmdOption.add("-XX:+UseConcMarkSweepGC");
        Process proc = gen.startTestApp("8181", "", "Test", cmdOption);
        BufferedReader reader2 = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        String output = reader2.readLine();
        assertThat(output, is(equalTo("Hello World")));
        gen.stopTestApp(proc);
    }

    @Test
    public void testJMXQueryRunnerConcMarkSweepGC() throws IOException, CannotCompileException, NotFoundException, MalformedObjectNameException {
        createTmpClassMainClass();
        Generator gen = new Generator();
        List<String> cmdOption = new LinkedList<>();
        cmdOption.add("-XX:+UseConcMarkSweepGC");
        Process proc = gen.startTestApp("8181", "", "Test", cmdOption);
        JMXQueryRunner runner = new JMXQueryRunner("8181");
        
        runner.init();

        assertThat(runner.getOldGenCollector(), is(notNullValue()));
        assertThat(runner.getYoungGenCollector(), is(notNullValue()));
        assertThat(runner.getEdenSpace(), is(notNullValue()));
        assertThat(runner.getPermGen(), is(notNullValue()));
        assertThat(runner.getSurvivorSpace(), is(notNullValue()));
        assertThat(runner.getOldGen(), is(notNullValue()));
        
        gen.stopTestApp(proc);
    }


    @Test
    public void testJMXQueryRunnerSerialGC() throws IOException, CannotCompileException, NotFoundException, MalformedObjectNameException {
        createTmpClassMainClass();
        Generator gen = new Generator();
        List<String> cmdOption = new LinkedList<>();
        cmdOption.add("-XX:+UseSerialGC");
        Process proc = gen.startTestApp("8181", "", "Test", cmdOption);
        JMXQueryRunner runner = new JMXQueryRunner("8181");
        
        runner.init();

        assertThat(runner.getOldGenCollector(), is(notNullValue()));
        assertThat(runner.getYoungGenCollector(), is(notNullValue()));
        assertThat(runner.getEdenSpace(), is(notNullValue()));
        assertThat(runner.getPermGen(), is(notNullValue()));
        assertThat(runner.getSurvivorSpace(), is(notNullValue()));
        assertThat(runner.getOldGen(), is(notNullValue()));
        
        gen.stopTestApp(proc);
    }    

    @Test
    public void testJMXQueryRunnerParallelGC() throws IOException, CannotCompileException, NotFoundException, MalformedObjectNameException {
        createTmpClassMainClass();
        Generator gen = new Generator();
        List<String> cmdOption = new LinkedList<>();
        cmdOption.add("-XX:+UseParallelGC");
        Process proc = gen.startTestApp("8181", "", "Test", cmdOption);
        JMXQueryRunner runner = new JMXQueryRunner("8181");
        
        runner.init();

        assertThat(runner.getOldGenCollector(), is(notNullValue()));
        assertThat(runner.getYoungGenCollector(), is(notNullValue()));
        assertThat(runner.getEdenSpace(), is(notNullValue()));
        assertThat(runner.getPermGen(), is(notNullValue()));
        assertThat(runner.getSurvivorSpace(), is(notNullValue()));
        assertThat(runner.getOldGen(), is(notNullValue()));
        
        gen.stopTestApp(proc);
    }       
    
    @Test
    public void testJMXQueryRunnerG1GC() throws IOException, CannotCompileException, NotFoundException, MalformedObjectNameException {
        createTmpClassMainClass();
        Generator gen = new Generator();
        List<String> cmdOption = new LinkedList<>();
        cmdOption.add("-XX:+UseG1GC");
        Process proc = gen.startTestApp("8181", "", "Test", cmdOption);
        JMXQueryRunner runner = new JMXQueryRunner("8181");
        
        runner.init();

        assertThat(runner.getOldGenCollector(), is(notNullValue()));
        assertThat(runner.getYoungGenCollector(), is(notNullValue()));
        assertThat(runner.getEdenSpace(), is(notNullValue()));
        assertThat(runner.getPermGen(), is(notNullValue()));
        assertThat(runner.getSurvivorSpace(), is(notNullValue()));
        assertThat(runner.getOldGen(), is(notNullValue()));
        
        gen.stopTestApp(proc);
    }    
    
    
    private void createTmpClassMainClass() throws CannotCompileException, NotFoundException, IOException {
        ClassPool cpool = ClassPool.getDefault();
        CtClass tmpClass;
        try {
            tmpClass = cpool.get("Test");
        } catch (NotFoundException ex) {
            tmpClass = cpool.makeClass("Test");
            if (tmpClass.isFrozen()) {
                return;
            }
            CtConstructor defaultConstructor = CtNewConstructor.defaultConstructor(tmpClass);
            tmpClass.addConstructor(defaultConstructor);
            CtMethod main = CtNewMethod.make("public static void main(String []args){"
                    + "System.out.println(\"Hello World\");"
                    + "Test test = new Test();"
                    + "synchronized(test){"
                    + "     test.wait();"
                    + "}"
                    + "}", tmpClass);
            tmpClass.addMethod(main);
            tmpClass.writeFile();
        }
    }

}
