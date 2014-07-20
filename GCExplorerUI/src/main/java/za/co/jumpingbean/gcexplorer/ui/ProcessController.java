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
package za.co.jumpingbean.gcexplorer.ui;

import com.sun.jnlp.JNLPClassLoader;
import com.sun.jnlp.JNLPClassLoaderUtil;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart.Series;
import javax.inject.Inject;
import javax.jnlp.BasicService;
import javax.jnlp.DownloadService2;
import javax.jnlp.ServiceManager;
import javax.jnlp.UnavailableServiceException;
import za.co.jumpingbean.gc.service.GCExplorerServiceException;
import za.co.jumpingbean.gc.service.GeneratorService;
import za.co.jumpingbean.gc.service.constants.DESC;
import za.co.jumpingbean.gcexplorer.model.MemoryPool;
import za.co.jumpingbean.gcexplorer.model.ProcessFactory;
import za.co.jumpingbean.gcexplorer.model.UUIDProcess;

/**
 *
 * @author mark
 */
public class ProcessController implements Runnable {

    private final Map<UUID, UUIDProcess> liveProcesses
            = new HashMap<>();

    private int millisecs = 1000;
    private final GCExplorer main;

    private boolean isRunning = true;

    @Inject
    private final GeneratorService gen = new GeneratorService();

    ExecutorService executorService = Executors.newFixedThreadPool(2);

    ProcessController(GCExplorer main) {
        this.main = main;
    }

    public UUIDProcess getUUIDProcess(UUID id) {
        synchronized (liveProcesses) {
            return liveProcesses.get(id);
        }
    }

    public void stopAllProcesses() {
        isRunning = false;
        synchronized (liveProcesses) {
            gen.stopAllTestApps();
            this.liveProcesses.clear();
        }
        executorService.shutdownNow();
    }

    public void stopProcess(UUID procId) {
        synchronized (liveProcesses) {
            gen.stopTestApp(procId);
            this.liveProcesses.remove(procId);
        }
    }

