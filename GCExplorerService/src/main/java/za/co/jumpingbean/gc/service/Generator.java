package za.co.jumpingbean.gc.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.MalformedObjectNameException;

public class Generator {

    Map<Process, ProcessParams> processes = new HashMap<>();
    Map<Process,JMXQueryRunner> jmxConnections = new HashMap<>();


    public Process startTestApp(String port, String classPath, String mainClass, List<String> gcOptions) throws IOException, MalformedObjectNameException {
        ProcessParams params;
        if (gcOptions == null) {
            params = new ProcessParams(port, classPath, mainClass);
        } else {
            params = new ProcessParams(port, classPath, mainClass, gcOptions);
        }
        return this.startTestApp(params);
    }

    public Process startTestApp(ProcessParams params) throws IOException, MalformedObjectNameException {
        System.out.println("Starting up....");
        List<String> cmd = new LinkedList<>();
        cmd.add("java");
        if (params.getClassPath() != null && !params.getClassPath().isEmpty()) {
            cmd.add("-cp");
            cmd.add(params.getClassPath());
        }
        cmd.add("-Dcom.sun.management.jmxremote");
        cmd.add("-Dcom.sun.management.jmxremote.port=" + params.getPort());
        cmd.add("-Dcom.sun.management.jmxremote.ssl=false");
        cmd.add("-Dcom.sun.management.jmxremote.authenticate=false");
        cmd.add("-Djava.rmi.server.hostname=127.0.0.1");
        if (!params.getGcOptions().isEmpty()) {
            cmd.addAll(params.getGcOptions());
        }
        cmd.add(params.getMainClass());
        ProcessBuilder procBuilder = new ProcessBuilder(cmd);
        Process proc = procBuilder.start();
        try {
            if (proc.exitValue() != 0) {
                BufferedReader error = new BufferedReader(new InputStreamReader(
                        proc.getErrorStream()));
                String err = error.readLine();
                throw new IllegalStateException("Error starting process: " + err);
            }
        } catch (IllegalThreadStateException ex) {
            //process has not yet exited which is fine.
        }
        processes.put(proc, params);
        //give proc time to start up before
        //connecting to JMX server
        synchronized(proc){
            try {
                proc.wait(500L);
            } catch (InterruptedException ex) {
                Logger.getLogger(Generator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        jmxConnections.put(proc,new JMXQueryRunner(params.getPort()));
        return proc;
    }

    public void stopTestApp(Process proc) {
        proc.destroy();
        try {
            synchronized (proc) {
                proc.wait(500L);
                proc.exitValue();
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(Generator.class.getName()).log(Level.SEVERE, null, ex);
        }catch (IllegalThreadStateException ex){
            throw new IllegalStateException("failed to stop process"+proc.toString());
        }
        processes.remove(proc);
        jmxConnections.remove(proc);
    }

    public void stopAllTestApps() {
        for (Process proc : processes.keySet()) {
            stopTestApp(proc);
        }
    }


}
