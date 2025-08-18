package org.oosd;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;


public class Main extends Application {

    private MainMenuScreen mainMenuScreen;
    private HighScoresScreen highScoresScreen;
    private ConfigScreen configScreen;
    private GameScreen gameScreen;

    private StackPane root;
    private Scene scene;
    private Stage primaryStage;


    // Splash screen
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        root = new StackPane();
        scene = new Scene(root, 800, 900);

        primaryStage.setTitle("My JavaFX Application");
        primaryStage.setScene(scene);

        SplashScreen splashScreen = new SplashScreen(this, primaryStage, 600, 600);
        splashScreen.show();
    }



    //Show screens
    public void showMainMenuScreen() {
        if (mainMenuScreen == null) {
            mainMenuScreen = new MainMenuScreen(root, this);
        }
        mainMenuScreen.show();
    }

    public void showGameScreen() {
        if (gameScreen == null) {
            gameScreen = new GameScreen(root, this);
        }
        gameScreen.show();
    }

    public void showConfigScreen() {
        if (configScreen == null) {
            configScreen = new ConfigScreen(root, this);
        }
        configScreen.show();
    }

    public void showHighScoresScreen() {
        if (highScoresScreen == null) {
            highScoresScreen = new HighScoresScreen(root, this);
        }
        highScoresScreen.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}