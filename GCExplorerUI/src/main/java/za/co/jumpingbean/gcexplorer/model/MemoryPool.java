/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.jumpingbean.gcexplorer.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;

/**
 *
 * @author mark
 */
public abstract class MemoryPool {

    protected final ObservableList<XYChart.Series<Number, Number>> seriesList;
    protected final DataItem max;
    protected final DataItem used;
    protected final DataItem committed;
    protected final DataItem free;
    protected int numDataPoints = 40;
    protected final ReentrantReadWriteLock lock;
    protected final Timestamp startTime;

    public MemoryPool(int numDataPoints, Timestamp startTime) {
        this.startTime = startTime;
        this.numDataPoints = numDataPoints;
        this.max = new DataItem("Max", 0);
        this.used = new DataItem("Used", 0);
        this.committed = new DataItem("Committed", 0);
        this.free = new DataItem("Free", 0);
        seriesList = FXCollections.observableArrayList(new ArrayList<XYChart.Series<Number, Number>>());
        lock = new ReentrantReadWriteLock();
        for (int i = 0; i < 2; i++) {
            XYChart.Series<Number, Number> series = new XYChart.Series();
            series.setData(FXCollections.observableArrayList(new ArrayList<Data<Number, Number>>()));
            seriesList.add(series);
        }
    }

    public int getNumDataPoints() {
        return numDataPoints;
    }

    public void setNumDataPoints(int numDataPoints) {
        lock.writeLock().lock();
        try {
            this.numDataPoints = numDataPoints;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public ObservableList<XYChart.Series<Number, Number>> getSeries() {
        return seriesList;
    }

    public DataItem getMax() {
        return max;
    }

    public DataItem getUsed() {
        return used;
    }

    public DataItem getCommitted() {
        return committed;
    }

    public DataItem getFree() {
        return free;
    }

    public void setMaxValue(Double num) {
        this.max.setMeasure(num);
    }

    public void setUsedValue(Double num) {
        this.used.setMeasure(num);
    }

    public void setCommittedValue(Double num) {
        this.committed.setMeasure(num);
    }

    public void setFreeValue(Double num) {
        this.free.setMeasure(num);
    }

    public void addDataPoint(int index,Timestamp ts, Number value) {
        ObservableList<Data<Number, Number>> list = seriesList.get(index).getData();
        lock.writeLock().lock();
        try {
            if (list.size() > this.numDataPoints) {
                int over = list.size() - this.numDataPoints;
                list.remove(0, over);
            }
            list.add(new Data(ts.getTime() - startTime.getTime(), value));
        } finally {
            lock.writeLock().unlock();
        }
    }

}
