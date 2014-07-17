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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import za.co.jumpingbean.gcexplorer.model.Utils;

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

    private final GCExplorer app;
    private final UUID procId;
    private final ProcessViewForm parent;

    GarbageGenerationOptionsForm(GCExplorer app, UUID procId, ProcessViewForm form) {
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
            Utils.createPopup("All inputs must be integers",txtCreationPauseTime.getScene().getWindow());
        }
    }

    private void createLocalInstances(ActionEvent e) {
        try {
            int numInstances = Integer.parseInt(txtNumInstances.getText());
            int instanceSize = Integer.parseInt(txtInstanceSize.getText());
            int creationPauseTime = Integer.parseInt(txtCreationPauseTime.getText());
            if (numInstances * instanceSize > 1000) {
                Utils.createPopup("You will use " + numInstances * instanceSize + " MB of memory",
                        txtCreationPauseTime.getScene().getWindow());
            }
            int totalSeconds = numInstances * creationPauseTime / 1000;
            if (totalSeconds > 180) {
                Utils.createPopup("This will take " + totalSeconds + " seconds to complete",
                        txtCreationPauseTime.getScene().getWindow());
            }
            app.getProcessController().genLocalInstances(procId, numInstances, instanceSize, creationPauseTime,parent);
            ((Stage) txtNumInstances.getScene().getWindow()).close();
        } catch (NumberFormatException ex) {
            Utils.createPopup("All inputs must be integers",
                    txtCreationPauseTime.getScene().getWindow());
        }
    }

    private void createLongLivedInstances(ActionEvent e) {
        try {
            int numInstances = Integer.parseInt(txtNumInstances.getText());
            int instanceSize = Integer.parseInt(txtInstanceSize.getText());
            int creationPauseTime = Integer.parseInt(txtCreationPauseTime.getText());
            app.getProcessController().genLongLivedInstances(procId, numInstances, instanceSize, creationPauseTime,parent);
            ((Stage) txtNumInstances.getScene().getWindow()).close();
        } catch (NumberFormatException ex) {
            Utils.createPopup("All inputs must be integers.",txtCreationPauseTime.getScene().getWindow());
        }
    }
}
