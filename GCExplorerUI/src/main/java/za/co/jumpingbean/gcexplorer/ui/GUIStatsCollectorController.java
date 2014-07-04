/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.jumpingbean.gcexplorer.ui;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart.Series;
import javax.inject.Inject;
import za.co.jumpingbean.gc.service.GeneratorService;
import za.co.jumpingbean.gc.service.constants.DESC;
import za.co.jumpingbean.gcexplorer.model.DataItem;
import za.co.jumpingbean.gcexplorer.model.MemoryPool;
import za.co.jumpingbean.gcexplorer.model.ProcessFactory;
import za.co.jumpingbean.gcexplorer.model.UUIDProcess;

/**
 *
 * @author mark
 */
public class GUIStatsCollectorController implements Runnable {

    private final Map<UUID, UUIDProcess> liveProcesses
            = new HashMap<>();

    private int millisecs = 1000;
    private final Main main;

    private boolean isRunning = true;

    @Inject
    private final GeneratorService gen = new GeneratorService();

    GUIStatsCollectorController(Main main) {
        this.main = main;
    }

    public void stopAllProcesses() {
        isRunning = false;
        gen.stopAllTestApps();
        this.liveProcesses.clear();
    }

//    public void startScheduler() {
//        isRunning = true;
//    }
    public void stopProcess(UUID procId) {
        gen.stopTestApp(procId);
        this.liveProcesses.remove(procId);
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
                    Logger.getLogger(GUIStatsCollectorController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private void updateDataItems(UUID procId) {

        UUIDProcess proc = this.liveProcesses.get(procId);
        int convert = main.getUnits().getConversionUnits();
        final Timestamp ts = new Timestamp(System.currentTimeMillis());

        double result = gen.getMeasure(procId, DESC.EDENSPACEUSED).doubleValue() / convert;
        proc.getEdenPool().setUsedValue(result);
        runLater(proc.getEdenPool(), 0, ts, result);

        result = gen.getMeasure(procId, DESC.EDENSPACECOMMITTED).doubleValue() / convert;
        proc.getEdenPool().setCommittedValue(result);
        runLater(proc.getEdenPool(), 1, ts, result);

        result = gen.getMeasure(procId, DESC.EDENSPACEMAX).doubleValue() / convert;
        proc.getEdenPool().setMaxValue(result);

        result = gen.getMeasure(procId, DESC.EDENSPACEFREE).doubleValue() / convert;
        proc.getEdenPool().setFreeValue(result);
 
        result = gen.getMeasure(procId, DESC.SURVIVORSPACEUSED).doubleValue() / convert;
        proc.getSurvivorPool().setUsedValue(result);
        runLater(proc.getSurvivorPool(), 0, ts, result);

        result = gen.getMeasure(procId, DESC.SURVIVORSPACECOMMITTED).doubleValue() / convert;
        proc.getSurvivorPool().setCommittedValue(result);
        runLater(proc.getSurvivorPool(), 1, ts, result);

        proc.getSurvivorPool().setMaxValue(gen.getMeasure(procId, DESC.SURVIVORSPACEMAX).doubleValue() / convert);
        proc.getSurvivorPool().setFreeValue(gen.getMeasure(procId, DESC.SURVIVORSPACEFREE).doubleValue() / convert);

        result = gen.getMeasure(procId, DESC.OLDGENSPACEUSED).doubleValue() / convert;
        proc.getOldGenPool().setUsedValue(result);
        runLater(proc.getOldGenPool(), 0, ts, result);

        result = gen.getMeasure(procId, DESC.OLDGENSPACECOMMITTED).doubleValue() / convert;
        proc.getOldGenPool().setCommittedValue(result);
        runLater(proc.getOldGenPool(),1, ts, result);

        proc.getOldGenPool().setMaxValue(gen.getMeasure(procId, DESC.OLDGENSPACEMAX).doubleValue() / convert);
        proc.getOldGenPool().setFreeValue(gen.getMeasure(procId, DESC.OLDGENSPACEFREE).doubleValue() / convert);

    }

    public int getMillisecs() {
        return millisecs;
    }

    public void setMillisecs(int millisecs) {
        this.millisecs = millisecs;
    }

    public boolean isIsRunning() {
        return isRunning;
    }

    public UUID launchProcess(String string) throws IllegalStateException, IOException {
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
        UUID procId;
        String tmpPort = port.toString();
        port++;
        procId = gen.startTestApp(tmpPort, "/home/mark/Projects/gcexplorer/GarbageGeneratorApp"
                + "/build/libs/GarbageGeneratorApp.jar",
                "za.co.jumpingbean.gc.testApp.GarbageGeneratorApp", gcOptions);
        this.liveProcesses.put(procId, ProcessFactory.newProcess(procId));
        return procId;
    }

    public ObservableList<Series<Number, Number>> getEdenSeries(UUID procId) {
        return this.liveProcesses.get(procId).getEdenPool().getSeries();
    }

    public ObservableList<Series<Number, Number>> getSurvivorSeries(UUID procId) {
        return this.liveProcesses.get(procId).getSurvivorPool().getSeries();
    }

    public ObservableList<Series<Number, Number>> getOldGenSeries(UUID procId) {
        return this.liveProcesses.get(procId).getOldGenPool().getSeries();
    }

    public Collection<UUIDProcess> getDataItems() {
        return this.liveProcesses.values();
    }

    private void runLater(MemoryPool pool, int index, Timestamp ts, final double result) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                pool.addDataPoint(index,ts, result);
            }
        });
    }
}

