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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class GCExplorer extends Application {

    private ProcessController processController;
    private Units units = Units.MB;
    public final Properties properties = new Properties();
    private static OutputStream propOut;
    private static InputStream propIn;

    public static void main(String[] args) {
        launch(GCExplorer.class, args);
    }

    public Units getUnits() {
        return units;
    }

    public void setUnits(Units units) {
        this.units = units;
    }

    public Properties getProperties() {
        return this.properties;
    }
    
    public void saveProperty(String label,String value){
        try {
            
            properties.setProperty(label, value);
            properties.store(propOut,null);
            propOut.flush();
        } catch (IOException ex) {
            Logger.getLogger(GCExplorer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            String home  = System.getProperty("user.home");
            propOut = new FileOutputStream(home+"/.gcexplorer.properties",true);
            propIn = new FileInputStream(home+"/.gcexplorer.properties");
            properties.load(propIn);
        }catch(FileNotFoundException ex){
            System.out.println("not file");
        }
        processController = new ProcessController(this);
        Thread controllerThread = new Thread(processController, "GUI Stats Updater Controller");
        controllerThread.setDaemon(true);
        controllerThread.setName("GUI Process Controller");
        controllerThread.start();

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("mainForm.fxml")
        );

        loader.setController(new MainForm(processController, this));
        Parent pane = loader.load();
        Scene scene = new Scene(pane);
        primaryStage.setTitle("GC Explorer");
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image(this.getClass().getResourceAsStream("gcExplorer.png")));
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        processController.stopAllProcesses();
        propIn.close();
        propOut.close();
    }

    public ProcessController getProcessController() {
        return this.processController;
    }

    String getPlatform() {
        return properties.getProperty("default","java");
    }

}
