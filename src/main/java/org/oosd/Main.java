package org.oosd;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.concurrent.Task;

public class Main extends Application {


    @Override
    public void start(Stage primaryStage) {

        Stage splashStage = new Stage(StageStyle.UNDECORATED);

        ImageView splashImage = new ImageView(new Image(getClass().getResource("/splash-image.png").toExternalForm()));
        splashImage.setFitWidth(640);
        splashImage.setFitHeight(480);
        splashImage.setPreserveRatio(true);
        splashImage.setSmooth(true);

        Label loadingLabel = new Label("Loading...");

        StackPane splashLayout = new StackPane(splashImage, loadingLabel);
        Scene splashScene = new Scene(splashLayout, 640, 480);

        splashStage.setScene(splashScene);
        splashStage.show();

        Task<Void> loadTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep(3000);
                return null;
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    splashStage.close();
                    showMainStage(primaryStage);
                });
            }
        };

        new Thread((loadTask)).start();

    }

    private void showMainStage(Stage primaryStage) {

            Label label = new Label("Please input your name:");
            TextField input = new TextField();
            Button btn = new Button("Click Me");
            StackPane root = new StackPane();
            root.getChildren().addAll(label, input, btn);
            btn.setOnAction(e -> {
                int x = 10;
                int y = x + 5;
                System.out.println(x + y);
                label.setText("Hello, " + input.getText() + "!");
            });
            StackPane.setAlignment(label, Pos.TOP_CENTER);
            StackPane.setAlignment(input, Pos.CENTER);
            StackPane.setAlignment(btn, Pos.BOTTOM_CENTER);
            Scene scene = new Scene(root, 400, 200);
            primaryStage.setScene(scene);
            primaryStage.setTitle("JavaFX Demo");
            primaryStage.show();
        }


    public static void main(String[] args) {
        launch(args);
    }
}