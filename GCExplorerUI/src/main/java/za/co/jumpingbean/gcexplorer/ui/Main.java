/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.jumpingbean.gcexplorer.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author mark
 */
public class Main extends Application {

    private ProcessController processController;
    private Units units = Units.MB;
    
    public static void main(String[] args) {
        launch(Main.class, args);
    }

    public Units getUnits() {
        return units;
    }

    public void setUnits(Units units) {
        this.units = units;
    }
  
    @Override
    public void start(Stage primaryStage) throws Exception {
        processController = new ProcessController(this);
        Thread controllerThread = new Thread(processController, "GUI Stats Updater Controller");
        controllerThread.setDaemon(true);
        controllerThread.setName("GUI Process Controller");
        controllerThread.start();

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("mainForm.fxml")
        );

        loader.setController(new MainForm(processController,this));
        Parent pane = loader.load();
        Scene scene = new Scene(pane, 800, 600);
        primaryStage.setTitle("GE Explorer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        processController.stopAllProcesses();
    }

    public ProcessController getProcessController() {
        return this.processController;
    }

}
