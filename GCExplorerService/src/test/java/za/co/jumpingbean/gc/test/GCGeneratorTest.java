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
package za.co.jumpingbean.gc.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import javax.management.MalformedObjectNameException;
import javax.management.remote.JMXServiceURL;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import org.hamcrest.Matchers;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import org.junit.Test;
import za.co.jumpingbean.gc.service.GCExplorerServiceException;
import za.co.jumpingbean.gc.service.GeneratorService;
import za.co.jumpingbean.gc.service.JMXQueryRunner;
import za.co.jumpingbean.gc.service.LocalJavaProcessFinder;
import za.co.jumpingbean.gc.service.constants.DESC;
import za.co.jumpingbean.gc.service.constants.EdenSpace;
import za.co.jumpingbean.gc.service.constants.OldGenerationCollector;
import za.co.jumpingbean.gc.service.constants.OldGenerationSpace;
import za.co.jumpingbean.gc.service.constants.PermGen;
import za.co.jumpingbean.gc.service.constants.SurvivorSpace;
import za.co.jumpingbean.gc.service.constants.YoungGenerationCollector;

/**
 *
 * @author mark
 */
public class GCGeneratorTest {

    @Test
    public void testJMXQueryBrowser() {

    }

    @Test
    public void testSerialGCLookups() {

        assertThat(EdenSpace.isMember("Eden Space"), is(true));
        assertThat(SurvivorSpace.isMember("Survivor Space"), is(true));
        assertThat(OldGenerationSpace.isMember("Tenured Gen"), is(true));

        //JVMCollector serial = JVMCollector.SERIALGC;
        assertThat(YoungGenerationCollector.isMember("Copy"), is(true));
        assertThat(OldGenerationCollector.isMember("MarkSweepCompact"), is(true));
    }

    @Test
    public void testParallelGCLookups() throws IllegalStateException {

        assertThat(EdenSpace.isMember("PS Eden Space"), is(true));
        assertThat(SurvivorSpace.isMember("PS Survivor Space"), is(true));
        assertThat(OldGenerationSpace.isMember("PS Old Gen"), is(true));

        //JVMCollector parallelGC = JVMCollector.PARALLELGC;
        assertThat(YoungGenerationCollector.isMember("PS Scavenge"), is(true));
        assertThat(OldGenerationCollector.isMember("PS MarkSweep"), is(true));

    }

    @Test
    public void testConcMarkSweepGCLookups() throws IllegalStateException {
        assertThat(EdenSpace.isMember("Par Eden Space"), is(true));
        assertThat(SurvivorSpace.isMember("Par Survivor Space"), is(true));
        assertThat(OldGenerationSpace.isMember("CMS Old Gen"), is(true));

        //JVMCollector concMarkSweepGC = JVMCollector.CONCMARKSWEEP;
        assertThat(YoungGenerationCollector.isMember("ParNew"), is(true));
        assertThat(OldGenerationCollector.isMember("ConcurrentMarkSweep"), is(true));

    }

    @Test
    public void testPermGenLookup() throws IllegalStateException {
        assertThat(PermGen.isMember("Perm Gen"), is(true));
        assertThat(PermGen.isMember("PS Perm Gen"), is(true));
        assertThat(PermGen.isMember("CMS Perm Gen"), is(true));
        assertThat(PermGen.isMember("G1 Perm Gen"), is(true));
        assertThat(PermGen.isMember("Metaspace"), is(true));
    }

    @Test
    public void testG1GCLookups() throws IllegalStateException {
        assertThat(EdenSpace.isMember("G1 Eden Space"), is(true));
        assertThat(SurvivorSpace.isMember("G1 Survivor Space"), is(true));
        assertThat(OldGenerationSpace.isMember("G1 Old Gen"), is(true));

        //JVMCollector concMarkSweepGC = JVMCollector.G1GC;
        assertThat(YoungGenerationCollector.isMember("G1 Young Generation"), is(true));
        assertThat(OldGenerationCollector.isMember("G1 Old Generation"), is(true));
    }