    @Override
    public void run() {
        while (isRunning) {
            synchronized (liveProcesses) {
                liveProcesses.keySet().stream().forEach((procObj) -> {
                    updateDataItems(procObj);
                });
                try {
                    liveProcesses.wait(millisecs);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ProcessController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private void updateDataItems(UUID procId) {

        synchronized (liveProcesses) {
            try {
                UUIDProcess proc = this.liveProcesses.get(procId);
                int convert = main.getUnits().getConversionUnits();
                final Timestamp ts = new Timestamp(System.currentTimeMillis());

                double result = gen.queryJMXForValue(procId, DESC.EDENSPACEUSED).doubleValue() / convert;
                proc.getEdenPool().setUsedValue(result);
                //runLater(proc.getEdenPool(), 0, ts, result);

                double result2 = gen.queryJMXForValue(procId, DESC.EDENSPACECOMMITTED).doubleValue() / convert;
                proc.getEdenPool().setCommittedValue(result2);

                double result3 = gen.queryJMXForValue(procId, DESC.EDENSPACEFREE).doubleValue() / convert;
                proc.getEdenPool().setFreeValue(result3);
                runLater(proc.getEdenPool(), ts, result, result2, result3);

                result = gen.queryJMXForValue(procId, DESC.EDENSPACEMAX).doubleValue() / convert;
                proc.getEdenPool().setMaxValue(result);

                result = gen.queryJMXForValue(procId, DESC.SURVIVORSPACEUSED).doubleValue() / convert;
                proc.getSurvivorPool().setUsedValue(result);
                //runLater(proc.getSurvivorPool(), 0, ts, result);

                result2 = gen.queryJMXForValue(procId, DESC.SURVIVORSPACECOMMITTED).doubleValue() / convert;
                proc.getSurvivorPool().setCommittedValue(result2);

                result3 = gen.queryJMXForValue(procId, DESC.SURVIVORSPACEFREE).doubleValue() / convert;
                proc.getSurvivorPool().setFreeValue(result3);
                runLater(proc.getSurvivorPool(), ts, result, result2, result3);

                proc.getSurvivorPool().setMaxValue(gen.queryJMXForValue(procId, DESC.SURVIVORSPACEMAX).doubleValue() / convert);

                //Set free survivor space stats
                proc.getEmptySurvivorPool().setUsedValue(0d);
                proc.getEmptySurvivorPool().setCommittedValue(result2);
                proc.getEmptySurvivorPool().setFreeValue(result2);
                runLater(proc.getEmptySurvivorPool(), ts, 0, result2, result2);
                proc.getEmptySurvivorPool().setMaxValue(gen.queryJMXForValue(procId, DESC.SURVIVORSPACEMAX).doubleValue() / convert);

                result = gen.queryJMXForValue(procId, DESC.OLDGENSPACEUSED).doubleValue() / convert;
                proc.getOldGenPool().setUsedValue(result);
                //runLater(proc.getOldGenPool(), 0, ts, result);

                result2 = gen.queryJMXForValue(procId, DESC.OLDGENSPACECOMMITTED).doubleValue() / convert;
                proc.getOldGenPool().setCommittedValue(result2);

                result3 = gen.queryJMXForValue(procId, DESC.OLDGENSPACEFREE).doubleValue() / convert;
                proc.getOldGenPool().setFreeValue(result3);
                runLater(proc.getOldGenPool(), ts, result, result2, result3);

                proc.getOldGenPool().setMaxValue(gen.queryJMXForValue(procId, DESC.OLDGENSPACEMAX).doubleValue() / convert);

                result = gen.queryJMXForValue(procId, DESC.PERMGENSPACEUSED).doubleValue() / convert;
                proc.getPermGenPool().setUsedValue(result);
                //runLater(proc.getPermGenPool(), 0, ts, result);

                result2 = gen.queryJMXForValue(procId, DESC.PERMGENSPACECOMMITTED).doubleValue() / convert;
                proc.getPermGenPool().setCommittedValue(result2);

                result3 = gen.queryJMXForValue(procId, DESC.PERMGENSPACEFREE).doubleValue() / convert;
                proc.getPermGenPool().setFreeValue(result3);
                runLater(proc.getPermGenPool(), ts, result, result2, result3);

                proc.getPermGenPool().setMaxValue(gen.queryJMXForValue(procId, DESC.PERMGENSPACEMAX).doubleValue() / convert);

                String gcInfo = gen.getGCInfo(procId);
                proc.setGCInfo(gcInfo);
                //force a wait to update sys info block. Sometimes it doesn't
                //update display.
                if (proc.getDataItems().get(0).getFreeCommittedSeries().get(0).getData().size() < 6) {
                    proc.setSysInfo(true);
                } else {
                    proc.setSysInfo(false);
                }
            } catch (GCExplorerServiceException ex) {
                //proce removed during read.
            }
        }
    }

    public int getMillisecs() {
        return millisecs;
    }

    public void setMillisecs(int millisecs) {
        this.millisecs = millisecs;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public UUID connectToProcess(int pid, String cmdLine) throws IOException {
        UUID id = gen.connectToJavaProcess(pid);
        String javaVersion = gen.getJavaVersion(id);
        synchronized (liveProcesses) {
            this.liveProcesses.put(id, ProcessFactory.newProcess(id, cmdLine, javaVersion));
        }
        return id;
    }

    public UUID connectToProcess(String url, String username, String password) throws IOException {
        UUID id = gen.connectToJavaProcess(url, username, password);
        String javaVersion = gen.getJavaVersion(id);
        synchronized (liveProcesses) {
            this.liveProcesses.put(id, ProcessFactory.newProcess(id, "", javaVersion));
        }
        return id;
    }

    public UUID launchProcess(String javaPath, String collector, List<String> gcOptionsExtra) throws IllegalStateException, IOException {
        Integer port = null;
        //find an open port
        for (int i = 8181; i < 65535; i++) {
            try {
                try (ServerSocket soc = new ServerSocket(i)) {
                    port = i;
                }
                break;
            } catch (IOException ex) {
                //socket not available continue search for new socket
            }
        }
        //check if we exhausted port scan
        if (port == null) {
            throw new IOException("No open ports available");
        }

        List<String> gcOptions = new LinkedList<>();
        if (!collector.isEmpty()) {
            gcOptions.add(collector);
        }
        gcOptions.addAll(gcOptionsExtra);
        UUID procId;
        String tmpPort = port.toString();
        port++;
        String classpath = "";
        if (!isRunningJavaWebStart()) {
            //Get current classpath
            StringBuilder buffer = new StringBuilder();
            for (URL url
                    : ((URLClassLoader) (Thread.currentThread()
                    .getContextClassLoader())).getURLs()) {
                buffer.append(new File(url.getPath()));
                buffer.append(System.getProperty("path.separator"));
            }
            classpath = buffer.toString();
            int toIndex = classpath
                    .lastIndexOf(System.getProperty("path.separator"));
            classpath = classpath.substring(0, toIndex);
            procId = gen.startTestApp(javaPath, tmpPort, classpath,
                    "za.co.jumpingbean.gc.testapp.GarbageGeneratorApp", gcOptions);
        } else {
            throw new IllegalStateException("Unable to launch process via Java Web Start.");
        }

        String javaVersion = gen.getJavaVersion(procId);
        synchronized (liveProcesses) {
            this.liveProcesses.put(procId,
                    ProcessFactory.newProcess(procId, gcOptions.toString(), javaVersion));
        }
        return procId;
    }

    private boolean isRunningJavaWebStart() {
        boolean isWebStartApp = false;
        try {
            Class.forName("javax.jnlp.ServiceManager");
            isWebStartApp = true;
        } catch (ClassNotFoundException ex) {
            isWebStartApp = false;
        }
        return isWebStartApp;
    }

    public ObservableList<Series<Number, Number>> getEdenSeries(UUID procId) {
        synchronized (liveProcesses) {
            return this.liveProcesses.get(procId).getEdenPool().getFreeCommittedSeries();
        }
    }

    public ObservableList<Series<Number, Number>> getSurvivorSeries(UUID procId) {
        synchronized (liveProcesses) {
            return this.liveProcesses.get(procId).getSurvivorPool().getFreeCommittedSeries();
        }
    }

    public ObservableList<Series<Number, Number>> getOldGenSeries(UUID procId) {
        synchronized (liveProcesses) {
            return this.liveProcesses.get(procId).getOldGenPool().getFreeCommittedSeries();
        }
    }

    public ObservableList<Series<Number, Number>> getPermGenSeries(UUID procId) {
        synchronized (liveProcesses) {
            return this.liveProcesses.get(procId).getPermGenPool().getFreeCommittedSeries();
        }
    }

    private void runLater(MemoryPool pool, Timestamp ts, final double used, double committed, double free) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                pool.addDataPoint(ts, used, committed, free);
            }
        });
    }

    public ObservableList<Series<String, Number>> getStackedBarChartSeries(UUID id) {
        UUIDProcess process;
        synchronized (liveProcesses) {
            process = liveProcesses.get(id);
        }
        return process.getStackedBarChartSeries();
    }

    public ObservableList<Series<Number, Number>> getStackedAreaChartSeries(UUID id) {
        UUIDProcess process;
        synchronized (liveProcesses) {
            process = liveProcesses.get(id);
        }
        return process.getStackedAreaChartDataItems();
    }

    public Future genLocalInstances(UUID id, int numInstances, int instanceSize, int creationPauseTime, ProcessViewForm form) {
        return executorService.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                String result = gen.genLocalInstances(id, numInstances, instanceSize, creationPauseTime);
                if (result.equalsIgnoreCase("Out of memory!")) {
                    form.setGenStatus("Process terminated - out of memory");
                    stopProcess(id);
                } else {
                    StringBuilder str = new StringBuilder("Obj creation started with:");
                    str.append("Objects:\t").append(numInstances).append("\n\r");
                    str.append("Size(MB):\t").append(instanceSize).append("\n\r");
                    str.append("Creation Pause (ms):\t").append(creationPauseTime).append("\n\r");
                    form.setGenStatus(str.toString());
                }
                return result;
            }
        });
    }

    public Future genLongLivedInstances(UUID id, int numInstances, int instanceSize, int creationPauseTime, ProcessViewForm form) {
        return executorService.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                String result = gen.genLongLivedInstances(id, numInstances, instanceSize, creationPauseTime);
                if (result.equalsIgnoreCase("Out of memory!")) {
                    form.setGenStatus("Process terminated - out of memory");
                    stopProcess(id);
                } else {
                    StringBuilder str = new StringBuilder("Obj creation started with:");
                    str.append("Objects:\t").append(numInstances).append("\n\r");
                    str.append("Size(MB):\t").append(instanceSize).append("\n\r");
                    str.append("Creation Pause (ms):\t").append(creationPauseTime).append("\n\r");
                    form.setGenStatus(str.toString());
                }
                return result;
            }
        });
    }

    String getParameters(UUID procId) {
        return this.gen.getProcessParams(procId).getStartupParameters();
    }

