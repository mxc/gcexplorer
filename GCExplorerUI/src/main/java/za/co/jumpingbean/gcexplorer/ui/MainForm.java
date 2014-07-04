/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.jumpingbean.gcexplorer.ui;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import za.co.jumpingbean.gcexplorer.model.UUIDProcess;

/**
 *
 * @author mark
 */
public class MainForm implements Initializable {

    @FXML
    private MenuItem mnuNewProcess;
    @FXML
    private TableView<UUIDProcess> tblDetails;
    @FXML
    private ToggleGroup unitGroup;
    @FXML
    private TableColumn<UUIDProcess, Number> tcCommitted;
    @FXML
    private TableColumn<UUIDProcess, Number> tcUsed;
    @FXML
    private TableColumn<UUIDProcess, Number> tcMax;
    @FXML
    private TableColumn<UUIDProcess, Number> tcFree;
    @FXML
    private TableColumn<UUIDProcess, String> tcDescription;
    @FXML
    private RadioButton rdbGB;
    @FXML
    private RadioButton rdbKB;
    @FXML
    private RadioButton rdbMB;
    @FXML
    private RadioButton rdbB;
    @FXML
    private TabPane tabPane;

    private final Main app;

    MainForm(GUIStatsCollectorController statsCollector, Main app) {
        this.app = app;
    }

    protected void changeUnits(ActionEvent e) {
        app.setUnits((Units) this.unitGroup.getSelectedToggle().getUserData());
    }

    protected void newProcess(ActionEvent e) {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("launchProcessDialog.fxml")
            );
            loader.setController(new LaunchProcessDialog(app,this));
            Parent pane = loader.load();
            stage.setScene(new Scene(pane));
            stage.setTitle("Start New Process");
            stage.initModality(Modality.WINDOW_MODAL);
//            stage.initOwner(
//                    ((MenuItem) e.getSource()).getScene().getWindow());
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.mnuNewProcess.setOnAction(this::newProcess);
        this.rdbB.setOnAction(this::changeUnits);
        this.rdbKB.setOnAction(this::changeUnits);
        this.rdbMB.setOnAction(this::changeUnits);
        this.rdbGB.setOnAction(this::changeUnits);

        this.rdbB.setUserData(Units.B);
        this.rdbKB.setUserData(Units.KB);
        this.rdbMB.setUserData(Units.MB);
        this.rdbGB.setUserData(Units.GB);

        tcDescription.setCellValueFactory(new PropertyValueFactory<>("description"));

        tcCommitted.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<UUIDProcess, Number>, ObservableValue<Number>>() {

            @Override
            public ObservableValue<Number> call(TableColumn.CellDataFeatures<UUIDProcess, Number> param) {
                return param.getValue().getEdenPool().getCommitted().measureProperty();
            }
        });
        tcCommitted.setCellFactory(new CellNumberFormatter(app));

        tcUsed.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<UUIDProcess, Number>, ObservableValue<Number>>() {

            @Override
            public ObservableValue<Number> call(TableColumn.CellDataFeatures<UUIDProcess, Number> param) {
                return param.getValue().getEdenPool().getUsed().measureProperty();
            }
        });
        tcUsed.setCellFactory(new CellNumberFormatter(app));

        tcFree.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<UUIDProcess, Number>, ObservableValue<Number>>() {

            @Override
            public ObservableValue<Number> call(TableColumn.CellDataFeatures<UUIDProcess, Number> param) {
                return param.getValue().getEdenPool().getFree().measureProperty();
            }
        });
        tcFree.setCellFactory(new CellNumberFormatter(app));

        tcMax.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<UUIDProcess, Number>, ObservableValue<Number>>() {

            @Override
            public ObservableValue<Number> call(TableColumn.CellDataFeatures<UUIDProcess, Number> param) {
                return param.getValue().getEdenPool().getMax().measureProperty();
            }
        });
        tcMax.setCellFactory(new CellNumberFormatter(app));
        tblDetails.setItems(FXCollections.observableList(new ArrayList(app.getProcessController().getDataItems())));
    }

    void addTab(Parent pane,UUID procId) {
        Tab tab = new Tab();
        tab.setUserData(procId);
        tab.setOnClosed(new EventHandler<Event>() {

            @Override
            public void handle(Event event) {
                    app.getProcessController().stopProcess((UUID) ((Tab)event.getSource()).getUserData());
            }
        });
        tab.setText("Proc");
        tab.setContent(pane);
        tabPane.getTabs().add(tab);
    }

}
