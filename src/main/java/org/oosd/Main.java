package org.oosd;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.oosd.screens.*;
import org.oosd.audio.MusicManager;
import org.oosd.audio.SoundEffectsManager;
import org.oosd.ui.ScorePanel;


public class Main extends Application {

    private MainMenuScreen mainMenuScreen;
    private HighScoresScreen highScoresScreen;
    private ConfigScreen configScreen;
    private GameScreen gameScreen;

    private StackPane root;
    private Scene scene;
    private Stage primaryStage;


    // Set up application window
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        root = new StackPane();
        scene = new Scene(root, 800, 900);

        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        primaryStage.setTitle("My JavaFX Application");
        primaryStage.setScene(scene);

        // Initialize audio managers
        MusicManager.getInstance().updateFromConfig();
        SoundEffectsManager.getInstance(); // Initialize sound effects

        // Show splash screen
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

    @Override
    public void stop() throws Exception {
        MusicManager.getInstance().dispose();
        super.stop();
    }

    public GameScreen getGameScreen() {
        return gameScreen;
    }

    public static void main(String[] args) {
        launch(args);
    }
}