//    public double getMaxHeap(UUID uuid){
//        return this.liveProcesses.get(uuid).getMaxHeap();
//    }
    void addSystemInfoEventListener(UUID procId, ChangeListener changeListener) {
        synchronized (liveProcesses) {
            if (this.liveProcesses.get(procId) != null) {
                this.liveProcesses.get(procId).addSystemInfoEventListener(changeListener);
            }
        }
    }

    void addGCInfoEventListener(UUID procId, ChangeListener changeListener) {
        synchronized (liveProcesses) {
            if (this.liveProcesses.get(procId) != null) {
                this.liveProcesses.get(procId).addGCInfoEventListener(changeListener);
            }
        }
    }

    void updateNumberOfDataPoints(int numDataPoints) {
        synchronized (liveProcesses) {
            for (UUIDProcess proc : liveProcesses.values()) {
                proc.updateNumDataPoints(numDataPoints);
            }
        }
    }

    public int getNumber(UUID procId) {
        synchronized (liveProcesses) {
            if (liveProcesses.get(procId) != null) {
                return liveProcesses.get(procId).getNumber();
            } else {
                return 0;
            }
        }

    }

    void releaseLongLivedInstances(UUID id, int numInstances, boolean reverse) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                gen.releaseLongLivedInstances(id, numInstances, reverse);
            }
        });
        thread.setName("GC JMX Query Thread");
        thread.setDaemon(true);
        thread.start();
    }

    public List<String> getLocalProcessesList() {
        return gen.getLocalProcessesList();
    }

}
