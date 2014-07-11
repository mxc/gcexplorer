/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.jumpingbean.gcexplorer.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
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

    protected final ObservableList<XYChart.Series<Number, Number>> freeCommittedSeriesList;
    protected final ObservableList<XYChart.Series<Number, Number>> usedFreeSeriesList;
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
        freeCommittedSeriesList = FXCollections.observableArrayList(new ArrayList<XYChart.Series<Number, Number>>());
        usedFreeSeriesList = FXCollections.observableArrayList(new ArrayList<XYChart.Series<Number, Number>>());
        lock = new ReentrantReadWriteLock();
        for (int i = 0; i < 2; i++) {
            XYChart.Series<Number, Number> freeCommittedSeries = new XYChart.Series();
            freeCommittedSeries.setData(FXCollections.observableArrayList(new ArrayList<Data<Number, Number>>()));
            freeCommittedSeriesList.add(freeCommittedSeries);
            if (i == 0) {
                freeCommittedSeries.setName("Used");
            } else {
                freeCommittedSeries.setName("Committed");
            }

            XYChart.Series<Number, Number> usedFreeSeries = new XYChart.Series();
            //Initiaise usedFree series to prevent AreaGraph crashing on empty data set
            List<Data<Number, Number>> tmpList = new ArrayList<>();
            tmpList.add(new Data(0, 0));
            usedFreeSeries.setData(FXCollections.observableArrayList(tmpList));
            usedFreeSeriesList.add(usedFreeSeries);
            String name = this.getClass().getSimpleName();
            name = name.substring(0, name.indexOf("MemoryPool"));
            if (i == 0) {
                usedFreeSeries.setName(name + " Used");
            } else {
                usedFreeSeries.setName(name + " Free");
            }
        }
    }

    public int getNumDataPoints() {
        return numDataPoints;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setNumDataPoints(int numDataPoints) {
        lock.writeLock().lock();
        try {
            this.numDataPoints = numDataPoints;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public ObservableList<XYChart.Series<Number, Number>> getFreeCommittedSeries() {
        return freeCommittedSeriesList;
    }

    public ObservableList<XYChart.Series<Number, Number>> getUsedFreeSeries() {
        return usedFreeSeriesList;
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

    //TODO: Remove or reimplement locking. Data read by JavaFX and no read lock
    public void addDataPoint(Timestamp ts, Number used, Number committed, Number free) {
        ObservableList<Data<Number, Number>> fcList1 = freeCommittedSeriesList.get(0).getData();
        ObservableList<Data<Number, Number>> ufList1 = usedFreeSeriesList.get(0).getData();

        ObservableList<Data<Number, Number>> fcList2 = freeCommittedSeriesList.get(1).getData();
        ObservableList<Data<Number, Number>> ufList2 = usedFreeSeriesList.get(1).getData();

        lock.writeLock().lock();
        try {
            if (fcList1.size() > this.numDataPoints) {
                int over = fcList1.size() - this.numDataPoints;
                try {
                    fcList1.remove(0, over);
                } catch (UnsupportedOperationException | NullPointerException ex) {
                    //javafx throws an error here.
                    //looks like it thinks its adding a data point
                    //that it has just been asked to remove
                }
                try {
                    fcList2.remove(0, over);
                } catch (UnsupportedOperationException | NullPointerException ex) {
                    //javafx throws an error here.
                    //looks like it thinks its adding a data point
                    //that it has just been asked to remove
                }

            }
            if (ufList1.size() > this.numDataPoints) {
                int over = ufList1.size() - this.numDataPoints;
                try {
                    ufList1.remove(0, over);
                } catch (UnsupportedOperationException | NullPointerException ex) {
                    //javafx throws an error here.
                    //looks like it thinks its adding a data point
                    //that it has just been asked to remove
                }
                try {
                    ufList2.remove(0, over);
                } catch (UnsupportedOperationException | NullPointerException ex) {
                    //javafx throws an error here.
                    //looks like it thinks its adding a data point
                    //that it has just been asked to remove
                }
            }
            //TODO Make conversion of milliseconds accomodate changes in sampling time.
            try {
                fcList1.add(new Data((ts.getTime() - startTime.getTime()) / 1000, used));
            } catch (NullPointerException ex) {

            }

            try {
                ufList1.add(new Data((ts.getTime() - startTime.getTime()) / 1000, used));
            } catch (NullPointerException ex) {

            }
            try {
                fcList2.add(new Data((ts.getTime() - startTime.getTime()) / 1000, committed));
            } catch (NullPointerException ex) {

            }
            try {
                ufList2.add(new Data((ts.getTime() - startTime.getTime()) / 1000, free));
            } catch (NullPointerException ex) {

            }
        } finally {
            lock.writeLock().unlock();
        }
    }

}
