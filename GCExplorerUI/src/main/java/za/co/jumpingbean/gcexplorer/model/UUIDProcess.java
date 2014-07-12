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

import java.util.Formatter;
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

    private static int count = 0;
    private final int number;
    private final UUID id;
    private final EdenMemoryPool edenPool;
    private final SurvivorMemoryPool survivorPool;
    private final OldGenMemoryPool oldGenPool;
    private final List<MemoryPool> list;
    private final PermGenMemoryPool permGenPool;
    private final EmptySurvivorMemoryPool emptySurvivorPool;
    private final ObservableList<XYChart.Series<String, Number>> stackedBarChartSeries = FXCollections.observableArrayList();
    private final ObservableList<XYChart.Series<Number, Number>> stackedAreaChartSeries = FXCollections.observableArrayList();
    private final SimpleStringProperty gcInfo = new SimpleStringProperty();
    private final SimpleStringProperty sysInfo = new SimpleStringProperty();
    private final String initParams;

    public UUIDProcess(UUID id, EdenMemoryPool edenPool,
            SurvivorMemoryPool survivorPool, EmptySurvivorMemoryPool emptySurvivorMemoryPool, OldGenMemoryPool oldGenPool, PermGenMemoryPool permGenPool, String initParams) {
        this.id = id;
        number = ++count;
        this.edenPool = edenPool;
        this.survivorPool = survivorPool;
        this.oldGenPool = oldGenPool;
        this.permGenPool = permGenPool;
        this.emptySurvivorPool = emptySurvivorMemoryPool;
        this.initParams = initParams;
        list = new LinkedList<>();
        list.add(this.edenPool);
        list.add(this.survivorPool);
        list.add(this.oldGenPool);
        stackedBarChartSeries.add(new XYChart.Series<>());//eden free
        stackedBarChartSeries.add(new XYChart.Series<>());//eden used
        stackedBarChartSeries.add(new XYChart.Series<>());//survivor used
        stackedBarChartSeries.add(new XYChart.Series<>());//survior free
        stackedBarChartSeries.add(new XYChart.Series<>());//empty survivor user
        stackedBarChartSeries.add(new XYChart.Series<>());//empty survivor free
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
        stackedBarChartSeries.get(4).setName("Empty Survivor Used");
        stackedBarChartSeries.get(4).getData().add(new Data("Total", 0));
        stackedBarChartSeries.get(5).setName("Empty Survivor Free");
        stackedBarChartSeries.get(5).getData().add(new Data("Total", 0));
        stackedBarChartSeries.get(6).setName("Old Gen Used");
        stackedBarChartSeries.get(6).getData().add(new Data("Total", 0));
        stackedBarChartSeries.get(7).setName("Old Gen Free");
        stackedBarChartSeries.get(7).getData().add(new Data("Total", 0));

        stackedAreaChartSeries.addAll(0, this.edenPool.usedFreeSeriesList);
        stackedAreaChartSeries.addAll(2, this.survivorPool.usedFreeSeriesList);
        stackedAreaChartSeries.addAll(4, this.emptySurvivorPool.usedFreeSeriesList);
        stackedAreaChartSeries.addAll(6, this.oldGenPool.usedFreeSeriesList);

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

        //update empty survivor space.
        survivorPool.getCommitted().measureProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                //stackedBarChartSeries.get(4).getData().get(0).setYValue(newValue);
                stackedBarChartSeries.get(5).getData().get(0).setYValue(newValue);
            }
        });

        oldGenPool.getUsed().measureProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                stackedBarChartSeries.get(6).getData().get(0).setYValue(newValue);
            }
        });

        oldGenPool.getFree().measureProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                stackedBarChartSeries.get(7).getData().get(0).setYValue(newValue);
            }
        });

    }

    public String getDescription() {
        return "Proc "+ Integer.toString(this.number);
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

    public EmptySurvivorMemoryPool getEmptySurvivorPool() {
        return emptySurvivorPool;
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
        return this.getMaxYoungGen() + oldGenPool.getMax().getMeasure();
    }

    public void setSysInfo(boolean flag) {
        //hack to force update to textarea text
        if (flag) {
            this.sysInfo.set("Updating...");
        }
        StringBuilder str = new StringBuilder("Java Version:");
        String version = System.getProperty("java.version");
        str.append(version).append("\n\r");
        str.append(this.initParams).append("\n\r");
        Formatter pf = new Formatter(str);
        pf.format("%s %,33.2f", "Max Eden:", this.edenPool.getMax().getMeasure());
        str.append("\n\r");
        pf.format("%s %,29.2f", "Max Survivor:", this.survivorPool.getMax().getMeasure());
        str.append("\n\r");
        pf.format("%s %,18.2f", "Max Empty Survivor:", this.survivorPool.getMax().getMeasure());
        str.append("\n\r================================\n\r");
        pf.format("%s %,23.2f", "Young Gen Total:", this.getMaxYoungGen());
        str.append("\n\r");
        pf.format("%s %,36.2f", "Old Gen:", this.oldGenPool.getMax().getMeasure());
        str.append("\n\r================================\n\r");
        pf.format("%s %,31.2f", "Heap Total:", this.getMaxHeap());

        //force update to display. Seems to not update in some instances
        //TODO Invesitgate!
        //this.sysInfo.set("Updating,...");
        this.sysInfo.set(str.toString());
    }

    public void setGCInfo(String str) {
        this.gcInfo.set(str);
    }

    public void addSystemInfoEventListener(ChangeListener changeListener) {
        this.sysInfo.addListener(changeListener);
    }

    public void addGCInfoEventListener(ChangeListener changeListener) {
        this.gcInfo.addListener(changeListener);
    }

    private double getMaxYoungGen() {
        return this.getEdenPool().getMax().getMeasure() + this.getSurvivorPool().getMax().getMeasure() * 2;
    }

    public void updateNumDataPoints(int numDataPoints) {
        this.edenPool.setNumDataPoints(numDataPoints);
        this.permGenPool.setNumDataPoints(numDataPoints);
        this.survivorPool.setNumDataPoints(numDataPoints);
        this.emptySurvivorPool.setNumDataPoints(numDataPoints);
        this.oldGenPool.setNumDataPoints(numDataPoints);
    }

    public int getNumber() {
        return this.number;
    }

}