    @Test
    public void testStartStopTestApp() throws CannotCompileException, IOException, NotFoundException,
            InterruptedException, MalformedObjectNameException, IllegalStateException {
        createTmpClassMainClass();
        GeneratorService gen = new GeneratorService();
        UUID id = null;
        try {

            List<String> cmdOption = new LinkedList<>();
            cmdOption.add("-XX:+UseConcMarkSweepGC");
            id = gen.startTestApp("java","8484", "", "Test", cmdOption,false);
            String output = gen.getProcessOutput(id);
            assertThat(output, is(equalTo("Hello World")));
        } finally {
            if (id != null) {
                gen.stopTestApp(id);
            }
        }
    }

    @Test
    public void testJMXQueryRunnerConcMarkSweepGCQuery() throws IOException, CannotCompileException, NotFoundException, MalformedObjectNameException, GCExplorerServiceException {
        createTmpClassMainClass();
        GeneratorService gen = new GeneratorService();
        List<String> cmdOption = new LinkedList<>();
        cmdOption.add("-XX:+UseConcMarkSweepGC");
        UUID procId = null;
        try {

            procId = gen.startTestApp("java","8484", "", "Test", cmdOption,false);
            JMXQueryRunner runner = gen.getJMXQueryRunner(procId);

            //runner.init();
            assertThat(runner.getOldGenCollector(), is(notNullValue()));
            assertThat(runner.getYoungGenCollector(), is(notNullValue()));
            assertThat(runner.getEdenSpace(), is(notNullValue()));
            assertThat(runner.getPermGenSpace(), is(notNullValue()));
            assertThat(runner.getSurvivorSpace(), is(notNullValue()));
            assertThat(runner.getOldGenSpace(), is(notNullValue()));
            assertThat(gen.queryJMXForValue(procId, DESC.EDENSPACEMAX).longValue(),
                    is(greaterThan(0L)));
            assertThat(gen.queryJMXForValue(procId, DESC.EDENSPACEUSED).longValue(),
                    is(greaterThan(0L)));
            assertThat(gen.queryJMXForValue(procId, DESC.EDENSPACEFREE).longValue(),
                    is(greaterThan(0L)));
            assertThat(gen.queryJMXForValue(procId, DESC.EDENSPACECOMMITTED).longValue(),
                    is(greaterThan(0L)));

            assertThat(gen.queryJMXForValue(procId, DESC.PERMGENSPACEMAX).longValue(),
                    is(greaterThanOrEqualTo(-1L)));
            assertThat(gen.queryJMXForValue(procId, DESC.PERMGENSPACEUSED).longValue(),
                    is(greaterThan(0L)));
            assertThat(gen.queryJMXForValue(procId, DESC.PERMGENSPACEFREE).longValue(),
                    is(greaterThan(0L)));
            assertThat(gen.queryJMXForValue(procId, DESC.PERMGENSPACECOMMITTED).longValue(),
                    is(greaterThan(0L)));

            assertThat(gen.queryJMXForValue(procId, DESC.OLDGENSPACEMAX).longValue(),
                    is(greaterThan(0L)));
            assertThat(gen.queryJMXForValue(procId, DESC.OLDGENSPACEUSED).longValue(),
                    is(greaterThan(0L)));
            assertThat(gen.queryJMXForValue(procId, DESC.OLDGENSPACEFREE).longValue(),
                    is(greaterThan(0L)));
            assertThat(gen.queryJMXForValue(procId, DESC.OLDGENSPACECOMMITTED).longValue(),
                    is(greaterThan(0L)));

            assertThat(gen.queryJMXForValue(procId, DESC.SURVIVORSPACEMAX).longValue(),
                    is(greaterThan(0L)));
            assertThat(gen.queryJMXForValue(procId, DESC.SURVIVORSPACEUSED).longValue(),
                    is(Matchers.greaterThanOrEqualTo(0L)));
            assertThat(gen.queryJMXForValue(procId, DESC.SURVIVORSPACEFREE).longValue(),
                    is(greaterThan(0L)));
            assertThat(gen.queryJMXForValue(procId, DESC.SURVIVORSPACECOMMITTED).longValue(),
                    is(greaterThan(0L)));
        } finally {
            if (procId != null) {
                gen.stopTestApp(procId);
            }
        }
    }

