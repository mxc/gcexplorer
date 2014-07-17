/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package za.co.jumpingbean.gcexplorer.model;

import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import za.co.jumpingbean.gc.service.GCExplorerServiceException;
import za.co.jumpingbean.gcexplorer.ui.GCExplorer;
import za.co.jumpingbean.gcexplorer.ui.LaunchProcessDialog;
import za.co.jumpingbean.gcexplorer.ui.MainForm;
import za.co.jumpingbean.gcexplorer.ui.ProcessViewForm;

/**
 *
 * @author Mark Clarke
 */
public class Utils {
        public static void createPopup(String msg,Window owner) {
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
        stage.initOwner(owner);
        stage.initStyle(StageStyle.UTILITY);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.show();
    }

}
