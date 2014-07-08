/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.jumpingbean.gcexplorer.ui;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Popup;
import javafx.stage.Stage;

/**
 *
 * @author mark
 */
public class GarbageGenerationOptionsForm implements Initializable {

    @FXML
    private TextField txtNumInstances;
    @FXML
    private TextField txtCreationPauseTime;
    @FXML
    private TextField txtReturnDelay;
    @FXML
    private TextField txtInstanceSize;
    @FXML
    private Button btnGenLocalInstances;
    @FXML
    private Button btnGenLongLivedInstances;
    private final Main app;
    private final UUID procId;
    private final ProcessViewForm parent;

    GarbageGenerationOptionsForm(Main app, UUID procId, ProcessViewForm form) {
        this.app = app;
        this.procId = procId;
        this.parent = form;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnGenLongLivedInstances.setOnAction(this::createLongLivedInstances);
        btnGenLocalInstances.setOnAction(this::createLocalInstances);
    }

    private void createLocalInstances(ActionEvent e) {
        try {
            int numInstances = Integer.parseInt(txtNumInstances.getText());
            int instanceSize = Integer.parseInt(txtInstanceSize.getText());
            int returnDelay = Integer.parseInt(txtReturnDelay.getText());
            int creationPauseTime = Integer.parseInt(txtCreationPauseTime.getText());
            if (numInstances * instanceSize > 1000) {
                Popup popup = new Popup();
                popup.getContent().add(new Label("You will use " + numInstances * instanceSize + " MB of memory"));
                popup.show(txtNumInstances.getScene().getWindow());
            }
            int totalSeconds = numInstances * creationPauseTime / 1000;
            if (totalSeconds > 180) {
                Popup popup = new Popup();
                popup.getContent().add(new Label("This will take " + totalSeconds + " seconds to complete"));
                popup.show(txtNumInstances.getScene().getWindow());
            }
            app.getProcessController().genLocalInstances(procId, numInstances, instanceSize, creationPauseTime, returnDelay);
            ((Stage) txtNumInstances.getScene().getWindow()).close();
            StringBuilder str = new StringBuilder("Obj creation started with:");
            str.append("Objects:\t").append(numInstances).append("\n\r");
            str.append("Size(MB):\t").append(instanceSize).append("\n\r");
            str.append("Creaiton Pause (ms):\t").append(creationPauseTime).append("\n\r");
            str.append("Return Delay(ms):\t").append(returnDelay).append("\n\r");
            parent.setGenStatus(str.toString());
        } catch (NumberFormatException ex) {
            Popup popup = new Popup();
            popup.getContent().add(new Label("All input must be integers"));
            popup.show(txtNumInstances.getScene().getWindow());
        }
    }

    private void createLongLivedInstances(ActionEvent e) {
        try {
            int numInstances = Integer.parseInt(txtNumInstances.getText());
            int instanceSize = Integer.parseInt(txtInstanceSize.getText());
            int returnDelay = Integer.parseInt(txtReturnDelay.getText());
            int creationPauseTime = Integer.parseInt(txtCreationPauseTime.getText());
            app.getProcessController().genLongLivedInstances(procId, numInstances, instanceSize, creationPauseTime, returnDelay);
            ((Stage) txtNumInstances.getScene().getWindow()).close();
            StringBuilder str = new StringBuilder("Obj creation started with:");
            str.append("Objects:\t").append(numInstances).append("\n\r");
            str.append("Size(MB):\t").append(instanceSize).append("\n\r");
            str.append("Creaiton Pause (ms):\t").append(creationPauseTime).append("\n\r");
            str.append("Return Delay(ms):\t").append(returnDelay).append("\n\r");
            parent.setGenStatus(str.toString());
        } catch (NumberFormatException ex) {
            Popup popup = new Popup();
            popup.getContent().add(new Label("All input must be intergers"));
            popup.show(txtNumInstances.getScene().getWindow());
        }
    }

}