    @Test
    public void testJMXQueryRunnerSerialGCQuery() throws IOException, CannotCompileException, NotFoundException, MalformedObjectNameException, GCExplorerServiceException {
        createTmpClassMainClass();
        GeneratorService gen = new GeneratorService();
        UUID procId = null;
        try {
            List<String> cmdOption = new LinkedList<>();
            cmdOption.add("-XX:+UseSerialGC");
            procId = gen.startTestApp("java","8484", "", "Test", cmdOption,false);
            JMXQueryRunner runner = gen.getJMXQueryRunner(procId);

            //runner.init();
            assertThat(runner.getOldGenCollector(), is(notNullValue()));
            assertThat(runner.getYoungGenCollector(), is(notNullValue()));
            assertThat(runner.getEdenSpace(), is(notNullValue()));
            assertThat(runner.getPermGenSpace(), is(notNullValue()));
            assertThat(runner.getSurvivorSpace(), is(notNullValue()));
            assertThat(runner.getOldGenSpace(), is(notNullValue()));
            assertThat(gen.queryJMXForValue(procId, DESC.EDENSPACEMAX).longValue(),
                    is(greaterThan(0L)));
            assertThat(gen.queryJMXForValue(procId, DESC.EDENSPACEUSED).longValue(),
                    is(greaterThan(0L)));
            assertThat(gen.queryJMXForValue(procId, DESC.EDENSPACEFREE).longValue(),
                    is(greaterThan(0L)));
            assertThat(gen.queryJMXForValue(procId, DESC.EDENSPACECOMMITTED).longValue(),
                    is(greaterThan(0L)));

            assertThat(gen.queryJMXForValue(procId, DESC.PERMGENSPACEMAX).longValue(),
                    is(greaterThanOrEqualTo(-1L)));
            assertThat(gen.queryJMXForValue(procId, DESC.PERMGENSPACEUSED).longValue(),
                    is(greaterThan(0L)));
            assertThat(gen.queryJMXForValue(procId, DESC.PERMGENSPACEFREE).longValue(),
                    is(greaterThan(0L)));
            assertThat(gen.queryJMXForValue(procId, DESC.PERMGENSPACECOMMITTED).longValue(),
                    is(greaterThan(0L)));

            assertThat(gen.queryJMXForValue(procId, DESC.OLDGENSPACEMAX).longValue(),
                    is(greaterThan(0L)));
            assertThat(gen.queryJMXForValue(procId, DESC.OLDGENSPACEUSED).longValue(),
                    is(greaterThan(0L)));
            assertThat(gen.queryJMXForValue(procId, DESC.OLDGENSPACEFREE).longValue(),
                    is(greaterThan(0L)));
            assertThat(gen.queryJMXForValue(procId, DESC.OLDGENSPACECOMMITTED).longValue(),
                    is(greaterThan(0L)));

            assertThat(gen.queryJMXForValue(procId, DESC.SURVIVORSPACEMAX).longValue(),
                    is(greaterThan(0L)));
            assertThat(gen.queryJMXForValue(procId, DESC.SURVIVORSPACEUSED).longValue(),
                    is(greaterThanOrEqualTo(0L)));
            assertThat(gen.queryJMXForValue(procId, DESC.SURVIVORSPACEFREE).longValue(),
                    is(greaterThan(0L)));
            assertThat(gen.queryJMXForValue(procId, DESC.SURVIVORSPACECOMMITTED).longValue(),
                    is(greaterThan(0L)));
        } finally {
            if (procId != null) {
                gen.stopTestApp(procId);
            }
        }
    }

