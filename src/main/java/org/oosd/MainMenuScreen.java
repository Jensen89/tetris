package org.oosd;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class MainMenuScreen {
    private final StackPane root;
    private final Main mainApp;

    public MainMenuScreen(StackPane root, Main mainApp) {
        this.root = root;
        this.mainApp = mainApp;
    }

    public void show() {
        VBox mainScreen = new VBox(10);
        mainScreen.setPadding(new Insets(20));
        mainScreen.setAlignment(Pos.CENTER);

        Button startButton = new Button("Start Game");
        startButton.setOnAction(e -> mainApp.showGameScreen());

        Button configButton = new Button("Configuration");
        configButton.setOnAction(e -> mainApp.showConfigScreen());

        Button highScoresButton = new Button("High Scores");
        highScoresButton.setOnAction(e -> mainApp.showHighScoresScreen());

        Button exitButton = new Button("Exit");
        exitButton.setOnAction(e -> System.exit(0));

        mainScreen.getChildren().addAll(startButton, configButton, highScoresButton, exitButton);
        root.getChildren().setAll(mainScreen);
    }
}
