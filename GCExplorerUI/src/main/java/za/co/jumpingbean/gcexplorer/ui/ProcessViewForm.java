/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.jumpingbean.gcexplorer.ui;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;

/**
 *
 * @author mark
 */
public class ProcessViewForm implements Initializable {

    @FXML
    private LineChart<Number, Number> chtEdenSpace;
    @FXML
    private LineChart<Number, Number> chtSurvivorSpace;
    @FXML
    private LineChart<Number, Number> chtOldGenSpace;
    private final Main app;
    private final UUID procId;

    public ProcessViewForm(Main app, UUID procId) {
            this.procId =procId;
            this.app=app;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        this.chtEdenSpace.setAnimated(false);
        this.chtEdenSpace.getXAxis().setAnimated(false);
        this.chtEdenSpace.getXAxis().setAutoRanging(true);
        ((NumberAxis) this.chtEdenSpace.getXAxis()).setForceZeroInRange(false);
        this.chtEdenSpace.getYAxis().setAnimated(false);
        this.chtEdenSpace.getYAxis().setAnimated(true);
        ((NumberAxis) this.chtEdenSpace.getYAxis()).setForceZeroInRange(false);

        this.chtSurvivorSpace.setAnimated(false);
        this.chtSurvivorSpace.getXAxis().setAnimated(false);
        this.chtSurvivorSpace.getXAxis().setAutoRanging(true);
        ((NumberAxis) this.chtSurvivorSpace.getXAxis()).setForceZeroInRange(false);
        this.chtSurvivorSpace.getYAxis().setAnimated(false);
        this.chtSurvivorSpace.getYAxis().setAnimated(true);
        ((NumberAxis) this.chtSurvivorSpace.getXAxis()).setForceZeroInRange(false);

        this.chtOldGenSpace.setAnimated(false);
        this.chtOldGenSpace.getXAxis().setAnimated(false);
        this.chtOldGenSpace.getXAxis().setAutoRanging(true);
        ((NumberAxis) this.chtOldGenSpace.getXAxis()).setForceZeroInRange(false);
        this.chtOldGenSpace.getYAxis().setAnimated(false);
        this.chtOldGenSpace.getYAxis().setAnimated(true);
        ((NumberAxis) this.chtOldGenSpace.getXAxis()).setForceZeroInRange(false);

        chtEdenSpace.setData(app.getProcessController().getEdenSeries(procId));
        chtSurvivorSpace.setData(app.getProcessController().getSurvivorSeries(procId));
        chtOldGenSpace.setData(app.getProcessController().getOldGenSeries(procId));

    }

}
