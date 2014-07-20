/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.jumpingbean.gcexplorer.ui;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;

/**
 *
 * @author Mark Clarke
 */
public class ManagePlatformsDialog implements Initializable {

    @FXML
    ListView lstPlatforms;
    @FXML
    Button btnAddPlatform;
    @FXML
    Button btnSetDefault;
    @FXML
    Button btnDelete;
    @FXML
    Label lblDefault;

    private final GCExplorer app;
    private final MainForm mainForm;

    ManagePlatformsDialog(GCExplorer app, MainForm mainForm) {
        this.app = app;
        this.mainForm = mainForm;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String platformProp = app.getProperties().getProperty("platforms", "java");
        String[] platforms = platformProp.split(";");
        lstPlatforms.setItems(FXCollections.observableArrayList(Arrays.asList(platforms)));
        lblDefault.setText(app.getPlatform());
        btnAddPlatform.setOnAction(new EventHandler() {

            @Override
            public void handle(Event event) {
                FileChooser chooser = new FileChooser();
                chooser.setTitle("Java Platform Selector");
                File file = chooser.showOpenDialog(btnAddPlatform.getScene().getWindow());
                file.getAbsolutePath();
                String platforms = app.properties.getProperty("platforms", "java");
                platforms += ";" + file.getAbsolutePath();
                app.saveProperty("platforms", platforms);
                lstPlatforms.getItems().add(file.getAbsolutePath());
            }

        });

        btnSetDefault.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                String path = (String) lstPlatforms.getSelectionModel().getSelectedItem();
                app.saveProperty("default", path);
                lblDefault.setText(path);
            }
        });

        btnDelete.setOnAction(new EventHandler() {

            @Override
            public void handle(Event event) {
                String path = (String) lstPlatforms.getSelectionModel().getSelectedItem();
                if (path.equalsIgnoreCase("java")) {
                    return;
                }
                if (path.equalsIgnoreCase(app.getPlatform())) {
                    lblDefault.setText("java");
                    app.saveProperty("default", "java");
                }
                //String platformsProp = app.getProperties().getProperty("platforms","java");
                List<String> platforms = lstPlatforms.getItems();//Arrays.asList(platformsProp.split(":"));
                platforms.remove(path);
                StringBuilder builder = new StringBuilder();
                for (String entry : platforms) {
                    builder.append(";").append(entry);
                }
                app.saveProperty("platforms", builder.toString());
                //lblDefault.setText(path);
            }

        });
    }
}
