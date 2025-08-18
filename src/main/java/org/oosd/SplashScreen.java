package org.oosd;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class SplashScreen {
    private final Main mainApp;
    private final Stage primaryStage;
    private final int width;
    private final int height;

    public SplashScreen(Main mainApp, Stage primaryStage, int width, int height) {
        this.mainApp = mainApp;
        this.primaryStage = primaryStage;
        this.width = width;
        this.height = height;
    }

    public void show() {
        Stage splashStage = new Stage(StageStyle.UNDECORATED);

        ImageView splashImage = new ImageView(new Image(getClass().getResource("/splash-image.png").toExternalForm()));
        splashImage.setFitWidth(width);
        splashImage.setFitHeight(height);
        splashImage.setPreserveRatio(true);
        splashImage.setSmooth(true);

        Label loadingLabel = new Label("Loading...");

        StackPane splashLayout = new StackPane(splashImage, loadingLabel);
        Scene splashScene = new Scene(splashLayout, width, height);

        splashStage.setScene(splashScene);
        splashStage.show();

        Task<Void> loadTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep(3000); // 3 second loading time
                return null;
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    splashStage.close();
                    mainApp.showMainMenuScreen();
                    primaryStage.show();
                });
            }
        };

        new Thread(loadTask).start();
    }
}