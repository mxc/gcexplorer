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
import java.util.ResourceBundle;
import java.util.UUID;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import za.co.jumpingbean.gcexplorer.model.Utils;

/**
 *
 * @author Mark Clarke
 */
public class AttachExistingProcessDialog implements Initializable {

    @FXML
    private Button btnConnectLocalProcess;
    @FXML
    private Button btnConnectRemoteProcess;
    @FXML
    private TextField txtRemoteURL;
    @FXML
    private ListView lstLocalProcesses;
    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtPassword;

    private final GCExplorer app;
    private final MainForm parent;

    public AttachExistingProcessDialog(GCExplorer app, MainForm parent) {
        this.app = app;
        this.parent = parent;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lstLocalProcesses.setItems(FXCollections.observableArrayList(app.getProcessController().getLocalProcessesList()));
        btnConnectLocalProcess.setOnAction(new EventHandler() {

            @Override
            public void handle(Event event) {
                String str = (String) lstLocalProcesses.getSelectionModel().getSelectedItem();
                if (str != null) {
                    try {
                        String params[] = str.split(" ");
                        int pid = Integer.parseInt(params[0]);
                        String strParams ="";
                        if (params.length >=2){
                            strParams=params[1];
                        }
                        UUID procId = app.getProcessController().connectToProcess(pid,strParams);
                        try {
                            parent.addPane(procId, true);
                            ((Stage) lstLocalProcesses.getScene().getWindow()).close();
                        } catch (IOException ex) {
                            Utils.createPopup("There was an error adding tab to display",
                                    lstLocalProcesses.getScene().getWindow());
                        }
                    } catch (NumberFormatException | IOException ex) {
                        Utils.createPopup("Error connection to process",
                                lstLocalProcesses.getScene().getWindow());
                    }
                }
            }

        });

        btnConnectRemoteProcess.setOnAction(new EventHandler() {

            @Override
            public void handle(Event event) {
                String str = txtRemoteURL.getText();
                String user = txtUsername.getText();
                String password = txtPassword.getText();
                if (str != null) {
                    try {
                        UUID procId = app.getProcessController().connectToProcess(str,user,password);
                        try {
                            parent.addPane(procId, true);
                            ((Stage) lstLocalProcesses.getScene().getWindow()).close();
                        } catch (IOException ex) {
                            Utils.createPopup("There was an error adding tab to display",
                                    lstLocalProcesses.getScene().getWindow());
                        }
                    } catch (NumberFormatException | IOException ex) {
                        Utils.createPopup("Error connection to process",
                                lstLocalProcesses.getScene().getWindow());
                    }
                }
            }
        });
    }
}
