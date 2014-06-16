/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package za.co.jumpingbean.gcexplorer.ui;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;


/**
 *
 * @author mark
 */
public class Main extends Application {
    
    
    
    @FXML
    private Button btnLaunch;
    @FXML
    private ToggleGroup garbageCollectorGroup;

    public static void main(String[] args) {
        launch(args);
    }    
    
    @Override
    public void start(Stage primaryStage) throws Exception {
         Parent root = FXMLLoader.load(getClass().getResource("gcexplorer.fxml"));
         Scene scene = new Scene(root,800,600);
         primaryStage.setTitle("GE Explorer");
         primaryStage.setScene(scene);
         primaryStage.show();
    }
    
    @FXML
    protected void launchProcess(ActionEvent e){
        System.out.println("Button Pushed");
    }
    
}
