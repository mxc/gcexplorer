/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.jumpingbean.gcexplorer.ui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;

/**
 *
 * @author mark
 */
public class LaunchProcessDialog implements Initializable {

    @FXML
    private Button btnLaunch;
    @FXML
    private TextArea txtStatus;
    @FXML
    private ToggleGroup garbageCollectorGroup;
    @FXML
    private RadioButton rdbSerial;
    @FXML
    private RadioButton rdbG1;
    @FXML
    private RadioButton rdbConcMarkSweep;
    @FXML
    private RadioButton rdbParallel;
    @FXML
    private RadioButton rdbParallelOld;
    private final Main app;
    private final MainForm parent;

    public LaunchProcessDialog(Main app, MainForm parent) {
        this.app = app;
        this.parent = parent;
    }

    protected void launchProcess(ActionEvent e) {
        //garbageCollectorGroup.getSelectedToggle();
        UUID procId;
        String selectedGC = (String) garbageCollectorGroup.getSelectedToggle().getUserData();
        try {
            procId = app.getProcessController().launchProcess(selectedGC);
        } catch (IOException |
                IllegalStateException |
                NullPointerException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            StringBuilder str = new StringBuilder("Error launching process: ");
            str.append(ex.getMessage());
            txtStatus.setText(str.toString());
            return;
        }

        StringBuilder strBuilder = new StringBuilder(txtStatus.getText());
        strBuilder.append("\n\r");
        strBuilder.append("Process id:");
        strBuilder.append(procId.toString());
        strBuilder.append(" Started with GC:");
        strBuilder.append(selectedGC);
        txtStatus.setText(strBuilder.toString());

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("processCharts.fxml")
        );
        loader.setController(new ProcessViewForm(app, procId));
        Parent pane;
        try {
            pane = loader.load();
            this.parent.addTab(pane, procId);
        } catch (IOException ex) {
            Logger.getLogger(LaunchProcessDialog.class.getName()).log(Level.SEVERE, null, ex);
            String txt = txtStatus.getText();
            txtStatus.setText(txt + "\n\rThere was an error adding tab to display");
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.rdbConcMarkSweep.setUserData("-XX:+UseConcMarkSweepGC");
        this.rdbSerial.setUserData("-XX:+UseSerialGC");
        this.rdbG1.setUserData("-XX:+UseG1GC");
        this.rdbParallel.setUserData("-XX:+UseParallelGC");
        this.rdbParallelOld.setUserData("-XX:+UseParallelOldGC");
        this.btnLaunch.setOnAction(this::launchProcess);
    }

}
