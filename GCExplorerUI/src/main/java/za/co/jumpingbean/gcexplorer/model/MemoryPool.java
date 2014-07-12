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
package za.co.jumpingbean.gcexplorer.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;
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
        //create empty data series
        for (int i = 0; i < 2; i++) {
            XYChart.Series<Number, Number> freeCommittedSeries = new XYChart.Series();
            freeCommittedSeries.setData(FXCollections.observableArrayList(new LinkedList<Data<Number, Number>>()));
            freeCommittedSeriesList.add(freeCommittedSeries);
            if (i == 0) {
                freeCommittedSeries.setName("Used");
            } else {
                freeCommittedSeries.setName("Committed");
            }

            XYChart.Series<Number, Number> usedFreeSeries = new XYChart.Series();
            //Initiaise usedFree series to prevent AreaGraph crashing on empty data set
            List<Data<Number, Number>> tmpList = new LinkedList<>();
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

            //TODO Make conversion of milliseconds accomodate changes in sampling time.
            this.addDataWithRetry(fcList1, new Data((ts.getTime() - startTime.getTime()) / 1000, used),"used");
            this.addDataWithRetry(ufList1, new Data((ts.getTime() - startTime.getTime()) / 1000, used),"used");
            this.addDataWithRetry(fcList2, new Data((ts.getTime() - startTime.getTime()) / 1000, committed),"committed");
            this.addDataWithRetry(ufList2, new Data((ts.getTime() - startTime.getTime()) / 1000, free),"free");

            if (fcList1.size() > this.numDataPoints) {
                int over = fcList1.size() - this.numDataPoints;
                this.removeExcessDataItems(fcList1, over,"used");
                this.removeExcessDataItems(fcList2, over,"used");
            }
            if (ufList1.size() > this.numDataPoints) {
                int over = ufList1.size() - this.numDataPoints;
                this.removeExcessDataItems(ufList1, over,"committed");
                this.removeExcessDataItems(ufList2, over,"free");
            }            
            
        } finally {
            lock.writeLock().unlock();
        }
    }

    //Sometime get null pointer exceptions when adding points and points have been removed.
    private void addDataWithRetry(ObservableList<Data<Number, Number>> list, Data data,String type) {
        try {
            list.add(data);
        } catch(NullPointerException ex){
            //Weird null pointer exception from XYChart.Series complaining about
            //chart pointer being null for PermGen and EmptySurvivorSpace
            //Following up with JavaFX team.
            //ex.printStackTrace();
        }    
    }

    private void removeExcessDataItems(ObservableList<Data<Number, Number>> list, int numItems,String type) {
        try {
            list.remove(0);
        } catch(NullPointerException ex){
          //  System.out.println("Remove Data Null Pointer: size=" + list.size()+" over="+numItems);
          //  ex.printStackTrace();            
        } catch(UnsupportedOperationException ex){
            //At some point JavaFX deciceds to add the removed element to a unmodifiable list.
            //for PermGen and EmptySurvivorSpace
         //   ex.printStackTrace();            
        }    
    }
}
