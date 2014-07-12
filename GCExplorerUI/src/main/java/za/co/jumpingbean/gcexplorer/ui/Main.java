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

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
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
        primaryStage.getIcons().add(new Image(this.getClass().getResourceAsStream("gcExplorer.png")));
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
