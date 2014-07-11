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
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import za.co.jumpingbean.gcexplorer.model.MemoryPool;
import za.co.jumpingbean.gcexplorer.model.OldGenMemoryPool;
import za.co.jumpingbean.gcexplorer.model.PermGenMemoryPool;
import za.co.jumpingbean.gcexplorer.model.SurvivorMemoryPool;
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
    private TableColumn<UUIDProcess, Number> tcEdenCommitted;
    @FXML
    private TableColumn<UUIDProcess, Number> tcEdenUsed;
    @FXML
    private TableColumn<UUIDProcess, Number> tcEdenMax;
    @FXML
    private TableColumn<UUIDProcess, Number> tcEdenFree;

    @FXML
    private TableColumn<UUIDProcess, Number> tcSurvivorCommitted;
    @FXML
    private TableColumn<UUIDProcess, Number> tcSurvivorUsed;
    @FXML
    private TableColumn<UUIDProcess, Number> tcSurvivorMax;
    @FXML
    private TableColumn<UUIDProcess, Number> tcSurvivorFree;
    
    @FXML
    private TableColumn<UUIDProcess, Number> tcOldGenCommitted;
    @FXML
    private TableColumn<UUIDProcess, Number> tcOldGenUsed;
    @FXML
    private TableColumn<UUIDProcess, Number> tcOldGenMax;
    @FXML
    private TableColumn<UUIDProcess, Number> tcOldGenFree;

    @FXML
    private TableColumn<UUIDProcess, Number> tcPermGenCommitted;
    @FXML
    private TableColumn<UUIDProcess, Number> tcPermGenUsed;
    @FXML
    private TableColumn<UUIDProcess, Number> tcPermGenMax;
    @FXML
    private TableColumn<UUIDProcess, Number> tcPermGenFree;    
    
    @FXML
    private TableColumn<UUIDProcess, String> tcDescription;
    @FXML
    private RadioMenuItem rdbGB;
    @FXML
    private RadioMenuItem rdbKB;
    @FXML
    private RadioMenuItem rdbMB;
    @FXML
    private RadioMenuItem rdbB;
    @FXML
    private TabPane tabPane;

    private final Main app;
    
    private ObservableList<UUIDProcess> processData= FXCollections.observableArrayList(new ArrayList<>());

    MainForm(ProcessController statsCollector, Main app) {
        this.app = app;
    }

    protected void changeUnits(ActionEvent e) {
        Units unit = (Units) this.unitGroup.getSelectedToggle().getUserData();
        app.setUnits(unit);
        for (Tab tab : tabPane.getTabs()){
            ((ProcessViewForm)tab.getUserData()).updateYAxii(unit);
        }
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
            stage.initOwner(this.tblDetails.getParent().getScene().getWindow());
            stage.initStyle(StageStyle.UTILITY);
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
        this.setColumns(EdenMemoryPool.class, tcEdenCommitted, tcEdenFree, tcEdenUsed, tcEdenMax);
        this.setColumns(SurvivorMemoryPool.class, tcSurvivorCommitted, tcSurvivorFree, tcSurvivorUsed, tcSurvivorMax);
        this.setColumns(OldGenMemoryPool.class, tcOldGenCommitted, tcOldGenFree, tcOldGenUsed, tcOldGenMax);
        this.setColumns(PermGenMemoryPool.class, tcPermGenCommitted, tcPermGenFree, tcPermGenUsed, tcPermGenMax);

        tblDetails.setItems(processData);
    }

    private void setColumns(Class<? extends MemoryPool> pool, TableColumn committed,TableColumn free, TableColumn used,TableColumn max){
        committed.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<UUIDProcess, Number>, ObservableValue<Number>>() {

            @Override
            public ObservableValue<Number> call(TableColumn.CellDataFeatures<UUIDProcess, Number> param) {
                return param.getValue().getPool(pool).getCommitted().measureProperty();
            }
        });
        committed.setCellFactory(new CellNumberFormatter(app));

        used.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<UUIDProcess, Number>, ObservableValue<Number>>() {

            @Override
            public ObservableValue<Number> call(TableColumn.CellDataFeatures<UUIDProcess, Number> param) {
                return param.getValue().getPool(pool).getUsed().measureProperty();
            }
        });
        used.setCellFactory(new CellNumberFormatter(app));

        free.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<UUIDProcess, Number>, ObservableValue<Number>>() {

            @Override
            public ObservableValue<Number> call(TableColumn.CellDataFeatures<UUIDProcess, Number> param) {
                return param.getValue().getPool(pool).getFree().measureProperty();
            }
        });
        free.setCellFactory(new CellNumberFormatter(app));

        max.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<UUIDProcess, Number>, ObservableValue<Number>>() {

            @Override
            public ObservableValue<Number> call(TableColumn.CellDataFeatures<UUIDProcess, Number> param) {
                return param.getValue().getPool(pool).getMax().measureProperty();
            }
        });
        max.setCellFactory(new CellNumberFormatter(app));
        
    }
    
    
    void addTab(Parent pane,UUID procId,ProcessViewForm controller) {
        Tab tab = new Tab();
        tab.setUserData(procId);
        tab.setUserData(controller);
        tab.setOnClosed(new EventHandler<Event>() {

            @Override
            public void handle(Event event) {
                UUID id = (UUID) (((Tab)event.getSource()).getUserData());
                processData.remove(app.getProcessController().getUUIDProcess(id));
                app.getProcessController().stopProcess(id);
                    
            }
        });
        tab.setText("Proc");
        tab.setContent(pane);
        tabPane.getTabs().add(tab);
        this.processData.add(this.app.getProcessController().getUUIDProcess(procId));
    }

}