    @Test
    public void testJMXQueryRunnerParallelGCQuery() throws IOException, CannotCompileException,
            NotFoundException, MalformedObjectNameException, IllegalStateException, GCExplorerServiceException {
        createTmpClassMainClass();
        GeneratorService gen = new GeneratorService();
        UUID procId = null;
        try {

            List<String> cmdOption = new LinkedList<>();
            cmdOption.add("-XX:+UseParallelGC");
            procId = gen.startTestApp("java","8484", "", "Test", cmdOption,false);
            JMXQueryRunner runner = gen.getJMXQueryRunner(procId);

            //runner.init();
            assertThat(runner.getOldGenCollector(), is(notNullValue()));
            assertThat(runner.getYoungGenCollector(), is(notNullValue()));
            assertThat(runner.getEdenSpace(), is(notNullValue()));
            assertThat(runner.getPermGenSpace(), is(notNullValue()));
            assertThat(runner.getSurvivorSpace(), is(notNullValue()));
            assertThat(runner.getOldGenSpace(), is(notNullValue()));

            assertThat(gen.queryJMXForValue(procId, DESC.EDENSPACEMAX).longValue(),
                    is(greaterThan(0L)));
            assertThat(gen.queryJMXForValue(procId, DESC.EDENSPACEUSED).longValue(),
                    is(greaterThan(0L)));
            assertThat(gen.queryJMXForValue(procId, DESC.EDENSPACEFREE).longValue(),
                    is(greaterThan(0L)));
            assertThat(gen.queryJMXForValue(procId, DESC.EDENSPACECOMMITTED).longValue(),
                    is(greaterThan(0L)));

            assertThat(gen.queryJMXForValue(procId, DESC.PERMGENSPACEMAX).longValue(),
                    is(greaterThanOrEqualTo(-1L)));
            assertThat(gen.queryJMXForValue(procId, DESC.PERMGENSPACEUSED).longValue(),
                    is(greaterThan(0L)));
            assertThat(gen.queryJMXForValue(procId, DESC.PERMGENSPACEFREE).longValue(),
                    is(greaterThan(0L)));
            assertThat(gen.queryJMXForValue(procId, DESC.PERMGENSPACECOMMITTED).longValue(),
                    is(greaterThan(0L)));

            assertThat(gen.queryJMXForValue(procId, DESC.OLDGENSPACEMAX).longValue(),
                    is(greaterThan(0L)));
            assertThat(gen.queryJMXForValue(procId, DESC.OLDGENSPACEUSED).longValue(),
                    is(greaterThan(0L)));
            assertThat(gen.queryJMXForValue(procId, DESC.OLDGENSPACEFREE).longValue(),
                    is(greaterThan(0L)));
            assertThat(gen.queryJMXForValue(procId, DESC.OLDGENSPACECOMMITTED).longValue(),
                    is(greaterThan(0L)));

            assertThat(gen.queryJMXForValue(procId, DESC.SURVIVORSPACEMAX).longValue(),
                    is(greaterThan(0L)));
            assertThat(gen.queryJMXForValue(procId, DESC.SURVIVORSPACEUSED).longValue(),
                    is(greaterThanOrEqualTo(0L)));
            assertThat(gen.queryJMXForValue(procId, DESC.SURVIVORSPACEFREE).longValue(),
                    is(greaterThanOrEqualTo(0L)));
            assertThat(gen.queryJMXForValue(procId, DESC.SURVIVORSPACECOMMITTED).longValue(),
                    is(greaterThan(0L)));
        } finally {
            if (procId != null) {
                gen.stopTestApp(procId);
            }
        }
    }

