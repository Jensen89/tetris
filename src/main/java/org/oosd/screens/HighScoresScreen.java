package org.oosd.screens;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
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
        highScoresScreen.setAlignment(Pos.TOP_CENTER);

        Label titleLabel = new Label("High Scores");

        //Dummy high score data
        String[] names = {
                "Alex",
                "Sarah",
                "Mike",
                "Emma",
                "James",
                "Lisa",
                "David",
                "Sophie",
                "Chris",
                "Kate"
        };

        int[] scores = {
                50000,
                45000,
                42000,
                38000,
                35000,
                32000,
                28000,
                25000,
                22000,
                20000
        };

        //Grid to display scores
        GridPane scoreGrid = new GridPane();
        scoreGrid.setAlignment(Pos.CENTER);
        scoreGrid.setHgap(20);
        scoreGrid.setVgap(10);

        //Column headers
        Label rankLabel = new Label("Rank");
        Label nameLabel = new Label("Name");
        Label scoreLabel = new Label("Score");

        scoreGrid.add(rankLabel, 0, 0);
        scoreGrid.add(nameLabel, 1, 0);
        scoreGrid.add(scoreLabel, 2, 0);

        //Add scores
        for (int i = 0; i < scores.length; i++) {
            Label rank = new Label(String.valueOf(i + 1));
            Label name = new Label(names[i]);
            Label score = new Label(String.valueOf(scores[i]));

            scoreGrid.add(rank, 0, i + 1);
            scoreGrid.add(name, 1, i + 1);
            scoreGrid.add(score, 2, i + 1);
        }


        Button backButton = new Button("Back");
        backButton.setLayoutX(10);
        backButton.setLayoutY(10);
        backButton.setOnAction(e -> mainApp.showMainMenuScreen());

        highScoresScreen.getChildren().addAll(titleLabel, scoreGrid, backButton);
        root.getChildren().setAll(highScoresScreen);
    }
}
