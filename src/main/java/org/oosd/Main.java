package org.oosd;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.concurrent.Task;
import javafx.scene.paint.Color;


public class Main extends Application {

    private StackPane root;
    private Scene scene;
    private Stage primaryStage;

    // Splash screen
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        root = new StackPane();
        scene = new Scene(root, 600, 600);

        primaryStage.setTitle("My JavaFX Application");
        primaryStage.setScene(scene);

        Stage splashStage = new Stage(StageStyle.UNDECORATED);

        ImageView splashImage = new ImageView(new Image(getClass().getResource("/splash-image.png").toExternalForm()));
        splashImage.setFitWidth(600);
        splashImage.setFitHeight(600);
        splashImage.setPreserveRatio(true);
        splashImage.setSmooth(true);

        Label loadingLabel = new Label("Loading...");

        StackPane splashLayout = new StackPane(splashImage, loadingLabel);
        Scene splashScene = new Scene(splashLayout, 600, 600);

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
                    showMainScreen();
                    primaryStage.show();
                });
            }
        };

        new Thread(loadTask).start();
    }

    //Main menu
    public void showMainScreen() {
        VBox mainScreen = new VBox(10);
        mainScreen.setPadding(new Insets(20));
        mainScreen.setAlignment(Pos.CENTER);

        Button startButton = new Button("Start Game");
        startButton.setOnAction(e -> showGameScreen());

        Button configButton = new Button("Configuration");
        configButton.setOnAction(e -> showConfigScreen());

        Button exitButton = new Button("Exit");
        exitButton.setOnAction(e -> System.exit(0));

        Button highScoresButton = new Button("High Scores");
        highScoresButton.setOnAction(e -> showHighScoresScreen());

        mainScreen.getChildren().addAll(startButton, configButton, exitButton, highScoresButton);
        root.getChildren().setAll(mainScreen);
    }

    //Game screen
    private void showGameScreen() {
        // Change to pane
        VBox gameScreen = new VBox(10);
        gameScreen.setPadding(new Insets(20));
        gameScreen.setAlignment(Pos.TOP_LEFT);


        Circle ball = new Circle(10, Color.RED);
        ball.setCenterX(600 / 2);
        ball.setCenterY(600 / 2);


        // Back button
        Button backButton = new Button("Back");
        backButton.setLayoutX(10);
        backButton.setLayoutY(10);
        backButton.setOnAction(e -> showMainScreen());

        gameScreen.getChildren().addAll(backButton,  ball);
        root.getChildren().setAll(gameScreen);
    }

    // Configuration screen
    private void showConfigScreen() {
        VBox configScreen = new VBox(10);
        configScreen.setPadding(new Insets(20));
        configScreen.setAlignment(Pos.TOP_LEFT);

        Label label = new Label("Configuration");

        // Back button
        Button backButton = new Button("Back");
        backButton.setLayoutX(10);
        backButton.setLayoutY(10);
        backButton.setOnAction(e -> showMainScreen());

        // Setting buttons
        CheckBox musicCB =  new CheckBox("Music (on/off)");
        CheckBox soundCB =  new CheckBox("Sound Effects (on/off)");
        CheckBox aiCB =  new CheckBox("AI Play (on/off)");
        CheckBox extendCB =  new CheckBox("Extend Mode (on/off)");

        Label fWidthLabel = new Label("Field Width");
        Slider fWidthSlider = new Slider(0, 100, 600);
        fWidthSlider.setShowTickLabels(true);
        fWidthSlider.setMajorTickUnit(100);
        Label fHeightLabel = new Label("Field Height");
        Slider fHeightSlider = new Slider(0, 100, 600);
        fHeightSlider.setShowTickLabels(true);
        fHeightSlider.setMajorTickUnit(100);

        Label levelLabel = new Label("Game Level");
        Slider levelSlider = new Slider(0, 100, 600);
        levelSlider.setShowTickLabels(true);
        levelSlider.setMajorTickUnit(10);

        configScreen.getChildren().addAll(label, backButton, musicCB,  soundCB,   aiCB,  extendCB, fWidthLabel, fWidthSlider, fHeightLabel, fHeightSlider,  levelLabel, levelSlider);
        root.getChildren().setAll(configScreen);
    }

    private void showHighScoresScreen() {
        VBox highScoresScreen = new VBox(10);
        highScoresScreen.setPadding(new Insets(20));
        highScoresScreen.setAlignment(Pos.TOP_LEFT);

        Label label = new Label("High Scores");

        Button backButton = new Button("Back");
        backButton.setLayoutX(10);
        backButton.setLayoutY(10);
        backButton.setOnAction(e -> showMainScreen());

        highScoresScreen.getChildren().addAll(label, backButton);
        root.getChildren().setAll(highScoresScreen);
    }

    public static void main(String[] args) {
        launch(args);
    }
}