    @Test
    public void testJMXConnectionToLocalProcess() throws IOException, GCExplorerServiceException {
        Process p = Runtime.getRuntime().exec("jconsole");
        try {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                Logger.getLogger(GCGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
            }
            List<String> procs = LocalJavaProcessFinder.getLocalJavaProcesses();
            assertThat(procs.size(), greaterThan(0));
            String strPID = null;
            for (String proc : procs) {
                String[] items = proc.split(" ");
                if (items.length >= 2) {
                    if (items[1].contains("JConsole")) {
                        strPID = items[0];
                        break;
                    }
                }
            }
            if (strPID == null) {
                p.destroy();
                throw new GCExplorerServiceException("No suitable java process found.");

            }
            int pid = Integer.parseInt(strPID);
            GeneratorService gen = new GeneratorService();
            UUID procId = gen.connectToJavaProcess(pid);
            JMXQueryRunner runner = gen.getJMXQueryRunner(procId);

            //runner.init();
            assertThat(runner.getOldGenCollector(), is(notNullValue()));
            assertThat(runner.getYoungGenCollector(), is(notNullValue()));
            assertThat(runner.getEdenSpace(), is(notNullValue()));
            assertThat(runner.getPermGenSpace(), is(notNullValue()));
            assertThat(runner.getSurvivorSpace(), is(notNullValue()));
            assertThat(runner.getOldGenSpace(), is(notNullValue()));
            assertThat(gen.queryJMXForValue(procId, DESC.EDENSPACEMAX).longValue(),
                    is(greaterThanOrEqualTo(-1L)));
            assertThat(gen.queryJMXForValue(procId, DESC.EDENSPACEUSED).longValue(),
                    is(greaterThan(0L)));
            assertThat(gen.queryJMXForValue(procId, DESC.EDENSPACEFREE).longValue(),
                    is(greaterThan(0L)));
            assertThat(gen.queryJMXForValue(procId, DESC.EDENSPACECOMMITTED).longValue(),
                    is(greaterThan(0L)));

            assertThat(gen.queryJMXForValue(procId, DESC.PERMGENSPACEMAX).longValue(),
                    is(greaterThanOrEqualTo(-1L)));
            assertThat(gen.queryJMXForValue(procId, DESC.PERMGENSPACEUSED).longValue(),
                    is(greaterThan(0L)));
            assertThat(gen.queryJMXForValue(procId, DESC.PERMGENSPACEFREE).longValue(),
                    is(greaterThan(0L)));
            assertThat(gen.queryJMXForValue(procId, DESC.PERMGENSPACECOMMITTED).longValue(),
                    is(greaterThan(0L)));

            assertThat(gen.queryJMXForValue(procId, DESC.OLDGENSPACEMAX).longValue(),
                    is(greaterThan(0L)));
            assertThat(gen.queryJMXForValue(procId, DESC.OLDGENSPACEUSED).longValue(),
                    is(greaterThan(0L)));
            assertThat(gen.queryJMXForValue(procId, DESC.OLDGENSPACEFREE).longValue(),
                    is(greaterThan(0L)));
            assertThat(gen.queryJMXForValue(procId, DESC.OLDGENSPACECOMMITTED).longValue(),
                    is(greaterThan(0L)));

            assertThat(gen.queryJMXForValue(procId, DESC.SURVIVORSPACEMAX).longValue(),
                    is(greaterThanOrEqualTo(-1L)));
            assertThat(gen.queryJMXForValue(procId, DESC.SURVIVORSPACEUSED).longValue(),
                    is(greaterThanOrEqualTo(0L)));
            assertThat(gen.queryJMXForValue(procId, DESC.SURVIVORSPACEFREE).longValue(),
                    is(greaterThanOrEqualTo(0L)));
            assertThat(gen.queryJMXForValue(procId, DESC.SURVIVORSPACECOMMITTED).longValue(),
                    is(greaterThan(0L)));
        } finally {
            p.destroy();
        }
    }

