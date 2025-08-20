package org.oosd.screens;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.oosd.Main;

public class HighScoresScreen {
    private final StackPane root;
    private final Main mainApp;

    public HighScoresScreen(StackPane root, Main mainApp) {
        this.root = root;
        this.mainApp = mainApp;
    }

    public void show() {
        VBox highScoresScreen = new VBox(10);
        highScoresScreen.setPadding(new Insets(20));
        highScoresScreen.setAlignment(Pos.TOP_LEFT);

        Label label = new Label("High Scores");

        Button backButton = new Button("Back");
        backButton.setLayoutX(10);
        backButton.setLayoutY(10);
        backButton.setOnAction(e -> mainApp.showMainMenuScreen());

        highScoresScreen.getChildren().addAll(label, backButton);
        root.getChildren().setAll(highScoresScreen);
    }
}
