package org.oosd.screens;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.oosd.Main;
import org.oosd.dialogs.ExitConfirmationDialog;
import org.oosd.manager.HighScoreManager;
import org.oosd.model.HighScore;

import java.time.format.DateTimeFormatter;
import java.util.List;

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
        titleLabel.getStyleClass().add("title-label");

        //Get real high score data
        HighScoreManager highScoreManager = HighScoreManager.getInstance();
        List<HighScore> highScores = highScoreManager.getTopHighScores();

        //Grid to display scores
        GridPane scoreGrid = new GridPane();
        scoreGrid.setAlignment(Pos.CENTER);
        scoreGrid.setHgap(15);
        scoreGrid.setVgap(8);

        //Column headers
        Label rankLabel = new Label("Rank");
        Label nameLabel = new Label("Name");
        Label scoreLabel = new Label("Score");
        Label linesLabel = new Label("Lines");
        Label sizeLabel = new Label("Size");
        Label dateLabel = new Label("Date");
        
        // Style headers
        rankLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
        scoreLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
        linesLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
        sizeLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
        dateLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

        scoreGrid.add(rankLabel, 0, 0);
        scoreGrid.add(nameLabel, 1, 0);
        scoreGrid.add(scoreLabel, 2, 0);
        scoreGrid.add(linesLabel, 3, 0);
        scoreGrid.add(sizeLabel, 4, 0);
        scoreGrid.add(dateLabel, 5, 0);

        //Add high scores
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        
        for (int i = 0; i < highScores.size(); i++) {
            HighScore highScore = highScores.get(i);
            
            Label rank = new Label(String.valueOf(i + 1));
            Label name = new Label(highScore.getPlayerName());
            Label score = new Label(String.valueOf(highScore.getScore()));
            Label lines = new Label(String.valueOf(highScore.getLinesCleared()));
            Label size = new Label(highScore.getFieldSize());
            Label date = new Label(highScore.getDate().format(dateFormatter));

            scoreGrid.add(rank, 0, i + 1);
            scoreGrid.add(name, 1, i + 1);
            scoreGrid.add(score, 2, i + 1);
            scoreGrid.add(lines, 3, i + 1);
            scoreGrid.add(size, 4, i + 1);
            scoreGrid.add(date, 5, i + 1);
        }


        // Button container
        HBox buttonContainer = new HBox(20);
        buttonContainer.setAlignment(Pos.CENTER);

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> mainApp.showMainMenuScreen());

        Button clearButton = new Button("Clear High Scores");
        clearButton.setStyle("-fx-text-fill: #cc0000; -fx-font-weight: bold;");
        clearButton.setOnAction(e -> {
            Stage stage = (Stage) root.getScene().getWindow();
            ExitConfirmationDialog confirmDialog = new ExitConfirmationDialog(
                    "Clear High Scores",
                    "Are you sure you want to clear all high scores?\nThis action cannot be undone."
            );
            
            boolean shouldClear = confirmDialog.show(stage);
            if (shouldClear) {
                HighScoreManager.getInstance().clearHighScores();
                // Refresh the display by showing the screen again
                show();
            }
        });

        buttonContainer.getChildren().addAll(backButton, clearButton);

        highScoresScreen.getChildren().addAll(titleLabel, scoreGrid, buttonContainer);
        root.getChildren().setAll(highScoresScreen);
    }
}