    @Test
    public void testJMXConnectionToRemoteProcess() throws IOException,
            GCExplorerServiceException, CannotCompileException, NotFoundException {
        try {
            createTmpClassMainClass();
            GeneratorService gen = new GeneratorService();
            List<String> cmd = new LinkedList<>();
            cmd.add("java");
            cmd.add("-Dcom.sun.management.jmxremote");
            cmd.add("-Dcom.sun.management.jmxremote.port=8888");
            cmd.add("-Dcom.sun.management.jmxremote.ssl=false");
            cmd.add("-Dcom.sun.management.jmxremote.authenticate=false");
            cmd.add("-Djava.rmi.server.hostname=127.0.0.1");
            cmd.add("Test");
            ProcessBuilder procBuilder = new ProcessBuilder(cmd);
            Process proc = procBuilder.start();
            BufferedReader rd = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            if (!proc.isAlive()) {
                String error = rd.readLine();
            }
            Thread.sleep(1000L);
            try {
                UUID procId = gen.connectToJavaProcess(
                        "service:jmx:rmi:///jndi/rmi://127.0.0.1:8888/jmxrmi", "", "");
                JMXQueryRunner runner = gen.getJMXQueryRunner(procId);

                //runner.init();
                assertThat(runner.getOldGenCollector(), is(notNullValue()));
                assertThat(runner.getYoungGenCollector(), is(notNullValue()));
                assertThat(runner.getEdenSpace(), is(notNullValue()));
                assertThat(runner.getPermGenSpace(), is(notNullValue()));
                assertThat(runner.getSurvivorSpace(), is(notNullValue()));
                assertThat(runner.getOldGenSpace(), is(notNullValue()));
                assertThat(gen.queryJMXForValue(procId, DESC.EDENSPACEMAX).longValue(),
                        is(greaterThanOrEqualTo(-1L)));
                assertThat(gen.queryJMXForValue(procId, DESC.EDENSPACEUSED).longValue(),
                        is(greaterThan(0L)));
                assertThat(gen.queryJMXForValue(procId, DESC.EDENSPACEFREE).longValue(),
                        is(greaterThan(0L)));
                assertThat(gen.queryJMXForValue(procId, DESC.EDENSPACECOMMITTED).longValue(),
                        is(greaterThan(0L)));

                assertThat(gen.queryJMXForValue(procId, DESC.PERMGENSPACEMAX).longValue(),
                        is(greaterThanOrEqualTo(-1L)));
                assertThat(gen.queryJMXForValue(procId, DESC.PERMGENSPACEUSED).longValue(),
                        is(greaterThan(0L)));
                assertThat(gen.queryJMXForValue(procId, DESC.PERMGENSPACEFREE).longValue(),
                        is(greaterThan(0L)));
                assertThat(gen.queryJMXForValue(procId, DESC.PERMGENSPACECOMMITTED).longValue(),
                        is(greaterThan(0L)));

                assertThat(gen.queryJMXForValue(procId, DESC.OLDGENSPACEMAX).longValue(),
                        is(greaterThan(0L)));
                assertThat(gen.queryJMXForValue(procId, DESC.OLDGENSPACEUSED).longValue(),
                        is(greaterThan(0L)));
                assertThat(gen.queryJMXForValue(procId, DESC.OLDGENSPACEFREE).longValue(),
                        is(greaterThan(0L)));
                assertThat(gen.queryJMXForValue(procId, DESC.OLDGENSPACECOMMITTED).longValue(),
                        is(greaterThan(0L)));

                assertThat(gen.queryJMXForValue(procId, DESC.SURVIVORSPACEMAX).longValue(),
                        is(greaterThanOrEqualTo(-1L)));
                assertThat(gen.queryJMXForValue(procId, DESC.SURVIVORSPACEUSED).longValue(),
                        is(greaterThanOrEqualTo(0L)));
                assertThat(gen.queryJMXForValue(procId, DESC.SURVIVORSPACEFREE).longValue(),
                        is(greaterThanOrEqualTo(0L)));
                assertThat(gen.queryJMXForValue(procId, DESC.SURVIVORSPACECOMMITTED).longValue(),
                        is(greaterThan(0L)));
            } finally {
                proc.destroyForcibly();
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(GCGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void testJMXQueryRunnerG1GCQuery() throws IOException, CannotCompileException, NotFoundException, MalformedObjectNameException, GCExplorerServiceException {
        createTmpClassMainClass();
        GeneratorService gen = new GeneratorService();
        UUID procId = null;
        try {
            List<String> cmdOption = new LinkedList<>();
            cmdOption.add("-XX:+UseG1GC");
            procId = gen.startTestApp("java","8484", "", "Test", cmdOption,false);
            JMXQueryRunner runner = gen.getJMXQueryRunner(procId);

            //runner.init();
            assertThat(runner.getOldGenCollector(), is(notNullValue()));
            assertThat(runner.getYoungGenCollector(), is(notNullValue()));
            assertThat(runner.getEdenSpace(), is(notNullValue()));
            assertThat(runner.getPermGenSpace(), is(notNullValue()));
            assertThat(runner.getSurvivorSpace(), is(notNullValue()));
            assertThat(runner.getOldGenSpace(), is(notNullValue()));
            assertThat(gen.queryJMXForValue(procId, DESC.EDENSPACEMAX).longValue(),
                    is(greaterThanOrEqualTo(-1L)));
            assertThat(gen.queryJMXForValue(procId, DESC.EDENSPACEUSED).longValue(),
                    is(greaterThan(0L)));
            assertThat(gen.queryJMXForValue(procId, DESC.EDENSPACEFREE).longValue(),
                    is(greaterThan(0L)));
            assertThat(gen.queryJMXForValue(procId, DESC.EDENSPACECOMMITTED).longValue(),
                    is(greaterThan(0L)));

            assertThat(gen.queryJMXForValue(procId, DESC.PERMGENSPACEMAX).longValue(),
                    is(greaterThanOrEqualTo(-1L)));
            assertThat(gen.queryJMXForValue(procId, DESC.PERMGENSPACEUSED).longValue(),
                    is(greaterThan(0L)));
            assertThat(gen.queryJMXForValue(procId, DESC.PERMGENSPACEFREE).longValue(),
                    is(greaterThan(0L)));
            assertThat(gen.queryJMXForValue(procId, DESC.PERMGENSPACECOMMITTED).longValue(),
                    is(greaterThan(0L)));

            assertThat(gen.queryJMXForValue(procId, DESC.OLDGENSPACEMAX).longValue(),
                    is(greaterThan(0L)));
            assertThat(gen.queryJMXForValue(procId, DESC.OLDGENSPACEUSED).longValue(),
                    is(greaterThan(0L)));
            assertThat(gen.queryJMXForValue(procId, DESC.OLDGENSPACEFREE).longValue(),
                    is(greaterThan(0L)));
            assertThat(gen.queryJMXForValue(procId, DESC.OLDGENSPACECOMMITTED).longValue(),
                    is(greaterThan(0L)));

            assertThat(gen.queryJMXForValue(procId, DESC.SURVIVORSPACEMAX).longValue(),
                    is(greaterThanOrEqualTo(-1L)));
            assertThat(gen.queryJMXForValue(procId, DESC.SURVIVORSPACEUSED).longValue(),
                    is(greaterThanOrEqualTo(0L)));
            assertThat(gen.queryJMXForValue(procId, DESC.SURVIVORSPACEFREE).longValue(),
                    is(greaterThanOrEqualTo(0L)));
            assertThat(gen.queryJMXForValue(procId, DESC.SURVIVORSPACECOMMITTED).longValue(),
                    is(greaterThan(0L)));
        } finally {
            if (procId != null) {
                gen.stopTestApp(procId);
            }
        }
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
            String str = "public static void main(String []args){"
                    + "Test test = new Test();"
                    + "Byte[]bigArr = new Byte[100000000];"
                    + "Byte[]arr = new Byte[1000];"
                    + "System.out.println(\"Hello World\");"
                    + "try {"
                    + "    Thread.sleep(2000L);"
                    + "} catch (InterruptedException ex1) {"
                    + "}"
                    + "}";
            CtMethod main = CtNewMethod.make(str, tmpClass);
            tmpClass.addMethod(main);
            tmpClass.writeFile();
        }
    }
}
