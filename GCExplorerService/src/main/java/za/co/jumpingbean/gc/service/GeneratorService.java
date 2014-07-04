package za.co.jumpingbean.gc.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import za.co.jumpingbean.gc.service.constants.DESC;

public class GeneratorService {

    //Map<Process, ProcessParams> processes = new HashMap<>();
    //Map<Process,JMXQueryRunner> jmxConnections = new HashMap<>();
    Map<UUID, ProcessObject> processes = new HashMap<>();

    public UUID startTestApp(String port, String classPath, String mainClass, List<String> gcOptions)
            throws IllegalStateException, IOException {
        ProcessParams params;
        if (gcOptions == null) {
            params = new ProcessParams(port, classPath, mainClass);
        } else {
            params = new ProcessParams(port, classPath, mainClass, gcOptions);
        }
        return this.startTestApp(params);
    }

    public UUID startTestApp(ProcessParams params) throws IllegalStateException, IOException {
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
        BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        try {
            if (proc.exitValue() != 0) {
                BufferedReader error = new BufferedReader(new InputStreamReader(
                        proc.getErrorStream()));
                String err = error.readLine();
                throw new IllegalStateException("Error starting process: " + err);
            } else {
                throw new IllegalStateException("Process terminate too early.");
            }
        } catch (IllegalThreadStateException ex) {
            //process has not yet exited which is fine.
        }
        //Only start stdin reader if proc was launched successfully.

        //give proc time to start up before
        //connecting to JMX server
        synchronized (proc) {
            try {
                proc.wait(500L);
            } catch (InterruptedException ex) {
                Logger.getLogger(GeneratorService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //See if proc exits early. It should be still be alive at this point
        if (proc.isAlive()) {
            ProcOutputReaderList outputList = new ProcOutputReaderList(proc, reader);
            outputList.init();
            try {
                ProcessObject procObj = new ProcessObject(proc, params,
                        JMXQueryRunner.createJXMQueryRunner(params.getPort()), outputList);
                processes.put(procObj.getId(), procObj);
                return procObj.getId();
            } catch (IOException ex) {
                outputList.stop();
                proc.destroy();
                throw new IllegalStateException(ex.getMessage());
            }
        } else {
            proc.destroy();
            throw new IllegalStateException("Process terminate before initialistion could complete.");
        }
    }

    public void stopTestApp(UUID id) {
        processes.get(id).stop();
        processes.remove(id);
    }

    public void stopAllTestApps() {
        for (UUID id : processes.keySet()) {
            stopTestApp(id);
        }
    }

    public JMXQueryRunner getJMXQueryRunner(UUID id) {
        return this.processes.get(id).getQry();
    }

    public ProcessParams getProcessParams(UUID id) {
        return this.processes.get(id).getParams();
    }

    public String getProcessOutput(UUID id) {
        return this.processes.get(id).readProcessOutputLine();
    }

    public Number getMeasure(UUID id, DESC desc) {
        Number result = 0;
        switch (desc) {
            case EDENSPACEUSED:
                result = processes.get(id).getQry().getEdenSpace().getUsage().getUsed();
                break;
            case EDENSPACECOMMITTED:
                result = processes.get(id).getQry().getEdenSpace().getUsage().getCommitted();
                break;
            case EDENSPACEMAX:
                result = processes.get(id).getQry().getEdenSpace().getUsage().getMax();
                break;
            case EDENSPACEFREE:
                result = (processes.get(id).getQry().getEdenSpace().
                        getUsage().getCommitted()
                        - processes.get(id).getQry().getEdenSpace().
                        getUsage().getUsed());
                break;
            case SURVIVORSPACEUSED:
                result = processes.get(id).getQry().getSurvivorSpace().getUsage().getUsed();
                break;
            case SURVIVORSPACECOMMITTED:
                result = processes.get(id).getQry().getSurvivorSpace().getUsage().getCommitted();
                break;
            case SURVIVORSPACEMAX:
                result = processes.get(id).getQry().getSurvivorSpace().getUsage().getMax();
                break;
            case SURVIVORSPACEFREE:
                result = (processes.get(id).getQry().getSurvivorSpace().
                        getUsage().getCommitted()
                        - processes.get(id).getQry().getSurvivorSpace().
                        getUsage().getUsed());
                break;
            case OLDGENSPACEUSED:
                result = processes.get(id).getQry().getOldGenSpace().getUsage().getUsed();
                break;
            case OLDGENSPACECOMMITTED:
                result = processes.get(id).getQry().getOldGenSpace().getUsage().getCommitted();
                break;
            case OLDGENSPACEMAX:
                result = processes.get(id).getQry().getOldGenSpace().getUsage().getMax();
                break;
            case OLDGENSPACEFREE:
                result = (processes.get(id).getQry().getOldGenSpace().
                        getUsage().getCommitted()
                        - processes.get(id).getQry().getOldGenSpace().
                        getUsage().getUsed());
                break;
            case PERMGENSPACEUSED:
                result = processes.get(id).getQry().getPermGenSpace().getUsage().getUsed();
                break;
            case PERMGENSPACECOMMITTED:
                result = processes.get(id).getQry().getPermGenSpace().getUsage().getCommitted();
                break;
            case PERMGENSPACEMAX:
                result = processes.get(id).getQry().getPermGenSpace().getUsage().getMax();
                break;
            case PERMGENSPACEFREE:
                result = (processes.get(id).getQry().getPermGenSpace().
                        getUsage().getCommitted()
                        - processes.get(id).getQry().getPermGenSpace().
                        getUsage().getUsed());
                break;
        }
        return result;
    }

}
