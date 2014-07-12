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

import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author mark
 */
public class GarbageGenerationOptionsForm implements Initializable {

    @FXML
    private TextField txtNumInstances;
    @FXML
    private TextField txtLongLivedInstances;
    @FXML
    private TextField txtCreationPauseTime;
    @FXML
    private TextField txtInstanceSize;
    @FXML
    private Button btnGenLocalInstances;
    @FXML
    private Button btnReleaseLongLivedInstances;
    @FXML
    private Button btnGenLongLivedInstances;
    @FXML
    private CheckBox chkReverse;

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
        btnReleaseLongLivedInstances.setOnAction(this::releaseLongLivedInstances);
    }

    private void releaseLongLivedInstances(ActionEvent e) {
        int numInstances;
        try {
            numInstances = Integer.parseInt(txtLongLivedInstances.getText());
            boolean reverse = chkReverse.isSelected();
            app.getProcessController().releaseLongLivedInstances(procId, numInstances, reverse);
            ((Stage) txtNumInstances.getScene().getWindow()).close();
            StringBuilder str = new StringBuilder("Obj references released:");
            str.append("Objects:\t").append(numInstances).append("\n\r");
            parent.setGenStatus(str.toString());
        } catch (NumberFormatException ex) {
            createPopup("All inputs must be integers");
        }
    }

    private void createLocalInstances(ActionEvent e) {
        try {
            int numInstances = Integer.parseInt(txtNumInstances.getText());
            int instanceSize = Integer.parseInt(txtInstanceSize.getText());
            int creationPauseTime = Integer.parseInt(txtCreationPauseTime.getText());
            if (numInstances * instanceSize > 1000) {
                createPopup("You will use " + numInstances * instanceSize + " MB of memory");
            }
            int totalSeconds = numInstances * creationPauseTime / 1000;
            if (totalSeconds > 180) {
                createPopup("This will take " + totalSeconds + " seconds to complete");
            }
            app.getProcessController().genLocalInstances(procId, numInstances, instanceSize, creationPauseTime);
            ((Stage) txtNumInstances.getScene().getWindow()).close();
            StringBuilder str = new StringBuilder("Obj creation started with:");
            str.append("Objects:\t").append(numInstances).append("\n\r");
            str.append("Size(MB):\t").append(instanceSize).append("\n\r");
            str.append("Creation Pause (ms):\t").append(creationPauseTime).append("\n\r");
            parent.setGenStatus(str.toString());
        } catch (NumberFormatException ex) {
            createPopup("All inputs must be integers");
        }
    }

    private void createLongLivedInstances(ActionEvent e) {
        try {
            int numInstances = Integer.parseInt(txtNumInstances.getText());
            int instanceSize = Integer.parseInt(txtInstanceSize.getText());
            int creationPauseTime = Integer.parseInt(txtCreationPauseTime.getText());
            app.getProcessController().genLongLivedInstances(procId, numInstances, instanceSize, creationPauseTime);
            ((Stage) txtNumInstances.getScene().getWindow()).close();
            StringBuilder str = new StringBuilder("Obj creation started with:");
            str.append("Objects:\t").append(numInstances).append("\n\r");
            str.append("Size(MB):\t").append(instanceSize).append("\n\r");
            str.append("Creation Pause (ms):\t").append(creationPauseTime).append("\n\r");
            parent.setGenStatus(str.toString());
        } catch (NumberFormatException ex) {
            createPopup("All inputs must be integers.");
        }
    }

    private void createPopup(String msg) {
        Stage stage = new Stage();
        VBox box = new VBox();
        box.setPadding(new Insets(10));
        box.setAlignment(Pos.CENTER);
        Label label = new Label(msg);
        label.setMaxWidth(200);
        label.setWrapText(true);
        Button btnOk = new Button("Ok");
        btnOk.setOnAction(new EventHandler() {

            @Override
            public void handle(Event event) {
                stage.close();
            }

        });
        box.getChildren().add(label);
        box.getChildren().add(btnOk);
        stage.setScene(new Scene(box));
        stage.initOwner(txtCreationPauseTime.getScene().getWindow());
        stage.initStyle(StageStyle.UTILITY);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.show();
    }
}
