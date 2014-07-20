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
package za.co.jumpingbean.gcexplorer.preloader;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.application.Preloader;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 *
 * @author Mark Clarke
 */
public class GCExplorerSplashScreen extends Preloader {

    private ProgressBar bar;
    StackPane root = new StackPane();
    ImageView[] slides;
    Stage stage=new Stage();
    
    private  void setUp() {
        this.slides = new ImageView[2];
        Image image1 = new Image(GCExplorerSplashScreen.class.
                getResource("jozi-jug-logo.png").toExternalForm());
        Image image2 = new Image(GCExplorerSplashScreen.class.
                getResource("jozi-lug-logo.png").toExternalForm());
        slides[0] = new ImageView(image1);
        slides[1] = new ImageView(image2);
    }

    public void startSlides() {
        SequentialTransition slideshow = new SequentialTransition();
        slideshow.setCycleCount(Timeline.INDEFINITE);
        for (ImageView slide : slides) {
            SequentialTransition sequentialTransition = new SequentialTransition();
            FadeTransition fadeIn = getFadeTransition(slide, 0.0, 1.0, 2000);
            PauseTransition stayOn = new PauseTransition(Duration.millis(15000));
            FadeTransition fadeOut = getFadeTransition(slide, 1.0, 0.0, 2000);
            sequentialTransition.getChildren().addAll(fadeIn, stayOn, fadeOut);
            slide.setOpacity(0);
            this.root.getChildren().add(slide);
            slideshow.getChildren().add(sequentialTransition);
        }
        root.getChildren().add(bar);
        slideshow.play();
    }

    public FadeTransition getFadeTransition(ImageView imageView, double fromValue, double toValue, int durationInMilliseconds) {
        FadeTransition ft = new FadeTransition(Duration.millis(durationInMilliseconds), imageView);
        ft.setFromValue(fromValue);
        ft.setToValue(toValue);
        return ft;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        bar = new ProgressBar();
        root.setPrefHeight(600);
        root.setPrefWidth(800);
        setUp();
        Scene scene = new Scene(root);
        stage = new Stage();
        stage.setScene(scene);
        stage.show();
        startSlides();
    }

    @Override
    public void handleProgressNotification(ProgressNotification pn) {
        bar.setProgress(pn.getProgress());
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification evt) {
        if (evt.getType() == StateChangeNotification.Type.BEFORE_START) {
            stage.hide();
        }
    }

}
