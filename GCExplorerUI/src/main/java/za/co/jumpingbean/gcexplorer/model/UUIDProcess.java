/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.jumpingbean.gcexplorer.model;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import za.co.jumpingbean.gcexplorer.ui.EdenMemoryPool;

/**
 *
 * @author mark
 */
public class UUIDProcess {

    private final UUID id;
    private final EdenMemoryPool edenPool;
    private final SurvivorMemoryPool survivorPool;
    private final OldGenMemoryPool oldGenPool;
    private final List<MemoryPool> list;
    private final PermGenMemoryPool permGenPool;
    private final ObservableList<XYChart.Series<String, Number>> stackedBarChartSeries = FXCollections.observableArrayList();
    private final ObservableList<XYChart.Series<Number, Number>> stackedAreaChartSeries = FXCollections.observableArrayList();
    private final SimpleStringProperty gcInfo = new SimpleStringProperty();
    private final SimpleStringProperty sysInfo = new SimpleStringProperty();
    private final String initParams;
    
    public UUIDProcess(UUID id, EdenMemoryPool edenPool,
            SurvivorMemoryPool survivorPool, OldGenMemoryPool oldGenPool, PermGenMemoryPool permGenPool,String initParams) {
        this.id = id;
        this.edenPool = edenPool;
        this.survivorPool = survivorPool;
        this.oldGenPool = oldGenPool;
        this.permGenPool = permGenPool;
        this.initParams = initParams;
        list = new LinkedList<>();
        list.add(this.edenPool);
        list.add(this.survivorPool);
        list.add(this.oldGenPool);
        stackedBarChartSeries.add(new XYChart.Series<>());//eden free
        stackedBarChartSeries.add(new XYChart.Series<>());//eden used
        stackedBarChartSeries.add(new XYChart.Series<>());//survivor used
        stackedBarChartSeries.add(new XYChart.Series<>());//survior free
        stackedBarChartSeries.add(new XYChart.Series<>());//old gen used
        stackedBarChartSeries.add(new XYChart.Series<>());//old gen free
        stackedBarChartSeries.get(0).setName("Eden Used");
        stackedBarChartSeries.get(0).getData().add(new Data("Total", 0));
        stackedBarChartSeries.get(1).setName("Eden Free");
        stackedBarChartSeries.get(1).getData().add(new Data("Total", 0));
        stackedBarChartSeries.get(2).setName("Survivor Used");
        stackedBarChartSeries.get(2).getData().add(new Data("Total", 0));
        stackedBarChartSeries.get(3).setName("Survivor Free");
        stackedBarChartSeries.get(3).getData().add(new Data("Total", 0));
        stackedBarChartSeries.get(4).setName("Old Gen Used");
        stackedBarChartSeries.get(4).getData().add(new Data("Total", 0));
        stackedBarChartSeries.get(5).setName("Old Gen Free");
        stackedBarChartSeries.get(5).getData().add(new Data("Total", 0));

        stackedAreaChartSeries.addAll(0,this.edenPool.usedFreeSeriesList);
        stackedAreaChartSeries.addAll(2,this.survivorPool.usedFreeSeriesList);
        stackedAreaChartSeries.addAll(4,this.oldGenPool.usedFreeSeriesList);

        edenPool.getUsed().measureProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                stackedBarChartSeries.get(0).getData().get(0).setYValue(newValue);
            }
        });

        edenPool.getFree().measureProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                stackedBarChartSeries.get(1).getData().get(0).setYValue(newValue);
            }
        });

        survivorPool.getFree().measureProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                stackedBarChartSeries.get(2).getData().get(0).setYValue(newValue);
            }
        });

        survivorPool.getUsed().measureProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                stackedBarChartSeries.get(3).getData().get(0).setYValue(newValue);
            }
        });

        oldGenPool.getUsed().measureProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                stackedBarChartSeries.get(4).getData().get(0).setYValue(newValue);
            }
        });

        oldGenPool.getFree().measureProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                stackedBarChartSeries.get(5).getData().get(0).setYValue(newValue);
            }
        });

    }

    public String getDescription() {
        return id.toString();
    }

    public UUID getId() {
        return id;
    }

    public EdenMemoryPool getEdenPool() {
        return edenPool;
    }

    public SurvivorMemoryPool getSurvivorPool() {
        return survivorPool;
    }

    public OldGenMemoryPool getOldGenPool() {
        return oldGenPool;
    }

    public PermGenMemoryPool getPermGenPool() {
        return this.permGenPool;
    }

    public List<MemoryPool> getDataItems() {
        return list;
    }

    public MemoryPool
            getPool(Class<? extends MemoryPool> pool) {
        if (pool.equals(EdenMemoryPool.class
        )) {
            return this.edenPool;
        } else if (pool.equals(SurvivorMemoryPool.class
        )) {
            return this.survivorPool;
        } else if (pool.equals(OldGenMemoryPool.class
        )) {
            return this.oldGenPool;
        } else {
            return this.permGenPool;
        }
    }

    public ObservableList<XYChart.Series<String, Number>> getStackedBarChartSeries() {
        return stackedBarChartSeries;
    }

    public ObservableList<XYChart.Series<Number, Number>> getStackedAreaChartDataItems() {
        return stackedAreaChartSeries;
    }

    public double getMaxHeap() {
            return edenPool.getMax().getMeasure()+survivorPool.getMax().getMeasure()+oldGenPool.getMax().getMeasure();
    }

    
    public void setSysInfo(){
        StringBuilder str = new StringBuilder(this.initParams);
        str.append("Max Heap:").append(this.getMaxHeap());
         this.sysInfo.set(str.toString());
    }
    
    public void setGCInfo(String str){
        this.gcInfo.set(str);
    }
    
    public void addSystemInfoEventListener(ChangeListener changeListener){
        this.sysInfo.addListener(changeListener);
    }
        
    public void addGCInfoEventListener(ChangeListener changeListener){
        this.gcInfo.addListener(changeListener);
    }
    
}
