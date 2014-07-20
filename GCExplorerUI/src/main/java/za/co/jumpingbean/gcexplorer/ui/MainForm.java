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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
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
    private MenuItem mnuNumDataPoints;
    @FXML
    private MenuItem mnuAbout;
    @FXML
    private MenuItem mnuAttachToExisting;
    @FXML
    private MenuItem mnuManagePlatforms;
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

    private final GCExplorer app;
    private int numDataPoints = 40;

    private ObservableList<UUIDProcess> processData = FXCollections.observableArrayList(new ArrayList<>());

    MainForm(ProcessController statsCollector, GCExplorer app) {
        this.app = app;
    }

    protected void changeUnits(ActionEvent e) {
        Units unit = (Units) this.unitGroup.getSelectedToggle().getUserData();
        app.setUnits(unit);
        for (Tab tab : tabPane.getTabs()) {
            ((ProcessViewForm) tab.getUserData()).updateYAxii(unit);
        }
    }

    protected void newProcess(ActionEvent e) {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("launchProcessDialog.fxml")
            );
            loader.setController(new LaunchProcessDialog(app, this));
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

    protected void managePlatformsDialog(ActionEvent e) {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("managePlatforms.fxml")
            );
            loader.setController(new ManagePlatformsDialog(app, this));
            Parent pane = loader.load();
            stage.setScene(new Scene(pane));
            stage.setTitle("Manage Platforms");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(this.tblDetails.getParent().getScene().getWindow());
            stage.initStyle(StageStyle.UTILITY);
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }   
    
    
    protected void attachToProcessDialog(ActionEvent e) {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("attachExistingProcess.fxml")
            );
            loader.setController(new AttachExistingProcessDialog(app, this));
            Parent pane = loader.load();
            stage.setScene(new Scene(pane));
            stage.setTitle("Attach to Process");
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
        this.mnuNumDataPoints.setOnAction(this::showNumDataPointsDialog);
        this.mnuAttachToExisting.setOnAction(this::attachToProcessDialog);
        this.mnuManagePlatforms.setOnAction(this::managePlatformsDialog);

        this.mnuAbout.setOnAction(this::showAboutDialog);
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

    private void setColumns(Class<? extends MemoryPool> pool, TableColumn committed, TableColumn free, TableColumn used, TableColumn max) {
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

    public void addPane(UUID procId, boolean disableGeneratorButton) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                ProcessViewForm.class.getResource("processView.fxml")
        );
        ProcessViewForm controller = new ProcessViewForm(app, procId);
        loader.setController(controller);
        Parent pane;
        try {
            pane = loader.load();
            this.addTab(pane, procId, controller);
            if (disableGeneratorButton) {
                controller.disableGenerateObjectsButton();
            }
        } catch (IOException ex) {
            throw new IOException("There was an error adding tab to the main display");
        }

    }

    private void addTab(Parent pane, UUID procId, ProcessViewForm controller) {
        Tab tab = new Tab();
        tab.setUserData(procId);
        //tab.setUserData(controller);
        tab.setOnClosed(new EventHandler<Event>() {

            @Override
            public void handle(Event event) {
                UUID id = (UUID) (tab.getUserData());
                processData.remove(app.getProcessController().getUUIDProcess(id));
                app.getProcessController().stopProcess(id);

            }
        });
        tab.setText("Proc " + app.getProcessController().getNumber(procId));
        tab.setContent(pane);
        tabPane.getTabs().add(tab);
        this.processData.add(this.app.getProcessController().getUUIDProcess(procId));
    }

    private void showNumDataPointsDialog(ActionEvent e) {
        Stage stage = new Stage();
        VBox vBox = new VBox();
        HBox hBox = new HBox();
        Button btn = new Button("OK");
        Label lbl = new Label("Number of data points?");
        TextField txtNum = new TextField();
        txtNum.setMaxWidth(50);
        txtNum.setText(Integer.toString(this.numDataPoints));
        hBox.getChildren().add(lbl);
        hBox.getChildren().add(txtNum);

        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                String num = txtNum.getText();
                try {
                    int curr = numDataPoints;
                    numDataPoints = Integer.parseInt(num);
                    if (numDataPoints > 20 && curr != numDataPoints) {
                        app.getProcessController().updateNumberOfDataPoints(numDataPoints);
                    }
                    stage.close();
                } catch (NumberFormatException ex) {

                }
            }
        });
        hBox.setPadding(new Insets(0, 0, 10, 0));
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(10));
        vBox.getChildren().add(hBox);
        vBox.getChildren().add(btn);
        stage.setScene(new Scene(vBox));
        stage.setTitle("Set Data Set Size");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(this.tblDetails.getParent().getScene().getWindow());
        stage.initStyle(StageStyle.UTILITY);
        stage.show();

    }

    public void showAboutDialog(ActionEvent e) {

        VBox vBox = new VBox();
        vBox.setPadding(new Insets(10));
        Label jumpingbean = new Label("GCExplorer V0.1 by");
        jumpingbean.setPadding(new Insets(10));
        jumpingbean.setFont(new Font(14));
        Image img = new Image(this.getClass().getResourceAsStream("jumpingbean-logo.png"));
        ImageView view = new ImageView(img);
        Hyperlink link = new Hyperlink("www.JumpingBean.biz");
        link.setPadding(new Insets(10));
        link.setOnAction(new EventHandler() {

            @Override
            public void handle(Event event) {
                app.getHostServices().showDocument("http://www.jumpingbean.biz");
            }
        });

        Label about = new Label("GCExplorer was developed as a training aid for Jumping Bean's"
                + " Java performance tuning training course. It has been released under the GPLv3."
                + "Jumping Bean is a trademark of Indicento cc");
        about.setWrapText(true);
        about.setMaxWidth(400);
        vBox.getChildren()
                .add(jumpingbean);
        vBox.getChildren()
                .add(view);
        vBox.getChildren()
                .add(link);
        vBox.getChildren().add(about);
        vBox.setAlignment(Pos.CENTER);

        Stage stage = new Stage();

        stage.setScene(
                new Scene(vBox));
        stage.initModality(Modality.WINDOW_MODAL);

        stage.initOwner(
                this.tblDetails.getParent().getScene().getWindow());
        stage.initStyle(StageStyle.UTILITY);

        stage.show();
    }

}
