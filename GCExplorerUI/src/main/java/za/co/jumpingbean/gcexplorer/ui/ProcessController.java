/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.jumpingbean.gcexplorer.ui;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart.Series;
import javax.inject.Inject;
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
    private final Main main;

    private boolean isRunning = true;

    @Inject
    private final GeneratorService gen = new GeneratorService();

    ProcessController(Main main) {
        this.main = main;
    }

    public UUIDProcess getUUIDProcess(UUID id) {
        synchronized (liveProcesses) {
            return liveProcesses.get(id);
        }
    }

    public void stopAllProcesses() {
        isRunning = false;
        gen.stopAllTestApps();
        synchronized (liveProcesses) {
            this.liveProcesses.clear();
        }
    }

    public void stopProcess(UUID procId) {
        gen.stopTestApp(procId);
        synchronized (liveProcesses) {
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
            UUIDProcess proc = this.liveProcesses.get(procId);
            int convert = main.getUnits().getConversionUnits();
            final Timestamp ts = new Timestamp(System.currentTimeMillis());

            double result = gen.getMeasure(procId, DESC.EDENSPACEUSED).doubleValue() / convert;
            proc.getEdenPool().setUsedValue(result);
            //runLater(proc.getEdenPool(), 0, ts, result);

            double result2 = gen.getMeasure(procId, DESC.EDENSPACECOMMITTED).doubleValue() / convert;
            proc.getEdenPool().setCommittedValue(result2);

            double result3 = gen.getMeasure(procId, DESC.EDENSPACEFREE).doubleValue() / convert;
            proc.getEdenPool().setFreeValue(result3);
            runLater(proc.getEdenPool(), ts, result, result2, result3);

            result = gen.getMeasure(procId, DESC.EDENSPACEMAX).doubleValue() / convert;
            proc.getEdenPool().setMaxValue(result);

            result = gen.getMeasure(procId, DESC.SURVIVORSPACEUSED).doubleValue() / convert;
            proc.getSurvivorPool().setUsedValue(result);
            //runLater(proc.getSurvivorPool(), 0, ts, result);

            result2 = gen.getMeasure(procId, DESC.SURVIVORSPACECOMMITTED).doubleValue() / convert;
            proc.getSurvivorPool().setCommittedValue(result2);

            result3 = gen.getMeasure(procId, DESC.SURVIVORSPACEFREE).doubleValue() / convert;
            proc.getSurvivorPool().setFreeValue(result3);
            runLater(proc.getSurvivorPool(), ts, result, result2, result3);

            proc.getSurvivorPool().setMaxValue(gen.getMeasure(procId, DESC.SURVIVORSPACEMAX).doubleValue() / convert);


            //Set free survivor space stats
            proc.getEmptySurvivorPool().setUsedValue(0d);
            proc.getEmptySurvivorPool().setCommittedValue(result2);
            proc.getEmptySurvivorPool().setFreeValue(result2);
            runLater(proc.getEmptySurvivorPool(), ts, 0, result2, result2);
            proc.getEmptySurvivorPool().setMaxValue(gen.getMeasure(procId, DESC.SURVIVORSPACEMAX).doubleValue() / convert);
            
            
            
            result = gen.getMeasure(procId, DESC.OLDGENSPACEUSED).doubleValue() / convert;
            proc.getOldGenPool().setUsedValue(result);
            //runLater(proc.getOldGenPool(), 0, ts, result);

            result2 = gen.getMeasure(procId, DESC.OLDGENSPACECOMMITTED).doubleValue() / convert;
            proc.getOldGenPool().setCommittedValue(result2);

            result3 = gen.getMeasure(procId, DESC.OLDGENSPACEFREE).doubleValue() / convert;
            proc.getOldGenPool().setFreeValue(result3);
            runLater(proc.getOldGenPool(), ts, result, result2, result3);

            proc.getOldGenPool().setMaxValue(gen.getMeasure(procId, DESC.OLDGENSPACEMAX).doubleValue() / convert);

            result = gen.getMeasure(procId, DESC.PERMGENSPACEUSED).doubleValue() / convert;
            proc.getPermGenPool().setUsedValue(result);
            //runLater(proc.getPermGenPool(), 0, ts, result);

            result2 = gen.getMeasure(procId, DESC.PERMGENSPACECOMMITTED).doubleValue() / convert;
            proc.getPermGenPool().setCommittedValue(result2);

            result3 = gen.getMeasure(procId, DESC.PERMGENSPACEFREE).doubleValue() / convert;
            proc.getPermGenPool().setFreeValue(result3);
            runLater(proc.getPermGenPool(), ts, result, result2, result3);

            proc.getPermGenPool().setMaxValue(gen.getMeasure(procId, DESC.PERMGENSPACEMAX).doubleValue() / convert);

            String gcInfo = gen.getGCInfo(procId);
            proc.setGCInfo(gcInfo);
            proc.setSysInfo();
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

    public UUID launchProcess(String string,List<String> gcOptionsExtra) throws IllegalStateException, IOException {
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
        gcOptions.add(string);
        gcOptions.addAll(gcOptionsExtra);
        UUID procId;
        String tmpPort = port.toString();
        port++;
        procId = gen.startTestApp(tmpPort, "/home/mark/Projects/gcexplorer/GarbageGeneratorApp"
                + "/build/libs/GarbageGeneratorApp.jar",
                "za.co.jumpingbean.gc.testApp.GarbageGeneratorApp", gcOptions);
        synchronized (liveProcesses) {
            this.liveProcesses.put(procId, ProcessFactory.newProcess(procId,gcOptions.toString()));
        }
        return procId;
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

    void genLocalInstances(UUID id, int numInstances, int instanceSize, int creationPauseTime, int returnDelay) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                gen.genLocalInstances(id, numInstances, instanceSize, creationPauseTime, returnDelay);
            }
        });
        thread.setName("GC JMX Query Thread");
        thread.setDaemon(true);
        thread.start();
    }

    void genLongLivedInstances(UUID id, int numInstances, int instanceSize, int creationPauseTime, int returnDelay) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                gen.genLongLivedInstances(id, numInstances, instanceSize, creationPauseTime, returnDelay);
            }
        });
        thread.setName("GC JMX Query Thread");
        thread.setDaemon(true);
        thread.start();
    }

    String getParameters(UUID procId) {
            return this.gen.getProcessParams(procId).getStartupParameters();
    }
    
//    public double getMaxHeap(UUID uuid){
//        return this.liveProcesses.get(uuid).getMaxHeap();
//    }

    void addSystemInfoEventListener(UUID procId, ChangeListener changeListener) {
            this.liveProcesses.get(procId).addSystemInfoEventListener(changeListener);
    }

    void addGCInfoEventListener(UUID procId, ChangeListener changeListener) {
            this.liveProcesses.get(procId).addGCInfoEventListener(changeListener);
    }

}
