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

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedAreaChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.converter.NumberStringConverter;

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
    @FXML
    private LineChart<Number, Number> chtPermGenSpace;
    @FXML
    private Button btnGenerateGarbageOptions;
    @FXML
    private TextArea lblSysInfo;
    @FXML
    private Label lblGCInfo;
    @FXML
    private TextArea txtGeneratorStatus;

    private final GCExplorer app;
    private final UUID procId;
    @FXML
    private StackedBarChart<String, Number> chtStackedBarTotalMemory;
    @FXML
    private CategoryAxis xAxisCategory;
    @FXML
    private HBox hBox;
    @FXML
    private GridPane gridPane;
    @FXML
    private StackedAreaChart<Number, Number> chtStackedAreaTotalMemory;
    @FXML
    private Button btnViewGCLog;
    private Stage gcViewForm;


    public ProcessViewForm(GCExplorer app, UUID procId) {
        this.procId = procId;
        this.app = app;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        configureChart(chtEdenSpace);
        configureChart(chtSurvivorSpace);
        configureChart(chtOldGenSpace);
        configureChart(chtPermGenSpace);
        configureChart(chtStackedAreaTotalMemory);

        chtEdenSpace.setData(app.getProcessController().getEdenSeries(procId));
        chtSurvivorSpace.setData(app.getProcessController().getSurvivorSeries(procId));
        chtOldGenSpace.setData(app.getProcessController().getOldGenSeries(procId));
        chtPermGenSpace.setData(app.getProcessController().getPermGenSeries(procId));
        ((NumberAxis) chtStackedAreaTotalMemory.getYAxis()).setForceZeroInRange(true);
        chtStackedAreaTotalMemory.setData(app.getProcessController().getStackedAreaChartSeries(procId));

        this.xAxisCategory.setCategories(FXCollections.<String>observableArrayList(Arrays.asList("Total")));
        this.chtStackedBarTotalMemory.setData(app.getProcessController().getStackedBarChartSeries(procId));
        configureChart(chtStackedBarTotalMemory);
        HBox.setHgrow(gridPane, Priority.ALWAYS);
        ((NumberAxis) this.chtStackedBarTotalMemory.getYAxis()).setTickLabelFormatter(new NumberStringConverter(NumberFormat.getIntegerInstance()));

        this.updateYAxii(app.getUnits());
        btnGenerateGarbageOptions.setOnAction(this::showGarbageOptionsForm);

        app.getProcessController().addSystemInfoEventListener(procId, new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (oldValue == null || !oldValue.equals(newValue)) {
                    Platform.runLater(new Runnable() {

                        @Override
                        public void run() {
                            lblSysInfo.setText(newValue);
                        }
                    });
                }
            }

        });
        btnViewGCLog.setOnAction(this::showGCLogView);
        app.getProcessController().addGCInfoEventListener(procId, new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (oldValue == null || !oldValue.equals(newValue)) {
                    Platform.runLater(new Runnable() {

                        @Override
                        public void run() {
                            lblGCInfo.setText(newValue);
                        }
                    });
                }
            }

        });
        lblSysInfo.setText("Updating...");
        lblGCInfo.setText("Updating...");
    }

    public void disableGenerateObjectsButton() {
        this.btnGenerateGarbageOptions.disableProperty().setValue(Boolean.TRUE);
        this.txtGeneratorStatus.disableProperty().setValue(Boolean.TRUE);
    }

    public UUID getProcUUID() {
        return procId;
    }

    private void showGCLogView(ActionEvent e) {
        if (gcViewForm != null) {
            gcViewForm.toFront();
            return;
        }
        gcViewForm = new Stage();
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("gcLogView.fxml")
            );
            GCLogViewForm controller = new GCLogViewForm(app, procId,this);
            loader.setController(controller);
            Parent pane = loader.load();
            Scene scene = new Scene(pane);
            gcViewForm.setScene(scene);
            gcViewForm.initModality(Modality.NONE);
            gcViewForm.initOwner(gridPane.getParent().getScene().getWindow());
            gcViewForm.initStyle(StageStyle.DECORATED);
            gcViewForm.setTitle(app.getProcessController().getProcName(procId));
            gcViewForm.setOnCloseRequest(controller);
            gcViewForm.show();
        } catch (IOException ex) {
            Logger.getLogger(ProcessViewForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void showGarbageOptionsForm(ActionEvent e) {
        Stage stage = new Stage();
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("garbageGeneratorOptionsForm.fxml")
            );
            GarbageGenerationOptionsForm controller = new GarbageGenerationOptionsForm(app, procId, this);
            loader.setController(controller);
            Parent pane = loader.load();
            Scene scene = new Scene(pane);
            stage.setScene(scene);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(gridPane.getParent().getScene().getWindow());
            stage.initStyle(StageStyle.UTILITY);
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(ProcessViewForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void configureChart(XYChart chart) {
        chart.setAnimated(false);
        chart.getXAxis().setAnimated(false);
        chart.getXAxis().setAutoRanging(true);
        if (chart.getXAxis() instanceof NumberAxis) {
            ((NumberAxis) chart.getXAxis()).setForceZeroInRange(false);
            ((NumberAxis) chart.getXAxis()).setTickLabelFormatter(new NumberStringConverter(NumberFormat.getIntegerInstance()));
        }
        chart.getYAxis().setAnimated(false);
        chart.getYAxis().setAutoRanging(true);
        ((NumberAxis) chart.getYAxis()).setForceZeroInRange(false);
        ((NumberAxis) chart.getYAxis()).setTickLabelFormatter(new NumberStringConverter(new DecimalFormat("#,###,##0.000")));

    }

    void updateYAxii(Units unit) {
        chtEdenSpace.getYAxis().setLabel(unit.getName());
        chtSurvivorSpace.getYAxis().setLabel(unit.getName());
        chtOldGenSpace.getYAxis().setLabel(unit.getName());
        chtPermGenSpace.getYAxis().setLabel(unit.getName());
        chtStackedBarTotalMemory.getYAxis().setLabel(unit.getName());
        chtStackedAreaTotalMemory.getYAxis().setLabel(unit.getName());
    }

    void setGenStatus(String str) {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                synchronized (app) {
                    txtGeneratorStatus.setText(str);
                }
            }
        });
    }

    void resetGCLogView() {
        this.gcViewForm=null;
    }

}
